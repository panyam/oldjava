package com.sri.apps.netsim;

    /**
     * Our connection table.
     */
public class ConnectionTable
{
        /**
         * The list of connections.
         */
    Connection connections[] = new Connection[5];
    int nConnections = 0;

        /**
         * Create a new connection entry.
         */
    public Connection createConnection(Packet packet, int proto,
                                       int srcIP, int srcPort,
                                       int destIP, int destPort)
    {
        Connection newConn = null;
        if (proto == Protocols.TCP)
        {
            newConn = new TCPConnection();
        } else if (proto == Protocols.UDP)
        {
            newConn = new Connection(proto);
        }
        newConn.initialise(packet, srcIP, srcPort, destIP, destPort);

        addConnection(newConn);
        return newConn;
    }

        /**
         * Ensure we have enough space.
         */
    public void ensureCapacity(int newCap)
    {
        if (newCap > connections.length)
        {
            Connection conn[] = connections;
            connections = new Connection[newCap + 5];
            System.arraycopy(conn, 0, connections, 0, nConnections);
        }
    }

    public void removeConnection(int which)
    {
        for (int i = which;i < nConnections - 1;i++)
        {
            connections[i] = connections[i + 1];
        }
        connections[nConnections - 1] = null;
        nConnections--;
    }

        /**
         * Add a new connection.
         */
    public void addConnection(Connection conn)
    {
        ensureCapacity(nConnections + 1);
        connections[nConnections++] = conn;
    }

        /**
         * Get a connection that matches the packet with
         * the following source and destination info.
         * Also create a connection if it doesnt exist.
         */
    public Connection getConnection(int srcIP, int srcPort,
                                    int destIP, int destPort)
    {
        long currTime = 0;

        for (int i = 0;i < nConnections;i++)
        {
            if (connections[i].srcIP == srcIP &&
                connections[i].destIP == destIP &&
                connections[i].srcPort == srcPort &&
                connections[i].destPort == destPort)
            {
                    // check the timer value as well..
                return connections[i];
            }
        }
        return null;
    }

        /**
         * Process a packet and return the connection
         * to which the given packet belongs.
         */
    public Connection processPacket(Packet packet, 
                                    int srcIP, int srcPort,
                                    int destIP, int destPort)
    {
        for (int i = 0;i < nConnections;i++)
        {
            if (connections[i].processPacket(packet,
                                             srcIP, srcPort,
                                             destIP, destPort) != Connection.INVALID_PACKET)
            {
                Connection out = connections[i];
                if (connections[i].connectionClosed)
                {
                    // then remove this connection
                    removeConnection(i);
                }
                return out;
            }
        }
        return null;
    }
}
