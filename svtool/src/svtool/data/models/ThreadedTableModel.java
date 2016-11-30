
package svtool.data.models;

import svtool.core.*;
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
 * Locally used super class of ThreadedTableModels.
 *
 * Basically allows a table to be loaded in background and also
 * that all the items in the table are only of the String type.
 *
 * @author Sri Panyam
 */
public abstract class ThreadedTableModel extends AbstractTableModel
    implements Runnable
{
    protected boolean threadRunning = false;
    protected boolean threadStopped = false;
    protected Thread ourThread;

        /**
         * Tells if a cell is editable or not.
         *
         * By default in this model all cells or NOT editable.
         *
         * @param   row     The row of the cell being specified.
         * @param   col     The column of the cell being specified.
         *
         * @return  Tells if the cell is editable or not.
         */
    public boolean isEditable(int row, int col)
    {
        return false;
    }

        /**
         * Notify all function.
         */
    protected void doNotify()
    {
        try
        {
            super.notifyAll();
        } catch (Exception exc)
        {
        }
    }

        /**
         * Get the class of a given column.
         */
    public Class getColumnClass(int c)
    {
        return String.class;
    }
}
