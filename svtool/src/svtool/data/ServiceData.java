
package svtool.data;

import svtool.core.*;

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
 * Holds information regarding Services and FNNs and so on.
 *
 * This object will be used mainly by the ServiceView object for
 * representing services related information.
 *
 * @author Sri Panyam.
 */
public class ServiceData extends DBViewData
{
    protected Document xmlDoc = null;
    protected Element xmlDocRoot;

        /**
         * These are the customers whose VLANs are currently being
         * displayed.
         * A customer can have multiple services_ids.  So the idea is
         * that we need to display the company, serviceID and the FNN.  A
         * tree can only be used for the service tree.
         */
    protected Map customers = new HashMap();

        /**
         * All the roots of the services elements.
         * Basically a serviceID can have mulitple FNNs - Port, VLAN or VPN
         */
    protected Map serviceElements = new HashMap();

        /**
         * List of all the FNNs that are currently being
         * displayed.
         * FNNs are unique.  there can be many FNNs per service ID,
         * hwoerver one FNN can only belong to one service id.  So in order
         * to be smart about the display we cant replicate teh serviceID
         * tree node for each FNN.  So a 2D structure is required for this.
         */
    protected Map fnnSet = new HashMap();

        /**
         * Holds structure codes for the service.
         */
    protected HashMap structureCodeSet = new HashMap();

        /**
         * The first hash table tells what the name of a given attribute
         * code is.
         */
    protected HashMap attribCodeMap = new HashMap();

        /**
         * Constructor.
         */
    public ServiceData()
    {
    }

        /**
         * Adds a new customer.
         */
    public void addCustomer(String custID, CustomerInfo cust)
    {
        customers.put(custID, cust);
    }

        /**
         * Returns the customer count.
         */
    public int getCustomerCount()
    {
        return customers.size();
    }

        /**
         * Gets a Customer by its ID.
         */
    public CustomerInfo getCustomer(String custID)
    {
        return (CustomerInfo)customers.get(custID);
    }

        /**
         * Add a new FNN item to the list.
         */
    public void addFNNInfo(FNNInfo fnn)
    {
        fnnSet.put(fnn.fnnID, fnn);
    }

        /**
         * The service iterator.
         */
    public Iterator getServiceIterator()
    {
        return serviceElements.values().iterator();
    }

        /**
         * Get the list of FNNs.
         */
    public Iterator getFNNIterator()
    {
        return fnnSet.values().iterator();
    }

        /**
         * Get the customer iterator.
         */
    public Iterator getCustomerIterator()
    {
        return customers.values().iterator();
    }

        /**
         * Clears out the document.
         */
    public void clearAll()
    {
        removeAllCustomers();
        removeAllServices();
    }

        /**
         * REmove all customers.
         */
    public void removeAllCustomers()
    {
        customers.clear();
    }

        /**
         * Remove a Customer by its ID.
         */
    public void removeCustomer(String custID)
    {
        customers.remove(custID);
    }

        /**
         * Adds a new service.
         */
    public void addService(String serviceID, ServiceInfo service)
    {
        serviceElements.put(serviceID, service);
    }

        /**
         * Returns the service count.
         */
    public int getServiceCount()
    {
        return serviceElements.size();
    }

        /**
         * Gets a service by its ID.
         */
    public ServiceInfo getService(String serviceID)
    {
        return (ServiceInfo)serviceElements.get(serviceID);
    }

        /**
         * Remove a service by its ID.
         */
    public void removeService(String serviceID)
    {
        serviceElements.remove(serviceID);
    }

        /**
         * Remove all services.
         */
    public void removeAllServices()
    {
        serviceElements.clear();
    }

        /**
         * Clears the set of structure codes.
         */
    public void clearStructures()
    {
        structureCodeSet.clear();
    }

        /**
         * Adds a new structure along with its ID.
         *
         * @param   structID    The ID of the structure being added.
         *          structName  Name of the structure being added.
         */
    public void addStructure(String structID, String structName)
    {
        if ( ! structureCodeSet.containsKey(structID))
        {
            structureCodeSet.put(structID, structName);
        }
    }

        /**
         * Adds a new Attribute along with its ID.
         *
         * @param   attrID      The ID of the Attribute being added.
         *          attrName    Name of the Attribute being added.
         */
    public void addAttribute(String attrID, String attrName)
    {
        if ( ! attribCodeMap.containsKey(attrID))
        {
            attribCodeMap.put(attrID, attrName);
        }
    }

        /**
         * Clears the set/list of attribute codes.
         */
    public void clearAttributes()
    {
        attribCodeMap.clear();
    }

        /**
         * Gets the name of an attribute given its code.
         */
    public String getAttributeName(String attribCode)
    {
        return (String)attribCodeMap.get(attribCode);
    }

    public String getStructureName(String structureID)
    {
        return (String)structureCodeSet.get(structureID);
    }

        /**
         * Create a new XML document.
         */
    public Document createNewXMLDocument()
    {
            // create the root document...
        xmlDoc = new DocumentImpl();
        xmlDocRoot =
            xmlDoc.createElement(XMLUtils.SERVICES_ROOT_NAME);
        xmlDoc.appendChild(xmlDocRoot);
        return xmlDoc;
    }
}
