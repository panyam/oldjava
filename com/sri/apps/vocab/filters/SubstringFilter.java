
package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * Allows any word that begins with a certain substring
 *
 * @author Sri Panyam
 */
public class SubstringFilter extends SingleValueFilter
{
        /**
         * Uninstantiable default constructor.
         */
    public SubstringFilter()
    {
        this("");
    }

        /**
         * Constructor.
         */
    public SubstringFilter(String subs)
    {
        super(subs);
    }

        /**
         * Tells if a word matches this range.
         */
    public boolean wordMatches(Word word)
    {
        return invert == (word.lowerCase.indexOf(filterValue) >= 0);
    }

        /**
         * Gets the name of the filter.
         */
    public String getFilterName()
    {
        return "Substring Filter";
    }

        /**
         * Gets the name of the value.
         */
    public String getValueType()
    {
        return "Substring";
    }
}
