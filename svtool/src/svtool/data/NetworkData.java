
package svtool.data;

import svtool.core.*;

import java.io.*;

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
public class NetworkData extends DBViewData
{
        /**
         * Names of all the different tables in which devices may reside.
         * For the moment they contain "DM01" and "Service Data"
         */
    protected List tableNames = new LinkedList();

        /**
         * List of devices for each table.
         *
         * Each list will be a List of devices.
         */
    protected List deviceLists = new LinkedList();

        /**
         * Column names.
         */
    protected final static String devInfoColumns[] =  new String []
    {
        "DEVICE_NAME", "VLAN_ID", "VLAN_FNN",
        "PORT_NBR", "PORT_FNN", "VTP_DOMAIN"
    };

        /**
         * Gets the number of columns in the dev info columns.
         */
    public final static int getColumnCount()
    {
        return devInfoColumns.length;
    }

        /**
         * Returns the name of the column at a given index.
         *
         * @param   i   index of the column.
         * @return  Returns the name of the column at the requested index.
         *
         * @author Sri Panyam
         */
    public final static String getColumnName(int i)
    {
        return devInfoColumns[i];
    }

        /**
         * Constructor.
         */
    public NetworkData()
    {
    }

        /**
         * Adds a new device table to the list of devices.
         *
         * @param   tableName   Name of the table to add.
         * @return  The table id if  new table is added or the existing ID
         *          if it already exists.
         */
    public int addTable(String tableName)
    {
        int id = getTableID(tableName);
        if (id >= 0) return id;
        deviceLists.add(new ArrayList());
        tableNames.add(tableName);
        return deviceLists.size() - 1;
    }

        /**
         * Gets the integer ID of the table.
         */
    public int getTableID(String tableName)
    {
        int i = 0;
        for (Iterator iter= tableNames.iterator();iter.hasNext();i++)
        {
            String tN = (String)iter.next();
            if (tN.equalsIgnoreCase(tableName)) return i;
        }

        return -1;
    }

        /**
         * Insert a list into the given list.
         */
    protected synchronized int insertDevice(List devList,DeviceInfo dev)
    {
            // dont insert empty devices
        if (dev.attribs[0].trim().length() == 0) return -1;

        int lo = 0, hi = devList.size() - 1;

        if (devList.isEmpty())
        {
            devList.add(dev);
            return 0;
        }

        while (lo < hi)
        {
            int mid = (lo + hi) / 2;
            DeviceInfo midDev = (DeviceInfo)devList.get(mid);
            int comp = dev.compareTo(midDev);
            if (comp == 0) 
            {
                //System.out.println("Found Duplicate: " + midDev + ", " + dev);
                return mid;
            }
            else if (comp < 0)
            {
                hi = mid == lo ? lo : mid - 1;
            } else
            {
                lo = mid + 1;
            }
        }

                // then insert above or below it...
        if (lo == hi)
        {
            int comp = dev.compareTo((DeviceInfo)devList.get(lo));
            if (comp == 0) return lo;
            else if (comp < 0)
            {
                devList.add(lo, dev);
                    // fire??
            return lo;
            } else
            {
                devList.add(lo + 1, dev);
                    // fire??
                return lo + 1;
            }
        } else
        {
            System.out.println("lo, hi, dev, loItem, hiItem: " + 
                    lo + ", " + hi + ", " + dev.attribs[0] + ", " + 
                        ((DeviceInfo)devList.get(lo)).attribs[0] + ", " + 
                        ((DeviceInfo)devList.get(hi)).attribs[0]);
        }
        return lo;
    }

        /**
         * Insert a device into the table specified by its ID.
         *
         * @param   tableID     The id of the table into which the device
         *                      is to be inserted.
         *          dev         The device to be inserted.
         *
         * @author  Sri Panyam
         */
    public synchronized int insertDevice(int tableID, DeviceInfo dev)
    {
        if (tableID < 0) return -1;

        List devList = (List)deviceLists.get(tableID);
        return insertDevice(devList, dev);
    }

        /**
         * Insert a device into the table by the given name.
         *
         * @param   tableName   The name of the table into which the device
         *                      is to be inserted.
         *          dev         The device to be inserted.
         *
         * @author  Sri Panyam
         */
    public synchronized int insertDevice(String tableName, DeviceInfo dev)
    {
        return insertDevice(getTableID(tableName), dev);
    }

        /**
         * Get a specific list.
         *
         * @param   index   The index within the list of device lists.
         * @return  The requested device list.
         */
    public synchronized List getDeviceList(int index)
    {
        return (List)deviceLists.get(index);
    }

        /**
         * Get a specific list, given by its name.
         *
         * @param   tableName   The name of the table whose list is to be
         *                      returned.
         * @return  The requested device list.
         */
    public synchronized List getDeviceList(String tableName)
    {
        return getDeviceList(getTableID(tableName));
    }
}
