package com.sri.java.attribute;

import com.sri.java.bytecode.*;
import com.sri.java.utils.*;
import com.sri.java.expression.*;

import java.util.*;
import java.io.*;

/**
 * A class that holds a constantvalue attribute.
 */
public class LVTableAttribute extends Attribute
{
        /**
         * Number of inner classes.
         */
    protected short numLocals = 0;
    protected short startPCs[] = null;
    protected short nameIndex[] = null;
    protected short descrIndex[] = null;
	protected short length[] = null;
    protected short index[] = null;

	protected Expression exprs[] = null;

        /**
         * Default constructor.
         */
    protected LVTableAttribute()
    {
    }

        /**
         * Default constructor.
         */
    public LVTableAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, ConstantPool cpool, Object params) throws IOException
    {
        numLocals = din.readShort();
		int total = 8;
        startPCs = new short[numLocals];
        length = new short[numLocals];
        nameIndex = new short[numLocals];
        descrIndex = new short[numLocals];
        index = new short[numLocals];
        for (int i = 0;i < numLocals;i++)
        {
            startPCs[i] = din.readShort();
            length[i] = din.readShort();
            nameIndex[i] = din.readShort();
            descrIndex[i] = din.readShort();
            index[i] = din.readShort();
        }
		total += (numLocals * 8);
		return total;
    }

		/**
		 * Here we create identifier objects for the various local variables found.
		 */
	protected void createIdentifiers(ConstantInfo cpool)
	{
	}
	
    public void print(ConstantInfo cpool, int level)
    {
		String front = Formats.getSpacingString(level);;
		
		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "# Vars: " + this.numLocals);
		for (int i = 0;i < numLocals;i++)
		{
			String name = cpool.get(nameIndex[i]).getString();
			String desc = cpool.get(descrIndex[i]).getString();
			System.out.println(front + "startPc, length, nameIndex, descrIndex, index = " + 
							   startPCs[i] + ", " + 
							   length[i] + ", " +
							   nameIndex[i] + " = " + name + ", " + 
							   descrIndex[i] + " = " + desc + ", " + 
							   index[i]);
		}
    }
}
