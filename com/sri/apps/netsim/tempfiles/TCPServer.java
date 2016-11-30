package com.sri.apps.netsim;

import java.util.*;

/**
 * A TCP Server object.
 * In our simplified server object, we dont send out acks as
 * we assume all links are lossless and all packets will be 
 * delivered.    The only exception is for syn packets.  
 * Because, syn packets may not reach the destination in 
 * only one case.  They are being blocked by firewalls.  In which
 * case we WANT to ensure that an ack is recieved before we send
 * out any more packets.
 *
 * Plus we dont fragment ANY packet for two reasons
 * 1) We assume all packets are small.  
 * 2) Too much work.  This is a project for simulating
 *    firewalls, not on TCP or other network protocols. 
 *
 * Fragmentation will be done later.
 */
public abstract class TCPServer extends PortServer
{

        /**
         * This is the state a client goes to
         * when it is first created because a 
         * client is created only when we see 
         * a packet with the SYN flag set.
         */
    public final static byte SYN_RECIEVED       = 1;

        /**
         * When we see a SYN packet, we send out
         * an acknowledgement.
         */
    public final static byte SYN_ACK_SENT       = 2;

        /**
         * Holds info about each client.
         */
    Vector clients = new Vector();

        /**
         * IP Address of the host
         * which is running this process.
         */
    public int ourIP = 0;

