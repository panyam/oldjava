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

    /**
     * The tree model that takes care of the tree data representation
     * of our service element tree.
     */
public class LogicalAttributeTableModel extends NodeAttributeTableModel
{
        /**
         * Sets the current node.
         */
    public void setNode(ElementTreeNode node)
    {
        currNode = node;
        if (node != null) attribTable = node.getLogicalAttributeTable();
        fireTableStructureChanged();
    }
        /**
         * Get the name of a given column.
         */
    public String getColumnName(int col)
    {
        if (col == ElementTreeNode.ATTRIB_CODE)
        {
            return "Attribute Code";
        }
        return super.getColumnName(col);
    }

        /**
         * Returns the number of columns 
         */
    public int getColumnCount()
    {
        return 3;
    }
}
