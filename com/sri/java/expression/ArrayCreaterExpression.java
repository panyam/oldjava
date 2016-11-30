package com.sri.java.expression;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.bytecode.*;
import com.sri.java.utils.*;

import java.io.*;
/**
 * An expression for array creation.
 */
public class ArrayCreaterExpression extends ValuedExpression
{
	protected final static byte []COMA = { (byte)',' };
	protected final static byte []OPEN_SQ = { (byte)'[' };
	protected final static byte []CLOSE_SQ = { (byte)']' };
	
	protected final static String NEW = "new ";
	
    ValuedExpression params[] = null;
	
		/**
		 * Constructor.
		 */
	public ArrayCreaterExpression ()
	{
	}
	
		/**
		 * Initialises this expression.
		 */
	public void create(VariableType typeName,
                       ValuedExpression params[])
	{
        this.outputType = typeName;
		this.params = params;
	}
	
		/**
		 * Initialises this expression.
		 */
	public void create(VariableType typeName,
                       ValuedExpression size)
	{
        this.outputType = typeName;
        this.params = new ValuedExpression[1];
        this.params[0] = size;
	}
	
	public VariableType getType(ConstantInfo []cpool)
	{
		return (VariableType)this.outputType;
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out) throws IOException
	{
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
		out.write(Formats.getSpacingString(level).getBytes());
		print(out);
	}
}
