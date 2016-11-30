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
 * Dialog for handling the connections.
 */
public class ConnectDialog extends OkCancelDialog
                           implements
                                ListSelectionListener
{
    JButton addButton = new JButton("Add");
    JButton removeButton = new JButton("Remove");
    int selectedRow = -1;
    String selectedDBName = "";
    String selectedLoginName = "";
    String selectedPassword = "";

    static String columnNames[] =  new String []
    {
        "Database / Server", "Username / Schema", "Password"
    };

    JPasswordField passwordField =new JPasswordField("", 20);
    class PasswordCellRenderer implements TableCellRenderer
    {
        public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column)
        {
            passwordField.setText(value.toString());
            if (isSelected) passwordField.transferFocus();
            return passwordField;
        }
    }

    DefaultTableModel connModel = new DefaultTableModel();
    JTable connTable = new JTable(connModel);

        /**
         * Connect to a database.
         */
    public ConnectDialog(JFrame parent)
    {
        super(parent, "Open connection", true);

        JScrollPane connScroller = new JScrollPane(connTable);
        getContentPane().add("Center", connScroller);

        addButton(addButton);
        addButton(removeButton);


        for (int i = 0;i < columnNames.length;i++)
        {
            connModel.addColumn(columnNames[i]);
        }
        connModel.addTableModelListener(connTable);
        connTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = connTable.getSelectionModel();
        rowSM.addListSelectionListener(this);

        TableColumn passwordColumn = connTable.getColumnModel().getColumn(2);
        passwordColumn.setCellEditor(
                new DefaultCellEditor(new JPasswordField()));
        passwordColumn.setCellRenderer(new PasswordCellRenderer());

        setVisible(false);
        setResizable(true);
        setLocationRelativeTo(parent);
        pack();
    }

        /**
         * Tells when a section has changed.
         */
    public void valueChanged(ListSelectionEvent e)
    {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        selectRow(lsm.getMinSelectionIndex());
    }

        /**
         * Select a given row./
         */
    public void selectRow(int row)
    {
        selectedRow = row;

        int col = connTable.getSelectedColumn();
        okButton.setEnabled(selectedRow >= 0);

        if (row >= 0)
        {
            connTable.changeSelection(selectedRow, col, false, false);
            selectedDBName =
                ((String)connModel.getValueAt(selectedRow, 0)).trim();
            selectedLoginName =
                ((String)connModel.getValueAt(selectedRow, 1)).trim();
            selectedPassword =
                ((String)connModel.getValueAt(selectedRow, 2)).trim();
        } else
        {
            selectedDBName = "";
            selectedLoginName = "";
            selectedPassword = "";
            connTable.clearSelection();
        }
    }

        /**
         * Action performed event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src == addButton)
        {
            // add a new row
            connModel.addRow(new String[] { "server_name", "userid", "password" });
            selectRow(connModel.getRowCount() - 1);
        } else if (src == removeButton)
        {
                // remove the current row...
            int selRow = connTable.getSelectedRow();
            int nRows = connModel.getRowCount();
            if (selRow < 0 || selRow >= nRows) 
            {
                selectRow(selRow < 0 ? 0 : nRows - 1);
                return ;
            }
            connModel.removeRow(selRow);
            nRows = connModel.getRowCount();
            selectRow(selRow >= nRows ? nRows - 1 : selRow);
        }
        else super.actionPerformed(e);
    }

        /**
         * Gets the name of the database.
         */
    public String getDatabaseName()
    {
        if (selectedRow < 0) return null;
        return selectedDBName;
    }

        /**
         * Gets the login name.
         */
    public String getLogin()
    {
        if (selectedRow < 0) return null;
        return selectedLoginName;
    }

        /**
         * Gets the password.
         */
    public String getPassword()
    {
        if (selectedRow < 0) return null;
        return selectedPassword;
    }

        /**
         * Read the configurations.
         */
    public void readConfigurations(Document  configDoc)
    {
        if (configDoc == null) return ;

        Element docElem = configDoc.getDocumentElement();

            // remove all rows...
            // should this be done???
        for (int s = connModel.getRowCount() - 1, i = s;i >= 0;i--)
        {
            connModel.removeRow(i);
        }


            // get all "query" elements
        NodeList connNodeList =
            docElem.getElementsByTagName(XMLUtils.CONNECTIONS_ROOT_NAME);
        if (connNodeList == null || connNodeList.getLength() <= 0) return ;

        Element connNode = (Element)connNodeList.item(0);
        NodeList conns = connNode.getElementsByTagName(XMLUtils.CONNECTION_ROOT_NAME);

        if (conns == null || conns.getLength() <= 0) return ;
        int nConns = conns.getLength();

        for (int i = 0;i < nConns;i++)
        {
            Element conn = (Element)conns.item(i);

                // basically read the info here.
            String dbase = conn.getAttribute(XMLUtils.SID_ATTRIB);
            String user = conn.getAttribute(XMLUtils.USERID_ATTRIB);
            String passwd = conn.getAttribute(XMLUtils.PASSWORD_ATTRIB);
            connModel.addRow(new String[]{ dbase, user, passwd });
        }

        selectRow(0);
    }

        /**
         * Creates an element for the configurations.
         */
    public Element getConfigurations(Document doc)
    {
        Element connsElem = doc.createElement(XMLUtils.CONNECTIONS_ROOT_NAME);

        int nConns = connModel.getRowCount();

        for (int i = 0;i < nConns;i++)
        {
            Element conn = doc.createElement(XMLUtils.CONNECTION_ROOT_NAME);
            conn.setAttribute(XMLUtils.SID_ATTRIB,
                              (String)connModel.getValueAt(i, 0));
            conn.setAttribute(XMLUtils.USERID_ATTRIB,
                              (String)connModel.getValueAt(i, 1));
            conn.setAttribute(XMLUtils.PASSWORD_ATTRIB,
                              (String)connModel.getValueAt(i, 2));
            connsElem.appendChild(conn);
        }

        return connsElem;
    }
}
