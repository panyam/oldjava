package svtool.gui.views;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.gui.*;
import svtool.gui.dialogs.*;

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
 * Represents a database view.
 *
 * This GUI panel is responsible for performing certain operations on one
 * instance of one database.  Examples of views are query windows,
 * discovery windows, analysis windows and so on.
 *
 * @author Sri Panyam
 */
public abstract class DBView extends JPanel
{
        /**
         * The mediator interface for this view.
         */
    protected AppMediator appMediator = null;

        /**
         * The icon representing this view.
         */
    protected ImageIcon viewIcon = null;

        /**
         * The name of the View.
         */
    protected String viewName = null;

        /**
         * The current database that is being worked on.
         */
    protected Database currDatabase = null;

        /**
         * Set the mediator object.
         */
    public void setMediator(AppMediator med)
    {
        appMediator = med;
    }

        /**
         * Sets the database that will be used and worked on by this view.
         *
         * @param   dBase   The database object that will be the one used
         * by the view. 
         */
    public void setDatabase(Database dBase) throws Exception
    {
        this.currDatabase = dBase;
    }

        /**
         * Get the name of the view.
         */
    public String getViewName()
    {
        return viewName;
    }

        /**
         * Set the name of the view.
         */
    public void setViewName(String vName)
    {
        this.viewName = vName;
    }

        /**
         * Return the database object associated with this view.
         *
         * @return The Database object for this view.
         */
    public Database getDatabase()
    {
        return currDatabase;
    }

        /**
         * Get the view data created and USED by this view object.
         *
         * @return  Returns the DBViewData object used.
         */
    //public abstract DBViewData getDBViewData();

        /**
         * Set the name of the view.
         */
    public void setViewIcon(ImageIcon icon)
    {
        this.viewIcon = icon;
    }

        /**
         * Get the associated icon for this view.
         *
         * @return  Returns the icon for this view.
         */
    public ImageIcon getIcon()
    {
        return viewIcon;
    }

        /**
         * Searchable Types.
         */
    protected Set searchableTypes = new TreeSet();
    protected Map searchableMaps = new HashMap();

        /**
         * Tells how many searchable types there are.
         */
    public int getSearchableTypeCount()
    {
        return searchableMaps.size();
    }

        /**
         * Get the list of searchable objects.
         *
         * Each of these objects will be in a seperate list
         * indexed by a String.  Eg a "Company" or "Service" and 
         * so on.
         */
    public java.util.List getSearchableSet(String type)
    {
        return (java.util.List)searchableMaps.get(type);
    }

        /**
         * Remove all searchable types.
         */
    public void clearSearchables()
    {
        searchableTypes.clear();
        searchableMaps.clear();
    }

        /**
         * Get the iterator of searchable types.
         *
         * So it would return things like "Company", "Service" and 
         * "VLAN FNN".  This means that a find can be done on items
         * which match certain types.
         */
    public Iterator getSearchableTypes()
    {
        return searchableTypes.iterator();
    }

        /**
         * Adds a new type of searchable object.
         */
    public synchronized void addSearchableType(String type)
    {
        if (searchableTypes.contains(type)) return ;
        searchableTypes.add(type);
    }

        /**
         * Adds a new searchable object.
         */
    public synchronized void addSearchable(String type,
                                           String searchable)
    {
        java.util.List model = 
            (java.util.List)searchableMaps.get(type);

        if (model == null)
        {
            model = new java.util.LinkedList();
            addSearchableType(type);
            searchableMaps.put(type, model);
        }

        if (model.indexOf(searchable) < 0)
            model.add(searchable);
    }

        /**
         * Called when the database is being closed.
         *
         * This function should be implemented so that when the database is
         * called, the view may change to reflect this or to clear out all
         * data that is currently displaying.
         */
    public abstract void viewClosing();

        /**
         * Show an object that is the result of a search.
         */
    public abstract void showSearchItem(Object item);

        /**
         * Gets a list of searched Items.
         */
    public abstract ListIterator searchItems(String searchType,
                                             Object searchCriteria);
}
