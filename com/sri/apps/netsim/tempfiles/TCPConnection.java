package com.sri.apps.netsim;

/**
 * Describes a TCP connection.
 */
class TCPConnection extends Connection
{
    protected final static byte SYN_EXPECTED        = 0;
    protected final static byte SYN_ACK_TO_BE_SEEN  = 1;
    protected final static byte SYN_ACK_SENT        = 2;

    protected byte state = SYN_EXPECTED;

        /**
         * Constructor.
         */
    public TCPConnection()
    {
        super(Protocols.TCP);
    }

        /**
         * Process a packet and update the state variables 
         * of this connection.
         */
    public int processPacket(Packet packet, 
                             int srcIP, int srcPort,
                             int destIP, int destPort)
    {
        int packetType = super.processPacket(packet,
                                             srcIP, srcPort,
                                             destIP, destPort);
        if (packetType == INVALID_PACKET) return INVALID_PACKET;

            // otherwise do checks based on the state.

        if (state == SYN_EXPECTED)
        {
            if (packetType == CLIENT_TO_SERVER_PACKET &&
                    TCPUtils.isFlagSet(packet, TCPUtils.SYN_FLAG) && 
                   !TCPUtils.isFlagSet(packet, TCPUtils.ACK_FLAG))
            {
                state = SYN_ACK_TO_BE_SEEN;
                return packetType;
            }
            return INVALID_PACKET;
        } else if (state == SYN_ACK_TO_BE_SEEN)
        {
            if (packetType == SERVER_TO_CLIENT_PACKET &&
                  TCPUtils.isFlagSet(packet, TCPUtils.SYN_FLAG) && 
                  TCPUtils.isFlagSet(packet, TCPUtils.ACK_FLAG))
            {
                state = SYN_ACK_TO_BE_SEEN;
                return packetType;
            }
            return INVALID_PACKET;
        } else if (TCPUtils.isFlagSet(packet, TCPUtils.FIN_FLAG))
        {
            connectionClosed = true;
            return packetType;
        }
        return packetType;
    }
}
