package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class in which fraction symbols can be typed.
 */
public class MMLFractionTemplate extends MMLTemplate
{
        /**
         * Thickness of the bar to be displayed.
         */
    byte thickness = 1;

        /**
         * Constructor.
         */
    public MMLFractionTemplate()
	{
		this(1);
	}
	
        /**
         * Constructor.
         */
    public MMLFractionTemplate(int thickness)
    {
            // only need two...
            // one for the numerator and one for the denominator
        super(2);
        this.thickness = (byte)thickness;
        symbols[0] = new MMLParagraph();
        symbols[1] = new MMLParagraph();
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
        int ex2 = sx + width;
        symbols[0].draw(g, sx + ((width - symbols[0].width) >> 1), sy + 2, ex, ey, cursor, true);
        int currY = sy + 5 + symbols[0].height;
            // draw the bar...
        if (currY < ey)
        {
            for (int i = 0;i < thickness;i++)
            {
                g.drawLine(sx, currY, ex2, currY++);
            }
            
            if (currY < ey)
            {
                symbols[1].draw(g, sx + ((width - symbols[1].width) >> 1), currY + 2, 
								ex, ey, cursor, true);
            }
        }
    }

        /**
         * Enter into the next container.
         *
         * @param kc    The key stroke used to enter this container.
         * @param old   The cursor information of the "exiting" container
         * @param curr  The cursor information of the current container.
         *              This will be updated by the enter function.
         */
    public MMLNavigateableSymbol enter (KeyEvent e, MMLCursor old, MMLCursor curr)
    {
				// regardless of the key pressed,  
				// you always enter ot the numerator symbol
        return (MMLNavigateableSymbol)symbols[0];
    }

        /**
         * Enter into the next container.
         */
    public MMLNavigateableSymbol exit(KeyEvent e, MMLCursor prev, MMLCursor next)
    {
        int kc = e.getKeyCode();
		
            // see where we are exiting from...
        if (prev.holder == symbols[0])
		{
			if (kc == KeyEvent.VK_DOWN)
			{
				return (MMLNavigateableSymbol)symbols[1];
			} 
        } else if (prev.holder == symbols[1])
        {
			if (kc == KeyEvent.VK_UP)
			{
				return (MMLNavigateableSymbol)symbols[0];
			} 
        }
				// otherwise you are exiting this symbol 
				// to go to the parent
        return null;
    }

        /**
         * Recalculat the size.
         */
    public void recalculateBounds(Graphics g)
    {
        symbols[0].recalculateBounds(g);
        symbols[1].recalculateBounds(g);

        width = (short)(2 + Math.max(symbols[0].width, symbols[1].width));
        height = (short)(2 + symbols[0].height + (0xff & thickness) + symbols[1].height);
    }

		/**
		 * Given the mouse pressed location, returns the MMLNavigateableSymbol
		 * at the that point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p, MMLCursor cursor)
	{
		return ((MMLNavigateableSymbol)(p.y < symbols[0].height ? 
										symbols[0] : symbols[1]));
	}
}
