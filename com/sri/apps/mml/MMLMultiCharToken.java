package com.sri.apps.mml;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

    /**
     * A token with more than one character.
     */
public class MMLMultiCharToken extends MMLNavigateableSymbol
{
        /**
         * Value of the token.
         */
    protected StringBuffer sbuffer = null;

        /**
         * Constructor.
         */
    public MMLMultiCharToken(String t)
    {
        sbuffer = new StringBuffer(t);
    }

        /**
         * Draw this token.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
        FontMetrics fm = g.getFontMetrics();
        String str = sbuffer.toString();
        int sh = fm.getAscent();
        g.drawString(str, sx, sy + sh);

                // if this is the cursor holder 
                // then draw the cursor as well...
        if (cursor.holder == this)
        {
            String bef = str.substring(0, cursor.currPos);
            int sw = fm.stringWidth(bef);
            g.drawLine(sx + sw, sy - 2, sx + sw, sy + sh + 2);
				// set the current position of the cursor...
			cursor.globalX = sx + sw;
			cursor.globalY = sy - 2;
        }
    }

        /**
         * Recalculate the preferred size.
         */
    public void recalculateBounds(Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();
        height = (short)(fm.getAscent() + 2);
        width = (short)(fm.stringWidth(sbuffer.toString()) + 2);
    }

        /**
         * Process a key and return an appropriate cursor.
         */
    public MMLNavigateableSymbol processKey(KeyEvent e,
											MMLCursor curr)
    {
        int l = curr.currLine;
        int c = curr.currPos;
        char ch = e.getKeyChar();
        int kc = e.getKeyCode();
        System.out.println("KC = " + kc);
        int len = sbuffer.length();
        switch (kc)
        {
            case KeyEvent.VK_LEFT:
            {
                if (c <= 0) return null;
                else
                {
                    curr.currPos--;
                    return this;
                }
            }
            case KeyEvent.VK_RIGHT:
            { 
                if (c >= len) return null;
                else
                {
                    curr.currPos++;
                    return this;
                }
            }
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            {
                return null;
            }
            case KeyEvent.VK_HOME:
            {
                curr.currPos = 0;
                return this;
            }
            case KeyEvent.VK_END:
            {
                curr.currPos = len;
                return this;
            }
            default :
            {
                    // if it is a "typeable" key then enter the key
                    // appropriately and set the sizechanged flag to true.
                    // for the time being we only worry about letters
                    // and characters... just for testing...
                    // later we put in all the other ones...
                if ((ch >= 'a' && ch <= 'z') ||
                    (ch >= 'A' && ch <= 'Z') ||
                    (ch >= '0' && ch <= '9'))
                {
                    sbuffer.insert(c, ch);
                    curr.currPos++;
                    curr.sizeChanged = true;
                } else
				{
						// we are doing this so that the parent can know that
						// this token needs to be broken or something
					return null;
				}
            } break;
        }
        return this;
    }

        /**
         * Enter into the next container.
         */
    public MMLNavigateableSymbol enter(KeyEvent e, MMLCursor prev, MMLCursor next)
    {
        int len = sbuffer.length();
        int kc = e.getKeyCode();
        char ch = e.getKeyChar();
        switch (kc)
        {
            case KeyEvent.VK_LEFT:
            { 
                next.currPos = len;
            } break;
            case KeyEvent.VK_UP: case KeyEvent.VK_DOWN:
            case KeyEvent.VK_RIGHT:
            { 
                next.currPos = 0;
            } break;
            default:
            {
                    // if it is a "typeable" key then enter the key
                    // appropriately and set the sizechanged flag to true.
                    // for the time being we only worry about letters
                    // and characters... just for testing...
                    // later we put in all the other ones...
                if ((ch >= 'a' && ch <= 'z') ||
                    (ch >= 'A' && ch <= 'Z') ||
                    (ch >= '0' && ch <= '9'))
                {
                        // if we are here it means we are currently
                        // empty...  so insert a new MultiCharToken symbol
                        // at the current spot and send it the event...
                    next.currPos = 1;
                    sbuffer.insert(0, ch);
                    next.sizeChanged = true;
                }
            } break;
        }
        return this;
    }

        /**
         * Enter into the next container.
         */
    public MMLNavigateableSymbol exit(KeyEvent e, MMLCursor prev, MMLCursor next)
    {
        return this;
    }

		/**
		 * Given a point where the mouse was pressed, returns the symbol
		 * that is at the point.
		 * The cursor information MUST be set by the symbol.
		 * null is returned if no navigateable symbol is found
		 * at the point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p,MMLCursor cursor)
	{
		cursor.currLine = cursor.currPos = 0;
		return null;
	}

        /**
         * Break this token at the give nposition and
         * reutrn a new token.
         */
    public MMLMultiCharToken breakAt(int pos)
    {
        String v = sbuffer.toString();
        sbuffer = new StringBuffer(v.substring(0, pos));
        return new MMLMultiCharToken(v.substring(pos));
    }
}
