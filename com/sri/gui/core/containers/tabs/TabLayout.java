package com.sri.gui.core.containers.tabs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A Layout manager used by TabPanels.
 */
public abstract class TabLayout extends ContainerAdapter implements LayoutManager2
{
	public abstract void paint(TabPanel parent,Graphics g,Dimension d);
	
	public void addLayoutComponent(String n,Component comp) { }
	public void removeLayoutComponent(Component comp) { }
	public void addLayoutComponent(Component comp, Object constraints) { }
	
	public float getLayoutAlignmentX(Container target) { return 0; }
	public float getLayoutAlignmentY(Container target) { return 0; }
	
	public void invalidateLayout(Container target) { }
	public void layoutContainer(Container parent) { System.out.println("Laying out..."); }
	
	public Dimension minimumLayoutSize(Container parent) { return preferredLayoutSize(parent); }
	public Dimension maximumLayoutSize(Container target) { return null; }
	public Dimension preferredLayoutSize(Container parent) { return preferredLayoutSize(parent); }
}
