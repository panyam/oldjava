
package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A routing module.
 * When attached to a device, takes care of the routing of packets.
 */
public class RoutingModule extends PacketModule
{
        /**
         * This is the table that will be modified by
         * most processes and consulted when routing
         * decisions need to be made.
         * The structure is still undecided.
         */
    protected RoutingTable routingTable = null;

        /**
         * The default interface.
         */
    protected int defaultInterface = 0;

    protected final static byte INVALID_FRAME   = -2;
    protected final static byte NORMAL_FRAME    = -1;

    IPTable ipTable = null;

        /**
         * Constructor.
         */
    protected RoutingModule(Device dev)
    {
        super(dev);

            // find if there is an IPTable object in the parent..
            // if one isnt found then add it in...
        ipTable = (IPTable)parent.getSharedObject(IPTable.class, true);
    }

        /**
         * Add a new route.
         */
    public void addRoute(Route route)
    {
        routingTable.addRoute(route);
    }

        /**
         * Generate default routes for all interfaces.
         */
    public void generateDefaultRoutes()
    {
            // is this step necessary??
        //routingTable.removeAllRoutes();
        Route newRoute = null;
        NetworkInterface iface = null;
        //System.out.println("nInterfaces = " + nInterfaces);
        for (int i = 0;i < parent.nInterfaces;i++)
        {
            newRoute = new Route();
            newRoute.destAddress = ipTable.ipAddress[i];
            newRoute.maskLength = ipTable.subnetMask[i];
            newRoute.destInterface = parent.interfaces[i];
            addRoute(newRoute);
        }
    }

        /**
         * Get the type of packet it is.
         */
    protected byte getPacketType(Packet packet)
    {
        byte data[] = packet.data;

            // check protocol type... it has to be 0x0800
            // for an IP packet...
        if (data[12] != 0x08 || data[13] != 0) return INVALID_FRAME;

            // do other checks to see if we have proper
            // IP packets...

            // ignore non ipv4 packets...
        if (data[IPUtils.IP_OFFSET] >> 4 != 0x04) return INVALID_FRAME;

            // get the header length..
        int headerLen = data[IPUtils.IP_OFFSET] & 0x0f;

            // TODO:: Validate the header checksum

        return NORMAL_FRAME;
    }

        /**
         * Returns the routing table.
         */
    public RoutingTable getRoutingTable()
    {
        return routingTable;
    }

        /**
         * Reads an XML Node.
         */
    public void readXMLNode(Element elem) throws Exception
    {
        if (routingTable != null) routingTable = new RoutingTable();
        Element rTableNode = (Element)elem.getElementsByTagName(XMLParams.ROUTING_TABLE_ROOT_NAME).item(0);

        routingTable.readXMLNode(elem, parent);
    }

        /**
         * Creates an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element moduleElement =
            doc.createElement(XMLParams.PACKET_MODULE_TAG);

        moduleElement.setAttribute(XMLParams.CLASS_PARAM, "" + getClass());

        Element rtNode = routingTable.getXMLNode(doc);
        moduleElement.appendChild(rtNode);

        return moduleElement;
    }
}
