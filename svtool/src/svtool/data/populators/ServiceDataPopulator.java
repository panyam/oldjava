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
 * This the object that modifies the document by populating it
 * or erasing stuff from it.
 */
public abstract class ServiceDataPopulator implements Task
{
        /**
         * Tells if the population was completed or not.
         */
    protected boolean populationCompleted = true;
    protected boolean taskStopRequested = false;
    protected boolean taskStopped = false;

        /**
         * The current service data object being populated.
         */
    protected ServiceData serviceData = null;

        /**
         * The current database.
         */
    protected Database currDB = null;

        /**
         * Count of the triplets.
         */
    protected int tripletCount = 0;

        /**
         * Indicates the current triplet.
         */
    protected int currentTriplet = 0;

    protected String currCustID = "";
    protected String currCustName = "";
    protected String currServiceID = "";

        /**
         * The main central document that is to be drawn.
         */
    protected ServiceData sData = null;

        /**
         * The execution result.
         */
    protected Object execResult = null;

        /**
         * instead of doing
         * select * from service_data where service_id = xx and
         * structinstid = yyy
         *
         * we can avoid the structinstid = yyy condition and get "ALL"
         * attributes for the serviceID.  This is only one query.  So if
         * this was stored originally in a table, then this table can be
         * checked to see which entries have the structInstID and only
         * those fields could be extracted.
         *
         * So this table given an "instantID", stores all the attributes
         * associated with it.
         *
         * This means only one query for each serviceID.  But what is the
         * cost of this computation itself?  Is this any better?  A quick
         * way is needed.  So a sorted list or array is the best way to do
         * this.  Each node would have (instID, attribCode, attribValue).
         *
         * The query can be ordered by structInstID.  So the insertion
         * appears is constant time and retrieval can be linear at worst
         * case.
         */
    protected svtool.core.adt.LinkedList structureAttributeList =
        new svtool.core.adt.LinkedList();

    public void setDocument(ServiceData sData)
    {
        serviceData = sData;
    }

    public void setDatabase(Database db)
    {
        currDB = db;
    }

        /**
         * Sets the list of items to be used as parameters.
         */
    public abstract void setItemList(String list);

        /**
         * Starts the document population.
         */
    public abstract void populateDocument() throws Exception;

        /**
         * Stop the task.
         */
    public synchronized boolean stopTask()
    {
        taskStopRequested = true;

        while (taskStopRequested ||  ! populationCompleted)
        {
            try
            {
                wait(200);
            } catch (Exception exc)
            {
            }
        }
        taskStopRequested = false;
        taskStopped = true;

            // also clear out the document
        //serviceData.clearSearchables();
        serviceData.removeAllServices();
        serviceData.removeAllCustomers();

        return true;
    }

        /**
         * Start the task.
         */
    public synchronized void startTask()
    {
        final SwingWorker worker = new SwingWorker()
        {
            public Object construct()
            {
                taskStopRequested = false;
                populationCompleted = false;
                taskStopped = false;
                currentTriplet = 0;
                tripletCount = -1;
                execResult = null;
                try
                {
                    populateDocument();
                } catch (Exception exc)
                {
                    execResult = exc;
                }
                populationCompleted = true;
                return execResult;
            }
        };
        worker.start();
    }

        /**
         * Returns the result.
         */
    public Object getResult()
    {
        return execResult;
    }

