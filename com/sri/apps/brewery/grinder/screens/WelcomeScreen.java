/*
 * WelcomeScreen.java
 *
 * Created on 21 July 2004, 15:03
 */

package com.sri.apps.brewery.grinder.screens;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.io.*;

/**
 * The welcome screen.
 *
 * @author  Sri Panyam
 */
public class WelcomeScreen extends GUIScreen
{
    
        /**
         * The image that will be used as the splash screen.
         */
    protected Object splashImage = null;

        /**
         * Names of the persistable fields.
         */
    private final static String fieldNames[] = new String[]
    {
        "Title",
        "Description",
        "Welcome Label",
    };

        /**
         * Types of the persistable fields.
         */
    private final static PersistableType fieldTypes[] = new PersistableType[]
    {
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
    };

        /**
         * Names of the persistable fields.
         */
    private String fieldValues[] = new String[]
    {
        "Title",
        "This screen shows welcomes the user to the installation process.",
        "Welcome to the installation process."
    };

        /**
         * Description of this screen.
         */
    protected String description = fieldValues[1];

        /**
         * The welcome label.
         */
    protected JLabel welcomeLabel = new JLabel(fieldValues[2]);

        /**
         * Constructor.
         */
    public WelcomeScreen()
        throws Exception
    {
        initialise();
    }

        /**
         * Initialise the screen.
         */
    public void initialise() throws Exception
    {
    }

        /**
         * Get the title of this screen.
         */
    public String getTitle()
    {
        return fieldValues[0];
    }

        /**
         * Get the description of this screen.
         */
    public String getDescription()
    {
        return description;
    }

        /**
         * Called when the screen is entered but before displayed.
         */
    public void onEntry(GrinderScreen from)
    {
        gParent.setPreviousEnabled(false);
    }

        /**
         * Called when going back.
         */
    public boolean onBackward (GrinderScreen to)
    {
        return true;
    }
    
        /**
         * Called when proceeding forward.
         */
    public boolean onForward (GrinderScreen to)
    {
        return true;
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
        fieldValues[fieldIndex] = value.toString();
        if (fieldIndex == 2)
        {
            welcomeLabel.setText(fieldValues[2]);
        }
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
        return fieldValues[index];
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
        return fieldTypes[index];
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
        return fieldNames.length;
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
        return fieldNames[index];
    }

        /**
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName)
    {
        for (int i = 0;i < fieldNames.length;i++)
        {
            if (fieldNames.equals(fieldName)) return i;
        }
        return -1;
    }
}
