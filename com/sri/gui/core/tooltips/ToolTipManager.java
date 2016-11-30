package com.sri.gui.core.tooltips;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ToolTipManager extends MouseAdapter implements MouseMotionListener
{
	public final static Toolkit toolkit = Toolkit.getDefaultToolkit();
	protected boolean isApplet = false;	
	protected Hashtable tooltipTable = new Hashtable();
	protected Window wnd = null;
	protected Panel panel = null;
						
		/**
		 * Default constructor.
		 * Assumes we are using an applet.
		 */
	public ToolTipManager() {
		this.isApplet = false;
		panel = new Panel(new BorderLayout());
	}
	
		/**
		 * Constructor with parent frame.
		 * Assumes we are using an applet.
		 */
	public ToolTipManager(Frame parent) {
		this.isApplet = true;
		wnd = new Window(parent);
		wnd.setLayout(new BorderLayout());
	}
	
	
		/**
		 * Constructor with parent frame.
		 * Assumes we are using an applet.
		 */
	public ToolTipManager(boolean isApplet) {
		this.isApplet = isApplet;
		if (isApplet) {
			panel = new Panel(new BorderLayout());
		} else {
			wnd = new Window((Frame)null);
			wnd.setLayout(new BorderLayout());
		}
	}
	
		/**
		 * Registers a tool tip with a component.
		 */
	public void registerComponent(Component target, Component tooltip) 
	{
		if (tooltipTable.containsKey(target)) {
			throw new IllegalArgumentException(
					"Target component already exists. Remove it first.");
		}
		tooltipTable.put(target,tooltip);
		target.addMouseListener(this);
		target.addMouseMotionListener(this);
	}
	
		/**
		 * Handles mouse dragging event.
		 */
	public void mouseDragged(MouseEvent e) 
	{
	}

		/**
		 * Handles mouse moved event.
		 */
	public void mouseMoved(MouseEvent e) 
	{
	}
}

class ToolTipThread extends Thread 
{
	Component comp, tip;
	Point p;
	long startTime = 1000;	// wait for 1 second b4 showing ttip
	long sleepTime = 1000;	// hide ttip after 1 second after showing...
	
	public ToolTipThread(Component comp, Component ttip, Point p) 
	{
		this.p = p;
		this.comp = comp;
		this.tip = ttip;
	}
	
		/**
		 * Main thread loop.
		 */
	public void run() {
		try {
			wait();
			comp.setVisible(true);
		} catch (Exception e) {
		}
	}
}