
/**
 * BreweryWizard.java
 *
 * Created on 12 August 2004, 10:57
 */
package com.sri.apps.brewery.wizard;

import javax.swing.*;
import java.util.*;
import java.io.*;
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
 * This is class that handles the installation wizard from a GUI
 * perspective.  This will be the central point or the Mediator for all
 * other objects to get and set info or to access other objects related to
 * the Brewery.
 *
 * @author Sri Panyam
 */
public class BreweryWizard
{

        /**
         * The wizard document that is currently being worked upon - The
         * model for the application
         */
    protected WizardDocument wizDoc = new WizardDocument();

        /**
         * The application frame - the View and part Controller for the
         * Model.
         */
    protected BreweryAppFrame bAppFrame = null;

        /**
         * Constructor.
         */
    public BreweryWizard(String configFile) throws Exception
    {
            // read the config file...
        wizDoc.readConfigFile(configFile);
    }

        /**
         * Get the wizard document.
         */
    public WizardDocument getDocument()
    {
        return wizDoc;
    }

        /**
         * Exit the application.
         */
    public void exitApplication()
    {
        bAppFrame.setVisible(false);
        bAppFrame.dispose();
        AppLauncher.programQuit();
    }

        /**
         * Starts the gui.
         */
    public void startGUI()
    {
        if (bAppFrame == null) bAppFrame = new BreweryAppFrame(this);

        bAppFrame.loadAndShow();
    }
}
