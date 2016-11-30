package com.sri.java.expression;

import java.io.*;
import java.util.*;
import com.sri.java.*;
import com.sri.java.utils.*;

/**
 * This class represents a type of expression that itself is 
 * a list of expression.
 */
public class ExpressionList extends VoidExpression
{
        /**
         * The next item in the list.
         */
    public ExpressionList next;

	public ExpressionList()
	{
		super();
	}
	
		/**
		 * Prints the list of expressions.
		 */
	public void print(OutputStream out) throws IOException
	{
		print(out,0);
	}
	
		/**
		 * Prints this expression list with the given indentation.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
		String front = Formats.getSpacingString(level);
		DataOutputStream dout = new DataOutputStream(out);
		/*for (int i = 0;i < exprs.size();i++)
		{
			dout.writeBytes(front);
			((Expression)exprs.elementAt(i)).print(out,level);
			dout.writeBytes("\n");
		}*/
	}
}

