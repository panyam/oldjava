package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;

/**
 * A statement class.
 */
public abstract class Expression
{
        /**
         * The next expression.
         */
    public Expression next = null;

        /**
         * Generate the bytecode for this expression.
         * Parameters are:
         *
         * lvStart  -   The first local variable that can be used.
         *              We need this because, we can reuse local
         *              variables by taking advantage of scope
         *              of the variables.
         *
         * cpool    -   The constant pool table that we will make 
         *              use of.  The pool table will also be updated
         *              as we go on.
         *
         * This funciton should be called by a "SJavaMethod" class 
         * on its expression object.
         */
    public void generateCode(int lvStart, ConstantPool cpool)
    {
    }

		/**
		 * Prints the expression.
		 */
	public void print(DataOutputStream dout,int level) throws IOException 
	{
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out) throws IOException
    {
    }
	
		/**
		 * Prints this level after printing the
		 * appropriate indentation.
		 */
	public void print(OutputStream out,int level) throws IOException
    {
    }
}
