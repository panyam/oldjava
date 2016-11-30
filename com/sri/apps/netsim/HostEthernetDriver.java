
package com.sri.apps.netsim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A basic implementation of an Ethernet Driver.
 *
 * This is what is used in hosts.
 * Basically all this does is send and recieve packets without
 * any forwarding.  So any packets destined to this interface but not
 * addresses to this MAC address (or without a Broadcast address) is
 * rejected.
 */
public class HostEthernetDriver implements EthernetDriver
{
    protected boolean isRunning = false;
        /**
         * AN IP Driver.
         * Other children are possible but for this purpose this is enough.
         */
    protected ProtocolDriver ipDriver = null;

        /**
         * The ARP Driver.
         */
    protected ARPDriver arpDriver;

        /**
         * The layer in which this driver is being used.
         */
    protected byte layer = 0;

        /**
         * Maximum transmit unit.
         */
    protected short mtu = EthernetUtils.ETHERNET_LENGTH;

        /**
         * Constructor.
         */
    public HostEthernetDriver(int layer)
    {
        this(EthernetUtils.ETHERNET_LENGTH, layer);
    }

        /**
         * Constructor.
         */
    public HostEthernetDriver(int mtu, int layer)
    {
        this.mtu = (short)mtu;
        setLayer(layer);
    }

        /**
         * Sets the layer in which this driver is being used.
         */
    public void setLayer(int layer)
    {
        this.layer = (byte)layer;
    }

        /**
         * Returns the layer in which this driver is being used.
         */
    public int getLayer()
    {
        return layer;
    }

        /**
         * Sets the driver offset and header length of this
         * packet at the given layer.
         */
    public void stripHeaders(Packet packet, int layer)
    {
        if (layer > 0)
        {
            packet.driverOffset[layer] = (short)
                                        (packet.driverOffset[layer - 1] + 
                                         packet.headerLength[layer - 1]);
        } else
        {
            packet.driverOffset[layer] = 0;
        }
        packet.headerLength[layer] = 14;
    }

        /**
         * Extracts the protocol ID of a packet.
         */
    public short readProtocolID(Packet packet)
    {
        return EthernetUtils.readProtocolID(packet, packet.driverOffset[layer]);
    }

        /**
         * Extracts the protocol ID of a packet.
         */
    public void writeProtocolID(Packet packet, short protoID)
    {
        EthernetUtils.writeProtocolID(packet,
                                      protoID, packet.driverOffset[layer]);
    }

        /**
         * Sets the source mac address of this packet.  
         */
    public void writeSourceMac(Packet packet, long srcMac)
    {
        EthernetUtils.writeSrcMac(packet, srcMac, packet.driverOffset[layer]);
    }

        /**
         * Sets the destination mac address of this packet.  
         */
    public void writeDestMac(Packet packet, long dstmac)
    {
        EthernetUtils.writeDestMac(packet, dstmac, packet.driverOffset[layer]);
    }

        /**
         * Gets the source mac address of this packet.  
         * Since this is called by the child protocol, offset and length
         * in a packet denotes the offset and length of the "child"
         * protocol.
         */
    public long readSourceMac(Packet packet)
    {
        return EthernetUtils.readSrcMac(packet,
                                        packet.driverOffset[layer]);
    }

        /**
         * Gets the destination mac address of this packet.  
         */
    public long readDestMac(Packet packet)
    {
        return EthernetUtils.readDestMac(packet,
                                         packet.driverOffset[layer]);
    }

        /**
         * Process a packet that has just entered the Device.
         *
         * Returns a child driver if one exists.
         */
    public ProtocolDriver processInPacket(Packet packet,
                                          Device parent, 
                                          Network network)
    {
            // if the inbound NIC is not known then it is an invalid
            // packet.. so drop it...
        if (packet.inNic == null) return null;

            // check the packet type...
            // if it is a valid Ethernet Packet then accept..
        short protoID = readProtocolID(packet);

            // the source mac address MUST be set in this case as the
            // packet is not outbound.
        long srcMAC = readSourceMac(packet);

            // check if we can forward packets...
            // if the destination is not this Device then 
            // forward only if can forward
        long destMAC = readDestMac(packet);

            // TODO:: check if packet is inbound or to be forwarded:::
            // how to determine this host depends on whether this device
            // has a unique MAC address or whether we are just checking for
            // destMac == packet.inNic.macAddress
            // "this_host" should be one of the destination mac addresses
            // or the broad cast address.  Not dealing with multicast
            // addresses yet..

        NetworkInterface thisInt = findInterface(destMAC, parent);
        if (thisInt != null)
        {
            if (protoID == Protocols.IP_PROTOCOL) return ipDriver;
        }
        return null;
    }

