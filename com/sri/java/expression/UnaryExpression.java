package com.sri.java.expression;

import java.io.*;
import java.util.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.bytecode.*;

/**
 * A branch statement class.
 * eg if (elseif *) (else)
 * eg for, while and doWhile...
 */
public class UnaryExpression extends ValuedExpression
{
    public final static byte NEG = 1;			// -a
    public final static byte POST_DEC = 2;		// --a
    public final static byte PRE_DEC = 2;		// a--
    public final static byte POST_INC = 3;		// ++a
    public final static byte PRE_INC = 3;		// a++
    public final static byte NOT = 4;			// !a
    public final static byte LOG_NOT = 5;		// ~a

    byte opType = NEG;
    ValuedExpression target = null;		// the expression to which
										// the operator is being applied to.

        /**
         * Default constructor.
         */
    protected UnaryExpression()
    {
    }
	
        /**
         * Constructor.
         */
	public UnaryExpression(byte op, ValuedExpression target)
	{
		opType = op;
		this.target = target;
	}
	
	public VariableType getType(ConstantInfo [] cpool)
	{
		if (opType == NOT) return BasicType.BOOLEAN_TYPE;
		outputType = target.getType(cpool);
		return (VariableType)outputType;
	}
	
		/**
		 * Print this expression.
		 * 
		 * So print the left expression and then the
		 * operator and then the right expression.
		 */
	public void print(OutputStream out) throws IOException
	{
	}
	
		/**
		 * Prints the expression to a given indentation level.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
	}
}
