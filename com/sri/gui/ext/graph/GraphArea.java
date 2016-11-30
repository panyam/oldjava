package com.sri.gui.ext.graph;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import com.sri.gui.ext.backgrounds.*;
import com.sri.gui.ext.graph.graphable.*;
import com.sri.gui.ext.graph.renderer.*;

/**
 * This components provides features to draw graphs.
 */
public class GraphArea extends Component 
                                implements 
                                    MouseListener, 
                                    MouseMotionListener, 
                                    FocusListener, 
                                    Runnable
{
        /**
         * The class that actually renders the
         * graphable objects.
         */
    protected GraphRenderer renderer = null;

        /**
         * All the graphable objects.
         */
    protected Graphable graphables[];

        /**
         * The number of graphable objects we have.
         */
    int nGraphables;

        /**
         * The painting thread.
         */
    protected Thread painter = null;
    
        /**
         * The default backgroun dobject.
         */
    protected Background background = null;
    
        /**
         * Tells if we have focus or not.
         */
    protected boolean hasFocus = false;
    
        /**
         * Off screen buffer image
         */
    protected Image buffer = null;
    
        /**
         * Buffer graphics
         */
    protected Graphics bg = null;
        
        /**
         * Buffer size.
         */
    protected Dimension bs = new Dimension();

        /**
         * Tells whether the values are to be drawn
         * or not.
         */
    protected boolean showEmptyGraph = false;
    
        /**
         * Constructor.
         */
    protected GraphArea()
    {
        addMouseListener(this);
        addMouseMotionListener(this);
        addFocusListener(this);
    }

        /**
         * Returns the name of the graph.
         */
    public String getGraphName()
    {
        return "Graph";
    }
    
        /**
         * Set the renderer for this graph.
         */
    public void setRenderer(GraphRenderer ren)
    {
        this.renderer = ren;
        refresh();
    }

        /**
         * Returns the description of the graph.
         */
    public String getGraphDescription()
    {
        return "A Graph";
    }
    
        /**
         * Adds a graphable item to the list.
         */
    public synchronized void addGraphable(Graphable gr)
    {
		if (gr != null)
		{
			if (graphables.length == nGraphables)
			{
				Graphable gr2[] = new Graphable[nGraphables + 2];
				System.arraycopy(graphables, 0, gr2, 0, nGraphables);
				graphables = gr2;
			}
			graphables[nGraphables++] = gr;
		}
    }

        /**
         * Removes a graphable item from the list.
         */
    public synchronized void removeGraphable(Graphable gr)
    {
		if (gr == null) return ;
		int ind = indexOfGraphable(gr);
		if (ind < 0) return ;
		System.arraycopy(graphables, ind + 1, graphables, ind, 
						 nGraphables - (ind + 1));
    }
    
		/**
		 * Find the index of a graphable object.
		 */
	public int indexOfGraphable(Graphable gr)
	{
		for (int i = 0;i < nGraphables;i++) 
			if (graphables[i] == gr) return i;
		return -1;
	}
	
        /**
         * Tells whether an empty graph is to be shown or not.
         */
    public void showEmptyGraph(boolean showEmpty)
    {
        if (showEmptyGraph != showEmpty)
        {
            this.showEmptyGraph = showEmpty;
            paint(getGraphics());
        }
    }
    
		/**
		 * Redraws the graph.
		 */
	public synchronized void refresh()
	{
		if (painter != null && painter.isAlive())
        {
            painter = null;
        }
        painter = new Thread(this);
        painter.start();
	}
	
        /**
         * The update method.
         */
    public void update(Graphics g) { }

        /**
         * Paint method.
         */
    public void paint(Graphics g)
    {
        if (g != null && buffer != null) g.drawImage(buffer, 0, 0,null);
    }

        /**
         * The thread run method.
         */
    public synchronized void run()
    {
        Thread currThread = Thread.currentThread();
        Dimension d = getSize();
        if (currThread == painter) prepareBuffer(d,this.hasFocus);
        while (currThread == painter && !renderer.renderingCompleted())
        {
                // paint the background!!!
            background.paint(bg,this,0,0,d.width,d.height);

            if (currThread == painter)
            {
                    // do all the painting here....
                renderer.renderGraph(bg, graphables, nGraphables);
            }
        }
        
        if (currThread == painter && hasFocus)
        {
            bg.drawRect(0,0,bs.width - 1,bs.height - 1);
        
            bg.fillRect(-2,-2,4,4);
            bg.fillRect(bs.width / 2 - 2,-2,4,4);
            bg.fillRect(bs.width -2,-2,4,4);
    
            bg.fillRect(-2,bs.height / 2 -2,4,4);
            bg.fillRect(bs.width -2,bs.height / 2 -2,4,4);
    
            bg.fillRect(-2,bs.height -2,4,4);
            bg.fillRect(bs.width / 2 - 2,bs.height -2,4,4);
            bg.fillRect(bs.width -2,bs.height -2,4,4);
        }
    
            // now paint the image...
        if (currThread == painter)
        {
            Graphics g = getGraphics();
            if (g != null) g.drawImage(buffer,0,0,this);
        }
    }
    
        /**
         * Prepares and paints offscreen buffer.
         */
    private void prepareBuffer(Dimension d, boolean hasFocus)
    {
        if (buffer == null || bs.width != d.width || bs.height != d.height)
        {
            if (buffer != null) buffer.flush();
            bs.width = d.width > 0 ? d.width : 1;
            bs.height = d.height > 0 ? d.height : 1;
            buffer = createImage(bs.width,bs.height);
            if (buffer == null) return ;
            if (bg != null) bg.dispose();
            bg = buffer.getGraphics();
        }
    }
    
        /**
         * Tells that we can recieve focus.
         */
    public boolean isFocusTraversable()
    {
        return true;
    }
    
    public void mouseMoved(MouseEvent e)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        if (renderer != null && renderer.mouseMoved(e)) return ;
    }
    public void mouseDragged(MouseEvent e)
    {
        if (renderer != null && renderer.mouseDragged(e)) return ;
    }
    public void mouseClicked(MouseEvent e)
    {
        if (renderer != null && renderer.mouseClicked(e)) return ;
    }
    public void mouseReleased(MouseEvent e)
    {
        if (renderer != null && renderer.mouseReleased(e)) return ;
    }
    public void mousePressed(MouseEvent e)
    {
        requestFocus();
        if (renderer != null && renderer.mousePressed(e)) return ;
    }
    public void mouseEntered(MouseEvent e)
    {
        if (renderer != null && renderer.mouseEntered(e)) return ;
    }
    public void mouseExited(MouseEvent e)
    {
        if (renderer != null && renderer.mouseExited(e)) return ;
    }
    
    public void focusLost(FocusEvent e)
    {
        if (e.getSource() == this)
        {
            hasFocus = false;
            paint(getGraphics());
        }
    }
    
    public void focusGained(FocusEvent e)
    {
        if (e.getSource() == this)
        {
            hasFocus = true;
            paint(getGraphics());
        }
    }
}
