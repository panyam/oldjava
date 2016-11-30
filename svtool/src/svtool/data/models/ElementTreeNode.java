package svtool.data.models;

import svtool.data.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 

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

public class ElementTreeNode extends DefaultMutableTreeNode
{
        // These are the attributes of "physical" node
    public final static int ATTRIB_NAME = 0;
    public final static int ATTRIB_VALUE = 1;
    public final static int ATTRIB_CODE = 2;

    AttributeTable attribTable[] = new AttributeTable[2];

        // These are the attributes of "logical" node

        /**
         * Constructor.
         */
    public ElementTreeNode(String label, int nAttributes)
    {
        super(label);

        attribTable[0] = new AttributeTable(nAttributes, 2, ATTRIB_NAME);
    }

        /**
         * Create the logical attributes of a given node.
         */
    public void createLogicalAttributes(int nRows)
    {
        attribTable[1] = new AttributeTable(nRows, 3, ATTRIB_CODE);
    }

        /**
         * Return the node attributes.
         */
    public AttributeTable getAttributeTable()
    {
        return attribTable[0];
    }

        /**
         * Gets the first attribute index whose attribute value
         * matches the given value.  The match is done only
         * onthe specific table.
         */
    public int getAttributeID(String attributeValue, int whichTable)
    {
        AttributeTable table = attribTable[whichTable];
        if (table == null) return -1;
        int nRows = table.size();

        for (int row = 0;row < nRows;row++)
        {
            String attrib = table.getAttribute(row, ATTRIB_VALUE);
            if (attrib == null) return -1;
            if (attrib.equals(attributeValue))
            {
                return row;
            }
        }
        return -1;
    }

        /**
         * Return the node attributes.
         */
    public AttributeTable getLogicalAttributeTable()
    {
        return attribTable[1];
    }

        /**
         * Sets attribute.
         */
    public void setAttribute(String attrib, String value)
    {
        if (attribTable[0] != null)
        {
            attribTable[0].setValues(new String[]{ attrib, value });
        }
    }

        /**
         * Sets the logical attribute.
         */
    public void setAttribute(String code, String attrib, String value)
    {
        if (attribTable[1] != null)
        {
            attribTable[1].setValues(new String[]{ attrib, value, code});
        }
    }
}
