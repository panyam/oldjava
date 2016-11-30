package com.sri.java.attribute;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;

/**
 * This class reads the input stream and returns an appropriate
 * attribute that matches the contents of the input stream.
 */
public class AttributeReader
{
	protected int bytesRead = 0;
	
		/**
		 * This table holds various types of attributes
		 * indexed by their name.
		 */
	protected static Hashtable attributeTable = new Hashtable();
	
	static
	{
		attributeTable.put("Code",CodeAttribute.class);
		attributeTable.put("ConstantValue",CValueAttribute.class);
		attributeTable.put("SourceFile",CValueAttribute.class);
		attributeTable.put("Exceptions",ExceptionAttribute.class);
		attributeTable.put("Synthetic",Attribute.class);
		attributeTable.put("Depracated",CodeAttribute.class);
		attributeTable.put("InnerClasses",ICAttribute.class);
		attributeTable.put("LineNumberTable",LNTableAttribute.class);
		attributeTable.put("LocalVariableTable",LVTableAttribute.class);
		attributeTable.put("LocalVariableTable",LVTableAttribute.class);
	}
	
	public int getBytesRead()
	{
		return bytesRead;
	}
	
		/**
		 * Reads an attribute from the input stream.
		 * @param din:	The input stream from which the attribute is to be read.
		 * @param cpool:	The constant pool table.
		 * @param params:	Other parameters.
		 */
    public Attribute readAttribute(DataInputStream din, ConstantPool cpool, Object params) throws IOException
    {
			// The name index is the index into the
			// constant pool table that holds the name
			// of this attribute.
		short nameIndex = din.readShort();
		int length = din.readInt();
		bytesRead = 6 + length;
		String name = cpool.get(nameIndex).getString();

		Class attrClass = (Class)attributeTable.get(name);
		Attribute out = null;
		if (attrClass == null)		// if unknown attribute type
		{
			out = new BlankAttribute();
		} else
		{
			try
			{
				out = (Attribute)attrClass.newInstance();
				if (name.equalsIgnoreCase("SourceFile"))
				{
					((CValueAttribute)out).sourceFile = true;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (out != null)
		{
			out.attribName = name;
			out.attributeLength = length;
			out.nameIndex = nameIndex;
			out.readInputStream(din,cpool,params);
		}
		return out;
    }
}
