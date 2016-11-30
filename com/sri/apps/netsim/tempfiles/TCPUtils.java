package com.sri.apps.netsim;

/**
 * A class for utilities on TCP packets.
 */
public class TCPUtils
{
        /**
         * The offset in a packet where the TCP info of a
         * packet begins.
         *
         * Actually this could be 20 OR 24.  But we are keeping it 
         * simple and taking it as 24.  Because in this project
         * we wont be generating any packets that would have a IP Header
         * Length of 24.
         */
    public final static int TCP_OFFSET = 21 + IPUtils.IP_OFFSET;

        /**
         * Once again we are not implementing any options.
         */
    public final static int TCP_HLENGTH = 24;

        /**
         * Offset in the packet where the TCP data begins.
         */
    public final static int TCP_DATA_OFFSET = TCP_OFFSET + TCP_HLENGTH;

        /**
         * Position where the flags can be found.
         */
    public final static int TCP_FLAGS_OFFSET = TCP_OFFSET + 13;

        /**
         * TCP flags Masks
         */
    public final static byte URG_FLAG = 32;     // 100000
    public final static byte ACK_FLAG = 16;     // 010000
    public final static byte PSH_FLAG = 8;      // 001000
    public final static byte RST_FLAG = 4;      // 000100
    public final static byte SYN_FLAG = 2;      // 000010
    public final static byte FIN_FLAG = 1;      // 000001

        /**
         * Create a TCP packet.
         */
    public static Packet createPacket(int length)
    {
        Packet out = IPUtils.createPacket(length);
        IPUtils.writeProtocol(out, Protocols.TCP);
        return out;
    }

        /**
         * Get the sequence number of a packet.
         */
    public int readSequenceNumber(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[TCP_OFFSET + 4] & 0xff) << 24) | 
               ((data[TCP_OFFSET + 5] & 0xff) << 16) | 
               ((data[TCP_OFFSET + 6] & 0xff) << 8) | 
               (data[TCP_OFFSET + 7] & 0xff);
    }

        /**
         * Writes the sequence number
         */
    public final static void writeSequenceNumber(Packet packet, int seqNum)
    {
        byte data[] = packet.data;
        data[TCP_OFFSET + 4] = (byte)(seqNum & 0xff);
        data[TCP_OFFSET + 5] = (byte)((seqNum >> 8) & 0xff);
        data[TCP_OFFSET + 6] = (byte)((seqNum >> 16) & 0xff);
        data[TCP_OFFSET + 7] = (byte)((seqNum >> 24) & 0xff);
    }

        /**
         * Get the Ack number of a packet.
         */
    public int readAckNumber(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[TCP_OFFSET + 8] & 0xff) << 24) | 
               ((data[TCP_OFFSET + 9] & 0xff) << 16) | 
               ((data[TCP_OFFSET + 10] & 0xff) << 8) | 
               (data[TCP_OFFSET + 11] & 0xff);
    }

        /**
         * Writes the Ack number
         */
    public final static void writeAckNumber(Packet packet, int ackNum)
    {
        byte data[] = packet.data;
        data[TCP_OFFSET + 8] = (byte)(ackNum & 0xff);
        data[TCP_OFFSET + 9] = (byte)((ackNum >> 8) & 0xff);
        data[TCP_OFFSET + 10] = (byte)((ackNum >> 16) & 0xff);
        data[TCP_OFFSET + 11] = (byte)((ackNum >> 24) & 0xff);
    }

        /**
         * Gets the destination port of a packet.
         */
    public final static int readDestPort(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[TCP_OFFSET + 2] << 8) |
                 data[TCP_OFFSET + 3]) & 0x0000ffff;
    }

        /**
         * Reads the src port of an packet.
         */
    public final static int readSrcPort(Packet packet)
    {
        byte data[] = packet.data;
        return ((data[TCP_OFFSET] << 8) | data[TCP_OFFSET + 1]) & 0x0000ffff;
    }

        /**
         * Writes the Source port of the packet.
         */
    public final static void writeSrcPort(Packet packet, int sport)
    {
        byte data[] = packet.data;
        data[TCP_OFFSET]     = (byte)((sport >> 8) & 0xff);
        data[TCP_OFFSET + 1] = (byte)(sport & 0xff);
    }

        /**
         * Writes the dest port of the packet.
         */
    public final static void writeDestPort(Packet packet, int dport)
    {
        byte data[] = packet.data;
        data[TCP_OFFSET + 2] = (byte)((dport >> 8) & 0xff);
        data[TCP_OFFSET + 3] = (byte)(dport & 0xff);
    }

        /**
         * Set a given flag.
         */
    public final static void setFlag(Packet packet, byte flag)
    {
        packet.data[TCP_FLAGS_OFFSET] |= flag;
    }

        /**
         * Tells if a certain flag is set.
         */
    public final static boolean isFlagSet(Packet packet, byte flag)
    {
        return (packet.data[TCP_FLAGS_OFFSET] & flag) != 0;
    }
}
