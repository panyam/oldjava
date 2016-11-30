package com.sri.gui.core.tooltips;

import java.awt.*;
import java.util.*;

public class ToolTip extends Component
{
		/**
		 * String to be shown.
		 */
	protected String str = "";
	
		/**
		 * Preferred size of the tooltip.
		 */
	protected Dimension prefSize = null;
	
		/**
		 * Constructor.
		 */
	public ToolTip(String s) {
		this.str = s;
	}

		/**
		 * Returns the most appropriate size.
		 */
	public Dimension getPreferredSize() {
		if (prefSize == null) paint(getGraphics());
		if (prefSize == null) prefSize = new Dimension(5,5);
		return prefSize;
	}
	
		/**
		 * Paints the tooltip.
		 */
	public void paint(Graphics g) {
		Dimension d = getSize();
		g.setColor(Color.yellow);
		g.fillRect(0,0,d.width,d.height);
		g.setColor(Color.black);
		g.drawRect(0,0,d.width - 1,d.height - 1);
		FontMetrics fm = g.getFontMetrics();
		
		int h = fm.getAscent() + 5;
		int x = 2, y = h;
		int nLines = 0;
		int maxW = 0;
		
		StringTokenizer tokens = new StringTokenizer(str,"\n",false);
		while (tokens.hasMoreTokens()) {
			String n = tokens.nextToken();
			nLines ++;
			g.drawString(n,x,y);
			int w = fm.stringWidth(n);
			if (maxW < w) maxW = w;
			y += h;
		}
		if (prefSize != null) prefSize = new Dimension();
		prefSize.width = maxW + 5;
		prefSize.height = (h * nLines);
	}
}