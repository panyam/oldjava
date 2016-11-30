package com.sri.gui.ext.drawing.selectors;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.ext.drawing.*;

public abstract class SelectorPanel extends Panel implements MouseListener, MouseMotionListener, ItemSelectable
{
	protected int prefItemHeight = 15;
	protected int numStyles = 3;
	protected int currItem = 0;
	protected int currPressedItem = -1;
	protected int currMovingItem = -1;
	protected Image buffer = null;
	protected Graphics bg = null;
	protected Dimension bs = new Dimension();
	protected transient ItemListener itemListener = null;
    protected InfoListener infoListener;
					
	protected SelectorPanel(InfoListener infoListener) 
	{
		setForeground(Color.black);
		setBackground(Color.lightGray);
		addMouseListener(this);
		addMouseMotionListener(this);

        this.infoListener = infoListener;
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(4 + 75,4 + numStyles * prefItemHeight);
	}

	public void mousePressed(MouseEvent e)
	{
		mouseDragged(e);
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if (e.getSource() == this && isEnabled())
		{
			int c = pointToIndex(e.getX(),e.getY());
			if (c >= 0)
			{
				currPressedItem = currMovingItem = -1;
				currItem = c;
				generateEvent(c);
			}
			paint(getGraphics());
		}
	}
	
	public void mouseExited(MouseEvent e)
	{
		currPressedItem = currMovingItem = -1;
		paint(getGraphics());
	}
	
	public void mouseEntered(MouseEvent e) { }
	
	public void mouseClicked(MouseEvent e) { }
	
	public void mouseMoved(MouseEvent e)
	{	
		if (e.getSource() == this && isEnabled())
		{
			currPressedItem = -1;
			int mouseOn = pointToIndex(e.getX(),e.getY());
			if (mouseOn != this.currMovingItem)
			{
				currMovingItem = mouseOn;
				paint(getGraphics());
			}
		}
	}
	public void mouseDragged(MouseEvent e)
	{
		if (e.getSource() == this && isEnabled())
		{
			this.currMovingItem = -1;
			int pr = pointToIndex(e.getX(),e.getY());
			if (pr != currPressedItem)
			{
				currPressedItem = pr;
				paint(getGraphics());
			}
		}
	}
	
	public void addItemListener(ItemListener l)
	{
		if (l != null)
		{
			itemListener = AWTEventMulticaster.add(itemListener,l);
		}
	}
	
	public void removeItemListener(ItemListener l)
	{
		if (l != null)
		{
			itemListener = AWTEventMulticaster.remove(itemListener,l);
		}
	}

    public Object [] getSelectedObjects() {
        Object out[] = { this };
        return out;
    }
	
	protected void processItemEvent(ItemEvent e) {
		if (e != null && itemListener != null) {
			itemListener.itemStateChanged(e);
		}
	}
	
	protected abstract int pointToIndex(int x,int y);
	
	protected void prepareBuffer(Dimension d)
	{
		if (buffer == null || d.width != bs.width || bs.height != d.height)
		{
			if (buffer != null) buffer.flush();
			if (bg != null) bg.dispose();
			
			bs.width = Math.max(1,d.width);
			bs.height = Math.max(1,d.height);
			
			buffer = createImage(bs.width,bs.height);
			if (buffer == null) return ;
			bg = buffer.getGraphics();
		}
		if (bg == null) return ;
		bg.setColor(getBackground());
		bg.fillRect(0,0,bs.width,bs.height);
	}
	
	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();
		prepareBuffer(d);
		if (buffer != null) g.drawImage(buffer,0,0,this);
	}
	
	protected abstract void generateEvent(int which);
}
