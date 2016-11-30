package com.sri.apps.netsim; 

import java.util.*;

/**
 *  A Server. Basically any applications that binds to a port
 *  and can receive packets on that port. So only aplies to 
 *  UDP or TCP packets.
 */
public abstract class PortServer extends PortService
{
        /**
         * Constructor.
         */
    protected PortServer(HostModule host, String name, int port)
    {
        super(host, name, port);
    }
}
