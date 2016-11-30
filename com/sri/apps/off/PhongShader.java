package com.sri.apps.off;

import java.util.*;

class PhongShader extends FaceShader
{
        /**
         * Constructor.
         *
         * Takes in the values of the kA, iA, and kD.
         */
    PhongShader()
    {
        ia = 1;
        n = 200;
        ks = 0.5;
        kd = 0.4;
        ka = 0.1;
    }

        /**
         * Phong shades a face onto an image.
         */
    public void fillFace (Face f, SImage image, Vector lights,
                          int aet[], Edge globalET[], double foc)
    {
        if (!commonInit(f, image, lights, aet, globalET)) return ;

        double r, g, b;
        int aetLen;

        double Xa, Xb, Ya, Yb;
        double za, zb, zp;
        int sx, ex;
        int nLights = lights.size();

        SVector sNa = new SVector(0, 0, 0);
        SVector sNb = new SVector(0, 0, 0);
        SVector sNp = new SVector(0, 0, 0);
        SVector V = new SVector(0, 0, 0); // the viewer vector...

        double Yb_Ya, Xb_Xa;
        double zb_za, xb_xa;
        double x_nb_na, y_nb_na, z_nb_na;
        double diffInt, specInt, ndotl, rdotv, rvn;
        double rx, ry, rz, rmag, Vmag;

        for (int sl = (int)maxy; sl >= (int)miny;sl--)
        {
            aetLen = updateAET(aet, globalET, sl, sl == maxy);

            //viewer.y = sl;


                // take each pair of edges and draw between
                // their currX, while interpolating all
                // values...
            for (int ed = 0;ed < aetLen;)
            {
                Edge curr = globalET[aet[ed++]];
                Edge next = globalET[aet[ed++]];

                        // this is the (y1 - ys) /(y1 - y2) for the 2 edges..
                double cY = ((double)(curr.top.yPix - sl)) /
                                                    ((double)curr.dypix);

                double nY = ((double)(next.top.yPix - sl)) /
                                                    ((double)next.dypix);

                        // find the z values on both the edges..
                za = curr.top.transZ - (curr.dz * cY);
                zb = next.top.transZ - (next.dz * nY);

                Xa = curr.top.transX - (curr.dx * cY);
                Xb = next.top.transX - (next.dx * nY);

                Ya = curr.top.transY - (curr.dy * cY);
                Yb = next.top.transY - (next.dy * nY);

                sNa.x = curr.top.normal.x - (curr.dN.x * cY);
                sNa.y = curr.top.normal.y - (curr.dN.y * cY);
                sNa.z = curr.top.normal.z - (curr.dN.z * cY);

                sNb.x = next.top.normal.x - (next.dN.x * nY);
                sNb.y = next.top.normal.y - (next.dN.y * nY);
                sNb.z = next.top.normal.z - (next.dN.z * nY);

                sx = curr.currX;
                ex = next.currX;

                zb_za = zb - za;
                xb_xa = 1.0 / (ex - sx);

                Xb_Xa = Xb - Xa;
                Yb_Ya = Yb - Ya;

                    // difference in normal's x, y and z components
                x_nb_na = sNb.x - sNa.x;
                y_nb_na = sNb.y - sNa.y;
                z_nb_na = sNb.z - sNa.z;

                    // check for trivial rejection...
                if ( ! ((sx < 0 && ex < 0) || (sx >= iw && ex >= iw)))
                {
                            // do cliping
                    if (sx < 0) sx = 0;
                    if (ex >= iw) ex = iw - 1;

                    for (int i = sx;i <= ex;i++)
                    {
                                // we need this later so 
                                // pre compute...
                        double mult_fac = xb_xa * (ex - i);

                            // what we are doing is
                            // multiplying by 1 / (xb - xa)
                            // rather than dividing by xb - xa.
                            // This is because divisions have a higher
                            // strength and thus more time consuming.
                            // Same for Intensity interpolation
                            // Basic Strength Reduction
                        V.z = zp = zb - (zb_za * mult_fac);

                        if (zp <= 0 && zp > zf[i][sl])
                        {
                            //V.x = i;
                            zf[i][sl] = zp;

                                    // interpolate normal...
                            sNp.x = sNb.x - (x_nb_na * mult_fac);
                            sNp.y = sNb.y - (y_nb_na * mult_fac);
                            sNp.z = sNb.z - (z_nb_na * mult_fac);
                            
                            V.x = Xb - (Xb_Xa * mult_fac);
                            V.y = Yb - (Yb_Ya * mult_fac);

                                    // interpolate normal...
                            sNp.normalize();

                            //V.normalize();

                                    // calculate the diffuse and specular
                                    // intensities...
                            diffInt = specInt = 0;

                            for (int j = 0;j < nLights;j++)
                            {
                                // the lights are calculated at each vertex as
                                // normally...
                                Light l = (Light)lights.elementAt(j);

                                ndotl = l.dotProduct(sNp);

                                if (ndotl > 0)
                                {
                                        // R = 2N ( N.L) - L
                                    rx = (2 * ndotl * sNp.x - l.x);
                                    ry = (2 * ndotl * sNp.y - l.y);
                                    rz = (2 * ndotl * sNp.z - l.z);

                                        // The normalisation
                                    rmag = Math.sqrt(rx*rx+ry*ry+rz*rz);
                                    rx /= rmag;
                                    ry /= rmag;
                                    rz /= rmag;

                                    Vmag = Math.sqrt(V.x*V.x+V.y*V.y+V.z * V.z);

                                        // R.V / |V| as V is not normalised...
                                    rdotv = (rx*V.x+ry*V.y+rz*V.z)/Vmag;

                                        // (R.V) ^ n
                                    rvn = (rdotv > 0 ? Math.pow(rdotv, n) : 0);

                                    // also involve speculare factor...
                                    diffInt += l.intensity * ndotl;
                                    specInt += l.intensity * rvn;
                                }
                            }

                            diffInt *= kd;
                            specInt *= ks;

                            if (diffInt < 0) diffInt = 0;
                            else if (diffInt > 1) diffInt = 1;

                            if (specInt < 0) specInt = 0;
                            else if (specInt > 1) specInt = 1;

                            specInt *= SPEC_COLOR;

                                    // find color..
                            r = (short)(ambR + (diffInt * odr) + specInt);
                            g = (short)(ambG + (diffInt * odg) + specInt);
                            b = (short)(ambB + (diffInt * odb) + specInt);

                            if (r < Globals.MIN_VAL) r = Globals.MIN_VAL;
                            else if (r > Globals.MAX_VAL) r = Globals.MAX_VAL;
                            if (g < Globals.MIN_VAL) g = Globals.MIN_VAL;
                            else if (g > Globals.MAX_VAL) g = Globals.MAX_VAL;
                            if (b < Globals.MIN_VAL) b = Globals.MIN_VAL;
                            else if (b > Globals.MAX_VAL) b = Globals.MAX_VAL;

                            rf[i][sl] = (short)r;
                            gf[i][sl] = (short)g;
                            bf[i][sl] = (short)b;
                        }
                    }
                }
            }
        }
    }
}
