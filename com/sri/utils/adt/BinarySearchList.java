package com.sri.utils.adt;

import java.util.*;

/**
 * Defines a list in which items can be inserted and deleted in a given
 * order.
 */
public class BinarySearchList
{
        /**
         * The set of objects.
         */
    public Object items[] = new Object[2];
    public int nItems = 0;

        /**
         * Default Constructor
         */
    public BinarySearchList()
    {
        nItems = 0;
    }

        /**
         * Finds a given item in the list.
         * Insert if need be.
         */
    public int findItem(Object obj, ObjectComparer comparer, boolean insert)
    {
        if (obj == null || comparer == null) return -1;
        int lo = 0, hi = nItems - 1;
        int mid = 0;

        while (lo < hi)
        {
            mid = (lo + hi) / 2;
            int res = comparer.compare(obj, items[mid]);

            if (res == 0) 
            {
                return mid;
            } else if (res < 0)
            {
                hi = mid - 1;
            } else 
            {
                lo = mid + 1;
            }
        }

        System.out.println("lo, hi = " + lo + ", " + hi);

            // at this point lo will be greater than hi by 1
        if (insert)
        {
            ensureCapacity(nItems + 1);

            System.arraycopy(items, lo, items, lo+1, nItems - lo);
            items[lo] = obj;
            nItems++;
            return lo;
        }
        return -1;
    }

        /**
         * Ensure that we have the given capacity.
         */
    public void ensureCapacity(int newCapacity)
    {
        if (items.length < newCapacity)
        {
            Object temp[] = items;
            items = new Object[newCapacity];
            System.arraycopy(temp, 0, items, 0, nItems);
        }
    }
}
