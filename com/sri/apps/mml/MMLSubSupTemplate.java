package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;

/**
 * A template to allow subscripts and super scripts
 */
public class MMLSubSupTemplate extends MMLTemplate
{
		/**
		 * Tells if the superscript and subscripts are to 
		 * be shown before or after.
		 */
	boolean before;

        /**
         * Tells if the to and from should be placed on the "sides" 
         * or on the "top and bottom"
         */
    boolean top = false;

        /**
         * Constructor.
         * @param square    Tells if it is a square root or normal root.
         */
    public MMLSubSupTemplate(boolean bef, boolean showTo, boolean showFrom, boolean top)
    {
            // only need two...
            // one for the numerator and one for the denominator
        super(3);
		this.before = bef;
		this.top = top;
        symbols[0] = new MMLParagraph();
		symbols[1] = showTo ? new MMLParagraph() : null;
		symbols[2] = showFrom ? new MMLParagraph() : null;
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
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
        if (kc == KeyEvent.VK_DOWN)
	    {
		    return (MMLNavigateableSymbol)(symbols[1] == null ?
			                            symbols[1] : symbols[0]);
        } else if (kc == KeyEvent.VK_UP)
	    {
		    return (MMLNavigateableSymbol)(symbols[2] == null ?
			                            symbols[2] : symbols[0]);
        } else 
		{
			if (before)
			{
				if (kc == KeyEvent.VK_LEFT)
			    {
				    if (symbols[1] != null) return (MMLNavigateableSymbol)symbols[1];
					else if (symbols[2] != null) return (MMLNavigateableSymbol)symbols[2];
		            else return (MMLNavigateableSymbol)symbols[0];
				}
			} else
			{
				if (kc == KeyEvent.VK_RIGHT)
			    {
				    if (symbols[1] != null) return (MMLNavigateableSymbol)symbols[1];
					else if (symbols[2] != null) return (MMLNavigateableSymbol)symbols[2];
		            else return (MMLNavigateableSymbol)symbols[0];
				}
			}
        }
        return (MMLNavigateableSymbol)symbols[0];
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
		if (kc == KeyEvent.VK_UP)
		{
			if (old.holder == symbols[0] && symbols[1] != null)
			{
				return (MMLNavigateableSymbol)symbols[1];
			} else if (symbols[1] != null && old.holder == symbols[2])
			{
				return (MMLNavigateableSymbol)symbols[1];
			}
		} else if (kc == KeyEvent.VK_DOWN)
		{
			if (old.holder == symbols[0] && symbols[2] != null)
			{
				return (MMLNavigateableSymbol)symbols[2];
			} else if (symbols[2] != null && old.holder == symbols[1])
			{
				return (MMLNavigateableSymbol)symbols[2];
			}
		} else
		{
			if (before)
			{
				if (kc == KeyEvent.VK_LEFT) return (MMLNavigateableSymbol)symbols[0];
				else if (kc == KeyEvent.VK_RIGHT)
				{
					if (symbols[1] != null) return (MMLNavigateableSymbol)symbols[1];
					else if (symbols[2] != null) return (MMLNavigateableSymbol)symbols[2];
					return (MMLNavigateableSymbol)symbols[0];
				}
			} else
			{
				if (kc == KeyEvent.VK_RIGHT) return (MMLNavigateableSymbol)symbols[0];
				else if (kc == KeyEvent.VK_LEFT)
				{
					if (symbols[1] != null) return (MMLNavigateableSymbol)symbols[1];
					else if (symbols[2] != null) return (MMLNavigateableSymbol)symbols[2];
					return (MMLNavigateableSymbol)symbols[0];
				}
			}
		}
		return null;
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
		cursor.currLine = cursor.currPos = 0;
		return ((MMLNavigateableSymbol)symbols[0]);
	}
}
