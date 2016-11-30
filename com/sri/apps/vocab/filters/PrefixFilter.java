
package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * Allows any word that begins with a certain prefix
 *
 * @author Sri Panyam
 */
public class PrefixFilter extends SingleValueFilter
{
        /**
         * Uninstantiable default constructor.
         */
    public PrefixFilter()
    {
        this("");
    }

        /**
         * Constructor.
         */
    public PrefixFilter(String pref)
    {
        super(pref);
    }

        /**
         * Gets the name of the filter.
         */
    public String getFilterName()
    {
        return "Prefix Filter";
    }

        /**
         * Gets the name of the value.
         */
    public String getValueType()
    {
        return "Prefix";
    }

        /**
         * Tells if a word matches this range.
         */
    public boolean wordMatches(Word word)
    {
        return invert == word.lowerCase.startsWith(filterValue);
    }
}
