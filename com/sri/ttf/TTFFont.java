package com.sri.ttf;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The class that holds information about a font.
 */
public class TTFFont
{
        /**
         * The font version.
         */
    int version;

        /**
         * The number of tables in the file.
         */
    int numTables;

        /**
         * Maximum power of (2 <= num tables) * 16
         */
    int searchRange;

        /**
         * Log of searchRange to base 2
         */
    int entrySel;

        /**
         * Numtables * 16-searchRange
         */
    int rangeShift;

        /**
         * Name of the font.
         */
    String fontName;

		/**
		 * The actual table directory.
		 */
	TTFTableDirectory tableDir = new TTFTableDirectory();

		/**
		 * The following tables are the ones that ARE REQUIRED
		 * for every font.
		 */
	TTFCMAPTable cmapTable = null;
	TTFGLYFTable glyfTable = null;
	TTFHEADTable headTable = null;
	TTFHHEATable hheaTable = null;
	TTFHMTXTable hmtxTable = null;
	TTFLOCATable locaTable = null;
	TTFMAXPTable maxpTable = null;
	TTFNAMETable nameTable = null;
	TTFPOSTTable postTable = null;
	TTFOS2Table os2Table = null;
	
        /**
         * Constructor.
         * Reads the font information from the input file.
         */
    public TTFFont(String fontName, DataInputStream din) throws IOException
    {
        setName(fontName);
        readInputStream(din);
    }

        /**
         * Sets the font name.
         */
    public void setName(String n)
    {
        this.fontName = n;
    }

        /**
         * Reads fnot information from an input stream.
         */
    public  void readInputStream(DataInputStream din) throws IOException
    {
        this.version = din.readInt();
		this.numTables = din.readShort();
		this.searchRange = din.readShort();
		this.entrySel = din.readShort();
		this.rangeShift = din.readShort();
		
			// number of bytes read so far...
		int bytesRead = 12;
		
			/**
			 * Read each table one by one...
			 */
		for (int i = 0;i < numTables;i++)
		{
			TTFTable ttable = TTFTableGenerator.readTable(this, din);
			tableDir.addTable(ttable);
				// assign the table to the approiate "required"
				// table method, if it IS a "requried" table
			bytesRead += 16;
		}
		
		int padding = 0;
		
			// after all the tables were read, read the actuall
			// information of the table..
		for (int i = 0;i < numTables;i++)
		{
			TTFTable table = tableDir.getTable(i);
			System.out.println("Reading contents of " + table.tag + ", at " + table.offset);
			if (table instanceof TTFCMAPTable)
			{
				cmapTable = (TTFCMAPTable)table;
			} else if (table instanceof TTFGLYFTable)
			{
				glyfTable = (TTFGLYFTable)table;
			} else if (table instanceof TTFHEADTable)
			{
				headTable = (TTFHEADTable)table;
			} else if (table instanceof TTFHHEATable)
			{
				hheaTable = (TTFHHEATable)table;
			} else if (table instanceof TTFHMTXTable)
			{
				hmtxTable = (TTFHMTXTable)table;
			} else if (table instanceof TTFLOCATable)
			{
				locaTable = (TTFLOCATable)table;
			} else if (table instanceof TTFMAXPTable)
			{
				maxpTable = (TTFMAXPTable)table;
			} else if (table instanceof TTFNAMETable)
			{
				nameTable = (TTFNAMETable)table;
			} else if (table instanceof TTFPOSTTable)
			{
				postTable = (TTFPOSTTable)table;
			} else if (table instanceof TTFOS2Table)
			{
				os2Table = (TTFOS2Table)table;
			}
				// read a byte to get to the offset..
			while (bytesRead != table.offset)
			{
				din.readByte();
				bytesRead++;
			}
			bytesRead += table.readInputStream(din);
		}
    }
}
