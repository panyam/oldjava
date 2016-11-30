package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class where child symbols are surrounded by
 * brackets.
 * 
 * This is the most generic of the bracket operators.
 * The separators are given in the characters and these
 * are ofcourse optional.
 */
public class MMLFenceTemplate extends MMLTemplate
{
        /**
         * Use No brackets.
         */
    public final static byte NO_BRACKET = 0;

        /**
         * Use opening round brackets.
         */
    public final static byte O_ROUND_BRACKET = 1;

        /**
         * Use closgin round brackets.
         */
    public final static byte C_ROUND_BRACKET = 2;

        /**
         * Use opening curly brackets.
         */
    public final static byte O_CURLY_BRACKET = 3;

        /**
         * Use closing curly brackets.
         */
    public final static byte C_CURLY_BRACKET = 4;

        /**
         * Use opening square brackets.
         */
    public final static byte O_SQUARE_BRACKET = 5;

        /**
         * Use closing square brackets.
         */
    public final static byte C_SQUARE_BRACKET = 6;

        /**
         * Use opening angle brackets.
         */
    public final static byte O_ANGLE_BRACKET = 7;

        /**
         * Use closing angle brackets.
         */
    public final static byte C_ANGLE_BRACKET = 8;

        /**
         * Use single bar brackets.
         */
    public final static byte S_BAR_BRACKET = 9;

        /**
         * Use double brackets.
         */
    public final static byte D_BAR_BRACKET = 10;

        /**
         * Use open ceiling bracket.
         */
    public final static byte O_CEIL_BRACKET = 11;

        /**
         * Use closing ceiling bracket.
         */
    public final static byte C_CEIL_BRACKET = 12;

        /**
         * Use opening floor bracket
         */
    public final static byte O_FLOOR_BRACKET = 13;

        /**
         * Use closing floor bracket.
         */
    public final static byte C_FLOOR_BRACKET = 14;

		/**
		 * The width of the separator
		 * charactors.
		 */
	protected static int SEPARATOR_WIDTH = 0;
										  
	char separators[];
	
        /**
         * Opening bracket type.
         */
    byte openBracket = O_ROUND_BRACKET;

        /**
         * Closing bracket type.
         */
    byte closeBracket = C_ROUND_BRACKET;

        /**
         * The padding width.
         */
    private static short W2 = 10;

        /**
         * The padding height.
         */
    private static short H2 = 8;

        /**
         * Constructor.
         */
    public MMLFenceTemplate(int nSyms, byte open, byte close, String seps)
    {
		super(nSyms);
		this.separators = new char[nSyms - 1];
		for (int i = 0;i < nSyms - 1;i++)
		{
			symbols[i] = new MMLParagraph();
			this.separators[i] = (i < seps.length() ? seps.charAt(i) : ',');
		}
		symbols[nSyms - 1] = new MMLParagraph();
        openBracket = open;
        closeBracket = close;
    }

