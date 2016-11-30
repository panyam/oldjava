package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class in which fraction symbols can be typed.
 */
public class MMLRootTemplate extends MMLTemplate
{
        /**
         * Constructor.
         * @param square    Tells if it is a square root or normal root.
         */
    public MMLRootTemplate(boolean square)
    {
            // only need two...
            // one for the numerator and one for the denominator
        super(2);
        symbols[0] = new MMLParagraph();
        if (!square) symbols[1] = new MMLParagraph();
		else symbols[1] = null;
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
		int ex2 = sx + width;
		
		if (symbols[1] != null)
		{
			symbols[1].draw(g, sx + 1, sy + ((height - symbols[1].height) >> 1), 
							ex, ey, cursor, true);
			sx += symbols[1].width + 1;
		}

			// now draw the root symbol and the first symbol...
		g.drawLine(sx, sy + height, sx - 3, sy + height - 5);
		g.drawLine(sx, sy + height, sx, sy);
		g.drawLine(sx, sy, ex2, sy);
        
        symbols[0].draw(g, sx + 5, sy + ((height - symbols[0].height) >> 1), 
						ex, ey, cursor, true);
    }

        /**
         * Exit from a CHILD symbol.
         *
         * @param kc    The key stroke used to enter this container.
         * @param old   The cursor information of the "exiting" container
         * @param curr  The cursor information of the current container.
         *              This will be updated by the enter function.
         */
    public MMLNavigateableSymbol exit (KeyEvent e, MMLCursor prev, MMLCursor next)
    {
        int kc = e.getKeyCode();
		
            // see where we are exiting from...
        if (prev.holder == symbols[0])
		{
			if (kc == KeyEvent.VK_LEFT && symbols.length > 1)
			{
				return (MMLNavigateableSymbol)symbols[1];
			} 
        } else if (prev.holder == symbols[1])
        {
			if (kc == KeyEvent.VK_RIGHT)
			{
				return (MMLNavigateableSymbol)symbols[0];
			} 
        }
				// otherwise you are exiting this symbol 
				// to go to the parent
        return null;
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
		int kc = e.getKeyCode();
		int n = symbols.length;
		if (kc == KeyEvent.VK_LEFT) 
		{
			return symbols[0];
		}
		else 
		{
			return symbols[symbols.length > 1 ? 1 : 0];
		}		
    }

        /**
         * Recalculat the size.
         */
    public void recalculateBounds(Graphics g)
    {
		int w = 0, h = 0;
        symbols[0].recalculateBounds(g);
        if (symbols[1] != null) 
		{
			symbols[1].recalculateBounds(g);
			w = symbols[1].width;
			h = symbols[1].height;
		}

        height = (short)(2 + Math.max(symbols[0].height, h));
        width = (short)(7 + symbols[0].width + w);
    }

		/**
		 * Given the mouse pressed location, returns the MMLNavigateableSymbol
		 * at the that point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p, MMLCursor cursor)
	{
		if (symbols[1] != null)
		{
			if (p.x < symbols[1].width) 
			{
				return (MMLNavigateableSymbol)symbols[1];
			}
		}
		return ((MMLNavigateableSymbol)symbols[0]);
	}
}
