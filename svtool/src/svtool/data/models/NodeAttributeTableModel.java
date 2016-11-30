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

public class NodeAttributeTableModel extends AbstractTableModel
{
        /**
         * The node that is currently selected.
         */
    ElementTreeNode currNode;
    AttributeTable attribTable;

        /**
         * Sets the current node.
         */
    public void setNode(ElementTreeNode node)
    {
        currNode = node;
        if (node != null) attribTable = node.getAttributeTable();
        fireTableStructureChanged();
    }

        /**
         * Get the class of a given column.
         */
    public Class getColumnClass(int c)
    {
        return String.class;
    }

        /**
         * Get the name of a given column.
         */
    public String getColumnName(int col)
    {
        if (col == ElementTreeNode.ATTRIB_NAME)
        {
            return "Attribute Name";
        }
        return "Attribte Value";
    }

        /**
         * Returns the number of rows. 
         */
    public int getRowCount()
    {
        if (currNode == null || attribTable == null) return 0;
        return attribTable.size();
    }

        /**
         * Gets the value at a givenlocation.
         */
    public Object getValueAt(int row, int col)
    {
        if (currNode == null) return null;
        return attribTable.getAttribute(row, col);
    }

        /**
         * Returns the number of columns 
         */
    public int getColumnCount()
    {
        return 2;
    }
}
