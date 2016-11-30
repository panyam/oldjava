package com.sri.apps.netsim;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * An interface class.
 *
 * Basically the idea is that Devices listen on interfaces and
 * links connect to interfaces on devices.
 */
public abstract class NetworkInterface implements XMLObject
{
        /**
         * Tells if a link is connected to this
         * interface.
         */
    public boolean connected = false;

        /**
         * The Link that connects to this interface.
         */
    public Link link = NullLink.DEFAULT_LINK;

        /**
         * The interface ID.
         */
    public short interfaceID = 0;

        /**
         * Tells if the port is active.
         */
    public boolean isEnabled = true;

        /**
         * The devices that holds this interface.
         */
    public Device parentDevice = null;

        /**
         * Name of the interface.
         * Eg 2/2 1/2, Fe/10/0/0 and so on.
         */
    public String name = "";

        /**
         * Constructor.
         */
    protected NetworkInterface()
    {
    }

        /**
         * Constructor.
         */
    public NetworkInterface(Device parent, int id, String name)
    {
        this.parentDevice = parent;
        this.name = name;
        this.interfaceID = (short)id;
        //macAddress = IANA.getMACAddress();
        isEnabled = true;
    }

        /**
         * Gets the ProtocolDriver class that is most suitable to
         * handle packets recieved on this interface.
         */
    public abstract Class getProtocolDriver();

        /**
         * Return the name of the interface.
         */
    public String getName()
    {
        return name;
    }

        /**
         * Tells if the interface is enabled or not.
         */
    public boolean isEnabled()
    {
        return isEnabled;
    }

        /**
         * Writes a packet to the network.
         * Must be called by the device.
         */
    public void writeToLink(Packet packet, Network network)
    {
        if (link.isRunning())
        {
            packet.src = parentDevice;
            packet.dest = link;
            network.addEvent(packet);
        }
    }

        /**
         * Calculate the link.
         */
    public void setLink(Link link)
    {
        this.link = link;
    }

        /**
         * Process an XML node.
         */
    public void readXMLNode(Element intNode) throws Exception
    {
        short intId = Short.parseShort(intNode.getAttribute(XMLParams.INTERFACE_ID));
        //long intMAC = Long.parseLong(intNode.getAttribute(XMLParams.INTERFACE_MAC));
        //setMacAddress(intMAC);
        interfaceID = intId;
    }

        /**
         * The interface as an XML node
         */
    public Element getXMLNode(Document doc)
    {
        Element intNode = doc.createElement(XMLParams.DEVICE_INTERFACE_TAG_NAME);
        intNode.setAttribute(XMLParams.INTERFACE_ID, "" + interfaceID);
        return intNode;
    }

        /**
         * Copy info from another interface.
         */
    public void copyFrom(NetworkInterface iface)
    {
        this.connected = iface.connected;
        this.link = iface.link;
        this.interfaceID = iface.interfaceID;
        this.isEnabled = iface.isEnabled;
        this.parentDevice = iface.parentDevice;
        this.name = iface.name;
    }
}
