package com.sri.gui.core;

import java.awt.image.*;
import java.awt.*;

public class ImageMap extends Component
{
	protected Image currImage = null;
	protected static ColorModel dcm = new DirectColorModel(32,0xff << 16,0xff << 8, 0xff << 0);
	
		/**
		 * Constructor.
		 */
	public ImageMap(Image im)
	{
		setImage(im);
	}
	
	public void setImage(Image im) {
		currImage = im;
	}
	
		/**
		 * Returns the existing image.
		 */
	public Image getImage()
	{
		return currImage;
	}
	
		/**
		 * Returns parts of the image.
		 */
	public Image getImage(int x,int y,int w,int h) 
	{
		if (currImage == null) return null;
		
		int array[] = new int [1 + w * h];	// the array for the image...
		PixelGrabber pix = new PixelGrabber(currImage,x,y,w,h,array,0,w);
		try
		{
			pix.grabPixels();
		} catch (InterruptedException e) {
		}
		return createImage(new MemoryImageSource(w,h,dcm,array,0,w));
	}
	
		/**
		 * Returns parts of the image.
		 */
	public Image getImage(Rectangle rect)
	{
        return getImage(rect.x, rect.y, rect.width, rect.height);
	}
}
