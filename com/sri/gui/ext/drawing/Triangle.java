package com.sri.gui.ext.drawing;

import java.awt.*;

/**
 * A Shape that represents a triangle.
 * 
 * The control point pretty much controls one of the angles.
 */
public class Triangle extends BoundedShape
{
		/**
		 * Constructor.
		 */
	public Triangle()
	{
			// create the control point...
		super.setVisiblePoints(Shape.SHOW_DEFAULT_POINTS);
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
            int xs[] = new int[3], ys[] = new int[3];
            xs[0] = tx + (width / 2);
            ys[0] = ty;

            xs[1] = tx + width;
            ys[1] = ys[2] = ty + height;

            xs[2] = tx;

            g.setColor(Color.white);
            g.fillPolygon(xs, ys, 3);

            g.setColor(Color.black);
            g.drawPolygon(xs, ys, 3);

            super.paint(g, xOffset, yOffset);
        }
	}
}
