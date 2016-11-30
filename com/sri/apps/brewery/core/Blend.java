package com.sri.apps.brewery.core;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * A blend is a project or data for the Brewery.
 *
 * While the BreweryWizard, provides controllers/mechanisms for editing the
 * Blend, the Grinder uses the Blend to isntall an application.
 *
 * Data will consist of attribute value pairs belonging to different
 * sectiosn, effeictivaly forming a (section, attrib_name, attrib_value)
 * triplet.
 *
 * Section names and attribute names will be case insensitive.
 *
 * @author Sri Panyam
 */
public class Blend implements Persistable
{
        /**
         * Names of the different fields.
         */
    protected final static int PROJECT_NAME_FIELD = 0;
    protected final static int STARTUP_TASKS_FIELD = 1;
    protected final static int FINAL_TASKS_FIELD = 2;
    protected final static int SCREEN_LIST_FIELD = 3;
    protected final static int PACKAGE_INFO_FIELD = 4;

        /**
         * Names of the main components of this persistable
         * object.
         */
    protected final static String fieldNames[] = new String[]
    {
        "Project Name",
        "Startup Tasks",
        "Final Tasks",
        "Screen List",
        "Package Info",
    };

        /**
         * Types of the main components of this persistable
         * object.
         */
    protected final static PersistableType fieldTypes[] = new PersistableType[]
    {
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.COLLECTION),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.COLLECTION),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.COLLECTION),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.NONE),
    };

        /**
         * Name of this project.
         */
    public String projectName;

        /**
         * The list of tasks that need to be performed before startup.
         * This could include, setting up temporary files, displaying
         * splash screens, creating caches and so on.
         */
    public List startupTasks;

        /**
         * The task that is executed just before quitting the installer.
         * This could be things like deleting temporary files and so on.
         */
    public List finalTasks;
    
        /**
         * All the lists that are used by this Grinder.
         */
    public List screenList;

        /**
         * A list of destination directories.
         */
    public String []destDirs;

        /**
         * List of all packages.
         * Hashed by ID.
         */
    public PackageInfo packageInfo;

        /**
         * Set the name of this project.
         */
    public void setProjectName(String name)
    {
        this.projectName = name;
    }

        /**
         * Get the project name.
         */
    public String getProjectName(String name)
    {
        return projectName;
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
        switch (fieldIndex)
        {
            case PROJECT_NAME_FIELD: projectName = value.toString();
            return ;
            case STARTUP_TASKS_FIELD: startupTasks = (List)value;
            return ;
            case FINAL_TASKS_FIELD: finalTasks = (List)value;
            return ;
            case SCREEN_LIST_FIELD: screenList = (List)value;
            return ;
            case PACKAGE_INFO_FIELD: packageInfo = (PackageInfo)value;
            return ;
        }
    }

        /**
         * Get the name of the field at the given index.
         */
    public String getFieldName(int index)
    {
        return fieldNames[index];
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
        switch (index)
        {
            case PROJECT_NAME_FIELD:
            return projectName;
            case STARTUP_TASKS_FIELD:
            return startupTasks;
            case FINAL_TASKS_FIELD:
            return finalTasks;
            case SCREEN_LIST_FIELD:
            return screenList;
            case PACKAGE_INFO_FIELD:
            return packageInfo;
        }
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

        /**
         * Get a screen given its name.
         */
    public GrinderScreen getScreen(String screenName)
    {
        return null;
    }

        /**
         * Get a screen given its index.
         */
    public GrinderScreen getScreen(int index)
    {
        return null;
    }
}
