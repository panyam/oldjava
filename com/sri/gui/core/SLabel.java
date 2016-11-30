package com.sri.gui.core;

import java.awt.*;

public class SLabel extends Component
{
	protected String text;
	
	public final static byte LEFT = 0;
	public final static byte RIGHT = 2;
	public final static byte CENTER = 1;
	public final static byte TOP = 1;
	public final static byte BOTTOM = 2;
	
		/**
		 * Return the preferred size.
		 */
	protected Dimension prefSize = new Dimension(20, 20);
	
		/**
		 * Vertical alignment
		 */
	protected byte vAlign = CENTER;
	
		/**
		 * Horizontal alignment
		 */
	protected byte hAlign = CENTER;
	
		/**
		 * Constructor.
		 */
	public SLabel(String s, int val, int hal)
	{
		setText(s);
		setVAlign(val);
		setHAlign(hal);
	}
	
		/**
		 * Constructor.
		 */
	public SLabel(String s)
	{
		this(s, CENTER, CENTER);
	}
	
		/**
		 * Set the text.
		 */
	public void setText(String s)
	{
		this.text = s;
		paint(getGraphics());
	}
	
		/**
		 * Set the vertical alignment.
		 */
	public void setVAlign(int v)
	{
		vAlign = (byte) v;
		paint(getGraphics());
	}
	
		/**
		 * Set the horizontal alignment.
		 */
	public void setHAlign(int h)
	{
		hAlign = (byte)h;
		paint(getGraphics());
	}
	
		/**
		 * Paint the string.
		 */
	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();
		g.setFont(g.getFont());
		g.setColor(getBackground());
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(this.getForeground());
		FontMetrics fm = g.getFontMetrics();
		int x = 0;
		int y = fm.getAscent();
		if (vAlign == CENTER) y = (d.height + y) / 2;
		else if (vAlign == BOTTOM) y = d.height;
		
		if (hAlign == RIGHT) x = d.width - fm.stringWidth(text);
		else if (hAlign == CENTER) x = (d.width - fm.stringWidth(text)) / 2;
		g.drawString(text, x, y);
	}
	
		/**
		 * Update
		 */
	public void update(Graphics g)
	{
	}

		/**
		 * Get the preferred size.
		 */
	public Dimension getPreferredSize()
	{
		Graphics g = getGraphics();
		if (g == null) prefSize.width = prefSize.height = 50;
		else 
		{
			FontMetrics fm = g.getFontMetrics();
			prefSize.width = fm.stringWidth(text) + 5;
			prefSize.height = fm.getMaxAscent() + 3;
		}
		return prefSize;
	}
}
