
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
 * The UDP Driver object.
 * Basically this driver is needed somewhere in the
 * stack if UDP based objects are to be running.
 */
public interface UDPDriver extends ProtocolDriver
{
        /**
         * Reads the Destination port.
         */
    public int getDestPort(Packet packet);

        /**
         * Writes the dest port.
         */
    public void setDestPort(Packet packet, int port);

        /**
         * Reads the dest port.
         */
    public int getSourcePort(Packet packet);

        /**
         * Writes the Source port.
         */
    public void setSourcePort(Packet packet, int port);

        /**
         * Registers a child, so that when a packet comes
         * on this port, this driver will automatically be fetched.
         */
    public void registerChild(ProtocolDriver driver, int port);
}