        /**
         * Processes an outgoing packet before returning it to the parent.
         * If output is null then it means that the packet has been
         * consumed and it will not be availale to the device again.
         * When a packet is going out, all we do is set the type/length and
         * the checksum.
         *
         * And also in this case the outNic, will be set by the device..
         *
         * In order to find out which NIC to send it to, the Source MAC
         * Address must be checked...
         *
         * Also the ARP process will come in handy here.  The idea is that,
         * Network Destination Addresses should be matched to the
         * Destination Mac addresses.
         *
         * The issue is how should the ARP be processed?  Should the ARP
         * process be a separate driver or part of the ethernet module?
         * Sounds good if it is part of the ethernet module itself??  Or,
         * it can be a "colleague" process but "requests" for addresses and
         * has a virtual "wait".  So this ethernet driver can have a
         * "waitingToSend" table where packets will be queued while the
         * addresses are being located.
         *
         * But, it is better to have the ARP process a part of this module
         * itself because the ARP process would have to send arp packets
         * out anyway.
         *
         * When is the ARP process to be invoked?
         *
         * We can make it so that if the destination mac address is 0,
         * then it means that the mac address is not known.
         * So how is the ARP invoked?  We can do it two ways.  One is we
         * canhave a call to the ARP module requesting the MAC address or
         * we can simply give the packet to the ARP module in the hope that
         * it will resolve the MAC address and transmit it as well...
         * The ARP module will take care of all the buffering and so on.
         */
    public int processOutPacket(Packet packet, Device parentDevice) 
    {
        if (packet.outNic != null) return 0;

            // if  length is greater than MTU then reject it...
        if (packet.length > mtu) return -1;

        //if (packet.outNic != Packet.UNKNOWN_NIC) return -1;

                // at this point, the destination address MUST be set...
                // This MUST be done by drivers in lower layers...
                // otherwise behaviour is undefined...

            // if the destination is not this Device then forward only if
            // can output it..
        long srcMAC = readSourceMac(packet);

        long destMAC = readDestMac(packet);

        if (destMAC == 0)
        {
            if (arpDriver != null)
            {
                arpDriver.transmitPacket(packet, parentDevice);
            }
        } else
        {
                // since no forwarding is done and only outbound so what are
                // the steps::
                //
                // At this point, the destination mac address will be known
                // as well as 
                // Check the source mac address and get the interface
                // associated with it.
                //
                // Write the packet to that interface...
            NetworkInterface toInt = findInterface(srcMAC, parentDevice);

                // if no interface matches, then broadcast the packet on all
                // interfaces...  This means that the source mac hasnt been set
                // yet.
            if (toInt == null)
            {
            } else
            {
            }
        }

        return 0;
    }

        /**
         * Finds the interface that matches this interface.
         *
         * TODO: How can we make this faster?  One way is to have different
         * lists for different kinds of interfaces so this way we can get
         * rid of the "instanceof" checks.
         */
    protected NetworkInterface findInterface(long mac, Device parentDevice)
    {
        for (int i = 0;i < parentDevice.nInterfaces;i++)
        {
            if (parentDevice.interfaces[i] instanceof EthernetInterface)
            {
                EthernetInterface eint =
                    (EthernetInterface)parentDevice.interfaces[i];
                if (eint.macAddress == mac) return eint;
            }
        }
        return null;
    }

        /**
         * Reads an XML Node.
         */
    public void readXMLNode(Element elem) throws Exception
    {

    }

        /**
         * Creates an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        return null;
    }

        /**
         * Creates a packet of a given length + headers and returns it.
         *
         * Why use it:
         * Basically makes encapsulation a transparent process.
         * MUST be called only by a child driver object.
         */
    public Packet getPacket(int length)
    {
        Packet packet = new Packet(18 + length);
        packet.driverOffset[layer] = 0;
        packet.headerLength[layer] = 14;
        return packet;
    }

        /**
         * Add a protocol as a parent that can be used to
         * encapsulate these packets.
         */
    public void addParent(ProtocolDriver parent)
    {
        // doesnt do anything because with hosts, we do not want any
        // parents.  Usually parents for ethernetdrivers are not
        // necessary.
    }

        /**
         * Adds a new child.
         */
    public void registerChild(ARPable driver, int protoID)
    {
        if (protoID == Protocols.IP_PROTOCOL) ipDriver = driver;
        else if (protoID == Protocols.ARP_PROTOCOL) arpDriver = driver;
            // other ones can be added on later.
    }

        /**
         * Starts the driver.
         */
    public void start(Network parent, double currTime)
    {
        isRunning = true;
    }

        /**
         * Stops the driver.
         */
    public void stop(Network parent, double currTime)
    {
        isRunning = false;
    }

        /**
         * Tells if the driver isrunning.
         */
    public boolean isRunning()
    {
        return isRunning;
    }

        /**
         * Nothing is done here.  This driver, anyway, 
         * doenst handle anyevents.
         */
    public double handleEvent(SimEvent event,
                              double currTime,
                              Network parent)
    {
        return 0;
    }
}
