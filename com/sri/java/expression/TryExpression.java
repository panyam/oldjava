package com.sri.java.expression;

import java.io.*;
import java.util.*;
import com.sri.java.*;
import com.sri.java.utils.*;

/**
 * An expression for try and catch statements.
 */
public class TryExpression extends VoidExpression
{
	Expression tryExpr = null;			// expression for the try block
	Vector catchExprs = new Vector();	// a whole list of catch statements...
	Vector catchTypes = new Vector();	// the exception name thrown.
	Expression finallyExpr = null;		// expression for the finally ...
	
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
		dout.writeBytes(front + "try\n");
		dout.writeBytes(front + "{\n");
		tryExpr.print(dout,level + 1);
		dout.writeBytes(front + "}");
		
		for (int i = 0;i < catchTypes.size();i++)
		{
			Expression ct = (Expression)catchTypes.elementAt(i);
			Expression catchExpr = (Expression)catchExprs.elementAt(i);
			dout.writeBytes(front + "} catch (");
			ct.print(dout);
			dout.writeBytes(") {\n");
			catchExpr.print(dout,level + 1);
		}
		dout.writeBytes(front + "} finally {\n");
		finallyExpr.print(dout,level + 1);
		dout.writeBytes(front + "}\n");
	}
}
