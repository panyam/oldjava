package com.sri.gui.core.containers.tabs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.core.button.*;

public class VerticalTabLayout extends TabLayout
{
		/**
		 * Lay tab controllers on the left.
		 */
	public final static int LEFT = 0;
		/**
		 * Lay tab controllers on the right.
		 */
	public final static int RIGHT = 1;
	
		/**
		 * Tells where the tab controllers are to be placed.
		 */
	protected int dir = RIGHT;
	
	public final static int TOP = 0;
	public final static int CENTER = 1;
	public final static int BOTTOM = 2;

		/**
		 * The justification of the tabs.
		 */
	protected int justify = TOP;

		/**
		 * Number of visible tabs.
		 */
	protected int nShowing = 1;

	public VerticalTabLayout(int d)
	{
		dir = d;
	}
	
		/**
		 * Sets the direction of the layout.
		 */
	public void setDirection(int d)
	{
		dir = d;
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
            // see how many components there are...
            // proceed only if we have enough...
        if (parent.getComponentCount() <= 0) return ;
		int st = parent.getStartingTab();
		int ci = parent.getCurrentTabIndex();
		Insets in = parent.getInsets();
		for (int i = st;i < st + nShowing;i++)
		{
			if (i != ci) paintTab(g,(SButton)parent.getController(i),false);
		}
		
		SButton current = (SButton)parent.getController(ci);
		int width = current.getSize().width;
		Component curr = parent.getComponent(ci);
		Rectangle bounds = curr.getBounds();
		
		g.setColor(Color.lightGray);
		g.draw3DRect(bounds.x - 1,bounds.y - 1,bounds.width + 2,bounds.height + 2,true);
		//g.draw3DRect(x+1,y+1,w-2,h-2,true);
		g.setColor(parent.getBackground());
		g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
		
	
			// draw the current showing tab.
		if (ci >= st && ci < st + nShowing)
		{
			paintTab(g,(SButton)parent.getController(ci),true);
		}
	}

		/**
		 * Paints a given tag.
		 */
	protected void paintTab(Graphics g,SButton button,boolean current)
	{
		Point loc = button.getLocation();
		Dimension d = button.getSize();
		int top = loc.y - 3;
		int left = loc.x - (dir == LEFT ? 2 : 1);
		int right = loc.x + d.width + 2;
		int down = loc.y + d.height + 4;
		Color tabcol = button.getBackground();
		
		if (dir == LEFT)
		{
			g.setColor(tabcol);
			g.drawLine(left + 2,top+1,right,top+1);
			g.drawLine(left + 2,top+2,right,top+2);
		
			if (current)
			{
				g.drawLine(right - 1,top + 2,right - 1,down - 4);
				g.drawLine(right - 0,top + 2,right - 0,down - 4);
				g.drawLine(left + 2,down - 3,right,down - 3);
			}
		
			g.setColor(Color.white);
			g.drawLine(left,top + 2,left + 2,top);
			g.drawLine(left + 2,top,right,top);
			g.drawLine(left,top + 2,left,down - 2);
			g.drawLine(left,down - 2,left + 2,down);
			g.setColor(Color.gray);
			g.drawLine(left + 2,down - 2,right,down - 2);
			g.setColor(Color.black);
			g.drawLine(left + 2,down - 1,right,down - 1);
		} else if (dir == RIGHT)
		{
			g.setColor(tabcol);
			g.drawLine(left,top+1,right-1,top+1);
			g.drawLine(left,top+2,right-1,top+2);
			g.drawLine(left,down - 1,right-1,down - 1);
			g.drawLine(left,down - 2,right-1,down - 2);
	
			if (current)
			{
				g.drawLine(left,top + 2,left,down - 3);
				g.drawLine(left + 1,top + 2,left + 1,down - 3);
			}
			g.setColor(Color.white);
			g.drawLine(left,top,right - 2,top);
			g.setColor(Color.black);
			g.drawLine(right - 2,top,right,top + 2);
			g.drawLine(right,top + 2,right,down - 2);
			g.drawLine(right,down - 2,right - 2,down);
			g.drawLine(right - 2,down,left,down);
			g.setColor(Color.gray);
			g.drawLine(right - 1,top + 2,right - 1,down - 2);
			g.drawLine(right - 2,down - 1,left,down - 1);
		}
		
		g.translate(loc.x,loc.y);
		button.paint(g);
		g.translate(-loc.x,-loc.y);
	}

	
		/**
		 * Return's the container's preferred size.
		 */
	public Dimension preferredLayoutSize(Container parent)
	{
		TabPanel p = (TabPanel)parent;
		int nc = p.getComponentCount();
		Insets insets = p.getInsets();
		Dimension out = new Dimension(0,0);
		int hGap = 6;
		Color bg = parent.getBackground();
		for (int i = 0;i < nc;i++)
		{
			SButton controller = (SButton)p.getController(i);
			controller.setBackground(bg);			// set tabcolor to parent color...
			Dimension pr = controller.getPreferredSize();
			if (out.width < pr.width) out.width = pr.width;
			out.height += pr.height;
		}
		out.width += (insets.left + insets.right) + 10;
		out.height += (insets.top + insets.bottom) + 6;
		out.height += (nc * hGap);
		return out;
	}
	
