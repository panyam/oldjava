package svtool.gui.views;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;

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
 * Temp DBView object used to test the View architecture.
 */
public class TestViewB extends DBView
{

        /**
         * Called when the database is being closed.
         *
         * This function should be implemented so that when the database is
         * called, the view may change to reflect this or to clear out all
         * data that is currently displaying.
         */
    public void viewClosing()
    {
    }

        /**
         * Show an object that is the result of a search.
         */
    public void showSearchItem(Object item)
    {
    }

        /**
         * Gets a list of searched Items.
         */
    public ListIterator searchItems(String searchType,
                                    Object searchCriteria)
    {
        return null;
    }
}
