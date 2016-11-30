package com.sri.java.types;

import java.util.*;
import java.io.*;

/**
 * A type of method also includes the type of
 * the input parameters.
 */
public class MethodType extends Type
{
	public VariableType returnType = BasicType.VOID_TYPE;	// the return type...
	public Vector paramTypes = new Vector();
	
		/**
		 * Prints this type.
		 */
	public void print(OutputStream out) throws IOException
	{
	}
	
		/**
		 * Prints this type.
		 */
	public void print(OutputStream out,int level) throws IOException
	{
	}
	
	public void printReturnType(DataOutputStream dout) throws IOException
	{
		returnType.print(dout);
	}

		/**
		 * Gets the number of arguments of this method.
		 */
	public int getNumParameters()
	{
		return paramTypes.size();
	}
	
		/**
		 * Prints all the parameters separated by comas except 
		 * the last one...
		 */
	public void printParams(DataOutputStream dout) throws IOException
	{
		int s = paramTypes.size();
		for (int i = 0;i < s;i++) 
		{
			if (i > 0) dout.writeBytes(", ");
			((VariableType)paramTypes.elementAt(i)).print(dout);
		}
	}
	
	public String getSampleName(int id)
	{
		return "";
	}
	
	public String getBaseName()
	{
		return "";
	}
	
	public void setParamTypes(char chars[],int from,int to)
	{
		String basics = "BCDEFIJSZVbcdefijszv";
		paramTypes.removeAllElements();
		for (int i = from;i < to;i++)
		{
			if (basics.indexOf(chars[i]) >= 0) 
			{
				paramTypes.addElement(new BasicType(chars[i]));
			}
			else if (chars[i] == 'l' || chars[i] == 'L')
			{
				int t2 = i + 1;
				while (t2 <= to && chars[t2] != ';') t2++;
				paramTypes.addElement(TypeParser.evaluateType(chars,i,t2 - 1));
				i = t2;
			} else if (chars[i] == '[')
			{
				int t = i;
				while (t <= to && chars[t] == '[') t++;
				paramTypes.addElement(TypeParser.evaluateType(chars,i,to));
				if (basics.indexOf(chars[t]) >= 0)
				{
					i = t;
				} else if (chars[t] == 'l' || chars[t] == 'L')
				{
					while (t <= to && chars[t] != ';') t++;
					i = t;
				}
			} else return ;
		}
	}
	
	public VariableType getArgumentType(int which)
	{
		return (VariableType)paramTypes.elementAt(which);
	}
}
