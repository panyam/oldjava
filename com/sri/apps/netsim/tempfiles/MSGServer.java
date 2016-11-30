package com.sri.apps.netsim;

import java.util.*;

/**
 * A simple message server.
 * Basically a server that sends out an arbitrary message to a client.
 * each time it receives a message from the client which says 
 * "AdviceMe number".
 *
 * Basically here is what happens in the "MSG" protocol!!!
 * Client connects to server
 * Client asks server for a magic number and server responds
 * this happens until client says "disconnect" or either
 * side just closes the connection...
 */
public class MSGServer extends TCPServer
{
        /**
         * Response Number.
         */
    protected int responseNumber = 1;

        /**
         * Constructor.
         */
    public MSGServer(HostModule host, String name, int port)
    {
        super(host, name, port);
    }

        /**
         * Start the server.
         */
    public void start()
    {
        responseNumber = 1;
        super.start();
    }

        /**
         * Basically this is what is extended by each protocol.
         */
    protected void handleSessionPacket(Packet packet, int clientIndex)
        throws Exception
    {
        ClientInfo client = (ClientInfo)clients.elementAt(clientIndex);

            // size of the payload.
        int payloadSize = IPUtils.readLength(packet) - TCPUtils.TCP_DATA_OFFSET;
        int destIP = IPUtils.readDestIP(packet);

        int command = packet.data[TCPUtils.TCP_DATA_OFFSET];
        if (command == MSGProtocol.SEND_ME_A_NUMBER)
        {
            int magicNumber = (int)(Math.random() * 1000000);
            String reply = 
                serviceName + " Server Response: " + responseNumber++ + "\n" +
                "Host     = " + hostModule.parent.getName() + "\n" +
                "Src IP   = " + Utils.ipToString(destIP) + "\n" +
                "SrcPort  = " + port + "\n" +
                "Dst IP   = " + Utils.ipToString(client.srcIP) + "\n" +
                "DstPort  = " + client.srcPort + "\n" +
                "magicNum = " + magicNumber;
            byte data[] = reply.getBytes();

            hostModule.parent.writeToConsole(DeviceEvent.RESPONDING,
                                       "Response: " + "\n" + 
                                       "    Dst: " +
                                       Utils.ipToString(client.srcIP) + "\n" + 
                                       "    Port: " + client.srcPort + "\n" + 
                                       "    Magic Number: " + magicNumber + "\n");

            writePayload(data, data.length, client);
        } else if (command == MSGProtocol.GOOD_BYE)
        {
            closeConnection(clientIndex);
        }
    }
}
