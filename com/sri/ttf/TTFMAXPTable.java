package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This table specifies the memory requirements for
 * this font.
 */
public class TTFMAXPTable extends TTFTable
{
		/**
		 * Number of glyphs used.
		 */
	int numGlyphs;
	
		/**
		 * Maximum points in a non composite glyph.
		 */
	int maxPoints;
	
		/**
		 * Maximum contours in a non composite glyph.
		 */
	int maxContours;
		
		/**
		 * Maximum points in a composite glyph.
		 */
	int maxCompositePoints;
	
		/**
		 * Maximum contours in a composite glpyh.
		 */
	int maxCompositeContours;
	
		/**
		 * 1 if instructions do not use the
		 * twilight zone (zo) or 2 if they do.
		 */
	int maxZones;
	
		/**
		 * Maximum twilight points.
		 */
	int maxTwilightPoints;
	
		/**
		 * Number of storage area locations.
		 */
	int maxStorage;
	
		/**
		 * Maximum function defs.
		 */
	int maxFunctionDefs;
		/**
		 * maximum IDEFS;
		 */
	int maxInstructionDefs;

		/**
		 * Maximum stack depth.
		 */
	int maxStackElements;
	
		/**
		 * Maximum byte count for glyph instructions.
		 */
	int maxSizeOfInstructions;
	
		/**
		 * Maximum number of components referenced at
		 * top level for any composite glyph.
		 */
	int maxComponentElements;
	
		/**
		 * Maximum levels of recursion; 1 for
		 * simple components.
		 */
	int maxComponentDepth;

        /**
         * Constructor.
         */
    public TTFMAXPTable(TTFFont par, String tag, 
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
		numGlyphs = din.readShort();
		this.maxPoints = din.readShort();
		this.maxContours = din.readShort();
		this.maxCompositePoints = din.readShort();
		this.maxCompositeContours  = din.readShort();
		this.maxZones = din.readShort();
		this.maxTwilightPoints = din.readShort();
		this.maxStorage = din.readShort();
		this.maxFunctionDefs = din.readShort();
		this.maxInstructionDefs = din.readShort();
		this.maxStackElements = din.readShort();
		this.maxSizeOfInstructions = din.readShort();
		this.maxComponentElements = din.readShort();
		this.maxComponentDepth = din.readShort();
		
			// 64 bytes read...
		return 32;
    }
}