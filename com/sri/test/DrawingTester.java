package com.sri.test;

import java.awt.*;
import com.sri.gui.ext.*;
import com.sri.gui.core.button.*;
import com.sri.gui.ext.drawing.*;
import com.sri.gui.ext.drawing.stencils.*;
import com.sri.apps.netsim.*;
import com.sri.apps.ovo.*;
import java.awt.image.*;

public class DrawingTester extends TesterFrame 
{
    SceneViewer sceneViewer = new SceneViewer();
    DrawingPanel dPanel = new DrawingPanel(sceneViewer);
    DrawingToolbar dToolbar = new DrawingToolbar(sceneViewer);
    //PopupManager popupManager = new NetworkPopupManager(sceneViewer);
    PopupManager popupManager = new OVOPopupManager(sceneViewer);

    SActionButton buttons[] = 
    {
        new SActionButton("Basic Shapes"),
        new SActionButton("Network Shapes"),
        new SActionButton("Transform"),
        new SActionButton("Auto Shapes"),
    };

    Stencil stencils[] =
    {
        new BasicShapesStencil(sceneViewer, dToolbar),
        new NetworkStencil(sceneViewer, dToolbar, Toolkit.getDefaultToolkit().getImage("images/network_icons.gif")),
        new OVOStencil(sceneViewer, dToolbar, Toolkit.getDefaultToolkit().getImage("images/ovo_icons.gif")),
        new Stencil(sceneViewer, dToolbar),
    };

        /**
         * Constructor.
         */
	public DrawingTester()
	{
		super("Drawing Panel");
		setLayout(new BorderLayout());

        Dialog toolDialog = new Dialog(this, "Toolbar...", false);
        dPanel.setToolbar(dToolbar);

        toolDialog.setLayout(new BorderLayout());
        toolDialog.add("Center", dToolbar);
        toolDialog.setSize(310,285);
        toolDialog.setVisible(true);
		toolDialog.toFront();
		
        sceneViewer.setPopupManager(popupManager);
        configureButtons();

            // now we add the stencils...
        for (int i = 0;i < stencils.length;i++)
        {
            dToolbar.addStencil(buttons[i], stencils[i]);
        }

		add("Center",dPanel);
		pack();
		setVisible(true);
		setBounds(150,150,500,400);
		toFront();
	}

        /**
         * Configures the buttons to be displayed.
         */
    private void configureButtons()
    {
        int wh = 30;
        Image im[] = new Image[buttons.length];
        int ind[] = new int[wh*wh];
        for (int i = 10;i < 20;i++)
        {
            ind[i * wh + i] = 0xff000000;
        }
        for (int i = 0;i < buttons.length;i++)
        {
            im[i] = createImage(DrawingResource.createMainIconImage(i));
            buttons[i].setBackground(Color.lightGray);
            buttons[i].showText(true);
            buttons[i].setFont(new Font("Serif",Font.ITALIC + Font.BOLD, 12));
            buttons[i].showFocus(false);
            buttons[i].setRollOver(true);
            buttons[i].showBorder(true);
            buttons[i].setContentAllignment(SButton.CENTER);
            buttons[i].setTextPosition(SButton.EAST);
            //buttons[i].addMouseListener(this);
        }

            // initially all images are transparent...
        for (int i = 0;i < im.length;i++)
        {
            buttons[i].setMouseOutImage(im[i]);
            buttons[i].setMousePressedImage(im[i]);
            buttons[i].setMouseOverImage(im[i]);
        }
    }
}

/**
 * This class defines objects necessary to create 
 * images for the drawing toolbar.
 */
class DrawingResource
{
	private static Buffer buffers[] = new Buffer[4];
	private final static int ICON_SIZE = 20;

