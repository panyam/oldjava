
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
 * The Ethernet Driver object.
 * Works at the lowest level for accepting ethernet packets.
 */
public class IPDriver extends PacketDriver 
{
        /**
         * The offset in a packet where the IP info of a
         * packet begins.
         */
    public final static int IP_OFFSET = 14;

        /**
         *  The ip header length.
         */
    public final static int IP_HEADER_LENGTH = 20;

        /**
         * The byte in the packet which determines the type
         * of IP protocol.
         */
    public final static int PROTOCOL_OFFSET = IP_OFFSET + 10;

        /**
         * Maximum transmit unit.
         */
    protected short mtu = EthernetUtils.ETHERNET_LENGTH;

        /**
         * This is the table that will be modified by
         * most processes and consulted when routing
         * decisions need to be made.
         * The structure is still undecided.
         */
    protected RoutingTable routingTable = null;

        /**
         * Tells if this driver can forward packets.
         * If true then this driver also works as a Switch.
         */
    public boolean canRoute = true;

        /**
         * The default interface.
         */
    protected int defaultInterface = 0;

    protected final static byte INVALID_FRAME   = -2;
    protected final static byte NORMAL_FRAME    = -1;

        /**
         * A counter that keeps track of the IP datagram Identification
         * tag.
         */
    protected short idCounter = Short.MIN_VALUE;

    IPTable ipTable = null;

        /**
         * Constructor.
         */
    public IPDriver(Device dev, boolean canRoute, int mtu)
    {
            // find if there is an IPTable object in the parent..
            // if one isnt found then add it in...
        this.ipTable = (IPTable)dev.getSharedObject(IPTable.class, true);
        this.routingTable =
            (RoutingTable)dev.getSharedObject(RoutingTable.class, true);

        this.canRoute = canRoute;
        this.mtu = (short)mtu;
    }

        /**
         * Add a new route.
         */
    public void addRoute(Route route)
    {
        routingTable.addRoute(route);
    }

        /**
         * Generate default routes for all interfaces.
         */
    public void generateDefaultRoutes(Device parent)
    {
            // is this step necessary??
        //routingTable.removeAllRoutes();
        Route newRoute = null;
        NetworkInterface iface = null;
        //System.out.println("nInterfaces = " + nInterfaces);
        for (int i = 0;i < parent.nInterfaces;i++)
        {
            newRoute = new Route();
            newRoute.destAddress = ipTable.ipAddress[i];
            newRoute.maskLength = ipTable.subnetMask[i];
            newRoute.destInterface = parent.interfaces[i];
            addRoute(newRoute);
        }
    }

        /**
         * Returns true if packet is an Ethernet Packet.
         */
    public boolean isPacketValid(Packet packet)
    {
        if (parent == null) return false;
        byte data[] = packet.data;
        int offset = packet.offset;

                // we only deal with ETHERNET 
                // based encapsulation for now...
        if (parent instanceof EthernetProtocol)
        {
                // check protocol type... it has to be 0x0800
                // for an IP packet...
            if (data[offset - 2] != 0x08 ||
                    data[offset - 1] != 0) return false;

                // do other checks to see if we have proper
                // IP packets...

                // ignore non ipv4 packets for now...
            if (data[offset] >> 4 != 0x04) return false;

                // get the header length..
            int headerLen = data[offset] & 0x0f;

                // TODO:: Validate the header checksum

            return true;
        }
        return false;
    }

        /**
         * Reads the src IP address of an ethernet packet.
         */
    public final static int readDestIP(Packet packet)
    {
        byte data[] = packet.data;
        int offset = packet.offset;
        return ((data[offset + 17] & 0xff) << 24) | 
               ((data[offset + 18] & 0xff) << 16) | 
               ((data[offset + 19] & 0xff) << 8) | 
               (data[offset + 20] & 0xff);
    }

        /**
         * Writes the dest IPAddress of the packet.
         */
    public final static void writeDestIP(Packet packet, long IP)
    {
        byte data[] = packet.data;
        int offset = packet.offset;
        data[offset + 17] = (byte)((IP >> 24) & 0xff);
        data[offset + 18] = (byte)((IP >> 16) & 0xff);
        data[offset + 19] = (byte)((IP >> 8) & 0xff);
        data[offset + 20] = (byte)(IP & 0xff);
    }

        /**
         * Reads the dest IP address of an ethernet packet.
         */
    public final static int readSrcIP(Packet packet)
    {
        byte data[] = packet.data;
        int offset = packet.offset;
        return ((data[offset + 13] & 0xff) << 24) | 
               ((data[offset + 14] & 0xff) << 16) | 
               ((data[offset + 15] & 0xff) << 8) | 
               (data[offset + 16] & 0xff);
    }

