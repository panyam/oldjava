package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sri.gui.ext.borders.*;
import com.sri.gui.ext.backgrounds.*;

    /**
     * The super class of all Shape objects.
     */
public abstract class Shape implements SceneElement
{
            // Tells which of the current points
            // is being dragged.
    protected static int currDragPoint = 0;
            // tells where the mouse was currently pressed
    protected static int pressedX = -1, pressedY = -1;
    protected static int tempX, tempY, tempW, tempH;

        /**
         * Bounds of this shape.
         */
    public int x, y, width, height;

    protected final static int MOVEABLE_MASK =  1 << 0;              // 0th bit
    protected final static int RESIZABLE_MASK = 1 << 1;              // 1st bit
    protected final static int SHEARABLE_MASK = 1 << 2;              // 2nd bit
    protected final static int ROTATABLE_MASK = 1 << 3;              // 3rd bit

                                                        // 11th and 12th bit
    protected final static int WHICH_TO_SHOW_MASK = ((1 << 2) - 1) << 10;

                                                        // 13th-15th bits
    protected final static int MODE_MASK    =   ((1 << 3) - 1) << 12;
        /**
         * Different mouse modes.
         */
    public final static int NORMAL_MODE = 0 << 12;
    public final static int SCALING_MODE = 1 << 12;
    public final static int ROTATING_MODE = 2 << 12;
    public final static int CONTROLLING_MODE = 3 << 12;
    public final static int SHEARING_MODE = 4 << 12;
    public final static int TRANSLATING_MODE = 4 << 12;
    public final static int CREATING_MODE = 5 << 12;
    public final static int TRANSFORMING_MODE = 7 << 12;        // all bits set!!!

        /**
         * Show no points.
         */
    public final static int SHOW_NO_POINTS = 0;

        /**
         * Show only the rotation points
         */
    public final static int SHOW_ROTATION_POINTS = 1;
    
        /**
         * Show the Scaling points.
         */
    public final static int SHOW_SHEARING_POINTS = 2; 
    
        /**
         * Show resizing and control points.
         */
    public final static int SHOW_DEFAULT_POINTS = 3;
        /**
         * THis is used to mention the flags of 
         * various fields.
         */
    protected int flags = ROTATABLE_MASK |
                          SHEARABLE_MASK |
                          MOVEABLE_MASK |
                          RESIZABLE_MASK |
                          (WHICH_TO_SHOW_MASK & (SHOW_ROTATION_POINTS << 2));
    
        /**
         * Description string of the Shape object.
         */
    protected String description = "Shape Object";
    
        /**
         * Outline color of the Shape object.
         */
    protected Color outlineColor = Color.black;

        /**
         * The border for this object.
         */
    protected Border border = Border.defaultBorder;
    
        /**
         * Tells which points need to be shown.
         */
    public final void showPoints(int flags)
    {
    }
    
        /**
         * Sets or clears the resizable option.
         */
    public final void setResizable(boolean s)
    {
        if (s) flags |= RESIZABLE_MASK;
        else flags &= (0xffffffff ^ RESIZABLE_MASK);
    }
    
        /**
         * Sets or clears the resizable option.
         */
    public final boolean isResizable()
    {
        return (flags & RESIZABLE_MASK) != 0;
    }
    
        /**
         * Sets or clears the resizable option.
         */
    public final void setMovable(boolean m)
    {
        if (m) flags |= MOVEABLE_MASK;
        else flags &= (0xffffffff ^ MOVEABLE_MASK);
    }
    
        /**
         * Sets or clears the movable option.
         */
    public final boolean isMovable()
    {
        return (flags & MOVEABLE_MASK) != 0;
    }

        /**
         * Sets or clears the transforming option.
         */
    public final void setTransforming(boolean t)
    {
        if (t) flags |= TRANSFORMING_MODE;
        else flags &= (0xffffffff ^ TRANSFORMING_MODE);
    }

        /**
         * Tells if this shape is currently undergoing any transformation
         */
    public final boolean isTransforming()
    {
        return (flags & TRANSFORMING_MODE) != 0;
    }
    
        /**
         * Sets or clears the transforming option.
         */
    public final void setCreating(boolean c)
    {
        if (c) flags |= CREATING_MODE;
        else flags &= (0xffffffff ^ CREATING_MODE);
    }

        /**
         * Tells if this shape is currently undergoing any transformation
         */
    public final boolean isCreating()
    {
        return (flags & CREATING_MODE) != 0;
    }
    
        /**
         * Sets the outline color of this shape.
         */
    public final void setOutlineColor(Color c)
    {
        outlineColor = c;
    }
    
        /**
         * Gets the outline color of this shape.
         */
    public Color getOutlineColor()
    {
        return outlineColor;
    }
    
        /**
         * Sets the visible points of this object.
         */
    public final void setVisiblePoints(int p)
    {
        flags = (flags & (0xffffffff ^ WHICH_TO_SHOW_MASK)) | (p << 10);
    }

        /**
         * Tells whether this shape has focus or not.
         */
    public int getVisiblePoints()
    {
        return (flags & WHICH_TO_SHOW_MASK) >> 10;
    }

        /**
         * Draws the outline.
         */
    public abstract void paintOutline(Graphics g, int xOffset, int yOffset);

        /**
         * sets the mode of the object.
         * No validation for values of mode is done here...
         * It is assumed that it is exactly one of the _MODE values
         * as described above...
         */
    protected void setMode(int mode)
    {
        flags &= (0xffffffff ^ TRANSFORMING_MODE);
        flags |= mode;
    }

        /**
         * sets the mode of the object.
         */
    protected int getMode()
    {
        return flags & TRANSFORMING_MODE;
    }

        /**
         * Sets the bounds.
         */
    public void setBounds(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
}
