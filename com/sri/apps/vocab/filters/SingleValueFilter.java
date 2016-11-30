
package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * Allows filter which use single parameters to filter out words.
 *
 * @author Sri Panyam
 */
public abstract class SingleValueFilter implements WordFilter
{
        /**
         * The prefix to match for.
         */
    protected String filterValue;

        /**
         * Tells if the filter should be inverted or not.
         */
    protected boolean invert = false;

        /**
         * Uninstantiable default constructor.
         */
    public SingleValueFilter()
    {
        this("");
    }

        /**
         * Constructor.
         */
    public SingleValueFilter(String pref)
    {
        setValue(pref);
    }

        /**
         * Gets the filter value.
         */
    public String getValue()
    {
        return filterValue;
    }

        /**
         * Tells if the filter should be inverted or not.
         */
    public void invertFilter(boolean invert)
    {
        this.invert = invert;
    }

        /**
         * Gets the name of the value.
         */
    public abstract String getValueType();

        /**
         * Sets the filter value.
         */
    public void setValue(String value)
    {
        this.filterValue = value.toLowerCase();
    }
}
