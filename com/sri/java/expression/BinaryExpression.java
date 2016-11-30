package com.sri.java.expression;

import java.util.*;
import java.io.*;
import com.sri.java.types.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;

/**
 * A branch statement class.
 * eg if (elseif *) (else)
 * eg for, while and doWhile...
 */
public class BinaryExpression extends ValuedExpression
{
    public final static int ASSIGN = 0;				// b = c

    public final static int ADD = 1;				// b + c
    public final static int ADD_EQ = 2;				// b += c
    public final static int EQ_ADD = 3;				// b =+ c

    public final static int SUB = 4;				// b - c
    public final static int SUB_EQ  = 5;			// b -= c
    public final static int EQ_SUB  = 6;			// b =- c

    public final static int MUL = 7;				// b * c
    public final static int MUL_EQ  = 8;			// b *= c

    public final static int DIV = 9;				// b / c
    public final static int DIV_EQ  = 10;			// b /= c

    public final static int MOD = 11;				// b % c
    public final static int MOD_EQ  = 12;			// b %= c
	
    public final static int XOR = 13;				// a ^ b
    public final static int XOR_EQ  = 14;			// a ^= b

    public final static int LSHIFT = 15;			// a << 1
    public final static int LSHIFT_EQ  = 16;		// a <<= 1
    
    public final static int RSHIFT = 17;				// a >> 1
    public final static int RSHIFT_EQ  = 18;			// a >>= 2

    public final static int LOG_OR = 19;				// a | b
    public final static int LOG_OR_EQ  = 20;			// a |= b

    public final static int LOG_AND = 21;			// a & b
    public final static int LOG_AND_EQ  = 22;		// a &= b

	public final static int ARRAY_ACCESS = 23;		// a [ b ];
	public final static int CASTING = 24;			//(int)0xff
	
    public final static int DOT_FIELD = 25;				// point.x;

    int opType = ADD;
    Object left = null;			    // the left part of the operator
									// in the case of an array access
									// this would be the target array
    ValuedExpression right = null;	// the right part of the operator
									// in the case of an array access
									// this would be the index
									// for dot this would be just a	
									// string name.
    public BinaryExpression()
    {
    }
	
	public VariableType getType(ConstantInfo []cpool)
	{
		if (opType == CASTING)
		{
			return (VariableType)left;
		} else
		{
			if (outputType != null) return (VariableType)outputType;
			VariableType lType = ((ValuedExpression)left).getType(cpool);
			VariableType rType = ((ValuedExpression)right).getType(cpool);
			
				// now what are the rules for types... given left and right types...
				// for assign, add, mul, div, sub and mod, take the superior
				// of the two types...  for shift ones, also take the superior one...
			if (lType instanceof BasicType && rType instanceof BasicType)
			{
				int lt = ((BasicType)lType).getType() & 0xff;
				int rt = ((BasicType)rType).getType() & 0xff;
				return (lt < rt ? lType : rType);				
			} else if ((opType == ADD || opType == ADD_EQ || opType == EQ_ADD) &&
					   (lType instanceof ObjectType && rType instanceof ObjectType))
			{
					// then most be of a string type...
				if (((ObjectType)lType).getTypeName().equals("String") &&
					((ObjectType)lType).nDim == 0)
				{
					return lType;
				}
			}
		}
		return null;
	}
	
	public void create(int opType,Object fst,ValuedExpression snd)
	{
		if (opType < 0 || opType > 25) throw new IllegalArgumentException("Invalid operand type.");
        if (!(fst instanceof VariableType) && !(fst instanceof ValuedExpression))
        {
		    throw new IllegalArgumentException("Invalid first parameter type.");
        }
        if (fst instanceof VariableType && opType != CASTING)
        {
		    throw new IllegalArgumentException(
                                "Object type does not match operator type.");
        } else if (fst instanceof ValuedExpression && opType == CASTING)
        {
		    throw new IllegalArgumentException(
                                "Value expression cannot be used for casting.");
        }
		this.opType = opType;
		left = fst;
		right = snd;
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
