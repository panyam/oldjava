
/*
 * GUIInfoSection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.wizard.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * The section for obtaining GUI Info regarding the installer.
 *
 * At the moment this will have two sub sections: Screens and
 * Containers/Screen Managers.
 *
 * Apart from default screens, 
 * @author  Sri Panyam
 */
public class GUIInfoSection extends BrewerySection
{
        /**
         * List of all the screen classes.
         *
         * More classes can be added.
         */
    protected List availableClasses = new LinkedList();

        /**
         * List of all the containers and the ones that can be used to hold
         * the classes.
         *
         * The containers will be hashed by their name.
         */
    protected Map containerClasses = new HashMap();

        /**
         * The section that holds the screen info.
         */
    protected ScreenInfoSection screenSection = null;

        /**
         * The section where container info can be entered.
         */
    protected ContainerInfoSection containerSection = null;

        /**
         * Constructor.
         */
    public GUIInfoSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);
        screenSection = new ScreenInfoSection(sMan, bParent);
        containerSection = new ContainerInfoSection(sMan, bParent);

        addChild(screenSection);
        addChild(containerSection);
    }

        /**
         * Add a new screen class to the list.
         */
    public void insertScreenClass(int index, String className)
    {
        GrinderScreen gScreen =
            (GrinderScreen)BreweryUtils.createInstance(className);

        if (gScreen != null)
        {
            availableClasses.add(index, gScreen);
        }
    }

        /**
         * The section that shows screen Info.
         */
    public class ScreenInfoSection extends BrewerySection
    {
            /**
             * Constructor.
             */
        public ScreenInfoSection(SectionManager sMan,
                                 BreweryWizard bParent)
        {
            super(sMan, bParent);
        }
    }

        /**
         * Sections that shows information about containers.
         */
    public class ContainerInfoSection extends BrewerySection
    {
            /**
             * Constructor.
             */
        public ContainerInfoSection(SectionManager sMan,
                                    BreweryWizard bParent)
        {
            super(sMan, bParent);
        }
    }
}
