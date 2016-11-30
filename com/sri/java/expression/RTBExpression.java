package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.utils.*;

/**
 * This class if for return throw and break unary expressions.
 */
public class RTBExpression extends VoidExpression
{
    public final static byte RETURN = 0;
    public final static byte THROW = 1;
    public final static byte BREAK = 2;			// break label

    byte type = RETURN ;
    Object value = null;

    public RTBExpression()
    {
        
    }

    public void create(byte type, Object exp)
    {
		if (exp instanceof String || exp instanceof ValuedExpression)
		{
			this.type = type;
			this.value = exp;
		} else throw new IllegalArgumentException("Invalid expression type.");
    }
}
