package com.sri.java.attribute;

import com.sri.java.*;
import com.sri.java.utils.*;
import com.sri.java.blocks.*;
import com.sri.java.expression.*;
import com.sri.java.bytecode.*;
import com.sri.java.types.*;
import java.util.*;
import java.io.*;
/**
 * A class that holds a constantvalue attribute.
 */
public class CodeAttribute extends Attribute
{
		/**
		 * The method that contains this code.
		 */
	protected SJavaMethod method = null;
	
		/**
		 * Maximum stack size.
		 */
    protected short maxStack = 0;
	
		/**
		 * Maximum number of locals.
		 */
    protected short maxLocals = 0;
	
		/**
		 * The local variable table.
		 */
	public IdentifierExpression localVars[] = null;
	
		/**
		 * Number of bytes of code.
		 */
    protected int codeLength = 0;
	
		/**
		 * The actual code.
		 */
	protected byte []code = null;
	
		/**
		 * Number of exceptions.
		 */
    protected short numExceptions = 0;
		
		/**
		 * The exception table.
		 */
    protected ExceptionInfo exceptions[];
	
		/**
		 * Number of attributes.
		 */
    protected short numAttributes = 0;
		
		/**
		 * The attribute list.
		 */
    protected Attribute attributes[];

		/**
		 * LineNumber table attribute.
		 */
	public LNTableAttribute lnTable = null;
	
		/**
		 * Local Variable table attribute.
		 */
	public LVTableAttribute lvTable = null;
	
		/**
		 * The generated expression syntax tree for
		 * this block of code.
		 */
	protected Expression expression = new ExpressionList();

	
        /**
         * A instruction table that tells the offset of 
         * the ith instruction.
         */
    public InstructionTable iTable = null;

		/**
		 * The block graph of this code.
		 */
    public BlockGraph bGraph = null;
	
		/**
		 * The attribute reader that reads the attributes
		 * for this attribute.
		 */
	protected AttributeReader aReader = new AttributeReader();

		/**
		 * The type and identifier info about each local
		 * variable.
		 */
	public IdentifierExpression lvExprs[];

        /**
         * Default constructor.
         */
    protected CodeAttribute()
    {
    }

        /**
         * Constructor.
         */
    public CodeAttribute(SJavaClass parentClass)
    {
        super(parentClass);
    }

		/**
		 * Sets the method that contains this code.
		 */
	public void setMethod(SJavaMethod method)
	{
		this.method = method;
	}
	
        /**
         * Return the number of locals in this method.
         */
    public short getMaxLocals()
    {
        return maxLocals;
    }

        /**
         * Return the number of locals in this method.
         */
    public int getCodeLength()
    {
        return codeLength;
    }

        /**
         * Returns the length of the exception table.
         */
    public short getExceptionTableLength()
    {
        return numExceptions;
    }

		/**
		 * From the code bytes, generates a set of
		 * instructions.
		 */
	public void parseCodeBytes()
	{
		/*int i = 0;
		numInstructions = 0;
		codeStart = null;
		codeStart = new int[codeLength];
		for (;i < this.codeLength;)
		{
			int opcode = code[i] & 0xff;
			codeStart[numInstructions++] = i++;
			int incr = OpCode.numParameters[opcode];
			if (opcode == OpCode.LOOKUPSWITCH)
			{
			} else if (opcode == OpCode.TABLESWITCH)
			{
			} else if (opcode == OpCode.WIDE)
			{
				incr = ((code[i] & 0xff) == OpCode.IINC ? 5 : 3);
			}
			i += incr;
		}*/
	}
	
    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