	public void layoutContainer(Container parent)
	{
		TabPanel p = (TabPanel)parent;
		int nc = p.getComponentCount();
		
		if (nc == 0) return ;
		
		Insets insets = p.getInsets();
		Dimension out = new Dimension(0,0);
		Dimension ps = parent.getSize();
		boolean needNavigator = false;
		int remH = ps.height - insets.top - insets.bottom;
		int heights[] = new int[nc];
		int hGap = 8;				// the gap bw tabs...
		
		int maxW = 0, totalH = 0;		// maximum width of tabs... and total height of all tabs
		
		for (int i = 0;i < nc;i++)
		{
			SButton controller = (SButton)p.getController(i);
			p.getComponent(i).setVisible(false);
			Dimension pr = controller.getPreferredSize();
			if (maxW < pr.width) maxW = pr.width;
			heights[i] = pr.height;
			totalH += (pr.height + hGap);
		}
		
		nShowing = nc;
		needNavigator = (totalH > remH);
		int startingTab = 0;
		int totalDisplayableH = 0;		// total height of all tabs that can be seen.

		if (!needNavigator) p.setStartingTab(0);
		else 
		{
			startingTab = p.getStartingTab();
			boolean end = false;
					// now we calcualte how many tabs can fit in here....
			for (int i = startingTab; i < nc && !end;i++)
			{
				int c = totalDisplayableH + heights[i];
				if (c > remH)
				{
					end = true;
				} else 
				{
					totalDisplayableH = c;
					nShowing++;
				}
			}			
			if (nShowing == 0) nShowing = 1;
		}
		
			/**
			 * Now we calculate the starting x and y coordinates
			 * for the tabs.
			 */
		int startX = (this.dir == LEFT ? insets.left + 6 : ps.width - insets.right - maxW - 6);
		int startY = insets.top + 4;
		if (justify == CENTER)
		{
			startY = insets.top + ((remH - totalDisplayableH) >> 1);
		} else if (justify == BOTTOM)
		{
			startY = ps.height - insets.bottom - totalDisplayableH;
		}
		
		for (int i = startingTab;i < startingTab + nShowing;i++)
		{
			SButton controller = (SButton)p.getController(i);
			controller.setSize(maxW,heights[i]);
			controller.setLocation(startX,startY + (hGap >> 1));
			startY += heights[i] + hGap;
		}
	
		int ci = p.getCurrentTabIndex();
		if (ci >= startingTab && ci < startingTab + nShowing)
		{
			Component cont = p.getController(ci);
			Point loc = cont.getLocation();
			if (dir == LEFT)
			{
				cont.setBounds(loc.x - 2,loc.y - 2,maxW + 2,heights[ci] + 4);
			} else if (dir == RIGHT)
			{
				cont.setBounds(loc.x,loc.y - 2,maxW + 2,heights[ci] + 4);
			}
		}
		
		Component curr = p.getComponent(ci);
        int nComps = p.getComponentCount();
        for (int i = 0;i < nComps;i++)
        {
            Component comp = p.getComponent(i);
            comp.setVisible(false);
		    comp.setBounds((dir == LEFT ? insets.left + maxW + 2 : insets.left) + 6,
					        insets.top + 4,
					        ps.width - insets.left - insets.right - maxW - 14,
					        ps.height - insets.top - insets.bottom - 12);
        }
		curr.setVisible(true);
		p.paint(p.getGraphics());
	}
}
