
package com.sri.apps.brewery.core;

import java.util.*;
import java.io.*;
import javax.swing.tree.*;

import com.sri.apps.brewery.io.*;

/**
 * A set of files that belong to a packge.
 *
 * @author Sri Panyam
 */
public class Files extends InstallUnit
{
        /**
         * Names of all persistable fields.
         */
    protected final static String fieldNames[] = new String[]
    {
        "File List",
        "Destinations"
    };

        /**
         * Types of all persistable fields.
         */
    protected final static PersistableType fieldTypes[] = new PersistableType[]
    {
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.COLLECTION),
        PersistableType.getType(PersistableType.INT_TYPE,
                                PersistableType.ARRAY),
    };

        /**
         * The file names.
         */
    protected List fileList;

        /**
         * Where they are to be copied to.
         */
    protected int dirIndex[] = new int[5];
    protected int nIndices = 0;

        /**
         * Get the idnex of a given file name
         * within our set.
         */
    public int getFileIndex(String fName)
    {
        return -1;
    }

        /**
         * Clear all files from this set.
         */
    public void removeAll()
    {
        nIndices = 0;
        fileList.clear();
    }

        /**
         * Add a new file to the list.
         */
    public void addFile(String fName, int dIndex)
    {
            // if it already exists, should it be copied again?
            // no it will be ignored... though this may not be
            // a good thing.  For example, the same file may need
            // to be copied to a whole bunch of locations....
        if (fileList.contains(fName)) return ;

            // check index capacity.
        if (nIndices >= dirIndex.length)
        {
            int dir2[] = dirIndex;
            dirIndex = new int[(nIndices * 3) / 2];
            System.arraycopy(dir2, 0, dirIndex, 0, nIndices);
            dir2 = null;
        }
        dirIndex[nIndices++] = dIndex;

        fileList.add(fName);
    }

        /**
         * Get the length of the DirIndex field/array.
         */
    public int getFieldLength(int index)
    {
        return (index == 1 ? nIndices : 0);
    }

        /**
         * Set the field value.
         */
    public void setFieldValue(String fieldName, Object value)
    {
        setFieldValue(getFieldIndex(fieldName), value);
    }

        /**
         * Sets the value of a field given its index.
         */
    public void setFieldValue(int fieldIndex, Object value)
    {
    }

        /**
         * Get the value of a field.
         */
    public Object getFieldValue(String fieldName)
    {
        return getFieldValue(getFieldIndex(fieldName));
    }

        /**
         * Get the field value.
         */
    public Object getFieldValue(int index)
    {
        switch (index)
        {
            case 0: return fileList;
            case 1: return dirIndex;
        }
        return null;
    }

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(String fieldName)
    {
        return getFieldType(getFieldIndex(fieldName));
    }

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(int index)
    {
        return fieldTypes[index];
    }

        /**
         * Gets a field iterator.
         * Returns the (name, type, value) triplet in each
         * "item" in the iterator.
         */
    //public Iterator fieldIterator();

        /**
         * Tells how many fields are there to be persisted.
         */
    public int fieldCount()
    {
        return fieldNames.length;
    }

        /**
         * Get the name of a given field.
         */
    public String getFieldName(int fieldIndex)
    {
        return fieldNames[fieldIndex];
    }

        /**
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName)
    {
        for (int i = 0;i < fieldNames.length;i++)
        {
            if (fieldNames[i].equals(fieldName)) return i;
        }
        return -1;
    }
}
