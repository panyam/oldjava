package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class in which fraction symbols can be typed.
 */
public class MMLTableTemplate extends MMLTemplate
{
        /**
         * Number of rows.
         */
    int nRows;

        /**
         * Number of columns.
         */
    int nCols;

        /**
         * The maximum columns width of each column.
         */
    int colWidth[];

        /**
         * Maximum row height fo each row.
         */
    int rowHeight[];

        /**
         * Constructor.
         */
    public MMLTableTemplate(int nr, int nc)
    {
        super(nr * nc);
        this.nRows = nr;
        this.nCols = nc;

        colWidth = new int[nCols];
        rowHeight = new int[nRows];

        for (int i = 0;i < nRows;i++) rowHeight[i] = 0;
        for (int i = 0;i < nCols;i++) colWidth[i] = 0;

        for (int i = 0;i < symbols.length;i++)
        {
            symbols[i] = new MMLParagraph();
        }
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey, 
					 MMLCursor cursor, boolean drawBorder)
    {
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
            return (MMLNavigateableSymbol)symbols[nCols * (nRows - 1)];
        } else if (kc == KeyEvent.VK_DOWN || kc == KeyEvent.VK_RIGHT)
        {
            return (MMLNavigateableSymbol)symbols[0];
        } else if (kc == KeyEvent.VK_RIGHT)
        {
            return (MMLNavigateableSymbol)symbols[nCols - 1];
        }
        return (MMLNavigateableSymbol)symbols[0];
    }

        /**
         * Enter into the next container.
         */
    public MMLNavigateableSymbol exit(KeyEvent e, MMLCursor prev, MMLCursor next)
    {
        int kc = e.getKeyCode();
				// otherwise you are exiting this symbol 
				// to go to the parent
        return null;
    }

        /**
         * Recalculat the size.
         */
    public void recalculateBounds(Graphics g)
    {
    }

		/**
		 * Given the mouse pressed location, returns the MMLNavigateableSymbol
		 * at the that point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p, MMLCursor cursor)
	{
		return null;
	}
}
