package com.sri.gui.ext.drawing.selectors;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.ext.drawing.*;

public class ArrowStyleSelector extends SelectorPanel
{
	public final static int PLAIN = 0;
	public final static int ARROW = 1;
	public final static int OPEN = 2;
	public final static int STEALTH = 3;
	public final static int DIAMOND = 4;
	public final static int ROUND = 5;
	
	public ArrowStyleSelector(InfoListener infoListener)
	{
		super(infoListener);
		numStyles = 6;
	}
	
	protected void prepareBuffer(Dimension d)
	{
		super.prepareBuffer(d);
			
			// now basically draw all the ones and
			// the one that is currently 
		int x = 2, y = 2;
		int w = (d.width - 4) >> 1;
		int h = (d.height - 4) / numStyles;
		for (int i = 0;i < numStyles;i++)
		{
			drawArrowStyle(i,x,y,w,h,bg,false);
			drawArrowStyle(i,x + w + 1,y,w,h,bg,true);
			y += h;
		}
	}

	protected void drawArrowStyle(int which,int x,int y,int w,int h,Graphics g,boolean open)
	{
		if (which == (open ? currMovingItem >> 4 : currMovingItem)) g.draw3DRect(x,y,w,h,true);
		if (which == (open ? currPressedItem >> 4 : currPressedItem)) g.draw3DRect(x,y,w,h,false);
		if (which == (open ? currItem >> 4 : currItem)) g.draw3DRect(x,y,w,h,false);
	}
	
	public int pointToIndex(int x,int y)
	{
		Dimension d = getSize();
		if (x >= 0 || x < d.width || y >= 0 || y < d.height)
		{
			int out = (y * numStyles) / d.height;
			return (x < (d.width >> 1) ? out << 4 : out);
		}
		return -1;
	}
	
		/**
		 * Generates the event.
		 */
	protected void generateEvent(int which)
	{
		processEvent (new ItemEvent(this,
									ItemEvent.ITEM_STATE_CHANGED,
									this,
									ItemEvent.ITEM_STATE_CHANGED));
	}
	
		/**
		 * Gets the beginning style.
		 */
	public final int getBeginningStyle()
	{
		return (currItem >> 4);
	}
	
		/**
		 * Gets the closing style...
		 */
	public final int getEndingStyle()
	{
		return currItem & 15;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e); 
		infoListener.setInfo("" + currMovingItem);
	}
	
	public void mouseDragged(MouseEvent e)
	{
		super.mouseDragged(e); 
		infoListener.setInfo("" + currPressedItem);
	}
	
	public void mouseExited(MouseEvent e)
	{
		super.mouseExited(e);
		infoListener.setInfo("");
	}
}	
