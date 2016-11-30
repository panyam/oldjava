package com.sri.apps.netsim;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;
import com.sri.gui.ext.drawing.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A class for adding contacts.
 */
public class AddLinkDialog extends NetworkDialog implements ItemListener
{
        /**
         * The scene object that contains all the device information.
         */
    Scene ourScene = null;

        /**
         * The starting device.
         */
    java.awt.List startingDeviceList = new java.awt.List(5);

        /**
         * The interface on the starting device
         * where the link should start.
         */
    Choice startingInterface = new Choice();

        /**
         * The ending device.
         */
    java.awt.List endingDeviceList = new java.awt.List(5);

        /**
         * The interface on the ending device
         * where the link should start.
         */
    Choice endingInterface = new Choice();

        /**
         * Constructor.
         */
    public AddLinkDialog(Frame parent, Scene scene)
    {
        super(parent, "Add Link", true, true);

        this.ourScene = scene;

        Container contentPanel = getContentPanel();
        contentPanel.setLayout(new GridLayout(1, 2));

        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel fromIntPanel = new Panel(new BorderLayout());
        Panel toIntPanel = new Panel(new BorderLayout());

        fromPanel.add("North", new Label("Starting Device: "));
        fromPanel.add("Center", startingDeviceList);
        fromIntPanel.add("North", new Label("Interface: "));
        fromIntPanel.add("Center", startingInterface);
        fromPanel.add("South", fromIntPanel);

        toPanel.add("North", new Label("Ending Device: "));
        toPanel.add("Center", endingDeviceList);
        toIntPanel.add("North", new Label("Interface: "));
        toIntPanel.add("Center", endingInterface);
        toPanel.add("South", toIntPanel);

        contentPanel.add(fromPanel);
        contentPanel.add(toPanel);

        startingInterface.addItemListener(this);
        endingInterface.addItemListener(this);
        startingDeviceList.addItemListener(this);
        endingDeviceList.addItemListener(this);

        setResizable(false);
    }

        /**
         * Get the starting Device.
         */
    public Device getStartingDevice()
    {
        return Utils.findDevice(startingDeviceList.getSelectedItem(), ourScene);
    }

        /**
         * Get the starting interface.
         */
    public int getStartingInterface()
    {
        String item = startingInterface.getSelectedItem();
        if (item != null)
        {
            return Integer.parseInt(item.substring(10));
        }
        return -1;
    }

        /**
         * Get the Ending Device.
         */
    public Device getEndingDevice()
    {
        return Utils.findDevice(endingDeviceList.getSelectedItem(), ourScene);
    }

        /**
         * Get the ending interface.
         */
    public int getEndingInterface()
    {
        String item = endingInterface.getSelectedItem();
        if (item != null)
        {
            return Integer.parseInt(item.substring(10));
        }
        return -1;
    }

        /**
         * Shows this dialog box.
         */
    public void setVisible(boolean sh)
    {
        if (sh)
        {
            int nItems = (ourScene == null ? 0 : ourScene.nShapes);

            startingInterface.removeAll();
            endingInterface.removeAll();
            startingDeviceList.removeAll();
            endingDeviceList.removeAll();

            for (int i = 0;i < nItems;i++)
            {
                if (! (ourScene.shapes[i] instanceof Device)) continue ;
                Device dev = (Device)ourScene.shapes[i];

                startingDeviceList.add(dev.getName());
                endingDeviceList.add(dev.getName());
            }
        }

        invalidate();
        Utils.centerWindow(this, true);
        super.setVisible(sh);
    }

        /**
         * Shows all the interfaces of a selected interface on
         * either the starting or the ending device.
         */
    protected void selectDevice(java.awt.List devList,
                                Choice intList, int which)
    {
        intList.removeAll();

        if (which < 0) return ;

        Device dev = Utils.findDevice(devList.getSelectedItem(), ourScene);

        if (dev == null) return ;

        int nInts = dev.getInterfaceCount();

        for (int j = 0;j < nInts;j++)
        {
            if ( ! dev.getInterface(j).connected)
            {
                intList.add("Interface " + j);
            }
        }
    }

        /**
         * Item Event listener.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        Object src = ie.getSource();
        if (src == startingInterface)
        {
        } else if (src == endingInterface)
        {
        } else if (src == startingDeviceList)
        {
            selectDevice(startingDeviceList,
                         startingInterface,
                         startingDeviceList.getSelectedIndex());
        } else if (src == endingDeviceList)
        {
            selectDevice(endingDeviceList,
                         endingInterface,
                         endingDeviceList.getSelectedIndex());
        }
    }
}
