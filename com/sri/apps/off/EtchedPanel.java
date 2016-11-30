package com.sri.apps.off;

import java.awt.*;

/**
 * Creates a Panel with a etched border around it.
 *
 * The border can be either etched in or out and can be specified during
 * runtime.
 *
 * @author Sri Panyam
 * @version 1.0 16 July 1999
 */
public class EtchedPanel extends Panel {

        /**
         * The caption of the panel.
         */
    private String title;

    private int top = 18,right = 6,bottom = 4,left = 4;

        // Is it etched in or etched out.. Default is in..
    private boolean etchedIn = true;

    public EtchedPanel(String title) {
        this(title,true);
    }

    public EtchedPanel(String title,boolean etchedIn) {
        super();
        setTitle(title);
        setEtched(etchedIn);
    }

        /**
         * Returns the title of this panel.
         */
    public String getTitle() {
        return title;
    }

            /**
             * Sets the title of this panel.
             */
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

            /**
             * Sets the etch type of this panel.
             */
    public void setEtched(boolean etchedIn) {
        this.etchedIn = etchedIn;
        repaint();
    }

            /**
             * Returns the insets of the panel.
             */
    public Insets getInsets() {
        return new Insets(top,left,bottom,right);
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int w = d.width - 2;
        int h = d.height - 3;
        int x = 2;
        FontMetrics f = g.getFontMetrics();
        int asc = (int)(f.getAscent()/2);
        top = f.getAscent() + 5;
        int wid = f.stringWidth(title) + 10;
        Color store = g.getColor();

        Color dg = new Color(128,128,128);
        g.setColor(etchedIn ? Color.white : store.darker());
        g.drawLine(x,asc + 1,x+3,asc + 1);
        g.drawLine(x+1,asc + 1,x+1,h);
        g.drawLine(wid,asc + 1,w - 2,asc + 1);
        g.drawLine(w,asc,w,h);
        g.drawLine(w,h,3,h);
        g.setColor(etchedIn ? store.darker() : Color.white);
        //g.setColor(dg);
        g.drawLine(x,asc,x+4,asc);
        g.drawLine(x,asc,x,h-1);
        g.drawLine(x,h - 1,w-1,h-1);
        g.drawLine(w - 1,h - 1,w - 1,asc);
        g.drawLine(wid,asc,w - 1,asc);
        g.setColor(store);
        g.drawString(title,8,asc + asc);
        super.paint(g);
    }
}
