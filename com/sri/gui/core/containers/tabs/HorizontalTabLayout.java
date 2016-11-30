package com.sri.gui.core.containers.tabs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.core.button.*;

public abstract class HorizontalTabLayout extends TabLayout
{
		/**
		 * Lay tab controllers on the left.
		 */
	public final static int TOP = 0;
		/**
		 * Lay tab controllers on the right.
		 */
	public final static int BOTTOM = 1;
	
		/**
		 * Tells where the tab controllers are to be placed.
		 */
	protected int position = TOP;
	
	public final static int LEFT = 0;
	public final static int CENTER = 1;
	public final static int RIGHT = 2;

		/**
		 * The justification of the tabs.
		 */
	protected int justify = LEFT;

		/**
		 * Default constructor.
		 */
	protected HorizontalTabLayout()
	{
		setPosition(HorizontalTabLayout.TOP);
		setJustify(HorizontalTabLayout.LEFT);
	}

		/**
		 * Constructor
		 */
	protected HorizontalTabLayout(int pos)
	{
		position = pos;
	}
	
		/**
		 * Sets the position of the layout.
		 */
	public void setPosition(int p)
	{
		position = p;
	}
	
		/**
		 * Sets the justification of the layout.
		 */
	public void setJustify(int j)
	{
		justify = j;
	}
	
		/**
		 * Paints the target tab panel.
		 */
	public void paint(TabPanel parent,Graphics g,Dimension d)
	{
	}

		/**
		 * Paints a given tag.
		 */
	protected void paintTab(Graphics g,SButton button,boolean current)
	{
	}

	
		/**
		 * Return's the container's preferred size.
		 */
	public Dimension preferredLayoutSize(Container parent)
	{
		return null;
	}
	
	public void layoutContainer(Container parent)
	{
	}
}
