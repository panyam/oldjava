package com.sri.apps.cat;

import java.io.*;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.MemoryImageSource;

/**
 * This class represents the super class of all Image readers.  To create a
 * new type of image reader, just subclass this image and over ride the
 * two functions :
 *          public void setInputStream(InputStream in) throws IOException ;
 *          public MemoryImageSource makeImageInMemory();
 *
 * @version 	1.0 4/7/99
 * @author 	    Sriram Panyam
 */

public abstract class ImageReader extends Object {
    protected DataInputStream din = null;
    protected boolean validFile = false;
    int bytesRead = 0;
    public int rows,cols;

    public abstract void setInputStream(DataInputStream in,boolean s)
                                                    throws IOException ;
    public abstract int[] getPixList() ;
    //abstract void printHeaderValues() ;
    abstract public MemoryImageSource makeImageInMemory();

    protected int readInt(DataInputStream din,boolean little) 
                                            throws IOException {
        int out = 0 & 0x00;
        byte ch;
        if (little) {
            ch = din.readByte();
            out = ch & 0xff;
            ch = din.readByte();
            out |= (ch & 0xff) << 8;
            ch = din.readByte();
            out |= (ch & 0xff) << 16;
            ch = din.readByte();
            out |= (ch & 0xff) << 24;
            return out;
        }
        else return din.readInt();
    }

    protected short readShort(DataInputStream din,boolean little) 
                                                    throws IOException {
        short out = 0 & 0x00;
        byte ch;
        if (little) {
            ch = din.readByte();
            out = (short)(ch & 0xff);
            ch = din.readByte();
            out |= (ch & 0xff) << 8;
            return out;
        }
        else return din.readShort();
    }
}
