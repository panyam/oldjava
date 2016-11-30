package com.sri.java.types;

public class TypeParser
{
		/**
		 * Given a type descriptor string, returns
		 * the type that is held in the string.
		 */
	public static Type evaluateType(char chars[],int from,int to)
	{
		if (chars[from] == 'b' || chars[from] == 'B') return BasicType.BYTE_TYPE;
		else if (chars[from] == 'c' || chars[from] == 'C') return BasicType.CHAR_TYPE;
		else if (chars[from] == 'd' || chars[from] == 'D') return BasicType.DOUBLE_TYPE;
		else if (chars[from] == 'f' || chars[from] == 'F') return BasicType.FLOAT_TYPE;
		else if (chars[from] == 'i' || chars[from] == 'I') return BasicType.INT_TYPE;
		else if (chars[from] == 'j' || chars[from] == 'J') return BasicType.LONG_TYPE;
		else if (chars[from] == 'v' || chars[from] == 'V') return BasicType.VOID_TYPE;
		else if (chars[from] == 'z' || chars[from] == 'Z') return BasicType.BOOLEAN_TYPE;
		else if (chars[from] == 's' || chars[from] == 'S') return BasicType.SHORT_TYPE;
		else if (chars[from] == 'l' || chars[from] == 'L')
		{
			int t = ++from;
			while (chars[t] != ';' && t <= to) t++;
			return new ObjectType(String.valueOf(chars,from,t - from));
		} 
		else if (chars[from] == '[')
		{
			int nDim = 0;
			for (;chars[from] == '[' && from <= to;from++,nDim++);
			if (from > to)
			{
				throw new IllegalArgumentException("Invalid type descriptor.");
			}
			VariableType t = (VariableType)TypeParser.evaluateType(chars,from,to);
			t.nDim = (byte)nDim;
			return t;//new ArrayType(t,nDim);
		} else if (chars[from] == '(')			// then a method type descriptor
		{
			int f2 = from + 1;
			while (f2 < to && chars[f2] != ')') f2++;
			if (f2 == to) throw new IllegalArgumentException("Invalid type descriptor");
			VariableType outputType = (VariableType)TypeParser.evaluateType(chars,f2 + 1,to);
			MethodType mType = new MethodType();
			mType.returnType = outputType;
			mType.setParamTypes(chars,from + 1,f2 - 1);
			return mType;
		}
		else 
		{
			throw new IllegalArgumentException("Invalid type descriptor: " + new String(chars,from,to - from + 1));
		}
	}
}
