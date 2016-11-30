

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
 * An implementation of the IP module.   Takes care of all routing and so
 * on.
 */
public class IPDriverImpl implements IPDriver
{
    protected boolean isRunning = false;

        /**
         * A parent driver.
         * Basically we know how to work if its an ethDriver.
         * For other parent types, we may need extensions of this
         * classes.
         */
    protected ProtocolDriver ethParent = null;

        /**
         * TCP driver that would be a child for this object.
         * We should be using a table and adding drivers as they come
         * along.
         */
    protected ProtocolDriver tcpChild = null;

        /**
         * UDP driver that would be a child for this object.
         * We should be using a table and adding drivers as they come
         * along.
         */
    protected ProtocolDriver udpChild = null;

        /**
         * Ping driver that would be a child for this object.
         * We should be using a table and adding drivers as they come
         * along.
         */
    protected ProtocolDriver pingChild = null;

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
    public IPDriverImpl(int layer)
    {
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
         * Reads the Destination IP address of an ethernet packet.
         */
    public int readDestIP(Packet packet)
    {
        return IPUtils.readDestIP(packet, packet.driverOffset[layer]);
    }

        /**
         * Writes the dest IPAddress of the packet.
         */
    public void writeDestIP(Packet packet, int ip)
    {
        IPUtils.writeDestIP(packet, ip, packet.driverOffset[layer]);
    }

        /**
         * Reads the dest IP address of an ethernet packet.
         */
    public int readSourceIP(Packet packet)
    {
        return IPUtils.readSrcIP(packet, packet.driverOffset[layer]);
    }

        /**
         * Writes the Source IPAddress of the packet.
         */
    public void writeSourceIP(Packet packet, int ip)
    {
        IPUtils.writeSrcIP(packet, ip, packet.driverOffset[layer]);
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

        packet.headerLength[layer] =
            (short)(packet.data[packet.driverOffset[layer]] & 0x0f);
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
         * Adds a new child.
         */
    public void registerChild(ProtocolDriver driver, byte protoID)
    {
        if (protoID == Protocols.TCP_PROTOCOL) tcpChild = driver;
        else if (protoID == Protocols.UDP_PROTOCOL) udpChild = driver;
        else if (protoID == Protocols.PING_PROTOCOL) pingChild = driver;

            // other ones can be added on later.
    }

        /**
         * Add a protocol as a parent that can be used to
         * encapsulate these packets.
         */
    public void addParent(ProtocolDriver parent)
    {
        if (parent instanceof EthernetDriver) ethParent = parent;
    }

        /**
         * Processes an incoming packet before returning it to the parent.
         *
         * So what are the steps?
         */
    public ProtocolDriver processInPacket(Packet packet,
                                          Device parent, 
                                          Network network)
    {
        return null;
    }

        /**
         * Processes an outgoing packet before returning it to the parent.
         * If output is null then it means that the packet has been
         * consumed and it will not be availale to the device again.
         *
         * At this point, the packet.offset and packet.length refer to the
         * where the "child" layer's packet is encapsulated. 
         * So basically when the PacketDriver at the lowermost level writes
         * to a packet, it starts writing from a certain offset from a
         * packet rather than having to write at the start and have each
         * layer resize the packet and copy the payload each time.
         *
         * Basically this function should be called ONLY if a packet has
         * been called previously by a child driver object!!!  basically
         * doign pointer-y kind of stuff here.
         *
         * Unlike the processInPacket function this doesnt return the
         * "next" ProtocolDriver because these must already be defined from
         * the "getPacket()" function call.
         */
    public int processOutPacket(Packet packet, Device parent)
    {
        return -1;
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
}
