package svtool.data;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 
import javax.swing.*;

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
 * Interface for all classes that need hold data regarding a particular
 * view.
 *
 * Inherently, all data is the databse itself.  However, each view
 * (DatabaseView class) will represent the Data in the DB differnetly.
 * This would mean that there wouldnt be a "Single" document representing
 * all the data in the application.  So different views will require
 * different "kinds" of data, each of which is represented by this
 * interface/class.
 *
 * @author Sri Panyam
 */
public class DBViewData
{
        /**
         * List of registered listeners.
         */
    protected List dbDataListeners = new LinkedList();

        /**
         * Adds a new listener for this kind of data.
         *
         * @param   dl  The DBDataListener which is to be added.
         */
    public void addDBDataListener(DBDataListener dl)
    {
        if (dbDataListeners.contains(dl)) return ;
        dbDataListeners.add(dl);
    }

        /**
         * Removes a new listener for this kind of data.
         *
         * @param   dl  The DBDataListener which is to be removed.
         */
    public void removeDBDataListener(DBDataListener dl)
    {
        if ( ! dbDataListeners.contains(dl)) return ;
        dbDataListeners.remove(dl);
    }

        /**
         * Send a data event to all registered listeners.
         *
         * @param   dataEvent   The event to listen to.
         */
    public void fireDBDataEvent(Object dataEvent)
    {
        for (Iterator iter = dbDataListeners.iterator();iter.hasNext();)
        {
            DBDataListener dataListener = (DBDataListener)iter.next();
            dataListener.dbDataChanged(dataEvent);
        }
    }
}
