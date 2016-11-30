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
 * A class for generating traffic from a host.
 */
public class GTDialog extends NetworkDialog implements ItemListener
{
    public final static int UDP_TRAFFIC = 0;
    public final static int TCP_TRAFFIC = 1;
    public final static int TCP_SESSION = 2;

    Label trafficType = new Label("Traffic Type:");
    CheckboxGroup trafficTypeGroup = new CheckboxGroup();
    Checkbox udpTraffic = new Checkbox("UDP Packets", trafficTypeGroup, false);
    Checkbox tcpTraffic = new Checkbox("TCP Packets", trafficTypeGroup, false);
    Checkbox tcpSessionTraffic =
            new Checkbox("TCP Session", trafficTypeGroup, true);

        /**
         * The source host from which the traffic is originating.
         */
    Choice srcHosts = new Choice();
    Label srcIPLabel = new Label("255.255.255.255");

        /**
         * Set of all hosts that can be targeted.
         */
    Choice destHosts = new Choice();

        /**
         * What we are doing is that the destination could 
         * have multiple IPs, because we are includgn
         * firewalls to be target hosts as well.
         * This is only valid for a NATed connection.
         * In all other cases, the firewall would just drop
         * the packet.
         */
    Choice destIPs = new Choice();
    TextField destPortField = new TextField("19");

    Scene network = null;

    Device sourceDevice = null;
    Device targetDevice = null;
    int sourceIP = -1;
    int targetIP = -1;

        /**
         * Constructor.
         */
    public GTDialog(Frame parent)
    {
        super(parent, "Generate Traffic", true, true);

        Container contentPanel = getContentPanel();
        contentPanel.setLayout(new GridLayout(3, 2));

        Panel trafficTypePanel = new Panel();
        trafficTypePanel.add(new Label("Traffic Type: "));
        trafficTypePanel.add(udpTraffic);
        trafficTypePanel.add(tcpTraffic);
        trafficTypePanel.add(tcpSessionTraffic);

        Panel bottomPanel = new Panel(new BorderLayout());
        Panel bottomLeftPanel = new Panel(new GridLayout(5, 1));
        Panel bottomRightPanel = new Panel(new GridLayout(5, 1));

        bottomLeftPanel.add(new Label("Source Host: "));
        bottomLeftPanel.add(new Label("Source IP: "));
        bottomLeftPanel.add(new Label("Target Host: "));
        bottomLeftPanel.add(new Label("Target IP: "));
        bottomLeftPanel.add(new Label("Destination Port: "));

        bottomRightPanel.add(srcHosts);
        bottomRightPanel.add(srcIPLabel);
        bottomRightPanel.add(destHosts);
        bottomRightPanel.add(destIPs);
        bottomRightPanel.add(destPortField);

        bottomPanel.add("West", bottomLeftPanel);
        bottomPanel.add("Center", bottomRightPanel);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add("North", trafficTypePanel);
        contentPanel.add("Center", bottomPanel);

        srcHosts.addItemListener(this);
        destHosts.addItemListener(this);
        destIPs.addItemListener(this);

        //destHosts.select("host_2");
        setResizable(false);
    }

        /**
         * Extract the IPs for a given hsots.
         */
    protected void extractIPs(boolean source)
    {
        /*if (source)
        {
            Host host = (Host)Utils.findDevice(srcHosts.getSelectedItem(), network);
            NetworkInterface iface = host.interfaces[0];
            srcIPLabel.setText(Utils.ipToString(iface.ipAddress));
            sourceDevice = host;
            sourceIP = iface.ipAddress;
        } else
        {
            Device dev = Utils.findDevice(destHosts.getSelectedItem(), network);
            destIPs.removeAll();
            for (int i = 0;i < dev.nInterfaces;i++)
            {
                NetworkInterface iface = dev.interfaces[i];
                destIPs.addItem(Utils.ipToString(iface.ipAddress));
            }
            targetDevice = dev;
            targetIP = (dev.interfaces[0]).ipAddress;
        }*/
    }

        /**
         * Item Event listener.
         */
    public void itemStateChanged(ItemEvent e)
    {
        if (network == null) return ;
        Object source = e.getSource();
        if (source == srcHosts)
        {
            extractIPs(true);
        } else if (source == destHosts)
        {
            extractIPs(false);
        } else if (source == destIPs)
        {
            targetIP = Utils.stringToIP(destIPs.getSelectedItem());
        }
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
         * Get the dest host name
         */
    public String getDestHost()
    {
        return destHosts.getSelectedItem();
    }

        /**
         * Get the target device.
         */
    public Device getTargetDevice()
    {
        return targetDevice;
    }

        /**
         * Get the dest ip address
         */
    public int getDestIP()
    {
        return targetIP;
    }

        /**
         * Get the destination port.
         */
    public int getDestPort()
    {
        String portText = destPortField.getText().trim().toLowerCase();
        int outPort = 0;
        try
        {
            outPort = Integer.parseInt(portText);
        } catch (Exception ex)
        {
            outPort = 19;
        }
        return outPort;
    }

        /**
         * Get the source host name
         */
    public String getSourceHost()
    {
        return srcHosts.getSelectedItem();
    }

        /**
         * Get the source device.
         */
    public Device getSourceDevice()
    {
        return sourceDevice;
    }

        /**
         * Get the source ip address
         */
    public int getSourceIP()
    {
        return sourceIP;
    }

        /**
         * Get the current client.
         */
    public int getTrafficType()
    {
        if (udpTraffic.getState()) return UDP_TRAFFIC;
        else if (tcpTraffic.getState()) return TCP_TRAFFIC;

        return TCP_SESSION;
    }

        /**
         * Show the dialog.
         */
    public void show(Scene network, Device currDevice)
    {
        this.network = network;

        srcHosts.removeAll();
        destHosts.removeAll();

        if (network != null)
        {
            for (int i = 0;i < network.nShapes;i++)
            {
                Device device = (Device)network.shapes[i];

                /*if (device instanceof Host)
                {
                    srcHosts.addItem(device.getName());
                    destHosts.addItem(device.getName());
                }*/

                if (device instanceof Firewall)
                {
                    destHosts.addItem(device.getName());
                }
            }

            if (currDevice != null)
            {
                srcHosts.select(currDevice.name);
            }
            extractIPs(true);
            extractIPs(false);
        }

        Utils.centerWindow(this, true);
        super.setVisible(true);
    }
}
