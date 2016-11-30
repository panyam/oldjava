package com.sri.gui.ext.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class ColorSelector extends Canvas implements 
											MouseListener, 
											MouseMotionListener
{
	protected static ColorModel dcm = new DirectColorModel(32,0xff << 16,0xff << 8, 0xff << 0);
	public final static int RED = 0;
	public final static int GREEN = 1;
	public final static int BLUE = 2;
	
	public final static int HUE = 3;
	public final static int SATURATION = 4;
	public final static int LUMINANCE = 5;
	
	protected Image twoD = null;
	protected Image oneD = null;
	protected Image buffer = null;
	protected Graphics bg = null;
	protected Dimension bs = new Dimension();
	
	private int xAxisType = RED;
	private int yAxisType = BLUE;
	private int zAxisType = GREEN;
	
	protected float currX = (float)0.5;
	protected float currY = (float)0.5;
	protected float currZ = (float)0.5;
	
	protected boolean usingRGB = true;
	
	private static final int NO_WHERE = -1;
	private static final int ON_1D_BAR = 0;
	private static final int ON_2D_BAR = 1;
	
	protected int pressedIn = NO_WHERE;
	
	Color curr = Color.black;
	public boolean useRGB = false;
	
	protected Rectangle twoDarea = new Rectangle(-1,-1,-1,-1);
	protected Rectangle oneDarea = new Rectangle(-1,-1,-1,-1);

		/**
		 * Constructor.
		 * 
		 * @param	axes	Tells what should be in each of the axis.
		 */
	public ColorSelector(int axes[])
	{
		setAxesTypes(axes);
		//setBackground(Color.lightGray);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
		/**
		 * Sets the color values...
		 */
	public void setColor(int r,int g,int b)
	{
		curr = null;
		curr = new Color(r,g,b);
		double f = 1.0 / 255.0;
		
		currX = (float)(r * f);
		currY = (float)(g * f);
		currZ = (float)(b * f);
		paint(getGraphics());
	}
	
		/**
		 * Sets the HSB values.
		 */
	public void setHSB(double h,double s,double b)
	{
		curr = null;
		curr = Color.getHSBColor((float)h,(float)s,(float)b);
		paint(getGraphics());
	}

	private void setColorToPointers()
	{
		curr = null;
		if (useRGB)
		{
			int xs = 16 - (xAxisType * 8);
			int ys = 16 - (yAxisType * 8);
			int zs = 16 - (zAxisType * 8);
			curr = new Color(0xff << 24 | 
							 ((int)(currX * 255)) << xs |
							 ((int)(currY * 255)) << ys |
							 ((int)(currZ * 255)) << zs);
		} else
		{
			curr = Color.getHSBColor((float)currX,(float)currY,(float)currZ);
		}
		paint(getGraphics());
	}
	
		/**
		 * Returns the current color.
		 */
	public Color getColor()
	{
		return curr;
	}
	
		/**
		 * Draws a pointer arrow at the given location.
		 * @param dir tells which direction the pointer is 
		 * to be facing.
		 *	if dir = 0 then face up
		 *	if dir = 1 then face down
		 *	if dir = 2 then face left
		 *	if dir = 3 then face right
		 */
	private void drawPointer(Graphics g,int x,int y,int w,int h,int dir)
	{
		Polygon p = new Polygon();
		int points[][][] = {
			{{x,y + h}, {x + w,y + h}, {x + w / 2,y}, {x,y + h}},
			{{x,y}, {x + w,y}, {x + w / 2,y + h}, {x,y}},
			{{x,y + h / 2}, {x + w,y}, {x + w,y + h}, {x,y + h / 2}},
			{{x,y}, {x + w,y + h / 2}, {x,y + h}, {x,y}},
		};
		
		for (int i = 0;i < 4;i++)
		{
			p.addPoint(points[dir][i][0],points[dir][i][1]);
		}
		g.fillPolygon(p);
	}

		/**
		 * Sets the axes types
		 */
	public void setAxesTypes(int axes[])
	{
		int nRGB = 0;
		int nHSB = 0;
		boolean usedUpRGB[] = { false, false, false };
		boolean usedUpHSB[] = { false, false, false };
		
		boolean error = false;
		for (int i = 0;i < 3 && !error;i++)
		{
			if (axes[i] < 3)
			{
				if (!usedUpRGB[i]) 
				{
					usedUpRGB[i] = true;
					nRGB ++;
				} else error = true;
			} else
			{
				if (!usedUpHSB[i]) 
				{
					usedUpHSB[i] = true;
					nHSB ++;
				} else error = true;
			}
		}
		
		if (nRGB == 3) useRGB = true;
		else if (nHSB == 3) useRGB = false;
		else {
			throw new IllegalArgumentException("Invalid axes types");
		}
		xAxisType = axes[0];
		yAxisType = axes[1];
		zAxisType = axes[2];
		bs.width = bs.height = 0;
		paint(getGraphics());
	}
	
		/**
		 * Creates the HSB map rectangles.
		 */
	private void createHSBMap(boolean onlyL)
	{
		int vals2[] = new int[twoDarea.width * twoDarea.height];
		int c2 = 0;
		int xshift = 16 - (xAxisType * 8);
		int yshift = 16 - (yAxisType * 8);
		int zshift = 16 - (zAxisType * 8);
		int byte3 = 0xff << 24;
		
				// delete old map images
		if (!onlyL)
		{
			if (twoD != null) twoD.flush();
			twoD = null;
		}
		if (oneD != null) oneD.flush();
		oneD = null;
		oneD = createImage(oneDarea.width,oneDarea.height);
		Graphics oneG = oneD.getGraphics();
			
		if (!onlyL)
		{
			for (int j = twoDarea.height - 1;j >= 0;j--)
			{
				double sat = j  * 1.0 / twoDarea.height;
				for (int i = 0;i < twoDarea.width;i++)
				{
					double hue = i * 1.0 / twoDarea.width;
					int valx = (i * 255) / (twoDarea.width - 1) << xshift;
					Color c = Color.getHSBColor((float)hue,(float)sat,(float)1.0);
					vals2[c2++] = (byte3 | 
								   (c.getRed() << 16) | 
								   (c.getGreen() << 8) | 
								   c.getBlue());
					c = null;
				}
			}
		}
			
		for (int j = twoDarea.height - 1,jy = 0;j >= 0;j--,jy++)
		{
			double lum = j * 1.0 / twoDarea.height;
				//and also draw the z axis one as well...
			oneG.setColor(Color.getHSBColor((float)currX,(float)currY,(float)lum));
			oneG.drawLine(0,jy,oneDarea.width,jy);
		}
		
		oneG.dispose();
		oneG = null;
		
		if (!onlyL)
		{
			twoD = createImage(
							new MemoryImageSource(
								twoDarea.width,twoDarea.height,dcm,
								vals2,0,twoDarea.width));
		}
	}
	
		/**
		 * Creates the RGB map rectangles.
		 */
	private void createRGBMap()
	{
		int vals2[] = new int[twoDarea.width * twoDarea.height];
		int c2 = 0;
		int xshift = 16 - (xAxisType * 8);
		int yshift = 16 - (yAxisType * 8);
		int zshift = 16 - (zAxisType * 8);
		int byte3 = 0xff << 24;
		
				// delete old map images
		if (twoD != null) twoD.flush();
		twoD = null;
		if (oneD == null) oneD = createImage(oneDarea.width,oneDarea.height);
		Graphics oneG = oneD.getGraphics();
			
		for (int j = twoDarea.height - 1;j >= 0;j--)
		{
			int  valy = ((j * 255) / (twoDarea.height - 1)) << yshift;
			for (int i = 0;i < twoDarea.width;i++)
			{
				int valx = (i * 255) / (twoDarea.width - 1) << xshift;
				vals2[c2++] = (byte3 | valx | valy );
			}
				//and also draw the z axis one as well...
			oneG.setColor(new Color(0xff000000 | ((valy >> yshift) << zshift)));
			oneG.drawLine(0,oneDarea.height - j,oneDarea.width,oneDarea.height - j);
		}

		oneG.dispose();
		oneG = null;
		twoD = createImage(
						new MemoryImageSource(
							twoDarea.width,twoDarea.height,dcm,
							vals2,0,twoDarea.width));
	}
	
		/**
		 * Prepares screen for offscreen buffering.
		 */
	private void prepareBuffer(Dimension d)
	{
		int ps = 10;
		if (buffer == null || d.width != bs.width || d.height != bs.height)
		{
			if (buffer != null) buffer.flush();
			buffer = null;
			
			if (bg != null) bg.dispose();
			bg = null;
			bs.width = Math.max(1,d.width);
			bs.height = Math.max(1,d.height);
			buffer = createImage(bs.width,bs.height);
			if (buffer == null) return ;
			bg = buffer.getGraphics();
			if (bg == null) return ;
			
			
				// create image boundaries...
			int ps2 = 2 * ps;
			int gap = 30;
			
			twoDarea.height = oneDarea.height = bs.height - ps2;
			twoDarea.y = oneDarea.y = ps;
			oneDarea.width = 10;
			twoDarea.x = ps2;
			oneDarea.x = bs.width - ps - oneDarea.width;
			twoDarea.width = oneDarea.x - gap - ps;
			
					// create new images...
			if (useRGB)
			{
				createRGBMap();
			} else
			{
				createHSBMap(false);
			}
		} 
		
		if (!useRGB) createHSBMap(true);
		
		bg.setColor(getBackground());
		bg.fillRect(0,0,bs.width,bs.height);
		
		bg.setColor(Color.lightGray);
		bg.draw3DRect(oneDarea.x - 1,oneDarea.y - 1,oneDarea.width + 2,oneDarea.height + 2,false);
		bg.draw3DRect(twoDarea.x - 1,twoDarea.y - 1,twoDarea.width + 2,twoDarea.height + 2,false);
		bg.drawImage(oneD,oneDarea.x,oneDarea.y,this);
		bg.drawImage(twoD,twoDarea.x,twoDarea.y,this);
		
			// finally draw the pointers...
		int x = (int)((currX * twoDarea.width) + twoDarea.x);
		int y = (int)(twoDarea.height + twoDarea.y - (currY * twoDarea.height));
		int z = (int)(oneDarea.height + oneDarea.y - (currZ * oneDarea.height));
		
		bg.setColor(Color.black);
		drawPointer(bg,x - ps / 2,2 + twoDarea.y + twoDarea.height,ps,ps - 2,0);
		drawPointer(bg,twoDarea.x - ps,y - 5,ps - 4,ps,3);
		drawPointer(bg,oneDarea.x + oneDarea.width,z - 5,ps - 2,ps,2);
		
			// and also draw a marker at the current color...
		bg.setClip(twoDarea);
		bg.fillRect(x - 1,y - 10,2,8);
		bg.fillRect(x - 10,y - 1,8,2);
		bg.fillRect(x - 1,y + 2,2,8);
		bg.fillRect(x + 3,y - 1,8,2);
		bg.setClip(0,0,bs.width,bs.height);
	}

		/**
		 * Paint method.
		 */
	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();
		prepareBuffer(d);
		if (buffer != null) g.drawImage(buffer,0,0,this);
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}
	
		// tells if mouse was pressed on 
		// the image map boxes or outside
	private int pressedAt = -1;
	public void mousePressed(MouseEvent e)
	{
		if (e.getSource() == this)
		{
			int x = e.getX();
			int y = e.getY();
			boolean changed = false;
			
			int th = oneDarea.height + oneDarea.y;
			
				// first see which rectangle was clicked
			if (twoDarea.contains(x,y))
			{
					// then x and y get changed....
				float cx = (float)((1.0 * (x - twoDarea.x)) / twoDarea.width);
				float cy = (float)((th - y) * 1.0 / oneDarea.height);
				
				if (cx < 0) cx = 0;
				if (cx > 1) cx = 1;
				currX = cx;
				
				if (cy < 0) cy = 0;
				if (cy > 1) cy = 1;
				currY = cy;
				changed = true;
				pressedAt = 1;
			} else if (oneDarea.contains(x,y))
			{
					// then y changes....
					// then x and y get changed....
				float cz = (float)((th - y) * 1.0 / oneDarea.height);
				
				if (cz < 0) cz = 0;
				if (cz > 1) cz = 1;
				currZ = cz;
				changed = true;
				pressedAt = 0;
			} else pressedAt = -1;

			if (changed)
			{
				setColorToPointers();
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e)
	{ 
		if (e.getSource() == this)
		{
			//bs.width = 0;
			int x = e.getX(), y = e.getY();
			int th = oneDarea.height + oneDarea.y;
			
			if (pressedAt == 0)
			{
					// then y changes....
					// then x and y get changed....
				float cz = (float)((th - y) * 1.0 / oneDarea.height);
				
				if (cz < 0) cz = 0;
				if (cz > 1) cz = 1;
				currZ = cz;
			} else if (pressedAt == 1)
			{
					// then x and y get changed....
				float cx = (float)((1.0 * (x - twoDarea.x)) / twoDarea.width);
				float cy = (float)((th - y) * 1.0 / oneDarea.height);
				
				if (cx < 0) cx = 0;
				if (cx > 1) cx = 1;
				currX = cx;
				
				if (cy < 0) cy = 0;
				if (cy > 1) cy = 1;
				currY = cy;
			}
			if (pressedAt >= 0) { setColorToPointers(); paint(getGraphics()); }
		}
	}
}
