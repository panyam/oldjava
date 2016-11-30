package com.sri.apps.netsim;


/**
 * A class for utilities on UDP packets.
 */
public class UDPUtils
{
        /**
         * The offset in a packet where the UDP info of a
         * packet begins.
         */
    public final static int UDP_OFFSET = 21 + IPUtils.IP_OFFSET;

        /**
         * The length of the header...
         */
    public final static int UDP_HLENGTH = 8;

        /**
         * Offset in the packet where the UDP data begins.
         */
    public final static int UDP_DATA_OFFSET = UDP_OFFSET + UDP_HLENGTH;

        /**
         * Create a UDP packet.
         */
    public final static Packet createPacket(int length)
    {
        Packet out = IPUtils.createPacket(length);
        IPUtils.writeProtocol(out, Protocols.UDP);
        return out;
    }

        /**
         * Get the Ack number of a packet.
         */
    public final static int readLength(Packet packet)
    {
        byte data[] = packet.data;
        return (((data[UDP_OFFSET + 4] & 0xff) << 8) | 
               (data[UDP_OFFSET + 5] & 0xff)) & 0x0000ffff;
    }

        /**
         * Writes the Ack number
         */
    public final static void writeLength(Packet packet, int length)
    {
        byte data[] = packet.data;
        data[UDP_OFFSET + 3] = (byte)(length & 0xff);
        data[UDP_OFFSET + 4] = (byte)((length >> 8) & 0xff);
    }

        /**
         * Gets the destination port of a packet.
         */
    public final static int readDestPort(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[UDP_OFFSET + 2] << 8) |
                 data[UDP_OFFSET + 3]) & 0x0000ffff;
    }

        /**
         * Reads the src port of an packet.
         */
    public final static int readSrcPort(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[UDP_OFFSET] << 8) | data[UDP_OFFSET + 1]) & 0x0000ffff;
    }

        /**
         * Writes the Source port of the packet.
         */
    public final static void writeSrcPort(Packet packet, int sport)
    {
        byte data[] = packet.data;
        data[UDP_OFFSET]     = (byte)((sport >> 8) & 0xff);
        data[UDP_OFFSET + 1] = (byte)(sport & 0xff);
    }

        /**
         * Writes the dest port of the packet.
         */
    public final static void writeDestPort(Packet packet, int dport)
    {
        byte data[] = packet.data;
        data[UDP_OFFSET + 2] = (byte)((dport >> 8) & 0xff);
        data[UDP_OFFSET + 3] = (byte)(dport & 0xff);
    }
}
