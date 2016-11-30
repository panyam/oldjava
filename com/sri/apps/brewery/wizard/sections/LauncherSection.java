/*
 * LauncherSection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for obtaining info on how the java app is to be launched.
 *
 * @author  Sri Panyam
 */
public class LauncherSection extends BrewerySection
{
        /**
         * Constructor.
         */
    public LauncherSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);
    }
}
