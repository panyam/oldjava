

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
 * Super interface of all Ethernet Drivers.
 * Basically this (or an implementation of this) is what the packet will be
 * sent to when it is recieved from a Ethernet NetworkInterface.
 */
public interface EthernetDriver extends ProtocolDriver
{
        /**
         * Sets the source mac address of a packet.
         */
    public void writeSourceMac(Packet packet, long srcMac);

        /**
         * Extracts the source mac address of a packet.
         */
    public long readSourceMac(Packet packet);

        /**
         * Sets the destination mac address of this packet.  
         */
    public void writeDestMac(Packet packet, long srcmac);

        /**
         * Gets the destination mac address of this packet.  
         */
    public long readDestMac(Packet packet);

        /**
         * Extracts the protocol ID of a packet.
         */
    public short readProtocolID(Packet packet);

        /**
         * Extracts the protocol ID of a packet.
         */
    public void writeProtocolID(Packet packet, short protoID);

        /**
         * Adds a new child.
         */
    public void registerChild(ARPable driver, int protoID);
}
