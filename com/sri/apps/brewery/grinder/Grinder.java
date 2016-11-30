package com.sri.apps.brewery.grinder;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.grinder.screens.*;
import com.sri.apps.brewery.grinder.containers.*;

/**
 * This is class that handles the installation from the user perspective.
 * Behind the scenes, it would invoke the necessary State objects, Room
 * objects and so on to do the installation.
 *
 * In essence, this is the mediator between the data, screen containers and
 * so on.
 *
 * @author Sri Panyam
 */
public class Grinder implements ActionListener
{
        /**
         * The data that is used by this grinder object.
         */
    protected Blend gData = null;

        /**
         * The main container that will host the UI.  GUI or Otherwise.
         */
    protected GrinderContainer gContainer = null;

        /**
         * The current screen we are currently in.
         */
    protected GrinderScreen currScreen;

        /**
         * The current task that is executing.
         */
    protected GrinderTask currGTask = null;
    protected int currGTaskIndex = -1;

        /**
         * Tells if the task is startup or a finalising task.
         */
    protected boolean isTaskStartup = false;

    protected javax.swing.Timer taskTimer =
                    new javax.swing.Timer(50, this);

        /**
         * The index of the current screen.
         */
    protected int currScreenIndex = -1;

        /**
         * Constructor.
         */
    public Grinder()
    {
        //setProjectName(name);
    }

        /**
         * The first procedure that is called.
         * Once this run, Container is loaded with the screen and GUI or
         * any other UI kicks in for the user to fiddle around with.
         *
         * At this point there MUSt be a valid instance of a Grinder
         * Container and a grinder data object.
         */
    public void run()
    {
            // first run the startup tasks.
        List taskList = gData.startupTasks;
        Iterator taskIter = taskList.iterator();
        currGTaskIndex = 0;

        isTaskStartup = true;
        while (taskIter.hasNext())
        {
            currGTask = (GrinderTask)taskIter.next();
            currGTaskIndex++;
                // execute this task and wait for it to finish...
            currGTask.startTask();
                // wait till the task finishes before going 
                // to the next one
            while (currGTask != null)
            {
                try
                {
                    wait(50);
                } catch(InterruptedException iExc)
                {
                    // do nothing
                }
            }
        }

        isTaskStartup = false;
        currGTaskIndex = 0;

        gContainer.showTaskStatus(null, -1, -1, false);

            // now load the screens and give the first screen to the 
            // container
        currScreenIndex = -1;
            // first run the 
        currScreen = gData.getScreen(0);
        if (currScreen != null) currScreenIndex = 0;
        gContainer.setPreviousVisible(false);
        gContainer.showScreen(currScreen);

    }

        /**
         * Proceed to the next screen.
         */
    public void goForward()
    {
    }

        /**
         * Proceed to the previous screen.
         */
    public void goBackward()
    {
    }

        /**
         * Go to a particular screen.
         */
    public void gotoScreen(String screenName)
    {
        GrinderScreen newScreen = getScreen(screenName);
    }

        /**
         * Go to the screen pointed to by this index.
         */
    public void gotoScreen(int screenIndex)
    {
        GrinderScreen newScreen = getScreen(screenIndex);
    }

        /**
         * Get a particular screen.
         */
    public GrinderScreen getScreen(String screenName)
    {
        return gData.getScreen(screenName);
    }

        /**
         * Get the screen pointed to by this index.
         */
    public GrinderScreen getScreen(int screenIndex)
    {
        return gData.getScreen(screenIndex);
    }

        /**
         * Cancel/Exit the installation process.
         */
    public void exitInstallation()
    {
    }

        /**
         * Called when it is necessary to go to the "copying" stage without
         * entering any more options or screen details.
         */
    public void finishInstallation()
    {
            // when this is called, do all the finalTasks.  The "file copy"
            // procedure is only one of the final tasks.
    }

        /**
         * Sets the grinder data.
         */
    public void setData(Blend data)
    {
        this.gData = data;
    }

        /**
         * Sets the container that will control the traversal of Screens
         * and be the trap-point for all events.
         */
    public void setGrinderContainer(GrinderContainer gCon)
    {
        this.gContainer = gCon;
        gContainer.setEnvironment(this, gData);
    }

        /**
         * Tells if the "next" option should be enabled.
         */
    public void setNextEnabled(boolean en)
    {
        gContainer.setNextEnabled(en);
    }

        /**
         * Tells if the "previous" option should be enabled.
         */
    public void setPreviousEnabled(boolean en)
    {
        gContainer.setPreviousEnabled(en);
    }

        /**
         * Tells if the "cancel/quit" option should be enabled.
         */
    public void setCancelEnabled(boolean en)
    {
        gContainer.setCancelEnabled(en);
    }

        /**
         * Tells if the "finish" option should be enabled.
         */
    public void setFinishEnabled(boolean en)
    {
        gContainer.setFinishEnabled(en);
    }

        /**
         * Tells if the "next" option should be visible.
         */
    public void setNextVisible(boolean vis)
    {
        gContainer.setNextVisible(vis);
    }

        /**
         * Tells if the "previous" option should be visible.
         */
    public void setPreviousVisible(boolean vis)
    {
        gContainer.setPreviousVisible(vis);
    }

        /**
         * Tells if the "cancel/quit" option should be visible.
         */
    public void setCancelVisible(boolean vis)
    {
        gContainer.setCancelVisible(vis);
    }

        /**
         * Tells if the "finish" option should be visible.
         */
    public void setFinishVisible(boolean vis)
    {
        gContainer.setFinishVisible(vis);
    }

        /**
         * Action event handler, mainly for the timers.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        if (src == taskTimer)
        {
            if (currGTask == null)
            {
                taskTimer.stop();
                return ;
            }

            /*int min = currTask.getMinimumValue();
            int max = currTask.getMaximumValue();
            int curr = currTask.getCurrentValue();
            double pcent = currTask.getPercentComplete();
            String msg = currTask.getMessage();*/
            int status = currGTask.getTaskStatus();

            if (status == GrinderTask.SUCCEEDED ||
                status == GrinderTask.CANCELLED)
            {
                taskTimer.stop();

                currGTask = null;
            } else
            {
                gContainer.showTaskStatus(currGTask,
                                          currGTaskIndex,
                                          gData.startupTasks.size(),
                                          isTaskStartup);
            }
        }
    }
}
