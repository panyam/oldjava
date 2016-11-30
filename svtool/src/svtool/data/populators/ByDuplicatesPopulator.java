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
public class ByDuplicatesPopulator extends ByFNNPopulator
{
        /**
         * Now that we know what fnns there are, 
         * get all the services and custoemrs that own these FNNs.
         */
    public void populateDocument()
        throws Exception
    {
        //serviceData.clearSearchables();
            // first extract the "duplicate services" this can be done by
            // this query:
        String query1=
            "select service_name , count(*) cn from  CSOM_ADMIN.service " +
            "   group by service_name having count (*) > 1 ";
        String query2 =
            "select distinct(dup_services.service_name), " +
                    "sv_data.service_id from " + 
            "( select service_name , count(*) cn from  CSOM_ADMIN.service " +
            "   group by service_name having count (*) > 1 " +
            ") dup_services, " +
            "( select * from CSOM_ADMIN.service_data " + 
            "   where structure_attrib_id in ( " + fnnTypeList + ") " + 
            ") sv_data " + 
            " where " +
            "   sv_data.attribute_value = dup_services.service_name " +
            "   and " +
            "   sv_data.structure_attrib_id in ( " + fnnTypeList + ") " +
            " order by " +
            "   dup_services.service_name ";
        Connection currConnection = currDB.getConnection();
        Statement stmt = currConnection.createStatement();
        ResultSet rset = stmt.executeQuery(query1);

        StringBuffer flBuff = new StringBuffer();

        int i = 0;
        while (rset.next())
        {
            if (i > 0) flBuff.append(", ");
            flBuff.append(rset.getString("SERVICE_NAME"));
            i++;
        }

        setItemList(flBuff.toString());

        stmt.close();

        super.populateDocument();
        populationCompleted = true;
    }
}