        /**
         * Constructor.
         */
    public TCPServer(HostModule host, String name, int port)
    {
        super(host, name, port);
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
            isRunning = false;

                // now remove all clients as well...
            for (int i = clients.size() - 1;i >= 0;i --)
            {
                closeConnection(i);
            }
        }
    }

        /**
         * Given a destination port, returns the index
         * of the client, if it exists.
         * This is used to determine what to do with 
         * a packet that we recieve.
         */
    public int getClient(int srcIP, int sport)
    {
        for (int i = 0, nc = clients.size(); i < nc; i++)
        {
            ClientInfo client = (ClientInfo)clients.elementAt(i);
            if (client.srcPort == sport && client.srcIP == srcIP)
            {
                return i;
            }
        }
        return -1;
    }

        /**
         * Called when a packet arrives for this service.
         */
    public synchronized void processPacket(Packet packet)
        throws Exception
    {
        Device parentDevice = hostModule.parent;

            // if we are not running then dont do anything...
        if ( ! isRunning)
        {

            parentDevice.writeToConsole(DeviceEvent.NO_SERVICE, 
                           "No service running " +
                           "on port: " + port);

            return ;
        }
        int srcPort = TCPUtils.readSrcPort(packet);
        int srcIP = IPUtils.readSrcIP(packet);
        int clientIndex = getClient(srcIP, srcPort);

        ourIP = IPUtils.readDestIP(packet);

            // if the synflag of the packet is set
            // and if the client index is valid then 
            // ignore the packet because it is invalid...
            // basically this is what happens with syn-flooding
            //
            // another check we should be doing is to look
            // for dodgy flag optiosn like syn and fin set
            // or so on.  Once again, this is not an issue
            // as we are simulating firewalls rather than
            // gateways or host functionality
        if (TCPUtils.isFlagSet(packet, TCPUtils.SYN_FLAG))
        {
            if (clientIndex >= 0) return ;

            ClientInfo newClient =
                new ClientInfo(EthernetUtils.readSrcMac(packet),
                               srcIP,
                               srcPort);

            clients.addElement(newClient);

            parentDevice.writeToConsole(DeviceEvent.RECEIVED_NEW_HOST, 
                                        "    Src: " +
                                        Utils.ipToString(srcIP) +
                                                "        (" + srcIP + ")\n" +
                                        "    Port: " + srcPort  + "\n");

            Packet synAckPacket = TCPUtils.createPacket(128);

                // basically the dstMac and the srcMac would
                // be reverse of the the packet we 
                // recieved...
            EthernetUtils.writeDestMac(synAckPacket, 
                                      EthernetUtils.readSrcMac(packet));
            EthernetUtils.writeSrcMac(synAckPacket,
                                      EthernetUtils.readDestMac(packet));

                // same deal with the IP addresses

            //IPUtils.writeSrcIP(synAckPacket, ourIP);
            IPUtils.writeDestIP(synAckPacket, srcIP);

            IPUtils.writeLength(synAckPacket,
                                TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH);

            TCPUtils.writeSrcPort(synAckPacket, port);
            TCPUtils.writeDestPort(synAckPacket, srcPort);

                // set the syn and ack flags...
            TCPUtils.setFlag(synAckPacket, TCPUtils.SYN_FLAG);
            TCPUtils.setFlag(synAckPacket, TCPUtils.ACK_FLAG);

            synAckPacket.packetModule = hostModule;
            hostModule.parent.transmitPacket(synAckPacket);

            parentDevice.writeToConsole(DeviceEvent.SYN_ACK_SENT, null);

        } else if (TCPUtils.isFlagSet(packet, TCPUtils.FIN_FLAG))
        {
            if (clientIndex < 0) return ;

                // then remove the client from our list...
            clients.removeElementAt(clientIndex);
        } else if (clientIndex >= 0)
        {
                // then let the client
                // take care of this packet...
                // this means that at this stage, 
                // this packet is part the actual
                // session itself...
            handleSessionPacket(packet, clientIndex);
        }
    }

        /**
         * Close the given client.
         */
    public void closeConnection(int which)
        throws Exception
    {
        Packet finPacket = TCPUtils.createPacket(128);
        Device parentDevice = hostModule.parent;
        ClientInfo client = (ClientInfo)clients.elementAt(which);

            // basically the dstMac and the srcMac would
            // be reverse of the the packet we 
            // recieved...
        EthernetUtils.writeDestMac(finPacket, client.srcMac);
        //EthernetUtils.writeSrcMac(finPacket, parentDevice.interfaces[0].macAddress);

            // same deal with the IP addresses
        IPUtils.writeDestIP(finPacket, client.srcIP);
        //IPUtils.writeSrcIP(finPacket, routerInterface.ipAddress);

        IPUtils.writeLength(finPacket,
                            TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH);

        TCPUtils.writeSrcPort(finPacket, port);
        TCPUtils.writeDestPort(finPacket, client.srcPort);

            // set the syn and ack flags...
        TCPUtils.setFlag(finPacket, TCPUtils.FIN_FLAG);

        finPacket.packetModule = hostModule;
        hostModule.parent.transmitPacket(finPacket);
        clients.removeElementAt(which);
    }

        /**
         * Appends the payload to to the TCP packet
         * and writes it to the interface.  Happesn
         * only if we are connected
         */
    public void writePayload(byte data[], int length, ClientInfo client)
        throws Exception
    {
        int l = length + TCPUtils.TCP_OFFSET + TCPUtils.TCP_HLENGTH;
        Device parentDevice = hostModule.parent;
        Packet outPacket = TCPUtils.createPacket(l + 10);

        IPUtils.writeLength(outPacket, l);

        //EthernetUtils.writeSrcMac(outPacket, routerInterface.macAddress);
        EthernetUtils.writeDestMac(outPacket, client.srcMac);

        //IPUtils.writeSrcIP(outPacket, ourIP);
        IPUtils.writeDestIP(outPacket, client.srcIP);

        TCPUtils.writeSrcPort(outPacket, this.port);
        TCPUtils.writeDestPort(outPacket, client.srcPort);

        System.arraycopy(data, 0,
                         outPacket.data, TCPUtils.TCP_DATA_OFFSET,
                         length);

        outPacket.packetModule = hostModule;
        hostModule.parent.transmitPacket(outPacket);
    }

        /**
         * Basically this is what is extended by each protocol.
         */
    protected abstract void handleSessionPacket(Packet packet, int clientIndex)
        throws Exception;

    class ClientInfo 
    {
        long srcMac;

            /**
             * Source port of the client.
             */
        int srcPort;

            /**
             * IP of this client.
             */
        int srcIP;

            /**
             * The state in which this client is in.
             */
        int state = SYN_ACK_SENT;

            /**
             * The time when the last packet was recieved.
             */
        //long lastPacketTime = calendar.getTimeInMillis();

            /**
             * Constructor.
             */
        public ClientInfo(long macAddress, int srcIP, int srcPort)
        {
            this.srcIP = srcIP;
            this.srcPort = srcPort;
            this.srcMac = macAddress;
        }
    }
}
