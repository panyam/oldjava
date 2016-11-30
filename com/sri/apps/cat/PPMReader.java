package com.sri.apps.cat;
import java.io.*;
import java.awt.image.*;
import java.awt.image.*;
import java.awt.Color;

/**
 * This class represents a PPM image reader class.   This object reads
 * given PPM information from an input stream and stores in its internal
 * structures.  Only 1, 4, 8 and 24 bit bitmaps can be read.
 *
 * @version 	1.0 4/7/99
 * @author 	    Sriram Panyam
 */

public class PPMReader extends ImageReader {
    int pixList[];

    int maxVal;
     
    int magic2;

    ColorModel ourCM;

    /**
     * A normal constructor.
     */
    public PPMReader(int m) {
        magic2 = m;
        System.out.println("File Type: P" + m);
    }

    /**
     * Each time a file or other input stream is passed to this object, the
     * ppm information is read from the input stream.
     */
    public void setInputStream(DataInputStream din,boolean skipHeader)
                                                        throws IOException {
        if (this.din != null) this.din.close();
        this.din = din;

                // the first 2 bytes of any bmp file 
                // MUST be 'P' and '6'
        validFile = true;

        if (!skipHeader) {
            if (din.readByte() == 0x50) {
                byte b = din.readByte();
                char ch = (char)b;
                if (b == '5') magic2 = 5;
                else if (b == '6') magic2 = 6;
            } else validFile = false;
        }

        if (validFile) readInImage();
    }

    /**
     * Simply prints out the header info in a bitmap.
     */
    public void printHeaderValues() {
        System.out.println("# Columns = " + cols);
        System.out.println("# Rows = " + rows);
        System.out.println("Max Val = " + maxVal);
    }

    public int[] getPixList() { return pixList; }

    /**
     * A function that breaks up the task of reading the input stream for
     * bitmap information.  It is basically a wrapper function of all lower
     * level functions.
     */
    protected void readInImage() {
        
        try {
            getImageData();
        }
        catch (IOException e) {
            System.err.println("Error Reading file");
        }

        createColorModel();             // create a color model based
                                        // on the color map that we read in

        validFile = true;

        printHeaderValues();
        try {
            din.close();
        }
        catch (IOException e) {
            System.err.println("Error Closing file");
        }
        //printHeaderValues();
    }

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

    /**
     * Gets the image data for each pixel in the bmp.
     */
    private void getImageData() throws IOException {

        cols = getInt(din);
        rows = getInt(din);
        maxVal = getInt(din);

        if (maxVal > 255) maxVal = 255;

        System.out.println("Rows, cols = " + rows + ", " + cols);
        pixList = new int[10 + rows * cols];

        int index = 0;
        int height = rows, width = cols;

        int counter = 0;
        for (int row = 0; row < rows;row++) {
            for (int col = 0;col < cols;col ++) {
                byte r,b,g;
                if (magic2 == 5) {
                    r = g = b = din.readByte();
                } else {
                    r = din.readByte();
                    g = din.readByte();
                    b = din.readByte();
                }
                pixList[counter++] =  (0xff << 24) | 
                                      (((r & 0xff)) << 16) | 
                                      (((g & 0xff)) << 8) | 
                                      (((b & 0xff)));
            }
        }
    }

    /**
     * Creates a color model based on the number of bits per pixel and
     * the number of colors in the image.
     *
     * If the bits per pixel is 24 a direct RGB model is used.
     * other wise if image is 1 4 or 8 bit image an index color model is
     * used to between the values.
     */
    protected void createColorModel() {
        ourCM = new DirectColorModel(32, 0xff0000,0x00ff00, 
                                         0x0000ff,0xff000000);
    }


    /**
     * Creates an image in memory based on the information read from the
     * input stream.  This function is called from Image producer objects
     * which need to create an image object based on what was read.
     */
    public MemoryImageSource makeImageInMemory() {
        return (new MemoryImageSource(cols,rows,ourCM,pixList,0,cols));
    }

    public static void toOutputStream(DataOutputStream dout,int list[],
                                int rows, int cols,int maxval,
                                String comment,int magic) throws IOException {
        if (comment == null) comment = "";
        char chars[] = comment.toCharArray();
        dout.writeByte('P');
        dout.writeByte('0' + magic);
        dout.writeByte('\n');
        dout.writeByte('#');
        for (int i  =0;i < comment.length();i++) {
            dout.writeByte(chars[i]);
            if (chars[i] == '\n') dout.writeByte('#');
        }
        dout.writeByte('\n');
        String out = cols + " ";
        for (int i = 0;i < out.length();i++) dout.writeByte(out.charAt(i));
        out = rows + " ";
        for (int i = 0;i < out.length();i++) dout.writeByte(out.charAt(i));
        out = maxval + " ";
        for (int i = 0;i < out.length();i++) dout.writeByte(out.charAt(i));
        dout.writeByte('\n');
        int c = 0;
        for (int i = 0;i < rows;i++) {
            for (int j = 0;j < cols;j++) {
                dout.writeByte(list[c++] & 0xff);
            }
        }
    }
}
