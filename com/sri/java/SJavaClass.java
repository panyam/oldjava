package com.sri.java;

import java.util.*;
import java.io.*;
import com.sri.java.bytecode.*;
import com.sri.java.attribute.*;
import com.sri.java.utils.*;
    /**
     * A class that represents a class.
     */
public class SJavaClass extends SJavaAccessFlags
{
        /**
         * The attribute reader that reads the attributes
         * for all class infos.
         */
    protected static AttributeReader aReader = new AttributeReader();
        
        /**
         * A list of constants.
         */
    public ConstantPool cpool = new ConstantPool();

        /**
         * A list of methods.
         */
    protected SJavaMethod []methods = null;
    int nMethods = 0;

        /**
         * A list for the fields.
         */
    protected SJavaField []fields = null;
    int nFields = 0;

        /**
         * A list for all the attributes.
         */
    protected Attribute []attributes = null;
    int nAttributes = 0;

        /**
         * Version numbers.
         */
    protected short minorVersion = 0;
    protected short majorVersion = 0;
    
    protected String className = "";
    
    protected String superClassName = "";

        /**
         * Tells all the packages that are used.
         */
    protected Hashtable classesUsed = new Hashtable();
                                                    
        /**
         * THe flags describing this class's access
         * properties. eg public, interface and so on.
         */
    //public SJavaAccessFlags accessFlags = new SJavaAccessFlags();

        /**
         * THis class.
         */
    protected short thisClass = 0;

        /**
         * Super class.
         */
    protected short superClass = 0;

        /**
         * All the interfaces implemented.
         */
    protected short interfaces[] = null;
    protected int nInterfaces = 0;

        /**
         * Constructor.
         */
    protected SJavaClass()
    {
    }

        /**
         * Constructor.
         */
    public SJavaClass(String name)
    {
        this.className = name;
    }

        /**
         * REturn the constant pool.
         */
    public ConstantPool getConstantPool()
    {
        return cpool;
    }

        /**
         * Set the name of the parent class.
         */
    public void setSuperClass(String parentName)
    {
        this.superClassName = parentName;
    }

        /**
         * Set the minor version.
         */
    public void setMinorVersion(short minV)
    {
        this.minorVersion = minV;
    }

        /**
         * Set the major version.
         */
    public void setMajorVersion(short majV)
    {
        this.majorVersion = majV;
    }

        /**
         * Set the interface count.
         */
    public void ensureInterfaceTableSize(int nInterfaces)
    {
        System.out.println("NM = " + nInterfaces);
        if (interfaces == null)
        {
            interfaces = new short[nInterfaces];
        } else if (interfaces.length < nInterfaces)
        {
            short i2[] = interfaces;
            interfaces = null;
            interfaces = new short[nInterfaces];
            System.arraycopy(i2, 0, interfaces, 0, i2.length);
        }
    }

        /**
         * Set the number of attributes in this class.
         */
    public void ensureAttributeTableSize(int nAttributes)
    {
        System.out.println("NAttr = " + nAttributes);
        if (attributes == null)
        {
            attributes = new Attribute[nAttributes];
        } else if (attributes.length < nAttributes)
        {
            Attribute a2[] = attributes;
            attributes = null;
            attributes = new Attribute[nAttributes];
            System.arraycopy(a2, 0, attributes, 0, a2.length);
        }
    }

        /**
         * Set the number of methods in this class.
         */
    public void ensureMethodTableSize(int nMethods)
    {
        System.out.println("NM = " + nMethods);
        if (methods == null)
        {
            methods = new SJavaMethod[nMethods];
        } else if (methods.length < nMethods)
        {
            SJavaMethod m2[] = methods;
            methods = null;
            methods = new SJavaMethod[nMethods];
            System.arraycopy(m2, 0, methods, 0, m2.length);
        }
    }

        /**
         * Add a new method.
         */
    public void addMethod(SJavaMethod method)
    {
        ensureMethodTableSize(nMethods + 1);
        methods[nMethods++] = method;
    }

        /**
         * Add a new field.
         */
    public void addField(SJavaField fi)
    {
        ensureFieldTableSize(nFields + 1);
        fields[nFields++] = fi;
    }

        /**
         * Add a new interface.
         */
    public void addInterface(short iface)
    {
        ensureInterfaceTableSize(nInterfaces + 1);
        interfaces[nInterfaces++] = iface;
    }

        /**
         * Add a new attribute.
         */
    public void addAttribute(Attribute attrib)
    {
        ensureAttributeTableSize(nAttributes + 1);
        attributes[nAttributes++] = attrib;
    }