        /**
         * Get the whole service tree associated with a serviceID.
         * THis will return an ElementTreePanel object beginning
         * with the service ID.
         */
    public Element extractService(String serviceID, Document xmlDoc)
        throws Exception
    {
        Element servElement = xmlDoc.createElement(XMLUtils.SERVICE_ROOT_NAME);
        servElement.setAttribute(XMLUtils.ID_ATTRIB, serviceID);

            // now go through this result set and extract the
            // struct_instant_id and the structure_id and make the tree!!
            // the tree will be based on the WIP or TE service tree "template"

            // best way is to have an "element" for each one...
        HashMap elemTable = getStructInstanceMap(serviceID, xmlDoc);

            // PASS2:
            // now that the elements are in the hash table,
            // go through each one and set the parents of each one.
            // finally the one without a root will be the one that is 
            // the child for the "ServElement"

        for (Iterator elems = elemTable.values().iterator();
                                    elems.hasNext();)
        {
            Element elem = (Element)elems.next();

                // get the current element's parent ID
            String parentID = elem.getAttribute("PARENT_STRUCT_INST_ID");
                // find the element with this parent ID
            Element parentElem = (Element)elemTable.get(parentID);

            if (parentElem != null) parentElem.appendChild(elem);
        }

            // PASS3:
            // All elements in the hash table with a parent are
            // appended to servElement!!!
        for (Iterator elems = elemTable.values().iterator(); elems.hasNext();)
        {
            Element elem = (Element)elems.next();
                // also remove the "PARENT_STRUCT_INST_ID" attribute as it
                // is no longer useful and can be "deduced"
                // and also the "ORDER_ID"
            //elem.removeAttribute("PARENT_STRUCT_INST_ID");
            //elem.removeAttribute("ORDER_ID");
            if (elem.getParentNode() == null)
            {
                servElement.appendChild(elem);
            }
        }

            // clear the hashtable
        elemTable.clear();

        return servElement;
    }

        /**
         * Gets all the service ID and creates "Element" entries
         * and returns a HashMap with it.
         * This table will then be processed to link all the elements as
         * they go.
         *
         * The precondition is that the database's refresh() method has
         * been called!!!
         */
    protected HashMap getStructInstanceMap(String serviceID,
                                                 Document xmlDoc)
        throws Exception
    {
            // now do another query from the service structure which will
            // help with the tree creation...
        if (currDB == null || currDB.getConnection() == null) return null;

        Connection currConnection = currDB.getConnection();

        Statement stmt = currConnection.createStatement();
        ResultSet rset = stmt.executeQuery(
                    "select * from CSOM_ADMIN.service_structure " +
                    "where service_id = " + serviceID +
                    " and status <> 16 " +  // or should this be == 1??
                    " order by struct_instant_id");

        int nPopulateQueries = 0;

        populateStructureAttributeTable(serviceID);

        HashMap instanceMap = new HashMap();
        while (rset.next())
        {
            String structureID = rset.getString("STRUCTURE_ID");
            String parentID = rset.getString("PARENT_STRUCT_INST_ID");
            String structInstantID = rset.getString("STRUCT_INSTANT_ID");
            String orderID = rset.getString("ORDER_ID");
            String status = rset.getString("status");

            /*System.out.println("-----------" + structureID + ", " +
                                               parentID + ", " +
                                               structInstantID);*/

                // first check if this element exists::
                // items are hashed by the instant IDs.
            Element structElem = (Element)instanceMap.get(structInstantID);

                // if no element exists then fine just put it as it is.
            if (structElem == null)
            {
                    // now create a new element for this and put this into the
                structElem = xmlDoc.createElement("struct");
                structElem.setAttribute("STRUCTURE_ID", structureID);
                structElem.setAttribute("STRUCT_INSTANT_ID", structInstantID);
                structElem.setAttribute("PARENT_STRUCT_INST_ID", parentID);
                structElem.setAttribute("ORDER_ID", orderID);
                structElem.setAttribute("STATUS", status);
                populateStructElementWithAttributes(serviceID,
                                                    structureID,
                                                    structInstantID,
                                                    structElem,
                                                    xmlDoc);
                nPopulateQueries ++;
                instanceMap.put(structInstantID, structElem);
            } else
            {
                // Now if there are two elements with the same instant id
                // but different structure IDs then it is a criticla fault
                // and MUST be reported.
                String currStructID = structElem.getAttribute("STRUCTURE_ID");

                if ( ! currStructID.equalsIgnoreCase(structureID))
                {
                    throw new Exception("Serios Error: " +
                                        "Inconsistent Structure_ID: (" +
                                        currStructID + ", " + structureID +
                                        ") for InstantID: " + structInstantID);
                } else
                {
                    // otherwise if they are equal then check if parents
                    // are equal
                    String currParentID = structElem.getAttribute(
                                                "PARENT_STRUCT_INST_ID");

                    if (currParentID.equalsIgnoreCase(parentID))
                    {
                        // if parent ids are equal then choose the "new"
                        // order number - but this may be incorrect.
                        // only the "good" order ID must be chosen...
                        // but for now only the "older" one is taken...
                        int currOrderID = Integer.parseInt(structElem.getAttribute("ORDER_ID"));
                        int thisOrderID = Integer.parseInt(orderID);
                        if (thisOrderID > currOrderID)
                        {
                            structElem.setAttribute("ORDER_ID", thisOrderID + "");
                        }
                    } else
                    {
                            // if two instances with same structure id have
                            // different parents then still add it in 
                            // but later on these will be added to
                            // different parent nodes!!!
                        structElem = xmlDoc.createElement("struct");
                        structElem.setAttribute("STRUCTURE_ID", structureID);
                        structElem.setAttribute("STRUCT_INSTANT_ID",
                                                structInstantID);
                        structElem.setAttribute("PARENT_STRUCT_INST_ID",
                                                parentID);
                        structElem.setAttribute("ORDER_ID", orderID);
                        structElem.setAttribute("STATUS", status);
                        populateStructElementWithAttributes(serviceID,
                                                            structureID,
                                                            structInstantID,
                                                            structElem,
                                                            xmlDoc);
                        nPopulateQueries ++;
                        instanceMap.put(structInstantID, structElem);
                    }
                }
            }
        }
        System.out.println("  ----  Num Attrib Queries Required: " + nPopulateQueries);

        stmt.close();
        return instanceMap;
    }

