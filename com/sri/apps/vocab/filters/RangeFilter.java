

package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * Allows any word that is between a range.
 *
 * @author Sri Panyam
 */
public class RangeFilter implements WordFilter
{
        /**
         * The lower word on the range.
         */
    protected String loWord;

        /**
         * Tells if the filter should be inverted or not.
         */
    protected boolean invert = false;

        /**
         * The higher word on the range.
         */
    protected String hiWord;

        /**
         * Uninstantiable default constructor.
         */
    public RangeFilter()
    {
        this("", ('z' + 1) + "");
    }

        /**
         * Constructor.
         */
    public RangeFilter(String lo, String hi)
    {
        setLo(lo);
        setHi(hi);
    }

        /**
         * Tells if the filter should be inverted or not.
         */
    public void invertFilter(boolean invert)
    {
        this.invert = invert;
    }

        /**
         * Sets the lo word.
         */
    public void setLo(String lo)
    {
        this.loWord = lo.toLowerCase();
    }

        /**
         * Get the lo word.
         */
    public String getLo()
    {
        return loWord;
    }

        /**
         * Sets the hi word.
         */
    public void setHi(String hi)
    {
        this.hiWord = hi.toLowerCase();
    }

        /**
         * Get the hi word.
         */
    public String getHi()
    {
        return hiWord;
    }

        /**
         * Gets the name of the filter.
         */
    public String getFilterName()
    {
        return "Range Filter";
    }

        /**
         * Tells if a word matches this range.
         */
    public boolean wordMatches(Word word)
    {
        return invert == 
                ((loWord.compareTo(word.lowerCase) <= 0) &&
                 (hiWord.compareTo(word.lowerCase) >= 0));
    }
}
