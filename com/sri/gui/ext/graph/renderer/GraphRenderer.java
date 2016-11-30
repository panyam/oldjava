
package com.sri.gui.ext.graph.renderer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import com.sri.gui.ext.backgrounds.*;
import com.sri.gui.ext.graph.graphable.*;

/**
 * This components provides features to draw graphs.
 */
public interface GraphRenderer 
{
        /**
         * Tells if the rendering of the graph is finished.
         */
    public boolean renderingCompleted();

        /**
         * Does the actual painting of the graph.
         */
    public void renderGraph(Graphics g, Graphable grs[], int ngrs);

        /**
         * These functions take care of mouse handling on
         * the renderer.
         * So basically the class that takes care of the rendering
         * of the graph, also takes care of input on the graph.
         */
    public boolean mouseClicked(MouseEvent e);
    public boolean mouseReleased(MouseEvent e);
    public boolean mousePressed(MouseEvent e);
    public boolean mouseEntered(MouseEvent e);
    public boolean mouseExited(MouseEvent e);
    public boolean mouseMoved(MouseEvent e);
    public boolean mouseDragged(MouseEvent e);
}
