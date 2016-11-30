package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class in which some standard symbol types are displayed.
 */
public class MMLTPTemplate extends MMLTemplate
{
		/**
		 * Preferred symbol widths of all the signs.
		 */
	protected final static short PREF_SYM_WIDTH[] =
	{
		20, 25, 25, 20, 20, 
		15, 30, 45, 15, 30, 45,
	};

		/**
		 * Preferred heights of all the signs.
		 */
	protected final static short PREF_SYM_HEIGHT[] =
	{
		40, 40, 40, 40, 40, 
		47, 47, 47, 47, 47, 47,
	};
	
        /**
         * Summation to be used.
         */
    public final static byte SUMMATION = 0;
        
        /**
         * product to be used.
         */
    public final static byte PRODUCT = 1;
        
        /**
         * Coproduct to be used.
         */
    public final static byte COPRODUCT = 2;

        /**
         * Union to be used.
         */
    public final static byte UNION = 3;

        /**
         * Intersection to be used.
         */
    public final static byte INTERSECTION = 4;

        /**
         * Single Integral
         */
    public final static byte S_INTEGRAL = 5;

        /**
         * Double Integral
         */
    public final static byte D_INTEGRAL = 6;

        /**
         * Tripple Integral
         */
    public final static byte T_INTEGRAL = 7;

        /**
         * Contour integral.
         * ie Single integral with a circle.
         */
    public final static byte C_INTEGRAL = 8;

        /**
         * Area integral.
         * ie Double integral with a circle.
         */
    public final static byte A_INTEGRAL = 9;

        /**
         * Volume integral.
         * ie Triple integral with a circle.
         */
    public final static byte V_INTEGRAL = 10;

        /**
         * Tells if the to and from should be placed on the "sides" 
         * or on the "top and bottom"
         */
    boolean top = false;

        /**
         * The type of sign to be drawn.
         */
    protected byte fType = SUMMATION;

        /**
         * Constructor.
         */
    public MMLTPTemplate(byte type, boolean showTo, boolean showFrom, boolean top)
    {
            // we need 3 symbols...
        super(3);

        this.fType = type;
        this.top = top;

            // the main term symbol
        symbols[0] = new MMLParagraph();
        symbols[1] = showTo ? new MMLParagraph() : null;
        symbols[2] = showFrom ? new MMLParagraph() : null;
    }

