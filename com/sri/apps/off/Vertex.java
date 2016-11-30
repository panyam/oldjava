package com.sri.apps.off;

import java.io.*;

class Vertex
{
        /**
         * Coordinates of the vertex.
         */
    double x, y, z;

    double transX, transY, transZ;

        /**
         * Values of the x and y coordinates in
         * terms of pixel values.
         *
         * These are short values, as we hope that
         * screen sizes dont exceed 16 bit values!!!
         */
    short xPix, yPix;

        /**
         * This normal is the average of all the faces
         * that share this vertex.
         */
    SVector normal = new SVector(0, 0, 0);

        /**
         * The vertex diffuse intensity.
         */
    double diffInt;

        /**
         * The vertex specular intensity.
         */
    double specInt;
   
        /**
         * Constructor.
         */
    Vertex(double x, double y, double z)
    {
        transX = this.x = x;
        transY = this.y = y;
        transZ = this.z = z;

        normal.x = normal.y = normal.z = 0;
    }

        /**
         * Prints a vertex to the output stream.
         */
    void print(OutputStream out, Vertex v)
    {
        //out << "(" << v.x << ", " << v.y << ", " << v.z << ") -> ";
        //out << "(" << v.xPix << ", " << v.yPix << ")";
    }
}
