package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.bytecode.*;

/**
 * An expression for identifiers.
 *
 * Some examples are:
 */
public class IdentifierExpression extends ValuedExpression
{
	public String varName = "";
	
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
		out.write(varName.getBytes());
	}
	
	public VariableType getType(ConstantInfo []cpool)
	{
		return (VariableType)this.outputType;
	}
}
