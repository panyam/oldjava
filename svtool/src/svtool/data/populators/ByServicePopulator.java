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
 * Populates the document by ServiceIDs.
 */
public class ByServicePopulator extends ServiceDataPopulator
{
    String services[] = null;
    StringBuffer servListBuffer = new StringBuffer();
    int nServices = 0;

        /**
         * Sets the services that are to be extracted.
         */
    public void setServices(String servList)
    {
        setItemList(servList);
    }

        /**
         * Set the list of items to be extracted.
         */
    public void setItemList(String servList)
    {
        StringTokenizer tokens =
            new StringTokenizer(servList," \t,", false);
        nServices = tokens.countTokens();
        services = new String[1 + nServices];
        servListBuffer.delete(0, servListBuffer.length());

        int i = 0;
        while (tokens.hasMoreTokens())
        {
            services[i] = tokens.nextToken();
                // also check that services[i] has only digits as
                // this is a prerequisite of the service!!
            int l = services[i].length();
            for (int c = 0;c < l;c++) 
            {
                char ch = services[i].charAt(c);
                if (ch < '0' || ch > '9') 
                {
                    throw new IllegalArgumentException(
                            "Invalid Number: " + services[i]  + ".  " +
                            "Services must be integer values");
                }
            }
            if (i > 0) servListBuffer.append(", ");
            servListBuffer.append(services[i]);
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

            // first make sure appropriate service and customer
            // holder objects are available and create them if
            // they are missing...

            // make the list of serviceIDs seperated by comas

        Document xmlDoc = serviceData.createNewXMLDocument();

        serviceData.clearAll();

        CustomerInfo currCust = null;

        Connection currConnection = currDB.getConnection();
        Statement stmt = currConnection.createStatement();
        ResultSet rset = stmt.executeQuery(
                "select c.CUSTOMER_ID, c.CUSTOMER_NAME, s.SERVICE_ID" + 
                "   from CSOM_ADMIN.service s, CSOM_ADMIN.customer c " + 
	            "   where " +
                "     s.SERVICE_ID in (" + servListBuffer.toString() + ")" +
                "   and " + 
                "   s.CUSTOMER_ID = c.CUSTOMER_ID " +
                "   order by CUSTOMER_ID ");
        setTripletTable(rset, xmlDoc);
        stmt.close();
        populationCompleted = true;
    }
}
