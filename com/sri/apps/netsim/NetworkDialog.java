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
public class NetworkDialog extends Dialog implements ActionListener, WindowListener
{
    protected Container contentPanel = new Panel();
    protected Panel southPanel = new Panel();

    protected Button okButton = new Button("Ok");
    protected Button cancelButton = new Button("Cancel");
    protected boolean wasCancelled = false;

        /**
         * Constructor.
         */
    public NetworkDialog(Frame parent, String title,
                     boolean isModal, boolean showCancel)
    {
        super(parent, title, isModal);
        addWindowListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        southPanel.add(okButton);
        if (showCancel) southPanel.add(cancelButton);
        super.setLayout(new BorderLayout());
        super.add("South", southPanel);
        super.add("Center", contentPanel);
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            okButtonPressed();

            setVisible(false);
            wasCancelled = false;
        } else if (e.getSource() == cancelButton)
        {
            cancelButtonPressed();
            setVisible(false);
            wasCancelled = true;
        }
    }

        /**
         * OK Button pressed.
         */
    protected void okButtonPressed()
    {
    }

        /**
         * Cancel Button pressed.
         */
    protected void cancelButtonPressed()
    {
    }

        /**
         * Tells if we were cancelled.
         */
    public boolean wasCancelled()
    {
        return wasCancelled;
    }

        /**
         * Return the container in which
         * content can be added.
         */
    public Container getContentPanel()
    {
        return contentPanel;
    }

        /**
         * Over ride to do nothing.
         */
    public void setLayout(LayoutManager lmgr)
    {
    }

        /**
         * Centers the dialog in the screen.
         */
    public void centerDialog()
    {
        Utils.centerWindow(this, true);
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
}
