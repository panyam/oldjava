package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFCMAPTable extends TTFTable
{
		/**
		 * Number of encoding tables.
		 */
	int numTables;
	
	TTFEncodingTable encTables[];
	
        /**
         * Constructor.
         */
    public TTFCMAPTable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
			// read information here...
		this.tableVersion = din.readShort();
		this.numTables = din.readShort();
		this.encTables = new TTFEncodingTable[numTables];
		for (int i = 0;i < numTables;i++)
		{
			this.encTables[i] = new TTFEncodingTable();
		}
		return 4;
    }
	
	class TTFEncodingTable
	{
		int platformID;
		int platformSpecificEncID;
		int byteOffset;
	}
}
