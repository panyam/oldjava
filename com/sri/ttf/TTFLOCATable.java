package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This table stores the offsets to the locations of the 
 * glyph in the font, relative to the beginning of the 
 * glyph date table.  In order to compute the length of the 
 * last glyph element, there is an extra entry after the
 * last valid index.  This table comes in 2 versions, 
 * short and long.  Which version is used depends on
 * the indexToLocFormat specified in the head table.
 * It also depends on the maxp table to tell 
 * how many glyphs are stored in this table...
 * 
 * The concern is what if the loca table is specified
 * BEFORE the head table.  We can always store a flag 
 * indicating knowing which one needs to be read.
 */
public class TTFLOCATable extends TTFTable
{
	boolean isShortVersion = false;
	
	int numGlyphs;
	
	int offsets[];
	
        /**
         * Constructor.
         */
    public TTFLOCATable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		isShortVersion = parent.headTable.indexToLocFormat == 0;
		this.numGlyphs = parent.maxpTable.numGlyphs + 1;
		offsets = new int[numGlyphs];
		if (isShortVersion)
		{
			for (int i = 0;i < numGlyphs;i++)
			{
				offsets[i] = din.readShort() & 0xffff;
			}
			return numGlyphs * 2;
		} else
		{
			for (int i = 0;i < numGlyphs;i++)
			{
				offsets[i] = din.readInt();
			}
			return numGlyphs * 4;
		}
    }
}
