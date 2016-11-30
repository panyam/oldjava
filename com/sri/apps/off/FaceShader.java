package com.sri.apps.off;

import java.util.*;

public abstract class FaceShader
{
        /**
         * The value of the specular colour, used in highlighting.
         */
    public final static int SPEC_COLOR = 255;

        /**
         * Value of n specular reflection exponent.
         */
    public int n;

        /**
         * Ambient reflection coefficient.
         */
    public double ka;

        /**
         * Ambient light intensity.
         */
    public double ia;

        /**
         * Diffuse reflection coefficient.
         */
    public double kd;

        /**
         * Specular reflection coefficient.
         */
    public double ks;

        /**
         * The following are variables, that are initialised
         * once for each face, being shaded, so that repetitive
         * function calls are not made to extract these features
         * from the face and other parameters.
         */

        /**
         * The number of edges in the face being painted.
         */
    protected int nEdges;

            // image width and height
    protected int iw, ih;

        // maximum and minimum pixel coordintaes...
    protected int maxy, miny;
    protected int maxx, minx;

            // number of light sources...
    protected int nLights;

            // face color components
    protected int odr, odg, odb;

            // face normal..
    protected SVector normal;

            // image and z buffers
    protected short rf[][];
    protected short gf[][];
    protected short bf[][];
    protected double [][]zf;

        // ambient light values...
    protected double ambR, ambG, ambB;

        // interpolated z values..
    protected double za, zb, zp;

        // zb - za and xb - xa
    protected double zb_za, xb_xa;

        // x interections of two consecutive
        // edges.
    protected int sx, ex;
    Vertex vertices[];

    public FaceShader()
    {

    }

    public abstract void fillFace (Face f, SImage image, Vector lights,
                                   int aet[], Edge globalET[], double foc);
                                   
        /**
         * Function to initialise variables used by the
         * shaders...
         */
    boolean commonInit(Face f, SImage image, Vector lights,
                       int aet[], Edge globalET[])
    {
        nEdges = f.getVertexCount();

        iw = image.getCols();
        ih = image.getRows();
    
        nLights = lights.size();

        for (int i = 0;i < nEdges;i++)
        {
            Vertex v = vertices[f.getVertex(i)];

            if (i == 0)
            {
                miny = maxy = v.yPix;
                minx = maxx = v.xPix;
            } else
            {
                if (v.yPix < miny) miny = v.yPix;
                if (v.xPix < minx) minx = v.xPix;

                if (v.yPix > maxy) maxy = v.yPix;
                if (v.xPix > maxx) maxx = v.xPix;
            }
        }

                // Check for trivial rejection on the top and bottom
        if ((miny < 0 && maxy < 0) || (miny >= ih && maxy >= ih))
        {
            return false;
        }

                // Check for trivial rejection on the left and right
        if ((minx < 0 && maxx < 0) || (minx >= iw && maxx >= iw))
        {
            return false;
        }

        if (miny < 0) miny = 0;
        if (maxy >= ih) maxy = ih - 1;


                // generate the edge table 
                // for the current face....
        f.generateEdgeTable(vertices, globalET);

            // store face's color values
        odr = f.colorRed & 0xff;
        odg = f.colorGreen & 0xff;
        odb = f.colorBlue & 0xff;

            // compute the ambient values...
        double iaka = ia * ka;

        if (iaka < 0) iaka = 0;
        else if (iaka > 1) iaka = 1;

                // calculate ambient values...
        ambR = iaka * odr;
        ambG = iaka * odg;
        ambB = iaka * odb;

        normal = f.getNormal();
    
                // get the image's buffers
        rf = image.getRedFrame();
        gf = image.getGreenFrame();
        bf = image.getBlueFrame();
        zf = image.getZBuffer();

        return true;
    }

        /**
         *  Updates the AET given the current scan line and the
         *  global edge table...
         */
    int updateAET(int aet[], Edge globalET[], int sl, boolean first)
    {
        int aetLen = 0;
        for (int i = 0;i < nEdges;i++)
        {
            if (globalET[i].dypix != 0 &&
                ((!first && globalET[i].top.yPix > sl) ||
                (first && globalET[i].top.yPix >= sl)))
            {
                if (globalET[i].bottom.yPix <= sl)
                {
                    Vertex t = globalET[i].top;
                    Vertex b = globalET[i].bottom;

                    aet[aetLen++] = i;

                    globalET[i].currX = globalET[i].XAtY(sl);

                        // if the edge is already in the AET
                        // then compute its next y value...
                    if (globalET[i].inAET)
                    {
                        if (globalET[i].dypix != 0)
                        {
                            globalET[i].currY -= ((double)globalET[i].dy) / 
                                                  ((double)globalET[i].dypix);
                        }
                    } else
                    {
                        globalET[i].inAET = true;

                                // So given a scan line Ys, we need to 
                                // find out the y value...
                                // We know yTop and yBottom values along with
                                // yTopPix and yBottomPix..
                                //
                                // So currY is just
                                //
                                // (sl - yTopPix) * (yTop - yBottom) / (yTopPix
                                // - yBottomPix)
                        globalET[i].currY = t.y +
                                            (((double)(sl - t.yPix)) *
                                            ((double)(t.y - b.y)) / 
                                            ((double)(t.yPix - b.yPix)));
                    }
                } else
                {
                    globalET[i].inAET = false;
                }
            } else
            {
                globalET[i].inAET = false;
            }
        }

            // now sort the active edges by x value..
            // we can do a simple bubble sort here!!!
        sortAETbyX(aet, globalET, aetLen, nEdges);

            // if we dont have an even number of edges,
            // then skip the last edge... as it is just
            // a dodgy polygon...
        if (aetLen % 2 != 0) aetLen --;
        return aetLen;
    }

        /**
         * Sorts the active adge table by 
         * increasing order of the x pixel intersection
         * values with the scan line.
         */
    void sortAETbyX(int aet[], Edge edgeTable[],int aetLen,int nEdges)
    {
        int i, j, t;
        for (i = aetLen - 1; i >= 0;i--)
        {
            for (j = 1;j <= i;j++)
            {
                if (edgeTable[aet[j - 1]].currX > edgeTable[aet[j]].currX )
                {
                            // then swap
                    t = aet[j - 1];
                    aet[j - 1] = aet[j];
                    aet[j] = t;
                }
            }
        }
    }
}
