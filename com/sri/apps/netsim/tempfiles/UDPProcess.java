
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
 * A UDP Server object.
 *
 * Basically a test process that can send and receive UDP packets.
 */
public class UDPProcess extends ProcessObject
{
    short srcPort;
    short destPort;

        /**
         * Sends a packet.
         */
    public void send(Device dev, byte payload[], int length)
    {
        if (length > UDPProtocol.MAX_DATAGRAM_SIZE)
            throw new IllegalArgumentException(
                    "Length cannot exceed maximum datagram size.");

        Packet packet = getPacket(length);

        System.arrayCopy(payload, 0, packet.data, packet.offset, length);

        packet.generator = this;

        dev.transmitPacket(packet, this);
    }

        /**
         * Returns true if packet is an Ethernet Packet.
         */
    public boolean isPacketValid(Packet packet)
    {
        if (parent == null) return false;
        byte data[] = packet.data;
        int offset = packet.offset;

                // we only deal with IP
                // based encapsulation for now...  As we dont 
                // yet know what the encapsulation for non-ip 
                // parents would be
        if (parent instanceof IPProtocol)
        {
            return true;
        }
        return false;
    }

        /**
         * Process the next outgoing packet.
         */
    public int processOutPacket(Packet packet, Device parent) throws Exception
    {
        return 0;
    }

        /**
         * Process the next incoming packet.
         */
    public int processInPacket(Packet packet, Device parent) throws Exception
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

        /**
         * Sets the parent of this packet driver.
         * MUST be called in order to register a driver class as a child of
         * an existing driver class.
         */
    public void setParent(PacketDriver parent)
    {
        if (parent == null || ( ! parent instanceof UDPProtocol))
        {
            throw new IllegalArgumentException(
                        "Parent MUST be a UDPProtocol instance.");
        }

        super.setParent(parent);
    }
}
