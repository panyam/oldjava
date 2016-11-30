package com.sri.java.types;

import java.io.*;
import java.util.*;

public abstract class VariableType extends Type
{
    public static Hashtable predefinedTypes = new Hashtable();
                                                            
    public abstract String getSampleName(int id);
    public abstract String getBaseName();       // gets the baseName eg String would be string
    public abstract String getTypeName();       // the name of th etype eg int would be int 
                                                // and String would be String

    public short nDim = 0;                      // the number of dimensions in this array
    
        /**
         * Prints this type.
         */
    public void print(OutputStream out) throws IOException
    {
        print(out,0);
    }
    
        /**
         * Prints this type.
         */
    public void print(OutputStream out,int level) throws IOException
    {
        out.write(getTypeName().getBytes());
    }
}
