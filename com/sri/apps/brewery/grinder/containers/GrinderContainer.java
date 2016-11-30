package com.sri.apps.brewery.grinder.containers;

import javax.swing.*;
import java.util.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * The main container interface.  This is the container that is responsible
 * for navigation between screens, showing the next, previous and cancel
 * buttons and so on.
 *
 * This object is ONLY responsible for the display and the event handler
 * functionality.  It will be the main grinder's responsibility to get the
 * "next" or "previous" screens.  The container simply shows the screens
 * and acts as a trap for all events (like GUI events) and sends them off
 * in an higheer level logical manner to the grinder (eg goNext, goForward,
 * cancelInstallation, screenFailed) and so on.
 *
 * @author Sri Panyam
 */
public interface GrinderContainer
{
        /**
         * Sets the grinder parent and the data model.
         *
         * All actions, that require the grinder parent, before this
         * function is called may have undefined results.
         */
    public void setEnvironment(Grinder parent, Blend gData);

        /**
         * Request the Container to "represent" or "present" the status of
         * a task that the grinder is executing.
         *
         * The container may show this anyway it chooses to, eg using
         * progressbars or just displaying lines of text etc.
         */
    /*public void setTaskStatus(int taskID,
                              int numTasks, 
                              double percentComplete,
                              String taskMessage);*/

        /**
         * Start the process of displaying the screen.
         */
    public void showScreen(GrinderScreen screen);

        /**
         * Shows the status of the current task.
         *
         * A value of "null" for currTask means that no task is being
         * performed at the moment.
         */
    public void showTaskStatus(GrinderTask currTask,
                               int taskIndex,
                               int taskCount,
                               boolean startup);

        /**
         * Tells if the "next" option should be enabled.
         */
    public void setNextEnabled(boolean en);

        /**
         * Tells if the "previous" option should be enabled.
         */
    public void setPreviousEnabled(boolean en);

        /**
         * Tells if the "cancel/quit" option should be enabled.
         */
    public void setCancelEnabled(boolean en);

        /**
         * Tells if the "finish" option should be enabled.
         */
    public void setFinishEnabled(boolean en);

        /**
         * Tells if the "next" option should be visible.
         */
    public void setNextVisible(boolean vis);

        /**
         * Tells if the "previous" option should be visible.
         */
    public void setPreviousVisible(boolean vis);

        /**
         * Tells if the "cancel/quit" option should be visible.
         */
    public void setCancelVisible(boolean vis);

        /**
         * Tells if the "finish" option should be visible.
         */
    public void setFinishVisible(boolean vis);
}
