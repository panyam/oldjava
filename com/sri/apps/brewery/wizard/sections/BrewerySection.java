
/*
 * BrewerySection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * This describes the details of a screen or a section.
 *
 * @author  Sri Panyam
 */
public abstract class BrewerySection extends JPanel
{
        /**
         * The parent section, next Section and Previous section.
         */
    protected BrewerySection parentSection = null;
    protected BrewerySection nextSection = null;
    protected BrewerySection prevSection = null;

        /**
         * The parent wizard context to which this section/form belongs to.
         * Through this context, other screens and environmetn parameters
         * can be obtained.  Eg, General_Information_Screen.applicationName
         * etc.
         */
    protected BreweryWizard bParent = null;

        /**
         * Parent section manager.
         * Section must belong to EXACTLY one section manager object.
         */
    protected SectionManager sectionManager = null;

        /**
         * The sections that are part of this section.
         */
    protected List childSections = new LinkedList();

        /**
         * Title of the section.
         */
    public String sectionTitle = "";

        /**
         * Description of the section.
         */
    public String sectionDescription = "";

        /**
         * Constructor.
         */
    public BrewerySection(SectionManager sManager, BreweryWizard bParent)
    {
        this.bParent = bParent;

        if (bParent == null)
        {
            throw new 
                IllegalArgumentException("Parent wizard cannot be null.");
        }
    }

        /**
         * Set the Title.
         */
    public void setTitle(String t)
    {
        sectionTitle = t;
    }

        /**
         * Get the Title.
         */
    public String getTitle()
    {
        return sectionTitle;
    }

        /**
         * Set the Description.
         */
    public void setDescription(String d)
    {
        sectionDescription = d;
    }

        /**
         * Get the Description.
         */
    public String getDescription()
    {
        return sectionDescription;
    }

        /**
         * Tells if this section is a "top level" section.
         */
    public boolean isMainSection()
    {
        return parentSection == null;
    }

        /**
         * Return the number of children.
         */
    public int getChildCount()
    {
        return childSections.size();
    }

        /**
         * Add a child section.
         */
    public void addChild(BrewerySection sec)
    {
        sec.parentSection = this;
        sec.nextSection = sec.prevSection = null;

        int nSec = childSections.size();

        if (nSec > 0)
        {
            BrewerySection currLast =
                (BrewerySection)childSections.get(nSec - 1);
            currLast.nextSection = sec;
            sec.prevSection = currLast;
        }

        childSections.add(sec);
    }

        /**
         * Return the parent section.
         */
    public BrewerySection parent()
    {
        return parentSection;
    }

        /**
         * Get the next section.
         */
    public BrewerySection previous()
    {
        return prevSection;
    }

        /**
         * Get the next section.
         */
    public BrewerySection next()
    {
        return nextSection;
    }

        /**
         * Get the index of a section of a given name.
         */
    public int getChildIndex(String title)
    {
        Iterator iter = childSections.iterator();

        int i = 0;
        while (iter.hasNext())
        {
            BrewerySection kid = (BrewerySection)iter.next();

            if (kid.getTitle().equals(title))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

        /**
         * Get the child section at a given index.
         */
    public BrewerySection getChild(int index)
    {
        return (BrewerySection)childSections.get(index);
    }

        /**
         * Tells the parent (when asked) if this can layout and display its
         * own children or if it needs the parents help.  If this function
         * returns a false, then the parent is responsible for laying out
         * the child sections otherwise, the parent will layout the
         * children in any manner it chooses.
         *
         * If this function returns false, then the "gotoSection" MUST be
         * implemented as well which at the moment returns nothing.
         */
    public boolean willHandleChildDisplay()
    {
        return false;
    }

        /**
         * Display the given section.  THis function will be called if and
         * ONLY if willHandleChildDisplay returns true.
         */
    public void gotoSection(int index)
    {
    }

        /**
         * Called to indicate a transfer of data from variables to GUI
         * components or viceversa.
         *
         * The load parameter indicates whether the contents in the data
         * are to be reflected into the GUI components or not.
         */
    public void updateData(boolean load)
    {
    }

        /**
         * Called before the section is entered but just after its chose
         * for display.
         */
    public void sectionEntering()
    {
    }

        /**
         * Called just before the seciton is hidden and is about to be
         * left.
         *
         * The cancelled parameter indicates that the section items are to
         * be disposed off and will not be considered.
         */
    public void sectionLeaving(boolean cancelled)
    {
        if (!cancelled) updateData(false);
    }
}
