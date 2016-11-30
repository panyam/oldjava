package com.sri.apps.off;

class Edge
{
    Vertex top, bottom;
    int dxpix, dypix;

    double dx;
    double dy;
    double dz;
    double dId;     //  diffuse intensity difference 
    double dIs;     //  specular intensity difference 
    SVector dN;      // normal difference...

    boolean inAET;     // tells if this edge is in the AET or not..

            // x value in pixels...
    int currX;

            // the current y value that corresponds to
            // this scan line on this edge...
    double currY;

    double Cpix;

            // is this edge horizontal
    boolean isHorizontal;

    public Edge()
    {
        dx = 0;
        dy = 0;
        dN = new SVector(0, 0, 0);
    }

        /**
         * Given a scan line y (in pixels), computes the X value.
         */
    int XAtY(double y)
    {
        if (isHorizontal || dxpix == 0)
        {
            return top.xPix;
        } else
        {
            return (int)((y - Cpix) * ((double)dxpix) / ((double)dypix));
        }
    }
}
