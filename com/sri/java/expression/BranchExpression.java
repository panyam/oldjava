package com.sri.java.expression;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.utils.*;

/**
 * A branch statement class.
 * eg if (elseif *) (else)
 * eg for, while and doWhile...
 */
public class BranchExpression extends VoidExpression
{
    Expression cond = null;       // the condition for the if
    Expression ifExpr = null;       // the statement for the if
    Expression elseExpr = null;     // the statement for the else

    public BranchExpression()
    {
    }
	
		/**

		 * Given a range for the expressions, 

		 */
	public BranchExpression(byte code[],int from,int to)
	{
	}
	
		/**

		 * Print this expression.

		 * 

		 * So print the left expression and then the

		 * operator and then the right expression.

		 */
	public void print(OutputStream out) throws IOException
	{
		print(out,0);
	}
	
		/**

		 * Prints the expression to a given indentation level.

		 */
	public void print(OutputStream out,int level) throws IOException
	{
		String front = Formats.getSpacingString(level);

		DataOutputStream dout = (out instanceof DataOutputStream ? 
                                        (DataOutputStream)out : 
                                        new DataOutputStream(out));

		dout.writeBytes(front + "if (");
		cond.print(out);
		dout.writeBytes(")\n");
		dout.writeBytes(front + "{\n");
		if (ifExpr != null) ifExpr.print(out,level + 1);
		if (!(ifExpr instanceof ExpressionList)) dout.writeBytes(";\n");
		dout.writeBytes(front + "} else {\n");
		elseExpr.print(out,level + 1);
		dout.writeBytes(front + "}\n");
	}
}
