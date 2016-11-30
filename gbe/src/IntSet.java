

/**
 * A class that holds set of ints.
 */
public abstract class IntSet
{
        /**
         * Number of items in this set.
         */
    protected int nValues = 0;

        /**
         * Constructor.
         */
    public IntSet(int nVals)
    {
        this.nValues = nVals;
    }

        /**
         * Adds a int to this set.
         */
    public abstract void add(int intID);

        /**
         * Clears the entries in this set.
         */
    public abstract void clearSet();

        /**
         * Gets an integer at the given index.
         */
    public abstract int get(int index);

        /**
         * Tells if this integer is in here or not.
         */
    public abstract boolean contains(int intID);

        /**
         * Returns the union of this set and another set.
         */
    public abstract IntSet union(IntSet another);

        /**
         * Unions this list with another.
         */
    public abstract void unionWith(IntSet another);

        /**
         * Returns the maximum value in the set.
         */
    public abstract int maxValue();

        /**
         * Returns the maximum value in the set.
         */
    public abstract int minValue();

        /**
         * Returns the size of this set.
         */
    public int size()
    {
        return nValues;
    }

        /**
         * Returns the hashcode.
         */
    public int hashCode()
    {
        int hash = 0;
        for (int i = 0;i < nValues;i++) hash += get(i);
        return hash;
    }

        /**
         * Returns a copy of this object.
         */
    public abstract IntSet makeCopy();
}
