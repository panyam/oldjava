
package com.sri.utils;

import java.util.*;

/**
 * A class to encapsulate a thread.
 * We already have a thread but doesnt properly support the stop method.
 * This is sort of a hackneyed attempt at it.
 */
public abstract class SThread implements Runnable
{
        /**
         * The thread to do the dirty work.
         */
    protected Thread ourThread = null;

        /**
         * Set to true when the thread needs to be stopped.
         */
    protected boolean threadStopped = false;

        /**
         * Tells if the thread is running or not.
         */
    protected boolean isRunning = false;

        /**
         * The id of this thread.
         */
    public int id;

        /**
         * Name of the thread.
         */
    public String name = null;

        /**
         * Constructor.
         */
    public SThread()
    {
        this(0,null); 
    }

        /**
         * Constructor.
         */
    public SThread(int id, String name)
    {
        this.id = id;
        setName(name);
    }

        /**
         * Get the name of the thread.
         */
    public String getName()
    {
        return name;
    }

        /**
         * Set the name.
         */
    public void setName(String name)
    {
        this.name = name;
    }

        /**
         * Starts the thread.
         */
    public synchronized void start()
    {
            // if we are already running then quit.
        if (isRunning) return ;

        isRunning = true;
        threadStopped = false;
        ourThread = null;
        ourThread = new Thread(this);
        ourThread.start();
    }

        /**
         * Stops the thread.
         */
    public synchronized void stop()
    {
            // if not running then do nothing...
        if (!isRunning) return ;

        threadStopped = true;
        notifyAll();

            // wait till the thread is still running...
        while (isRunning)
        {
            try
            {
                wait();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        ourThread = null;
        threadStopped = false;
    }

        /**
         * Tells if the thread is still running.
         */
    public boolean isAlive()
    {
        return isRunning && !threadStopped;
    }
}
