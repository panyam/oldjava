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
 * A class for adding contacts.
 */
public class AddDeviceDialog extends NetworkDialog implements
                                        KeyListener
{
    public static int HOST_COUNT = 0;
    public static int FW_COUNT = 0;
    public static int LAN_SWITCH_COUNT = 0;
    public static int ROUTER_COUNT = 0;

    Device currDevice = null;

    public final static int ADD_HOST = 0;
    public final static int ADD_FIREWALL = 1;
    public final static int ADD_LAN_SWITCH = 2;
    public final static int ADD_ROUTER = 3;

        /**
         * The name of the device. 
         * Common to all devices.
         */
    TextField deviceNameField = new TextField(15);

        /**
         * The number of ports per devices.
         * Also common to all devices.
         */
    TextField numIntsField = new TextField("2", 5);

        /**
         * IP Address Field.
         */
    TextField ipField = new TextField("10.0.0.0");
    Label ipFieldLabel = new Label("IP Address: ");

        /**
         * Constructor.
         */
    public AddDeviceDialog(Frame parent)
    {
        super(parent, "Add Device", true, true);

        Container contentPanel = getContentPanel();
        contentPanel.setLayout(new GridLayout(3, 2));

        contentPanel.add(new Label("Device Name: "));
        contentPanel.add(deviceNameField);
        contentPanel.add(new Label("# Interfaces: "));
        contentPanel.add(numIntsField);
        contentPanel.add(ipFieldLabel);
        contentPanel.add(ipField);
        setResizable(false);
    }

        /**
         * Key Pressed event listener.
         */
    public void keyPressed(KeyEvent e)
    {
    }

        /**
         * Key Typed event listener.
         */
    public void keyTyped(KeyEvent e)
    {
    }

        /**
         * Key Released event listener.
         */
    public void keyReleased(KeyEvent e)
    {
    }

        /**
         * Get the device that will be
         * created if OK was pressed.
         */
    public Device getDevice()
    {
        return null;
    }

        /**
         * Gets the host name.
         */
    public String getHostName()
    {
        return deviceNameField.getText();
    }

        /**
         * Gets the ip address.
         */
    public int getIPAddress()
    {
        return Utils.stringToIP(ipField.getText());
    }

        /**
         * Gets the number of interface.
         */
    public int getInterfaceCount()
    {
        try
        {
            return Integer.parseInt(numIntsField.getText());
        } catch (Exception ex)
        {
            return -1;
        }
    }

        // the equivalent of 10.0.0.0;
        // this is incremented each time we show this dialog
    public static int ipCounter = 167772160;

        /**
         * Shows this dialog box.
         */
    public void show(int type)
    {
        ipField.setVisible(false);
        ipFieldLabel.setVisible(false);
        numIntsField.setEnabled(true);
        if (type == ADD_HOST)
        {
            numIntsField.setText("1");
            numIntsField.setEnabled(true);
            setTitle("Add new host");
            deviceNameField.setText("host_" + HOST_COUNT);
            ipField.setVisible(true);
            ipFieldLabel.setVisible(true);
            ipField.setText(((ipCounter >> 24) & 0xff) + "." +
                            ((ipCounter >> 16) & 0xff) + "." +
                            ((ipCounter >> 8) & 0xff) + "." +
                             (ipCounter & 0xff));
        } else if (type == ADD_ROUTER)
        {
            setTitle("Add new router");
            deviceNameField.setText("router_" + ROUTER_COUNT);
        } else if (type == ADD_FIREWALL)
        {
            setTitle("Add new Firewall");
            deviceNameField.setText("fwall_" + FW_COUNT);
        } else if (type == ADD_LAN_SWITCH)
        {
            setTitle("Add new switch");
            deviceNameField.setText("switch_" + LAN_SWITCH_COUNT);
        }
        super.setVisible(true);
    }
}
