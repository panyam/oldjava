package com.sri.java.expression;

import com.sri.java.*;
import java.io.*;
import com.sri.java.utils.*;
import com.sri.java.types.*;
import com.sri.java.bytecode.*;

/**
 * A function call expression class
 * a(3,4)
 * v() and so on.
 */
public class FunctionExpression extends ValuedExpression
{
	protected final static byte []COMA = { (byte)',' };
	protected final static byte []OPEN_ROUND = { (byte)'(' };
	protected final static byte []CLOSE_ROUND = { (byte)')' };
	
	protected final static String NEW = "new ";
	
    boolean isConstructor = false;  // tells if the function is a  
                                    // constructor, in which case
                                    // a new key word is needed too...

	String name = "";               // the name of the function...
	int nParams = 0;
	ValuedExpression params[];		// the parameters to a function parameter.
    ValuedExpression target;        // the expression onto which the function is 
                                    // is applied. eg in object.clone(), the
                                    // param list is null, target is object
                                    // nad name is clone...
		/**
		 * Constructor.
		 */
	public FunctionExpression ()
	{
	}
	
		/**
		 * Initialises this expression.
		 */
	public void create(boolean isCons,
                       ValuedExpression target,
                       ValuedExpression params[],
					   MethodType mType)
	{
		nParams = params.length;
        this.isConstructor = isCons;
        this.target = target;
		this.params = params;
		this.outputType = mType;
	}
	
		/**
		 * Returns the type of this function call.
		 */
	public VariableType getType(ConstantInfo []cpool)
	{
		return ((MethodType)outputType).returnType;
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out) throws IOException
	{
	}
	
		/**
		 * Prints this expression.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
		out.write(Formats.getSpacingString(level).getBytes());
		print(out);
	}

        /**
         * Gets the output type of this
         * function.
         */
    public VariableType getType()
    {
        return null;
    }
}
