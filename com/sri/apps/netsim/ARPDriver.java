
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
public interface ARPDriver extends ProtocolDriver
{
        /**
         * This will be called by the parent ethernet driver.
         * This should happen only if the destination mac address is 0.
         */
    public void transmitPacket(Packet packet,
                               Device parentDevice);
}
