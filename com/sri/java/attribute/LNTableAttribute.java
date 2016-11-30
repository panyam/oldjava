package com.sri.java.attribute;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.utils.*;
/**
 * A class that holds a constantvalue attribute.
 */
public class LNTableAttribute extends Attribute
{
        /**
         * Number of inner classes.
         */
    protected short numLines = 0;
    protected short startPCs[] = null;
    protected short lineNums[] = null;

        /**
         * Default constructor.
         */
    protected LNTableAttribute()
    {
    }

        /**
         * Default constructor.
         */
    public LNTableAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, ConstantPool cpool, Object params) throws IOException
    {
        numLines = din.readShort();
        startPCs = new short[numLines];
        lineNums = new short[numLines];
		int total = 8;
        for (int i = 0;i < numLines;i++)
        {
            startPCs[i] = din.readShort();
            lineNums[i] = din.readShort();
        }
		total += (numLines * 4);
		return total;
    }

    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "# Lines: " + this.numLines);
		for (int i = 0;i < numLines;i++)
		{
			System.out.println(front + "Line " + i + ": startPC, lineNum = " + startPCs[i] + ", " + lineNums[i]);
		}
    }
}
