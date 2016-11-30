package com.sri.apps.mml;

import java.util.*;
import java.awt.*;

/**
 * A class that maintains a set of ranges.
 */
public class MMLRangeList
{
        /**
         * List of ranges.
         */
    MMLStyleRange ranges[] = new MMLStyleRange[5];

        /**
         * Number of ranges?
         */
    int nRanges = 0;

        /**
         * Given a line num and char pos, finds he appropriate
         * stylenode.
         */
    public MMLStyleRange find(int line, int pos)
    {
        // check if we have enough space::
        int cmp;
        int lo = 0;
        int hi = nRanges - 1;
        int mid = 0;
        int index = 0;

        if (nRanges == 0) return null;

        while (hi - lo <= 1)
        {
            mid = (lo + hi) >> 1;
            cmp = ranges[mid].compare(line, pos);
            if (cmp < 0) lo = mid;
            else if (cmp > 0) hi = mid;
            else return ranges[mid];
        }
        if (ranges[lo].compare(line, pos) == 0)
        {
            return ranges[lo];
        } else if (ranges[hi].compare(line, pos) == 0)
        {
            return ranges[hi];
        }
        return null;
    }

        /**
         * insert a style range.
         */
    public void insert(MMLStyleRange sr)
    {
        // check if we have enough space::
        int cmp;
        int lo = 0;
        int hi = nRanges - 1;
        int mid = 0;

        if (nRanges == ranges.length)
        {
            MMLStyleRange sr2[] = ranges;
            ranges = new MMLStyleRange[nRanges + 5];
            System.arraycopy(ranges, 0, sr2, 0, nRanges);
        }

            // find the index of where we need to insert
        if (nRanges == 0)
        {
            ranges[nRanges++] = sr;
            return ;
        } else if (nRanges == 1)
        {
            cmp = ranges[0].compare(sr);
            if (cmp < 0) 
            {
                ranges[1] = ranges[0];
                ranges[0] = sr;
            } else if (cmp > 0)
            {
                ranges[nRanges++] = sr;
            } else return ;
        } else
        {
            while (hi - lo <= 1)
            {
                mid = (lo + hi) >> 1;
                cmp = ranges[mid].compare(sr);
                if (cmp < 0) lo = mid;
                else if (cmp > 0) hi = mid;
                else return ;
            }
        }
        if (hi == lo) return ;
        else 
        {
            // insert between hi and lo
            System.arraycopy(ranges, hi, ranges, hi + 1, nRanges - hi);
            ranges[hi] = sr;
        }
    }

    public void delete(MMLStyleRange sr)
    {
        // check if we have enough space::
        int cmp;
        int lo = 0;
        int hi = nRanges - 1;
        int mid = 0;
        int index = 0;

        if (nRanges == 0) return ;

        while (hi - lo <= 1)
        {
            mid = (lo + hi) >> 1;
            cmp = ranges[mid].compare(sr);
            if (cmp < 0) lo = mid;
            else if (cmp > 0) hi = mid;
            else return ;
        }
        if (ranges[lo].compare(sr) == 0)
        {
            System.arraycopy(ranges, lo + 1, ranges, lo , nRanges - (lo + 1));
            nRanges--;
        } else if (ranges[hi].compare(sr) == 0)
        {
            System.arraycopy(ranges, hi + 1, ranges, hi, nRanges - (hi + 1));
            nRanges--;
        }
    }

        /**
         * Get the count of styles available.
         */
    public int getStyleCount()
    {
        return nRanges;
    }

        /**
         * Get the ith Style.
         */
    public MMLStyleRange getStyle(int i)
    {
        return ranges[i];
    }
}
