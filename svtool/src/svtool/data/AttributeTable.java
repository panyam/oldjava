package svtool.data;

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

public class AttributeTable
{
    String values[][];
    int primaryKeyColumn = 0;

    public AttributeTable(int nRows, int nColumns, int primaryKeyColumn)
    {
        values = new String[nColumns][nRows];
        this.primaryKeyColumn = primaryKeyColumn;
    }

        /**
         * Tells how many items are in this table.
         */
    public int size()
    {
        if (values == null || values[0] == null) return 0;
        return values[0].length;
    }

        /**
         * Sets the values.
         */
    public void setValues(String []vals)
    {
        int i, s = values[primaryKeyColumn].length;
        for (i = 0;i < s;i++)
        {
            if (values[primaryKeyColumn][i] == null ||
                values[primaryKeyColumn][i].
                    equalsIgnoreCase(vals[primaryKeyColumn]))
            {
                for (int j = 0;j < vals.length;j++)
                {
                    values[j][i] = vals[j];
                }
                return ;
            }
        }
    }

        /**
         * Return a proper attribute.
         */
    public String getAttribute(int row, int column)
    {
        return values[column][row];
    }
}