        /**
         * Set the number of fields.
         */
    public void ensureFieldTableSize(int nFields)
    {
        System.out.println("NF = " + nFields);
        if (fields == null)
        {
            fields = new SJavaField[nFields];
        } else if (fields.length < nFields)
        {
            SJavaField f2[] = fields;
            fields = null;
            fields = new SJavaField[nFields];
            System.arraycopy(f2, 0, fields, 0, f2.length);
        }
    }
    
        /**
         * Writes the class to an output stream.
         */
    public void writeOutputStream(DataOutputStream dout) throws Exception
    {
    }

        /**
         * Reads the info about this class from an input stream object.
         */
    public void readInputStream(DataInputStream din) throws IOException
    {
        int magic = din.readInt();
        
        setMinorVersion(din.readShort());
        setMajorVersion(din.readShort());

        this.nFields = 
        this.nInterfaces = 
        this.nMethods = 
        this.nAttributes = 0;
        
        int nConstants = din.readShort();

                /***  Add the constants         ***/
        int total = 10;
        try
        {
            cpool.addConstant(null);
            for (int i = 1;i < nConstants;i++)
            {
                ConstantInfo ci = new ConstantInfo();
                total += ci.readInputStream(din);
                cpool.addConstant(ci);
            }
        } catch (Exception e)
        {
            System.out.println("Constants.size = " + cpool.size());
        }

                /***  Read extra info           ***/
        accessFlags = din.readShort();
        thisClass = din.readShort();
        superClass = din.readShort();
        
                /***  Add the interfaces.       ***/
        int nInterfaces = din.readShort();
        ensureInterfaceTableSize(nInterfaces);
        for (int i = 0;i < nInterfaces;i++) addInterface(din.readShort());
        total += 10 + (interfaces.length * 2);

                /***        Add the fields      ***/
        int nFields = din.readShort();
        ensureFieldTableSize(nFields);
        for (int i = 0;i < nFields;i++)
        {
            SJavaField fi = new SJavaField();
            total += fi.readInputStream(din);
            addField(fi);
        }
        total += 2;

                /***        Read and add the methods    ***/
        int nMethods = din.readShort();
        ensureMethodTableSize(nMethods);
        for (int i = 0;i < nMethods;i++)
        {
            SJavaMethod mi = new SJavaMethod(this);
            System.out.println("Mi = " + i);
            total += mi.readInputStream(din);
            addMethod(mi);
        }

                /***        Read the attributes.        ***/
        int nAttributes = din.readShort();
        ensureAttributeTableSize(nAttributes);
        total += 2;
        for (int i = 0;i < nAttributes;i++) 
        {
            addAttribute(aReader.readAttribute(din,cpool,null));
            total += aReader.getBytesRead();
        }
        
        findPackagesImported();
        
            // doInitialisations...
        int nameInd = cpool.get(thisClass).getClassIndex();
        className = cpool.get(nameInd).getString();
        nameInd = cpool.get(superClass).getClassIndex();
        superClassName = cpool.get(nameInd).getString();
    }
    
        /**
         * Print all class file information.
         */
    public void print()
    {
        //printInfo();
        //printInterfaces();
        //printSJavaField();
        printSJavaMethod();
        //printAttributeInfo();
        //printPackagesUsed();
    }

        /**
         * Prints all the packages that are imported.
         */
    public void printPackagesUsed()
    {
        System.out.println("Packages Used:");
        for (Enumeration e = classesUsed.elements();e.hasMoreElements();)
        {
            String s = (String)e.nextElement();
            System.out.println("    " + s);
        }
    }
    
        /**
         * Finds all the packages used in this source file.
         */
    public void findPackagesImported()
    {
        this.classesUsed.clear();
        for (int i = 1;i < cpool.size();i++)
        {
            ConstantInfo ci = cpool.get(i);
            if (ci.tag == ConstantInfo.CONSTANT_CLASS)
            {
                ConstantInfo target = cpool.get(ci.getClassIndex());
                String className = target.getString();

                    // at this point each className is a fully qualified one.
                    // ie it has the info regarding which package
                    // it belongs to.  
                    // So a name like java/lang/system would mean
                    // the package is java.lang;
                    // so when we generate the .java file, 
                    // we need to add the line "import java.lang.*;
                    // if the fullyqualified class name is just abc;
                    // it means abc is in the same packages as the
                    // the class that is being decompiled.  which means
                    // it does not need to be explicitly imported.
                int slashInd = className.lastIndexOf('/');
                if (slashInd > 0)
                {
                        // get every thing upto the last slash...
                    String packageName = Formats.fullyQualified2Original(className.substring(0,slashInd),true);
                    if (!classesUsed.containsKey(packageName))
                    {
                        classesUsed.put(packageName,packageName);
                    }
                }
            }
        }
    }
    public void printInfo()
    {
        System.out.println("Version = " + majorVersion + "." + minorVersion);
        print();
        System.out.println("Const pool size = " + (cpool.size() - 1));
    }

