package com.sri.apps.off;


import java.io.*;
import java.util.*;

public class OFFReader
{
        /**
         * Default red value.
         */
    final static int DEFAULT_RED = 255;

        /**
         * Default green value.
         */
    final static int DEFAULT_GREEN = 255;

        /**
         * Default blue value.
         */
    final static int DEFAULT_BLUE = 255;

        /**
         * Reads a polyhedron description from an off file.
         */
    static Polyhedron readOffStream(InputStream istream) throws Exception
    {
        Polyhedron out = null;            // the output polyhedron
                
                    // the tokenizer to read the file with...
        Tokenizer tokenizer = new Tokenizer(istream);
                
        int nVertices, nFaces, nEdges;      // # vertices, faces and edges...


                // for the time being we ignore new line characters
                // ie they are not part important tokens.
        tokenizer.ignoreNewLineToken(true);

        try
        {
            int tok = tokenizer.getToken();

            if (tok == Tokenizer.STRING_TOKEN)
            {
                        // read the header of the file...
                String header = tokenizer.getLexeme();

                        // is the header a valid one???
                if (!isValidOffFileHeader(header))
                {
                    return null;
                }

                        // read the number of vertices, faces and edges.
                nVertices = tokenizer.getInt();
            } else if (tok == Tokenizer.INT_TOKEN)
            {
                nVertices = Integer.parseInt(tokenizer.getLexeme());
            } else return null;

            nFaces = tokenizer.getInt();
            nEdges = tokenizer.getInt();

            //System.out.println("Nv, Nf = " + nVertices + ", " + nFaces);

            out = new Polyhedron(nVertices, nFaces);

            readVertices(out, tokenizer, nVertices);

            readFaces(out, tokenizer, nFaces);
        } catch (Exception ite)
        {
            ite.printStackTrace();
            return null;
        }
        return out;
    }

        /**
         * Given a header, tells if it is a valid OFF file header.
         */
    static boolean isValidOffFileHeader(String header)
    {
            // simply we see if the header has the word OFF
            // if it does then take it otherwise skip it...
            // coz for this project, we assume it has the word OFF
            // and we dont care for the other variants of the OFF 
            // headers

        return (header.indexOf("OFF") >= 0);
    }


        /**
         * Reads all the vertices.
         */
    static void readVertices(Polyhedron poly, Tokenizer tokenizer, int nVertices) throws Exception
    {
        double x, y, z;              // temporary x, y and z variables
                // read all the vertices.
        for (int i = 0;i < nVertices;i++)
        {
                // read vertex coordinates
            x = tokenizer.getFloat();
            y = tokenizer.getFloat();
            z = tokenizer.getFloat();

            poly.addVertex((float)x, (float)y, (float)z);
        }
    }


        /**
         * Reads all the faces.
         */
    static void readFaces(Polyhedron poly, Tokenizer tokenizer, int nFaces) throws Exception
    {
        int cRed = DEFAULT_RED, cGreen = DEFAULT_GREEN, cBlue = DEFAULT_BLUE;
        int colors[] = new int[4];  // info about color can be 0 to 4 numbers
        int nNumbers = 0;

                // now we make sure that we dont ignore new line chars..
                // this is coz each face description is as follows:
                // Nv, v0, v1, v2 .. v[Nv - 1] where Nv is the number of
                //                             vertices
                // Now after V[nv - 1] we get the color space information
                // that is 0 to 4 integers till the end of the line..
                // to see when we get to the end of the line, we need to
                // consider the new line as well...
        tokenizer.ignoreNewLineToken(false);

        poly.maxVertices = 0;

                // read all the faces
        for (int i = 0;i < nFaces;i++)
        {
            int nVerts = tokenizer.getInt(); // read the # verts of the face

            if (nVerts > poly.maxVertices) poly.maxVertices = nVerts;

            int vertIndex = 0;
            Face newFace = new Face(nVerts);
            for (int j = 0;j < nVerts; j++)
            {
                vertIndex = tokenizer.getInt();
                newFace.addVertex(vertIndex);
            }

                // after the vertex read the color information...
            int token = tokenizer.getToken();

            nNumbers = 0;

            while (nNumbers < 4 && token != Tokenizer.NEW_LINE_TOKEN)
            {
                if (token == Tokenizer.FLOAT_TOKEN)
                {
                    int val = (int)((new Double(tokenizer.getLexeme()).doubleValue() * Globals.MAX_VAL));
                    colors[nNumbers++] = val;
                } else if (token == Tokenizer.INT_TOKEN)
                {
                    colors[nNumbers++]=Integer.parseInt(tokenizer.getLexeme());
                }
                token = tokenizer.getToken();
            }

                // see how many numbers we have...
                // if we have one number and it is an integer
                // then it is an index into the color table..
                // this we shall not worry here...
                // so we only worry about 3 or 4 numbers...
            if (nNumbers >= 3)
            {
                cBlue = colors[nNumbers - 1];
                cGreen = colors[nNumbers - 2];
                cRed = colors[nNumbers - 3];

                        // we are skipping the alpha
                        // channel here...
            }

            newFace.setColor(cRed, cGreen, cBlue);
            poly.addFace(newFace);
        }
    }
}
