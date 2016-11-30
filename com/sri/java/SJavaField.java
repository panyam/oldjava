package com.sri.java;

import java.util.*;
import java.io.*;
import com.sri.java.utils.*;
import com.sri.java.types.*;
import com.sri.java.attribute.*;
import com.sri.java.bytecode.*;
    /**
     * A field entry in the field table.
     */
public class SJavaField extends SJavaAccessFlags
{
        /**
         * The access flags.
         */
    //protected SJavaAccessFlags accessFlags = new SJavaAccessFlags();

        /**
         * The parent class.
         */
    protected SJavaClass parentClass = null;

        /**
         * Index into the constant pool table that
         * holds name for this field.
         */
    protected short nameIndex = 0;

		/**

		 * The type of this field.

		 */
	protected VariableType fieldType = null;

        /**
         * Number of attributes.
         */
    protected short attributeCount = 0;
        
        /**
         * List of attributes.
         */
    protected Attribute attributes[];
	
		/**
		 * The attribute reader that reads the attributes
		 * for all field infos
		 */
	protected static AttributeReader aReader = new AttributeReader();

		/**
		 * Constructor.
		 */
    protected SJavaField()
    {
    }

        /**
         * Constructor.
         */
    public SJavaField(SJavaClass parentClass)
    {
        this.parentClass = parentClass;
    }

        /**
         * Read information from the input stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
        ConstantPool cpool = parentClass.cpool;

        accessFlags = din.readShort();
        nameIndex = din.readShort();
        short desInd = din.readShort();
		char chars[] = cpool.get(desInd).getString().toCharArray();
		fieldType = (VariableType)TypeParser.evaluateType(chars,0,chars.length - 1);
        attributeCount = din.readShort();
		int total = 8;

        if (attributeCount >0)
        {
            attributes = new Attribute[attributeCount];
            for (int i = 0;i < attributeCount;i++) 
			{
				attributes[i]=aReader.readAttribute(din,cpool,this);
				total += aReader.getBytesRead();
			}
        }
		return total;
    }

	public void generateSource(ConstantPool cpool,DataOutputStream dout,int level) throws IOException
	{
		dout.writeBytes(Formats.getSpacingString(level));
		if (isPublic())         dout.writeBytes("public ");
		if (isProtected())      dout.writeBytes("protected ");
		if (isFinal())          dout.writeBytes("final ");
		if (isVolatile())       dout.writeBytes("volatile ");
		if (isStatic())         dout.writeBytes("static ");
		if (isTransient())      dout.writeBytes("transient ");
		if (isPrivate())        dout.writeBytes("private ");
		
		String name = cpool.get(nameIndex).getString();
		
		fieldType.print(dout);
		dout.writeBytes(" " + name + ";");
	}
	
    public void print(ConstantPool cpool, int level)
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";
		System.out.println(front + "Field Name: " + cpool.get(nameIndex).getString());
		System.out.print(front + "Descriptor: "); 
		try
		{
			fieldType.print(new DataOutputStream(System.out));
		} catch (Exception e) { }
		System.out.print("\n" + front);
		super.print();
		System.out.println(front + "Attribute count: " + attributeCount);
		for (int i = 0;i < attributeCount;i++)
		{
			System.out.println(front + "Attribute " + i + ": ");
			attributes[i].print(cpool,level + 1);
		}
    }
}
