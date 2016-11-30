package com.sri.apps.netsim; 

import java.util.*;

/**
 *  A Server. Basically any applications that binds to a port
 *  and can receive packets on that port. So only aplies to 
 *  UDP or TCP packets.
 */
public abstract class PortClient extends PortService
{
        /**
         * Destination mac address.
         */
    long destMac = 0;

        /**
         * The server to which we are connecting.
         */
    int serverIP;

        /**
         * The port on which we are connecting to.
         * This forms the destination port of all
         * packets originating from this service.
         */
    int serverPort;

        /**
         * Constructor.
         */
    public PortClient(HostModule host)
    {
            // initally we set the port to -1 
            // as the port will be automatically assigned
            // by the hsot object...
        super(host, "", -1);
    }

        /**
         * Connects to a server.
         */
    public abstract int connectTo(long destMac, int serverIP, int dport)
        throws Exception ;
}
