package com.sri.gui.core.containers.tabs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.core.button.*;

/**
 * A horizontal tab layout where the tabs are placed in a single 
 * line one after the other.
 */
public class SingleLineHorizontalTabLayout extends HorizontalTabLayout
{
		/**
		 * Number of visible tabs.
		 */
	protected int nShowing;
	
		/**
		 * Constructor.
		 */
	public SingleLineHorizontalTabLayout(int pos)
	{
		setPosition(pos);
	}
	
		/**
		 * Paints the target tab panel.
		 */
	public void paint(TabPanel parent,Graphics g,Dimension d)
	{
		int st = parent.getStartingTab();
		int ci = parent.getCurrentTabIndex();
		Insets in = parent.getInsets();
		for (int i = st;i < st + nShowing;i++)
		{
			if (i != ci) paintTab(g,(SButton)parent.getController(i),false);
		}
		
		SButton current = (SButton)parent.getController(ci);
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
		int left = (position == LEFT ? loc.x - 2 : loc.x - 1);
		int top = loc.y - 3;
		int right = loc.x + d.width + 2;
		int down = loc.y + d.height + 4;
		Color tabcol = button.getBackground();
		
		if (position == TOP)
		{
			g.setColor(tabcol);
			g.drawLine(left + 2,top+1,right - 2,top+1);
			g.drawLine(left + 1,top+2,left + 1,down);
		
			if (current)
			{
				//g.setColor(Color.black);
				g.fillRect(left, down - 4, right - left, 2);
			}
		
			g.setColor(Color.white);
			g.drawLine(left,top + 2,left + 2,top);
			g.drawLine(left + 2,top,right - 2,top);
			g.drawLine(left,top + 2,left,down);
			g.setColor(Color.gray);
			g.drawLine(right - 1, top + 2, right - 1, down);
			g.setColor(Color.black);
			g.drawLine(right, top + 3, right, down);
			g.drawLine(right - 1, top + 1, right, top + 2);
		} else if (position == BOTTOM)
		{
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
		int vGap = 6;
		Color bg = parent.getBackground();
		for (int i = 0;i < nc;i++)
		{
			SButton controller = (SButton)p.getController(i);
			controller.setBackground(bg);			// set tabcolor to parent color...
			Dimension pr = controller.getPreferredSize();
			if (out.height < pr.height) out.height = pr.height;
			out.width += pr.width;
		}
		out.width += (insets.left + insets.right) + 10;
		out.height += (insets.top + insets.bottom) + 6;
		out.width += (nc * vGap);
		return out;
	}
	
	public void layoutContainer(Container parent)
	{
		TabPanel p = (TabPanel)parent;
		int nc = p.getComponentCount();
		
		if (nc == 0) return ;
		
		Insets insets = p.getInsets();
		Dimension ps = parent.getSize();
		boolean needNavigator = false;
		int remW = ps.width - insets.left - insets.right;
		int widths[] = new int[nc];
		int vGap = 6;				// the gap bw tabs...
		
		int maxH = 0, totalW = 0;		// maximum width of tabs... and total height of all tabs
		
		for (int i = 0;i < nc;i++)
		{
			SButton controller = (SButton)p.getController(i);
			p.getComponent(i).setVisible(false);
			Dimension pr = controller.getPreferredSize();
			if (maxH < pr.height) maxH = pr.height;
			widths[i] = pr.width;
			totalW += (pr.width + vGap);
		}
		
		nShowing = nc;
		needNavigator = (totalW > remW);
		int startingTab = 0;
		int totalDisplayableW = 0;		// total height of all tabs that can be seen.

		if (!needNavigator) p.setStartingTab(0);
		else 
		{
			startingTab = p.getStartingTab();
			boolean end = false;
					// now we calcualte how many tabs can fit in here....
			for (int i = startingTab; i < nc && !end;i++)
			{
				int c = totalDisplayableW + widths[i];
				if (c > remW)
				{
					end = true;
				} else 
				{
					totalDisplayableW = c;
					nShowing++;
				}
			}			
			if (nShowing == 0) nShowing = 1;
		}
		
			/**
			 * Now we calculate the starting x and y coordinates
			 * for the tabs.
			 */
		int startY = (position == TOP ? insets.top + 6 : ps.height - insets.bottom - maxH - 6);
		int startX = insets.left + 4;
		if (justify == CENTER)
		{
			startX = insets.left + ((remW - totalDisplayableW) >> 1);
		} else if (justify == BOTTOM)
		{
			startX = ps.width - insets.right - totalDisplayableW;
		}
		
		startX += 3;
		for (int i = startingTab;i < startingTab + nShowing;i++)
		{
			SButton controller = (SButton)p.getController(i);
			controller.setSize(widths[i], maxH);
			controller.setLocation(startX,startY + (vGap >> 1));
			startX += (widths[i] + vGap);
		}
	
		int ci = p.getCurrentTabIndex();
		if (ci >= startingTab && ci < startingTab + nShowing)
		{
			Component cont = p.getController(ci);
			Point loc = cont.getLocation();
			if (position == TOP)
			{
				cont.setBounds(loc.x - 2,loc.y - 2,widths[ci] + 4,maxH + 2);
			} else if (position == BOTTOM)
			{
				cont.setBounds(loc.x,loc.y - 2,widths[ci] + 4,maxH + 2);
			}
		}
		
		Component curr = p.getComponent(ci);
		curr.setBounds(insets.left + 4,
					   (position == TOP ? insets.top + maxH + 5 : insets.top) + 6,
					   ps.width - insets.left - insets.right  - 12,
					   ps.height - insets.top - insets.bottom - maxH - 14);
		curr.setVisible(true);
		p.paint(p.getGraphics());
	}
}
