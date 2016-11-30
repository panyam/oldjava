package com.sri.apps.cat;

import java.io.*;

public class UnknownProjection extends Projection {
    protected double ATTEN = 1.0;
    protected final static int NUM_IMAGE_STEPS = 1024;
    ImageReader im = null;
    int width, height;
    int image[];

    public UnknownProjection (double atten,DataInputStream din) {
        this.ATTEN = atten;

        try {
            char x = (char)din.readByte();

            if (x == 'B') {
                        // then if next one is M then bitmap
                x = (char)din.readByte();
                if (x == 'M') {
                    im = new BMPReader();
                    im.setInputStream(din,true);
                }
            } else if (x == 'P') {
                x = (char)din.readByte();
                if (x == '5' || x == '6') {
                    im = new PPMReader(x - '0');
                    im.setInputStream(din,true);
                }
            }
        } catch (IOException e) {
            System.err.println("Invalid input stream");
        }

        if (im == null) {
            System.err.println("Unknown file type");
        } else {
            image = im.getPixList();
            width = im.cols;
            height = im.rows;
        }
    }

    public double projection(double phi, double x) {
        double sumAtten = 0;
        double xrot,yrot;
        double cphi = Math.cos(phi), sphi = Math.sin(phi);
        double xcphi = x * cphi, xsphi = x * sphi;
        int numsteps = NUM_IMAGE_STEPS;
        double stepsize = 3.2 / numsteps;
        double y=-(numsteps >> 1) * stepsize;

        for (int i=0; i<numsteps; i++) { 
            xrot = xcphi - y * sphi;            
            yrot = xsphi + y * cphi;
            double pv = pixval(xrot,yrot);
            //System.out.println("xr, yr, pv = " + xrot + ", " + yrot + ", " + pv);
            sumAtten += pv;//pixval(xrot,yrot);
            y += stepsize; 
        }
        return Math.exp(-ATTEN * sumAtten / numsteps);
    }

    protected double pixval(double x, double y) {
        int llx,lly; /* indices for bottom left corner */
        double t,u,ival = 0;
    
        if ( (x>1.0) || (x< -1.0) || (y>1.0) || (y< -1.0) ) return 0;
        llx = ((int)(0.5*(width - 1)*(x+1.0)));
        lly = ((int)(0.5*(height - 1)*(y+1.0)));

        //System.out.println("llx,lly = " + llx + ", " + lly);
        if (llx < 0 || llx >= width || lly < 0 || lly >= height) {
            return 0;
        }

        t = 0.5*width*(x+1.0)-llx; u=0.5*height*(y+1.0)-lly;
        int a = 0,b = 0,c = 0,d = 0;
        try {
            a = image[(lly * width) + llx] & 0xff;
        } catch (Exception e) {
            System.out.println("A is faulty");
        }
        try {
            b = image[(lly * width) + llx + 1 + 2] & 0xff;
        } catch (Exception e) {
            System.out.println("B is faulty");
        }
        try {
            c = image[((lly + 1)* width) + llx + 1 + 2] & 0xff;
        } catch (Exception e) {
            System.out.println("C is faulty");
        }
        try {
            d = image[((lly + 1) * width) + llx + 2] & 0xff;
        } catch (Exception e) {
            System.out.println("D is faulty");
        }

        return ((1.0-t)*(1.0-u)* a) + (t*(1.0-u)* b) + (t*u*c) + ((1.0-t)*u*d);
    }
}
