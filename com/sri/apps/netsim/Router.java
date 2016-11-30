package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;
/**
 * A Router class.
 * Basically used as a Layer3 device.
 */
public class Router extends Device
{
    //protected RoutingModule routingModule = null;

        /**
         * Constructor.
         */
    public Router(String name, int nInterfaces)
    {
        setName(name);
        ensureInterfaceCapacity(nInterfaces);
        //routingModule = new RoutingModule(this);
    }

        /**
         * Process an XML node.
         */
    public void processXMLNode(Element deviceElement)
    {
/*        super.processXMLNode(deviceElement);
        Element intNode = (Element)deviceElement.getElementsByTagName(XMLParams.DEVICE_INTERFACES).item(0);
        defaultInterface = Integer.parseInt(intNode.getAttribute(XMLParams.ROUTER_DEFAULT_INT));

            // also get the routing table..
            // note that this must be done LAST after all
            // other nodes have been read...
        Element rtNode = (Element)deviceElement.getElementsByTagName(XMLParams.ROUTING_TABLE_NAME).item(0);

        if (rtNode != null)
        {
            routingTable.processXMLNode(rtNode, this);
        }*/
    }

        /**
         * Sets the interface at the given index.
         */
    protected void setInterface(int index, NetworkInterface iface)
    {
        if (interfaces[index] == null)
        {
            interfaces[index] = iface;
        } else
        {
            interfaces[index].copyFrom(iface);
        }
    }

        /**
         * Enables or disables an interface.
         */
    public void enableInterface(NetworkInterface nic, boolean enable)
    {
    }
}
