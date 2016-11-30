package com.sri.apps.off;

import java.io.*;
import java.util.*;

class SVector
{
    public double x, y, z;

        /**
         * Constructor.
         */
    SVector()
    {
        setValues(0, 0, 0);
    }

        /**
         * Constructor.
         */
    SVector(double x, double y, double z)
    {
        setValues(x, y, z);
    }
    
        /**
         * Returns the magnitude of this vector.
         */
    double magnitude()
    {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

        /**
         * Returns the dot product with another vector.
         */
    double dotProduct(SVector another)
    {
        return (x * another.x) + (y * another.y) + (z * another.z);
    }

        /**
         * Tells if this vector is perpendicular
         * to another.
         */
    boolean isPerpendicularTo(SVector another)
    {
        return (dotProduct(another) == 0);
    }

        /**
         * Sets the x, y and z components.
         */
    void setValues(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

        /**
         * Adds another vector onto this vector.
         */
    void add(SVector another)
    {
        x += another.x;
        y += another.y;
        z += another.z;
    }

        /**
         * Normalize our self.
         */
    void normalize()
    {
        double mag = magnitude();
        x /= mag;
        y /= mag;
        z /= mag;
    }

        /**
         * Does a cross product with another vector
         * and returns the result.
         */
    SVector cross(SVector v)
    {
        SVector out = new SVector(0, 0, 0);

        out.x = (y * v.z) - (z * v.y);
        out.y = (z * v.x) - (x * v.z);
        out.z = (x * v.y) - (y * v.x);
        return out;
    }

        /**
         * Print to output stream.
         */
    void print(OutputStream out) throws IOException
    {
        String str = "(" + x + ", " + y + ", " + z + ")";
        out.write(str.getBytes());
    }
}
