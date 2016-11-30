package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFVDMXTable extends TTFTable
{
	short numRecs;
	short numRatios;
	Ratio ratRange[];
	short offset[];
	
		// following this is the VDMX groups info...
		/**
		 * Number of vfmx records to follow.
		 */
	short recs;

		/**
		 * Starting yPelHeight.
		 */
	byte startsz;

		/**
		 * Ending yPelHeight;
		 */
	byte endsz;
	
		/**
		 * The actual vdmx records.
		 */
	VTable vdmx[];
	
        /**
         * Constructor.
         */
    public TTFVDMXTable(TTFFont par, String tag, 
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
		this.numRecs = din.readShort();
		this.numRatios = din.readShort();
		this.ratRange = new Ratio[numRatios];
		this.offset = new short[numRatios];
		for(int i = 0;i < numRatios;i++)
		{
			ratRange[i] = new Ratio();
			ratRange[i].bCharSet = din.readByte();
			ratRange[i].xRatio = din.readByte();
			ratRange[i].yStartRatio = din.readByte();
			ratRange[i].yEndRatio = din.readByte();
		}
		for(int i = 0;i < numRatios;i++)
		{
			offset[i] = din.readShort();
		}
		
		this.recs = din.readShort();
		this.startsz = din.readByte();
		this.endsz = din.readByte();
		this.vdmx = new VTable[recs];
		for (int i = 0;i < recs;i++)
		{
			vdmx[i] = new VTable();
			vdmx[i].yPelHeight = din.readShort();
			vdmx[i].yMax = din.readShort();
			vdmx[i].yMin = din.readShort();
		}
		return (2 * numRatios) + (numRatios * 4) + 6 + (4 + 6 * recs);
    }
	
	class Ratio
	{
		byte bCharSet;
		byte xRatio;
		byte yStartRatio;
		byte yEndRatio;
	}
	
	class VTable
	{
		short yPelHeight;
		short yMax;
		short yMin;
	}
}
