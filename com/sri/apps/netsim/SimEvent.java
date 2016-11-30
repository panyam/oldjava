
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
 * Super class of all events that Network Entities send to each other with
 * respect to the simulator.
 */
public abstract class SimEvent 
{
        /**
         * ID of the event.
         */
    public int eventID;

        /**
         * Where the event originates from.
         */
    public NetworkEntity src;

        /**
         * Where the event is destined to.
         */
    public NetworkEntity dest;

        /**
         * Time at which the event should be delivered to the destination.
         */
    public double targetTime;
}
