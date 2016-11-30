package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.bytecode.*;

/**
 * This is a type of expression that actually holds a 
 * constant value. eg boolean values, double, floats, longs
 * strings and so on.  if the value is null then it 
 * holds the null value.
 */

public class CValueExpression extends ValuedExpression

{
		// some predefined values...
	public final static CValueExpression doubles[] = new CValueExpression[4];
	public final static CValueExpression longs[] = new CValueExpression[4];
	public final static CValueExpression floats[] = new CValueExpression[4];
	public final static CValueExpression ints[] = new CValueExpression[6];
	public final static CValueExpression bytes[] = new CValueExpression[256];
	public final static CValueExpression bools[] = new CValueExpression[2];
	public final static CValueExpression NULL = new CValueExpression();
	public final static CValueExpression LENGTH = new CValueExpression("length");
	public final static CValueExpression THIS = new CValueExpression("this");
	
	static
	{
		for (int i = 0;i < doubles.length;i++) 
		{
			doubles[i].value = new Double(i);
			doubles[i].setType(BasicType.DOUBLE_TYPE);
		}
		for (int i = 0;i < longs.length;i++) 
		{
			longs[i].value = new Long(i);
			longs[i].setType(BasicType.LONG_TYPE);
		}
		for (int i = 0;i < floats.length;i++)
		{
			floats[i].value = new Float(i);
			floats[i].setType(BasicType.FLOAT_TYPE);
		}
		for (int i = 0;i < ints.length;i++)
		{
			ints[i].value = new Integer(i);
			ints[i].setType(BasicType.INT_TYPE);
		}
		for (int i = 0;i < bytes.length;i++)
		{
			bytes[i].value = new Byte((byte)i);
			bytes[i].setType(BasicType.BYTE_TYPE);
		}
		bools[0].value = Boolean.FALSE;	bools[1].value = Boolean.TRUE;
		bools[0].setType(BasicType.BOOLEAN_TYPE); bools[1].setType(BasicType.BOOLEAN_TYPE);
	}
	Object value = null;

	public CValueExpression ()
	{
	}
	
	public CValueExpression (Object val)
	{
		this.value = val;
	}
	
		/**
		 * Gets the type...
		 */
	public VariableType getType(ConstantInfo [] cpool)
	{
		if (outputType == null)
		{
			if (value instanceof String) outputType = ObjectType.STRING_TYPE;
			else if (value instanceof Float) outputType = BasicType.FLOAT_TYPE;
			else if (value instanceof Double) outputType = BasicType.DOUBLE_TYPE;
			else if (value instanceof Integer) outputType = BasicType.INT_TYPE;
			else if (value instanceof Long) outputType = BasicType.LONG_TYPE;
			else if (value instanceof Byte) outputType = BasicType.BYTE_TYPE;
			else if (value instanceof Boolean) outputType = BasicType.BOOLEAN_TYPE;
			else if (value instanceof Character) outputType = BasicType.CHAR_TYPE;
			else if (value instanceof Short) outputType = BasicType.SHORT_TYPE;
			else outputType = ObjectType.OBJECT_TYPE;
		}
		return (VariableType)this.outputType;
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out) throws IOException
	{
		print(out,0);
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
		out.write(value.toString().getBytes());
	}
}

