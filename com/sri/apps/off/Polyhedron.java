package com.sri.apps.off;

import java.io.*;
import java.util.*;

class Polyhedron
{
    double sx, sy, sz, rx, ry, rz, tx, ty, tz;

    int maxVertices;

        /**
         * The list of faces.
         */
    Face faces[];

        /**
         * The number of faces and the maximum 
         * capacity on the number of faces that
         * can be added.
         */
    int nFaces;

        /**
         * The list of vertices.
         */
    Vertex vertices[];

        /**
         * The number of vertices and the maximum 
         * capacity on the number of vertices that
         * can be added.
         */
    int nVertices;

        /**
         * Constructor.
         */
    public Polyhedron(int v, int f)
    {
        nFaces = nVertices = 0;

        faces = new Face[f];
        vertices = new Vertex[v];
    }

    public Polyhedron(Polyhedron another)
    {
        nFaces = another.nFaces;
        nVertices = another.nVertices;
        faces = new Face[nFaces];
        vertices = new Vertex[nVertices];

        maxVertices = another.maxVertices;

        for (int i = 0;i < nVertices;i++)
        {
            Vertex v = another.vertices[i];
            vertices[i] = new Vertex(v.x, v.y, v.z);
        }

        for (int i = 0;i < nFaces;i++)
        {
            faces[i] = new Face(another.faces[i]);
        }
    }

        /**
         * The number of vertices.
         */
    public int getVertexCount()
    {
        return nVertices;
    }

        /**
         * The number of faces.
         */
    public int getFaceCount()
    {
        return nFaces;
    }

        /**
         * Adds a new face.
         */
    public void addFace(Face newFace)
    {
        if (nFaces < faces.length) faces[nFaces++] = newFace;
    }

        /**
         * Adds a new vertex.
         */
    void addVertex(Vertex newVertex)
    {
        if (nVertices < vertices.length) vertices[nVertices++] = newVertex;
    }

        /**
         * Adds a new vertex, whose coordinates are given.
         */
    void addVertex(double x, double y, double z)
    {
        addVertex(new Vertex(x, y, z));
    }

        /**
         * Sets the current transformation to this transformation.
         */
    void applyTransformation(Transform tMatrix, boolean overWrite)
    {
                // we also need to calculate the new min and max
                // x, y and z coordinates...

        for (int i = 0;i < nVertices;i++)
        {
                // now we transform the vertex curr
                // with the current transformation...
            tMatrix.transform(vertices[i]);
        }
    }

        /**
         * Calculates the pixel values of the x and y 
         * components of each of the coordinates.
         */
    void calcPixelValues(int width, int height, double f)
    {
        int h2 = height / 2;
        int w2 = width / 2;

        for (int i = 0;i < nVertices;i++)
        {
            Vertex v = vertices[i];

                    // after the transformation calculate
                    // the projected points...
            double fac = f / -(v.transZ);

            v.xPix = (short)((0.5 + v.transX * fac) + w2);
            v.yPix = (short)((0.5 + v.transY * fac) + h2);
        }
    }

        /**
         * Prints information about this object.
         */
    void print(OutputStream out)
    {
        /*out << "Faces: " << endl;
        for (int i = 0; i < nFaces;i++)
        {
            out << "   ";
            faces[i].print(out);
            out << endl;
        }

        out << "Vertices: " << endl;
        for (int i = 0; i < nVertices;i++)
        {
            out << "   " << vertices[i] << endl;
        }
        out << endl;*/
    }

        /**
         * Returns the ith face.
         */
    Face getFace(int i)
    {
        return faces[i];
    }

        /**
         * Returns the ith vertex.
         */
    Vertex getVertex(int i)
    {
        return vertices[i];
    }

        /**
         * Draws itself on an image.
         */
    void draw(FaceShader shader, SImage im, Vector lights,
              int aet[], Edge globalET[], double f)
    {
        if (shader == null)
        {
            int x1, x2, y1, y2;

            for (int j = 0;j < nFaces;j++)
            {
                Face currFace = faces[j];

                    // draw each face...
                int nVerts = currFace.getVertexCount();

                for (int i = 0;i < nVerts;i++)
                {
                    Vertex curr = vertices[currFace.getVertex(i)];
                    Vertex next = vertices[currFace.getVertex((i+1) % nVerts)];

                    x1 = curr.xPix;
                    y1 = curr.yPix;

                    x2 = next.xPix;
                    y2 = next.yPix;

                    im.drawLine(x1, y1, x2, y2, 255, 255, 255);
                }
            }
        } else
        {
            for (int j = 0;j < nFaces;j++)
            {
                shader.fillFace(faces[j], im, lights, aet, globalET, f);
            }
        }
    }

