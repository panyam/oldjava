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
 * A dialog for our purposes.
 */
public class ConsoleDialog extends Dialog
                           implements
                                ActionListener,
                                WindowListener,
                                Console
{
    protected Button clearButton = new Button("Clear");

    protected TextArea consoleText = new TextArea("", 50, 35);

        /**
         * Constructor.
         */
    public ConsoleDialog(Frame parent)
    {
        super(parent, "Console");
        addWindowListener(this);
        clearButton.addActionListener(this);

        Panel southPanel = new Panel();
        southPanel.add(clearButton);
        setLayout(new BorderLayout());
        add("South", southPanel);
        add("Center", consoleText);
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src == clearButton)
        {
            consoleText.setText("");
        }
    }

    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }

    public void windowClosing(WindowEvent e)
    {
        if (e.getSource() == this) setVisible(false);
    }

        /**
         * Process a message from the device.
         */
    public synchronized void processDeviceMessage(Device device,
                                                  DeviceEvent dev,
                                                  Object info)
        throws Exception
    {
            // ignore messages from switches...
        if (device instanceof Switch) return ;

        consoleText.append(
                "\n--------------" + 
                "\nMessage from: " + device.getName() +
                "\n    Message Type: " + dev.eventString + "\n");

		if (info != null && info instanceof String)
        {
			consoleText.append("    Info: \n" + info.toString());
        }
    }

        /**
         * Process a message from a link.
         */
    public void processLinkMessage(Link link, Integer code, Object info)
        throws Exception
    {
    }
}
