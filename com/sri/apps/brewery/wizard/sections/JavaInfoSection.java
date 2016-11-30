
/*
 * JavaInfoSection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for obtaining Java Info regarding the application.
 *
 * @author  Sri Panyam
 */
public class JavaInfoSection extends BrewerySection
{
        /**
         * Tells if the JRE should be bundled or not.
         */
    protected boolean bundleJRE = false;

        /**
         * Constructor.
         */
    public JavaInfoSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);
    }
}
