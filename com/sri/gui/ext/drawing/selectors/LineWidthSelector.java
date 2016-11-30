package com.sri.gui.ext.drawing.selectors;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.ext.drawing.*;

		/**
		 * Enables users to select line styles.
		 */
public class LineWidthSelector extends SelectorPanel
{
        /**
         * Constructor.
         */
	public LineWidthSelector(InfoListener infoListener)
	{
		super(infoListener);
		numStyles = 6;
	}
		
        /**
         * Prepare the buffer for drawing.
         */
	public void prepareBuffer(Dimension d)
	{
		super.prepareBuffer(d);
			
			// now basically draw all the ones and
			// the one that is currently 
		int x = 2, y = 2;
		int w = d.width - 4;
		int h = (d.height - 4) / numStyles;
		for (int i = 0;i < numStyles;i++)
		{
			drawLineWidth(i + 1,x,y,w,h,bg);
			y += h;
		}
	}
		
	private void drawLineWidth(int which, int x,int y,int w,int h,Graphics g)
	{
		if (which < 0) return ;
		Color store = g.getColor();
		g.setColor(Color.black);
		int off = (h - which) / 2;
		for (int i = 0;i < which;i++)
		{
			g.drawLine(x + 2,y + off + i,x + w - 4,y + off + i);
		}
		g.setColor(store);
		if (which == currMovingItem + 1) g.draw3DRect(x,y,w,h,true);
		if (which == currPressedItem + 1) g.draw3DRect(x,y,w,h,false);
		if (which == currItem + 1) g.draw3DRect(x,y,w,h,false);
	}

		/**
		 * Returns the item under the current mouse location.
		 */
	public int pointToIndex(int x,int y)
	{
		Dimension d = getSize();
		if (y >= 0 && x >= 0 && x <= d.width && y  <= d.height)
		{
			return ((y * numStyles) / d.height);
		}
		return -1;
	}
	
		/**
		 * Notifies all the item listeners that item i has been
		 * chosen.
		 */
	public void generateEvent (int which)
	{
		processItemEvent(new ItemEvent(this,
									   ItemEvent.ITEM_STATE_CHANGED,
									   this,
									   ItemEvent.ITEM_STATE_CHANGED));
	}
	
	public int getLineStyle()
	{
		return currItem;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e); 
		infoListener.setInfo(currMovingItem + "Pt line");
	}
	
	public void mouseDragged(MouseEvent e)
	{
		super.mouseDragged(e); 
		infoListener.setInfo(currPressedItem + "Pt line");
	}
	
	public void mouseExited(MouseEvent e)
	{
		super.mouseExited(e);
		infoListener.setInfo("");
	}
}
