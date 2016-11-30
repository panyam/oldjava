package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.utils.*;
import com.sri.java.bytecode.*;

/**
 * This class represents all expression types which
 * have a value.
 * 
 * eg a+b, a = c, int a,b,c[], d = ...;
 */
public abstract class ValuedExpression extends Expression
{
	public Type outputType = null;
	
		/**
		 * Gets the type for this expression.
		 */
	public abstract VariableType getType(ConstantInfo cpool[]);
	
	public void setType(VariableType vt)
	{
		this.outputType = vt;
	}
}
