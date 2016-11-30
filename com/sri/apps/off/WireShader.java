package com.sri.apps.off;

import java.util.*;

class WireShader extends FaceShader
{
        /**
         * Constructor.
         */
    public WireShader()
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

        double r = ambR, g = ambG, b = ambB;

        int aetLen;

        for (int i = 0;i < nLights;i++)
        {
            double ndotl;
            Light l = (Light)lights.elementAt(i);

            ndotl = l.intensity * kd * l.dotProduct(normal);

            if (ndotl > 0)
            {
                r += (odr * ndotl);
                g += (odg * ndotl);
                b += (odb * ndotl);
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

                sx = curr.currX;
                ex = next.currX;

                    // check for trivial rejection...
                if (!((sx < 0 && ex < 0) || (sx >= iw && ex >= iw)))
                {
                            // apply clippin
                    if (sx < 0) sx = 0;

                    if (ex >= iw) ex = iw - 1;

                    rf[sx][sl] = ((short)255);
                    gf[sx][sl] = ((short)255);
                    bf[sx][sl] = ((short)255);
                    rf[ex][sl] = ((short)255);
                    gf[ex][sl] = ((short)255);
                    bf[ex][sl] = ((short)255);
                }
            }
        }
    }
}
