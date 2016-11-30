
package com.sri.apps.vocab;

import com.sri.apps.vocab.filters.*;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/**
 * The a panel that shows information regarding a certain filter and
 * returns values of that filter.
 *
 * @author Sri Panyam
 */
public abstract class FilterEditor extends Container
{
        /**
         * Get the filter in question.
         */
    public abstract WordFilter getFilter();

        /**
         * The filter that is currently being edited.
         */
    public abstract void setFilter(WordFilter filter);
}
