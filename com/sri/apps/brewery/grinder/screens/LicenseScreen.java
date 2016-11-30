/*
 * LicenseScreen.java
 *
 * Created on 21 July 2004, 15:02
 */

package com.sri.apps.brewery.grinder.screens;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.grinder.*;


/**
 * The class that shows the License screen.
 *
 * @author  Sri Panyam
 */
public class LicenseScreen extends GUIScreen implements ActionListener
{
        /**
         * The actual licence information that is displayed.
         */
    protected StringBuffer licenseInfo = new StringBuffer();

        /**
         * Sets the license file name.
         */
    protected String licenseFileName = null;

        /**
         * Description of this screen.
         */
    protected String description = "This screen shows the license information for " +
                                   "this application.  If this cannot be agreed to, " +
                                   "then the installation cannot proceed.";

    protected ButtonGroup acceptGroup = new ButtonGroup();
    
    protected JRadioButton acceptCheckBox = new JRadioButton("I Accept the license agreement.");
    protected JRadioButton noAcceptCheckBox = new JRadioButton("I DO NOT accept the license agreement.");

        /**
         * Constructor.
         */
    public LicenseScreen() throws Exception
    {
        initialise();
    }

        /**
         * Set the license text.
         */
    public void setLicenseText(String lText)
    {
        licenseInfo.delete(0, licenseInfo.length());
        licenseInfo.append(lText);
    }

        /**
         * Set the license file.
         *
         * The license that will be displayed will be exactly as in the
         * file.
         */
    public void setLicenseFile(String fname)
    {
        this.licenseFileName = fname;
    }

        /**
         * Initialise the screen.
         */
    public void initialise() throws Exception
    {
            // now lay out the GUI components...

            // now read the license...
        /*if (licenseFileName == null) return ;

        BufferedInputStream iStream =
            new BufferedInputStream(new FileInputStream(licenseFileName));
        licenseInfo.delete(0, licenseInfo.length());
        byte bytes[] = new byte[1024];

        int nRead = iStream.read(bytes, 0, 1024);

        while (nRead > 0)
        {
            licenseInfo.append(new String(bytes, 0, nRead));
            nRead = iStream.read(bytes, 0, 1024);
        }

        iStream.close();*/
    }

        /**
         * Get the title of this screen.
         */
    public String getTitle()
    {
        return "License Information...";
    }

        /**
         * Get the description of this screen.
         */
    public String getDescription()
    {
        return description;
    }

        /**
         * Read the details of this from the input stream.
         */
    public void readInfo(DataInputStream iStream) throws Exception
    {
        // read the license from the file...
        int licenseLen = iStream.readInt();
        byte bytes[] = new byte[licenseLen];
        iStream.readFully(bytes, 0, licenseLen);
        licenseInfo.delete(0, licenseInfo.length());
        licenseInfo.append(bytes);
    }

        /**
         * Write the details of this to the output stream.
         */
    public void writeInfo(DataOutputStream oStream) throws Exception
    {
        // basically write the license to the file...
        byte bytes[] = licenseInfo.toString().getBytes();
        int len = bytes.length;
        oStream.writeInt(len);
        oStream.write(bytes, 0, len);
    }

        /**
         * Called when this state is entered.
         *
         * This is like an initialisation code for each entry 
         * into this screen.
         */
    public void onEntry(GrinderScreen from)
    {
            // when this screen is entered, disable the
            // next button and set the accept checkbox 
            // to true.
        noAcceptCheckBox.setSelected(true);

        gParent.setNextEnabled(false);

        //return null;
    }
    
        /**
         * Called whent he checkboxes are clicked.
         */
    public void actionPerformed (java.awt.event.ActionEvent actionEvent)
    {
        gParent.setNextEnabled(acceptCheckBox.isSelected());
    }
    
        /**
         * Gets the panel which can be used to show
         * the parameters of this screen.
         */
    //public getParameterPanel() { return paramPanel; }

        /**
         * Set the field value.
         */
    public void setFieldValue(String fieldName, Object value)
    {
        setFieldValue(getFieldIndex(fieldName), value);
    }

        /**
         * Sets the value of a field given its index.
         */
    public void setFieldValue(int fieldIndex, Object value)
    {
    }

        /**
         * Get the value of a field.
         */
    public Object getFieldValue(String fieldName)
    {
        return getFieldValue(getFieldIndex(fieldName));
    }

        /**
         * Get the field value.
         */
    public Object getFieldValue(int index)
    {
        return null;
    }

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(String fieldName)
    {
        return getFieldType(getFieldIndex(fieldName));
    }

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(int index)
    {
        return null;
    }

        /**
         * Gets a field iterator.
         * Returns the (name, type, value) triplet in each
         * "item" in the iterator.
         */
    //public Iterator fieldIterator();

        /**
         * Tells how many fields are there to be persisted.
         */
    public int fieldCount()
    {
        return 0;
    }

        /**
         * Get the field length if its an ARRAY.
         */
    public int getFieldLength(int index)
    {
        return 0;
    }

        /**
         * Get the name of the field at the given index.
         */
    public String getFieldName(int index)
    {
        return "";
    }

        /**
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName)
    {
        return -1;
    }
}
