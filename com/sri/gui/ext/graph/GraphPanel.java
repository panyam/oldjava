package com.sri.gui.ext.graph;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.ext.graph.renderer.*;

public class GraphPanel extends Container implements ComponentListener
{
		/**
		 * The x axis title.
		 */
	protected Title xTitle = new Title("X Axis");
	
		/**
		 * The y axis title.
		 */
	protected Title yTitle = new Title("Y Axis",90);
	
		/**
		 * The main title.
		 */
	protected Title mainTitle = new Title("Title");
	
		/**
		 * The graph rendering panel.
		 */
	protected GraphArea graphArea = new GraphArea();

		/**
		 * Constructor.
		 */
	public GraphPanel()
	{
		addComponentListener(this);
		setLayout(null);
		add(mainTitle);
		add(xTitle);
		add(yTitle);
		add(graphArea);
        mainTitle.setVisible(true);
        xTitle.setVisible(true);
        yTitle.setVisible(true);
        graphArea.setVisible(true);
	}

        /**
         * Gets the component that actually holds the graph.
         */
    public GraphArea getGraphArea()
    {
        return graphArea;
    }
        /**
         * Lays out the components for the graph.
         */
    public void doLayout()
    {
        System.out.println("Laying graph...");
        Dimension d = getSize();
        Dimension xSize = xTitle.getPreferredSize();
        Dimension ySize = yTitle.getPreferredSize();
        Dimension mSize = mainTitle.getPreferredSize();
        mainTitle.setBounds((d.width - mSize.width) / 2, 2,
                            mSize.width, mSize.height);
        xTitle.setBounds((d.width - xSize.width) / 2,
                            d.height - xSize.height - 2,
                            xSize.width, xSize.height);
        yTitle.setBounds(2, (d.height - ySize.height) / 2,
                            ySize.width, ySize.height);
        graphArea.setBounds(ySize.width + 2, mSize.height + 5,
                            d.width - ySize.width - 2,
                            d.height - xSize.height - mSize.height - 5);
    }

	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e)
	{
		//doLayout();
		/*notifyAll();
		if (e.getSource() == this)
		{
			try 
			{
				this.wait(1000);
				paint(getGraphics());
			} catch (InterruptedException ex)
			{
			}
			return ;
		}*/
	}
}
