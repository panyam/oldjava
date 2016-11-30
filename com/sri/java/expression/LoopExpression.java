package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.utils.*;

/**
 * A loop statement class.
 * eg for, while and doWhile...
 */
public class LoopExpression extends VoidExpression
{
	public final static byte FOR_LOOP = 0;
	public final static byte DO_WHILE_LOOP = 1;
	public final static byte WHILE_LOOP = 2;
	
    Expression initial   = null;        // the inital action before the loop.
    Expression condition = null;        // the condition for the loop
	Expression action	 = null;		// The action.  Only for for loops
    Expression expr      = null;        // the expression within the loop

	byte type = FOR_LOOP;
	
    public LoopExpression()
    {
    }
	
		/**

		 * Initialises this expression.

		 */
	public void create(byte type,Expression init,Expression cond, Expression action, Expression expr)
	{
		this.type = type;
		this.initial = init;
		this.condition = cond;
		this.expr = expr;
		this.action = action;
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
		String front = Formats.getSpacingString(level);
		DataOutputStream dout = (out instanceof DataOutputStream ? (DataOutputStream)out : 
																   new DataOutputStream(out));
		dout.writeBytes(front);
		if (type == DO_WHILE_LOOP)
		{
			dout.writeBytes("do\n");
		} else if (type == WHILE_LOOP)
		{
			dout.writeBytes("while (");
			this.condition.print(dout);
			dout.writeBytes(")");
		} else
		{
			dout.writeBytes("for (");
			if (this.initial != null) initial.print(dout);
			dout.writeBytes("; ");
			if (condition != null) condition.print(dout);
			dout.writeBytes("; ");
			if (action != null) action.print(dout);
			dout.writeBytes(")\n");
		}
		dout.writeBytes(front + "{\n");
		this.expr.print(dout,level + 1);
		if (!(expr instanceof ExpressionList)) dout.writeBytes(";");
		if (type == WHILE_LOOP)
		{
			dout.writeBytes(front + "} while (");
			condition.print(dout);
			dout.writeBytes(");");
		} else dout.writeBytes(front + "}\n");
	}
}
