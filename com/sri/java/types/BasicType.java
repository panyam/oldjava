package com.sri.java.types;

import java.io.*;

/**
 * A Basic Type.
 */
public class BasicType extends VariableType
{
	public final static byte VOID = 0;
	public final static byte BOOLEAN = 1;
	public final static byte BYTE = 2;
	public final static byte CHAR = 3;
	public final static byte SHORT = 4;
	public final static byte INT = 5;
	public final static byte LONG = 6;
	public final static byte FLOAT = 7;
	public final static byte DOUBLE = 8;
	
	public final static String typeNames[] =
	{
		"void", "boolean", "byte", "char", "short", "int", "long", "float", "double"
	};
	
	public static String baseNames[] = 
	{
		"", "bool", "by", "ch", "sh", "n", "l", "f", "d"
	};
	
	protected final static String allTypes = "BCDEFIJSZV";
	
	public final static BasicType VOID_TYPE = new BasicType(VOID);
	public final static BasicType BOOLEAN_TYPE = new BasicType(BOOLEAN);
	public final static BasicType BYTE_TYPE = new BasicType(BYTE);
	public final static BasicType CHAR_TYPE = new BasicType(CHAR);
	public final static BasicType SHORT_TYPE = new BasicType(SHORT);
	public final static BasicType INT_TYPE = new BasicType(INT);
	public final static BasicType LONG_TYPE = new BasicType(LONG);
	public final static BasicType FLOAT_TYPE = new BasicType(FLOAT);
	public final static BasicType DOUBLE_TYPE = new BasicType(DOUBLE);

    public final static VariableType typeList[] = 
    {
	    VOID_TYPE, BOOLEAN_TYPE, BYTE_TYPE, CHAR_TYPE, SHORT_TYPE,
        INT_TYPE, LONG_TYPE, FLOAT_TYPE, DOUBLE_TYPE, 
    };
	
            // put the predefined types in our hashtable 
            // of predefined types.
    static
    {
        for (int i = 0;i < typeList.length;i++)
        {
            if (!predefinedTypes.containsKey(typeNames[i]))
            {
                predefinedTypes.put(typeNames[i],typeList[i]);
            }
        }
    }

	protected byte type = VOID;
	
	public BasicType()
	{
		type = VOID;
	}
	
	public BasicType(char ch)
	{
		type = (byte)allTypes.indexOf(ch);
	}
	
	public BasicType(byte type)
	{
		this.type = type;
		if (type < VOID || type > DOUBLE)
		{
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}
	
	public byte getType()
	{
		return type;
	}
	
	public void print(DataOutputStream dout) throws IOException
	{
		dout.writeBytes(typeNames[type]);
	}
	
	public String toString()
	{
		return typeNames[type];
	}
	
	public String getSampleName(int id)
	{
		return baseNames[type & 0xff] + "_" + id;
	}
	
	public String getBaseName()
	{
		return baseNames[this.type & 0xff];
	}
	
	public String getTypeName()
	{
		return this.typeNames[type & 0xff];
	}

    public int hashCode()
    {
        return typeNames[type & 0xff].hashCode();
    }
}
