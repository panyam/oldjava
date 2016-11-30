package com.sri.gui.core.containers.tabs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.core.*;
import com.sri.gui.core.button.*;

/**
 * A Tab sheet panel.
 */
public class TabPanel extends SContainer
{
		/**
		 * Specifies whether all the controllers
		 * are in a check style or not.
		 */
	protected boolean checkStyle = true;

		/**
		 * The current check group.
		 */
	protected SCheckGroup checkGroup = new SCheckGroup();

		/**
		 * Current tab.
		 */
	protected int currTab = 0;
	
		/**
		 * Number of visible tabs.
		 */
	protected int nShowing = 1;
	
		/**
		 * The starting tab.
		 */
	protected int startingTab = 0;
	
		/**
		 * Constructor.
		 */
	public TabPanel(boolean checkStyle)
	{
		this.checkStyle = checkStyle;
		setBackground(Color.lightGray);
	}
	
		/**
		 * Prepares the offscreen buffer.
		 */
	protected void prepareBuffer(Dimension d)
	{
		createBuffer(d);
		if (bg == null) return ;
		
		bg.setColor(getBackground());
		bg.fillRect(0,0,d.width,d.height);
		
		LayoutManager lmgr = getLayout();
		if (lmgr == null) return ;
		
		((TabLayout)lmgr).paint(this,bg,d);
	}
	
		/**
		 * Paints the tab.
		 */
	public void paint(Graphics g)
	{
		if (g == null) return ;
		prepareBuffer(getSize());
		if (buffer != null) g.drawImage(buffer,0,0,this);
	}
	
		/**
		 * Shows the requested tab.
		 */
	public void showTab(int which)
	{
		currTab = which;
		doLayout();
	}
	
		/**
		 * Returns the number of components in the tab panel.
		 */
	public int getComponentCount()
	{
		return super.getComponentCount() >> 1;
	}

		/**
		 * Returns the controller at the given index.
		 */
	public Component getController(int which)
	{
		return super.getComponent(which + this.getComponentCount());
	}
	
		/**
		 * Find the index of the given controller
		 */
	public int findControllerIndex(String name)
	{
		int nc = this.getComponentCount();
		for (int i = nc;i < nc + nc;i++)
		{
			SButton curr = (SButton)super.getComponent(i);
			if (curr.getText().equals(name)) return i;
		}
		return -1;
	}

		/**
		 * Find the index of the given controller
		 */
	public int findControllerIndex(Component controller)
	{
		int nc = this.getComponentCount();
		for (int i = nc;i < nc + nc;i++)
		{
			if (super.getComponent(i) == controller) return i;
		}
		return -1;
	}

		/**
		 * Sets the layout manager.
		 */
	public void setLayout(LayoutManager lmgr)
	{
		if (!(lmgr instanceof TabLayout))
		{
			throw new IllegalArgumentException("Only TabLayout can be used.");
		}
		super.setLayout(lmgr);
		doLayout();
	}
	
		/**
		 * The main component adding function.
		 * 
		 * The constraints object must be either a SActionButton or a SCheckButton
		 * object depending on the checkStyle flag.
		 */
	public void addImpl(Component comp, Object constraints, int index)
	{
        System.out.println("Adding: + " + comp + ", " + index);
		if (comp == null) return ;
		if (constraints == null || !(constraints instanceof SButton))
		{
			throw new IllegalArgumentException("Constraints must be either an Action or a Check button object.");
		}
		int nc = this.getComponentCount();
		
		if (index > nc) 
		{
			throw new ArrayIndexOutOfBoundsException("Index exceeds tab panel's component count: " + index);
		}
		
		SButton controller = null;
		final int pos = (index < 0 ? nc : index);
		
		if (this.checkStyle && (constraints instanceof SCheckButton))
		{
			controller = (SCheckButton)constraints;
			((SCheckButton)constraints).setCheckGroup(this.checkGroup);
			((SCheckButton)constraints).addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					showTab(pos);
				}
			});
		} else if (!this.checkStyle && (constraints instanceof SActionButton))
		{
			controller = (SActionButton)constraints;
			((SActionButton)constraints).setRollOver(true);
			((SActionButton)constraints).addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					showTab(pos);
				}
			});
		} else return ;

		controller.setContentAllignment(SButton.CENTER);
		super.addImpl(controller,null,pos + nc);
		super.addImpl(comp,null,pos);
		doLayout();
	}
	
		/**
		 * Lays out the components.
		 */
	public void doLayout()
	{
		super.doLayout();
		paint(getGraphics());
	}
	
		/**
		 * Returns the starting tab.
		 */
	public int getStartingTab()
	{
		return this.startingTab;
	}
	
		/**
		 * Sets the starting tab.
		 */
	public int setStartingTab(int st)
	{
		return this.startingTab = st;
	}
	
		/**
		 * Returns the index of the currently showing tab.
		 */
	public int getCurrentTabIndex()
	{
		return this.currTab;
	}
	
		/**
		 * Returns the number of visible tabs.
		 */
	public int getNumShowingTabs()
	{
		return nShowing;
	}
}
