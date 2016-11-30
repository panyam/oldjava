package com.sri.gui.core.button;

import java.awt.*;
import java.awt.event.*;

public class SCheckGroup extends Object {
		/**
		 * Defines the mutual exclusiveness of the buttons.
		 * In all cases only one of the checkbuttons can be on.
		 * However with mutex on, exactly one button MUST be on.
		 * With mutex set to false all buttons can be false.
		 */
	protected boolean mutex = true;
	
        /**
         * The current check button.
         */
    protected SCheckButton selectedCheckButton = null;

        /**
         * Creates a new instance of a checkbox group.
         * All check buttons are mutually exclusive.
         */
    public SCheckGroup() { }

        /**
         * Creates a new instance of a checkbox group.
         * Can specify the mutual exclusiveness of the buttons.
         */
    public SCheckGroup(boolean m) {
		this.mutex = m;
	}
	
		/**
		 * Returns the selected button.
		 */
    public SCheckButton getSelected() {
        return selectedCheckButton;
    }

		/**
		 * Sets the selected checkbutton to true.
		 */
    public synchronized void setSelected(SCheckButton check) {
        SCheckButton old = selectedCheckButton;
        selectedCheckButton = check;
        selectedCheckButton.setState(true,true);
        if (old != null) old.setState(false,true);
    }
	
		/**
		 * Tells if the buttons are mutually exclusive or not.
		 */
	public boolean isMutex() {
		return mutex;
	}
}
