package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A RoutingTable class.
 *
 * Allows for high speed access of routes and for their
 * addition and deletion.
 */
public class RoutingTable implements SharedObject
{
    public final static int EXACT       = 0;
    public final static int MATCH_ROUTE = 1;
    public final static int BEST_SPOT   = 2;

        /**
         * The default route.
         */
    Route defaultRoute = null;

        /**
         * Routes.
         */
    Route routes[] = new Route[10];
        
        /**
         * Number of routes in each table.
         */
    int nRoutes = 0;
        
        /**
         * Constructor.
         */
    public RoutingTable()
    {
    }

        /**
         * Compare two routes and return a result
         * in terms of which one is "greater"
         */
    public static int routeCompare(int dest1, int len1,
								   int dest2, int len2)
    {
        int net1, net2 = 0;
        if (len1 < len2)
        {
            net1 = (dest1 >> (32 - len1)) & 0x7fffffff;
            net2 = (dest2 >> (32 - len1)) & 0x7fffffff;
                // means dest1 is a super net of dest2
                // so dest1 is greater than dest2
                // so dest1 would lie later in the list than dest2
            if (net1 == net2)
            {
                return 1;
            } else
            {
                return net1 - net2;
            }
        } else if (len1 > len2)
        {
            net1 = (dest1 >> (32 - len2)) & 0x7fffffff;
            net2 = (dest2 >> (32 - len2)) & 0x7fffffff;
                // means dest1 is a sub net of dest2
                // so dest1 is lesser than dest2
                // so dest1 would lie earlier in the list than dest2
            if (net1 == net2)
            {
                return -1;
            } else
            {
                return net1 - net2;
            }
        } else
        {
            net1 = (dest1 >> (32 - len2)) & 0x7fffffff;
            net2 = (dest2 >> (32 - len2)) & 0x7fffffff;
            if (net1 == net2)
            {
                return dest1 & NetUtils.WILDCARD_MASK[len1] - 
                       dest2 & NetUtils.WILDCARD_MASK[len1];
            } else
            {
                return net1 - net2;
            }
        }
    }

        /**
         * Ensure enough capacity is there for routes.
         */
    public void ensureRouteCapacity(int capacity)
    {
        if (routes.length < capacity)
        {
            Route dr2[] = routes;
            routes = null;
            routes = new Route[capacity];
            System.arraycopy(dr2, 0, routes, 0, nRoutes);
            dr2 = null;
        }
    }

        /**
         * Remove all the routes.
         */
    public synchronized void removeAllRoutes()
    {
        if (routes == null) return ;
        for (int i = 0;i < routes.length;i++)
        {
            routes[i] = null;
        }
        nRoutes = 0;
    }

        /**
         * Remove a route at the given index.
         */
    public void removeRoute(int index)
    {
        int nR = nRoutes;
        for (int i = index;i < nR - 1;i++)
        {
            routes[i] = routes[i + 1];
        }
        routes[nR - 1] = null;
        nRoutes--;
    }

        /**
         * Insert a new route at the given index.
         */
    public void insertRoute(Route route, int index)
    {
        //System.out.println("Adding route: " + route + ", at " + index);
        int nR = nRoutes;
        for (int i = nR;i > index;i--)
        {
            routes[i] = routes[i - 1];
        }
        routes[index] = route;
        nRoutes++;
    }

        /**
         * Get the number of routes in the table.
         */
    public int getRouteCount()
    {
        return nRoutes;
    }

        /**
         * Get the ith route.
         */
    public Route getRoute(int index)
    {
        return routes[index];
    }

        /**
         * Find a route that would match the 
         * given address and netmask.
         * First find the route int eh direct route
         * list.  Then the indirect ones...
         */
    public Route findRoute(int dest, int destLen)
    {
        for (int i = 0;i < nRoutes;i++)
        {
            if (destLen >= routes[i].maskLength)
            {
                int mask = 0xffffffff << (32 - routes[i].maskLength);
                if ((dest & mask) == (routes[i].destAddress & mask))
                {
                    return routes[i];
                }
            }
        }
        return null;
    }

        /**
         * Add a route to the table.
         * Assumes that the table is sorted.
         */
    public void addRoute(Route route)
    {
        ensureRouteCapacity(nRoutes + 1);

        int i = 0;
        for (;i < nRoutes &&
               routeCompare(route.destAddress,
                            route.maskLength,
                            routes[i].destAddress,
                            routes[i].maskLength) > 0;i++);


        for (int j = nRoutes;j > i;j--) routes[j] = routes[j - 1];
        routes[i] = route;
        nRoutes++;
    }

        /**
         * Prints out the routing table.
         */
    public void print()
    {
        for (int i = 0;i < nRoutes;i++)
        {
            routes[i].print();
        }
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element rTableNode =
            doc.createElement(XMLParams.ROUTING_TABLE_ROOT_NAME);

            // add the routes as we need them...
        for (int i = 0;i < nRoutes;i++)
        {
            rTableNode.appendChild(routes[i].getXMLNode(doc));
        }
        return rTableNode;
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         */
    public void readXMLNode(Element element/*, Device parent*/)
    {
        // process the interfaces
        Device parent = null;
        NodeList routeNodes =
            element.getElementsByTagName(XMLParams.ROUTE_TAG_NAME);

        this.nRoutes = 0;
        int routeNodeSize = routeNodes.getLength();
        ensureRouteCapacity(routeNodeSize + 5);

        for (int i = 0;i < routeNodeSize;i++)
        {
            Element routeNode = (Element)routeNodes.item(i);
            Route route = new Route();
            route.processXMLNode(routeNode, parent);
            addRoute(route);
        }
    }
}
