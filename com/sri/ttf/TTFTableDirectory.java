package com.sri.ttf;

import java.io.*;
import java.util.*;

/**
 * A directory of tables.  The tables are sorted
 * in increasing order of the offset.
 */
public class TTFTableDirectory
{

		/**
		 * The tags that are read.
		 * As the tags are read, they are put in this
		 * vector.  However, they are put in increasing 
		 * order of the offset within the file.  This is done
		 * so that as soon as the Table directory is read, each table
		 * can be traversed in this order and the whole file can
		 * be read without having to read the whole file more than
		 * once.
		 */
	TTFTable tables[] = new TTFTable[5];

		/**
		 * Number of tables present.
		 */
	int nTables = 0;
							   
		/**
		 * The index into the above vector of a given
		 * type of tag.
		 */
	Hashtable tagIndices = new Hashtable();

		/**
		 * Constructor.
		 */
	public TTFTableDirectory()
	{
	}
	
		/**
		 * Add a new table.
		 */
	public void addTable(TTFTable table)
	{
		if (table == null) return ;
		Integer in = ((Integer)tagIndices.get(table.tag));
		
			// if table already exists then quit...
		if (in != null) return ;
		
			// resize table if need be
		if (tables.length == nTables)
		{
			TTFTable t2[] = tables;
			tables = new TTFTable[nTables + 2];
			System.arraycopy(t2, 0, tables, 0, nTables);
		}
		
			/// insert then...
			// do a simple linear insert... as insertion
			// are rare and dont need the speed up...
		boolean found = false;
		int i = 0;
		for (i = 0;!found && i < nTables;i++)
		{
			if (table.offset < tables[i].offset)
			{
				found = true;
				i--;
			}
		}
		if (i < nTables)
		{
			System.arraycopy(tables, i, tables, i + 1, nTables - i);
			tables[i] = table;
			nTables++;
		} else
		{
			tables[nTables++] = table;
		}
	}
	
		/**
		 * Find the table with the given tag.
		 */
	public TTFTable findTable(String tag)
	{
		for (int i= 0;i < nTables;i++)
		{
			if (tables[i].tag.equals(tag)) return tables[i];
		}
		return null;
	}
	
		/**
		 * Get the ith table.
		 */
	public TTFTable getTable(int i)
	{
		return tables[i];
	}
}