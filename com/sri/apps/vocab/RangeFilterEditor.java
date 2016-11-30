
package com.sri.apps.vocab;

import com.sri.apps.vocab.filters.*;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/**
 * The a panel that shows information regarding a Range Filter and allows
 * these types of filters to be edited.
 *
 * @author Sri Panyam
 */
public class RangeFilterEditor extends FilterEditor
{
    protected RangeFilter currFilter = null;
    protected TextField fromField = new TextField("", 10);
    protected TextField toField = new TextField("", 10);

        /**
         * Constructor.
         */
    public RangeFilterEditor()
    {
        setLayout(new GridLayout(1, 2));

        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        fromPanel.add("West", new Label("From: "));
        fromPanel.add("Center", fromField);

        toPanel.add("West", new Label("To: "));
        toPanel.add("Center", toField);

        add(fromPanel);
        add(toPanel);
    }

        /**
         * Get the filter in question.
         */
    public WordFilter getFilter()
    {
        if (currFilter != null)
        {
            currFilter.setLo(fromField.getText());
            currFilter.setHi(toField.getText());
        }
        return currFilter;
    }

        /**
         * The filter that is currently being edited.
         */
    public void setFilter(WordFilter filter)
    {
        currFilter = null;
        if (filter instanceof RangeFilter)
        {
            currFilter = (RangeFilter)filter;
            toField.setText(currFilter.getHi());
            fromField.setText(currFilter.getLo());
        }
    }
}
