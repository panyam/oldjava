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

/**
 * Holds info about a customer.
 */
public class CustomerInfo
{
        /**
         * Customer ID.
         */
    public String custID = null;

        /**
         * Name of the customer.
         */
    public String custName = null;

        /**
         * List of services this customer has.
         */
    public Set services = null;

        /**
         * Adds a new service info.
         */
    public void addService(ServiceInfo sinfo)
    {
        services.add(sinfo);
    }

        /**
         * Default constructor.
         */
    public CustomerInfo()
    {
        services = new HashSet();
        custName = null;
        custID = null;
    }

        /**
         * Tells if this equals another object.
         */
    public boolean equals(Object another)
    {
        return ((another instanceof CustomerInfo) &&
                (((CustomerInfo)another).custID.equals(custID)));
    }

        /**
         * Get the service interator.
         */
    public Iterator getServiceIterator()
    {
        return services.iterator();
    }
}