        /**
         * Normalizes all the vertex normals.
         */
    void normalizeVertexNormals()
    {
        for (int i = 0;i < nVertices;i++)
        {
            vertices[i].normal.normalize();
        }
    }

        /**
         * Calculates the normals of all the vertices.
         *
         * Basically goes through each vertices and for
         * every face that shares this vertex, adds the
         * vertex to this face.
         *
         * This function is normally called after 
         * a transformation is applied to all the points
         * in this object
         */
    void calculateVertexNormals()
    {
            // initialise all vertex normals to
            // zero
        for (int i = 0;i < nVertices;i++)
        {
            Vertex v = vertices[i];
            v.normal.x = v.normal.y = v.normal.z = 0;
        }

            // for each face, calculate its
            // normal and add it to the shared
            // vertices
        for (int f = 0;f < nFaces;f++)
        {
            Face face = faces[f];
            face.calculateNormal(vertices);

            int nfV = face.getVertexCount();
            SVector faceNormal = face.getNormal();

            for (int vi = 0;vi < nfV;vi++)
            {
                Vertex v = vertices[face.getVertex(vi)];

                    // add the normal to it..
                v.normal.add(faceNormal);
            }
        }

            // finalise normalise all vertex normals
        normalizeVertexNormals();

    }

        /**
         * Calculates the normals of all the vertices,
         * based on the light sources and the shading 
         * parameters and the shader parameters.
         */
    void calculateVertexIntensities(Vector lights, FaceShader shader)
    {
        int nLights = lights.size();
        double ndotl;
        double rdotv;
        double rvn;

        double rx, ry, rz, rmag, vmag;

        for (int i = 0;i < nVertices;i++)
        {
            Vertex v = vertices[i];

            v.diffInt = v.specInt = 0;

            for (int j = 0;j < nLights;j++)
            {
                    // the lights are calculated at each vertex as
                    // normally...
                Light l = (Light)lights.elementAt(j);

                ndotl = l.dotProduct(v.normal);

                if (ndotl > 0)
                {
                            // R = @N ( N.L) - L
                    rx = (2 * ndotl * v.normal.x - l.x);
                    ry = (2 * ndotl * v.normal.y - l.y);
                    rz = (2 * ndotl * v.normal.z - l.z);

                            // The normalisation
                    rmag = Math.sqrt(rx * rx + ry * ry + rz * rz);
                    rx /= rmag;
                    ry /= rmag;
                    rz /= rmag;

                    vmag = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);

                            // R.V / |V| as V is not normalised...
                    rdotv = (rx * v.x + ry * v.y + rz * v.z) / vmag;

                            // (R.V) ^ n
                    rvn = (rdotv > 0 ? Math.pow(rdotv, shader.n) : 0);

                        // also involve speculare factor...
                    v.diffInt += l.intensity * ndotl;
                    v.specInt += l.intensity * rdotv;
                }
            }
            v.diffInt *= shader.kd;
            v.specInt *= shader.ks;
        }
    }

    void sortFaces()
    {
            // first calculate the boundaries of all faces
        for (int i = 0;i < nFaces;i++)
        {
            faces[i].calculateBounds(vertices);
        }
        sort(faces, 0, nFaces - 1);
    }

        /**
         * Sort faces by relative z position.
         *
         * Not Implemented.
         */
    void sort(Face []a, int lo0, int hi0)
    {
	    int lo = lo0;
	    int hi = hi0;
        int T2;
	    if (lo >= hi) return;
	    Face mid = a[(lo + hi) >> 1];
        Face T;
	    while (lo < hi)
        {
	        while (lo<hi && a[lo].compareWith(vertices, mid) < 0) lo++;
	        while (lo<hi && a[hi].compareWith(vertices, mid) > 0) hi--;
	        if (lo < hi) {
		        T = a[lo];
		        a[lo] = a[hi];
		        a[hi] = T;
                lo++;
                hi--;
	        }
	    }
	    if (hi < lo)
        {
	        T2 = hi;
	        hi = lo;
	        lo = T2;
	    }
	    sort(a, lo0, lo);
	    sort(a, lo == lo0 ? lo+1 : lo, hi0);
    }
}
