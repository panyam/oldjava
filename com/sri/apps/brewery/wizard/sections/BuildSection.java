/*
 * BuildSection.java
 *
 * Created on 09 August 2004, 11:31
 */
package com.sri.apps.brewery.wizard.sections;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.sri.apps.brewery.wizard.*;

/**
 * The section for doing the build of the setup files.
 *
 * @author Sri Panyam
 */
public class BuildSection extends BrewerySection
{
    protected JButton startBuild = new JButton("Start Build");
    protected JButton testBuild = new JButton("Test Build");

        /**
         * Verbose and temp dir options.
         */
    protected JCheckBox enableVerboseBtn = new JCheckBox("Verbose Output", true);
    protected JCheckBox delTempDirBtn = new JCheckBox("Delete Temporary Directory",
                                                      true);

        /**
         * Selecting items to build.
         */
    protected JCheckBox buildAllBtn = new JCheckBox("Build All", true);
    protected JCheckBox buildSelectedBtn = new JCheckBox("Build Selected", true);
    protected JPanel buildSelectionPanel = new JPanel();
    protected JList mediaList = new JList();

        /**
         * Build options.
         */
    protected JPanel buildOptionsPanel = new JPanel();

        /**
         * The "console" for the build output.
         */
    protected JTextArea buildOutputArea = new JTextArea(3, 80);

        /**
         * Constructor.
         */
    public BuildSection(SectionManager sMan, BreweryWizard bParent)
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
