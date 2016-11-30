package com.sri.java;

import java.util.*;
import com.sri.java.types.*;
import com.sri.java.expression.*;

/**
 * This class represents information about a local variable.
 */
public class SJavaLocalVariable
{
		/**
		 * The name of this local variable.
		 * 
		 * Initially the name is undefined
		 */
	public String lvName = null;
	
		/**
		 * This is true if any more types should be added.
		 * 
		 * We may not want to add any more types if we are
		 * absolutely certain about the type.  For example,
		 * if we know a variable is a boolean for sure
		 * then we dont have to add any ints or short
		 * types even if we encounter them.  So now that
		 * we know about the type, we can even modify
		 * our expression suit this type.
		 */
	public boolean moreTypesAddable = true;
	
		/**
		 * The expression for this identifier.
		 */
	public IdentifierExpression identExp = null;
	
	public SJavaLocalVariable()
	{
	}
	
		/**
		 * Creates a new lv with the given name...
		 */
	public SJavaLocalVariable(String name)
	{
		this.lvName = name;
		identExp = new IdentifierExpression();
		identExp.outputType = null;
	}
	
		/**
		 * A list of possible types.
		 */
	public Hashtable possibleTypes = new Hashtable();
	
	protected final static String prefixes[] = { "lv_i_", "lv_l_", "lv_f_", "lv_d_", "lv_a_" };
	public final static int INT_PREFIX = 0;
	public final static int LONG_PREFIX = 1;
	public final static int FLOAT_PREFIX = 2;
	public final static int DOUBLE_PREFIX = 3;
	public final static int REF_PREFIX = 4;
	
	public static String getSampleName(int type, int num)
	{
		if (type < INT_PREFIX || type > REF_PREFIX)
		{
			throw new IllegalArgumentException("Invalid prefix type: " + type + ".");
		}
		return prefixes[type] + num;
	}
	
		/**
		 * Adds a new type to our table.
		 */
	public void addType(VariableType type)
	{
				// add this type only if it does not already exist...
		if (!possibleTypes.containsKey(type)) possibleTypes.put(type,type);
	}
	
	public void removeAllTypes()
	{
		possibleTypes.clear();
	}
}
