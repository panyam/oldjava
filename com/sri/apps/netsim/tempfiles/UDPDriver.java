

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
 * The IP Stack object.
 * Basically if a udp packet has to be accepted by a host module
 * the host module has to have an UDP Stack object and the packet
 * must be validated against that UDPStack Object.
 */
public class UDPDriver extends PacketDriver implements UDPProtocol
{
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
}
