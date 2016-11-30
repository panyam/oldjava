package svtool.data.populators;

import svtool.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.data.*;
import svtool.data.models.*;

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
 * Populates the document by Customer names
 */
public class ByCustomerPopulator extends ServiceDataPopulator
{
    String customers[] = null;
    int nCustomers = 0;
    StringBuffer custListBuffer = new StringBuffer();

        /**
         * Sets the customers that are to be extracted.
         */
    public void setCustomers(String custList)
    {
        setItemList(custList);
    }

    public void setItemList(String custList)
    {
        StringTokenizer tokens =
            new StringTokenizer(custList,",", false);
        nCustomers = tokens.countTokens();
        customers = new String[1 + nCustomers];
        custListBuffer.delete(0, custListBuffer.length());

        int i = 0;
        while (tokens.hasMoreTokens())
        {
            if (i > 0) custListBuffer.append(" or ");
            customers[i] = tokens.nextToken().toLowerCase();
            custListBuffer.append(
                  "lower(c.customer_name) like ('" + customers[i] + "')");
            i++;
        }
    }

        /**
         * Now that we know what services there are, 
         * first get all the custoemrs that own each
         * of these services.
         *
         * Then extract the services...
         */
    public void populateDocument()
        throws Exception
    {
        //serviceData.clearSearchables();

        tripletCount = -1;
        populationCompleted = false;
            // first make sure appropriate service and customer
            // holder objects are available and create them if
            // they are missing...
        boolean custExists = false;
        boolean serviceExists = false;

            // make the list of serviceIDs seperated by comas
        StringBuffer custList = new StringBuffer();

        Document xmlDoc = serviceData.createNewXMLDocument();

        serviceData.removeAllServices();
        serviceData.removeAllCustomers();

            // first get all the services that 
            // belong to this customer...
            // query to get all services...
            //  select c.customer_id, c.customer_name, s.service_id
            //      from service s, customer c
            //      where
            //          (lower(c.customer_name) like ('%heat%')
            //          or lower(c.customer_name) like '%holic%')
            //      and s.customer_id = c.customer_id
            //      order by customer_id
            //
            // This would return a table with 3 columns

        CustomerInfo currCust = null;

        Connection currConnection = currDB.getConnection();
        Statement stmt = currConnection.createStatement();
        ResultSet rset = stmt.executeQuery(
                    "select c.customer_id, c.customer_name, s.service_id" + 
                    "   from CSOM_ADMIN.service s, CSOM_ADMIN.customer c " +
                    "   where (" + custListBuffer.toString() + ")" + 
                    "   and s.customer_id = c.customer_id " + 
                    "   order by c.customer_id"
                    );

            // count how many items are there in the rset!!!
        setTripletTable(rset, xmlDoc);

        stmt.close();
        populationCompleted = true;
        //currentTriplet = tripletCount = 0;
    }
}
