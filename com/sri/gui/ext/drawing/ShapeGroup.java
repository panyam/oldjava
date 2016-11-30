package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

	/**
	 * This class is a child class of the Shape object which 
	 * describes a group of shapes.
	 */
public abstract class ShapeGroup extends com.sri.gui.ext.drawing.Shape
{
	protected int maxSize = 5;
	protected static int sizeIncr = 5;
	protected SceneElement kids[] = new SceneElement [maxSize];
	protected int nKids = 0;
	
		/**
		 * Constructor.
		 */
	public ShapeGroup()
	{
		outlineColor = null;
		nKids = 0;
	}
	
		/**
		 * Returns the cursor to be used at a specific 
		 * control point.
		 */
	public Cursor getCursorAtControlPoint(int which)
	{
		return null;
	}
	
		/**
		 * Returns the cursor to be used at a specific 
		 * control point.
		 */
	public Cursor getCursorAtSizePoint(int which)
	{
		switch (which)
		{
			case 0 : 
				return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			case 1 : 
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			case 2 : 
				return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			case 3 : 
				return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			case 4 : 
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			case 5 : 
				return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			case 6 : 
				return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			case 7 : 
				return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		}
		return null;
	}
		/**
		 * Paints this SceneElement object at the given location and
		 * at the given mode.
		 */
	public void paint(Graphics g, int xOffset, int yOffset)
	{
	}
	
		/**
		 * Tells whether the given mouse coordinate is
		 * within this SceneElement object or not.
		 */
	public boolean insideMovableArea(int x,int y)
	{
		return false;
	}
	
		/**
		 * Adds a shape listener to the list of listeners.
		 */
	public synchronized int addElement(SceneElement dr) {
		if (dr != null) 
		{
				// check for duplicates
			for (int i = 0;i < nKids;i++) {
				if (kids[i] == dr) return 0;
			}
			
				// increase list size if max reached
			if (nKids == maxSize) {
				maxSize += sizeIncr;
				SceneElement []second = new SceneElement[maxSize];
				System.arraycopy(kids,0,second,0,nKids);
				kids = null;
				System.gc();
				kids = second;
			}
				// before we add the component, 
				// we must make sure that its bounds are relative 
				// to ours rather than our parents...
			/*dr.bounds.x -= bounds.x;
			dr.bounds.y -= bounds.y;
			kids[nKids++] = dr;
			calcBoundingRectangle();*/
		}
		return -1;
	}
	
		/**
		 * Recalculates the bounding rectangle
		 * based on the kids we have..
		 */
	private void calcBoundingRectangle()
	{
			// and also go through the whole list and 
			// re calculate the bounding rectangle...
		/*int currX = bounds.x;
		int currY = bounds.y;
		int minX = kids[0].bounds.x;
		int minY = kids[0].bounds.y;
		int maxX = minX;
		int maxY = minY;
		
		for (int i = 1;i < nKids;i++)
		{
			int cx = kids[i].bounds.x;
			int cy = kids[i].bounds.y;
			if (cx < minX) minX = cx;
			if (cy < minY) minY = cy;
			if (cx > maxX) maxX = cx;
			if (cy > maxY) maxY = cy;
		}
		bounds.x += minX;
		bounds.y += minY;
		bounds.width = maxX - minX;
		bounds.height = maxY - minY;*/
	}
	
		/**
		 * Removes a shape listener from the list of listeners.
		 */
	public synchronized boolean removeSceneElement(SceneElement dr) 
	{
		if (dr != null)
		{
			for (int i = 0;i < nKids;i++) 
			{
				if (dr == kids[i])
				{
						// when we remove it make sure that the 
						// shape's position is relative to parents 
						// position rather than ours.
					/*dr.bounds.x += bounds.x;
					dr.bounds.y += bounds.y;
					System.arraycopy(kids,i + 1,kids,i,nKids - i - 1);
					nKids --;
					calcBoundingRectangle();*/
					return true;
				}
			}
		}
		return false;
	}
	
	public int processMouseEvent(MouseEvent e,Scene parent)
	{
		return 0;
	}
}
