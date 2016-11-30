package com.sri.java;

import java.util.*;
import java.io.*;
import com.sri.java.utils.*;

    /**
     * A class that is an entry in the Constant pool.
     */
public class ConstantInfo
{
    public final static byte CONSTANT_NONE = 0;
    public final static byte CONSTANT_UTF8 = 1;
    public final static byte CONSTANT_INTEGER = 3;
    public final static byte CONSTANT_FLOAT = 4;
    public final static byte CONSTANT_LONG = 5;
    public final static byte CONSTANT_DOUBLE = 6;
    public final static byte CONSTANT_CLASS = 7;
    public final static byte CONSTANT_STRING = 8;
    public final static byte CONSTANT_FIELD_REF = 9;
    public final static byte CONSTANT_METHOD_REF = 10;
    public final static byte CONSTANT_INTERFACE_METHOD_REF = 11;
    public final static byte CONSTANT_NAME_AND_TYPE = 12;
        /**
         * The tag for the constant.
         */
    public byte tag = CONSTANT_NONE;

        /**
         * The info bytes.
         */
    protected byte info[] = null;

        /**
         * The string with the info bytes.
         */
    protected String valueString = null;
	//protected Object value = null;

        /**
         * The size of info for each class type.
         */
    protected static int infoSize[] = {
        -1, -1, -1, 4, 4, 8, 8, 2, 2, 4, 4, 4, 4
    };

        /**
         * Name of each tag.
         */
    protected static String tagNames[] = {
        "CONSTANT_NONE", "CONSTANT_UTF8", "-----", "CONSTANT_INTEGER", 
        "CONSTANT_FLOAT", "CONSTANT_LONG", "CONSTANT_DOUBLE", 
        "CONSTANT_CLASS", "CONSTANT_STRING", "CONSTANT_FIELD_REF", 
        "CONSTANT_METHOD_REF", "CONSTANT_INTERFACE_METHOD_REF",
        "CONSTANT_NAME_AND_TYPE",
    };

    public ConstantInfo()
    {
		setTag(ConstantInfo.CONSTANT_NONE);
    }

        /**
         * Constructor with tag type.
         */
    public ConstantInfo(byte tag)
    {
        setTag(tag);
    }

        /**
         * Constructor for an Int constant.
         */
    public ConstantInfo(int intConst)
    {
        setValue(intConst);
    }

        /**
         * Constructor for an Long constant.
         */
    public ConstantInfo(long longConst)
    {
        setValue(longConst);
    }

        /**
         * Constructor for an Float constant.
         */
    public ConstantInfo(float floatConst)
    {
        setValue(floatConst);
    }

        /**
         * Constructor for an Long constant.
         */
    public ConstantInfo(double doubleConst)
    {
        setValue(doubleConst );
    }

        /**
         * Sets the tag.
         */
    public void setTag(byte tag)
    {
        this.tag = tag;
        if (infoSize[tag & 0xff] > 0 && 
              (info == null || info.length < infoSize[tag & 0xff]))
        {
		    info = new byte[infoSize[tag & 0xff]];
        }
    }

        /**
         * The string value of this constant info.
         */
    public String toString()
    {
        return tagNames[tag & 0xff];
    }

        /**
         * Returns the integer value of the bytes.
         * Only valid when tags is an INT type.
         */
    public int getIntValue()
    {
        return ((info[0] & 0xff) << 24) |
               ((info[1] & 0xff) << 16) |
               ((info[2] & 0xff) << 8) |
               (info[3] & 0xff);
    }

        /**
         * Set the values of the "info" list
         * from an integer.
         */
    public void setValue(int value)
    {
        setTag(CONSTANT_INTEGER);
        info[0] = (byte)((value >> 24) & 0xff);
        info[1] = (byte)((value >> 16) & 0xff);
        info[2] = (byte)((value >> 8) & 0xff);
        info[3] = (byte)(value & 0xff);
    }

        /**
         * Set the values of the "info" list
         * from a long integer.
         */
    public void setValue(long value)
    {
        setTag(CONSTANT_LONG);
        info[0] = (byte)((value >> 56) & 0xff);
        info[1] = (byte)((value >> 48) & 0xff);
        info[2] = (byte)((value >> 40) & 0xff);
        info[3] = (byte)((value >> 32) & 0xff);
        info[4] = (byte)((value >> 24) & 0xff);
        info[5] = (byte)((value >> 16) & 0xff);
        info[6] = (byte)((value >>  8) & 0xff);
        info[7] = (byte)(value & 0xff);
    }

        /**
         * Set the values of the "info" list
         * from float.
         */
    public void setValue(float value)
    {
        setTag(CONSTANT_FLOAT);
        setValue(Float.floatToIntBits(value));
    }

