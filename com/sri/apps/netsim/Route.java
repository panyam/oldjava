package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Defines a normal Route.
 */
public class Route
{
        /**
         * Type of route.
         *  Can be:
         *      -   DIRECT: Routes that are used for interfaces
         *      -   STATIC: Static routes.
         *
         *      Anything else denotes that the route was created
         *      by a particular routing protocol.
         */
    byte routeType = DIRECT;

        /**
         * Route characters.
         */
    static String routeTypes[] =
    {
        "D", "S", "O"
    };

        /**
         * FLAG for a direct route.
         */
    protected final static byte DIRECT = 0;

        /**
         * FLAG for a static route.
         */
    protected final static byte STATIC = 1;

        /**
         * Flag for an known route type.
         */
    protected final static byte OTHER = 2;

        /**
         * The destination address.
         */
    int destAddress = 0;

        /**
         * The network mask.
         * By default this is 0 bits in length, which means
         * its all 1s
         * If the length is 0, then this is a host route.
         */
    int maskLength = 0;

        /**
         * The network interface which would be our next hope
         * in order to reach this network.  The question
         * is do we need a nextHopInterface or a nextHopIP
         * which is an IP address that is PART of the subnet that
         * to which the interface itself belongs to?
         */
    NetworkInterface destInterface;

        /**
         * Constructor.
         */
    public Route()
    {
    }

        /**
         * Gives a string representation.
         */
    public String toString()
    {
        int network = destAddress & (0xffffffff << (32 - maskLength));
        return (Utils.ipToString(network) + "/" + maskLength) + 
               " through Interface " + destInterface.interfaceID;
    }

        /**
         * Prints out the route info.
         */
    public void print()
    {
        int network = destAddress & (0xffffffff << (32 - maskLength));
        System.out.println("Route " + 
                           (Utils.ipToString(network) + "/" + maskLength) + 
                           " through Interface " + destInterface.interfaceID);
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc)
    {
        Element routeNode = doc.createElement(XMLParams.ROUTE_TAG_NAME);

        routeNode.setAttribute(XMLParams.ROUTE_DEST_NET, 
                                Utils.ipToString(destAddress));

        routeNode.setAttribute(XMLParams.ROUTE_SUBNET, 
                                "/" + maskLength);

        routeNode.setAttribute(XMLParams.ROUTE_INTERFACE,
                                "" + destInterface.interfaceID);

        routeNode.setAttribute(XMLParams.ROUTE_TYPE, routeTypes[routeType]);

        return routeNode;
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         */
    public void processXMLNode(Element routeNode, Device parent)
    {
        destAddress =
            Utils.stringToIP(
                    routeNode.getAttribute(XMLParams.ROUTE_DEST_NET));

        maskLength = Utils.stringToMaskLength(
                routeNode.getAttribute(XMLParams.ROUTE_SUBNET));

        if (maskLength == 0) maskLength = 32;

            // make the destination address a network address rather
            // than an IP address.
        destAddress &= (0xffffffff << (32 - maskLength));

        int interfaceID =
            Integer.parseInt(
                    routeNode.getAttribute(XMLParams.ROUTE_INTERFACE));

        destInterface = parent.interfaces[interfaceID];

        String rType = routeNode.getAttribute(XMLParams.ROUTE_TYPE);

        if (rType.equals("D")) routeType = DIRECT;
        else if (rType.equals("S")) routeType = STATIC;
        else routeType = OTHER;
    }
}

