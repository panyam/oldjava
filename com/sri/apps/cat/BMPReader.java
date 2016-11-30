package com.sri.apps.cat;

import java.io.*;
import java.awt.image.*;
import java.awt.image.*;
import java.awt.Color;

/**
 * This class represents a Bitmap image reader class.   This object reads
 * given bitmap information from an input stream and stores in its internal
 * structures.  Only 1, 4, 8 and 24 bit bitmaps can be read.
 *
 * @version 	1.0 4/7/99
 * @author 	    Sriram Panyam
 */
public class BMPReader extends ImageReader
{

    protected static final boolean LITTLE_ENDIAN = true;

            // Now here are some Bitmap variables...
    int filesize;          // size of the file

    short res1;            
    short res2;

    int pixeloffset;

    int bmisize;

    short planes;

    short bitsperpixel;

    int compression;

    int cmpsize;           // size of compressed image..

    int xscale;

    int yscale;

    int colors;            // number of colors used

    int impcolors;         // number of important colors used...

                            // a color map is ONLY used if we are 
                            // 1 4 or 8 bit mode.  for 24 bit we have 
                            // 16.7 million colors and no color map is
                            // needed for the true color image...
    byte mapRed[], mapGreen[], mapBlue[];

    byte red[][], green[][], blue[][];

    int pixList[];

    ColorModel ourCM;


    /**
     * A normal constructor.
     */
    public BMPReader() { }

    /**
     * Each time a file or other input stream is passed to this object, the
     * bitmap information is read from the input stream.
     */
    public void setInputStream(DataInputStream din,boolean skipHeader)
                                                        throws IOException {
        if (this.din != null) this.din.close();
        this.din = din;

        validFile = true;

        if (!skipHeader) {
                    // the first 2 bytes of any bmp file 
                    // MUST be 'B' and 'M'
            validFile = ((din.readByte() == 66) && (din.readByte() == 77));
        }

        if (validFile) readInImage();
    }

    public int[] getPixList() { return pixList; }

            // reads all the fields at the start like rows, cols and etc..
            //
            // remember that these fields HAVE to be read in this order...
    private void readHeaderFields() throws IOException {
        filesize = readInt(din,LITTLE_ENDIAN);
        res1 = readShort(din,LITTLE_ENDIAN);
        res2 = readShort(din,LITTLE_ENDIAN);
        pixeloffset = readInt(din,LITTLE_ENDIAN);
        bmisize = readInt(din,LITTLE_ENDIAN);
        cols = readInt(din,LITTLE_ENDIAN);
        rows = readInt(din,LITTLE_ENDIAN);
        planes = readShort(din,LITTLE_ENDIAN);
        bitsperpixel = readShort(din,LITTLE_ENDIAN);
        compression = readInt(din,LITTLE_ENDIAN);
        cmpsize = readInt(din,LITTLE_ENDIAN);
                // Some bitmaps do not have the sizeimage field calculated
                // Ferret out these cases and fix 'em.
        if (cmpsize == 0) {
            cmpsize = ((((cols*bitsperpixel)+31) & ~31 ) >> 3);
            cmpsize *= cols;
        }
        xscale = readInt(din,LITTLE_ENDIAN);
        yscale = readInt(din,LITTLE_ENDIAN);
        colors = readInt(din,LITTLE_ENDIAN);
        impcolors = readInt(din,LITTLE_ENDIAN);
    }

    /**
     * Simply prints out the header info in a bitmap.
     */
    public void printHeaderValues() {
        System.out.println("File size = " + filesize);
        System.out.println("Res1 = " + res1);
        System.out.println("Res2 = " + res2);
        System.out.println("Pixel Offset = " + pixeloffset);
        System.out.println("BM Header Size = " + bmisize);
        System.out.println("# Columns = " + cols);
        System.out.println("# Rows = " + rows);
        System.out.println("Planes = " + planes);
        System.out.println("Bits per pixel = " + bitsperpixel);
        System.out.println("Compression = " + compression);
        System.out.println("Compressed Image Size = " + cmpsize);
        System.out.println("X pixels/meter = " + xscale);
        System.out.println("Y pixels/meter = " + yscale);
        System.out.println("# of Colors = " + colors);
        System.out.println("# of important colors = " + impcolors);
    }

    /**
     * A function that breaks up the task of reading the input stream for
     * bitmap information.  It is basically a wrapper function of all lower
     * level functions.
     */
    protected void readInImage() {
        try {
            readHeaderFields();
        }
        catch (IOException e) {
            System.err.println("Error in Header");
            validFile = false;
        }

                // the value of the colors field is usually left 0.  In
                // this case we assume the image uses the maximum number of
                // colors that can be represented in the number of bit
                // used.  This is simply (2 ^ bitsperpixel).
        if (colors == 0) colors = 1 << bitsperpixel;


        bytesRead = 54;         // current position in file..
        try {
            getColorMap();      // read the color map if one exists...
        }
        catch (IOException e) { 
            validFile = false;
            return ; 
        }

        if (bytesRead > pixeloffset) {
            System.err.println("Corrupt BMP File");
            validFile = false;
        }

        try {
            while (bytesRead < pixeloffset) { din.readByte(); bytesRead ++; }
        }
        catch (IOException e) { 
            validFile = false;
            return ; 
        }

        try {
            getImageData();             // get the image data..
        }
        catch (IOException e) { 
            validFile = false;
            return ; 
        }

        createColorModel();             // create a color model based
                                        // on the color map that we read in

        validFile = true;
        try {
            din.close();
        }
        catch (IOException e) {
            System.err.println("Error Closing file");
        }
        //printHeaderValues();
    }

