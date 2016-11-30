
package svtool.gui;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.gui.dialogs.*;
import svtool.gui.views.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.beans.*;
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
 * Super class of all search panels.
 */
public class SearchPanel extends JPanel
                         implements ItemListener, ActionListener
{
    protected final static String CUSTOM_SEARCH_STRING = "Custom Search";
    protected boolean initialising = false;

    protected JComboBox searchTypeCombo = new JComboBox();
    protected JComboBox quickSearchItems = new JComboBox();
    protected java.util.List quickSearchList = new ArrayList();
    protected JTextField searchText =
            new JTextField("Type your search text here.");

    protected JButton nextButton = new JButton("Next >");
    protected JButton previousButton = new JButton("< Previous");
    protected JPanel navBtnPanel = new JPanel();
    protected JPanel centerPanel = new JPanel(new BorderLayout());

        /**
         * The iterator for searched objects.
         */
    protected ListIterator searchIterator = null;

        /**
         * Current dbview being searched.
         */
    protected DBView dbView = null;

        /**
         * Type of search being done.
         */
    protected String searchType = null;

        /**
         * The value beign searched for.
         */
    protected String searchValue = "";

        /**
         * Constructor.
         */
    public SearchPanel()
    {
        navBtnPanel.add(previousButton);
        navBtnPanel.add(nextButton);

        searchTypeCombo.addItemListener(this);
        quickSearchItems.addItemListener(this);

        centerPanel.add("West", searchTypeCombo);
        centerPanel.add("Center", searchText);

        nextButton.addActionListener(this);
        previousButton.addActionListener(this);

        add("North", centerPanel);
        add("South", navBtnPanel);
    }

        /**
         * Gets the current view being searched.
         */
    public DBView getView()
    {
        return dbView;
    }

        /**
         * INitialise the panel with this view object.
         */
    public void initSearch(DBView view)
    {
        initialising = true;
        this.dbView = view;
        int srchCount = view.getSearchableTypeCount();
        searchTypeCombo.removeAllItems();
        Iterator srchIter = view.getSearchableTypes();

        while (srchIter.hasNext())
        {
            String srchType = (String)srchIter.next();

            java.util.List srchList = view.getSearchableSet(srchType);
            DefaultComboBoxModel cmbModel =
                    new DefaultComboBoxModel(srchList.toArray());
            searchTypeCombo.addItem(srchType);
            quickSearchList.add(cmbModel);
        }

        searchTypeCombo.addItem(CUSTOM_SEARCH_STRING);
        //quickSearchList.add(null);

        initialising = false;
        selectSearchType(0);
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();

        if (dbView == null) return ;

        if (((String)searchTypeCombo.getSelectedItem()).
                                    equals(CUSTOM_SEARCH_STRING))
        {
            searchValue = searchText.getText();
        } else
        {
            searchValue = (String)quickSearchItems.getSelectedItem();
        }

        if (searchIterator == null) return ;

        if (src == nextButton)
        {
            if (searchIterator.hasNext())
            {
                dbView.showSearchItem(searchIterator.next());
            }
        } else if (src == previousButton)
        {
            if (searchIterator.hasPrevious())
            {
                dbView.showSearchItem(searchIterator.previous());
            }
        }
        nextButton.setEnabled(searchIterator.hasNext());
        previousButton.setEnabled(searchIterator.hasPrevious());
    }

        /**
         * Item event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        Object src = ie.getSource();
        if (src == searchTypeCombo)
        {
            selectSearchType(searchTypeCombo.getSelectedIndex());
            searchType = (String)searchTypeCombo.getSelectedItem();
            searchValue = (String)quickSearchItems.getSelectedItem();
            searchIterator = dbView.searchItems(searchType, searchValue);
        } else if (src == quickSearchItems)
        {
            searchType = (String)searchTypeCombo.getSelectedItem();
            searchValue = (String)quickSearchItems.getSelectedItem();

            searchIterator = dbView.searchItems(searchType, searchValue);
        }
    }

        /**
         * Select the search type.
         */
    public void selectSearchType(int index)
    {
        if (index < 0 || index >= quickSearchList.size()) return ;
        if (initialising) return ;

        searchType = (String)searchTypeCombo.getItemAt(index);
        DefaultComboBoxModel cmbModel = 
                (DefaultComboBoxModel)quickSearchList.get(index);

        if (searchType.equals(CUSTOM_SEARCH_STRING))
        {
            centerPanel.remove(quickSearchItems);
            centerPanel.add("Center", searchText);
        } else
        {
            quickSearchItems.setModel(cmbModel);
            centerPanel.remove(searchText);
            centerPanel.add("Center", quickSearchItems);
        }
    }
}
