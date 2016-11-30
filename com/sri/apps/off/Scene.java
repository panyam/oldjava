package com.sri.apps.off;

import java.io.*;
import java.util.*;

public class Scene
{
        /**
         * The list of objects.
         */
    protected Vector objects = new Vector();

        /**
         * The list of light sources.
         */
    protected Vector lights = new Vector();

        /**
         * The Ambient light.
         */
    protected double ambientLight;

        /**
         * A global edge table.
         * So that each face could put
         * all its edges in this table,
         * rather than having to create
         * new arrays and free memory
         * each time a polygon is filled.
         */
    public Edge globalET[];

        /**
         * A Global Active Edge table.
         * The size of the table is the number
         * of edges of the face with the most
         * number of vertices.
         *
         * We are doing this so that we dont waste
         * allocating and freeing memory for the
         * AET each time a polygon is filled.
         */
    public int aet[];

        /**
         * Maximum vertices on a face.
         */
    public int maxVertices;
    
        /**
         * Creates a new scene object.
         */
    Scene()
    {
        ambientLight = 0;       // default value
    }

        /**
         * Create AET and AET Indices.
         */
    void createAET(int mx)
    {
        maxVertices = mx + 2;
        aet = new int[maxVertices];

        globalET = new Edge[maxVertices];

        for (int i = 0;i < maxVertices;i++)
        {
            globalET[i] = new Edge();
        }
    }

        /**
         * Returns all the light sources.
         */
    Vector getLights()
    {
        return lights;
    }

        /**
         * Adds a new polyhedron object.
         */
    void addObject(Polyhedron obj)
    {
        if (obj == null) return ;
        if (obj.maxVertices > maxVertices) maxVertices = obj.maxVertices;
        objects.addElement(obj);
    }

        /**
         * Adds a new light source to the scene.
         */
    void addLight(Light light)
    {
        light.normalize();
        lights.addElement(light);
    }

        /**
         * Remove a light source from the scene.
         */
    void removeLight(int which)
    {
        if (which >= 0) lights.removeElementAt(which);
    }

        /**
         * Removes an object from the scene.
         */
    void removeObject(int which)
    {
        if (which >= 0) objects.removeElementAt(which);
    }

        /**
         * Sets the ambient light of this scene.
         */
    void setAmbientLight(double amb)
    {
        this.ambientLight = amb;
    }

        /**
         * Prints information to an output stream.
         */
    void print(OutputStream out)
    {
        /*out << "Ambient light: " << ambientLight << endl;

        out << "Light Sources: ";

        for (unsigned i = 0;i < lights.size();i++)
        {
            out << "   " << (i + 1) << *lights[i] << endl;
        }

        out << "Light Sources: ";

        for (unsigned i = 0;i < objects.size();i++)
        {
            out << "   " << (i + 1) << endl;
            objects[i].print(out);
        }*/
    }

        /**
         * Returns the requested object.
         */
    Polyhedron getObject(int which)
    {
        return (Polyhedron)objects.elementAt(which);
    }



        /**
         * Returns the requested light source.
         */
    Light getLight(int which)
    {
        return (Light)lights.elementAt(which);
    }

        /**
         * Returns the number of objects 
         * in this scene.
         */
    int getObjectCount()
    {
        return objects.size();
    }


        /**
         * Returns the ambient light value.
         */
    double getAmbientLight()
    {
        return ambientLight;
    }
}