		System.out.println(front + "Attribute Name: " + attribName);
		System.out.println(front + "Stack Size: " + this.maxStack);
		System.out.println(front + "# Locals: " + this.maxLocals);
		for (int i = 0;i < numExceptions;i++)
		{
			//System.out.println(front + "Exception " + i + ": ");
			//exceptions[i].print(constantPoolTable,level + 1);
		}
		for (int i = 0;i < numAttributes;i++)
		{
			System.out.println(front + "Attribute " + i + ": ");
			attributes[i].print(cpool,level + 1);
		}
		try
		{
			DataOutputStream dout = new DataOutputStream(System.out);
			//System.out.println(front + "Code: ");
			//iTable.printInstructions(dout,0,iTable.numInstrs - 1,level + 1);
			System.out.println(front + "Block Graph: ");
			bGraph.print(dout,level);
		} catch (Exception e) { }
		//printCode(constantPoolTable, level + 1);
    }


        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din, ConstantPool cpool,
                                Object params)
        throws IOException
    {
		this.method = (SJavaMethod)params;
        maxStack = din.readShort();
        maxLocals = din.readShort();
		
        codeLength = din.readInt();
		int total = 14 + codeLength + 4;
		code = new byte[codeLength];
		din.readFully(code,0,codeLength);
        iTable = new InstructionTable(this,code,codeLength);
        bGraph = new BlockGraph(this);
        numExceptions = din.readShort();
        exceptions = new ExceptionInfo[numExceptions];
		if (numExceptions > 0)
		{
			for (int i = 0;i < numExceptions;i++)
			{
			    exceptions[i] = new ExceptionInfo();
			    total += exceptions[i].readInputStream(din);
			}
        }
        numAttributes = din.readShort();
        attributes = new Attribute[numAttributes];
        for (int i = 0;i < numAttributes;i++) 
		{
			attributes[i] = aReader.readAttribute(din,cpool,params);
			if (attributes[i] instanceof LNTableAttribute) lnTable = (LNTableAttribute)attributes[i];
			else if (attributes[i] instanceof LVTableAttribute) lvTable = (LVTableAttribute)attributes[i];
			total += aReader.getBytesRead();
		}
		
			// now that the code is read, we also reverse engineer
			// the code bytes and create a syntax tree
			// that closely matches the source code.
			// so we are basically creating one big expression tree.
		//this.expression = ExpressionGenerator.generateExpression(code,0,codeLength - 1,cpool,this);
		
		//setLocalVariables(cpool);
		return total;
    }
	
	protected void setLocalVariables(ConstantPool cpool)
	{
		/*this.lvExprs = new IdentifierExpression[maxLocals];
		
		int loc = 0;
			// the method is not a static method,
			// then set the first variable to "this";
		if (!method.accessFlags.isStatic())
		{
			lvExprs[0] = new IdentifierExpression();
			lvExprs[0].varName = "this";
			loc++;
		}
		
			// check how many parameters are there in this method...
		int np = method.mType.getNumParameters();
		for (int i = 0;i < np;i++)
		{
			lvExprs[loc] = new IdentifierExpression();
			lvExprs[loc].type = (VariableType)method.mType.paramTypes.elementAt(i);
			loc++;
			if (lvExprs[loc].type == BasicType.DOUBLE_TYPE || lvExprs[loc].type == BasicType.LONG_TYPE)
			{
				lvExprs[loc++] = null;
			}
		}
		
			// for the remaining local variables,
			// create identifier expression but 
			// set their names and types to null,
			// so that this would indicate that 
			// these values are still undefined.
			// However, there is an exception.
			// That is if the local variable
			// table attribute is present
			// in which case these values can
			// be set...
		for (int i = loc;i < maxLocals;i++)
		{
			lvExprs[loc] = new IdentifierExpression();
			lvExprs[loc].type = null;
			lvExprs[loc].varName = null;
		}*/
	}
	
        /**
         * Return the maximum stack size for this method.
         */
    public short getMaxStack()
    {
        return maxStack;
    }

		/**
		 * here we go thru all the instructions and create
		 * higher level expressions.  The pre condition is
		 * that the setLocalVariables method must have been 
		 * called.
		 * 
		 * The only unprocessed instruction will be the
		 * goto instruction and other unused instructions.
		 * 
		 * The reason we are doing this is because, by doing
		 * this we can make the value in each instruction block
		 * as expressions rather than instructions.  Thus
		 * we have blocks that contain high level expressions
		 * rather than instructions...
		 * 
		 * Here we also need to take care of exceptions...
		 */
	public void createHighLevelExpressions(ConstantPool cpool)
	{
	}
}

	
class ExceptionInfo
{
    short startPC = 0;
    short endPC = 0;
    short handlerPC = 0;
    short catchType = 0;

        /**
         * Constructor.
         */
    public ExceptionInfo()
    {
    }

        /**
         * Read data from the input stream;
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
        startPC = din.readShort();
        endPC = din.readShort();
        handlerPC = din.readShort();
        catchType = din.readShort();
		return 8;
    }
		
	public void print(Vector constantPoolTable, int level)
	{
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";
		System.out.println(front + "StartPC, endPC, handlerPC, catchType = " + 
						   startPC + ", " + 
						   endPC + ", " + 
						   handlerPC + ", "  +
						   catchType);
	}
}