    /**
     * Gets the color map of a bmp image.
     *
     * A color map exists only for 1 4 or 8 bit images.  For 24 bit images
     * values are stored as true colors in 3 bytes.
     */
    private void getColorMap() throws IOException {
                // now a color map follows the head in a 1 4 or 8 bit/pixel
                // bitmap
        switch (bitsperpixel) {
            case 24 : break;
            case 1 : case 4 : case 8 : {
                mapRed = new byte[colors];
                mapGreen = new byte[colors];
                mapBlue = new byte[colors];
                for (int i = 0;i < colors;i++) {

                    mapBlue[i] = din.readByte();
                    mapGreen[i] = din.readByte();
                    mapRed[i] = din.readByte();

                    din.readByte();     // spare byte for some reason..
                    bytesRead += 4;
                }
            }break;
            default : {
                validFile = false;
                System.err.println("Invalid Bits/pixel");
            }break;
        }
    }


    /**
     * Gets the image data for each pixel in the bmp.
     */
    private void getImageData() throws IOException {
        red = new byte[rows][cols];
        green = new byte[rows][cols];
        blue = new byte[rows][cols];
        pixList = new int[rows * cols];

        int index = 0;
        int height = rows, width = cols;

        if (bitsperpixel == 24) {
            int npad = (cmpsize / height) - (width * 3);
            byte brgb[] = new byte[(width + npad) * 3 * height];

            din.read(brgb,0,(width + npad) * 3 * height);

            for (int i = 0;i < height;i++) {
                for (int j = 0;j < width;j++) {
                    red[(height - i - 1)][j] = brgb[index + 2];
                    green[(height - i - 1)][j] = brgb[index + 1];
                    blue[(height - i - 1)][j] = brgb[index];
                    pixList[width * (height - i - 1) + j] = 
                                    (255&0xff)<<24
                                    | (((int)brgb[index+2]&0xff)<<16)
                                    | (((int)brgb[index+1]&0xff)<<8)
                                    | (int)brgb[index]&0xff;
                    index += 3;
                }
                index += npad;
            }
        }
        else {      // read a 1 4 or 8 bit image...
                    // at this stage we have already read our
                    // color map aka palette..
                    // scan lines are still paded out to even 4 byte
                    // boundaries
            for (int i = 0;i < height;i++) {
                int bit_store = 0;
                int bit_count = 0;
                int mask = (1 << bitsperpixel) - 1;
                for (int j = 0;j < rows;j++) {
                    int pix = 0;

                    if (bit_count <= 0) {
                        bit_count = 8;
                        bit_store = din.readByte();
                        bytesRead++;
                    }

                    bit_count -= bitsperpixel;
                    pix = (bit_store >> bit_count) & mask;
                    red[(height - i - 1)][j] = mapRed[pix];
                    green[(height - i - 1)][j] = mapGreen[pix];
                    blue[(height - i - 1)][j] = mapBlue[pix];

                    pixList[width * (height - i - 1) + j] = pix;
                }
                while (((bytesRead - pixeloffset) & 3) != 0) {
                    din.readByte();
                    bytesRead ++;
                }
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
        if (bitsperpixel == 24) {
            ourCM = new DirectColorModel(32, 0xff0000,0x00ff00, 
                                             0x0000ff,0xff000000);
        }
        else if (bitsperpixel == 1 || bitsperpixel == 4 || bitsperpixel == 8) {
            ourCM = new IndexColorModel(bitsperpixel,mapRed.length,
                                        mapRed,mapGreen,mapBlue);
        }
        else {
            ourCM = null;
            System.err.println("Invalid bits per pixel");
        }
    }


    /**
     * Creates an image in memory based on the information read from the
     * input stream.  This function is called from Image producer objects
     * which need to create an image object based on what was read.
     */
    public MemoryImageSource makeImageInMemory() {
        int width = cols;
        int height = rows;
        int pixels[] = new int[cols * rows];
        int counter = 0;
        for (int i = 0;i < height;i++) {
            for (int j = 0;j < width;j++) {
                if (validFile) {
                    pixels[counter++] =   (0xff << 24) | 
                                          (((red[i][j] & 0xff)) << 16) | 
                                          (((green[i][j] & 0xff)) << 8) | 
                                          (((blue[i][j] & 0xff)));
                }
                else pixels[counter++] = 0xff000000;
            }
        }
        return (new MemoryImageSource(cols,rows,ourCM,pixList,0,cols));
    }

    void writeValue(DataOutputStream dout,int val) throws IOException {
        String out = String.valueOf(val);
        for (int i = 0;i < out.length();i++) {
            dout.writeByte((byte)out.charAt(i));
        }
    }
    public void writeGrayScale(DataOutputStream dout) {
        try {
        dout.writeByte('P');
        dout.writeByte('6');
        writeValue(dout,cols);
        dout.writeByte((byte)' ');
        writeValue(dout,rows);
        dout.writeByte((byte)' ');
        writeValue(dout,255);
        dout.writeByte((byte)' ');
        for (int i = 0;i < rows;i++) {
            for (int j = 0;j < cols;j++) {
                byte y = (byte)(0.299 * red[i][j] + 0.587 * green[i][j] + 0.114 * blue[i][j]);
                dout.writeByte(y);
                dout.writeByte(y);
                dout.writeByte(y);
            }
        }
        } catch (IOException e) { }
    }

    public void convertToJPEG(DataOutputStream dout) {
        //MyJPEGWriter mine = new MyJPEGWriter(rows,cols,pixList,dout);
        //try { mine.encode(); }
        //catch (IOException e) { }
    }
}
