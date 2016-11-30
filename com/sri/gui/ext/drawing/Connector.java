package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;

/**
 * A connector object.
 */
public abstract class Connector implements SceneElement
{
        /**
         * The shapes connected to each end.
         */
    protected Object endPoint[] = new Object[2];

        /**
         * The transform of this object.
         */
    protected Transform transform;
    
        /**
         * The inverse transform matrix for the current state
         * of the object.
         */
    protected Transform inverse;

        /**
         * The temporary transform.
         */
    protected Transform temp;
    
        /**
         * Tells whether the given mouse coordinate is
         * within this Drawable object or not.
         */
    public abstract boolean contains(int x,int y);

        /**
         * Default paint method.
         * Paints at the current bounds and as FIXED_MODE
         */
    public abstract void paint(Graphics g, int xOffset, int yOffset);

        /**
         * Tells if this object needs repaint.
         */
    public boolean needsRepaint()
    {
        return true;//needsRepaint;
    }

        /**
         * Resets the repaint flag.
         */
    public void resetRepaintRequest()
    {
        //needsRepaint = false;
    }

        /**
         * Request a repaint on this box.
         */
    public void requestRepaint()
    {
        //needsRepaint = true;
    }

        /**
         * Called when focus is lost.
         */
    public void removeFocus()
    {
        // does nothing so far...
    }
}
