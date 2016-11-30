

package com.sri.apps.vocab;

import java.util.*;
import com.sri.apps.vocab.filters.*;

/**
 * Sorts a list of words.  Uses the enhanced QuickSort as described in 
 * http://www.cs.ubc.ca/spider/harrison/Java/EQSortAlgorithm.java.html.
 *
 * @author Sri Panyam
 */
public class EQListSorter
{
        /**
         * Brute force search for a list of less then 3 items.
         */
    protected static void brute(Word a[], int lo, int hi)
    {
        if ((hi-lo) == 1)
        {
	        if (a[hi].compareTo(a[lo]) < 0)
            {
		        Word T = a[lo];
		        a[lo] = a[hi];
		        a[hi] = T;
	        }
	    }

	    if ((hi-lo) == 2)
        {
	        int pmin = a[lo].compareTo(a[lo+1]) < 0 ? lo : lo+1;
	        pmin = a[pmin].compareTo(a[lo+2]) < 0 ? pmin : lo+2;

	        if (pmin != lo)
            {
                Word T = a[lo];
                a[lo] = a[pmin];
                a[pmin] = T;
	        }
	        brute(a, lo+1, hi);
	    }

	    if ((hi-lo) == 3)
        {
	        int pmin = a[lo].compareTo(a[lo+1]) < 0 ? lo : lo+1;
	        pmin = a[pmin].compareTo(a[lo+2]) < 0 ? pmin : lo+2;
	        pmin = a[pmin].compareTo(a[lo+3]) < 0 ? pmin : lo+3;

	        if (pmin != lo)
            {
                Word T = a[lo];
                a[lo] = a[pmin];
                a[pmin] = T;
            }

            int pmax = a[hi].compareTo(a[hi-1]) > 0 ? hi : hi-1;
            pmax = a[pmax].compareTo(a[hi-2]) > 0 ? pmax : hi-2;
            if (pmax != hi)
            {
                Word T = a[hi];
                a[hi] = a[pmax];
                a[pmax] = T;
            }
            brute(a, lo+1, hi-1);
        }
    }

        /**
         * Sort a list of words between the boundaries lo and hi.
         */
    public static void sort(Word a[], int lo0, int hi0)
    {
        int lo = lo0;
        int hi = hi0;
        if ((hi-lo) <= 3)
        {
            brute(a, lo, hi);
	        return;
	    }

            
                // Pick a pivot and move it out of the way
	    Word pivot = a[(lo + hi) / 2];
        a[(lo + hi) / 2] = a[hi];
        a[hi] = pivot;

        while( lo < hi )
        {
                // Search forward from a[lo] until an element is found that
                // is greater than the pivot or lo >= hi
            while (a[lo].compareTo(pivot) <= 0 && lo < hi) lo++;

                // Search backward from a[hi] until element is found that
                // is less than the pivot, or hi <= lo
	        while (pivot.compareTo(a[hi]) <= 0 && lo < hi ) hi--;

                // Swap elements a[lo] and a[hi]
            if( lo < hi )
            {
                Word T = a[lo];
                a[lo] = a[hi];
                a[hi] = T;
            }
        }

            // Put the median in the "center" of the list
        a[hi0] = a[hi];
        a[hi] = pivot;

            // Recursive calls, elements a[lo0] to a[lo-1] are less than or
            // equal to pivot, elements a[hi+1] to a[hi0] are greater than
            // pivot.
	    sort(a, lo0, lo-1);
	    sort(a, hi+1, hi0);
    }
}
