
package com.sri.apps.brewery.grinder;

import javax.swing.*;
import java.util.*;
import java.io.*;
//import java.awt.*;
import com.sri.utils.*;
import com.sri.apps.brewery.grinder.screens.*;
import com.sri.apps.brewery.io.*;

/**
 * An object that does an arbitrary number of steps or actions.
 * Has support for querying status and so on.
 */
public interface GrinderTask
{
    public final static int SUCCEEDED   = 0;
    public final static int EXECUTING   = 1;
    public final static int CANCELLED   = 2;
    public final static int FAILED      = 3;

        /**
         * This MUST be invoked before the run method 
         * is called.
         */
    public void initialise(Grinder gParent);

        /**
         * Get the name of the task.
         */
    public String getName();

        /**
         * Set the name of the task.
         */
    public void setName(String name);

        /**
         * The main execution method that is performed
         */
    public void run() throws Exception;

        /**
         * Gets the current value of the task.
         * Negative value would indicate an 
         * task of indeterminate length.
         */
    public int getCurrentValue();

        /**
         * Gets the minimum value of the task.
         * Negative value would indicate an 
         * task of indeterminate length.
         */
    public int getMinimumValue();

        /**
         * Gets the maximum value of the task.
         * Negative value would indicate an 
         * task of indeterminate length.
         */
    public int getMaximumValue();

        /**
         * Tells the value completed.
         * Negative value would indicate an 
         * task of indeterminate length.
         */
    public double getPercentComplete();

        /**
         * Get the message.
         */
    public String getMessage();

        /**
         * Tells the status of the task.
         * Can be:
         *
         * SUCCESS      - FInished successfully.
         * FAILED       - Task failed.
         * CANCELLED    -   Task was cancelled.
         * PERFORMING   -   Task still going one.
         */
    public int getTaskStatus();

        /**
         * Starts the task.
         */
    public void startTask();

        /**
         * Stops the task.
         * Returns false if task stopping failed.
         */
    public boolean stopTask();

        /**
         * Gets the object which is the result of the task
         * execution.  This call is invalid if the task
         * is still executing.
         *
         * Technically this should block till execution has
         * completed.
         */
    public Object getResult();
}