        /**
         * Writes the Source IPAddress of the packet.
         * packet.offset at this point points to where the IP header
         * starts. Not where the child starts!!
         */
    public final static void writeSrcIP(Packet packet, long IP)
    {
        byte data[] = packet.data;
        int offset = packet.offset;
        data[offset + 16] = (byte)(IP & 0xff);
        data[offset + 15] = (byte)((IP >> 8) & 0xff);
        data[offset + 14] = (byte)((IP >> 16) & 0xff);
        data[offset + 13] = (byte)((IP >> 24) & 0xff);
    }

        /**
         * Process the next outgoing packet.
         *
         * What needs to be done:
         *
         *
         * Before a packet can be considered, its header fields have to be
         * computed.  Because the offset points to where the lower layer
         * protocols starts.  Options are not taken care of at the moment.
         *
         * If packet needs fragmentation:
         *      if dont fragment flag set, then drop it
         *      else fragment it
         * else 
         *      set source ip and mac address (or what ever physical
         *      address is requried) and forward it
         */
    public int processOutPacket(Packet packet, Device parent) throws Exception
    {

            // see what type of packet we have so that
            // the decision we take depends on it...
        byte type = getPacketType(packet);
        byte data[] = packet.data;

            // at this point the packet WILL be a normal packet as we are
            // encapsulateing LOWER layer packets.

        int destAddress = readDestIP(packet);

            // Step 1: write the headers
        packet.offset -= IP_HEADER_LENGTH;
        packet.length += IP_HEADER_LENGTH;
        NetworkInterface targetInterface = writeIPHeaders(packet, parent);

            // Step 2: Decide on Fragmentation

           // we do all this only if the NIC is unknown.
           // For example, we may already have determined
           // in an earlier stage of the packet module chain
           // that this packet should be sent out of a certain
           // interface, in which case no routing should be
            // done on the packet.
        if (packet.length <= mtu &&
                packet.outNic != Packet.UNKNOWN_NIC) return -1;



        int targetIP = ipTable.ipAddress[targetInterface.interfaceID];
        int srcIP = targetIP;

        String message = 
            "    Src: " + Utils.ipToString(srcIP) + " (" + srcIP + ")\n" +
            "    Dst: " + Utils.ipToString(destAddress) +
            " (" + destAddress+ ")\n" +
            "    Interface " + targetInterface.interfaceID +
            ": " + Utils.ipToString(targetIP) + " (" + targetIP + ")\n";

        //if (whichRoute != null) 
        //{
        //    message += "    Route: " + whichRoute.toString() + "\n";
        //}

        packet.outNic = targetInterface.interfaceID;

        if (packet.length > mtu)
        {
                // now do the fragmentation as all the headers have been
                // set...
            Packet fragments[] = getFragments(packet, parent, mtu);

                // could happen if dont fragment is set to a 1
            if (fragments == null) 
            {
                    // then drop the packet
                parent.writeToConsole(DeviceEvent.PACKET_DROPPED, message);
                return -1;
            }

            message += "# Fragments: " + fragments.length + "\n";

            parent.writeToConsole(DeviceEvent.PACKET_FRAGMENTED, message);

                    // now send the fragments to the
                    // parent to be transmitted
            for (int i = 0;i < fragments.length;i++)
            {
                parent.transmitPacket(fragments[i]);
                fragments[i].outNic =  targetInterface.interfaceID;
                    // and also the source MAC!!!
                EthernetUtils.writeSrcMac(fragments[i],
                                          fragments[i].offset,
                                          targetInterface.macAddress);
            }

            return -1;
        }

        parent.writeToConsole(DeviceEvent.ROUTE_CHOSEN, message);

            // if we here it means packet wasnt defragmented and is also of
            // the right size, so just let it through.  Plus all the source
            // IP and MAC would/should have been set by now. 
        return 0;
    }

        /**
         * Write the packet's headers and return the interface
         * through which this packet should be sent.
         */
    protected NetworkInterface writeIPHeaders(Packet packet, Device parent)
    {
        int offset = packet.offset;
        byte data[] = packet.data;
        int destAddress = readDestIP(packet);


            // find out the route given the destination IP...
            // for outgoing packets the source ip is irrelevant..
            // this ip will be set based on the NIC that is selected

            // we will give the destination address the
            // biggest possible masklength. So that we can
            // get the most. 
            // Note: This will be same for all packets!! 
            // so only do it once
        NetworkInterface targetInterface = null;

        if (defaultInterface >= 0)
        {
            targetInterface = parent.interfaces[defaultInterface];
        }

        Route whichRoute = routingTable.findRoute(destAddress, 32);
        if (whichRoute != null)
        {
            targetInterface = whichRoute.destInterface;
        }

        int srcIP = ipTable.ipAddress[targetInterface.interfaceID];

        data[offset] = (byte)((IP_HEADER_LENGTH << 4) | 0x04);
                // write the source IP of the packet!!!
                // leave the destination address alone as this 
                // is written by the lower layer...
        packet.putInt(packet.offset + 16, srcIP);

                // TODO:: Think about TOS values

                // write the length at offset 2
        packet.putShort(packet.offset + 2, packet.length);

                // write the identification field
        synchronized (this)
        {
            packet.putShort(packet.offset + 4, idCounter ++);
                
                // reset the id counter if necessary
            if (idCounter == Short.MAX_VALUE) idCounter = Short.MIN_VALUE;
        }

        return targetInterface;
    }

