package com.sri.gui.core;

import java.awt.image.*;
/**
 * This class implements a filter to convert a RGB pixel to its grey scale
 * equivalent.  This is used in the SImageButton class, where the icon
 * images are de-colorised in order to distinguish enabled buttons from
 * disabled buttons.
 */
public class BlackAndWhiteFilter extends RGBImageFilter {
    public BlackAndWhiteFilter() {
        canFilterIndexColorModel = true;
    }

        /**
         * Convert the rgb value to its new value.
         */
    public int filterRGB(int x, int y, int rgb) {
        DirectColorModel cm = 
            (DirectColorModel)ColorModel.getRGBdefault();

        int    alpha = cm.getAlpha(rgb);
        int    red   = cm.getRed  (rgb);
        int    green = cm.getGreen(rgb);
        int    blue  = cm.getBlue (rgb);
		int    mixed = (red + green + blue) / 3;

		red   = blue = green = mixed;
        alpha = alpha << 24;
        red   = red   << 16;
        green = green << 8;

        return alpha | red | green | blue;
    }
}
