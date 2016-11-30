package com.sri.ttf;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A table entry in a font file.
 */
public abstract class TTFTable
{
		/**
		 * The font to which this table belongs to.
		 */
	TTFFont parent;
	
		/**
		 * 0x00010000 for version 1.0
		 */
	int tableVersion;
	
		/**
		 * The tag of the table.
		 */
	String tag;

		/**
		 * The check sum for this table.
		 */
	int checkSum;
		
		/**
		 * Offset from the begining where the information
		 * for this table begins.
		 */
	int offset;
	
		/**
		 * The length of the payload in the table.
		 */
	int length;
	
		/**
		 * Constructor.
		 */
	protected TTFTable(TTFFont parent,
					   String tag, int checkSum, int offset, int length)
	{
		this.parent = parent;
		this.tag = tag;
		this.checkSum = checkSum;
		this.offset = offset;
		this.length = length;
	}
	
		/**
		 * Read the input stream for all the bytes
		 * corresponding to this table.
		 */
	public int readInputStream(DataInputStream din) 
			throws IOException
	{
		for (int i = 0;i < length;i++) din.readByte();
		return length;
	}
	
		/**
		 * Read a short from the input stream.
		 */
	public static short readShort(DataInputStream din) throws IOException
	{
		return (short)(((din.readByte() << 0) | (din.readByte() << 8) & 0xffff));
	}
	
		/**
		 * Read a int from the input stream.
		 */
	public static int readInt(DataInputStream din) throws IOException
	{
		return (((din.readByte() << 0) | 
				(din.readByte() << 8) |
				(din.readByte() << 16) |
				(din.readByte() << 24)) & 0xffffffff);
	}
}