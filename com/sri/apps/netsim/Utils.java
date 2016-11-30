package com.sri.apps.netsim;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;

import com.sri.gui.core.*;
import com.sri.gui.ext.drawing.*;

/**
 * A general utility class.
 */
public class Utils
{
    public final static String SPACES[] = 
    {
        "",
        " ",
        "  ",
        "   ",
        "    ",
        "     ",
        "      ",
        "       ",
        "        ",
        "         ",
        "          ",
        "           ",
        "            ",
        "             ",
        "              ",
        "               ",
        "                ",
        "                 ",
        "                  ",
    };

        /**
         * Subnet masks.
         */
    public final static int SUBNET_MASK[] =
    {
        0xffffffff << 0, 
    };

        /**
         * Convert a mac address to a dotted string form.
         */
    public static String macToString(long mac)
    {
        return ((mac >> 40) & 0xff) + ":" +
               ((mac >> 32) & 0xff) + ":" +
               ((mac >> 24) & 0xff) + ":" +
               ((mac >> 16) & 0xff) + ":" +
               ((mac >>  8) & 0xff) + ":" +
               (mac & 0xff);
    }

        /**
         * Convert a string to dotted IP address form.
         */
    public static String ipToString(int ip)
    {
        return ((ip >> 24) & 0xff) + "." +
               ((ip >> 16) & 0xff) + "." +
               ((ip >> 8) & 0xff) + "." +
                (ip & 0xff);
    }

        /**
         * Converts a subnet mask identifier to the
         * mask length.
         * Input can be in the format /xxx 
         * or
         * xxx.xxx.xxx.xxx
         */
    public static int stringToMaskLength(String maskString)
    {
        if (maskString.charAt(0) == '/')
        {
            return Integer.parseInt(maskString.substring(1));
        } else
        {
            int ip = stringToIP(maskString);
            int length = 0;
            while (ip != 0) 
            {
                ip >>= 1;
                length++;
            }
        }
        return -1;
    }

        /**
         * Given a string in the format ww.xx.yy.zz,
         * converts it to a string equivalent.
         */
    public static int stringToIP(String ipString)
    {
        StringTokenizer tokens = new StringTokenizer(ipString, ".", false);
        int out = (Integer.parseInt(tokens.nextToken()) << 24) | 
                  (Integer.parseInt(tokens.nextToken()) << 16) | 
                  (Integer.parseInt(tokens.nextToken()) << 8) | 
                  Integer.parseInt(tokens.nextToken());
        return out;
    }

        /**
         * Given a string in the format xx:xx:xx:xx:xx:xx
         * converts it to the appropriate mac address.
         */
    public static long stringToMac(String macString)
    {
        StringTokenizer tokens = new StringTokenizer(macString, ":", false);
        long out = (Integer.parseInt(tokens.nextToken()) << 40) | 
                   (Integer.parseInt(tokens.nextToken()) << 32) | 
                   (Integer.parseInt(tokens.nextToken()) << 24) | 
                   (Integer.parseInt(tokens.nextToken()) << 16) | 
                   (Integer.parseInt(tokens.nextToken()) << 8) | 
                    Integer.parseInt(tokens.nextToken());
        return out;
    }

        /**
         * Get the parent frame of a given component.
         */
    public static Frame getParentFrame(Component comp)
    {
        Component temp = comp;
        Component parent = temp.getParent();
        while (!(parent != null && (parent instanceof Frame)))
        {
            temp = parent;
            parent = temp.getParent();
        }
        return (Frame)parent;
    }

        /**
         * Given a window, centers it on the screen.
         */
    public static void centerWindow(Window window, boolean pack)
    {
        placeWindow(window, Alignment.CENTER, Alignment.CENTER, pack);
        if (pack) window.pack();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension ts = window.getSize();
        window.setLocation((ss.width - ts.width) / 2,
                           (ss.height - ts.height) / 2);
    }

