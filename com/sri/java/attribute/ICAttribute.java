package com.sri.java.attribute;

import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;
import java.util.*;
import java.io.*;

/**
 * A class that holds a constantvalue attribute.
 */
public class ICAttribute extends Attribute
{
        /**
         * Number of inner classes.
         */
    protected short numICs = 0;
    protected IC []ics = null;

        /**
         * Default constructor.
         */
    protected ICAttribute()
    {
    }

        /**
         * Constructor.
         */
    public ICAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, Object params) throws IOException
    {
        ConstantPool cpool = parentClass.cpool;

        numICs = din.readShort();
		int total = 8;
        ics = new IC[numICs];
        for (int i = 0;i < numICs;i++)
        {
            ics[i] = new IC();
            total += ics[i].readInputStream(din);
        }
		return total;
    }

    class IC
    {
        short icIndex = 0;
        short ocIndex = 0;
        short icName = 0;
		SJavaAccessFlags accessFlags = new SJavaAccessFlags();

            /**
             * Constructor.
             */
        public IC()
        {
        }

            /**
             * Read data from the input stream;
             */
        public int readInputStream(DataInputStream din) throws IOException
        {
            icIndex = din.readShort();
            ocIndex = din.readShort();
            icName = din.readShort();
            accessFlags.accessFlags = din.readShort();
			return 8;
        }
    }

    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "# Inner Class: " + this.numICs);
		for (int i = 0;i < numICs;i++)
		{
			System.out.println(front + "IC " + i + 
							   ": icIndex, ocIndex, icName = " +
							   cpool.get(ics[i].icIndex).getString() +
							   cpool.get(ics[i].ocIndex).getString() +
							   cpool.get(ics[i].icName).getString());
		}
    }
}
