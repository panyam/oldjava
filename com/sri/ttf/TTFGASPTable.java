package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This table contains information which describes
 * the preferred rasterization techniques for the 
 * typeface when it is rendered on grayscale capable
 * devices.
 */
public class TTFGASPTable extends TTFTable
{
	public final static short GASP_DOGRAY = 0x0002;
	public final static short GASP_GRIDFIT = 0x0001;
	public final static short GASP_DOGRAYL = 0x0003;
		/**
		 * Number of records to follow.
		 */
	int numRanges;
	int rangeMaxPPEMs[];
	int rangeGaspBehaviour[];
	
        /**
         * Constructor.
         */
    public TTFGASPTable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         * TODO:: Sort the gaspRange values sorted by PPEM
         */
    public int readInputStream(DataInputStream din) throws IOException
	{
		this.tableVersion = din.readShort();
		this.numRanges = din.readShort();
		
		this.rangeMaxPPEMs = new int[numRanges];
		this.rangeGaspBehaviour = new int[numRanges];
		for (int i = 0;i < numRanges;i++)
		{
			this.rangeMaxPPEMs[i] = din.readShort();
			this.rangeGaspBehaviour[i] = din.readShort();
		}
		return 4 + (4 * numRanges);
    }
}
