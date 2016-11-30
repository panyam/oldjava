

/**
 * An Intset implementation using sorted set of values.
 * This is efficient interms of space but for a dense DFA, this may not be
 * as efficient as the Bit based implementation.
 */
public class IntSetArrayImpl extends IntSet
{
        /**
         * The values for the array.
         */
    protected int values[] = null;

        /**
         * Constructor.
         */
    public IntSetArrayImpl()
    {
        this(3);
    }

        /**
         * Constructor.
         */
    public IntSetArrayImpl(int nValues)
    {
        super(0);
        values = new int[nValues];
    }

        /**
         * Adds a int to this set.
         */
    public void add(int val)
    {
        int index = 0;

            // do we need to resize?
        if (nValues >= values.length)
        {
            int v2[] = values;
            values = new int[nValues + 5];
            System.arraycopy(v2, 0, values, 0, nValues);
        }

        for (;index < nValues && values[index] > val;index++);

            // if already exists then quit
        if (values[index] == val) return ;

            // otherwise insert
        for (int i = nValues;i > index;i--) values[i] = values[i - 1];

        values[index] = val;
    }

        /**
         * Clears the entries in this set.
         */
    public void clearSet()
    {
        nValues = 0;
    }

        /**
         * Gets an integer at the given index.
         */
    public int get(int index)
    {
        return values[index];
    }

        /**
         * Tells if this integer is in here or not.
         */
    public boolean contains(int val)
    {
        int i = 0;
        for (;i < nValues && values[i] < val;i++);
        return (values[i] == val);
    }

        /**
         * Returns the maximum value in the set.
         */
    public int maxValue()
    {
        return nValues > 0 ? values[nValues - 1] : -1;
    }

        /**
         * Returns the maximum value in the set.
         */
    public int minValue()
    {
        return nValues > 0 ? values[0] : -1;
    }

        /**
         * Returns a copy of this object.
         */
    public IntSet makeCopy()
    {
        IntSetArrayImpl out = new IntSetArrayImpl(nValues);
        for (int i = 0;i < nValues;i++) out.add(values[i]);
        return out;
    }

        /**
         * Returns the union of this set and another set.
         */
    public IntSet union(IntSet another)
    {
        int as = another.size();
        IntSetArrayImpl newSet = new IntSetArrayImpl(nValues + another.size());

        int c1 = 0, c2 = 0, tv, av;
        while (c1 < nValues && c2 < as)
        {
            tv = values[c1];
            av = another.get(c2);

            if (tv < av)
            {
                newSet.add(tv);
                c1++;
            } else if (tv > av)
            {
                newSet.add(av);
                c2++;
            } else
            {
                newSet.add(av);
                c1++;
                c2++;
            }
        }

        while (c1 < nValues) newSet.add(values[c1++]);
        while (c2 < as) newSet.add(another.get(c2++));

        return newSet;
    }

        /**
         * Unions this list with another.
         */
    public void unionWith(IntSet another)
    {
        int as = another.size();

            // do we need to resize?
        if (values.length <= (as + nValues))
        {
            int v2[] = values;
            values = new int[nValues + as + 2];
            System.arraycopy(v2, 0, values, 0, nValues);
        }

        for (int i = 0;i < as;i++)
        {
            add(another.get(i));
        }
    }

        /**
         * Tells if two objects are equal.
         */
    public boolean equals(Object another)
    {
        if (! (another instanceof IntSet) ||
              ((IntSet)another).size() != nValues) return false;

        if (another instanceof IntSetArrayImpl)
        {
            IntSetArrayImpl an = (IntSetArrayImpl)another;
            for (int i = 0;i < an.nValues;i++)
            {
                if (values[i] != an.values[i]) return false;
            }
        } else
        {
            IntSet an = (IntSet)another;
            int s2 = an.size();
            for (int i = 0;i < s2;i++)
            {
                if (values[i] != an.get(i)) return false;
            }
        }
        return true;
    }
}
