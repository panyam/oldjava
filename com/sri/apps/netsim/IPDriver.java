
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
public interface IPDriver extends ProtocolDriver
{
        /**
         * Get the routing table.
         */
    //public RoutingTable getRoutingTable();

        /**
         * Reads the Destination IP address of an ethernet packet.
         */
    public int readDestIP(Packet packet);

        /**
         * Writes the dest IPAddress of the packet.
         */
    public void writeDestIP(Packet packet, int IP);

        /**
         * Reads the dest IP address of an ethernet packet.
         */
    public int readSourceIP(Packet packet);

        /**
         * Writes the Source IPAddress of the packet.
         */
    public void writeSourceIP(Packet packet, int IP);
}