    protected void populateStructureAttributeTable(String serviceID)
        throws SQLException
    {
        if (currDB == null || currDB.getConnection() == null) return ;
        Connection currConnection = currDB.getConnection();

        String attribQuery =    "select * from CSOM_ADMIN.service_data " +
                                "where service_id = " + serviceID +
                                " order by struct_instant_id, " +
                                "structure_attrib_id";
        Statement attribStmt = currConnection.createStatement();
        ResultSet attribSet = attribStmt.executeQuery(attribQuery);
        structureAttributeList.removeAllItems();

        while (attribSet.next())
        {
            structureAttributeList.addItem(new String[]
                {
                    attribSet.getString("STRUCT_INSTANT_ID"),
                    attribSet.getString("STRUCTURE_ATTRIB_ID"),
                    attribSet.getString("ATTRIBUTE_VALUE"),
                });
        }

        attribStmt.close();
    }

        /**
         * Takes a result set that is a table of (Customer ID, Customer
         * Name and Service ID) and sets the document values.
         */
    protected void setTripletTable(ResultSet rset, Document xmlDoc)
        throws Exception
    {

            // count how many triplets are here...
        tripletCount = -1;//countResultSetSize(rset);
        currentTriplet = 0;

        CustomerInfo currCust = null;
        ServiceInfo currServ = null;

        while (! taskStopRequested && rset.next())
        {
            currCustID = rset.getString("CUSTOMER_ID");
            currCustName = rset.getString("CUSTOMER_NAME");
            currServiceID = rset.getString("SERVICE_ID");

                // adds a new searchable...
            //serviceData.addSearchable("Customer", currCustName);
            //serviceData.addSearchable("Customer ID", currCustID);
            //serviceData.addSearchable("Service", currServiceID);

            if (currCust == null || !currCust.custID.equals(currCustID))
            {
                    // get the customer if it doesnt exist...
                currCust = serviceData.getCustomer(currCustID);

                if (currCust == null)
                {
                    currCust = new CustomerInfo();
                    currCust.custID = currCustID;
                    currCust.custName = currCustName;
                    serviceData.addCustomer(currCustID, currCust);
                }
            }

            currServ = serviceData.getService(currServiceID);
            if (currServ == null)
            {
                currServ = new ServiceInfo();
                currServ.serviceID = currServiceID;
                serviceData.addService(currServiceID, currServ);
            }
            currServ.parentCust = currCust;
            currCust.addService(currServ);

            if (currServ.treeNode == null)
            {
                currServ.treeNode = extractService(currServiceID, xmlDoc);
            }
            currentTriplet ++;
        }
        taskStopRequested = false;
        populationCompleted = true;
    }

