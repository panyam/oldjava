package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.core.button.*;
import com.sri.gui.ext.*;

	/**
	 * A panel that acts as a simple painting interface.
	 */
public class DrawingPanel extends Panel implements MouseMotionListener, ComponentListener
{
	protected SceneViewer sceneViewer = null;
	protected DrawingToolbar dtoolbar;
	protected Label info = new Label();
	protected Label location = new Label("000, 000");
	protected Label zoomValue = new Label();
	protected Panel statusBar = new Panel(new BorderLayout());
	protected ColorPallette pallette = new ColorPallette(2,8,5,5);
	
        /**
         * Constructor.
         */
	public DrawingPanel()
    {
        this(new SceneViewer());
    }

        /**
         * Constructor.
         */
	public DrawingPanel(SceneViewer sViewer)
    {
		super(new BorderLayout());
        this.sceneViewer = sViewer;
		Panel midPanel = new Panel(new BorderLayout());

		statusBar.setBackground(Color.lightGray);
		//midPanel.add("South",pallette);
		//midPanel.add("Center",sceneViewer);
		
		statusBar.add("Center",info);
		statusBar.add("East",location);

		add("South",statusBar);
		add("Center",sceneViewer);
		
		//sceneViewer.setSize(320,240);
		sceneViewer.addMouseMotionListener(this);
		addComponentListener(this);
		
		//sceneViewer.addElement(new Rect(), true);
	}

		/**
		 * Sets the toolbar of this panel.
		 */
	public void setToolbar(DrawingToolbar dt)
	{
		this.dtoolbar = dt;
	}
		
		/**
		 * Sets the sceneViewer of this panel.
		 */
	public void setScene(SceneViewer s)
	{
		remove(sceneViewer);
		this.sceneViewer = s;
		add("Center",sceneViewer);
        invalidate();
	}

        /**
         * Gets the current drawing toolbar.
         */
	public DrawingToolbar getToolbar()
	{
		return this.dtoolbar;
	}
	
		/**
		 * Returns the current sceneViewer.
		 */
	public SceneViewer getSceneViewer()
	{
		return this.sceneViewer;
	}
	
		/**
		 * Sets the text of the info box.
		 */
	public void setInformation(String str)
	{
		info.setText(str);
	}

		/**
		 * Lays out the components.
		 */
	/*public void doLayout()
	{
		Dimension d = getSize();
		Insets in = getInsets();
		Dimension statusD = statusBar.getPreferredSize();
		Dimension sr = sceneViewer.getPreferredSize();
		
		if (statusBar.isVisible())
		{
			statusBar.setBounds(in.left,d.height - in.bottom - statusD.height,
								d.width - in.left - in.right,
								Math.min(d.height - in.top - in.bottom,
										 statusD.height));
		}

		if (sceneViewer.isVisible())
		{
			sceneViewer.setBounds(in.left,in.top,
                                  d.width - (in.left + in.right),
                                  d.height - (in.top + in.bottom + statusD.height));
		}
	}*/
	
		/**
		 * Sets the mouse location info box.
		 */
	public void setMouseLocation(int x,int y)
	{
		location.setText(x + ", " + y);
	}
	
	public void mouseMoved(MouseEvent e)
	{
		if (e.getSource() == sceneViewer)
		{
			location.setText(e.getX() + ", " + e.getY());
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
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
    }
}
