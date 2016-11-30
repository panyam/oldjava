
package com.sri.apps.brewery.core;

import java.util.*;
import com.sri.apps.brewery.io.*;

/**
 * Holds information about what packages are installed and so on.
 */
public class PackageInfo implements Persistable
{
        /**
         * The object root package is NEVER calculated untill
         * it is required.
         */
    private boolean rootKnown = false;

        /**
         * A dummy package.
         */
    private final static BreweryPackage DUMMY_PACKAGE = new BreweryPackage(-1);

        /**
         * The root of all packages.
         */
    protected BreweryPackage rootPackage = new BreweryPackage(0);

        /**
         * All the packages as one big collection.
         */
    protected Map allPackages = new HashMap();

        /**
         * Constructor.
         */
    public PackageInfo()
    {
    }

        /**
         * Gets the root package.
         */
    public BreweryPackage getPackageRoot()
    {
        if ( ! rootKnown)
        {
            // find out what the root packages are....
            // basically do a one step traversal of the 
            // package collection and any package ID is 
            // <= 0 is a parentless package which means
            // they are children of the "rootPackage"
            rootPackage.clear();

                // get the package iterator
            Iterator iterator = allPackages.values().iterator();

            while (iterator.hasNext())
            {
                BreweryPackage pkg = (BreweryPackage)iterator.next();
                BreweryPackage parent = pkg.getParent();
                if (parent == null || parent == rootPackage)
                {
                    rootPackage.add(BreweryPackage.CHILD_PACKAGE, pkg);
                }
            }

            rootKnown = true;
        }
        return rootPackage;
    }

        /**
         * Get the package with the given ID.
         */
    public BreweryPackage getPackage(Integer pkgID)
    {
        return (BreweryPackage)allPackages.get(pkgID);
    }

        /**
         * Adds a package.
         *
         * A new ID is assigned to the package as it is
         * added to the list.
         */
    public void addPackage(BreweryPackage pkg)
    {
            // add a package if it doesnt exist
        if ( ! allPackages.containsKey(pkg.getID()))
        {
            allPackages.put(pkg.getID(), pkg);
            rootKnown = false;
        } else
        {
            throw new 
                IllegalArgumentException("Package already exists: \"" +
                                         pkg.getID() +
                                         "\"");
        }
    }

        /**
         * Removes an existing package.
         */
    public void removePackage(BreweryPackage pkg)
    {
        allPackages.remove(pkg.getID());
    }

        /**
         * Normalises the package IDs so that ID of any package will be
         * GREATER than the ID of its parent.
         */
    protected void normalisePackageIDs()
    {
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
        return 0;
    }

        /**
         * Get the field length if its an ARRAY.
         */
    public int getFieldLength(int index)
    {
        return 0;
    }

        /**
         * Get the name of a given field.
         */
    public String getFieldName(int fieldIndex)
    {
        return "";
    }

        /**
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName)
    {
        return -1;
    }
}
