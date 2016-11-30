package com.sri.apps.netsim; 

/**
 * Some functions for doing IP.
 */
public class IPUtils
{
        /**
         * Reads the src ip address of an IP packet.
         */
    public final static int readDestIP(Packet packet, int offset)
    {
        byte data[] = packet.data;
        return ((data[offset + 16] & 0xff) << 24) | 
               ((data[offset + 17] & 0xff) << 16) | 
               ((data[offset + 18] & 0xff) << 8)  |
               (data[offset + 19] & 0xff);
    }

        /**
         * Reads the dest IP address of an IP packet.
         */
    public final static int readSrcIP(Packet packet, int offset)
    {
        byte data[] = packet.data;
        return ((data[offset + 12] & 0xff) << 24) | 
               ((data[offset + 13] & 0xff) << 16) | 
               ((data[offset + 14] & 0xff) << 8)  |
               (data[offset + 15] & 0xff);
    }

        /**
         * Writes the Source ip of the packet.
         */
    public final static void writeSrcIP(Packet packet, int ip, int offset)
    {
        byte data[] = packet.data;
        data[offset + 15] = (byte)(ip & 0xff);
        data[offset + 14] = (byte)((ip >> 8) & 0xff);
        data[offset + 13]  = (byte)((ip >> 16) & 0xff);
        data[offset + 12]  = (byte)((ip >> 24) & 0xff);
    }

        /**
         * Writes the Source ip of the packet.
         */
    public final static void writeDestIP(Packet packet, long ip, int offset)
    {
        byte data[] = packet.data;
        data[offset + 19] = (byte)(ip & 0xff);
        data[offset + 18] = (byte)((ip >> 8) & 0xff);
        data[offset + 17]  = (byte)((ip >> 16) & 0xff);
        data[offset + 16]  = (byte)((ip >> 24) & 0xff);
    }

        /**
         * Set the ethernet type.
         */
    public final static void writeProtocol(Packet packet,
                                           byte type, int offset)
    {
        packet.data[offset + 9] = type;
    }

        /**
         * Get the ip packet type.
         */
    public final static byte readProtocol(Packet packet, int offset)
    {
        return packet.data[offset + 9];
    }
}
