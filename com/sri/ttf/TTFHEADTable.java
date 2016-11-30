package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFHEADTable extends TTFTable
{
		/**
		 * Set by font manufacturer.
		 */
	int fontRevision;
	
		/**
		 * To compute, set it to 0, 
		 * sum the entire font as ULONG and 
		 * then store 0xB1B0AFBA - sum
		 */
	int checkSumAdjustment;
	
		/**
		 * Set to 0x5F0F3Cf5
		 */
	int magicNumber;
	
		/**
		 * Bit 0 - Baseline for font at y = 0
		 * Bit 1 - left sidebearing at x = 0
		 * Bit 2 - instructions may depend on point size
		 * Bit 3 - Force PPEM to integer values for all 
		 *			internal scaler math; 
		 *			may use fractional ppem sizes if this bit is clear.
		 * Bit 4 - Instructions may alter advance width (the advance 
		 *			widths may not scale linearly)
		 * All other bits MUST be zero.
		 */
	int flags;
	
		/**
		 * Valid range : 16 - 16384
		 */
	int unitsPerEm;
	
		/**
		 * Creation date.
		 */
	long dateCreated;
		
		/**
		 * Modification date.
		 */
	long dateModified;
	
		/**
		 * Glyph bounding box
		 */
	int xMin, xMax, yMin, yMax;
	
		/**
		 * Bit 0 bold, Bit 1 italic, Bit 2-15 set to 0 and
		 * are reserved.
		 */
	short macStyle;
	
		/**
		 * Smallest readable size in pixels.
		 */
	int lowestRecPPEM;
	
		/**
		 * Font direction:
		 * 0	=>	Fully mixed directional glyphs
		 * 1	=>	Only strongly left to right
		 * 2	=>	Like 1 but als ocontains neutrals
		 * -1	=>	Only strongly right to left
		 * -2	=>	Like 2 but als ocontains neutrals
		 */
	int fontDirHint;
	
		/**
		 * 0 for int offsets and 1 for long
		 */
	int indexToLocFormat;
	
		/**
		 * 0 for current format.
		 */
	int glyphDataFormat;
        /**
         * Constructor.
         */
    public TTFHEADTable(TTFFont par, String tag, 
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
		fontRevision = din.readInt();
		checkSumAdjustment = din.readInt();
		magicNumber = din.readInt();
		
		if (magicNumber != 0x5f0f3cf5)
		{
			throw new IllegalArgumentException("Invalid magick number in header");
		}
		flags = din.readShort();
		unitsPerEm = din.readShort() & 0xffff;
		dateCreated = din.readLong();
		dateModified = din.readLong();
		xMin = din.readShort() & 0xffff;
		yMin = din.readShort() & 0xffff;
		xMax = din.readShort() & 0xffff;
		yMax = din.readShort() & 0xffff;
		macStyle = din.readShort();
		lowestRecPPEM = din.readShort() & 0xffff;
		fontDirHint = din.readShort() & 0xffff;
		indexToLocFormat = din.readShort() & 0xffff;
		glyphDataFormat = din.readShort() & 0xffff;
		return 54;
    }
}