	static		// create the main button icons...
	{
		for (int i = 0;i < buffers.length;i++)
		{
			buffers[i] = new Buffer(ICON_SIZE,ICON_SIZE);
		}
			// draw the basic shapes icon
		buffers[0].setColor(Color.black);
		buffers[0].drawLine(3,6,16,3);
		buffers[0].setColor(Color.red);
		buffers[0].fillRect(2,8,6,10);
		buffers[0].setColor(Color.blue);
		buffers[0].fillOval(10,5,9,13);
			// draw the transform icon
		buffers[1].setColor(Color.black);
		buffers[1].drawRect(2,2,7,5);
		
			// draw the first arrow...
		buffers[1].drawLine(12,2,14,2);
		buffers[1].drawLine(15,3,16,4);
		buffers[1].drawLine(16,4,16,7);
		buffers[1].drawLine(16,7,14,5);
		buffers[1].drawLine(16,7,18,5);
		buffers[1].drawRect(11,9,9,3);
		
			// draw the second arrow...
		buffers[1].drawLine(17,14,17,16);
		buffers[1].drawLine(16,17,13,17);
		buffers[1].drawLine(13,17,15,15);
		buffers[1].drawLine(13,17,15,19);
		
		buffers[1].drawLine(4,10,10,16);
		buffers[1].drawLine(10,16,7,19);
		buffers[1].drawLine(7,19,1,13);
		buffers[1].drawLine(1,13,4,10);
	
			// draw the autoshapes shapes icon
		buffers[2].setColor(Color.black);
		buffers[2].drawLine(1,3,1,5);
		buffers[2].drawLine(1,5,5,5);
		buffers[2].drawLine(5,5,5,7);
		buffers[2].drawLine(5,7,8,4);
		buffers[2].drawLine(8,4,5,1);
		buffers[2].drawLine(5,1,5,3);
		buffers[2].drawLine(5,3,1,3);
		
		buffers[2].setColor(Color.red);
		buffers[2].drawLine(3,10,5,10);
		buffers[2].drawLine(5,10,5,12);
		buffers[2].drawLine(5,12,7,12);
		buffers[2].drawLine(7,12,7,14);
		buffers[2].drawLine(7,14,5,14);
		buffers[2].drawLine(5,14,5,16);
		buffers[2].drawLine(5,16,3,16);
		buffers[2].drawLine(3,16,3,14);
		buffers[2].drawLine(3,14,1,14);
		buffers[2].drawLine(1,14,1,12);
		buffers[2].drawLine(1,12,3,12);
		buffers[2].drawLine(3,12,3,10);
		
		buffers[2].setColor(Color.blue);
		buffers[2].drawLine(11,4,14,1);
		buffers[2].drawLine(11,4,14,7);
		buffers[2].drawLine(14,7,14,5);
		buffers[2].drawLine(14,1,14,3);
		buffers[2].drawLine(14,5,18,5);
		buffers[2].drawLine(14,3,18,3);
		buffers[2].drawLine(18,5,18,3);
		
		buffers[2].setColor(Color.black);
		buffers[2].drawLine(13,10,13,13);
		buffers[2].drawLine(13,13,10,13);
		buffers[2].drawLine(9,14,10,15);
		buffers[2].drawLine(10,15,13,15);
		buffers[2].drawLine(13,15,13,18);
		buffers[2].drawLine(14,19,15,18);
		buffers[2].drawLine(15,18,15,15);
		buffers[2].drawLine(15,15,18,15);
		buffers[2].drawLine(19,14,18,13);
		buffers[2].drawLine(18,13,15,13);
		buffers[2].drawLine(15,13,15,10);
		buffers[2].drawLine(14,9,15,10);

            // draw the network shapes Icon...
	}
	
	public static MemoryImageSource createMainIconImage(int which)
	{
		return buffers[which].getMemoryImageSource();
	}
	
	public static void drawLine(Graphics g,int x1,int y1,int x2,int y2,int thickness)
	{
		if (thickness <= 1)
		{
			g.drawLine(x1,y1,x2,y2);
		}
	}
}
