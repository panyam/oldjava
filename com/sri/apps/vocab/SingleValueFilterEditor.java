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
public class SingleValueFilterEditor extends FilterEditor
{
    protected SingleValueFilter currFilter = null;

    protected Label valueLabel = new Label();
    protected TextField valueField = new TextField("", 10);

        /**
         * Checkbox to select whether the filter should be negated or not.
         */
    protected Checkbox negateCheckBox = new Checkbox("Invert Filter", false);

        /**
         * Constructor.
         */
    public SingleValueFilterEditor()
    {
        setLayout(new BorderLayout());
        add("West", valueLabel);
        add("Center", valueField);
    }

        /**
         * Get the filter in question.
         */
    public WordFilter getFilter()
    {
        if (currFilter != null)
            currFilter.setValue(valueField.getText());
        return currFilter;
    }

        /**
         * The filter that is currently being edited.
         */
    public void setFilter(WordFilter filter)
    {
        currFilter = null;
        if (filter instanceof SingleValueFilter)
        {
            currFilter = (SingleValueFilter)filter;
            valueField.setText(currFilter.getValue());
            valueLabel.setText(currFilter.getValueType());
        }
    }
}
