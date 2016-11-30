package com.sri.apps.netsim;
/**
 * A class for doing processing on IP packets.
 */
public class IPUtils
{
        /**
         * The offset in a packet where the IP info of a
         * packet begins.
         */
    public final static int IP_OFFSET = 14;

        /**
         * The byte in the packet which determines the type
         * of IP protocol.
         */
    public final static int PROTOCOL_OFFSET = IP_OFFSET + 10;

        /**
         * Create a IP packet.
         */
    /*public static Packet createPacket(int length)
    {
        Packet out = new Packet(length);

            // write the protocol value
        out.data[12] = 0x08;
        out.data[13] = 0x00;

            // set the version and the header length...
            // Version = 4
            // IHL = 5
        out.data[IP_OFFSET] = (4 << 4) | 5;
        EthernetUtils.writeProtocol(out, Protocols.IP);
        return out;
    }*/

        /**
         * Get the length.
         */
    public static int readLength(Packet packet)
    {
        return (((packet.data[IP_OFFSET + 2] & 0xff) << 8) | 
                 (packet.data[IP_OFFSET + 3] & 0xff)) & 0x0000ffff;
    }

        /**
         * Write the length.
         */
    public static void writeLength(Packet packet, int length)
    {
        packet.data[IP_OFFSET + 2] = (byte)((length >> 8) & 0xff);
        packet.data[IP_OFFSET + 3] = (byte)(length & 0xff);
    }

        /**
         * Sets the protocol.
         */
    public static void writeProtocol(Packet packet, byte protocol)
    {
        packet.data[PROTOCOL_OFFSET] = protocol;
    }

        /**
         * Gets a packets protocol.
         */
    public static byte readProtocol(Packet packet)
    {
        return packet.data[PROTOCOL_OFFSET];
    }

        /**
         * Reads the src IP address of an ethernet packet.
         */
    public final static int readDestIP(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[IP_OFFSET + 17] & 0xff) << 24) | 
               ((data[IP_OFFSET + 18] & 0xff) << 16) | 
               ((data[IP_OFFSET + 19] & 0xff) << 8) | 
               (data[IP_OFFSET + 20] & 0xff);
    }

        /**
         * Writes the dest IPAddress of the packet.
         */
    public final static void writeDestIP(Packet packet, long IP)
    {
        byte data[] = packet.data;
        data[IP_OFFSET + 17] = (byte)((IP >> 24) & 0xff);
        data[IP_OFFSET + 18] = (byte)((IP >> 16) & 0xff);
        data[IP_OFFSET + 19] = (byte)((IP >> 8) & 0xff);
        data[IP_OFFSET + 20] = (byte)(IP & 0xff);
    }

        /**
         * Reads the dest IP address of an ethernet packet.
         */
    public final static int readSrcIP(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[IP_OFFSET + 13] & 0xff) << 24) | 
               ((data[IP_OFFSET + 14] & 0xff) << 16) | 
               ((data[IP_OFFSET + 15] & 0xff) << 8) | 
               (data[IP_OFFSET + 16] & 0xff);
    }

        /**
         * Writes the Source IPAddress of the packet.
         */
    public final static void writeSrcIP(Packet packet, long IP)
    {
        byte data[] = packet.data;
        data[IP_OFFSET + 16] = (byte)(IP & 0xff);
        data[IP_OFFSET + 15] = (byte)((IP >> 8) & 0xff);
        data[IP_OFFSET + 14] = (byte)((IP >> 16) & 0xff);
        data[IP_OFFSET + 13] = (byte)((IP >> 24) & 0xff);
    }
}
