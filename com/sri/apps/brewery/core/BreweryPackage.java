
package com.sri.apps.brewery.core;

import java.util.*;
import java.io.*;
import javax.swing.tree.*;

import com.sri.apps.brewery.io.*;

/**
 * This class represents a package in the components installation
 * hierarchy.  A package may have sub packages or a set of files.
 *
 * @author Sri Panyam
 */
public class BreweryPackage extends InstallUnit
{
    protected final static String fieldNames[] = new String[]
    {
        "Package ID",               // int 
        "Package Title",            // string
        "Package Description",      // string
        "Package Name",             // String
        "Child Packages",           // Child Package objects
        "Package Files",            // Files for the package
        "Dependant Package IDs",    // Integer array
    };

        /**
         * The field types.
         */
    protected final static PersistableType fieldTypes[] = new PersistableType[]
    {
        PersistableType.getType(PersistableType.INT_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.STRING_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.COLLECTION),
        PersistableType.getType(PersistableType.PERSISTABLE_TYPE,
                                PersistableType.NONE),
        PersistableType.getType(PersistableType.INT_TYPE,
                                PersistableType.ARRAY),
    };

        /**
         * Description of the package.
         */
    protected String packageDescription;

        /**
         * The title of the package as will be displayed.
         */
    protected String packageTitle;

        /**
         * Packages which must installed if this has to be installed.
         *
         * Note that this can introduce circular dependancies but since
         * it only deals with file installation, this does not make a
         * difference, but its is best to avoid circular dependancies as
         * much as possible.
         */
    public final static int DEPENDANCY_PACKAGE = 0;

        /**
         * Child packages.
         */
    public final static int CHILD_PACKAGE = 1;

        /**
         * The files that belong to this package.
         * The idea is that if this package is to be installed,
         * then all these files are to be installed as well.
         */
    protected Files files = new Files();

        /**
         * The list of child packages, dependencies and installUnits.
         */
    protected List subPackages[] = new List[2];

        /**
         * Tells if this can be avoided.
         */
    protected boolean mandatory = false;

        /**
         * Tells if the package is to be install or not.
         */
    protected boolean install = false;

        /**
         * The package ID.
         */
    protected Integer packageID;

        /**
         * Constructor.
         */
    public BreweryPackage(int id)
    {
        this(new Integer(id));
    }

        /**
         * Constructor.
         */
    public BreweryPackage(Integer id)
    {
        packageID = id;
        parent = null;
    }

        /**
         * Gets the packge ID.
         */
    public Integer getID()
    {
        return packageID;
    }

        /**
         * Tells if this package is mandatory or not.
         */
    public boolean isMandatory()
    {
        return mandatory;
    }

        /**
         * Get the files that belong to this package.
         */
    public Files getPackageFiles()
    {
        return files;
    }

        /**
         * Tells if this package can be avoided or not.
         */
    public void setMandatory(boolean mand)
    {
        this.mandatory = mand;
    }

        /**
         * Get the package title.
         */
    public String getTitle()
    {
        return packageTitle;
    }

        /**
         * Set the package title.
         */
    public void setTitle(String title)
    {
        packageTitle = title;
    }

        /**
         * Get the package description.
         */
    public String getDescription()
    {
        return packageDescription;
    }

        /**
         * Set the package description.
         */
    public void setDescription(String desc)
    {
        packageDescription = desc;
    }

        /**
         * Adds a sub object
         */
    public void add(int pkgType, BreweryPackage pack)
    {
        getPackage(pkgType).add(pack);
    }

        /**
         * Clear all children.
         */
    public void clear()
    {
        subPackages[0].clear();
        subPackages[1].clear();
        files.removeAll();
    }

        /**
         * Removes a sub object.
         */
    public void remove(int pkgType, int index)
    {
        getPackage(pkgType).remove(index);
    }

        /**
         * Removes a sub object.
         */
    public void remove(int pkgType, InstallUnit pack)
    {
        getPackage(pkgType).remove(pack);
    }

        /**
         * Insert a package at the given location.
         */
    public void insertSubPackage(int pkgType, int index, BreweryPackage pack)
    {
        getPackage(pkgType).add(index, pack);
    }

        /**
         * Get the package at a given index.
         */
    public BreweryPackage getSubPackage(int unitType, int index)
    {
        return (BreweryPackage)getPackage(unitType).get(index);
    }

        /**
         * Get the package of the given type.
         */
    protected List getPackage(int pType)
    {
        try
        {
            return subPackages[pType];
        } catch (Exception exc)
        {
            throw
                new IllegalArgumentException(
                        "Invalid Package type: " + pType + ".  Package Type " +
                        "must be one of DEPENDANCY_PACKAGE or " +
                        "CHILD_PACKAGE.");
        }
    }

        /**
         * Get the count of how many packages are here.
         */
    public int getPackageCount(int which)
    {
        return getPackage(which).size();
    }

        /**
         * Tells if the package is to be installed or not.
         */
    public boolean toBeInstalled()
    {
        return install;
    }

        /**
         * Sets if the package is to be installed or not.
         */
    public void setToBeInstalled(boolean ins)
    {
        this.install = ins;
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
            case 0: return packageID;
            case 1: return packageTitle;
            case 2: return packageDescription;
            case 3: return "";
            case 4: 
            {
                int depList[] = new int[getPackageCount(DEPENDANCY_PACKAGE)];
                Iterator iter = subPackages[DEPENDANCY_PACKAGE].iterator();
                int i = 0;
                while (iter.hasNext())
                {
                    depList[i++] =
                        ((BreweryPackage)(iter.next())).getID().intValue();
                }
                return depList;
            }
            case 5: return files;
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
        return null;
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
         * For a field at the given position, returns its length.  This is
         * ONLY valid if this field is an ARRAY.
         */
    public int getFieldLength(int index)
    {
        if (fieldTypes[index].listType == PersistableType.ARRAY)
        {
            return subPackages[DEPENDANCY_PACKAGE].size();
        }
        return 0;
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
