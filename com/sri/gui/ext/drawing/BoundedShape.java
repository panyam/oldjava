package com.sri.gui.ext.drawing;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import com.sri.gui.ext.backgrounds.*;
import com.sri.gui.ext.borders.*;

/**
 * This class is a child class of the Shape object which
 * describes a closed/bounded shape.
 * TODO:: Controlling os shape
 * TODO:: Shearing of shapes
 */
public abstract class BoundedShape extends Shape
{
    protected final static int POINT_W2     = 3;
    protected final static int POINT_WIDTH  = POINT_W2 * 2;

    protected short nListeners = 0;
    protected short maxSize = 20;
    protected ShapeListener []listeners = new ShapeListener[maxSize];
    protected final static int sizeIncr = 5;

        /**
         * The cursor at different size points.
         */
    protected final static int cursors[] =
    {
        Cursor.MOVE_CURSOR,
        Cursor.NW_RESIZE_CURSOR,
        Cursor.N_RESIZE_CURSOR,
        Cursor.NE_RESIZE_CURSOR,
        Cursor.E_RESIZE_CURSOR,
        Cursor.SE_RESIZE_CURSOR,
        Cursor.S_RESIZE_CURSOR,
        Cursor.SW_RESIZE_CURSOR,
        Cursor.W_RESIZE_CURSOR,
    };


        /**
         * The fill color.
         */
    protected Color fillColor = null;

        /**
         * Fill type of the shape.
         */
    protected Background background = null;

        /**
         * Constructor
         */
    protected BoundedShape()
    {
    }

        /**
         * Returns The text of this shape.
         */
    public String getText()
    {
        return "";//text;
    }

        /**
         * Sets the text of this shape.
         */
    public void setText(String s)
    {
        //text = s;
    }

        /**
         * Sets the fill color of this shape.
         */
    public void setFillColor(Color c)
    {
        fillColor = c;
    }

        /**
         * Gets the fill color of this shape.
         */
    public Color getFillColor()
    {
        return fillColor;
    }

        /**
         * Paints all the size points.
         *
         * By default most objects will have the same
         * type of size points. ie all drawable objects
         * will have rectangular boundaries so the size
         * points will lie along this rectangle.
         */
    protected void drawSizePoints(Graphics g, int xOffset, int yOffset)
    {
        Color store = g.getColor();
        g.setColor(Color.black);

        int w2 = width / 2;
        int h2 = height / 2;

        int lx = x + xOffset;
        int ty = y + yOffset;
        int rx = x + xOffset + width;
        int by = y + yOffset + height;
        int mx = (lx + rx) / 2;
        int my = (ty + by) / 2;

        g.fillRect(lx - POINT_W2, ty - POINT_W2, POINT_WIDTH, POINT_WIDTH);
        g.fillRect(mx - POINT_W2, ty - POINT_W2, POINT_WIDTH, POINT_WIDTH);
        g.fillRect(rx - POINT_W2, ty - POINT_W2, POINT_WIDTH, POINT_WIDTH);

        g.fillRect(lx - POINT_W2, my - POINT_W2, POINT_WIDTH, POINT_WIDTH);
        g.fillRect(rx - POINT_W2, my - POINT_W2, POINT_WIDTH, POINT_WIDTH);

        g.fillRect(lx - POINT_W2, by - POINT_W2, POINT_WIDTH, POINT_WIDTH);
        g.fillRect(mx - POINT_W2, by - POINT_W2, POINT_WIDTH, POINT_WIDTH);
        g.fillRect(rx - POINT_W2, by - POINT_W2, POINT_WIDTH, POINT_WIDTH);

        g.setColor(store);
    }

        /**
         * Adds a shape listener to the list of listeners.
         */
    public synchronized void addShapeListener(ShapeListener l) {
        if (l != null)
        {
                // check for duplicates
            for (int i = 0;i < nListeners;i++) {
                if (listeners[i] == l) return ;
            }

                // increase list size if max reached
            if (nListeners == maxSize) {
                maxSize += sizeIncr;
                ShapeListener []second = new ShapeListener[maxSize];
                System.arraycopy(listeners,0,second,0,nListeners);
                listeners = null;
                System.gc();
                listeners = second;
            }
            listeners[nListeners++] = l;
        }
    }

        /**
         * Removes a shape listener from the list of listeners.
         */
    public synchronized void removeShapeListener(ShapeListener l)
    {
        if (l != null)
        {
            for (int i = 0;i < nListeners;i++)
            {
                if (l == listeners[i])
                {
                    System.arraycopy(listeners,i + 1,listeners,i,nListeners - i - 1);
                    nListeners --;
                    return ;
                }
            }
        }
    }

