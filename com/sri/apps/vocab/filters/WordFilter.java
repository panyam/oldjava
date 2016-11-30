package com.sri.apps.vocab.filters;

import java.util.*;
import com.sri.apps.vocab.*;

/**
 * List of words.
 *
 * An interface that will filter out certain words.
 */
public interface WordFilter
{
        /**
         * Given a word returns if it matches this filter or not.
         *
         * @param   word    The word which is to be checked against this
         *                  filter.
         *          negate  Tells if the result of the filter should be
         *                  invert in essence filter all words that DO NOT
         *                  match this filter.
         * @return  Tells if the word matches the filter or not.
         */
    public boolean wordMatches(Word word);

        /**
         * Tells if the filter should be inverted or not.
         */
    public void invertFilter(boolean invert);

        /**
         * Get the name of the filter.
         *
         * @return A short string indicating the name of the filter.
         */
    public String getFilterName();
}
