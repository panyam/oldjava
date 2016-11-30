

/**
 * Describes an integer range.
 */
public class IntRange
{
        /**
         * The lo and hi values in the range.
         */
    public int lo, hi;

        /**
         * Constructor.
         */
    public IntRange()
    {
        this(0, 0);
    }

        /**
         * Constructor.
         */
    public IntRange(int lo, int hi)
    {
        this.lo = lo;
        this.hi = hi;
    }
}
