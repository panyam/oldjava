package com.sri.java.attribute;

import java.util.*;
import java.io.*;

import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;
/**
 * A class that an entry for each Attribute.
 * By default every attribute info class
 * is a Synthetic Attribute because
 * this attribute has no information
 * and hence the attribute length is 0.
 */
public abstract class Attribute 
{
        /**
         * The parent class that holds this attribute.
         */
    protected SJavaClass parentClass = null;

        /**
         * Index into the constant pool table.
         */
    protected short nameIndex = 0;

        /**
         * The total number of bytes in the attributes.
         */
    protected int attributeLength = 0;
	
	protected String attribName = "";

        /**
         * Default constructor.
         */
    protected Attribute()
    {
    }

        /**
         * Default constructor.
         */
    public Attribute(SJavaClass parentClass)
    {
        this.parentClass = parentClass;
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, ConstantPool cpool, Object params) throws IOException
    {
		return 6;
    }

    public void print(ConstantPool cpool, int level)
    {
    }
}