    protected void drawBracket(Graphics g, int x, int y, int w, int h,
							   byte bracketType)
    {
		int w2 = w >> 1;
        switch(bracketType)
        {
            case O_CURLY_BRACKET:
			{
				int h2 = h >> 1;
				g.drawArc(x + w2, y, w, h2 - 1, 90, 180);
				g.drawArc(x + w2, y + h2 + 1, 
						  w, h2 - 1, 90, 180);
				g.drawLine(x + w, y + h2 - 1, x + w2, y + h2);
				g.drawLine(x + w, y + h2 + 1, x + w2, y + h2);
			} break;
            case O_ROUND_BRACKET:
            {
				g.drawArc(x, y, w, h, 90, 180);
            } break;
            case C_CURLY_BRACKET:
			{
				int h2 = h >> 1;
				g.drawArc(x, y, w, h2 - 1, 270, 180);
				g.drawArc(x, y + h2 + 1, 
						  w, h2 - 1, 270, 180);
				g.drawLine(x + w2, y + h2 - 1, x + w, y + h2);
				g.drawLine(x + w2, y + h2 + 1, x + w, y + h2);
			} break;
            case C_ROUND_BRACKET:
            {
				g.drawArc(x, y, w, h, -90, 180);
            } break;
            case O_SQUARE_BRACKET:
            {
				g.drawLine(x, y, x + w, y);
				g.drawLine(x, y, x, y + h);
				g.drawLine(x, y + h, x + w, y + h);
            } break;
            case C_SQUARE_BRACKET:
            {
				g.drawLine(x, y, x + w, y);
				g.drawLine(x + w, y, x + w, y + h);
				g.drawLine(x, y + h, x + w, y + h);
            } break;
            case O_ANGLE_BRACKET:
            {
				int h2 = h >> 1;
				g.drawLine(x + w, y, x, y + h2);
				g.drawLine(x, y + h2, x + w, y + h);
            } break;
            case C_ANGLE_BRACKET:
            {
				int h2 = h >> 1;
				g.drawLine(x, y, x + w, y + h2);
				g.drawLine(x + w, y + h2, x, y + h);
            } break;
            case S_BAR_BRACKET:
            {
				int x2 = x + w2;
				g.drawLine(x2, y, x2, y + h);
            } break;
            case D_BAR_BRACKET:
            {
				int x2 = x + w2;
				g.drawLine(x2 - 1, y, x2 - 1, y + h);
				g.drawLine(x2 + 1, y, x2 + 1, y + h);
            } break;
            case O_CEIL_BRACKET:
            {
				g.drawLine(x + w2, y, x + w + w2, y);
				g.drawLine(x + w2, y, x + w2, y + h);
            } break;
            case C_CEIL_BRACKET:
            {
				g.drawLine(x - w2, y, x + w2, y);
				g.drawLine(x + w2, y, x + w2, y + h);
            } break;
            case O_FLOOR_BRACKET:
            {
				g.drawLine(x + w2, y, x + w2, y + h);
				g.drawLine(x + w2, y + h, x + w + w2, y + h);
            } break;
            case C_FLOOR_BRACKET:
            {
				g.drawLine(x + w2, y, x + w2, y + h);
				g.drawLine(x - w2, y + h, x + w2, y + h);
            } break;
        }
    }
    
        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
        drawBracket(g, sx, sy, W2, height, openBracket);
		int x = sx + W2 + 3;
		int y = sy;
		int nSymbols = symbols.length - 1;
		FontMetrics fm = g.getFontMetrics();
		SEPARATOR_WIDTH = fm.charWidth('w');
		int h2 = height >> 1;
		int yh2 = y + h2;
		int sh = fm.getAscent() >> 1;
		int i = 0;
		for (i = 0;x < ex && i < nSymbols;i++)
		{
			symbols[i].draw(g, x, yh2 - (symbols[i].height >> 1),ex, ey, cursor, true);
			x += symbols[i].width;
			g.drawChars(separators,i,1,x,yh2 + sh);
			x += SEPARATOR_WIDTH;
		}
		
		if (i != nSymbols) return ;
		
		symbols[nSymbols].draw(g, x, yh2 - (symbols[nSymbols].height >> 1), 
							   ex, ey, cursor, true);
        drawBracket(g, sx + width - W2 - 5, sy, W2, height, closeBracket);
    }

        /**
         * Recalculate the size.
         */
    public void recalculateBounds(Graphics g)
    {
		this.width = this.height = 0;
		for (int i = 0;i < symbols.length;i++)
		{
			symbols[i].recalculateBounds(g);
			width += symbols[i].width;
			if (height < symbols[i].height) height = symbols[i].height;
		}
			// give 5 pixels for each symbol...
		width += ((symbols.length - 1) * g.getFontMetrics().charWidth('W'));
            // for the brackets...
        width += W2 + W2;
        height += H2;
    }

		/**
		 * Given the mouse pressed location, returns the MMLNavigateableSymbol
		 * at the that point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p, MMLCursor cursor)
	{
		int x = W2;
		for (int i = 0;i < symbols.length;i++)
		{
			int x2 = x + symbols[i].width + SEPARATOR_WIDTH;
			if (p.x < x2)
			{
				return symbols[i];
			}
			x += symbols[i].width + SEPARATOR_WIDTH;
		}
		return null;
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
		
			// we can either exit from a child or somewhere else.
			// i think it is impossible to "exit" from a non-child...
			// We know which symbol it is based on the currSymIndex
			// and currSymLine fields of the cursor.
		int pos = next.currSymIndex;
		if (kc == KeyEvent.VK_LEFT)
		{
			if (pos == 0) return null;
			else
			{
				next.currSymIndex = pos - 1;
				//next.currPos = symbols[pos - 1].nSymbols[0];
				//next.currLine = 0;
				return symbols[pos - 1];
			}
		} else if (kc == KeyEvent.VK_RIGHT)
		{
			if (pos == symbols.length - 1) return null;
			else
			{
				next.currSymIndex = pos + 1;
				//next.currPos = 0;
				//next.currLine = 0;
				return symbols[pos + 1];
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
        if (kc == KeyEvent.VK_RIGHT)
        {
			curr.currLine = curr.currPos = 
				curr.currSymIndex = curr.currSymLine = 0;
			return (MMLNavigateableSymbol)symbols[0];
        } else if (kc == KeyEvent.VK_LEFT)
		{
			curr.currLine = curr.currSymIndex = 0;
			curr.currPos = curr.currSymIndex = symbols.length - 1;
			return (MMLNavigateableSymbol)symbols[symbols.length - 1];
		}
        return (MMLNavigateableSymbol)symbols[0];
    }
}
