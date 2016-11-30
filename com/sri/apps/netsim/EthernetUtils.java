package com.sri.apps.netsim; 

/**
 * A class describing an Ethernet Packet.
 */
public class EthernetUtils
{
    public final static short ETHERNET_II       = 0;
    public final static short ETHERNET_II_RAW   = 1;
    public final static short ETHERNET_II_LLC   = 2;
    public final static short ETHERNET_II_SNAP  = 3;
    public final static short UNKNOWN_ETHERNET  = 4;

        /**
         * Max size of a ethernet packet.  
         * Does not include the headers (14 bytes) and checksum (4 bytes).
         */
    public final static int ETHERNET_LENGTH = 1500;

        /**
         * Reads the src mac address of an ethernet packet.
         */
    public final static long readDestMac(Packet packet, int offset)
    {
        byte data[] = packet.data;
        return ((data[offset + 6] & 0xff) << 40) | 
               ((data[offset + 7] & 0xff) << 32) | 
               ((data[offset + 8] & 0xff) << 24) | 
               ((data[offset + 9] & 0xff) << 16) | 
               ((data[offset + 10] & 0xff) << 8) | 
                (data[offset + 11] & 0xff);
    }

        /**
         * Reads the dest mac address of an ethernet packet.
         */
    public final static long readSrcMac(Packet packet, int offset)
    {
        byte data[] = packet.data;
        return ((data[offset + 0] & 0xff) << 40) | 
               ((data[offset + 1] & 0xff) << 32) | 
               ((data[offset + 2] & 0xff) << 24) | 
               ((data[offset + 3] & 0xff) << 16) | 
               ((data[offset + 4] & 0xff) << 8) | 
                (data[offset + 5] & 0xff);
    }

        /**
         * Writes the Source Mac of the packet.
         */
    public final static void writeSrcMac(Packet packet, long mac, int offset)
    {
        byte data[] = packet.data;
        data[offset + 11] = (byte)(mac & 0xff);
        data[offset + 10] = (byte)((mac >> 8) & 0xff);
        data[offset + 9]  = (byte)((mac >> 16) & 0xff);
        data[offset + 8]  = (byte)((mac >> 24) & 0xff);
        data[offset + 7]  = (byte)((mac >> 32) & 0xff);
        data[offset + 6]  = (byte)((mac >> 40) & 0xff);
    }

        /**
         * Writes the Source Mac of the packet.
         */
    public final static void writeDestMac(Packet packet, long mac, int offset)
    {
        byte data[] = packet.data;
        data[offset + 5] = (byte)(mac & 0xff);
        data[offset + 4] = (byte)((mac >> 8) & 0xff);
        data[offset + 3] = (byte)((mac >> 16) & 0xff);
        data[offset + 2] = (byte)((mac >> 24) & 0xff);
        data[offset + 1] = (byte)((mac >> 32) & 0xff);
        data[offset + 0] = (byte)((mac >> 40) & 0xff);
    }

        /**
         * Set the ethernet type.
         */
    public final static void writeProtocolID(Packet packet,
                                             short type, int offset)
    {
        packet.data[offset + 12] = (byte)((type >> 8)& 0xff);
        packet.data[offset + 13] = (byte)(type & 0xff);
    }

        /**
         * Get the ethernet packet type.
         * Either returns the packet length (less than 1500 bytes)
         * or returns the packet type (ie, SNAP or so on) in which
         * case a packet size of 1500 bytes is assumed.
         */
    public final static short readProtocolID(Packet packet, int offset)
    {
        byte data[] = packet.data;
        short byte34 = (short)(((data[offset + 12] << 8) & data[offset + 13]) & 0x0000ffff);

        if (byte34 < ETHERNET_LENGTH)
        {
                // then just return the ethernet packet length;
            return byte34;
        } else if (data[offset + 14] == 0xaa && 
                    data[offset + 15] == 0xaa && 
                    data[offset + 16] == 0x03)
        {
            return ETHERNET_II_SNAP;
        }
        else return UNKNOWN_ETHERNET;
    }
}
