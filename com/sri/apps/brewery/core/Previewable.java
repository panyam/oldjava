
package com.sri.apps.brewery.core;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

/**
 * For any object that has a "preview" representation/
 */
public interface Previewable
{
        /**
         * Get the size of the preview.
         */
    public void getPreviewSize(Dimension size);

        /**
         * Get the preview image.
         */
    public Image getPreviewImage();
}
