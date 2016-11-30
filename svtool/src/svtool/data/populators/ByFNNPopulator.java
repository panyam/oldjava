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
public class ByFNNPopulator extends ServiceDataPopulator
{
    String fnns[] = null;
    int nFNNs = 0;
    StringBuffer fnnListBuffer = new StringBuffer();
    StringBuffer fnnTypeList = new StringBuffer("0");
    String fnnTypes[] = null;

        /**
         * Sets the fnns that are to be extracted.
         */
    public void setFNNs(String fnnList)
    {
        setItemList(fnnList);
    }

        /**
         * Sets the types of FNNs to be extracted.
         */
    public void setFNNTypes(int fnnList[])
    {
        fnnTypeList.delete(0, fnnTypeList.length());

        if (fnnList == null) 
        {
            fnnTypeList.append("0");
            return ;
        }

        fnnTypes = new String[fnnList.length];

        for (int i = 0;i < fnnList.length; i++)
        {
            if (i > 0) fnnTypeList.append( " , ");
            fnnTypeList.append(fnnList[i]);
            fnnTypes[i] = fnnList[i] + "";
        }
    }

        /**
         * Set the fnn list.
         */
    public void setItemList(String fnnList)
    {
        StringTokenizer tokens =
            new StringTokenizer(fnnList," \t,", false);
        nFNNs= tokens.countTokens();
        fnns = new String[nFNNs];
        fnnListBuffer.delete(0, fnnListBuffer.length());

        int i = 0;
        while (tokens.hasMoreTokens())
        {
            if (i > 0) fnnListBuffer.append(" , ");
            fnns[i] = tokens.nextToken();
            fnnListBuffer.append("'");
            fnnListBuffer.append(fnns[i]);
            fnnListBuffer.append("'");
            i++;
        }
    }

        /**
         * Now that we know what fnns there are, 
         * get all the services and custoemrs that own these FNNs.
         */
    public void populateDocument()
        throws Exception
    {
        //serviceData.clearSearchables();
        Document xmlDoc = serviceData.createNewXMLDocument();

        serviceData.clearAll();

            // first get all the services that 
            // belong to this customer...
            // query to get all services...
            // select c.customer_id, c.customer_name, s.service_id
            //  from service s, customer c
            //  where s.service_id in
            //  ( select service_id from csom_admin.service_data
            //      where
            //        attribute_value in
            //          ('N3003980R', 'N8000827R')		  -- get the service_id to which 
            //                                               this vlan belongs to
            //        and
            //        structure_attrib_id in ( xxx, xxx, xxx )
            //  )
            //  and
            //  s.customer_id = c.customer_id
            //  order by customer_id
            //
            // This would return a table with 3 columns


        String query = 
            "   select c.customer_id, c.customer_name, s.SERVICE_ID " +
            "       from CSOM_ADMIN.service s, CSOM_ADMIN.customer c " + 
            "       where " + 
            "       s.service_id in " + 
            "       ( select service_id from CSOM_ADMIN.service_data " + 
            "         where " + 
            "         attribute_value in ( " + fnnListBuffer.toString() + ")" +
            "         and " + 
            "         structure_attrib_id in ( " + fnnTypeList.toString() + ")" + 
            "       ) and " + 
            "       s.customer_id = c.customer_id" + 
            "       order by customer_id ";

        //System.out.println("TypeList: " + fnnTypeList);
        //System.out.println("FNNList : " + fnnListBuffer);
        //System.out.println("Query: " + query);

        Connection currConnection = currDB.getConnection();
        Statement stmt = currConnection.createStatement();
        ResultSet rset = stmt.executeQuery(query);

        setTripletTable(rset, xmlDoc);
        stmt.close();

            // now add "FNN" items to the servicedata tree...
        for (int i = 0;i < fnns.length;i++)
        {
            FNNInfo newFNN = new FNNInfo();
            newFNN.fnnID = fnns[i];
            serviceData.addFNNInfo(newFNN);
        }

        /*
        Iterator iter = serviceData.getServiceIterator();

        while (iter.hasNext())
        {
            ServiceInfo sInfo = (ServiceInfo)iter.next();

            System.out.println("DOing Service, node: " + sInfo.serviceID + ", " + sInfo.treeNode.getClass());
            addFNNItems(sInfo, sInfo.treeNode);
        }*/

            // also set the FNN list in the document...
        populationCompleted = true;
    }

        /**
         * Go through the treeNode and its children and add all FNN of
         * certain types as children to the current service.
         */
    protected void addFNNItems(ServiceInfo currService, Element treeNode)
    {
        NodeList kids = treeNode.getChildNodes();
        int nKids = kids.getLength();

        NamedNodeMap attrs = treeNode.getAttributes();
        int nAttr = attrs.getLength();

        //System.out.println("Attributes: ");
        String attrCode = treeNode.getAttribute(XMLUtils.ATTRIB_CODE_ATTRIB);
        String attrName = treeNode.getAttribute(XMLUtils.ATTRIB_NAME_ATTRIB);
        String attrVal = treeNode.getAttribute(XMLUtils.ATTRIB_VALUE_ATTRIB);

        for (int i = 0;i < nAttr;i++)
        {
            attrCode = attrName = attrVal = null;
            Node node = attrs.item(i);
            String nodeName = node.getNodeName();
            String nodeValue = node.getNodeValue();

            if (nodeName.equals(XMLUtils.ATTRIB_CODE_ATTRIB))
            {
                attrCode = nodeValue;
            } else if (nodeName.equals(XMLUtils.ATTRIB_VALUE_ATTRIB))
            {
                attrVal = nodeValue;
            }
            //System.out.println(nodeName + " = " + nodeValue);
        }

        //System.out.println(nAttr + ", " + attrCode + ", " + attrName + ", " + attrVal);

        //if (true) return ;

        for (int i = 0;i < nKids;i++)
        {
            if ( ! (kids.item(i) instanceof Element)) continue;

            Element kid = (Element)kids.item(i);

                // see if there are any child nodes of certain types...
            addFNNItems(currService, kid);
        }
    }
}
