package com.sri.gui.ext.backgrounds;

import java.awt.*;
import java.util.*;
import java.awt.image.*;

/**
 * This class is used to draw backgrounds on objects.
 */
public class Background extends Object
{
		/**
		 * Paints in a given rectangular area.
		 */
	public void paint(Graphics g,Component imageCreater, int x,int y,int w,int h)
	{
		g.fillRect(x,y,w,h);
	}
		
		/**
		 * Fills the given polygon with the pattern.
		 */
	public void paint(Graphics g,Component imageCreater,Polygon p)
	{
		g.fillPolygon(p);
	}
}