    protected void drawSymbol(Graphics g, int x, int y, int w, int h)
    {
		int ex = x + w;
		int ey = y + h;
		int w2 = w >> 1;
		int h2 = h >> 1;
		int h4 = h2 >> 1;
		//g.drawRect(x, y, w, h);
        switch(fType)
        {
            case SUMMATION:
            {
				int x2 = (int)(x + (0.6 * w));
				g.drawLine(x, y, ex - 2, y);
				g.drawLine(x + 1, y + 1, ex - 1, y + 1);
				
				g.drawLine(ex - 1, y + 1, ex - 1, y + 7);
				g.drawLine(ex - 2, y, ex - 2, y + 5);
				g.drawLine(ex - 3, y + 3, ex - 3, y + 2);
				g.drawLine(ex - 5, y + 2, ex - 2, y + 2);
				
				g.drawLine(ex, ey - 10, ex, ey - 2);
				g.drawLine(ex - 1, ey - 8, ex - 1, ey);
				g.drawLine(ex - 2, ey - 6, ex - 2, ey);
				g.drawLine(ex - 5, ey - 4, ex - 3, ey - 4);

				for (int i = 2; i <= 6;i++)
				{
					g.drawLine(x + i, y + 2, x2 - 6 + i, y + h2);
					g.drawLine(x2 - 6 + i, y + h2, x +  i, ey - 4);
				}
				g.drawLine(x, ey - 3, ex - 3, ey - 3);
				g.drawLine(x, ey - 2, ex - 3, ey - 2);
				g.drawLine(x, ey - 1, ex - 3, ey - 1);
				g.drawLine(x, ey, ex - 3, ey);
            } break;
            case PRODUCT:
            {
				g.drawLine(x, y, x + w, y);
				g.drawLine(x + 3, y + 1,ex - 3, y+1);
				g.fillRect(x + 6, y + 2, 2, h - 6);
				g.fillRect(x + w - 6, y + 2, 2, h - 6);
				g.drawLine(x + 5, ey - 5, x + 8, ey - 5);
				g.drawLine(x + 2, ey - 4, x + 11, ey - 4);
				g.drawLine(ex - 7, ey - 5, ex - 4, ey - 5);
				g.drawLine(ex - 10, ey - 4, ex - 2, ey - 4);
            } break;
            case COPRODUCT:
            {
				g.drawLine(x, ey, ex, ey);
				g.drawLine(x + 3, ey - 1, ex - 3, ey - 1);
				g.fillRect(x + 6, y + 5, 2, h - 6);
				g.fillRect(ex - 6, y + 5, 2, h - 6);
				g.drawLine(x + 5, y + 4, x + 8, y + 4);
				g.drawLine(x + 2, y + 3, x + 11, y + 3);
				g.drawLine(ex - 7, y + 4, ex - 4, y + 4);
				g.drawLine(ex - 10, y + 3, ex - 2, y + 3);
            } break;
            case UNION:
            {
				g.drawArc(x, ey - w2 - 7, w, w2 + 7, 180, 180);
				g.drawArc(x + 1, ey - w2 - 7, w - 2, w2 + 7, 180, 180);
				g.drawLine(x, y, x, ey - w2);
				g.drawLine(x + 1, y, x + 1, ey - w2);
				g.drawLine(ex, y, ex, ey - w2 + 1);
				g.drawLine(ex - 1, y, ex - 1, ey - w2 + 1);
            } break;
            case INTERSECTION:
            {
				g.drawArc(x, y, w, w2 + 10, 0, 180);
				g.drawArc(x + 1, y, w - 2, w2 + 10, 0, 180);
				g.drawLine(x, y + w2, x, ey);
				g.drawLine(x + 1, y + w2, x + 1, ey);
				g.drawLine(ex, y + w2, x + w, ey);
				g.drawLine(ex - 1, y + w2, ex - 1, ey);
            } break;
			case C_INTEGRAL:
				g.drawOval(x, y + ((h - h4) >> 1), w, h4);
            case S_INTEGRAL: 
            {
				g.fillRect(x + w2, y + 4, 2, h - 8);
				g.fillRect(x + w2 - 1, y + ((h - h2) >> 1), 4, h2);

					// top circle bit
				g.fillRect(x + w2 + 1, y + 2, w2 - 2, 2);
				g.fillRect(x + w2 + 2, y + 1, w2 - 3, 3);
				g.fillRect(x + w2 + 4, y, w2 - 6, 5);

					// bottom circle bit
				g.fillRect(x + 2, ey - 4, w2 - 1, 2);
				g.fillRect(x + 2, ey - 4, w2 - 2, 3);
				g.fillRect(x + 3, ey - 5, w2 - 3, 5);
				g.drawLine(x + 3, ey, x + w2 - 3, ey);
            } break;
			case A_INTEGRAL:
        		g.drawOval(x, y + ((h - h4) >> 1), w, h4);
			case D_INTEGRAL: 
            {
				w2 = w / 3;
				g.fillRect(x + w2, y + 4, 2, h - 8);
				g.fillRect(x + w2 - 1, y + ((h - h2) >> 1), 4, h2);

					// top circle bit
				g.fillRect(x + w2 + 1, y + 2, w2 - 2, 2);
				g.fillRect(x + w2 + 2, y + 1, w2 - 3, 3);
				g.fillRect(x + w2 + 4, y, w2 - 6, 5);

					// bottom circle bit
				g.fillRect(x + 2, ey - 4, w2 - 1, 2);
				g.fillRect(x + 2, ey - 4, w2 - 2, 3);
				g.fillRect(x + 3, ey - 5, w2 - 3, 5);
				g.drawLine(x + 3, ey, x + w2 - 3, ey);
				
					// DRAW THE SECOND INTEGRAL SIGN...
				x += w2;
				g.fillRect(x + w2, y + 4, 2, h - 8);
				g.fillRect(x + w2 - 1, y + ((h - h2) >> 1), 4, h2);

					// top circle bit
				g.fillRect(x + w2 + 1, y + 2, w2 - 2, 2);
				g.fillRect(x + w2 + 2, y + 1, w2 - 3, 3);
				g.fillRect(x + w2 + 4, y, w2 - 6, 5);

					// bottom circle bit
				g.fillRect(x + 2, ey - 4, w2 - 1, 2);
				g.fillRect(x + 2, ey - 4, w2 - 2, 3);
				g.fillRect(x + 3, ey - 5, w2 - 3, 5);
				g.drawLine(x + 3, ey, x + w2 - 3, ey);
            } break;
			case V_INTEGRAL:
				g.drawOval(x, y + ((h - h4) >> 1), w, h4);
            case T_INTEGRAL: 
            {
				w2 = w / 4;
				for (int i = 0;i < 3;i++)
				{
					g.fillRect(x + w2, y + 4, 2, h - 8);
					g.fillRect(x + w2 - 1, y + ((h - h2) >> 1), 4, h2);

						// top circle bit
					g.fillRect(x + w2 + 1, y + 2, w2 - 2, 2);
					g.fillRect(x + w2 + 2, y + 1, w2 - 3, 3);
					g.fillRect(x + w2 + 4, y, w2 - 6, 5);

						// bottom circle bit
					g.fillRect(x + 2, ey - 4, w2 - 1, 2);
					g.fillRect(x + 2, ey - 4, w2 - 2, 3);
					g.fillRect(x + 3, ey - 5, w2 - 3, 5);
					g.drawLine(x + 3, ey, x + w2 - 3, ey);
					x += w2;
				}
            } break;
        }
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey, 
					 MMLCursor cursor, boolean drawBorder)
    {
        int symH = symbols[0].height;
        int symW = PREF_SYM_WIDTH[fType];
        if (symH < PREF_SYM_HEIGHT[fType]) symH = PREF_SYM_HEIGHT[fType];
		
		//g.drawRect(sx, sy, width, height);
		//TODO:: Set the colors of what we are gonna draw... based on
		// on whether we are highlighting ornot...
        if (top)
        {
            int p1w = symW;
            int cy = sy + 1;
            if (symbols[1] != null && p1w < symbols[1].width) p1w = symbols[1].width;
            if (symbols[2] != null && p1w < symbols[2].width) p1w = symbols[2].width;
            if (symbols[1] != null)
            {
                symbols[1].draw(g, sx + ((p1w - symbols[1].width) >> 1), sy + 1, ex, ey, cursor, true);
                cy += symbols[1].height + 3;
            }
            int tx =sx + ((p1w - symW) >> 1);

            drawSymbol(g, tx, cy, symW, symH);

                // finally draw the main one...
            //symbols[0].draw(g, sx + p1w + 2, sy + ((height - symbols[0].height) >> 1), ex, ey, cursor);
            symbols[0].draw(g, tx + symW + 2, cy + ((symH - symbols[0].height) >> 1), 
							ex, ey, cursor, true);

            cy += symH + 1;
                // draw the bottom one if space exists...
            if (symbols[2] != null)
            {
                symbols[2].draw(g, sx + ((p1w - symbols[2].width) >> 1), cy + 1, ex, ey, cursor, true);
            }

        } else
        {
			int m2 = 0;
			int cy = sy;
			int symY = sy + 4;
			symH = height - 8;
			if (symbols[1] != null)
			{
				symbols[1].draw(g, sx + symW + 3, sy, ex, ey, cursor, true);
				m2 = symbols[1].width;
				cy = symbols[1].height + 2;
				symY += (symbols[1].height >> 1);
				symH -= (symbols[1].height >> 1);
			}
			
			if (symbols[2] != null)
			{
				symbols[2].draw(g, sx + symW + 3, sy + height - symbols[2].height, ex, ey, cursor, true);
				if (symbols[2].width > m2) m2 = symbols[2].width;
				symH -= (symbols[2].height >> 1);
			}
			
			symbols[0].draw(g, sx + symW + m2 + 4, symY /*+ ((height - symbols[0].height) >> 1)*/,
							ex, ey, cursor, true);
			
			this.drawSymbol(g, sx + 1, symY, symW, symH);
        }
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
		
        if (prev.holder == symbols[0])
        {
            if (kc == KeyEvent.VK_LEFT) 
            {
                if (symbols[1] != null) return (MMLNavigateableSymbol)symbols[1];
                else if (symbols[2] != null) return (MMLNavigateableSymbol)symbols[2];
            } else if (kc == KeyEvent.VK_UP && symbols[1] != null)
            {
                return (MMLNavigateableSymbol)symbols[1];
            } else if (kc == KeyEvent.VK_DOWN && symbols[2] != null)
            {
                return (MMLNavigateableSymbol)symbols[2];
            }
        } else if (prev.holder == symbols[2] && kc == KeyEvent.VK_UP)
        {
            if (kc == KeyEvent.VK_UP)
            {
                return (MMLNavigateableSymbol)(symbols[1] == null ? symbols[0] : symbols[1]);
            } else if (kc == KeyEvent.VK_RIGHT)
            {
                return (MMLNavigateableSymbol)(symbols[0]);
            }
        } else if (prev.holder == symbols[1])
        {
            if (kc == KeyEvent.VK_DOWN)
            {
                return (MMLNavigateableSymbol)(symbols[2] == null ? symbols[0] : symbols[2]);
            } else if (kc == KeyEvent.VK_RIGHT)
            {
                return (MMLNavigateableSymbol)(symbols[0]);
            }
        }
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
        int kc = e.getKeyCode();
        if (kc == KeyEvent.VK_DOWN)
        {
			return symbols[symbols[1] == null ? 1 : 0];
        } else if (kc == KeyEvent.VK_UP)
        {
			return symbols[symbols[2] == null ? 2 : 0];
        } else if (kc == KeyEvent.VK_RIGHT)
        {
            if (symbols[1] != null) return symbols[1];
            else if (symbols[2] != null) return symbols[2];
        }
        return symbols[0];
    }

