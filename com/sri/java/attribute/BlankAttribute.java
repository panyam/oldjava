package com.sri.java.attribute;
import java.io.*;
import java.util.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;

public class BlankAttribute extends Attribute
{
    protected byte info[];

        /**
         * Default constructor.
         */
    protected BlankAttribute()
    {
    }

        /**
         * Default constructor.
         */
    public BlankAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

    public int readInputStream(DataInputStream din,ConstantPool cpool, Object params) throws IOException
    {
		if (info == null) info = new byte[attributeLength];
        din.readFully(info,0,attributeLength);
		return attributeLength + 6;
    }

    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "Attribute Value: Unknown");
    }
}
