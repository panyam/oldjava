

/**
 * Defines a set of bits.
 */
public class Bits
{
    protected final static byte oneAtPosition[] = new byte[]
    {
        (byte)((0x01 << 0) & 0xff),
        (byte)((0x01 << 1) & 0xff),
        (byte)((0x01 << 2) & 0xff),
        (byte)((0x01 << 3) & 0xff),
        (byte)((0x01 << 4) & 0xff),
        (byte)((0x01 << 5) & 0xff),
        (byte)((0x01 << 6) & 0xff),
        (byte)((0x01 << 7) & 0xff),
    };
    protected final static byte zeroAtPosition[] = new byte[]
    {
        (byte)(oneAtPosition[0] ^ 0xff),
        (byte)(oneAtPosition[1] ^ 0xff),
        (byte)(oneAtPosition[2] ^ 0xff),
        (byte)(oneAtPosition[3] ^ 0xff),
        (byte)(oneAtPosition[4] ^ 0xff),
        (byte)(oneAtPosition[5] ^ 0xff),
        (byte)(oneAtPosition[6] ^ 0xff),
        (byte)(oneAtPosition[7] ^ 0xff),
    };

        /**
         * Number of bits in this bit list.
         */
    protected int nBits = 0;

        /**
         * The byte array storing the bits.
         */
    protected byte bits[];

        /**
         * Constructor.
         */
    public Bits(int nBits)
    {
        setSize(nBits);
    }

        /**
         * Set the size of this bitset.
         */
    public void setSize(int nBits)
    {
        if (bits == null || bits.length < (1 + (nBits >> 3)))
        {
            bits = new byte[(1 + (nBits >> 3))];
                // reset all bits to 0
        }
        for (int i = 0;i < bits.length;i++) bits[i] = 0;
    }

        /**
         * Size of this bitlist.
         */
    public int size()
    {
        return nBits;
    }

        /**
         * Sets the value of a given bit.
         */
    public void setBit(int index, boolean value)
    {
        if (value)
        {
            bits[(index >> 3)] |= oneAtPosition[index & 3];
        } else
        {
            bits[(index >> 3)] &= zeroAtPosition[index & 3];
        }
    }

        /**
         * gets the value of a given bit.
         */
    public boolean getBit(int index)
    {
        return (bits[(index >> 3)] & oneAtPosition[index & 3]) != 1;
    }
}
