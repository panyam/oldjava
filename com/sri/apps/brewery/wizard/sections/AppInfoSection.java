
/*
 * AppInfoSection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for obtaining App Info regarding the application.
 *
 * @author  Sri Panyam
 */
public class AppInfoSection extends BrewerySection
{
        /**
         * Tells if any of the fields are mandatory.
         */
    protected boolean isMandatory[] = new boolean[]
    {
        true, true, true, false, false, false, false
    };
        /**
         * Labels used for display.
         */
    protected String infoNames[] = new String[]
    {
        "App Name",
        "Short Name",
        "Major Version",
        "Minor Version",
        "Build Number",
        "Company Name",
        "Company URL"
    };

        /**
         * Example infos.
         */
    protected String infoEGs[] = new String[]
    {
        "My App",
        "MyApp",
        "1",
        "010",
        "1498",
        "MySoft",
        "www.my.web.site.url.com"
    };

        /**
         * The actual GUI components.
         */
    protected JLabel infoLables[] = null;
    protected JTextField infoFields[] = null;
    protected JLabel infoDefaultLabels[] = null;

        /**
         * Constructor.
         */
    public AppInfoSection(SectionManager sMan, BreweryWizard bParent)
    {
        super(sMan, bParent);

        initComponents();
    }

        /**
         * Initialise the GUI components and their layouts.
         */
    protected void initComponents()
    {
        if (infoLables != null) return ;


        int nLabels = infoNames.length;
        infoLables = new JLabel[nLabels];
        infoFields = new JTextField[nLabels];
        infoDefaultLabels = new JLabel[nLabels];

        JPanel mainPanel = new JPanel(new java.awt.BorderLayout());
        JPanel eastPanel = new JPanel(new java.awt.GridLayout(nLabels, 1));
        JPanel midPanel = new JPanel(new java.awt.GridLayout(nLabels, 1));
        JPanel westPanel = new JPanel(new java.awt.GridLayout(nLabels, 1));
        mainPanel.add("East", eastPanel);
        mainPanel.add("Center", midPanel);
        mainPanel.add("West", westPanel);

        for (int i = 0;i < nLabels;i++)
        {
            infoLables[i] = new JLabel(infoNames[i]);
            infoFields[i] = new JTextField("");
            infoDefaultLabels[i] = new JLabel("Eg: " + infoEGs[i]);

            eastPanel.add(infoLables[i]);
            midPanel.add(infoFields[i]);
            westPanel.add(infoDefaultLabels[i]);
        }
        setLayout(new java.awt.BorderLayout());
        add("North", mainPanel);
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
