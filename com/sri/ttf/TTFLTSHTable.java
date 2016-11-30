package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFLTSHTable extends TTFTable
{
	short numGlyphs;
	byte yPels[];
        /**
         * Constructor.
         */
    public TTFLTSHTable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		this.tableVersion = din.readShort();
		this.numGlyphs = din.readShort();
		this.yPels = new byte[numGlyphs];
		din.readFully(yPels);
		return 4 + numGlyphs;
    }
}
