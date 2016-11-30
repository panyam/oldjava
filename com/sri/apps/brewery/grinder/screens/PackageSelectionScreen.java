/*
 * PackageSelectionScreen.java
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
 * The screen where packages can be selected or unselected.
 *
 * @author  Sri Panyam
 */
public class PackageSelectionScreen extends GUIScreen implements ActionListener
{

        /**
         * Description of this screen.
         */
    protected String description = 
        "This screen shows the package information for " +
        "this application.  Select the necessary packages " +
        "and proceed.";
    
        /**
         * Constructor.
         */
    public PackageSelectionScreen() throws Exception
    {
    }

        /**
         * Initialise the screen.
         *
         * Also takes care of the GUI and all.  This is called ONLY once
         * during the whole program.
         */
    public void initialise() throws Exception
    {
            // now lay out the GUI components...
    }

        /**
         * Get the title of this screen.
         */
    public String getTitle()
    {
        return "Package Selection...";
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
    }

        /**
         Write the details of this to the output strea
         */
    public void writeInfo(DataOutputStream oStream) throws Exception
    {
    }

        /**
         * Called when this state is entered.
         *
         * This is like an initialisation code for each entry 
         * into this screen.
         */
    public void onEntry(GrinderScreen from)
    {
        gParent.setNextEnabled(true);
    }
    
        /**
         * Called whent he checkboxes are clicked.
         */
    public void actionPerformed (java.awt.event.ActionEvent actionEvent)
    {
    }
    
        /**
         * Gets the panel which can be used to show
         * the parameters of this screen.
         */
    //public getParameterPanel() { return paramPanel; }

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
        return "";//fieldNames[index]
    }

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
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName)
    {
        return -1;
    }
}