        /**
         * Processes a mouse released event.
         */
    public int mouseReleased(MouseEvent e,
                             int mX,int mY,
                             //int pressedX, int pressedY,
                             SceneViewer renderer)
    {
        mouseDragged(e, mX, mY, renderer);

            // for now ignore this...
            // as negative widths and heights may make sense...
            // as these may imply inversions!!!
        /*if (width < 0)
        {
            x += width;
            width = -width;
        }

        if (height < 0)
        {
            y += height;
            height = -height;
        }*/

        setMode(NORMAL_MODE);

            // make all links "not-moving"
        currDragPoint = -1;
        renderer.setCursor(Cursor.getDefaultCursor());
        return 0;
    }

        /**
         * Processes a mouse dragged event.
         */
    public int mouseDragged(MouseEvent e, int mX,int mY,
                            //int pressedX, int pressedY,
                            SceneViewer renderer)
    {
            // first see if this shape is bein created coz if it is
            // then none of the other options are effective...
        int mode = getMode();
        if (mode == CREATING_MODE)
        {
            width = mX - pressedX;
            height = mY - pressedY;
            this.x = tempX;
            this.y = tempY;
            setVisiblePoints(SHOW_NO_POINTS);
        } else if (mode == TRANSLATING_MODE)
        {
            setVisiblePoints(SHOW_DEFAULT_POINTS);
            this.x = tempX + mX - pressedX;
            this.y = tempY + mY - pressedY;
        } else if (mode == SCALING_MODE)
        {
            setVisiblePoints(SHOW_DEFAULT_POINTS);
            int tx = tempX;
            int ty = tempY;
            int w2 = tempW;
            int h2 = tempH;
            if (currDragPoint == 1)
            {
                w2 = tempW + pressedX - mX;
                h2 = tempH + pressedY - mY;
                tx = mX;
                ty = mY;
            } else if (currDragPoint == 2)
            {
                h2 = tempH + pressedY - mY;
                ty = mY;
            } else if (currDragPoint == 3)
            {
                w2 = mX - x;
                h2 = tempH + pressedY - mY;
                ty = mY;
            } else if (currDragPoint == 4)
            {
                w2 = mX - x;
            } else if (currDragPoint == 5)
            {
                w2 = mX - x;
                h2 = mY- y;
            } else if (currDragPoint == 6)
            {
                h2 = mY - y;
            } else if (currDragPoint == 7)
            {
                w2 = tempW + pressedX - mX;
                h2 = mY - y;
                tx = mX;
            } else if (currDragPoint == 8)
            {
                w2 = tempW + pressedX - mX;
                tx = mX;
            }

            x = tx;
            y = ty;

            width = w2;
            height = h2;
        }

        return 0;
    }

        /**
         * Processes a mouse pressed event.
         */
    public int mousePressed(MouseEvent e, int mX, int mY,SceneViewer renderer)
    {
                // first see if this shape is bein created coz if it is
                // then none of the other options are effective...
        pressedX = mX;
        pressedY = mY;
        tempX = this.x;
        tempY = this.y;
        tempW = this.width;
        tempH = this.height;
        setVisiblePoints(SHOW_DEFAULT_POINTS);
        if (isCreating())
        {
            currDragPoint = -1;
            tempX = x = pressedX;
            tempY = y = pressedY;
            return 0;
        }

        currDragPoint = getMouseLocation(mX, mY);

        if (currDragPoint < 0) return -1;

        if (currDragPoint < 9)           // object being moved or resized
        {
            setMode(currDragPoint == 0 ? TRANSLATING_MODE : SCALING_MODE);
            setVisiblePoints(SHOW_DEFAULT_POINTS);
        }
        return currDragPoint;
    }

        /**
         * Processes a mouse move event.
         */
    public int mouseMoved(MouseEvent e, int mX, int mY, SceneViewer renderer)
    {
            // also it depends on whether renderer is in
            // adding link or a device...
        int loc = getMouseLocation(mX, mY);
        if (loc >= 0) 
        {
            renderer.setCursor(Cursor.getPredefinedCursor(cursors[loc]));
        }
        else
        {
            renderer.setCursor(Cursor.getDefaultCursor());
        }
        return loc;
    }

