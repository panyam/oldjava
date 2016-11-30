package com.sri.gui.ext.drawing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

    /**
     * A class for holding the information regarding a scene.
     * A scene contains a collection of SceneElements .
     */
public class SceneViewer  extends Container
                    implements
                            MouseListener,
                            MouseMotionListener,
                            AdjustmentListener,
                            ComponentListener
{
        /**
         * The popup manager for this scene viewer.
         */
    protected PopupManager popupManager = null;

        /**
         * The x and y offset from where we are supposed to
         * draw.  Affected when we have scrollbars.
         */
    protected int xOffset = 0, yOffset = 0;

        /**
         * Only used when adding links.
         */
    protected Shape startingShape = null,
                    endingShape = null;

    protected static Color GRID_COLOR = Color.lightGray;

        /**
         * number of pixels per grid.
         */
    protected int gridWidth = 20;

        /**
         * Tells if the grid should be shown or not.
         */
    protected boolean showGrid = true;

        /**
         * The location at which the mouse was
         * pressed.
         */
    protected int pressedX = -1, pressedY = -1;

        /**
         * The current location of the mouse.
         */
    protected int currentX = -1, currentY = -1;

        /**
         * This is the drawable object that is
         * added each time you press and drag while
         * the mouse is in ELEMENT_CREATION_MODE mode.
         * So after the mouse ahs been released,
         * this object is cloned and a new instance
         * is created.  We could have used the Class
         * object to hold the type of object that
         * is to be added.  However, say if we have
         * an autoshape object, that has differnt views
         * for the same class, this cannot be differentiated
         */
    protected SceneElement currElement = null;

    protected final static int NORMAL_MODE  = 0;
    protected final static int ELEMENT_CREATION_MODE = 1;
    protected final static int ELEMENT_TRANSFORMATION_MODE = 2;
    protected final static int RUBBER_BANDING_MODE  = 3;

    protected int mouseMode = NORMAL_MODE;

        /**
         * The preferred size.
         */
    protected Dimension prefSize = null;

        /**
         * Offscreen image buffImage.
         */
    protected Image buffImage = null;

        /**
         * Graphics object of the buffImage.
         */
    protected Graphics buffGraphics = null;

        /**
         * Buffer's size.
         */
    protected Dimension buffSize = new Dimension();

        /**
         * The buffer could actually be bigger than the view,
         * in which case we just wanna clip the view.
         */
    protected Dimension viewSize = new Dimension();

        /**
         * Collection of ourScene.
         */
    public Scene ourScene = new Scene();

        /**
         * The vertical scrollbar.
         */
    protected Scrollbar vScroll = new Scrollbar(Scrollbar.VERTICAL);

        /**
         * The horizontal scrollbar.
         */
    protected Scrollbar hScroll = new Scrollbar(Scrollbar.HORIZONTAL);

        /**
         * Tells if the Network data can be edited.
         */
    protected boolean isEditable = true;

        /**
         * Default Constructor.
         */
    public SceneViewer()
    {
        this(new Scene());
    }

        /**
         * Constructor.
         */
    public SceneViewer(Scene scene)
    {
        this.ourScene = scene;
        prefSize = new Dimension(640,480);
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        hScroll.addAdjustmentListener(this);
        vScroll.addAdjustmentListener(this);
        setBackground(Color.white);
        setForeground(Color.black);
        setLayout(null);
        add(vScroll);
        add(hScroll);
        vScroll.setMinimum(0);
        hScroll.setMinimum(0);
        vScroll.setMaximum(100);
        hScroll.setMaximum(100);
    }

        /**
         * Adds a new drawable object to the scene.
         */
    public void addElement(SceneElement element, boolean creating)
    {
        if (currElement != null) currElement.removeFocus();
        currElement = element;
        if (creating)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            currElement.setCreating(true);
            setMode(ELEMENT_CREATION_MODE);
        } else
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            ourScene.addElement(element);
            setMode(NORMAL_MODE);
        }
    }

        /**
         * Returns the drawable object at the given coordinate.
         */
    public SceneElement getElementAt(int x,int y)
    {
        x -= xOffset; y -= yOffset;
        for (int i = ourScene.nShapes - 1;i >= 0;i--) {
            Shape sh = ourScene.shapes[i];
            if (sh.contains(x,y)) return sh;
        }

        for (int i = 0, len = ourScene.nConnectors;i < len;i++)
        {
            Connector c = ourScene.connectors[i];
            if (c.contains(x, y))
            {
                return c;
            }
        }
        return null;
    }

        /**
         * Sets the popup manager
         */
    public void setPopupManager(PopupManager pMan)
    {
        this.popupManager = pMan;
    }

        /**
         * Brings the specified shape to
         * the front of all ourScene.
         */
    public void bringToFront(Shape d)
    {
        System.out.println("\"Bring to front\" To be implemented...");
    }

        /**
         * Sends the specified shape behind all
         * other ourScene.
         */
    public void sendToBack(Shape d)
    {
        System.out.println("\"Send to back\" To be implemented...");
    }

        /**
         * Delete a given shape.
         */
    public synchronized void deleteShape(Shape sh)
    {
        ourScene.removeShape(sh);
        repaint();
    }

        /**
         * Returns the preferred size.
         */
    public Dimension getPreferredSize() { return prefSize; }

        /**
         * Paint method.
         */
    public synchronized void paint(Graphics g)
    {
        Dimension d = getSize();
        Dimension vs = vScroll.getSize();
        Dimension hs = hScroll.getSize();

        viewSize.width = d.width;
        viewSize.height = d.height;
        if (buffImage == null ||
                buffSize.width < viewSize.width || buffSize.height < viewSize.height)
        {
            if (buffImage != null) buffImage.flush();
            if (buffGraphics != null) buffGraphics.dispose();
            buffSize.width = Math.max(1,viewSize.width);
            buffSize.height = Math.max(1,viewSize.height);
            buffImage = createImage(buffSize.width,buffSize.height);
            if (buffImage == null) return ;
            buffGraphics = buffImage.getGraphics();
            System.gc();
        }

        if (buffImage == null || buffGraphics == null) return ;

        updateBuffer(buffGraphics, viewSize, false);
        buffGraphics.setColor(Color.black);
        buffGraphics.drawRect(0,0,buffSize.width - 1,buffSize.height - 1);
        if (buffImage != null) g.drawImage(buffImage,0,0,this);
        //buffGraphics.fillRect(0,0,buffSize.width,buffSize.height);

                // now draw the rubber banding if it exists...
        if (mouseMode == RUBBER_BANDING_MODE)
        {
            g.setColor(Color.black);
            g.drawRect((currentX < pressedX ? currentX : pressedX),
                        (currentY < pressedY ? currentY : pressedY),
                        Math.abs(currentX - pressedX),
                        Math.abs(currentY - pressedY));
        } else if (mouseMode == ELEMENT_CREATION_MODE)
        {
            currElement.paint(g, xOffset, yOffset);
        }

    }

        /**
         * Sets the current mode.
         */
    protected synchronized void setMode(int mode)
    {
        this.mouseMode = mode;
        if (mouseMode == ELEMENT_CREATION_MODE)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

        /**
         * Adjustment event hadnler.
         */
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        if (e.getSource() == vScroll)
        {
            yOffset = -vScroll.getValue();
            updateBuffer(buffGraphics, viewSize, true);
        } else if (e.getSource() == hScroll)
        {
            xOffset = -hScroll.getValue();
            updateBuffer(buffGraphics, viewSize, true);
        }
    }

        /**
         * Lays out all the stuff.
         */
    public void doLayout()
    {
        Dimension d = getSize();

        Dimension vs = vScroll.getPreferredSize();
        Dimension hs = hScroll.getPreferredSize();

        vScroll.setBounds(d.width - vs.width, 0, vs.width,
                          hScroll.isShowing() ? d.height - hs.height :
                                                d.height);
        hScroll.setBounds(0, d.height - hs.height,
                          vScroll.isShowing() ? d.width - vs.width :
                                                d.width, hs.height);
    }

        /**
         * Component moved event handler.
         */
    public void componentMoved(ComponentEvent e) { }

        /**
         * Component hidden event handler.
         */
    public void componentHidden(ComponentEvent e) { }

        /**
         * Component shown event handler.
         */
    public void componentShown(ComponentEvent e)
    {
    }

        /**
         * Component Resized event handler.
         */
    public void componentResized(ComponentEvent e)
    {
        prefSize.width = prefSize.height = -1;
        updateScrollBars();
        //paint(getGraphics());
    }

        /**
         * Mouse pressed event handler.
         */
    public void mousePressed(MouseEvent e)
    {
        if (e.getSource() != this || !isEditable) return ;

            // see where the mouse was pressed
        currentX = pressedX = e.getX();
        currentY = pressedY = e.getY();

        if (e.isMetaDown() && popupManager != null)
        {
            setMode(NORMAL_MODE);
            popupManager.handlePopup(e,
                                     currentX - xOffset,
                                     currentY - yOffset,
                                     this);
        }
            
            // we can be in the following modes:
            // Normal Mode: 
            //      check if pressed on a element or not... 
            //      If pressed on an element then we can go to 
            //      transformation mode or into rubberbanding mode
            // Creation Mode
            //      Then get the current element to act on the mousePressed
            //      event and ask it if we should still be in Creation mode
            //      or not.

        if (currElement != null) currElement.removeFocus();

        if (mouseMode == NORMAL_MODE)
        {
            currElement = getElementAt(pressedX - xOffset,pressedY - yOffset);

            if (currElement != null)
            {
                currElement.mousePressed(e,
                                         pressedX - xOffset,
                                         pressedY - yOffset,
                                         this);
                if (currElement.isTransforming())
                {
                    setMode(ELEMENT_TRANSFORMATION_MODE);
                } else
                {
                    setMode(NORMAL_MODE);
                }
            } else
            {
                currElement = null;
                setMode(RUBBER_BANDING_MODE);
            }
        } else if (mouseMode == ELEMENT_CREATION_MODE)
        {
                // our new mode depends on what is returned by
                // processMouseEvent...  
                // TODO: For now if processMouseEvent returns 0 it
                // means we are still in creation mode
                // other wise we go to NORMAL_MODE
            currElement.mousePressed(e,
                                     pressedX - xOffset,
                                     pressedY - yOffset,
                                     this);

            if ( ! currElement.isCreating())
            {
                setMode(NORMAL_MODE);
            }
        } else if (mouseMode == ELEMENT_TRANSFORMATION_MODE)
        {

                // our new mode depends on what is returned by
                // processMouseEvent...  
                // TODO: For now if processMouseEvent returns 0 it
                // means we are still in creation mode
                // other wise we go to NORMAL_MODE
            currElement.mousePressed(e,
                                     pressedX - xOffset,
                                     pressedY - yOffset,
                                     this);

            if ( ! currElement.isTransforming())
            {
                setMode(NORMAL_MODE);
            }
        }
        paint(getGraphics());
    }

        /**
         * Mouse moved event handler.
         */
    public void mouseMoved(MouseEvent e)
    {
        if (e.getSource() != this) return ;
        int ex = e.getX();
        int ey = e.getY();

        if (mouseMode != ELEMENT_CREATION_MODE)
        {
            SceneElement elem = getElementAt(ex - xOffset, ey - yOffset);
            setCursor(Cursor.getDefaultCursor());
            if (elem != null) elem.mouseMoved(e, ex - xOffset, ey - yOffset, this);
        } else
        {
            currElement.mouseMoved(e, ex - xOffset, ey - yOffset, this);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

        /**
         * Mouse dragged event handler.
         */
    public void mouseDragged(MouseEvent e)
    {
        if (e.getSource() != this) return ;
        currentX = e.getX();
        currentY = e.getY();

        if (mouseMode == RUBBER_BANDING_MODE)
        {
            paint(getGraphics());
        } else if (currElement != null)
        {
            currElement.mouseDragged(e,
                                     currentX - xOffset,
                                     currentY - yOffset,
                                     //pressedX, pressedY,
                                     this);
            paint(getGraphics());
        }
    }

        /**
         * Mouse released event handler.
         */
    public void mouseReleased(MouseEvent e)
    {
        if (e.getSource() != this) return ;

        if (mouseMode == RUBBER_BANDING_MODE)
        {
                // do all the rubber banding and so on...
                // TODO: do selection of shapes and so on...
            currentX = pressedX = -1;
            currentY = pressedY = -1;
            paint(getGraphics());
            setMode(NORMAL_MODE);
            setCursor(Cursor.getDefaultCursor());
            return ;
        } else if (mouseMode == ELEMENT_CREATION_MODE)
        {
            currElement.mouseReleased(e, currentX - xOffset,
                                      currentY - yOffset,
                                      //pressedX, pressedY,
                                      this);

                // our new mode depends on what is returned by
                // processMouseEvent...  
                // TODO: For now if processMouseEvent returns 0 it
                // means we are still in creation mode
                // other wise we go to NORMAL_MODE
            if (!currElement.isCreating())
            {
                setMode(NORMAL_MODE);
                    // and we can add the element
                    // to our scene... as the
                    // object has finished being
                    // created...
                ourScene.addElement(currElement);
                    // should currElement be set to null?
                    // as in we still wanna show the highlights.
                // currElement = null;
                setCursor(Cursor.getDefaultCursor());
                currentX = pressedX = -1;
                currentY = pressedY = -1;
            }

            paint(getGraphics());
            return ;
        } else if (mouseMode == ELEMENT_TRANSFORMATION_MODE)
        {
            currElement.mouseReleased(e, currentX - xOffset,
                                      currentY - yOffset,
                                      //pressedX, pressedY,
                                      this);
                // our new mode depends on what is returned by
                // processMouseEvent...  
                // TODO: For now if processMouseEvent returns 0 it
                // means we are still in creation mode
                // other wise we go to NORMAL_MODE
            if (!currElement.isTransforming())
            {
                setMode(NORMAL_MODE);
                    // should currElement be set to null?
                    // as in we still wanna show the highlights.
                // currElement = null;
                setCursor(Cursor.getDefaultCursor());
                currentX = pressedX = -1;
                currentY = pressedY = -1;
            }
            paint(getGraphics());
            return ;
        }
        setCursor(Cursor.getDefaultCursor());
    }

        /**
         * Mouse clicked event handler.
         */
    public void mouseClicked(MouseEvent e) { }

        /**
         * Mouse entered event handler.
         */
    public void mouseEntered(MouseEvent e) { }

        /**
         * Mouse exited event handler.
         */
    public void mouseExited(MouseEvent e) { }

        /**
         * Updates the view.
         */
    public void update(Graphics g){ }

        /**
         * update the scroll bars.
         */
    protected void updateScrollBars()
    {
        xOffset = 0;
        yOffset = 0;

                // also update the values of the maximum values...
        int maxX = 0;
        int maxY = 0;
        for (int i = 0;i < ourScene.nShapes;i++)
        {
            Shape currShape = ourScene.shapes[i];
            maxX = Math.max(maxX, currShape.x + currShape.width);
            maxY = Math.max(maxY, currShape.y + currShape.height);
        }
        hScroll.setMaximum(100 + maxX - buffSize.width);
        vScroll.setMaximum(100 + maxY - buffSize.height);

        hScroll.setBlockIncrement(hScroll.getMaximum() / 5);
        vScroll.setBlockIncrement(vScroll.getMaximum() / 5);

        paint(getGraphics());
    }

        /**
         * This method is called when the background image
         * needs updating...  Not simply for a redraw...
         *
         * So this should be called when we do the following:
         *      -   Change x and y offsets
         *      -   Release mouse after dragging
         */
    protected void updateBuffer(Graphics gObj, Dimension gSize, boolean paint)
    {
		if (gObj == null) return ;
											 
            // we dont modify the background image at all in
            // this routine...  we could if we wanted to...
        gObj.setColor(getBackground());
        gObj.fillRect(0, 0, gSize.width, gSize.height);
        gObj.setColor(getForeground());

            // first draw the grids if we need to...
        if (showGrid)
        {
            gObj.setColor(GRID_COLOR);
            for (int i = xOffset;i < gSize.width;i += gridWidth)
            {
                gObj.drawLine(i, 0, i, gSize.height);
            }

            for (int i = yOffset;i < gSize.height;i += gridWidth)
            {
                gObj.drawLine(0, i, gSize.width, i);
            }
        }

            // draw highlight of the links
        if (startingShape != null)
        {
            gObj.setColor(Color.red);
            if (endingShape != null)
            {
                endingShape.paintOutline(gObj, xOffset, yOffset);

                gObj.drawLine(xOffset + startingShape.x + (startingShape.width / 2),
                              yOffset + startingShape.y + (startingShape.height/ 2),
                              xOffset + endingShape.x + (endingShape.width / 2),
                              yOffset + endingShape.y + (endingShape.height/ 2));
            } else
            {
                startingShape.paintOutline(gObj, xOffset, yOffset);

                gObj.drawLine(xOffset + startingShape.x + (startingShape.width / 2),
                              yOffset + startingShape.y + (startingShape.height/ 2),
                              currentX, currentY);
            }
        }

            // now basically draw all links and ourScene
        for (int i = 0, ls = ourScene.nConnectors;i < ls;i++)
        {
            Connector connector = ourScene.connectors[i];
            connector.paint(gObj, xOffset, yOffset);
        }

            // now basically draw all links and ourScene
        for (int i = 0, cs = ourScene.nShapes;i < cs;i++)
        {
            Shape cont = ourScene.shapes[i];
            cont.paint(gObj, xOffset, yOffset);
        }

        gObj.setColor(Color.black);
        gObj.drawRect(1, 1, gSize.width - 2, gSize.height - 2);

        if (paint) paint(getGraphics());
    }

        /**
         * Tells if teh grid is being painted or not.
         */
    public boolean isGridShowing()
    {
        return showGrid;
    }

    public void showGrid(boolean show)
    {
        showGrid = show;

        updateBuffer(buffGraphics, buffSize, true);
    }

        /**
         * Sets teh editable flag.
         */
    public void setEditable(boolean ed)
    {
        isEditable = ed;
        updateBuffer(buffGraphics, buffSize, true);
    }

        /**
         * Tells if we are editable or not.
         */
    public boolean isEditable()
    {
        return isEditable;
    }

        /**
         * Prints the network.
         */
    public void print()
    {
        try
        {
            Component temp = this;
            Container par = getParent();
            while (!(par != null && (par instanceof Frame)))
            {
                temp = par;
                par = temp.getParent();
            }
            Frame parent = (Frame)par;

            PrintJob pJob = Toolkit.getDefaultToolkit().getPrintJob(
                                parent, "Print...", null);
            Graphics currGraphics = getGraphics();

            if (pJob == null || currGraphics == null) return ;

            Font font = currGraphics.getFont();

            if (font == null)
            {
                font = new Font("Courier", Font.PLAIN, 12);
            }

            Graphics printGraphics = pJob.getGraphics();
            printGraphics.setFont(font);

            int tempX = xOffset;
            int tempY = yOffset;
            Dimension pageDim = pJob.getPageDimension();

            System.out.println("Dimensions = " + pageDim);
            System.out.println("Resolution = " + pJob.getPageResolution());

            xOffset = yOffset = 1;
            updateBuffer(printGraphics, pageDim, false);

            //paint(printGraphics);
            pJob.end();

            xOffset = tempX;
            yOffset = tempY;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

        /**
         * Clears the network and resets to an empty network.
         */
    public void clear()
    {
        ourScene.nShapes = 0;
        ourScene.nConnectors = 0;
        updateBuffer(buffGraphics, buffSize, true);
    }

        /**
         * Get the grid width.
         */
    public int getGridWidth()
    {
        return gridWidth;
    }

        /**
         * Set the grid width.
         */
    public void setGridWidth(int gw)
    {
        this.gridWidth = gw;
    }

        /**
         * Repaints.
         */
    public void repaint()
    {
        paint(getGraphics());
    }
}
