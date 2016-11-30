package com.sri.gui.ext.drawing.selectors;

import java.awt.*;
import com.sri.gui.ext.drawing.*;
import java.awt.event.*;

        /**
         * Enables users to select line styles.
         */
public class LineStyleSelector extends SelectorPanel
{
    public final static int PLAIN_LINE = 0;
    public final static int ROUND_DOTTED_LINE = 1;
    public final static int SQUARE_DOTTED_LINE = 2;
    public final static int DASH_DOTTED_LINE = 3;
    public final static int DASHED_LINE = 4;
    public final static int LONG_DASHED_LINE = 5;
    
    public final static String styleNames[] =
    {
        "Plain Line", "Round dotted line", "Square dotted line",
        "Dash Dotted Line", "Dashed Line", "Long Dashed"
    };

        /**
         * Constructor.
         */
    public LineStyleSelector(InfoListener infoListener)
    {
        super(infoListener);
        numStyles = 6;
    }
        
    public void prepareBuffer(Dimension d)
    {
        super.prepareBuffer(d);
            
            // now basically draw all the ones and
            // the one that is currently 
        int x = 2, y = 2;
        int w = d.width - 4;
        int h = (d.height - 4) / numStyles;
        for (int i = 0;i < numStyles;i++)
        {
            drawLineStyle(i,x,y,w,h,bg);
            y += h;
        }
    }
        
    private void drawLineStyle(int which, int x,int y,int w,int h,Graphics g)
    {
        if (which < 0) return ;
        Color store = g.getColor();
        g.setColor(Color.black);
        if (which == PLAIN_LINE)
        {
            g.drawLine(x + 2, y + h / 2,x + w - 4, y + h / 2);
        } else if (which == ROUND_DOTTED_LINE)
        {
            int sx =  x + 2, ex = x + w - 4;
            int sy = y + (h / 2) - 1;
            while (sx < ex)
            {
                g.drawOval(sx,sy,2,2);
                sx += 5;
            }
        } else if (which == SQUARE_DOTTED_LINE)
        {
            int sx =  x + 2, ex = x + w - 4;
            int sy = y + (h / 2) - 1;
            while (sx < ex)
            {
                g.fillRect(sx,sy,2,2);
                sx += 5;
            }
        } else if (which == DASH_DOTTED_LINE)
        {
            int sx =  x + 2, ex = x + w - 4;
            int    sy = y + (h / 2) - 1;
            boolean dash = true;
            while (sx < ex)
            {
                if (dash)
                {
                    g.drawLine(sx,sy,sx + 5,sy);
                    sx += 10;
                } else
                {
                    g.drawOval(sx,sy - 1,2,2);
                    sx += 6;
                }
                dash = !dash;
            }
        } else if (which == LONG_DASHED_LINE)
        {
            int sx =  x + 2, ex = x + w - 4;
            int sy = y + (h / 2) - 1;
            while (sx < ex)
            {
                g.fillRect(sx,sy - 1,10,2);
                sx += 15;
            }
        } else if (which == DASHED_LINE)
        {
            int sx = x + 2, ex = x + w - 4;
            int sy = y + (h / 2);
            while (sx < ex)
            {
                g.drawLine(sx,sy,sx + 5,sy);
                sx += 10;
            }
        }
        g.setColor(store);
        if (which == currMovingItem) g.draw3DRect(x,y,w,h,true);
        if (which == currPressedItem) g.draw3DRect(x,y,w,h,false);
        if (which == currItem) g.draw3DRect(x,y,w,h,false);
    }

        /**
         * Returns the item under the current mouse location.
         */
    public int pointToIndex(int x,int y)
    {
        Dimension d = getSize();
        if (y >= 0 && x >= 0 && x <= d.width && y  <= d.height)
        {
            return ((y * numStyles) / d.height);
        }
        return -1;
    }
    
        /**
         * Notifies all the item listeners that item i has been
         * chosen.
         */
    public void generateEvent (int which)
    {
        processItemEvent(new ItemEvent(this,
                                       ItemEvent.ITEM_STATE_CHANGED,
                                       this,
                                       ItemEvent.ITEM_STATE_CHANGED));
    }
    
    public int getLineStyle()
    {
        return currItem;
    }
	
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e); 
		infoListener.setInfo("" + currMovingItem);
	}
	
	public void mouseDragged(MouseEvent e)
	{
		super.mouseDragged(e); 
		infoListener.setInfo("" + currPressedItem);
	}
	
	public void mouseExited(MouseEvent e)
	{
		super.mouseExited(e);
		infoListener.setInfo("");
	}
}
