package svtool.gui.dialogs;

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
 * Dialog for finding items in the tree.
 */
public class FindItemDialog extends OkCancelDialog
                            implements
                                ListSelectionListener
{
    JComboBox getByServicesCombo = new JComboBox(ItemTypes.getByStrings);
    JComboBox getByFNNCombo = new JComboBox(ItemTypes.fnnStrings);
    JComboBox searchDirCombo = new JComboBox(new String[] { "Up", "Down" });

        /**
         * Text field that contains the value to be searched.
         */
    JTextField searchText = new JTextField("", 20);

        /**
         * Buttons to initiate the search.
         */
    JButton findButton = new JButton("Find");
    JButton findNextButton = new JButton("Find Next");

        /**
         * Constructor.
         */
    public FindItemDialog(JFrame parent)
    {
        super(parent, "Find Item...", false);

        addButton(findButton);
        addButton(findNextButton);
    }

        /**
         * Tells when a section has changed.
         */
    public void valueChanged(ListSelectionEvent e)
    {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
    }

}
