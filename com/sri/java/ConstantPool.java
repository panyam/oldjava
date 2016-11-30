package com.sri.java;

import java.io.*;
import java.util.*;
import com.sri.java.utils.*;

public class ConstantPool
{
        /**
         * The list of constants.
         */
    protected Vector constants = new Vector();

        /**
         * Constructor.
         */
    public ConstantPool()
    {
    }

        /**
         * Add a new constant.
         */
    public synchronized int addConstant(ConstantInfo constant)
    {
        constants.addElement(constant);
        return constants.size() - 1;
    }

        /**
         * Get a given constant.
         */
    public ConstantInfo get(int i)
    {
        return (ConstantInfo)constants.elementAt(i);
    }

        /**
         * Set the number of constants available
         * in this class.
         */
    public void ensureConstantPoolSize(int nConstants)
    {
        constants.ensureCapacity(nConstants);
    }

        /**
         * Get the number of entries in the table.
         */
    public int size()
    {
        return constants.size();
    }

        /**
         * Clear all the elements.
         */
    public void clear()
    {
        constants.removeAllElements();
    }
}