        /**
         * Recalculat the size.
         */
    public void recalculateBounds(Graphics g)
    {
        short maxW = 0;
        short h1 = 0, h2 = 0;
        short w1 = 0, w2 = 0;
		short wm = 5;

        symbols[0].recalculateBounds(g);
        height = symbols[0].height;

        if (symbols[1] != null) 
        {
            symbols[1].recalculateBounds(g);
            h1 = symbols[1].height;
			w1 = symbols[1].width;
			if (w1 > wm) wm = w1;
        }

        if (symbols[2] != null) 
        {
            symbols[2].recalculateBounds(g);
			w2 = symbols[2].width;
            h2 = symbols[2].height;
			if (w2 > wm) wm = w2;
        }

		if (height < PREF_SYM_HEIGHT[fType]) height = PREF_SYM_HEIGHT[fType];
		if (top)
		{
	        height += h1 + h2;
			if (wm < PREF_SYM_WIDTH[fType]) wm = PREF_SYM_WIDTH[fType];
			
			width = (short)(((wm + PREF_SYM_WIDTH[fType]) >> 1) + symbols[0].width);
			if (width < wm) width = wm;
		} else
		{
			if (height < (h1 + h2)) height = (short)(h1 + h2);
			width = (short)(wm + PREF_SYM_WIDTH[fType] + symbols[0].width);
		}
		width += 10;
		height += 8;
    }
	
