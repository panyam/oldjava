package com.sri.gui.ext;

import java.awt.*;
import java.awt.image.*;

public class Buffer
{
	protected boolean modified = false;
	int width, height;
	int pixels[];
	int currColor = 0xff000000;		// initialise to black
	MemoryImageSource imageSource = null;

		/**
		 * Constructor.
		 */
	public Buffer(int w,int h)
	{
		this.width = w;
		this.height = h;
		pixels = new int[w * h];
	}
	
		/**
		 * Draws a line.
		 * 
		 * This line drawing algorithm is based on the Bresenham's popular
		 * line drawing algorithm.  This code was obtained from
		 * http://graphics.lcs.mit.edu/~mcmillan/comp136/
		 */
	public void drawLine(int x0,int y0,int x1,int y1)
	{
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;

        if (dy < 0) { dy = -dy;  stepy = -width; } else { stepy = width; }
        if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
        dy <<= 1;
        dx <<= 1;

        y0 *= width;
        y1 *= width;
        pixels[x0+y0] = currColor;
        if (dx > dy) {
            int fraction = dy - (dx >> 1);
            while (x0 != x1) {
                if (fraction >= 0) {
                    y0 += stepy;
                    fraction -= dx;
                }
                x0 += stepx;
                fraction += dy;
                pixels[x0+y0] = currColor;
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (y0 != y1) {
                if (fraction >= 0) {
                    x0 += stepx;
                    fraction -= dy;
                }
                y0 += stepy;
                fraction += dx;
                pixels[x0+y0] = currColor;
            }
        }
		modified = true;
	}
	
		/**
		 * Draws a polygon
		 */
	public void drawPolygon(int x[],int y[],boolean closed)
	{
		int px = x[0];
		int py = y[0];
		int cx = px,cy = py;
		for (int i = 1;i < x.length;i++)
		{
			cx = x[i];  cy = y[i];
			drawLine(px,py,cx,cy);
			px = cx;	py = cy;
		}
		if (closed)
		{
			drawLine(cx,cy,x[0],y[0]);
		}
		modified = true;
	}
	
		/**
		 * Clears this buffer from memory.
		 */
	public void destroy()
	{
		this.pixels = null;
		this.imageSource = null;
	}
	
		/**
		 * Draws a filled polygon
		 */
	public void fillPolygon(Polygon p)
	{
		modified = true;
	}
	
		/**
		 * Draws an oval.
		 */
	public void drawOval(int x,int y,int w,int h)
	{
		modified = true;
	}
	
		/**
		 * Draws a filled oval.
		 */
	public void fillOval(int x,int y,int w,int h)
	{
		int a = w >> 1;
		int b = h >> 1;
		int cx = x + a;
		int cy = y + b;
		int a2 = a * a;
		int b2 = b * b;
		int ab2 = a2 * b2;
		
		int cc = 0xff000000 | currColor;
		
		int c = this.width * y;
		w = Math.min(w,width - x);
		h = Math.min(h,height - y);
		
		for (int i = y;i < y + h;i++)
		{
			for (int j = x;j < x + w;j++)
			{
				int currX = j - cx;
				int currY = i - cy;
				if (b2 * currX * currX + a2 * currY * currY < ab2)
				{
					pixels[j + c] = cc;
				}
			}
			c += height;
		}
		modified = true;
	}
	
		/**
		 * Draws a rectangle.
		 */
	public void drawRect(int x,int y,int w,int h)
	{
		modified = true;
		w = Math.min(w,width - x - 1);
		h = Math.min(h,height - y - 1);
		drawLine(x,y,x + w,y);
		drawLine(x + w,y,x + w,y + h);
		drawLine(x + w,y + h,x,y + h);
		drawLine(x,y,x,y + h);
	}

		/**
		 * Fills a rectangle.
		 */
	public void fillRect(int x,int y,int w,int h)
	{
		int c = this.width * y;
		System.out.println("Currcolor = " + Integer.toHexString(currColor));
		h = Math.min(h,height - y);
		w = Math.min(w,width - w);
		for (int i = 0;i < h;i++)
		{
			for (int j = 0;j < w;j++)
			{
				pixels[x + c + j] = 0xff000000 | currColor;
			}
			c += height;
		}
		modified = true;
	}
	
		/**
		 * Sets the current color.
		 */
	public void setColor(Color c)
	{
		this.currColor = c.getRGB();
	}
	
		/**
		 * Gets the current color.
		 */
	public int getRGB()
	{
		return currColor;
	}
	
		/**
		 * Draws an arc at the given location with the given width and height
		 */
	public void drawArc(int x,int y,int w,int h,float sa,float ea)
	{
		modified = true;
	}
	
		/**
		 * Draws a filled arc.
		 */
	public void fillArc(int x,int y,int w,int h,float sa,float ea)		
	{
		modified = true;
	}
	
		/**
		 * gets the image source of this buffer.
		 */
	public MemoryImageSource getMemoryImageSource()
	{
		
		if (imageSource == null)
		{
			imageSource = new MemoryImageSource(height,width,
									ColorModel.getRGBdefault(),
									pixels,0,width);
			imageSource.setAnimated(true);
		}
		return imageSource;
	}

		/**
		 * Returns the buffer's width.
		 */
	public int getWidth()
	{
		return width;
	}
	
		/**
		 * Returns the buffer's height.
		 */
	public int getHeight()
	{
		return height;
	}
	
	public void drawConic(int x0,int y0,int x1,int y1,int x2,int y2)
	{
		modified = true;
	}
	
		/**
		 * Updates the image source.
		 */
	public void update()
	{
		if (modified)
		{
			imageSource.newPixels();
		}
	}
	
		/**
		 * Tells if the image source has been modified or not.
		 */
	public boolean isModified()
	{
		return modified;
	}
	
		/**
		 * Clears the whole area of pixels and
		 * makes it completely transparent
		 */
	public void clear()
	{
		for (int i = 0, t = width * height;i < t;i++) pixels[i] = 0;
		modified = true;
	}
}