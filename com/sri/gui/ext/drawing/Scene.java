package com.sri.gui.ext.drawing;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;

/**
 * Describes a scene topology.
 * How do we get enough here...
 */
public class Scene
{
    public Shape shapes[] = new Shape[50];
    public int nShapes = 0;

    public Connector connectors[] = new Connector[50];
    public int nConnectors = 0;

        /**
         * Constructor.
         */
    public Scene()
    {
    }

        /**
         * Find a given shape.
         */
    public int find(Shape shape)
    {
        for (int i = 0;i < nShapes;i++)
        {
            if (shapes[i] == shape) return i;
        }
        return -1;
    }

        /**
         * Remove a given connector.
         */
    public void removeConnector(Connector connector)
    {
        removeConnector(find(connector));
    }

        /**
         * Return the index of a given connector.
         */
    public int find(Connector connector)
    {
        for (int i = 0;i < nConnectors;i++)
        {
            if (connectors[i] == connector) return i;
        }
        return -1;
    }

        /**
         * Remove the connector at the given index.
         */
    public void removeConnector(int which)
    {
        if (which < 0) return ;
        connectors[which] = null;
        for (int i = which;i < nConnectors - 1;i++)
        {
            connectors[i] = connectors[i + 1];
        }
        connectors[nConnectors - 1] = null;
        nConnectors --;
    }

        /**
         * Remove a given shape.
         */
    public void removeShape(Shape dev)
    {
        removeShape(find(dev));
    }

        /**
         * Remove a shape at the given index.
         */
    public void removeShape(int index)
    {
        if (index < 0) return ;
        Shape which = shapes[index];

        nShapes --;
        for (int i = index;i < nShapes;i++) shapes[i] = shapes[i + 1];
        shapes[nShapes] = null;
    }

        /**
         * Removes a given element.
         */
    public void removeElement(SceneElement element)
    {
        if (element instanceof Shape) removeShape((Shape)element) ;
        else if (element instanceof Connector)
            removeConnector((Connector)element);
    }

        /**
         * Add a new scene element.
         */
    public void addElement(SceneElement element)
    {
        if (element instanceof Shape) addShape((Shape)element) ;
        else if (element instanceof Connector)
            addConnector((Connector)element);
    }

        /**
         * Add a new network shape.
         */
    public void addShape(Shape shape)
    {
        ensureShapeCapacity(nShapes + 5);
        shapes[nShapes++] = shape;
    }

        /**
         * Add a new connector.
         */
    public void addConnector(Connector connector)
    {
        ensureConnectorCapacity(nConnectors + 5);
        connectors[nConnectors++] = connector;
    }

        /**
         * Add a connector between two shapes.
         */
    public void addConnector(Connector connector,
                        Shape from, String fromInterface,
                        Shape to, String toInterface)
    {
        addConnector(connector);
    }

        /**
         * Ensure a new shape capacity.
         */
    public void ensureShapeCapacity(int newDevCapacity)
    {
        if (shapes.length < newDevCapacity)
        {
            Shape dev2[] = shapes;
            shapes = new Shape[newDevCapacity];
            System.arraycopy(dev2, 0, shapes, 0, nShapes);
            dev2 = null;
        }
    }

        /**
         * Ensure a new connector capacity.
         */
    public void ensureConnectorCapacity(int newConnectorCapacity)
    {
        if (connectors.length < newConnectorCapacity)
        {
            Connector lnk2[] = connectors;
            connectors = new Connector[newConnectorCapacity];
            System.arraycopy(lnk2, 0, connectors, 0, nConnectors);
            lnk2= null;
        }
    }
}
