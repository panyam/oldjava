/**
 * WizardDocument.java
 *
 * Created on 12 August 2004, 10:57
 */
package com.sri.apps.brewery.wizard;

import javax.swing.*;
import java.util.*;
import java.io.*;
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
 * The document that holds all the wizard data.
 *
 * The data consists of name/value pairs for each sections.  Sections are
 * identified by ID.  Names will translate to IDs.  However, all sections are to
 * be referred by their IDs only.
 *
 * @author Sri Panyam
 */
public class WizardDocument
{

        /**
         * The xml file and the root element in this file that is used for
         * global configurations.
         */
    protected Document configDoc;
    protected Element rootElem;

        /**
         * Holds a list of "Map" objects for each section.  Each map holds
         * the name/value pairs for the particular section.
         */
    protected List sectionMap;

        /**
         * List of section names.
         */
    protected List nameList;

        /**
         * Tells if teh document has been modified or not.
         */
    public boolean docModified;

        /**
         * Creates a new map and returns it.
         */
    protected static Map getNewMap()
    {
        return new HashMap();
    }

        /**
         * Read the config file and be ready.
         */
    public void readConfigFile(String configFile)
        throws Exception
    {
        DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        configDoc = builder.parse(configFile);
        rootElem = configDoc.getDocumentElement();
    }

        /**
         * Adds a new attribute to a specific section.
         *
         * Attribute names ARE case sensitive.
         */
    public void addAttribute(int sectionID, String attrName, Object attrValue)
    {
        Map map = getSectionMap(sectionID);
        map.put(attrName, attrValue);
        docModified = true;
    }

        /**
         * Gets the value of an existing attribute.
         *
         * Attribute names ARE case sensitive.
         */
    public Object getAttribute(int sectionID, String attrName)
    {
        Map map = getSectionMap(sectionID);
        return map.get(attrName);
    }

        /**
         * Get the map of the given section.
         */
    public Map getSectionMap(int index)
    {
        return (Map)sectionMap.get(index);
    }

        /**
         * Get the section map of given section.
         */
    public Map getSectionMap(String name)
    {
        return getSectionMap(getSectionID(name));
    }

        /**
         * Adds a new section and returns its index within the list.
         */
    public int addSection(String name)
    {
        int index = getSectionID(name);
        if (index >= 0) return index;

        nameList.add(name);
        sectionMap.add(getNewMap());
        docModified = true;

        return nameList.size() - 1;
    }

        /**
         * Given a section name return its ID.
         * Section names are case insensitive.
         */
    public int getSectionID(String sectionName)
    {
        int i = 0;
        for (Iterator iter = nameList.iterator();iter.hasNext();)
        {
            String name = (String)iter.next();
            if (name.equalsIgnoreCase(sectionName)) return i;
        }
        return -1;
    }

        /**
         * Given a section ID, return its name.
         */
    public String getSectionName(int id)
    {
        return (String)nameList.get(id);
    }
}
