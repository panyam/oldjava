package com.sri.gui.core;

/*
 * Author...........: Sriram Panyam
 * File.............: SSeparator.java
 * Date.............: 16 July 99
 * Email............: spany@students.cs.mu.oz.au
 * Version..........: 1.0
 * Purpose..........: Acts as a separator between items.
 */

import java.awt.*;

/**
 * This class is used as a separater between objects in a layout.  A
 * separator can be either horizontal or vertical.
 */
public class SSeparator extends Canvas {
    public  static  final       int                 HORIZONTAL = 1;
    public  static  final       int                 VERTICAL = 2;

    public int mode = HORIZONTAL;
    private static final int thickness = 8;

    public SSeparator(int mode) {
		//setBackground(new Color(173,156,148));
        if (mode == HORIZONTAL || mode == VERTICAL) {
            this.mode = mode;
            //this.length = length;
            return ;
        }
        else 
           throw new IllegalArgumentException(
                        "Mode can only be HORIZONTAL or VERTICAL");
    }

    public Dimension getPreferredSize() {
		Dimension d = getSize();
        if (mode == VERTICAL) return new Dimension(thickness,2);
        else return new Dimension(2,thickness);
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int gap = 1;

        g.setColor(getBackground());
		g.fillRect(0,0,d.width,d.height);
		g.setColor(Color.lightGray);
        if (mode == VERTICAL) 
                g.draw3DRect(d.width/2,gap,1,d.height - gap - gap,false);
        else g.draw3DRect(gap,d.height/2,d.width - gap - gap,1,false);
    }
}