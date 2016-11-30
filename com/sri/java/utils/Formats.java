package com.sri.java.utils;

import java.util.*;

public class Formats
{
		/**
		 * A table that holds the initial spacing that
		 * is required for various levels of indentation
		 * assuming each level needs 4 spcaes.
		 */
	protected static Vector initialSpacing = new Vector();
    public static String spacingString = "    ";

	static
	{
		String out = "";
		initialSpacing.addElement(out);
		for (int i = 0;i < 32;i++)
		{
			String n = (out += spacingString);
			initialSpacing.addElement(out);
		}
	}
	
		/**
		 * Returns the spacing string.
		 */
	public static final String getSpacingString(int level)
	{
		return (String)initialSpacing.elementAt(level);
	}
	
		/**
		 * Converts a fully qualified class name into its original form.
		 * So java/lang/Object would become java.lang.Object.
		 * The java.lang part can be rejected by using false for 
		 * the withPackage parameter.
		 */
	public static String fullyQualified2Original(String name,boolean withPackage)
	{
		String out = "";
		StringTokenizer tokens = new StringTokenizer(name,"/",false);
		while (tokens.hasMoreTokens())
		{
			String n = tokens.nextToken();
			if (!tokens.hasMoreTokens() && !withPackage) return n;
			out += n;
			if (tokens.hasMoreTokens()) out += ".";
		}
		return out;
	}
}
