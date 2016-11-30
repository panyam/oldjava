package com.sri.apps.cat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;

public class PGMViewer extends Frame implements WindowListener {
    Dimension pref = new Dimension();
    Image buff = null;

    public PGMViewer(String s) {
        super("PGMViewer - by Sri - \"" + s + "\"");
        addWindowListener(this);
    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
    public void windowClosed(WindowEvent e) { dispose(); System.exit(0); }
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    protected int getInt (DataInputStream din) throws IOException {
        int out = 0;
        char ch = ' ';
        byte b = 0;

        while (b < 0x30 || b > 0x39) {
            b = din.readByte();

                    /*then skip the whole line with comments in it*/
            if (b == 0x23) {
                while (b != 10 && b != 13) b = din.readByte();
            }
        }
        ch = (char)b;
        do {
            out = (out * 10) + (ch - '0');
            ch = (char)(b = din.readByte());
        } while (ch >= '0' && ch <= '9');

                /**
                 * and also skip over all the white spacess and tabs 
                 */
        /*while (ch==' '||ch=='\n'||ch=='\t'||ch=='\n') ch = getc(fin);*/
        return out;
    }

    public void paint(Graphics g) {
        if (buff != null) {
            Insets in = getInsets();
            g.drawImage(buff,in.left + 1,in.top + 1,this);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void readImage(String fname) throws IOException {
        DataInputStream din = new DataInputStream(new FileInputStream(fname));

        din.readByte();
        din.readByte();

        int cols = getInt(din);
        int rows = getInt(din);
        int maxVal = getInt(din);

        if (maxVal > 255) maxVal = 255;

        System.out.println("Rows, cols = " + rows + ", " + cols);
        int pixList[] = new int[rows * cols];

        int index = 0;
        int height = rows, width = cols;

        Insets in = getInsets();
        pref.width = cols + in.left + in.right + 10;
        pref.height = rows + in.top + in.bottom + 30;

        int counter = 0;
        for (int row = 0; row < rows;row++) {
            for (int col = 0;col < cols;col ++) {
                byte b = din.readByte();
                pixList[counter++] =  (0xff << 24) | 
                                      (((b & 0xff)) << 16) | 
                                      (((b & 0xff)) << 8) | 
                                      (((b & 0xff)));
            }
        }
        DirectColorModel ourCM = new DirectColorModel(32, 0xff0000,0x00ff00, 
                                                          0x0000ff,0xff000000);
        buff = createImage(new MemoryImageSource(cols,rows,ourCM,pixList,0,cols));
        setSize(pref.width + 10, pref.height + 10);
    }

    public Dimension getPreferredSize() {
        return pref;
    }

    public static void main(String args[]) {
        PGMViewer p = new PGMViewer(args[1]);
        try {
            p.readImage(args[0] == null ? "" : args[0]);
            p.toFront();
            p.setVisible(true);
            p.setSize(50,50);
            p.pack();
        } catch (IOException e) {
            System.err.println("Cant read " + args[0]);
        }
    }
}
