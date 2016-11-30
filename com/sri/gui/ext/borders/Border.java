package com.sri.gui.ext.borders;

import java.awt.*;

/**
 * This class is used to draw the borders for 
 * various objects.
 */
public class Border
{
	public final static Border defaultBorder = new Border();

		/**
		 * Paints the actuall border.
		 */
	public void paint(Graphics g,Polygon p)
	{
		g.drawPolygon(p);
	}
	
		/**
		 * Draws a polygon given the xpoints and the ypoints.
		 */
	public void paint(Graphics g,int xp[], int yp[], int np)
	{
		g.drawPolygon(xp, yp, np);
	}
	
		/**
		 * Draws a line from and to the given points
		 * based on the current style.
		 */
	public void drawLine(Graphics g,int x1,int y1,int x2,int y2)
	{
		g.drawLine(x1,y1,x2,y2);
	}
	
		/**
		 * Draws a rectangle.
		 */
	public void drawRect(Graphics g,int x,int y,int w,int h)
	{
		g.drawRect(x,y,w,h);
	}
	
		/**
		 * Draws an oval.
		 */
	public void drawOval(Graphics g,int x,int y,int w,int h)
	{
		g.drawOval(x,y,w,h);
	}
	
		/**
		 * Draws an arc.
		 */
	public void drawArc(Graphics g,int x,int y,int w,int h,int sa,int aa)
	{
		g.drawArc(x,y,w,h,sa,aa);
	}
	
		/**
		 * Draws a rounded rectangle.
		 */
	public void drawRoundRect(Graphics g,int x,int y,int w,int h,int arcLength)
	{
		g.drawRoundRect(x,y,w,h,arcLength,arcLength);
	}
}
