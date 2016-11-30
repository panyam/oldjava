package com.sri.apps.netsim;

/**
 * Describes a connection.
 */
class Connection
{
    public boolean connectionClosed = false;

    public final static int FINAL_PACKET            = 0;
    public final static int INVALID_PACKET          = 1;
    public final static int CLIENT_TO_SERVER_PACKET = 2;
    public final static int SERVER_TO_CLIENT_PACKET = 3;

    int protocol;

        /**
         * Source IP and Port.
         */
    int srcIP, srcPort;

        /**
         * Destination IP and Port.
         */
    int destIP, destPort;

        // a time stamp showing when a packet for
        // this connection was last seen.
    long expiryTime;

        /**
         * Constructor.
         */
    public Connection(int protocol)
    {
        this.protocol = protocol;
    }

    public void initialise(Packet packet,
                           int srcIP, int srcPort,
                           int destIP, int destPort)
    {
        connectionClosed = false;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.destIP = destIP;
        this.destPort = destPort;
    }

        /**
         * Process a packet and update the state variables 
         * of this connection.
         */
    public int processPacket(Packet packet, 
                             int srcIP, int srcPort,
                             int destIP, int destPort)
    {
        if (this.srcIP == srcIP &&
            this.srcPort == srcPort &&
            this.destIP == destIP &&
            this.destPort == destPort)
        {
            return CLIENT_TO_SERVER_PACKET;
        } else if (this.srcIP == destIP &&
                   this.srcPort == destPort &&
                   this.destIP == srcIP &&
                   this.destPort == srcPort)
        {
            return SERVER_TO_CLIENT_PACKET;
        } else
        {
            return INVALID_PACKET;
        }
    }
}
