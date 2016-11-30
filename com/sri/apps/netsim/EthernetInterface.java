
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
public class EthernetInterface extends NetworkInterface
{
        /**
         * The mac address of the interface.
         */
    public long macAddress = 0;

        /**
         * Constructor.
         */
    public EthernetInterface(Device parent, int id, String name)
    {
        super(parent, id, name);
    }

        /**
         * Sets the mac address of this device.
         */
    public void setMacAddress(long macAdd)
    {
        this.macAddress = macAdd;
    }

        /**
         * Gets the ProtocolDriver class that is most suitable to
         * handle packets recieved on this interface.
         */
    public Class getProtocolDriver()
    {
        return EthernetDriver.class;
    }

        /**
         * Process an XML node.
         */
    public void readXMLNode(Element intNode) throws Exception
    {
        super.readXMLNode(intNode);
        long intMAC = Long.parseLong(intNode.getAttribute(XMLParams.INTERFACE_MAC));
        setMacAddress(intMAC);
    }

        /**
         * The interface as an XML node
         */
    public Element getXMLNode(Document doc)
    {
        Element intNode = super.getXMLNode(doc);
        intNode.setAttribute(XMLParams.INTERFACE_MAC, "" + macAddress);
        return intNode;
    }

        /**
         * Copy info from another interface.
         */
    public void copyFrom(NetworkInterface iface)
    {
        super.copyFrom(iface);
        if (iface instanceof EthernetInterface)
        {
            this.macAddress = ((EthernetInterface)iface).macAddress;
        }
    }
}
