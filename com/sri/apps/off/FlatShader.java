package com.sri.apps.off;

import java.util.*;

class FlatShader extends FaceShader
{
        /**
         * Constructor.
         */
    public FlatShader()
    {
        n = 1;
        ka = 0.5;
        ks = kd = 0.5;
        ia = 1;
    }
        /**
         * Flat shades a face onto an image.
         */
    public void fillFace (Face f, SImage image, Vector lights,
                          int aet[], Edge globalET[], double foc)
    {
        if (!commonInit(f, image, lights, aet, globalET)) return ;

        int r = (int)ambR, g = (int)ambG, b = (int)ambB;

        int aetLen;

        for (int i = 0;i < nLights;i++)
        {
            double ndotl;
            Light l = (Light)lights.elementAt(i);

            ndotl = l.intensity * kd * l.dotProduct(normal);

            if (ndotl > 0)
            {
                r += (l.red * ndotl);
                g += (l.green * ndotl);
                b += (l.blue * ndotl);
            }
        }

        if (r < Globals.MIN_VAL) r = Globals.MIN_VAL;
        else if (r > Globals.MAX_VAL) r = Globals.MAX_VAL;
        if (g < Globals.MIN_VAL) g = Globals.MIN_VAL;
        else if (g > Globals.MAX_VAL) g = Globals.MAX_VAL;
        if (b < Globals.MIN_VAL) b = Globals.MIN_VAL;
        else if (b > Globals.MAX_VAL) b = Globals.MAX_VAL;

                // for each scan line...
        for (int sl = (int)maxy; sl >= (int)miny;sl--)
        {
                // update the AET..
            aetLen = updateAET(aet, globalET, sl, sl == maxy);


                // take each pair of edges and draw between
                // their currX, while interpolating all
                // values...
            for (int ed = 0;ed < aetLen;ed += 2)
            {
                Edge curr = globalET[aet[ed]];
                Edge next = globalET[aet[ed + 1]];

                        // this is the (y1 - ys) /(y1 - y2) for the 2 edges..
                double cY = ((double)(curr.top.yPix - sl)) /
                                                    ((double)curr.dypix);

                double nY = ((double)(next.top.yPix - sl)) /
                                                    ((double)next.dypix);

                        // find the z values on both the edges..
                za = curr.top.transZ - (curr.dz * cY);
                zb = next.top.transZ - (next.dz * nY);

                zb_za = zb - za;

                sx = curr.currX;
                ex = next.currX;

                    // calculate 1 / (xb - xa)
                xb_xa = 1.0 / (ex - sx);

                    // check for trivial rejection...
                if (!((sx < 0 && ex < 0) || (sx >= iw && ex >= iw)))
                {
                            // apply clippin
                    if (sx < 0) sx = 0;

                    if (ex >= iw) ex = iw - 1;

                            // for all points in the scan, 
                            // fill it...
                    for (int i = sx;i <= ex;i++)
                    {
                            // what we are doing is
                            // multiplying by 1 / (xb - xa)
                            // rather than dividing by xb - xa.
                            // This is because divisions have a higher
                            // strength and thus more time consuming.
                            // Same for Intensity interpolation
                            // Basic Strength Reduction
                        zp = zb - (zb_za * xb_xa * (ex - i));

                        if (zp <= 0 && zp > zf[i][sl])
                        {   
                            rf[i][sl] = (short)r;
                            gf[i][sl] = (short)g;
                            bf[i][sl] = (short)b;
                            zf[i][sl] = zp;
                        }
                    }
                }
            }
        }
    }
}
