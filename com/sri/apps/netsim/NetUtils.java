package com.sri.apps.netsim;

/**
 * A few utilities to do some nifty functions on basic packets
 * and so on.
 */
public class NetUtils
{
        /**
         * Few defined Subnet and Wildcard masks.
         */
    public final static int SUBNET_MASK[];

    
        /**
         * Wild card masks.
         */
    public final static int WILDCARD_MASK[];

    static
    {
        SUBNET_MASK = new int[33];
        WILDCARD_MASK = new int[33];
        for (int i = 0;i < SUBNET_MASK.length;i++)
        {
            SUBNET_MASK[i] = 0xffffffff << (32 - i);
            WILDCARD_MASK[i] = 0xffffffff >> i;
        }
    }


        /**
         * Broadcast IP address.
         */
    public final static int BROADCAST_IP  = 0xffffffff;

        /**
         * The broadcast mac address.
         */
    public final static long BROADCAST_MAC = 0x0000ffffffffffffl;

        /**
         * Bit masks for extracting parts of an integer
         */
    public final static long INT_BYTE1_MASK = 0x000000ff;
    public final static long INT_BYTE2_MASK = 0x0000ff00;
    public final static long INT_BYTE3_MASK = 0x00ff0000;
    public final static long INT_BYTE4_MASK = 0xff000000;

        /**
         * Bit masks for extracting parts of a long integer.
         */
    public final static long LONG_BYTE1_MASK = 0x00000000000000ffl;
    public final static long LONG_BYTE2_MASK = 0x000000000000ff00l;
    public final static long LONG_BYTE3_MASK = 0x0000000000ff0000l;
    public final static long LONG_BYTE4_MASK = 0x00000000ff000000l;
    public final static long LONG_BYTE5_MASK = 0x000000ff00000000l;
    public final static long LONG_BYTE6_MASK = 0x0000ff0000000000l;
    public final static long LONG_BYTE7_MASK = 0x00ff000000000000l;
    public final static long LONG_BYTE8_MASK = 0xff00000000000000l;

        /**
         * Writes a short integer to the byte list.
         */
    public static void putShort(byte list[], int value, int offset)
    {
        offset += 2;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Writes an int to the byte list.
         */
    public static void putInt(byte list[], int value, int offset)
    {
        offset += 4;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public static void putMacAddress(byte list[], long value, int offset)
    {
        offset += 6;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public static void putLong(byte list[], long value, int offset)
    {
        offset += 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
        list[--offset] = (byte)(value & 0xff);  value >>= 8;
    }
}
