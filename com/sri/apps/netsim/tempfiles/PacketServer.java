package com.sri.apps.netsim; 

import java.util.*;

/**
 * A Basic Server.
 * This does not do sessions.  Basicaly packets
 * are generated (either TCP or UDP) and just thrown
 * no SYNs no ACKs, nothing...
 */
public class PacketServer extends PortServer
{
        /**
         * The response number of the server.
         */
    int responseNumber = 0;

        /**
         * Tells if its a UDP or a TCP server.
         */
    boolean asUDP = false;

        /**
         * IP Address of the host
         * which is running this process.
         */
    public int ourIP = 0;

        /**
         * Constructor.
         */
    public PacketServer(HostModule host, String name, int port, boolean asUDP)
    {
        super(host, name, port);
        this.asUDP = asUDP;
    }

        /**
         * Start the service.
         */
    public void start()
    {
        isRunning = true;
    }

        /**
         * Stop the service.
         */
    public void stop()
        throws Exception
    {
        if (isRunning)
        {
            responseNumber = 0;
            isRunning = false;
        }
    }

        /**
         * Called when a packet arrives for this service.
         */
    public synchronized void processPacket(Packet packet)
        throws Exception
    {
            // if we are not running then dont do anything...
        if ( ! isRunning)
        {
            hostModule.parent.writeToConsole(DeviceEvent.NO_SERVICE, 
                           "No service running " +
                           "on port: " + port);

            return ;
        }

        handleSessionPacket(packet);
    }

        /**
         * Appends the payload to to the TCP packet
         * and writes it to the interface.  Happesn
         * only if we are connected
         */
    public void writePayload(byte data[], int length,
                             long destMac, int destIP, int destPort)
        throws Exception
    {
        int l = length + TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH;

        Packet outPacket = null;
        int dataOffset = 0;
        if (asUDP)
        {
            outPacket = UDPUtils.createPacket(l + 10);
            dataOffset = UDPUtils.UDP_DATA_OFFSET;
        } else
        {
            outPacket = TCPUtils.createPacket(l + 10);
            dataOffset = TCPUtils.TCP_DATA_OFFSET;
        }

        IPUtils.writeLength(outPacket, l);

        //EthernetUtils.writeSrcMac(outPacket, routerInterface.macAddress);
        EthernetUtils.writeDestMac(outPacket, destMac);

        //IPUtils.writeSrcIP(outPacket, ourIP);
        IPUtils.writeDestIP(outPacket, destIP);

        TCPUtils.writeSrcPort(outPacket, this.port);
        TCPUtils.writeDestPort(outPacket, destPort);

        System.arraycopy(data, 0, outPacket.data, dataOffset, length);

        outPacket.packetModule = hostModule;
        hostModule.parent.transmitPacket(outPacket);
    }

        /**
         * Basically this is what is extended by each protocol.
         */
    protected void handleSessionPacket(Packet packet)
        throws Exception
    {
        int srcIP = IPUtils.readSrcIP(packet);
        int srcPort = 0;
        int offset = 0;
        if (asUDP)
        {
            srcPort = UDPUtils.readSrcPort(packet);
            offset = UDPUtils.UDP_DATA_OFFSET;
        } else
        {
            srcPort = TCPUtils.readSrcPort(packet);
            offset = TCPUtils.TCP_DATA_OFFSET;
        }

        ourIP = IPUtils.readDestIP(packet);

            // size of the payload.
        int payloadSize = IPUtils.readLength(packet) - offset;

        int command = packet.data[offset];
        if (command == MSGProtocol.SEND_ME_A_NUMBER)
        {
            int magicNumber = (int)(Math.random() * 1000000);
            String reply = 
                serviceName +
                " Server Response: " + (responseNumber++) + "\n" +
                "Host       = " + hostModule.parent.getName() + "\n" +
                "Protocol   = " + (asUDP ? "udp" : "tcp") + "\n" +
                "Src IP     = " + Utils.ipToString(ourIP) + "\n" +
                "SrcPort    = " + port + "\n" +
                "Dst IP     = " + Utils.ipToString(srcIP) + "\n" +
                "DstPort    = " + srcPort + "\n" +
                "magicNum   = " + magicNumber;
            byte data[] = reply.getBytes();

            hostModule.parent.writeToConsole(DeviceEvent.RESPONDING,
                                       "Response: " + "\n" + 
                                       "    Dst: " +
                                            Utils.ipToString(srcIP) + "\n" + 
                                       "    Port: " + srcPort + "\n" + 
                                       "    Magic Number: " + magicNumber + "\n");

            writePayload(data, data.length,
                         EthernetUtils.readSrcMac(packet), srcIP, srcPort);
        }
    }
}
