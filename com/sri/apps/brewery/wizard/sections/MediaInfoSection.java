
/*
 * MediaInfoSection.java
 *
 * Created on 12 August 2004, 11:36
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for obtaining Media Info regarding the application and how
 * the application and its files should be "packaged".  For a given
 * project, several media fiels can be produced.  Eg, one as a linux rpm,
 * one as a windows executable etc.
 *
 * @author  Sri Panyam
 */
public class MediaInfoSection extends BrewerySection
{
        /**
         * Constructor.
         */
    public MediaInfoSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);
    }
}
