package com.sri.apps.cat;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class ImagePanel extends Panel implements Transferable {
    Image im = null;
    int imageW = 0,imageH = 0;

    public static DataFlavor ImageFlavor;
    public DataFlavor[] flavors = { ImageFlavor };

    static {
        try {
            ImageFlavor = new DataFlavor(Class.forName("java.awt.Image"),"Image");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public ImagePanel() { }

    public int getImageWidth() { return imageW; }
    public int getImageHeight() { return imageH; }

    public Image getImage() {
        return im;
    }

    public ImagePanel(Image im) {
        setImage(im);
    }

    public void setImage(Image i) {
        this.im = i;
        if (i == null) {
            repaint();
            invalidate();
            return  ;
        }
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(im,0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            return ;
        }
        imageW = im.getWidth(this);
        imageH = im.getHeight(this);

        repaint();
        invalidate();
    }

    public void update(Graphics g) { paint(g); }

    public void paint(Graphics g) {
        Dimension d = getSize();
        if (im != null) {
            if (im != null) g.drawImage(im,(d.width - imageW) / 2,
                                           (d.height - imageH)/2,this);
        } else {
            g.fillRect(0,0,d.width,d.height);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(imageW,imageH);
    }

    public synchronized Object getTransferData(DataFlavor flavor) 
                    throws UnsupportedFlavorException, IOException {
        if (flavor.equals(ImageFlavor)) {
            if (im != null) return im;
            else {
                Dimension d = getSize();
                return createImage(d.width,d.height);
            }
        } else throw new UnsupportedFlavorException(flavor);
    }

    public synchronized boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(ImageFlavor);
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
}
