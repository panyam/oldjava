package com.sri.java.types;

import java.io.*;

/**
 * A type for variables that are types derived
 * from the Object class.
 */
public class ObjectType extends VariableType
{
	String objectName = "";
	String baseName = "";

		/**
		 * This is a static objecttype object for al
		 * variables who type is "Object".
		 * 
		 * This is the same as java.lang.Object.
		 */
	public static final ObjectType OBJECT_TYPE = new ObjectType("Object");
	public static final ObjectType STRING_TYPE = new ObjectType("String");
	
	public ObjectType(String name)
	{
		objectName = name;
        if (!predefinedTypes.containsKey(name)) predefinedTypes.put(name,this);
		baseName = objectName.toLowerCase();
	}
	
	public String getTypeName() { return objectName; } 
	
	public String toString()
	{
		return objectName;
	}
	
	public String getSampleName(int id)
	{
		return baseName + "_" + id;
	}
	
	public String getBaseName()
	{
		return this.baseName;
	}

		/**
		 * Hash key value is based on the object name along
		 * with information regarding the number of dimensions.
		 * Since the number of dimensions is within 0 and 255
		 * inclusive, the name could be "name]ndim".  the ] is compulsory.. 
		 * as it cannot be part of a valid name...  At least a 
		 * non obfuscated type name...
		 */
    public int hashCode()
    {
        return (objectName + "]" + (nDim + 0xff)).hashCode();
    }
}
