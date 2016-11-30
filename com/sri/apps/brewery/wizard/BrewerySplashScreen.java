
package com.sri.apps.brewery.wizard;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.wizard.sections.*;

/***    XML Classes     ***/
import  org.w3c.dom.*;
import  org.apache.xerces.dom.DocumentImpl;
import  org.apache.xerces.dom.DOMImplementationImpl;
import  org.w3c.dom.Document;
import  org.apache.xml.serialize.OutputFormat;
import  org.apache.xml.serialize.Serializer;
import  org.apache.xml.serialize.SerializerFactory;
import  org.apache.xml.serialize.XMLSerializer;
import javax.xml.parsers.*;

/**
 * The splash screen for the Brewery Wizard before it loads.
 *
 * @author Sri Panyam
 */

public class BrewerySplashScreen extends JWindow
{
        /**
         * Label to show the name of the current task being performed.
         */
    protected JLabel taskNameLabel;

        /**
         * Label to show the status of the current task.
         */
    protected JLabel statusLabel;

        /**
         * Constructor.
         */
    public BrewerySplashScreen(int width, int height)
    {
        super();
        setSize(width, height);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }
}
