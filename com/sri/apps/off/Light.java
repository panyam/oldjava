package com.sri.apps.off;

import java.io.*;

class Light extends SVector
{
        // color components = of the light...
    short red = 255, blue = 255, green = 255;
        
        /**
         * Intensity of the light source.
         */
    double intensity;

        /**
         * Constructor.
         */
    Light(double in, double x, double y, double z)
    {
        intensity = in;

        this.x = x;
        this.y = y;
        this.z = z;

        normalize();
    }

        /**
         * Print to output stream.
         */
    void print(OutputStream out) throws IOException
    {
        String str = "Intensity: " + intensity + ", ";
        out.write(str.getBytes());
        super.print(out);
    }

        /**
         * Sets the color.
         */
    void setColor(int r, int g, int b)
    {
        red = (short)r;
        green = (short)g;
        blue = (short)b;
    }
}
