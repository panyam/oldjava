
/*
 * BreweryAction.java
 *
 * Created on 10 August 2004, 11:28
 */

package com.sri.apps.brewery.wizard.actions;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.wizard.*;
import com.sri.apps.brewery.wizard.sections.*;

/**
 * Action for file-op menus like Open, New, Close, Save and etc.
 */
public class FileAction extends BreweryAction
{
    public final static int NEW_FILE_ACTION = 0;
    public final static int OPEN_FILE_ACTION = 1;
    public final static int CLOSE_FILE_ACTION = 2;
    public final static int SAVE_FILE_ACTION = 3;
    public final static int SAVEAS_FILE_ACTION = 4;

        /**
         * The type of action.
         */
    protected int actionType = NEW_FILE_ACTION;

        /**
         * Constructor.
         */
    public FileAction(int type,
                      BreweryWizard parent,
                      BreweryAppFrame appFrame)
    {
        super(parent, appFrame);
        if (type < 0 || type > 4)
        {
            throw new IllegalArgumentException("Invalid action type: " + type);
        }
        this.actionType = type;
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        switch (actionType)
        {
            case NEW_FILE_ACTION: newProjectAction(e); break;
            case OPEN_FILE_ACTION: openProjectAction(e); break;
            case CLOSE_FILE_ACTION: closeProjectAction(e); break;
            case SAVE_FILE_ACTION: saveProjectAction(e); break;
            case SAVEAS_FILE_ACTION: saveProjectAsAction(e); break;
        }
    }

        /**
         * To create a new project.
         */
    protected void newProjectAction(ActionEvent e)
    {
    }

        /**
         * Open an existing project.
         */
    protected void openProjectAction(ActionEvent e)
    {
    }

        /**
         * Close a project.
         */
    protected void closeProjectAction(ActionEvent e)
    {
    }

        /**
         * Save the current project.
         */
    protected void saveProjectAction(ActionEvent e)
    {
    }

        /**
         * Save the current project into a new name.
         */
    protected void saveProjectAsAction(ActionEvent e)
    {
    }
}
