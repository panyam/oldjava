package com.sri.apps.netsim; 

/**
 * A class describing a Packet.  From a simulator point of view, a packet
 * is also an event hence the extension of the SimEvent class.
 */
public class Packet extends SimEvent
{
        /**
         * Hard to imagine there being more than 8 layers
         * or drivers the packet would have to traverse.
         */
    public ProtocolDriver drivers[] = new ProtocolDriver[8];

        /**
         * driverOffset[i] tells where in the packet, the header 
         * of the driver at layer i begins.
         * The constraint for this is:
         * driverOffset[i] = driverOffset[i - 1] + headerLength[i - 1];
         */
    public short driverOffset[] = new short[8];

        /**
         * headerLength[i] tells the header length of the protocol driver
         * at level i.
         */
    public short headerLength[] = new short[8];

        /**
         * The interface from which the packet was recieved.
         * if negative then not yet set.
         */
    protected NetworkInterface inNic = null;

    public final static int ALL_NICS    = -1;
    public final static int UNKNOWN_NIC = -2;

        /**
         * The interface to which the packet is to be written.
         * if the outNic is -1 then the packet will be flooded
         * via all nics.  For any other negative number, then 
         * it means this value is undefined.
         * This value is set by the packet module that generates
         * the packet.  If it sets this value to a negative number 
         * other than ALL_NICS, then it means that the module 
         * does not know which nic it should send it through.
         *
         * The other possibility is if you want to send a packet 
         * to all NICs except one particular nic.  This NIC id
         * will be specified by a number less than or = -2.  So
         * to exclude nic N, set this value to -(N + 3);
         */
    protected NetworkInterface outNic = null; //UNKNOWN_NIC;

        /**
         * The data in the Packet.
         */
    public byte data[] = null;

        /**
         * Length of the payload.
         */
    public int length = 0;

        /**
         * Constructor.
         */
    public Packet()
    {
    }

        /**
         * Constructor.
         */
    public Packet(byte data[], int len)
    {
        this.data = data;
        this.length = Math.min(len, data == null ? 0 : data.length);
    }

        /**
         * Set the packet size.
         */
    public void setSize(int size)
    {
        if (data == null) 
        {
            data = new byte[size];
            length = 0;
        } else if (data.length < size)
        {
            byte d2[] = data;
            data = new byte[size];
            System.arraycopy(d2, 0, data, 0, d2.length);

                // here the length is not changed...
        }
    }


        /**
         * Constructor.
         */
    public Packet(int len)
    {
        data = new byte[len];
    }

        /**
         * Writes an short to the byte list.
         */
    public void putShort(int value, int offset)
    {
        data[offset] = (byte)((value >> 8) & 0xff);
        data[offset + 1] = (byte)(value & 0xff);
    }

        /**
         * Writes an int to the byte list.
         */
    public void putInt(int value, int offset)
    {
        offset += 4;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public void putMacAddress(long value, int offset)
    {
        offset += 6;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public void putLong(long value, int offset)
    {
        offset += 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
        data[--offset] = (byte)(value & 0xff);  value >>= 8;
    }

        /**
         * Reassembles a short integer (2 bytes) out of a byte 
         * list from the given offset.
         */
    public int getShort(int offset)
    {
        return ((data[offset++] & 0xff) << 8) | 
               (data[offset] & 0xff);
    }

        /**
         * Reassembles a integer (4 bytes) out of a byte 
         * list from the given offset.
         */
    public int getInteger(int offset)
    {
        return ((data[offset++] & 0xff) << 24) | 
               ((data[offset++] & 0xff) << 16) | 
               ((data[offset++] & 0xff) << 8) | 
               (data[offset] & 0xff);
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public long getMacAddress(int offset)
    {
        return ((data[offset++] & 0xff) << 40) | 
               ((data[offset++] & 0xff) << 32) | 
               ((data[offset++] & 0xff) << 24) | 
               ((data[offset++] & 0xff) << 16) | 
               ((data[offset++] & 0xff) << 8) | 
               (data[offset] & 0xff);
    }

        /**
         * Reassembles a long integer (8 bytes) out of
         * a byte list from the given offset.
         */
    public long getLong(int offset)
    {
        return ((data[offset++] & 0xff) << 56) | 
               ((data[offset++] & 0xff) << 48) | 
               ((data[offset++] & 0xff) << 40) | 
               ((data[offset++] & 0xff) << 32) | 
               ((data[offset++] & 0xff) << 24) | 
               ((data[offset++] & 0xff) << 16) | 
               ((data[offset++] & 0xff) << 8) | 
               (data[offset] & 0xff);
    }
}
