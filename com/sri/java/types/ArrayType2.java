package com.sri.java.types;

import java.io.*;

/**
 * An array type.
 */
public class ArrayType2 extends VariableType
{
	byte nDims = 0x00;				// number of dimensions..
	VariableType arrayType;			// type of the array.
	
	public final static String dimStrings[] = new String[256];

	static
	{
		dimStrings[0] = "";
		for (int i = 1;i < 256;i++) dimStrings[i] = dimStrings[i - 1] + "[]";
	}
					 
		/**
		 * Constructor.
		 */
	public ArrayType2(VariableType t, int ndims)
	{
		if (nDims < 0 || nDims > 255)
		{
			throw new IllegalArgumentException("An array MUST have between 0 and 255 dimensions.");
		}
		nDims = (byte)(ndims & 0xff);
	}
	
		/**
		 * String value.
		 */
	public String toString()
	{
		return arrayType + dimStrings[nDims & 0xff];
	}
	
		/**
		 * Given an id, returns a sample name.
		 * eg if id = 3 and array type is string,
		 * name would be string_3
		 */
	public String getSampleName(int id)
	{
		return arrayType.getBaseName() + "_" + id;
	}
	
		/**
		 * Returns thebase name for this aray.
		 */
	public String getBaseName()
	{
		return arrayType.getBaseName() + "_" + (nDims & 0xff);
	}
	
	public String getTypeName()
	{
		return arrayType + dimStrings[nDims & 0xff];
	}
}
