package com.sri.java.expression;

import com.sri.java.*;
import com.sri.java.types.*;

/**
 * This is for type declarations...
 * 
 * So each type declaration is like:
 * 
 * basetype ([]*(assignment | IdentifierExp)) (,([]*(assignment | IdentifierExp)))*
 * 
 * However, for the time being we will not worry about
 * the assignment within declaration expressions.
 * Since we are only decompiling, we dont have to worry about
 * it.  So each expression has to be an identifier.
 */
public class TypeDeclExpression extends VoidExpression
{
	VariableType baseType;
	int numDecls = 0;	// the number of declarations made...
	IdentifierExpression idExps[];		// each identifier
	int numDims[];			// the array dimension for each
							// identifier...
	
	public TypeDeclExpression()
	{
	}
	
		/**
		 * Creates this object.
		 */
	public void create(int nDecls,int numDims[],IdentifierExpression ids[])
	{
		this.numDecls = nDecls;
		this.idExps = ids;
		this.numDims = numDims;
		if (numDims.length != ids.length)
		{
			throw new IllegalArgumentException("The length of the dimension " +
											   "array must equal the number " + 
											   "of identifiers");
		}
	}
}