        /**
         * Prints information about all the methods.
         */
    public void printInterfaces()
    {
        System.out.println("# Interfaces Implemented = " + interfaces.length);
        for (int i = 0;i < interfaces.length;i++)
        {
            int ind  = cpool.get(interfaces[i]).getClassIndex();
            String intName = cpool.get(ind).getString();
            System.out.println("Interface " + i + ": " + intName);
        }
    }
    
        /**
         * Prints information about all the methods.
         */
    public void printSJavaMethod()
    {
        System.out.println("# methods : " + methods.length);
        for (int i = 0;i < methods.length;i++)
        {
            System.out.println("Method " + i + ": ");
            methods[i].print(cpool,1);
        }
    }
    
        /**
         * Prints information about all the fields.
         */
    public void printSJavaField()
    {
        System.out.println("# fields : " + fields.length);
        for (int i = 0;i < fields.length;i++)
        {
            System.out.println("Field " + i + ": ");
            fields[i].print(cpool,1);
        }
    }

        /**
         * Prints information about all the attributes.
         */
    public void printAttributeInfo()
    {
        System.out.println("# Attributes : " + attributes.length);
        for (int i = 0;i < attributes.length;i++)
        {
            System.out.println("Attribute " + i + ": ");
            attributes[i].print(cpool,1);
        }
    }
    
        /**
         * Writes the contents to an output stream.
         */
    public void generateSourceFile(DataOutputStream dout) throws IOException
    {
                // first we write all the import commands....
        generateImportStatements(dout);
        
                // now generate the inital lines ie class abc extends so on....
        generateInitial(dout);

                // generate info about all the fields....
        generateFieldCode(dout);
        
            // now we go about generating all the methods
        generateMethodCode(dout);
        dout.writeBytes("}");        // for the generateInitial(dout)
    }

        /**
         * Generate the code for methods.
         */
    public void generateMethodCode(DataOutputStream dout) throws IOException
    {
        dout.writeBytes("\n");
        for (int i = 0;i < methods.length;i++)
        {
            SJavaMethod mi = methods[i];
            mi.generateSource(cpool,dout,1,className);
            dout.writeBytes("\n");
        }
    }

        /**
         * Generate the code for fields.
         */
    public void generateFieldCode(DataOutputStream dout) throws IOException
    {
        dout.writeBytes("\n");
        for (int i = 0;i < fields.length;i++)
        {
            SJavaField fi = fields[i];
            fi.generateSource(cpool,dout,1);
            dout.writeBytes("\n\n");
        }
    }

        /**
         * Generates the import statements from the class file.
         */
    public void generateImportStatements(DataOutputStream dout) 
                                                throws IOException
    {
        dout.writeBytes("\n");
        for (Enumeration e = classesUsed.elements();e.hasMoreElements();)
        {
            String s = (String)e.nextElement();
            dout.writeBytes("import " + s + ".*;\n");
        }
        dout.writeBytes("\n");
    }

        /**
         * Generates the initial "public class ..." statements
         */
    public void generateInitial(DataOutputStream dout) throws IOException
    {
        int len = 7;        // for "class ";
        if (isPublic())         dout.writeBytes("public ");
        if (isProtected())      dout.writeBytes("protected ");
        if (isFinal())          dout.writeBytes("final ");
        if (isVolatile())       dout.writeBytes("volatile ");
        if (isStatic())         dout.writeBytes("static ");
        if (isTransient())      dout.writeBytes("transient ");
        if (isPrivate())        dout.writeBytes("private ");

        dout.writeBytes("class ");
        int nameInd = cpool.get(thisClass).getClassIndex();
        String name = cpool.get(nameInd).getString();
        dout.writeBytes(name + " "); len += name.length();

        String spaces = "";
        for (int i = 0;i < len;i++)
        {
            spaces += " ";
        }
        
            // show super class name...
        if (superClass > 0)
        {
            dout.writeBytes("extends " + superClassName + "\n");
        }
            
            // show interfaces implemented
        if (interfaces != null && interfaces.length > 0)
        {
            dout.writeBytes(spaces + "implements \n");
            for (int i = 0;i < interfaces.length;i++)
            {
                int inameInd = cpool.get(interfaces[i]).getClassIndex();
                String iname = Formats.fullyQualified2Original(cpool.get(inameInd).getString(),false);
                dout.writeBytes(spaces + "    " + iname);
                if (i != interfaces.length - 1) dout.writeBytes(",");
                dout.writeBytes("\n");
            }
        }
        dout.writeBytes("{");
    }
}