        /**
         * Given a node for a structure, also fetches all its attributes
         * and their values from the table.
         * At the moment, for each structureID a separate query is made.
         * Can this optimised?
         *
         * For example instead of doing
         * select * from service_data where service_id = xx and
         * structinstid = yyy
         *
         * we can avoid the structinstid = yyy condition and get "ALL"
         * attributes for the serviceID.  This is only one query.  So if
         * this was stored originally in a table, then this table can be
         * checked to see which entries have the structInstID and only
         * those fields could be extracted.
         *
         * Now here is where we use the structureAttribute List.
         * Simply find the first pointer to the serviceID and read all
         * subsequent items until there is a change in the serviceID since
         * the list is sorted by structInstId and then by attributeID!!!
         */
    protected void populateStructElementWithAttributes(String serviceID,
                                                       String structureID,
                                                       String structInstID,
                                                       Element structElem,
                                                       Document xmlDoc)
        throws SQLException
    {
            // so two queries are required:
            // one to get all attrib value pairs for the given node
            // this is with
            // select * from CSOM_ADMIN.service_data where service_id =
            // serviceID and struct_instant_id = structInstId

        //if (true) return ;

        LLNode currNode = structureAttributeList.head;

            // print what the first element is...
        String list[] = null;//(String[])currNode.key;
        //System.out.println("this, First Item InstID = " +
        //                              structInstID + ", " + list[0]);
        for (;currNode != null;currNode = currNode.next)
        {
            list = (String[])currNode.key;
            int comp = XMLUtils.compareStrings(structInstID, list[0]);
            if (comp == 0) break;
            else if (comp < 0) return ;
        }

        if (currNode == null) return ;

        Element attribsElement =
            xmlDoc.createElement(XMLUtils.ATTRIBUTES_ROOT_NAME);
        structElem.appendChild(attribsElement);

        LLNode startNode = currNode;
        for (;currNode != null;currNode = currNode.next)
        {
            list = (String[])currNode.key;
            if (! list[0].equals(structInstID)) break;

            String attribCode = list[1];
            String attribVal = list[2];
            String attribName = serviceData.getAttributeName(attribCode);

            Element attribEl = xmlDoc.createElement(
                    XMLUtils.ATTRIBUTE_ROOT_NAME);
            attribEl.setAttribute(XMLUtils.ATTRIB_CODE_ATTRIB, attribCode);
            attribEl.setAttribute(XMLUtils.ATTRIB_NAME_ATTRIB, attribName);
            attribEl.setAttribute(XMLUtils.ATTRIB_VALUE_ATTRIB, attribVal);
            attribsElement.appendChild(attribEl);
        }

        structureAttributeList.removeItemBetween(startNode,
                                currNode != null ? currNode.prev : currNode);

            // now remove all these items as they have already been used
            // and will not be accessed any more
        //structureAttributeList.removeRange(startIndex, currIndex + 1);
    }

        /**
         * Counts the size of a result set object.
         * This is a very embarassing and ineffecient way of doing it
         * but this is it for now.
         */
    protected int countResultSetSize(ResultSet rset) throws Exception
    {
        int count = 0;
        while (rset.next()) count++;
        while (rset.previous());
        return count;
    }

        /**
         * Gets the current value.
         */
    public int getCurrentValue()
    {
        return currentTriplet;
    }

        /**
         * Tells how many triplets are here.
         */
    public int getMaximumValue()
    {
        return tripletCount;
    }

        /**
         * Gets the minimum value.
         */
    public int getMinimumValue()
    {
        return 0;
    }

        /**
         * Gets the percentage value.
         */
    public double getPercentComplete()
    {
        return (100.0 * (double)currentTriplet) / tripletCount;
    }

        /**
         * returns the task status.
         */
    public int getTaskStatus()
    {
        if (taskStopped) return CANCELLED;
        return populationCompleted ? SUCCEEDED : EXECUTING;
    }

        /**
         * Gets the current message.
         */
    public String getMessage()
    {
        return populationCompleted ?
                    currentTriplet + " Services Extracted." : 
                    "Extracting Service " +
                    currentTriplet + (tripletCount > 0 ? " / " + tripletCount : "") +
                                        ": " +
                    currCustID + " - " + currCustName + " - " + currServiceID;
    }
}
