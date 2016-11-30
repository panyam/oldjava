package com.sri.apps.mml;

import java.awt.*;

/**
 * A class for having paragraphs that have some kind
 * of bar (or arrows) above or below them.
 */
public class MMLBarParagraph extends MMLParagraph
{
		/**
		 * Tells if the bar is above or below the paragraph.
		 */
	protected boolean above = true;
	
		/**
		 * Tells if the whole thing should start
		 * in the middle.
		 */
	protected boolean startInMiddle = false;
	
        /**
         * Use No bars.
         */
    public final static byte NO_BAR = 0;

        /**
         * Use opening round bars.
         */
    public final static byte SINGLE_BAR = 1;

        /**
         * Use closgin round bars.
         */
    public final static byte DOUBLE_BAR = 2;

        /**
         * Use opening curly bars.
         */
    public final static byte LEFT_ARROW_BAR = 3;

        /**
         * Use closing curly bars.
         */
    public final static byte RIGHT_ARROW_BAR = 4;

        /**
         * Use opening square bars.
         */
    public final static byte LR_ARROW_BAR = 5;

        /**
         * Opening bar type.
         */
    byte barType = SINGLE_BAR;

        /**
         * The padding width.
         */
    private static short w2 = 20;

        /**
         * The padding height.
         */
    private static short h2 = 10;

        /**
         * Constructor.
         */
    public MMLBarParagraph(byte type, boolean top, boolean mid)
    {
        super();
		this.barType = type;
		this.above = top;
		this.startInMiddle = mid;
    }

        /**
         * Constructor that specifies to use
         * the same type of bars for open and closing.
         */
    public MMLBarParagraph(byte type)
    {
        this(type, true, false);
    }

        /**
         * Default Constructor
         */
    public MMLBarParagraph()
    {
        this(SINGLE_BAR, true, false);
    }

    protected void drawBar(Graphics g, int x, int y)
    {
        switch(barType)
        {
			case NO_BAR:
			break;
			case SINGLE_BAR:
			break;
			case DOUBLE_BAR:
			break;
			case LEFT_ARROW_BAR:
			break;
			case RIGHT_ARROW_BAR:
			break;
			case LR_ARROW_BAR:
			break;
        }
    }
    
        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey,
					 MMLCursor cursor, boolean drawBorder)
    {
		int cy = sy;
        
        width -= w2;
        height -= h2;
        
        super.draw(g, sx, sy + h2, ex, ey, cursor, drawBorder);
        //drawbar(g, openbar, sx + 5, sy);
        width += w2;
        height += h2;
    }

        /**
         * Recalculate the size.
         */
    public void recalculateBounds(Graphics g)
    {
        super.recalculateBounds(g);
            // for the bars...
        width += w2;
        height += h2;
    }
}
