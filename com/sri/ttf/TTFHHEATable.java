package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFHHEATable extends TTFTable
{
		/**
		 * Typographic ascent.
		 */	
	int ascender;
		
		/**
		 * Typographic descent.
		 */	
	int descender;
	
		/**
		 * Typographic line gap, -ve linegaps are like 0.
		 */
	int lineGap;
		/**
		 * Maximum advance width value in hmtx table.
		 */
	int advanceWidthMax;
		/**
		 * Minimum left sidebearing value in hmtx table.
		 */
	int minLeftSideBearing;
		/**
		 * Minimum right sidebearing value in hmtx table.
		 */
	int minRightSideBearing;
	
		/**
		 * Max(lsb + (xMax - xMin));
		 */
	int xMaxExtent;
	
		
		/**
		 * Used to calculate the slope of the cursor(rise / run)
		 * 1 for vertical.
		 */
	int caretSlopeRise;
	
		/**
		 * As above, 0 for vertical.
		 */
	int caretSlopeRun;
	
		/**
		 * Some reserved items set to 0.
		 */
	short reserved1, reserved2, reserved3, reserve4, reserved5;
	
		/**
		 * Current is 0.
		 */
	int metricDataFormat;
	
		/**
		 * Number of hMetric entries in the hmtx table.  May be
		 * smaller than the total number of glyphs in the font.
		 */
	int numHMetrics;
	
        /**
         * Constructor.
         */
    public TTFHHEATable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		tableVersion = din.readInt();
		ascender = din.readShort();
		descender = din.readShort();
		lineGap = din.readShort();
		advanceWidthMax = din.readShort();
		minLeftSideBearing = din.readShort();
		minRightSideBearing = din.readShort();
		xMaxExtent = din.readShort();
		caretSlopeRise = din.readShort();
		caretSlopeRun = din.readShort();
		reserved1 = din.readShort();
		reserved2 = din.readShort();
		reserved3 = din.readShort();
		reserve4 = din.readShort();
		reserved5 = din.readShort();
		metricDataFormat = din.readShort();
		numHMetrics = din.readShort();
		return 36;
    }
}
