package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

	/**
	 * This class is a child class of the Shape object which 
	 * describes a Rectangle.
	 */
public class Rect extends BoundedShape
{
		/**
		 * Constructor.
		 */
	public Rect()
	{
	}
	
		/**
		 * Paints this Shape object at the given location and
		 * at the given mode.
		 */
	public void paint(Graphics g, int xOffset, int yOffset)
	{
        int tx = x + xOffset;
        int ty = y + yOffset;
        int w2 = width;
        int h2 = height;
		int t1 = tx + w2 / 2;


            // if the shape isnt visible why draw it?
        if (tx + width >= 0 && ty + height >= 0) 
        {

            if (w2 < 0) 
            {
                tx += w2;
                w2 = -w2;
            }
            if (h2 < 0) 
            {
                ty += h2;
                h2 = -h2;
            }


            g.setColor(Color.white);
            g.fillRect(tx, ty, w2, h2);

            g.setColor(Color.black);

            g.drawRect(tx, ty, w2, h2);

            super.paint(g, xOffset, yOffset);
        }
	}
}
