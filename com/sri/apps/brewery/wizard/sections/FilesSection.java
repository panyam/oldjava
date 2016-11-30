
/**
 * FilesSection.java
 *
 * Created on 09 August 2004, 11:57
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for entering information on what files are to be bundled
 * with the application and so on.
 *
 * Also an "auto locate" section is required.  In this one, given a
 * "Starting" class, all classes referred to by this class will be
 * "extracted".  This would include things like, "exclude from search"
 * options, "add Jar as is" option (or to extract the class archive) option
 * and others.
 *
 * @author  Sri Panyam
 */
public class FilesSection extends BrewerySection
{
        /**
         * Constructor.
         */
    public FilesSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);
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
