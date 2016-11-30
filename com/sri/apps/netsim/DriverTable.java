package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Holds a table of Protocol Drivers.
 * Basically this is a two dimensional array of Drivers.
 */
public class DriverTable implements XMLObject
{
        /**
         * The actual array that holds this info.
         */
    ProtocolDriver drivers[][] = new ProtocolDriver[2][1];

        /**
         * Number of Drivers in each level.
         */
    int nDrivers[] = new int[2];

        /**
         * Constructor.
         */
    public DriverTable()
    {
        this(5);
    }

        /**
         * Constructor.
         */
    public DriverTable(int nLevels)
    {
        drivers = new ProtocolDriver[nLevels][1];
        nDrivers = new int[nLevels];
    }

        /**
         * Get a protocol driver at a given level and index.
         */
    public ProtocolDriver getDriver(int level, int index)
    {
        return drivers[level][index];
    }

        /**
         * Gets the driver in the given level whose class
         * matches the required class.
         */
    public ProtocolDriver getDriver(Class driverClass, int level)
    {
        for (int i = 0;i < nDrivers[level];i++)
        {
            if (drivers[level][i].getClass() == driverClass)
            {
                return drivers[level][i];
            }
        }
        return null;
    }

        /**
         * Get the number of levels there are.
         */
    public int getDriverCount(int level)
    {
        return 0;
    }

        /**
         * Get the number of levels there are.
         */
    public int getLevelCount()
    {
        return 0;
    }

        /**
         * Adds a new packet driver.
         */
    public void addDriver(ProtocolDriver driver)
    {
        /*synchronized (protocolDrivers)
        {
            if (protocolDrivers == null) protocolDrivers = new ProtocolDriver[1];

            if (protocolDrivers.length <= nDrivers)
            {
                ProtocolDriver pm2[] = protocolDrivers;
                protocolDrivers = new ProtocolDriver[nDrivers + 1];
                System.arraycopy(pm2, 0, protocolDrivers, 0, nDrivers);
            }
            driver.driverID = nDrivers;
            protocolDrivers[nDrivers++] = driver;
        }*/
    }

        /**
         * Reads an XML Node.
         */
    public void readXMLNode(Element elem) throws Exception
    {
    }

        /**
         * Creates an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        return null;
    }
}
