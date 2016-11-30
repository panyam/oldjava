package com.sri.java.attribute;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;

/**
 * A class that holds a constantvalue attribute.
 */
public class ExceptionAttribute extends Attribute
{
    public short numExceptions = 0;
    public short exceptionTable[];

        /**
         * Default constructor.
         */
    protected ExceptionAttribute()
    {
    }

        /**
         * Constructor.
         */
    public ExceptionAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, Object params) throws IOException
    {
        ConstantPool cpool = parentClass.cpool;

        numExceptions = din.readShort();
		int total = 8;
        exceptionTable = new short[numExceptions];
        for (int i = 0;i < numExceptions;i++) exceptionTable[i] = din.readShort();
		total += (numExceptions * 2);
		return total;
    }

    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "# Exceptions: " + this.numExceptions);
		for (int i = 0;i < numExceptions;i++)
		{
			int ind = cpool.get(exceptionTable[i]).getClassIndex();
			String n = cpool.get(ind).getString();
			System.out.println(front + "Exception " + i + ": " + n);
		}
    }
}