		/**
		 * Given a point where the mouse was pressed, returns the symbol
		 * that is at the point.
		 * The cursor information MUST be set by the symbol.
		 * null is returned if no navigateable symbol is found
		 * at the point.
		 * 
		 * One thing to note is that if we are in this function,
		 * that means the cursor IS WITHIN this symbol.  This has to
		 * be ensured by the code that calls this function.  This
		 * function should not even be called if other wise.
		 */
	public MMLNavigateableSymbol mousePressed(Point p,MMLCursor cursor)
	{
			// see in which one the mouse can be in!!!
        int symH = symbols[0].height;
        int symW = PREF_SYM_WIDTH[fType];
		int x1, y1, w1, h1;
        if (symH < PREF_SYM_HEIGHT[fType]) symH = PREF_SYM_HEIGHT[fType];
		
		int sx = 0, sy = 0;
        if (top)
        {
            int p1w = symW;
            int cy = sy + 1;
            if (symbols[1] != null && p1w < symbols[1].width) p1w = symbols[1].width;
            if (symbols[2] != null && p1w < symbols[2].width) p1w = symbols[2].width;
            if (symbols[1] != null)
            {
				x1 = sx + ((p1w - symbols[1].width) >> 1);
				y1 = sy + 1;
				if (p.x >= x1 && p.y >= y1 && 
					(p.x < x1 + symbols[1].width) && 
					(p.y < y1 + symbols[1].height))
				{
					return (MMLNavigateableSymbol)symbols[1];
				}
            }
            int tx =sx + ((p1w - symW) >> 1);

                // finally draw the main one...
			x1 = tx + symW + 2;
			y1 = cy + ((symH - symbols[0].height) >> 1);
			if (p.x >= x1 && p.y >= y1 && 
				(p.x < x1 + symbols[0].width) && 
				(p.y < y1 + symbols[0].height))
			{
				return (MMLNavigateableSymbol)symbols[0];
			}

            cy += symH + 1;
                // draw the bottom one if space exists...
            if (symbols[2] != null)
            {
				x1 = sx + ((p1w - symbols[2].width) >> 1);
				y1 = cy + 1;
				if (p.x >= x1 && p.y >= y1 && 
					(p.x < x1 + symbols[2].width) && 
					(p.y < y1 + symbols[2].height))
				{
					return (MMLNavigateableSymbol)symbols[2];
				}
            }
        } else
        {
			int m2 = 0;
			int cy = sy;
			int symY = sy + 4;
			symH = height - 8;
			if (symbols[1] != null)
			{
				x1 = sx + symW + 3;
				y1 = sy;
				if (p.x >= x1 && p.y >= y1 && 
					(p.x < x1 + symbols[1].width) && 
					(p.y < y1 + symbols[1].height))
				{
					return (MMLNavigateableSymbol)symbols[1];
				}
				m2 = symbols[1].width;
				cy = symbols[1].height + 2;
				symY += (symbols[1].height >> 1);
				symH -= (symbols[1].height >> 1);
			}

			if (symbols[2] != null)
			{
				x1 = sx + symW + 3;
				y1 = sy + height - symbols[2].height;
				if (p.x >= x1 && p.y >= y1 && 
					(p.x < x1 + symbols[2].width) && 
					(p.y < y1 + symbols[2].height))
				{
					return (MMLNavigateableSymbol)symbols[2];
				}
				if (symbols[2].width > m2) m2 = symbols[2].width;
				symH -= (symbols[2].height >> 1);
			}

			x1 = sx + symW + m2 + 4;
			y1 = symY /*+ ((height - symbols[0].height) >> 1)*/;
			if (p.x >= x1 && p.y >= y1 && 
				(p.x < x1 + symbols[0].width) && 
				(p.y < y1 + symbols[0].height))
			{
				return (MMLNavigateableSymbol)symbols[0];
			}
        }
		return null;
	}
}
