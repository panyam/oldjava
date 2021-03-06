package com.sri.apps.netsim; 


/**
 * Basically this class is a connection or a TCP session that is
 * generated by a host.
 * This only represents a TCP client object.  So this can be extended by
 * each protocol as we want.
 */
public class PacketClient extends PortClient
{
        /**
         * UDP or TCP packet?
         */
    boolean asUDP;

        /**
         * Number of requests to send to the server.
         */
    int nRequests = 5;

        /**
         * Request counter
         */
    int currRequest = 0;

        /**
         * Constructor
         */
    public PacketClient(HostModule host, int numRequests, boolean asUDP)
    {
        super(host);
        this.nRequests = numRequests;
        this.asUDP = asUDP;
    }

        /**
         * Sends packets to a server on a given destination.
         */
    public synchronized int connectTo(long destMac, int serverIP, int dport)
        throws Exception
    {
        this.serverIP = serverIP;
        serverPort = dport;
        this.destMac = destMac;

        currRequest = 0;

            // send a requestion to the server with a 
            // SEND_ME_A_NUMBER command
        writePayload(MSGProtocol.SEND_ME_A_NUMBER);

        return 0;
    }

        /**
         * Called when a packet arrives for this service.
         */
    public synchronized void processPacket(Packet packet)
        throws Exception
    {
        int srcPort = TCPUtils.readSrcPort(packet);
        int serverIP = IPUtils.readSrcIP(packet);

            // check where the packet is coming from
            // could be dodgy packets coming from anywhere
        if (this.serverIP != serverIP ||
            this.serverPort != srcPort)
        {
            return ;
        }

            // then let the client
            // take care of this packet...
            // this means that at this stage, 
            // this packet is part the actual
            // session itself...

            // size of the payload.
        int offset = 0;

        if (asUDP)
        {
            offset = UDPUtils.UDP_DATA_OFFSET;
        } else
        {
            offset = TCPUtils.TCP_DATA_OFFSET;
        }

        int payloadSize = IPUtils.readLength(packet) - offset;
        String payLoad = new String(packet.data, offset, payloadSize);
        Device parentDevice = hostModule.parent;

        parentDevice.writeToConsole(DeviceEvent.GOT_REPLY_FROM_SERVER,
                                    payLoad);

        if (currRequest < nRequests)
        {
                // send another request...
            writePayload(MSGProtocol.SEND_ME_A_NUMBER);
        } else 
        {
                // then kill the application..
                // so it can be removed from the host
            hostModule.removeClient(this);
        }
        currRequest++;
    }

        /**
         * Write the payload with a single byte.
         * This is totally specialised for this protocol
         * but what the heck.
         */
    public void writePayload(byte byteVal)
        throws Exception
    {
        int l = 0;
        Packet outPacket = null;
        if (asUDP)
        {
            l = 1 + UDPUtils.UDP_OFFSET + UDPUtils.UDP_HLENGTH;
            outPacket = UDPUtils.createPacket(l + 5);

            UDPUtils.writeSrcPort(outPacket, this.port);
            UDPUtils.writeDestPort(outPacket, serverPort);

            outPacket.data[UDPUtils.UDP_DATA_OFFSET] = byteVal;
        } else
        {
            l = 1 + TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH;
            outPacket = TCPUtils.createPacket(l + 5);

            TCPUtils.writeSrcPort(outPacket, this.port);
            TCPUtils.writeDestPort(outPacket, serverPort);

            outPacket.data[TCPUtils.TCP_DATA_OFFSET] = byteVal;
        }

        IPUtils.writeLength(outPacket, l);

        //EthernetUtils.writeSrcMac(outPacket, routerInterface.macAddress);
        EthernetUtils.writeDestMac(outPacket, destMac);

        //IPUtils.writeSrcIP(outPacket, routerInterface.ipAddress);
        IPUtils.writeDestIP(outPacket, serverIP);

        outPacket.packetModule = hostModule;
        hostModule.parent.transmitPacket(outPacket);
    }

        /**
         * Appends the payload to to the TCP packet
         * and writes it to the interface.  Happesn
         * only if we are connected
         */
    public void writePayload(byte data[], int length)
        throws Exception
    {
        int l = 0;
        Packet outPacket = null;
        if (asUDP)
        {
            l = length + UDPUtils.UDP_OFFSET + UDPUtils.UDP_HLENGTH;
            outPacket = UDPUtils.createPacket(l + 5);

            UDPUtils.writeSrcPort(outPacket, this.port);
            UDPUtils.writeDestPort(outPacket, serverPort);

            System.arraycopy(data, 0,
                             outPacket.data, UDPUtils.UDP_DATA_OFFSET,
                             length);
        } else
        {
            l = 1 + TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH;
            outPacket = TCPUtils.createPacket(l + 5);

            TCPUtils.writeSrcPort(outPacket, this.port);
            TCPUtils.writeDestPort(outPacket, serverPort);

            System.arraycopy(data, 0,
                             outPacket.data, TCPUtils.TCP_DATA_OFFSET,
                             length);

        }

        IPUtils.writeLength(outPacket, l);

        //EthernetUtils.writeSrcMac(outPacket, routerInterface.macAddress);
        EthernetUtils.writeDestMac(outPacket, destMac);
        
        //IPUtils.writeSrcIP(outPacket, routerInterface.ipAddress);
        IPUtils.writeDestIP(outPacket, serverIP);

        outPacket.packetModule = hostModule;
        hostModule.parent.transmitPacket(outPacket);
    }
}
