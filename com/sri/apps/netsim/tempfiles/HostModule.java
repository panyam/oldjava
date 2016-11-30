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
 * A host class. Basically is a packet generator.
 * This is the tricky bit.  This should encompass
 * all processes, and protocol stacks.
 * Basically a packet that comes into this module, must be stripped
 * of layers.  For example, mechanisms are needed to see the protocol
 * type and so on for each packet.  So what wll be required is when 
 * a packet comes in, it has to be seen if it belongs to any of the stacks.
 */
public class HostModule extends PacketModule
{
    IPTable ipTable = null;

        /**
         * Set of all ProtocolStack Objects.
         */
    ProcessObject procObjects[] = null;
    int nProcObjects = 0;


        /**
         * Default Constructor.
         */
    public HostModule(Device parent)
    {
        super(parent);

            // find if there is an IPTable object in the parent..
            // if one isnt found then add it in...
        ipTable = (IPTable)parent.getSharedObject(IPTable.class, true);
    }

        /**
         * Add a new process object.
         * Basically puts the process object in the right location.
         * How do we find the right location?
         * At this point only link layer protocol objects are added.
         * We would assume that those who add other protocol objects like
         * TCP or UDP or so on would have set the parents to protocol
         * objects in this list already.
         */
    public synchronized void addProcessObject(ProcessObject po)
    {
        if (procObjects == null) procObjects = new ProcessObject[2];

        if (procObjects.length <= nProcObjects)
        {
            ProcessObject p2[] = procObjects;
            procObjects  = new ProcessObject[nProcObjects + 1];
            System.arraycopy(p2, 0, procObjects, 0, nProcObjects);
        }
        procObjects[nProcObjects++] = po;
    }

        /**
         * Processes an outbound packet.
         */
    public boolean processOutPacket(Packet packet) throws Exception
    {
            // nothing happens
        return true;
    }

        /**
         * This is what happenes while this device is running.
         * It generates and handles packets and so on.  
         * So how does this work?  
         * We need ways of
         * 1) Generating 1 packet at a time.
         * 2) Connection at a time
         * This only applies to TCP sessions
         * An application can handle the packets it accepts in any way it
         * wishes.  It can totally ignore it if it wants to.  The only
         * expection being TCP packets where there mite be some kind of
         * reply...
         */
    public boolean processInPacket(Packet packet) throws Exception
    {
            // what do we do with the packet...
            // We only deal with TCP and UDP traffic
            // also check that a packet is destined 
            // to this host... otherwise drop it...
        //byte proto = IPUtils.readProtocol(packet);
        //int port = TCPUtils.readDestPort(packet);
        //int destIP = IPUtils.readDestIP(packet);

            // now go through the process list and see which
            // one can handle this packet...

        for (int i = 0;i < nProcObjects;i++)
        {
            if (procObjects[i].isPacketValid(packet, 0, packet.length))
            {
                procObjects[i].handlePacket(packet, 0, packet.length);
                return true;
            }
        }

            // if none of the modules match then let the packet through
        return true;
    }

        /**
         * Enables or disables an interface.
         */
    public void enableInterface(NetworkInterface nic, boolean enable)
    {
    }

        /**
         * Reads an XML Node.
         */
    public void readXMLNode(Element elem) throws Exception
    {
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        //device.setAttribute(XMLParams.DEVICE_TYPE, "host");
        Element moduleElement = 
            doc.createElement(XMLParams.PACKET_MODULE_TAG);

        moduleElement.setAttribute(XMLParams.CLASS_PARAM, "" + getClass());

        //Element rtNode = routingTable.getXMLNode(doc);
        //moduleElement.appendChild(rtNode);

        return moduleElement;
    }
}
