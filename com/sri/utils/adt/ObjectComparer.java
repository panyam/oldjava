package com.sri.utils.adt;

/**
 * Generic functions to compare objects.
 * Can used be comparisons in comparison based Data Types like Binary
 * Trees, List Sorting of objects, Heaps and so on.
 */
public abstract class ObjectComparer
{
        /**
         * Compares two objects.
         * If obj1 < obj2 then return a negative number
         * else If obj1 = obj2 then return 0
         * otherwise returns a positive number
         */
    public abstract int compare(Object obj1, Object obj2);
}
