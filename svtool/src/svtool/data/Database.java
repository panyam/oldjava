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

public class Database
{
        /**
         * A variable to keep track of the sessions used so far.
         */
    protected static int sessionCounter = 0;

        /**
         * The sessionID is unique among any two Database isntances.
         */
    protected int sessionID;

    static
    {
        try
        {
                // register the databse driver here...
            DriverManager.registerDriver
                (new oracle.jdbc.driver.OracleDriver());
        } catch (Exception exc)
        {
            exc.printStackTrace();
            System.exit(0);
        }
    }

        /**
         * The host to which this database connection is made.
         */
    protected String databaseHost;

        /**
         * The loginname on the databse.
         */
    protected String login;

        /**
         * The password for the user connected to the database.
         */
    protected String password;

        /**
         * The current connection object.
         */
    protected Connection currConnection = null;

        /**
         * Constructor.
         */
    public Database()
    {
        sessionID = sessionCounter++;
    }

        /**
         * Get the DB's session ID.
         */
    public int getSessionID()
    {
        return sessionID;
    }

        /**
         * Get the password.
         */
    public String getPassword()
    {
        return password;
    }

        /**
         * Get the login name.
         */
    public String getLogin()
    {
        return login;
    }

        /**
         * Get the hostname.
         */
    public String getHost()
    {
        return databaseHost;
    }

        /**
         * Sets the required parameters.
         *
         * @param   host    The host to which connect to.
         *          login   The login name with which to connect to.
         *          pwd     The password for the login with which the
         *                  connection is made.
         */
    public void setParameters(String host, String login, String pwd)
        throws Exception
    {
        if (currConnection != null)
        {
            currConnection.close();
        }

        this.databaseHost = host;
        this.login = login;
        this.password = pwd;
    }

        /**
         * connect to a new dbase and return the connection.
         * The existing connection is closed.
         */
    public void connect() throws Exception
    {

        //System.out.println(" = " + host + ", " + login + ", " + pwd);

        currConnection =
            DriverManager.getConnection ("jdbc:oracle:oci8:@" +
                                         databaseHost, login, password);
        //refresh();
    }

        /**
         * Creates a new statement for running queries.
         *
         * @return Returns the new Statement object on which queries can be
         *         run.
         */
    public Statement newStatement() throws SQLException
    {
        return currConnection.createStatement();
    }

        /**
         * Closes the current connection.
         */
    public void closeConnection()
    {
        try
        {
            if (currConnection != null)
                currConnection.close();
            /*
            dbName = "";
            login = "";
            host = "";
            */
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        currConnection = null;
    }

        /**
         * Get the current connection.
         */
    public Connection getConnection()
    {
        return currConnection;
    }

        /**
         * Extracts the info about a FNN and creates all the tree for
         * one FNN by its ID.
         */
    public Element extractFNN2(String fnnID, Document xmlDoc,
                              String custID, String custName)
    {
        Element fnnElement = xmlDoc.createElement(XMLUtils.FNN_ROOT_NAME);
        fnnElement.setAttribute(XMLUtils.ID_ATTRIB, fnnID);

        fnnElement.setAttribute(XMLUtils.CUSTOMER_ID_ATTRIB, custID);
        fnnElement.setAttribute(XMLUtils.CUSTOMER_NAME_ATTRIB, custName);

        String query =  "select * from CSOM_ADMIN.service " +
                        "where service_name = " +
                        "\'" + fnnID + "\'";

        System.out.println("Processing FNN: " + fnnID);

        try
        {
            Statement serviceIDStmt = currConnection.createStatement();
            ResultSet servicesSet = serviceIDStmt.executeQuery(query);

            while (servicesSet.next())
            {
                String serviceID = servicesSet.getString("SERVICE_ID");

                    // create a serviceID entry into the current fnn node
                Element servElement = null;
                    //createServiceElement(serviceID, fnnElement, xmlDoc);

                fnnElement.appendChild(servElement);

                //System.out.println("------Service ID: " + serviceID);
            }
            serviceIDStmt.close();
        } catch (Exception ex2)
        {
            ex2.printStackTrace();
            return null;
        }
        return fnnElement;
    }

        /**
         * Get the owner of a service.
         */
    public String getServiceOwner(String service_id)
    {
        try
        {
            String query = "select * from CSOM_ADMIN.customer " +
                            "where customer_id in (" + 
                            " select customer_id from CSOM_ADMIN.service " +
                                " where service_id = " + service_id + " ) ";

            Statement stmt = currConnection.createStatement();
            ResultSet resultSet = stmt.executeQuery (query);

            while (resultSet.next())
            {
                // this structureID tells to which structure this attribute
                // belongs to!!!
                String custID = resultSet.getString("CUSTOMER_ID");
                String custName = resultSet.getString("CUSTOMER_NAME");

                stmt.close();

                return custID + "\n" + custName;
            }
            stmt.close();
        } catch (SQLException exc)
        {
            exc.printStackTrace();
        }

        return null;
    }

        /**
         * Returns the owner that owns this Vlan.
         */
    public String getFNNOwner(String fnnID)
    {
        try
        {
            String query = "select * from CSOM_ADMIN.customer " +
                            " where customer_id in " + 
                            "( select customer_id from CSOM_ADMIN.service " +
                                " where service_id in " + 
                                "( select service_id from " +
                                    " CSOM_ADMIN.service_data where " +
                                    " attribute_value = " + fnnID + ") ) ";
            Statement stmt = currConnection.createStatement();
            ResultSet resultSet = stmt.executeQuery (query);

            while (resultSet.next())
            {
                // this structureID tells to which structure this attribute
                // belongs to!!!
                String custID = resultSet.getString("CUSTOMER_ID");
                String custName = resultSet.getString("CUSTOMER_NAME");

                stmt.close();

                return custID + "\n" + custName;
            }
            stmt.close();
        } catch (SQLException exc)
        {
            exc.printStackTrace();
        }

        return null;
    }

        /**
         * Get the name of the customer given a customer ID.
         */
    public String getCustomerName(String custID)
    {
        try
        {
            String query = "select * from CSOM_ADMIN.customer " +
                            "where customer_id = " + custID;
            Statement stmt = currConnection.createStatement();
            ResultSet resultSet = stmt.executeQuery (query);

            while (resultSet.next())
            {
                // this structureID tells to which structure this attribute
                // belongs to!!!
                String custName = resultSet.getString("CUSTOMER_NAME");
                stmt.close();
                return custName;
            }
            stmt.close();
        } catch (SQLException exc)
        {
            exc.printStackTrace();
        }

        return null;
    }
}
