package com.sri.apps.off;

import java.io.*;
import java.awt.*;
import java.util.*;

class Face
{
    int vertIndex[];

        /**
         * Number of vertices present.
         */
    int nVertices = 0;

        /**
         * The normal of the plane that passes through all the 
         * vertices.
         */
    SVector normal = new SVector();


        /**
         * Components of the face color.
         */
    char colorRed, colorGreen, colorBlue;

        /**
         * The index to the vertices that
         * contain the min and max
         * x, y and z coordinates...
         *
         * This is space wise efficient 
         * than explicitly storing min and
         * max coordinate values.  The
         * assumption here is that
         * a face has no more than vertices
         * than the largest 16 bit value!!!
         */
    int minxIndex, minyIndex, minzIndex;
    int maxxIndex, maxyIndex, maxzIndex;

        /**
         * Constructor.
         */
    Face(int nv)
    {
        vertIndex = new int[nv];
        normal.x = normal.y = normal.z = 0;
    }

    Face (Face another)
    {
        nVertices = another.nVertices;
        normal.x = normal.y = normal.z = 0;
        vertIndex = another.vertIndex;
        setColor(another.colorRed, another.colorGreen, another.colorBlue);
        //System.arraycopy(vertIndex, 0, another.vertIndex, 0, nVertices);
    }

        /**
         * Set the color of this triangle.
         */
    void setColor(int r, int g, int b)
    {
        colorRed = (char)r;
        colorGreen = (char)g;
        colorBlue = (char)b;
    }

        /**
         * Adds a new vertex.
         */
    void addVertex(int vIndex)
    {
        if (vertIndex.length == nVertices)
        {
            int vi2[] = new int[nVertices + 5];
            System.arraycopy(vertIndex, 0, vi2, 0, nVertices);
            vertIndex = null;
            vertIndex = vi2;
        }
        vertIndex[nVertices++] = vIndex;
    }

        /**
         * Returns the normal of the face.
         * Assumes that calculateNormal has been called
         * prior to a call to this function.
         */
    SVector getNormal()
    {
        return normal;
    }

    final int getVertex(int which)
    {
        return vertIndex[which];
    }

        /**
         * Returns the number of vertices
         * in this face.
         */
    int getVertexCount()
    {
        return nVertices;
    }

        /**
         * Prints information about this face.
         */
    void print(OutputStream out)
    {
        /*out << "# Vertices: " << vertices.size() << endl;
        for (unsigned i = 0; i < vertices.size();i++)
        {
            out << vertices[i] << " ";
        }
        out << endl;*/

        //out << "Color: " << color << endl;
    }

        /**
         * Calculate the normal of the plane.
         */
    void calculateNormal(Vertex vertices[])
    {
        normal.setValues(0, 0, 0);

        Vertex v0 = vertices[vertIndex[0]];
        Vertex v1 = vertices[vertIndex[1]];
        Vertex v2 = vertices[vertIndex[2]];

        SVector va = new SVector (v1.transX - v0.transX,
                                  v1.transY - v0.transY,
                                  v1.transZ - v0.transZ);
        SVector vb = new SVector(v1.transX - v2.transX,
                                 v1.transY - v2.transY,
                                 v1.transZ - v2.transZ);

        normal = vb.cross(va);
        
        normal.normalize();
    }

        /**
         * Calculates the max and min x, y and
         * z coordinates.
         */
    void calculateBounds(Vertex vertices[])
    {
        minxIndex = minyIndex = minzIndex = 0;
        maxxIndex = maxyIndex = maxzIndex = 0;

        for (int i = 1;i < nVertices;i++)
        {
            int ind = vertIndex[i];
            Vertex v = vertices[ind];
            if (v.transX < vertices[minxIndex].transX)  minxIndex = ind;
            if (v.transY < vertices[minyIndex].transY)  minyIndex = ind;
            if (v.transZ < vertices[minzIndex].transZ)  minzIndex = ind;

            if (v.transX > vertices[maxxIndex].transX)  maxxIndex = ind;
            if (v.transY > vertices[maxyIndex].transY)  maxyIndex = ind;
            if (v.transZ > vertices[maxzIndex].transZ)  maxzIndex = ind;
        }
    }

        // tells if this face is 
        //  closer to the camera    ie greater minz
        //  further from the camera    ie greater minz
        // as the given face.
    int compareWith(Vertex vertices[], Face another)
    {
        double res = vertices[minzIndex].transZ -
                     vertices[another.minzIndex].transZ;
        if (res == 0) return 0;
        else if (res < 0) return -1;
        else return 1;
    }

        /**
         * Generates the edge table, whose items are
         * sorted by the smallest y coordinate.
         */
    void generateEdgeTable(Vertex vertices[], Edge globalET[])
    {
        for (int i = 0;i < nVertices;i++)
        {
            int next = i + 1;
            if (next == nVertices) next = 0;

                // current vertex
            Vertex vi = vertices[vertIndex[i]];

                // next vertex..
            Vertex vj = vertices[vertIndex[next]];

                // pointer to the ith edge in
                // the global edge table...
            Edge edge = globalET[i];

            if (vi.yPix > vj.yPix)
            {
                edge.top = vi;
                edge.bottom = vj;
            } else if (vi.yPix < vj.yPix)
            {
                edge.top = vj;
                edge.bottom = vi;
            } else
            {
                if (vi.xPix < vj.xPix)
                {
                    edge.top = vi;
                    edge.bottom = vj;
                } else
                {
                    edge.top = vj;
                    edge.bottom = vi;
                }
            }

            edge.inAET = false;
            edge.dxpix = edge.top.xPix - edge.bottom.xPix;
            edge.dypix = edge.top.yPix - edge.bottom.yPix;

            edge.dx = edge.top.transX - edge.bottom.transX;
            edge.dy = edge.top.transX - edge.bottom.transY;
            edge.dz = edge.top.transZ - edge.bottom.transZ;
            edge.dId = edge.top.diffInt - edge.bottom.diffInt;
            edge.dIs = edge.top.specInt - edge.bottom.specInt;
            edge.dN.x = edge.top.normal.x - edge.bottom.normal.x;
            edge.dN.y = edge.top.normal.y - edge.bottom.normal.y;
            edge.dN.z = edge.top.normal.z - edge.bottom.normal.z;


            edge.isHorizontal = edge.dypix == 0;

            edge.Cpix = vi.yPix;

            if (edge.dxpix != 0 && edge.dypix != 0)
            {
                edge.Cpix -= (((double)vi.xPix) *
                                 ((double)(vi.yPix - vj.yPix)) /
                                 ((double)(vi.xPix - vj.xPix)));
            }
        }
    }
}
