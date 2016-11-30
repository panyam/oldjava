package com.sri.gui.core;

import java.awt.*;
import java.awt.image.*;

public final class Utils {
    public static void waitForImage(Image image) {
        waitForImage(new Panel(),image);
    }

    public static void waitForImage(Component component, 
                                    Image image) {
        if (image == null) return ;
        MediaTracker tracker = new MediaTracker(component);
        try {
            tracker.addImage(image, 0);
            tracker.waitForID(0);
        }
        catch(InterruptedException e) { e.printStackTrace(); }
    }

    public static void drawEtchedArc(Graphics g,
                       int x,int y,
                       int w, int h, 
                       int startAngle, int arcAngle,
                       boolean raised) {
        Color c = g.getColor();

        g.setColor(raised ? c.darker() : c.brighter());
        g.drawArc(x,y,w,h,startAngle,arcAngle);
        g.drawArc(x+1,y,w,h,startAngle,arcAngle);
        g.drawArc(x,y + 1,w,h,startAngle,arcAngle);
        g.setColor(raised ? c.brighter() : c.darker());
        g.drawArc(x + 1,y + 1,w,h,startAngle,arcAngle);
        g.setColor(c);
    }

    public static void drawEtchedOval(Graphics g,
                               int x,int y,
                               int w, int h,
                               boolean raised) {
        Color c = g.getColor();

        g.setColor(raised ? c.darker() : c.brighter());
        g.drawOval(x,y,w,h);
        g.drawOval(x+1,y,w,h);
        g.drawOval(x,y + 1,w,h);
        g.setColor(raised ? c.brighter() : c.darker());
        g.drawOval(x + 1,y + 1,w,h);
        g.setColor(c);
    }

    public static Image createDisabledImage(Image in) {
        return createDisabledImage(in,new Panel());
    }

    public static Image createDisabledImage(Image in,Component c) {
        if (in != null) {
            BlackAndWhiteFilter _bwFilter = new BlackAndWhiteFilter();

            FilteredImageSource fis = 
                new FilteredImageSource(in.getSource(), _bwFilter);

            Image out = c.createImage(fis);
            MediaTracker tracker = new MediaTracker(c);
            try {
                tracker.addImage(out,0);
                tracker.waitForID(0);
            }
            catch (InterruptedException e) { e.printStackTrace(); }
            return out;
        }
        else return null;
    }
}
