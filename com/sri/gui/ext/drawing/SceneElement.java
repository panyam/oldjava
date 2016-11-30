
package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.ext.borders.*;
import com.sri.gui.ext.backgrounds.*;

    /**
     * The super class of all on screen elements.
     */
public interface SceneElement
{
        /**
         * Tells whether the given mouse coordinate is
         * within this Drawable object or not.
         */
    public abstract boolean contains(int x,int y);


        /**
         * Processes a mouse released event.
         */
    public abstract int mouseReleased(MouseEvent e,
                                        int mX,int mY,
                                        //int pressedX, int pressedY,
                                        SceneViewer shapeRenderer);

        /**
         * Processes a mouse dragged event.
         */
     public abstract int mouseDragged(MouseEvent e,
                                        int mX,int mY,
                                        //int pressedX, int pressedY,
                                        SceneViewer shapeRenderer);

        /**
         * Processes a mouse pressed event.
         */
    public abstract int mousePressed(MouseEvent e,
                                        int mX,int mY,
                                        SceneViewer shapeRenderer);

        /**
         * Processes a mouse move event.
         */
    public abstract int mouseMoved(MouseEvent e,
                                        int mX,int mY,
                                        SceneViewer shapeRenderer);

        /**
         * Default paint method.
         * Paints at the current bounds and as FIXED_MODE
         */
    public abstract void paint(Graphics g, int xOffset, int yOffset);

        /**
         * Notifies that this object is currently in "creation" mode.
         */
    public abstract void setCreating(boolean isCreating);

        /**
         * Tells if we are still in creation mode.
         */
    public abstract boolean isCreating();

        /**
         * Tells if this shape is currently undergoing any transformation
         */
    public abstract boolean isTransforming() ;

        /**
         * Sets the transformation flag for this element.
         */
    public abstract void setTransforming(boolean isTransforming) ;

        /**
         * Tells that the element has lost focus.
         */
    public abstract void removeFocus();
}
