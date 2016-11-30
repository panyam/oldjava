package com.sri.java.attribute;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.utils.*;
import com.sri.java.expression.*;

/**
 * A class that holds a constantvalue attribute.
 * This class can also be used for the Sourcefile attribute.
 * Because both have a similar parameter.
 */
public class CValueAttribute extends Attribute
{
        /**
         * Const value index.
         */
    protected short constValueIndex = 0;

		/**
		 * Tells if we are a source file type...
		 */
	protected boolean sourceFile = false;

        /**
         * Default constructor.
         */
    protected CValueAttribute()
    {
    }

        /**
         * Default constructor.
         */
    public CValueAttribute(SJavaClass parentClass)
    {
        this(parentClass, false);
    }
	
        /**
         * Default constructor.
         */
    public CValueAttribute(SJavaClass parentClass, boolean b)
    {
        super(parentClass);
        this.sourceFile = b;
        attributeLength = 2;
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, ConstantPool cpool, Object params) throws IOException
    {
        constValueIndex = din.readShort();
		return 8;
    }

        /**
         * Return the cosnt value index.
         */
    public short getConstValueIndex()
    {
        return constValueIndex;
    }

    public void print(ConstantPool cpool, int level)
    {
		String front = Formats.getSpacingString(level);

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "Attribute value: " + cpool.get(constValueIndex).getString());
    }
}
