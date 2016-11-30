package com.sri.gui.ext.graph.graphable;

import java.awt.*;
import java.util.*;

/**
 * An interface for objects that need to be drawn or plotted.  Classes that
 * implement interface ONLY provide a value for what needs to be drawn.
 * The actual drawing is done by the Renderer object.
 */
public interface Graphable
{
		/**
		 * Tells the name that is to be displayed in the legend.
		 * This is also the name of this series.
		 */
	public String getName();
	
        /**
         * The value of this entry.
         * The value can be of any generic type.
         * However the trouble would be the
         * conversion bw types like floats and
         * Integers and Doubles and so on...
         */
    public Object getValue();
}