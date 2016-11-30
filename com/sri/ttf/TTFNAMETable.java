package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This table allows multilingual strings to be 
 * associated with the TrueType font file.
 */
public class TTFNAMETable extends TTFTable
{
	int formatSelector;
	int numRecords;
	int stringOffset;
	
	String variableString;
	
	NameRecord records[];
        /**
         * Constructor.
         */
    public TTFNAMETable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		this.formatSelector = din.readShort();
		this.numRecords = din.readShort();
		this.stringOffset = din.readShort();
		int currOffset = 6;
		records = new NameRecord[numRecords];
		for (int i = 0;i < numRecords;i++)
		{
			records[i] = new NameRecord();
			records[i].platformID = din.readShort();
			records[i].platformSpecificEncId = din.readShort();
			records[i].languageId = din.readShort();
			records[i].nameID = din.readShort();
			records[i].stringLength = din.readShort();
			records[i].stringOffset = din.readShort();
			currOffset += 12;
		}
		while(currOffset < stringOffset) 
		{
			din.readByte();
			currOffset++;
		}
			// now read the actual string...
		byte stringBytes[] = new byte[this.length - currOffset];
		din.readFully(stringBytes);
		variableString = new String(stringBytes);
		
		// TODO:: now read the string value for each of the name record.
		for (int i = 0;i < numRecords;i++)
		{
		}
		return currOffset + stringBytes.length;
    }
	
	class NameRecord
	{
		short platformID;
		short platformSpecificEncId;
		short languageId;
		short nameID;
		short stringLength;
		short stringOffset;
	}
}
