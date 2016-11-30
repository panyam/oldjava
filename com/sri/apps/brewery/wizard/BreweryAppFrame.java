/*
 * BreweryAppFrame.java
 *
 * Created on 13 August 2004, 11:12
 */
package com.sri.apps.brewery.wizard;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.wizard.sections.*;
import com.sri.apps.brewery.wizard.sections.*;
import com.sri.gui.core.containers.*;

/***    XML Classes     ***/
import  org.w3c.dom.*;
import  org.apache.xerces.dom.DocumentImpl;
import  org.apache.xerces.dom.DOMImplementationImpl;
import  org.w3c.dom.Document;
import  org.apache.xml.serialize.OutputFormat;
import  org.apache.xml.serialize.Serializer;
import  org.apache.xml.serialize.SerializerFactory;
import  org.apache.xml.serialize.XMLSerializer;
import javax.xml.parsers.*;

/**
 * The main frame that "hosts" the Brewery Wizard GUI.
 *
 * @author  Sri Panyam
 */
public class BreweryAppFrame extends JFrame
                             implements
                                ActionListener,
                                SectionManager
{
        /**
         * XML Config info.
         */
    protected final static String NAME_ATTR = "name";
    protected final static String CLASS_ATTR = "class";
    protected final static String LAF_ROOT_NODE = "laf_list";
    protected final static String LAF_NODE = "laf";

        /**
         * The parent brewery wizard.
         */
    protected BreweryWizard wizParent = null;

        /**
         * The splash screen.
         */
    protected JWindow splashWindow = new BrewerySplashScreen(400, 300);

        /**
         * The menu bar for the application.
         */
    protected JMenuBar breweryMBar = null;

        /**
         * Main Menu 1: Project Menu.
         */
    protected JMenu projectMenu = null;
    protected JMenuItem newProjectMI = null;
    protected JMenuItem openProjectMI = null;
    protected JMenuItem closeProjectMI = null;
    protected JMenuItem saveProjectMI = null;
    protected JMenuItem saveAsProjectMI = null;
    protected JMenu recentListMenu = null;
    protected JMenuItem recentFileMI[] = new JMenuItem[5];
    protected JMenuItem clearRecListMI = null;
    protected JMenuItem exitMI = null;

        /**
         * Main Menu 2: Edit Menu.
         */
    protected JMenu editMenu = null;
    protected JMenuItem undoMI = null;
    protected JMenuItem redoMI = null;
    protected JMenuItem cutItemMI = null;
    protected JMenuItem copyItemMI = null;
    protected JMenuItem pasteItemMI = null;
    protected JMenuItem findItemMI = null;
    protected JMenuItem replaceItemMI = null;
    protected JMenuItem preferencesMI = null;

        /**
         * Main Menu 3: View Menu.
         */
    protected JMenu viewMenu = null;
    protected JMenuItem mainSectionMIs[] = null;
    protected JMenuItem optionsMI = null;

        /**
         * Main Menu 4: Help Menu.
         */
    protected JMenu helpMenu = null;
    protected JMenuItem helpContentsMI = null;
    protected JMenuItem tutorialsMI = null;
    protected JMenuItem webSiteMI = null;
    protected JMenuItem aboutMI = null;

        // Panels
    protected JPanel sectionButtonPanel = new JPanel();
    protected JPanel centerPanel = new JPanel();

        /**
         * Tells if the components have been created so that subsequent
         * calls to initComponents, will not create no instances of the GUI
         * objects.
         */
    protected boolean componentsInited = false;

        /**
         * List of LAF sorted by name.
         */
    protected Map lafMap = new HashMap();

        /**
         * When load section is called, it will do anything only if this
         * flag is true.
         */
    protected boolean sectionsChanged = true;

        /**
         * The main Sections that are used.
         */
    protected List mainSections = new LinkedList();

        /**
         * List of ALL the sections as a flat structure hashed by their
         * title.
         */
    protected Map sectionsByTitle = new HashMap();

        /**
         * The list of all sections so that they can be referenced QUICKLY
         * given their IDs.
         */
    protected List sectionsByID = new ArrayList();

        /**
         * Constructor.
         */
    public BreweryAppFrame(BreweryWizard bWizard)
    {
        super(BreweryVersion.getApplicationFrameTitle());

        this.wizParent = bWizard;
    }

        /**
         * Gets the count of the main sections.
         */
    public int getMainSectionCount()
    {
        return mainSections.size();
    }

        /**
         * Gets the count of ALL sections.
         */
    public int getSectionCount()
    {
        return sectionsByTitle.size();
    }

        /**
         * Get the main section at the given index.
         */
    public BrewerySection getMainSection(int index)
    {
        return (BrewerySection)mainSections.get(index);
    }

        /**
         * Get the section with the given ID.
         */
    public BrewerySection getSection(int index)
    {
        return (BrewerySection)sectionsByID.get(index);
    }

        /**
         * Get section by title.
         */
    public BrewerySection getSection(String title)
    {
        return (BrewerySection)sectionsByTitle.get(title);
    }

        /**
         * Load all the sections.
         */
    public void loadSections()
    {
        if ( ! sectionsChanged ) return ;

        loadMainSection(new AppInfoSection(this, wizParent));
        loadMainSection(new LauncherSection(this,  wizParent));
        loadMainSection(new FilesSection(this,  wizParent));
        loadMainSection(new GUIInfoSection(this,  wizParent));
        loadMainSection(new MediaInfoSection(this,  wizParent));
        loadMainSection(new BuildSection(this,  wizParent));

            // This lays out the contaienr that holds and displays the
            // sections...
        layoutSections();
    }

        /**
         * Loads the main section and all its child section recursively.
         *
         * It is assumed that a section is responsible for creating its
         * children.
         */
    protected void loadMainSection(BrewerySection section)
    {
        mainSections.add(section);
 
        Stack sectionStack = new Stack();

        sectionStack.push(section);

        while ( ! sectionStack.isEmpty())
        {
            BrewerySection curr = (BrewerySection)sectionStack.pop();
            sectionsByTitle.put(curr.getTitle(), curr);
            int nKids = curr.getChildCount();

                // register each kid as a valid section, so when asked to
                // display it, the section manager can quickly get it...
            for (int i = 0;i < nKids;i++)
            {
                sectionStack.push(curr.getChild(i));
            }
        }
    }

        /**
         * How are the sections laid out?
         */
    protected void layoutSections()
    {
        SOutlookPanel soPanel = new SOutlookPanel();

        for (Iterator iter = mainSections.iterator();iter.hasNext();)
        {
            BrewerySection section = (BrewerySection)iter.next();
            soPanel.addItem(new JButton(section.getTitle()),
                        new JLabel(section.getTitle()));
        }
        centerPanel.add("West", soPanel);
    }

        /**
         * Display the given section.
         */
    public void gotoSection(BrewerySection section)
    {
        // TODO: Does nothing yet... 
    }

        /**
         * Load the LAFs from the config document.
         * This file can be called ONLY after the readConfigFile file has
         * been successfully called.  Otherwise, results are not
         * guaranteed.
         */
    protected void loadLAFs()
    {
        WizardDocument wizDoc = wizParent.getDocument();

        Element rootElem = wizDoc.rootElem;

        Element lafListNode =
            (Element)rootElem.getElementsByTagName(LAF_ROOT_NODE).item(0);

            // only one persistable object can be returned?
        NodeList lafElems =
            lafListNode.getElementsByTagName(LAF_NODE);

        for (int i = 0, l = lafElems.getLength();i < l;i++)
        {
            Element lafNode = (Element)lafElems.item(i);
            String lafName = lafNode.getAttribute(NAME_ATTR);
            String className = lafNode.getAttribute(CLASS_ATTR);
            LookAndFeel laf = (LookAndFeel)BreweryUtils.createInstance(className);
            if (laf != null)
            {
                lafMap.put(lafName, laf);
            }
        }
    }

        /**
         * Starts the application.
         */
    public void loadAndShow()
    {
            // show the splash screen...
        splashWindow.setVisible(true);

        System.out.println("Starting Items...");

            // load the LAFs
        loadLAFs();

            // load Sections
        loadSections();

        System.out.println("Closig spash window...");

            // close the splash screen...
        splashWindow.setVisible(false);
        splashWindow.toBack();

            // MUST be called after sections are loaded...
        pack();
        setVisible(true);
        toFront();
    }

        /**
         * Initialises the components.
         */
    public void initComponents()
    {
            // quit if components already initialised...
        if (componentsInited) return ;

        breweryMBar = new JMenuBar();

        breweryMBar.add((projectMenu = new JMenu("Project")));
        projectMenu.
            add((newProjectMI = new JMenuItem("New...")));
        projectMenu.
            add((openProjectMI = new JMenuItem("Open...")));
        projectMenu.
            add((closeProjectMI = new JMenuItem("Close")));
        projectMenu.addSeparator();
        projectMenu.
            add((saveProjectMI = new JMenuItem("Save")));
        projectMenu.
            add((saveAsProjectMI = new JMenuItem("Save As...")));
        projectMenu.addSeparator();
        projectMenu.
            add((recentListMenu = new JMenu("Recent Files...")));
        recentListMenu.addSeparator();
        recentListMenu.add((clearRecListMI = new JMenuItem("Clear List")));
        projectMenu.addSeparator();
        projectMenu.add((exitMI = new JMenuItem("Exit")));

        breweryMBar.add((editMenu = new JMenu("Edit")));
        editMenu.add((undoMI = new JMenuItem("Undo")));
        editMenu.add((redoMI = new JMenuItem("Redo")));
        editMenu.addSeparator();
        editMenu.add((cutItemMI = new JMenuItem("Cut")));
        editMenu.add((copyItemMI = new JMenuItem("Copy")));
        editMenu.add((pasteItemMI = new JMenuItem("Paste")));
        editMenu.addSeparator();
        editMenu.add((findItemMI = new JMenuItem("Find...")));
        editMenu.add((replaceItemMI = new JMenuItem("Replace...")));
        editMenu.addSeparator();
        editMenu.add((preferencesMI = new JMenuItem("Preferences...")));

        breweryMBar.add((viewMenu = new JMenu("View")));
            // go through the sections and have a MenuItem for each
            // "MainSection"
        viewMenu.addSeparator();
        int nMainSec = getMainSectionCount();
        mainSectionMIs = new JMenuItem[nMainSec];
        for (int i = 0;i < nMainSec;i ++)
        {
            BrewerySection mSec = getMainSection(i);
            viewMenu.add((mainSectionMIs[i] = new JMenuItem(mSec.getTitle())));
        }
        viewMenu.addSeparator();

        breweryMBar.add((helpMenu = new JMenu("Help")));
        helpMenu.add((helpContentsMI = new JMenuItem("Help Contents...")));
        helpMenu.add((tutorialsMI = new JMenuItem("Tutorials...")));
        helpMenu.add((tutorialsMI = new JMenuItem("Brewery Online...")));
        helpMenu.addSeparator();
        helpMenu.add((aboutMI = new JMenuItem("About Brewery...")));

        setJMenuBar(breweryMBar);

            // now intialises the panels...
        sectionButtonPanel.setLayout(
                new BoxLayout(sectionButtonPanel, BoxLayout.PAGE_AXIS));
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add("Center", centerPanel);
    }

        /**
         * Lays out the components appropriately.
         */
    protected void layComponents()
    {
    }

        /**
         * Sets the listeners and all.
         */
    protected void setListeners()
    {
        newProjectMI.addActionListener(this);
        openProjectMI.addActionListener(this);
        closeProjectMI.addActionListener(this);
        saveProjectMI.addActionListener(this);
        saveAsProjectMI.addActionListener(this);
        recentListMenu.addActionListener(this);
        //recentFileMI[] = new JMenuItem[5];
        clearRecListMI.addActionListener(this);
        exitMI.addActionListener(this);

        undoMI.addActionListener(this);
        redoMI.addActionListener(this);
        cutItemMI.addActionListener(this);
        copyItemMI.addActionListener(this);
        pasteItemMI.addActionListener(this);
        findItemMI.addActionListener(this);
        replaceItemMI.addActionListener(this);
        preferencesMI.addActionListener(this);

        //mainSectionMIs[] = null;
        optionsMI.addActionListener(this);

        helpContentsMI.addActionListener(this);
        tutorialsMI.addActionListener(this);
        webSiteMI.addActionListener(this);
        aboutMI.addActionListener(this);
    }

    public void setVisible(boolean vis)
    {
        if (vis)
        {
            initComponents();
        }
        super.setVisible(vis);
    }

        /**
         * Action Event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
    }
}
