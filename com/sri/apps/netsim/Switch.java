package com.sri.apps.netsim;


import java.io.*;
import java.awt.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A host class. Basically is a packet generator.
 */
public class Switch extends Device
{

        /**
         * Default Constructor.
         */
    public Switch(String name, int nInterfaces)
    {
        super();
        setName(name);
        ensureInterfaceCapacity(nInterfaces);
    }

    public String toString()
    {
        return "Switch: " + name;
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element device = super.getXMLNode(doc);
        device.setAttribute(XMLParams.DEVICE_TYPE, "switch");
        return device;
    }

        /**
         * Draws this device.
         */
    public void paint(Graphics g, int xOffset, int yOffset)
    {
        super.paint(g, xOffset, yOffset);

        int tx = x + xOffset;
        int ty = y + yOffset;


            // if the shape isnt visible why draw it?
        if (tx + width >= 0 && ty + height >= 0) 
        {
            FontMetrics fm = g.getFontMetrics();
            int sw = fm.stringWidth("Sw");
            int sh = fm.getAscent();
            g.setColor(Color.black);
            g.fillRect(tx, ty, sw + 6, sh + 6);
            g.setColor(Color.white);
            g.drawString("Sw", tx + 3, ty + sh + 3);
        }
    }
}
