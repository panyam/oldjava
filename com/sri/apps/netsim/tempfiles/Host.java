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
 */
public class Host extends Device
{
        /**
         * Our host module.
         */
    protected HostModule hostModule = null;

        /**
         * Default Constructor.
         */
    public Host(String name, int nInterfaces)
    {
        setName(name);
        ensureInterfaceCapacity(nInterfaces);
        hostModule = new HostModule(this);
    }

        /**
         * Get the string representation.
         */
    public String toString()
    {
        return "Host: " + name;
    }

        /**
         * Draws this Link.
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
            int sw = fm.stringWidth("H");
            int sh = fm.getAscent();
            g.setColor(Color.black);
            g.fillRect(tx, ty, sw + 6, sh + 6);
            int ip = 0;//((RouterInterface)interfaces[0]).ipAddress;
            String ips = "IP: " + ((ip >> 24) & 0xff) + "." +
                                  ((ip >> 16) & 0xff) + "." +
                                  ((ip >> 8) & 0xff) + "." +
                                  (ip & 0xff);
            int sw2 = fm.stringWidth(ips);
            //g.drawString(ips , tx + ((width - sw2) / 2), ty + height - 5);
            g.setColor(Color.white);
            g.drawString("H", tx + 3, ty + sh + 3);
        }
    }
}
