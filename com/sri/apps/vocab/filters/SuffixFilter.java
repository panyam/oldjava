
package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * Allows any word that begins with a certain suffix
 *
 * @author Sri Panyam
 */
public class SuffixFilter extends SingleValueFilter
{
        /**
         * Uninstantiable default constructor.
         */
    public SuffixFilter()
    {
        this("");
    }

        /**
         * Constructor.
         */
    public SuffixFilter(String suff)
    {
        super(suff);
    }

        /**
         * Tells if a word matches this range.
         */
    public boolean wordMatches(Word word)
    {
        return invert == word.lowerCase.endsWith(filterValue);
    }

        /**
         * Gets the name of the filter.
         */
    public String getFilterName()
    {
        return "Suffix Filter";
    }

        /**
         * Gets the name of the value.
         */
    public String getValueType()
    {
        return "Suffix";
    }
}