        /**
         * Set the values of the "info" list
         * from a double.
         */
    public void setValue(double value)
    {
        setTag(CONSTANT_DOUBLE);
        /*if (value == Double.POSITIVE_INFINITY)
        {
            setValue(0x7ff0000000000000L);
        } else if (value == Double.NEGATIVE_INFINITY)
        {
            setValue(0xFFF0000000000000L);
        } else if (value == Double.NaN)
        {
                // actually a nan can be anything 
                // between 0x7FF0000000000001L  0x7FFFFFFFFFFFFFFFL  or
                // between 0xFFF0000000000001L  0xFFFFFFFFFFFFFFFFL
                // but we arejust picking an arbitrary number 
            setValue(0x7FF0000000000005L);
        } else
        {
            int s = value < 0 ? -1 : 1;
        }*/
            //TODO:: Check whether we need to use doubleToLongBits 
            //or doubleToRawLongBits
        setValue(Double.doubleToLongBits(value));
    }

        /**
         * Sets the UTF8 value.
         */
    public void setValue(String string)
    {
        setTag(CONSTANT_UTF8);

        byte bytes[] = string.getBytes();
        info = new byte[bytes.length + 2];
        info[0] = (byte)((bytes.length >> 8) & 0xff);
        info[1] = (byte)(bytes.length & 0xff);
        System.arraycopy(bytes, 0, info, 2, bytes.length);
    }

        /**
         * Sets the method value.
         */
    public void setMethodValue(short classIndex, short name_and_type_index)
    {
        setTag(CONSTANT_METHOD_REF);
        info[0] = (byte)((classIndex >> 8) & 0xff);
        info[1] = (byte)(classIndex & 0xff);
        info[2] = (byte)((name_and_type_index >> 8) & 0xff);
        info[3] = (byte)(name_and_type_index & 0xff);
    }

        /**
         * Sets the field value.
         */
    public void setFieldValue(short classIndex, short name_and_type_index)
    {
        setTag(CONSTANT_FIELD_REF);
        info[0] = (byte)((classIndex >> 8) & 0xff);
        info[1] = (byte)(classIndex & 0xff);
        info[2] = (byte)((name_and_type_index >> 8) & 0xff);
        info[3] = (byte)(name_and_type_index & 0xff);
    }

        /**
         * Sets the interface value.
         */
    public void setInterfaceValue(short classIndex, short name_and_type_index)
    {
        setTag(CONSTANT_INTERFACE_METHOD_REF);
        info[0] = (byte)((classIndex >> 8) & 0xff);
        info[1] = (byte)(classIndex & 0xff);
        info[2] = (byte)((name_and_type_index >> 8) & 0xff);
        info[3] = (byte)(name_and_type_index & 0xff);
    }

        /**
         * Sets the class value.
         */
    public void setClassValue(short nameIndex)
    {
        setTag(CONSTANT_CLASS);
        info[0] = (byte)((nameIndex >> 8) & 0xff);
        info[1] = (byte)(nameIndex & 0xff);
    }

        /**
         * Set the name and type value.
         */
    public void setNameAndTypeValue(short nameIndex, short descrIndex)
    {
        setTag(CONSTANT_NAME_AND_TYPE);
        info[0] = (byte)((nameIndex >> 8) & 0xff);
        info[1] = (byte)(nameIndex & 0xff);
        info[2] = (byte)((descrIndex >> 8) & 0xff);
        info[3] = (byte)(descrIndex & 0xff);
    }

        /**
         * Set the string value.
         */
    public void setStringValue(int stringIndex)
    {
        setTag(CONSTANT_STRING);
        info[0] = (byte)((stringIndex >> 8) & 0xff);
        info[1] = (byte)(stringIndex & 0xff);
    }

        /**
         * Returns the float value of the bytes.
         * Only valid when tags is an FLOAT type.
         */
    public float getFloatValue()
    {
        int bits = getIntValue();
        if (bits == 0x7f800000) return Float.POSITIVE_INFINITY;
        else if (bits == 0xff800000) return Float.NEGATIVE_INFINITY;
        else if ((bits > 0x7f800000 && bits < 0x7fffffff) ||
                 (bits > 0x7f800000 && bits < 0x7fffffff)) return Float.NaN;
        else 
        {
            int s = ((bits >> 31) == 0) ? 1 : -1;
            int e = ((bits >> 23) & 0xff);
            int m = (e == 0) ?
                        (bits & 0x7fffff) << 1 :
                        (bits & 0x7fffff) | 0x800000;
            return (float)(s * m * Math.pow(2,e - 150));
        }
    }

        /**
         * Returns the long value of the bytes.
         * Only valid when tags is an LONG type.
         */
    public long getLongValue()
    {
        return (info[0] << 56) |
               (info[1] << 48) |
               (info[2] << 40) |
               (info[3] << 32) |
               (info[4] << 24) |
               (info[5] << 16) |
               (info[6] << 8) |
               info[7];
    }

