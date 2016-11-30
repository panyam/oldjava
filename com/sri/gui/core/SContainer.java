package com.sri.gui.core;

import java.awt.*;

public class SContainer extends Container {
	protected Image buffer = null;
	protected Dimension buffSize = new Dimension();
	protected Graphics bg = null;
	protected Insets insets = new Insets(0,0,0,0);
	
	public void setInsets(int l, int r,int t,int b)
	{
		insets.top = t;
		insets.bottom = b;
		insets.right = r;
		insets.left = l;
	}
	
		/**
		 * Returns the insets
		 */
	public Insets getInsets()
	{
		return insets;
	}
	
	protected void createBuffer(Dimension d)
	{
		if (buffer == null || d.width != buffSize.width || d.height != buffSize.height) {
			if (bg != null) bg.dispose();
			if (buffer != null) buffer.flush();
			
			buffSize.width = Math.max(1,d.width);
			buffSize.height = Math.max(1,d.height);
			buffer = createImage(buffSize.width,buffSize.height);
			
			if (buffer == null) return ;
			bg = buffer.getGraphics();
		}
	}
	
	protected void prepareBuffer(Dimension d) {
		this.createBuffer(d);
		if (bg == null) return ;
		//bg.setColor(getBackground());
		bg.clearRect(0,0,buffSize.width - 1,buffSize.height - 1);
		super.paint(bg);
	}

    public void paint(Graphics g) {
		if (g == null) return ;
		super.paint(g);		
		//prepareBuffer(); if (buffer != null) g.drawImage(buffer,0,0,this);
    }
}