        /**
         * Given a window, centers it on a certain position on the screen.
         */
    public static void placeWindow(Window window, int vAlign, int hAlign, boolean pack)
    {
        if (pack) window.pack();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension ts = window.getSize();

        int windowX = 0;
        int windowY = 0;

        if (vAlign == Alignment.CENTER)
        {
            windowY = (ss.height - ts.height) / 2;
        } else if (vAlign == Alignment.BOTTOM)
        {
            windowY = ss.height - ts.height;
        }

        if (hAlign == Alignment.CENTER)
        {
            windowX = (ss.width - ts.width) / 2;
        } else if (hAlign == Alignment.RIGHT)
        {
            windowX = ss.width - ts.width;
        }

        window.setLocation(windowX, windowY);
    }

        /**
         * Find the device with the given name in a scene
         */
    public static Device findDevice(String name, Scene scene)
    {
        if (name == null) return null;
        for (int i = 0;i < scene.nShapes;i++)
        {
            if (! (scene.shapes[i] instanceof Device)) continue;
            Device dev = (Device)scene.shapes[i];
            if (dev.getName().equalsIgnoreCase(name)) return dev;
        }
        return null;
    }

        /**
         * Auto Assign mac addresses to all network
         * devices in a scene.
         */
    public static void autoAssignMacAddresses(Scene scene)
    {
        IANA.reset();

        for (int i = 0;i < scene.nShapes;i++)
        {
            if (! (scene.shapes[i] instanceof Device)) continue;
            Device dev = (Device)scene.shapes[i];
            dev.setMacAddress(IANA.getMacAddress());
            for (int j = 0;j < dev.nInterfaces;j++)
            {
                //dev.interfaces[j].setMacAddress(IANA.getMacAddress());
            }
        }
    }

        /**
         * Set the console for alll network elements.
         */
    public static void setConsole(Scene scene, Console console)
    {
        for (int i = 0;i < scene.nShapes;i++)
        {
            if (scene.shapes[i] instanceof Device)
            {
                ((Device)scene.shapes[i]).setConsole(console);
            }
        }

        //for (int i = 0;i < nConnectors;i++)
        //{
            //connectors[i].setConsole(console);
        //}
    }

        /**
         * Remove a given device in a scene.
         */
    public static void removeDevice(Scene scene, Device dev)
    {
        removeDevice(scene, scene.find(dev));
    }

        /**
         * Remove a device at the given index from a scene.
         */
    public static void removeDevice(Scene scene, int index)
    {
        com.sri.gui.ext.drawing.Shape shapes[] = scene.shapes;
        if (index < 0 || ! (shapes[index] instanceof Device)) return ;

        Connector connectors[] = scene.connectors;

        Device which = (Device)shapes[index];

        scene.nShapes --;

        for (int i = index;i < scene.nShapes;i++) shapes[i] = shapes[i + 1];
        shapes[scene.nShapes] = null;

            // now remove all connectors that refer to this device
        for (int i = 0;i < scene.nConnectors;i++)
        {
            if (((Link)connectors[i]).connectsTo(which))
            {
                ((Link)connectors[i]).disconnect();
                connectors[i] = null;
            }
        }

            // now to go aroudn "patching" all null spots
            // in the list of "connectors"
        int pos = 0;
        int curr = 0;
        while (curr < scene.nConnectors)
        {
            //while (pos < nConnectors && connectors[pos] != null) pos++;
            //curr = pos;
            while (curr < scene.nConnectors &&
                    connectors[curr] == null) curr++;
            while (curr < scene.nConnectors &&
                    connectors[curr] != null) 
            {
                connectors[pos++] = connectors[curr++];
            }
        }
        scene.nConnectors = pos;
    }

        /**
         * Remove the connector at the given index from a scene.
         */
    public static void removeLink(Scene scene, Link link)
    {
        removeLink(scene, scene.find(link));
    }

        /**
         * Remove the connector at the given index from a scene.
         */
    public static void removeLink(Scene scene, int which)
    {
        if (which < 0 &&
                ! (scene.connectors[which] instanceof Link)) return ;

        ((Link)scene.connectors[which]).disconnect();
        scene.removeConnector(which);
    }
}
