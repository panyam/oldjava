package com.sri.apps.netsim;

/**
 * Our MSG Client class.
 */
public class MSGClient extends TCPClient
{
        /**
         * Total number of requests to send to the client.
         */
    int nRequests = 2;

        /**
         * Which request are we processing now?
         */
    int currRequest = 0;

        /**
         * The client.
         */
    public MSGClient(HostModule module, int numRequests)
    {
        super(module);
        this.nRequests = numRequests;
    }

        /**
         * Reset the client.
         */
    public void reset()
    {
        currRequest = 0;
    }

        /**
         * Basically this is what is extended by each protocol.
         */
    protected void handleSessionPacket(Packet packet) 
        throws Exception
    {
            // size of the payload.
        int payloadSize = IPUtils.readLength(packet) - TCPUtils.TCP_DATA_OFFSET;
        String payLoad = new String(packet.data,
                                    TCPUtils.TCP_DATA_OFFSET,
                                    payloadSize);

        hostModule.parent.
            writeToConsole(DeviceEvent.GOT_REPLY_FROM_SERVER, payLoad);

        if (currRequest == nRequests)
        {
            disconnect();
        }
        currRequest++;

            // send another request...
        writePayload(MSGProtocol.SEND_ME_A_NUMBER);
    }

        /**
         * Called once, after the syn-ack packet is received
         * so taht we can start sending data or receiving 
         * data and so on.
         */
    protected void startProtocolBasedCommunication()
        throws Exception
    {
        // send a requestion to the server with a 
        // SEND_ME_A_NUMBER command
        writePayload(MSGProtocol.SEND_ME_A_NUMBER);
    }
}
