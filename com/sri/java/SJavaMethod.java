package com.sri.java;

import java.util.*;
import java.io.*;
import com.sri.java.types.*;
import com.sri.java.utils.*;
import com.sri.java.bytecode.*;
import com.sri.java.attribute.*;
import com.sri.java.expression.*;

    /**
     * A Method entry in the methods table
     */
public class SJavaMethod extends SJavaAccessFlags
{
        /**
         * The access flags.
         */
    //public SJavaAccessFlags accessFlags = new SJavaAccessFlags();

        /**
         * The class that holds this method.
         */
    protected SJavaClass parentClass = null;

        /**
         * Index into the constant pool table that
         * holds name for this field.
         */
    protected short nameIndex = 0;

        /**
         * The value of the descriptor index item.  This must be
         * a valid index into the constant pool table.
         */
    protected short descriptorIndex = 0;

        /**
         * The param and return types for this method.
         */
    public MethodType mType = null;

        /**
         * Number of attributes.
         */
    protected short attributeCount = 0;

        /**
         * List of attributes.
         */
    protected Attribute attributes[];
    protected boolean isConstructor = false;
    protected boolean isStaticInitialiser = false;
	
        /**
         * The attribute for the code.
         */
	protected CodeAttribute codeAttr = null;
	
        /**
         * The exception attribute.
         */
	protected ExceptionAttribute exceptionAttr = null;
	
		/**
		 * The attribute reader that reads the attributes
		 * for all field infos
		 */
	protected static AttributeReader aReader = new AttributeReader();

        /**
         * Default constructor.
         */
    protected SJavaMethod()
    {
    }

        /**
         * Constructor.
         * parentClass denotes the class that holds this method.
         * nameIndex holds the index into the constantpool which
         * contains the name of this method.
         */
    public SJavaMethod(SJavaClass parentClass, int nameIndex)
    {
        this.parentClass = parentClass;
        this.nameIndex = nameIndex;
    }

        /**
         * Writes the method to the outputstream.
         */
    public void writeOutputStream(DataOutputStream dout)
        throws Exception
    {
    }

        /**
         * Generates the bytecodefor this expression.
         */
    public byte[] generateCode(Expression exp)
    {
    }
	
        /**
         * Read information from the input stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
        ConstantPool cpool = parentClass.cpool;

        accessFlags = din.readShort();
        nameIndex = din.readShort();
		String name = cpool.get(nameIndex).getString();
		
		isConstructor = name.equalsIgnoreCase("<init>");
		isStaticInitialiser = name.equalsIgnoreCase("<clinit>");
		
        descriptorIndex = din.readShort();
		char chars[] = cpool.get(descriptorIndex).getString().toCharArray();
		mType = (MethodType)TypeParser.evaluateType(chars,0,chars.length - 1);
        attributeCount = din.readShort();
        int total = 8;
		codeAttr = null;
		exceptionAttr = null;
        if (attributeCount > 0)
        {
            attributes = new Attribute[attributeCount];
            for (int i = 0;i < attributeCount;i++)
			{
                attributes[i] = aReader.readAttribute(din,cpool, this);
				if (attributes[i] instanceof CodeAttribute)
				{
					codeAttr = (CodeAttribute)attributes[i];
					codeAttr.setMethod(this);
					//codeAttr.print(cpool,2);
				} else if (attributes[i] instanceof ExceptionAttribute)
				{
					exceptionAttr = (ExceptionAttribute)attributes[i];
				}
				total += aReader.getBytesRead();
			}
        }
		return total;
    }

	public void print(ConstantPool cpool,int level)
	{
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";
		System.out.println(front + "Method Name: " + cpool.get(nameIndex).getString());
		System.out.println(front + "Descriptor index: " + cpool.get(descriptorIndex).getString());
		System.out.print(front); print();
		System.out.println(front + "Attribute count: " + attributeCount);
		for (int i = 0;i < attributeCount;i++)
		{
			System.out.println(front + "Attribute " + i + ": ");
			attributes[i].print(cpool,level + 1);
		}
	}

    public void generateSource(ConstantPool cpool,DataOutputStream dout,int level,String className) throws IOException
	{
		String l = Formats.getSpacingString(level);
        String l2 = Formats.getSpacingString(level + 1);
		dout.writeBytes(l);
		if (isPublic()) dout.writeBytes("public ");
		if (isPrivate()) dout.writeBytes("private ");
        if (isProtected()) dout.writeBytes("protected ");
		if (isSynchronized()) dout.writeBytes("synchronized ");
		if (isFinal()) dout.writeBytes("final ");
		if (isStatic()) dout.writeBytes("static ");
		if (isNative()) dout.writeBytes("native ");
        String name = cpool.get(nameIndex).getString();
        if (name.equalsIgnoreCase("<init>"))
        {    // means we have constructor so dont print return type
            dout.writeBytes(className + "(");
			mType.printParams(dout);
			dout.writeBytes(")");
		} else if (name.equalsIgnoreCase("<clinit>"))
        {            //
			dout.writeBytes(l);
		} else
        {
			mType.printReturnType(dout);
			dout.writeBytes(" " + name + "(");
            mType.printParams(dout);
			dout.writeBytes(")");
		}
                        // now generate a list of exceptions that the method may throw.
		if (exceptionAttr != null)        {
            String thr = " throws ";
            int ne = exceptionAttr.numExceptions;
            for (int i = 0;i < ne;i++)
            {
				int classInd = cpool.get(exceptionAttr.exceptionTable[i]).getClassIndex();                
				String exname = Formats.fullyQualified2Original(cpool.get(classInd).getString(),false);
				thr += exname;
				if (i != ne - 1) thr += ", ";
            }
            dout.writeBytes(thr);
		}
                // now process the code attribute and the actual java source
                // code from the code bytes....  this is the toughest part...
		if (isAbstract() || isNative()) dout.writeBytes(";");
        else
        {
            dout.writeBytes("\n");
            dout.writeBytes(l + "{\n");
            dout.writeBytes(l + "}\n");
		}
	}
}
