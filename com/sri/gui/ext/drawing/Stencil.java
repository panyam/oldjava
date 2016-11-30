
package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.core.*;
import com.sri.gui.core.button.*;
import com.sri.gui.ext.*;

	/**
	 * A stencil that contains info on what needs to be shown.
	 */
public class Stencil extends Panel implements MouseListener, MouseMotionListener, ComponentListener
{
        /**
         * The scene viewer object that this stencil could
         * access and manipulate.
         */
    protected SceneViewer sceneViewer = null;

        /**
         * The info listener object.
         */
    protected InfoListener infoListener = null;

        /**
         * Constructor.
         */
    public Stencil(SceneViewer sceneViewer, InfoListener il)
    {
        this.sceneViewer = sceneViewer;
        this.infoListener = il;
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
    }

        /**
         * Mouse Moved Event handler.
         */
    public void mouseMoved(MouseEvent me) { }

        /**
         * Mouse Dragged Event handler.
         */
    public void mouseDragged(MouseEvent me) { }

        /**
         * Mouse Pressed Event handler.
         */
    public void mousePressed(MouseEvent me) { }

        /**
         * Mouse Released Event handler.
         */
    public void mouseReleased(MouseEvent me) { }

        /**
         * Mouse Clicked Event handler.
         */
    public void mouseClicked(MouseEvent me) { }

        /**
         * Mouse Entered Event handler.
         */
    public void mouseEntered(MouseEvent me) { }

        /**
         * Mouse Exited Event handler.
         */
    public void mouseExited(MouseEvent me) { }

        /**
         * Component Resized Event Handler.
         */
    public void componentResized(ComponentEvent e) { }

        /**
         * Component Hidden Event Handler.
         */
    public void componentHidden(ComponentEvent e) { }

        /**
         * Component Moved Event Handler.
         */
    public void componentMoved(ComponentEvent e) { }

        /**
         * Component Shown Event Handler.
         */
    public void componentShown(ComponentEvent e) { }
}