        /**
         * Returns a list of packet that are fragments of an original
         * packet.
         */
    protected Packet []getFragments(Packet packet, Device parent, int fragSize)
    {
        int nPackets = 1 + packet.length / fragSize;
        return null;
    }

        /**
         * Process the next incoming packet.
         */
    public int processInPacket(Packet packet, Device parent) throws Exception
    {
            // see what type of packet we have so that
            // the decision we take depends on it...
        NetworkInterface nic = parent.interfaces[packet.inNic];
        byte type = getPacketType(packet);
        byte data[] = packet.data;

        if (type != NORMAL_FRAME)
        {
                // get the IP address..
                // the dest address begins at the 17th byte since the
                // beginning of the IP section of the packet...
            int destAddress = readDestIP(packet);

            int srcIP = readSrcIP(packet);

                // if the destination is ourself then
                // return true so that the packet will
                // traverse further down the device's
                // packet module chain...
                //
                // inNic can be negative only if the packet isoriginating
                // from this device is basically heading out...
            if (packet.inNic >= 0 &&
                    destAddress == ipTable.ipAddress[packet.inNic])
            {
                parent.writeToConsole(DeviceEvent.PACKET_ALLOWED,
                               "    Src: " +
                                Utils.ipToString(srcIP) + " (" + srcIP + ")\n" +
                               "    Dst: " +
                               Utils.ipToString(destAddress) +
                                    " (" + destAddress+ ")\n");
                return 0;
            }

                // we will give the destination address the
                // biggest possible masklength. So that we can
                // get the most
            NetworkInterface targetInterface = null;

            if (defaultInterface >= 0)
            {
                targetInterface = parent.interfaces[defaultInterface];
            }

            Route whichRoute = routingTable.findRoute(destAddress, 32);
            if (whichRoute != null)
            {
                targetInterface = whichRoute.destInterface;
            }

                // dont write the packet if its coming to the same packet
            if (targetInterface != null &&
                    targetInterface.interfaceID != packet.inNic)
            {
                int targetIP = ipTable.ipAddress[targetInterface.interfaceID];
                String message =
                               "    Src: " +
                                Utils.ipToString(srcIP) + " (" + srcIP + ")\n" +
                               "    Dst: " +
                               Utils.ipToString(destAddress) +
                                    " (" + destAddress+ ")\n" +
                               "    Interface " + targetInterface.interfaceID +
                               ": " +
                               Utils.ipToString(targetIP) +
                               " (" + targetIP + ")\n";

                if (whichRoute != null)
                {
                    message += "    Route: " + whichRoute.toString() + "\n";
                }
                parent.writeToConsole(DeviceEvent.PACKET_FORWARDED, message);

                packet.outNic = targetInterface.interfaceID;
                packet.driverID = driverID;
                parent.transmitPacket(packet);

                return -1;
            } else
            {
                parent.writeToConsole(DeviceEvent.PACKET_DROPPED,
                               "    Src: " +
                                Utils.ipToString(srcIP) + " (" + srcIP + ")\n" +
                               "    Dst: " +
                               Utils.ipToString(destAddress) +
                                    " (" + destAddress+ ")\n");
            }
        }
        return 0;
    }

        /**
         * Get the type of packet it is.
         */
    protected byte getPacketType(Packet packet)
    {
        byte data[] = packet.data;

            // check protocol type... it has to be 0x0800
            // for an IP packet...
        if (data[12] != 0x08 || data[13] != 0) return INVALID_FRAME;

            // do other checks to see if we have proper
            // IP packets...

            // ignore non ipv4 packets...
        if (data[IP_OFFSET] >> 4 != 0x04) return INVALID_FRAME;

            // get the header length..
        int headerLen = data[IP_OFFSET] & 0x0f;

            // TODO:: Validate the header checksum

        return NORMAL_FRAME;
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
         * Packet drivers.
         */
    protected PacketDriver kids[];

        /**
         * Adds a new child.
         */
    public void addChild(PacketDriver driver)
    {
        synchronized (this)
        {
            if (driver instanceof TCPProtocol)
            {
                kids[0] = driver;
            } else if (driver instanceof UDPProtocol)
            {
                kids[1] = driver;
            } else if (driver instanceof ICMPProtocol)
            {
                kids[2] = driver;
            }
        }
    }
}
