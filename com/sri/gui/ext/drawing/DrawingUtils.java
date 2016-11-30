package com.sri.gui.ext.drawing;

import java.awt.*;

public class DrawingUtils
{
		/**
		 * Solid line style.
		 */
	public final static int SOLID = 0;
		/**
		 * Round dot style.
		 */
	public final static int ROUND_DOT = 1;
		/**
		 * Square dot style.
		 */
	public final static int SQUARE_DOT = 2;
		/**
		 * Dashed style.
		 */
	public final static int DASHED = 3;
		/**
		 * Dash dot style.
		 */
	public final static int DASH_DOT = 4;
		/**
		 * Long dash style.
		 */
	public final static int LONG_DASH = 5;
		/**
		 * Long dash dot style.
		 */
	public final static int LONG_DASH_DOT = 6;
		/**
		 * Long dash dot dot style.
		 */
	public final static int LONG_DASH_DOT_DOT = 7;
	

		/**
		 * Thickness parameters.
		 */
	public final static int PT_1_4 = 0;
	public final static int PT_1_2 = 1;
	public final static int PT_3_4 = 2;
	public final static int PT_1 = 3;
	public final static int PT_1_1_2 = 4;
	public final static int PT_2_1_4 = 5;
	public final static int PT_3 = 6;
	public final static int PT_4_1_2 = 7;
	public final static int PT_6 = 8;
	
		/**
		 * Draws a line with a given thickness and style.
		 */
	public static void drawLine(Graphics g,int x1,int y1,
								int x2,int y2,int thickness,int style)
	{
		boolean infGrad = (x1 == x2);
		double grad = (infGrad ? 0 : (y2 - y1) * 1.0 / (x2 - x1));
		double theta = (infGrad ? (Math.PI / 2.0) : Math.atan(grad));
		if (infGrad && y2 < y1) theta *= 3;
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		
		if (true)		// make true to style == SOLID
		{
			int half = thickness >> 1;	// half the thickness..
			int xdiff = (int)(half * costheta);
			int ydiff = (int)(half * sintheta);
			int sx = x1 - xdiff;
			for (int i = 0;i < thickness;i++) 
			{
				g.drawLine(x1 - xdiff + i,y1 + ydiff - i,
						   x2 - xdiff + i,y2 + ydiff - i);
			}
		}
	}
	
		/**
		 * Draws a rectangle with a given thickness and style.
		 */
	public static void drawRect(Graphics g,int x,int y,
								int w,int h,int thickness,int style)
	{
		drawLine(g,x,y,x + w,y,thickness,style);
		drawLine(g,x,y,x,y + h,thickness,style);
		drawLine(g,x + w,y,x + w,y + h,thickness,style);
		drawLine(g,x,y + h,x + w,y + h,thickness,style);
	}
}