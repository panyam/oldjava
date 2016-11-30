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


public class ServiceInfo
{
        /**
         * Service ID.
         */
    public String serviceID;

        /**
         * The customer to which this service belongs to.
         */
    public CustomerInfo parentCust;

        /**
         * The associated XML Element.
         */
    public Element treeNode;

        /**
         * Default service.
         */
    public ServiceInfo()
    {
        serviceID = null;
        parentCust = null;
    }

        /**
         * Tells if this equals another object.
         */
    public boolean equals(Object another)
    {
        return ((another instanceof ServiceInfo) &&
                (((ServiceInfo)another).serviceID.equals(serviceID)));
    }
}