        /**
         * Given the mouse coordinates, tells where we are.
         * The outputs are:
         *
         *  < 0         ->    Invalid
         *    0         ->    Object can be moved
         *  1 - 8       ->    On one of the sizing points.
         */
    public int getMouseLocation(int cx,int cy)
    {
            // first check if there are any size points.
        int tx = x, ty = y, w = width, h = height;

        if (w < 0)
        {
            tx += w;
            w = -w;
        }

        if (h < 0)
        {
            ty += h;
            h = -h;
        }
        int w2 = w / 2;
        int h2 = h / 2;

        if (getVisiblePoints() == SHOW_DEFAULT_POINTS)
        {
            if (cx <= (tx + POINT_W2) && cx >= (tx - POINT_W2) &&
                cy <= (ty + POINT_W2) && cy >= (ty - POINT_W2))
            {
                return 1;
            }

            if (cx <= (tx + w2 + POINT_W2) && cx >= (tx + w2 - POINT_W2) &&
                cy <= (ty + POINT_W2) && cy >= (ty - POINT_W2))
            {
                return 2;
            }

            if (cx <= (tx + w + POINT_W2) && cx >= (tx + w - POINT_W2) &&
                cy <= (ty + POINT_W2) && cy >= (ty - POINT_W2))
            {
                return 3;
            }

            if (cx <= (tx + w + POINT_W2) && cx >= (tx + w - POINT_W2) &&
                cy <= (ty + h2 + POINT_W2) && cy >= (ty + h2 - POINT_W2))
            {
                return 4;
            }

            if (cx <= (tx + w + POINT_W2) && cx >= (tx + w - POINT_W2) &&
                cy <= (ty + h + POINT_W2) && cy >= (ty + h - POINT_W2))
            {
                return 5;
            }

            if (cx <= (tx + w2 + POINT_W2) && cx >= (tx + w2 - POINT_W2) &&
                cy <= (ty + h + POINT_W2) && cy >= (ty + h - POINT_W2))
            {
                return 6;
            }

            if (cx <= (tx + POINT_W2) && cx >= (tx - POINT_W2) &&
                cy <= (ty + h + POINT_W2) && cy >= (ty + h - POINT_W2))
            {
                return 7;
            }

            if (cx <= (tx + POINT_W2) && cx >= (tx - POINT_W2) &&
                cy <= (ty + h2 + POINT_W2) && cy >= (ty + h2 - POINT_W2))
            {
                return 8;
            }
        }


        if (cx >= tx && cy >= ty && cx <= (tx + w) && cy <= (ty + h))
        {
            return 0;
        }
        return -1;
    }


        /**
         * Tells if a given pixel in the screen is inside
         * this object.
         */
    public boolean contains(int x,int y)
    {
        return (getMouseLocation(x, y) >= 0);
    }
    
        /**
         * Sets or clears the rotatable option.
         */
    public final void setRotatable(boolean r)
    {
        if (r) flags |= ROTATABLE_MASK;
        else flags &= (0xffffffff ^ ROTATABLE_MASK);
    }
    
        /**
         * Sets or clears the resizable option.
         */
    public final boolean isRotatable()
    {
        return (flags & ROTATABLE_MASK) != 0;
    }
    
        /**
         * Sets or clears the shearable option.
         */
    public final void setShearable(boolean s)
    {
        if (s) flags |= SHEARABLE_MASK;
        else flags &= (0xffffffff ^ SHEARABLE_MASK);
    }
    
        /**
         * Sets or clears the resizable option.
         */
    public final boolean isShearable()
    {
        return (flags & SHEARABLE_MASK) != 0;
    }

        /**
         *  Length of a line
         */
    public final static double lineLength(float x1, float y1,
                                          float x2, float y2)
    {
        return Math.sqrt(((y2 - y1) * (y2 - y1)) + ((x2 - x1) * (x2 - x1)));
    }

        /**
         * Calculates the shortest distance between a point (px, py) 
         * and a line (x1, y1) to (x2, y2)
         */
    public final static double distBwPointAndLine(int px, int py,
                                                 int x1, int y1,
                                                 int x2, int y2)
    {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return ((dx * (py - y1)) - (dy * (px - x1))) / Math.sqrt(dx * dx + dy * dy);
    }

        /**
         * Draws the outline.
         */
    public void paintOutline(Graphics g, int xOffset, int yOffset)
    {
        x += xOffset;
        y += yOffset;
        g.drawRect(x, y, width, height);
        x -= xOffset;
        y -= yOffset;
    }

        /**
         * Called when focus is lost.
         */
    public void removeFocus()
    {
        setVisiblePoints(SHOW_NO_POINTS);
    }

    public void paint(Graphics g, int xOffset, int yOffset)
    {
            // and do the text stuff...
        if (getVisiblePoints() == SHOW_DEFAULT_POINTS)
        {
            drawSizePoints(g, xOffset, yOffset);
        //} else if (visiblePoints == LINK_POINTS) {
            //drawLinkPoints(g, tx, ty, w2, h2);
        } else
        {
            //pointsShowing = false;
        }
    }
}
