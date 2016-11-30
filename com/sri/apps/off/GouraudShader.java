package com.sri.apps.off;

import java.util.*;

class GouraudShader extends FaceShader
{
        /**
         * Constructor.
         *
         * Takes in the values of the kA, iA, and kD.
         */
    GouraudShader()
    {
        ia = 1;
        n = 20;
        ks = 0;
        kd = 0.35;
        ka = 0.1;
    }

        /**
         * Gouraud shades a face onto an image. 
         */
    public void fillFace (Face f, SImage image, Vector lights,
                          int aet[], Edge globalET[], double foc)
    {
        if (!commonInit(f, image, lights, aet, globalET)) return ;

            // initially the color is just the ambient term...
        int r, g, b;

        int aetLen;

            // interpolated intensity values...
        double dIb, dIa, dib_ia, dIp;
        double sIb, sIa, sib_ia, sIp;

            // for each scan line...
        for (int sl = (int)maxy; sl >= (int)miny;sl--)
        {
                    // update AET
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

                        // interpolate diffuse and specular intensity
                dIa = curr.top.diffInt - (curr.dId * cY);
                dIb = next.top.diffInt - (next.dId * nY);

                sIa = curr.top.specInt - (curr.dIs * cY);
                sIb = next.top.specInt - (next.dIs * nY);

                zb_za = zb - za;
                sib_ia = sIb - sIa;
                dib_ia = dIb - dIa;

                sx = curr.currX;
                ex = next.currX;

                xb_xa = 1.0 / (ex - sx);

                    // check for trivial rejection...
                if (!((sx < 0 && ex < 0) || (sx >= iw && ex >= iw)))
                {
                        // clip x intersections...

                    if (sx < 0) sx = 0;

                    if (ex >= iw) ex = iw - 1;

                            // for each x on the scan line..
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
                                    // interpolate intensity...
                            sIp = sIb - (sib_ia * xb_xa * (ex - i));
                            dIp = dIb - (dib_ia * xb_xa * (ex - i));

                                    // validate
                            if (sIp < 0) sIp = 0;
                            else if (sIp > 1) sIp = 1;

                            if (dIp < 0) dIp = 0;
                            else if (dIp > 1) dIp = 1;

                            sIp *= SPEC_COLOR;

                                    // find color..
                            r = (short)(ambR + (dIp * odr) + sIp);
                            g = (short)(ambG + (dIp * odg) + sIp);
                            b = (short)(ambB + (dIp * odb) + sIp);

                            if (r < Globals.MIN_VAL) r = Globals.MIN_VAL;
                            else if (r > Globals.MAX_VAL) r = Globals.MAX_VAL;
                            if (g < Globals.MIN_VAL) g = Globals.MIN_VAL;
                            else if (g > Globals.MAX_VAL) g = Globals.MAX_VAL;
                            if (b < Globals.MIN_VAL) b = Globals.MIN_VAL;
                            else if (b > Globals.MAX_VAL) b = Globals.MAX_VAL;

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