        /**
         * Returns the float value of the bytes.
         * Only valid when tags is an FLOAT type.
         */
    public double getDoubleValue()
    {
        long bits = getLongValue();
        if (bits == 0x7ff0000000000000L) return Double.POSITIVE_INFINITY;
        else if (bits == 0x7ff0000000000000L) return Double.NEGATIVE_INFINITY;
        else if ((bits > 0x7ff0000000000000L && bits < 0x7fffffffffffffffL) ||
                 (bits > 0xfff0000000000000L && bits < 0xffffffffffffffffL)) return Float.NaN;
        else 
        {
            long s = ((bits >> 63) == 0) ? 1 : -1;
            long e = ((bits >> 52) & 0x7ffL);
            long m = (e == 0) ?
                        (bits & 0xfffffffffffffL) << 1 :
                        (bits & 0xfffffffffffffL) | 0x10000000000000L;
            return s * m * Math.pow(2,e - 1075);
        }
    }

        /**
         * Gets the index of into the constant pool table
         * for String info structures.
         */
    public short getStringIndex()
    {
        return (short)(((info[0] & 0xff) << 8) | (info[1] & 0xff));
    }

        /**
         * Returns the index into the constant pool table
         * which holds a corresponding class name entry.
         * Only used for Class and Name_and_type 
         * info structures.
         */
    public short getClassOrNameIndex()
    {
        return (short)(((info[0] & 0xff) << 8) | (info[1] & 0xff));
    }

        /**
         * Returns the descripter index into the constant 
         * pool table.
         * Only to be used for NameAndType info structures.
         */
    public short getDescriptorIndex()
    {
        return (short)(((info[2] & 0xff) << 8) | (info[3] & 0xff));
    }

        /**
         * Returns the string held in the bytes.
         * Only used for UTF8 info structures.
         */
    public String getString()
    {
        return valueString;
    }

        /**
         * Returns the index into the constant pool table
         * which holds a corresponding class entry.
         * Only used for FieldRef, MethodRef and 
         * InterfaceMethodRef structures.
         */
    public short getClassIndex()
    {
        return (short)(((info[0] & 0xff) << 8) | (info[1] & 0xff));
    }

        /**
         * Returns the name_and_type index into the constant 
         * pool table which holds a corresponding class entry.
         * Only used for FieldRef, MethodRef and 
         * InterfaceMethodRef structures.
         */
    public short getNameAndTypeIndex()
    {
        return (short)(((info[2] & 0xff) << 8) | (info[3] & 0xff));
    }

    public int readInputStream(DataInputStream din) throws IOException
    {
        int total = 0;
        if (tag == CONSTANT_NONE)
        {       // then read the tag as welll...
            setTag(din.readByte());
            total ++;
        }

        // infoSizes = -1, -1, -1, 4, 4, 8, 8, 2, 2, 4, 4, 4, 4

        if (tag == CONSTANT_UTF8)
        {
            short s = din.readShort();
            total+= 2;
            infoSize[1] = s;
            info = new byte[s];
        }
        for (int i = 0;i < infoSize[tag & 0xff];i++) info[i] = din.readByte();
        total += infoSize[tag & 0xff];

        if (tag == CONSTANT_UTF8) 
		{
				// here is how we calculate the string from the bytes.
			valueString = new String(info);
			//System.out.println("Value String = " + valueString);
		}
		//infoSize
        return total;
    }

    public void print(ConstantInfo []cpool, int level)
    {
		System.out.print(Formats.getSpacingString(level) + tagNames[this.tag & 0xff] + " -> ");
		String val = "";
		if (tag == CONSTANT_CLASS)
		{
			ConstantInfo ci = cpool[getClassIndex()];
			val = ci.getString();
		}
		else if (tag == CONSTANT_UTF8)
		{
			val = "" + this.getString();
		}
		else if (tag == CONSTANT_INTEGER)
		{
			val = "" + this.getIntValue();
		}
		else if (tag == CONSTANT_FLOAT)
		{
			val = "" + this.getFloatValue();
		}
		else if (tag == CONSTANT_LONG)
		{
			val = "" + this.getLongValue();
		}
		else if (tag == CONSTANT_DOUBLE)
		{
			val = "" + this.getDoubleValue();
		}
		else if (tag == CONSTANT_STRING)
		{
			val = cpool[getStringIndex()].getString();
		}
		else if (tag == CONSTANT_FIELD_REF)
		{
		}
		else if (tag == CONSTANT_METHOD_REF)
		{
		}
		else if (tag == CONSTANT_INTERFACE_METHOD_REF)
		{
		}
		else if (tag == CONSTANT_NAME_AND_TYPE)
		{
		}
		System.out.println(val);
    }
}
