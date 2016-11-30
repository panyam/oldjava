
/*
 * SectionManager.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;
/**
 * The section manager contains all the different section and is
 * responsible for the layout.
 *
 * @author  Sri Panyam
 */
public interface SectionManager
{
        /**
         * Display the given section.
         */
    public void gotoSection(BrewerySection section);
}
