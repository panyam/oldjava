package com.sri.gui.core.containers;

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

        /**
         * The insets we are using.
         */
    private Insets ourInsets = new Insets(20, 10, 10, 10);

        // Is it etched in or etched out.. Default is in..
    private boolean etchedIn = true;

        /**
         * Constructor.
         */
    public EtchedPanel(String title) {
        this(title,true);
    }

        /**
         * Constructor.
         */
    public EtchedPanel(String title,boolean etchedIn) {
        super();
        setTitle(title);
        setEtched(etchedIn);
    }

        /**
         * Constructor.
         */
    public EtchedPanel(String title, LayoutManager lmgr) {
        this(title,true, lmgr);
    }

        /**
         * Constructor.
         */
    public EtchedPanel(String title,boolean etchedIn, LayoutManager lmgr) {
        super(lmgr);
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
        return ourInsets;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int w = d.width - 2;
        int h = d.height - 3;
        int x = 2;
        FontMetrics f = g.getFontMetrics();
        ourInsets.top = f.getMaxAscent() + 5;
        int asc = (int)(f.getAscent()/2);
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
