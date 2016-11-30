package com.sri.apps.netsim;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A Protocol Driver that can be an ARP destination.
 */
public interface ARPable extends ProtocolDriver
{
    short getHardwareAddressType();
    short getProtocolAddressType();

    byte getHardwareAddressLength();
    byte getProtocolAddressLength();

    void writeHardwareAddress(byte bytes[], int offset);
    void writeProtocolAddress(byte bytes[], int offset);
}
