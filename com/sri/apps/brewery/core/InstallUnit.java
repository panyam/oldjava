
package com.sri.apps.brewery.core;

import java.util.*;
import java.io.*;
import javax.swing.tree.*;
import com.sri.apps.brewery.io.*;

/**
 * The the smallest installation unit.
 *
 * @author Sri Panyam
 */
public abstract class InstallUnit implements Persistable
{
        /**
         * The parent to which this installation unit belongs to.
         */
    protected BreweryPackage parent;

        /**
         * Gets the parent of this unit.
         */
    public BreweryPackage getParent()
    {
        return parent;
    }

        /**
         * Sets the parent package.
         */
    public void setParent(BreweryPackage parent)
    {
        this.parent = parent;
    }
}
