package com.sri.java.expression;

import com.sri.java.types.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;

/**
 * A boolean expression.
 */
public class BooleanExpression extends BinaryExpression
{
    public final static int OR =  0;				// a || b
    public final static int AND = 1;				// a && b
    public final static int LE =  2;				// a <= b
    public final static int LT =  3;				// a < b
    public final static int GE =  4;				// a >= b
    public final static int GT =  5;				// a > b
    public final static int NE =  6;				// a != b
    public final static int EQ =  7;				// a == b
    public final static int INSTANCE_OF = 8;		// a instanceof b
	
        /**
         * Constructor.
         */
	public BooleanExpression(int opType,Object fst,ValuedExpression snd)
	{
		if (opType >= OR && opType <= INSTANCE_OF)
		{
			this.opType = opType;
			left = fst;
			right = snd;
            return ;
		}
        throw new IllegalArgumentException("Invalid operand type");
	}
	
		/**
		 * Gets the type of the expression...
		 */
	public VariableType getType()
	{
		outputType = BasicType.BOOLEAN_TYPE;
		return (VariableType)outputType;
	}
}
