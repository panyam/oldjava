package com.sri.apps.mml;

import java.awt.*;
import java.util.*;

/**
 * The super class of all symbols.
 */
public abstract class MMLSymbol
{
	protected final static int DEFAULT_LINE_HEIGHT = 25;
	protected final static int DEFAULT_LINE_WIDTH = 12;
	
        /**
         * Size of the symbol.
         */
    short width, height;

        /**
         * All the attributes of this symbol.
         */
    protected MMLAttribute attribs[] = null;

        /**
         * Find the index of an attribute given an attribute name;
         */
    public int findAttributeIndex(String name)
    {
        return 0;
    }

        /**
         * Add a new attribute.
         */
    public void addAttribute(MMLAttribute attr)
    {
            // if attribute already exists, then simply replace it
        int ind = findAttributeIndex(attr.getName());
        if (ind >= 0)
        {
            attribs[ind] = attr;
            return ;
        }

            // increase the attrib array length by one...
        if (attribs == null) attribs = new MMLAttribute[1];
        else
        {
            MMLAttribute at2[] = attribs;
            attribs = new MMLAttribute[attribs.length + 1];
            System.arraycopy(at2, 0, attribs, 0, at2.length);
        }
        attribs[attribs.length - 1] = attr;
    }

        /**
         * Recalculates the preferred size and 
         * location of this symbol.
         */
    public abstract void recalculateBounds(Graphics g);

        /**
         * Draws the symbol onto a graphics context
         * at the given location.
         */
    public abstract void draw(Graphics g,
                              int sx, int sy, int ex, int ey,
                              MMLCursor cursor, boolean drawBorder);
	
	protected void drawDashedRectangle(Graphics g, int x, int y, int w, int h)
	{
		for (int i = x; i <= x + w;i += 4)
		{
			g.drawLine(i, y, i + 2, y);
			g.drawLine(x + w - i, y + h, x + w - (i + 2), y + h);
		}

		for (int i = y; i <= y + h;i += 4)
		{
			g.drawLine(x, i, x, i + 2);
			g.drawLine(x + w, y + h - i, x + w, y + h - (i + 2));
		}
	}
}
