
/*
 * BreweryAction.java
 *
 * Created on 10 August 2004, 11:28
 */
package com.sri.apps.brewery.wizard.actions;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.wizard.*;
import com.sri.apps.brewery.wizard.sections.*;

/**
 * The super class of all action objects.
 *
 * @author Sri Panyam
 */
public abstract class BreweryAction implements Action
{
        /**
         * Tells if the action is enabled or not.
         */
    protected boolean actionEnabled;

        /**
         * The Wizard parent which contains, calls and controlls this
         * action object.
         */
    protected BreweryWizard bParent;

        /**
         * The parent application frame that holds the menu to which this
         * action is associated with.
         */
    protected BreweryAppFrame appParent = null;

        /**
         * Uninstantiable default constructor.
         */
    protected BreweryAction()
    {
    }

        /**
         * Constructor.
         */
    public BreweryAction(BreweryWizard bWizard, BreweryAppFrame parentFrame)
    {
        this.bParent = bWizard;
        this.appParent = parentFrame;
    }

        /**
         * Override method.
         */
    public Object getValue(String key)
    {
        return null;
    }

        /**
         * Override method.
         */
    public void putValue(String id, Object value)
    {
    }

        /**
         * Override method.
         */
    public void addPropertyChangeListener(PropertyChangeListener pcL)
    {
    }

        /**
         * Override method.
         */
    public void removePropertyChangeListener(PropertyChangeListener pcL)
    {
    }

        /**
         * Override method from Action.
         */
    public void setEnabled(boolean en)
    {
        this.actionEnabled = en;
    }

        /**
         * Override method from Action.
         */
    public boolean isEnabled()
    {
        return actionEnabled;
    }
}
