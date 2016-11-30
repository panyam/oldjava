

package com.sri.apps.netsim;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * The event queue.
 *
 * Note that this can have different implementations.  This is responsible
 * for holding the events in a particular order.
 */
public class EventQueue
{
        /**
         * The events list.
         */
    protected SimEvent events[] = new SimEvent[20];

        /**
         * Number of events in the list.
         */
    protected int nEvents;

        /**
         * A mutex for synchronized access to the priority queue.
         */
    protected Object queueMutex = new Integer(0);

        /**
         * The current time.
         */
    protected double currTime = 0;

        /**
         * Sets the current time.
         */
    public void setTime(double time)
    {
        synchronized (queueMutex)
        {
            if (nEvents < 0 || events[0].targetTime > time)
            {
                currTime = time;
            } else
            {
                this.currTime = events[0].targetTime;
            }
        }
    }

        /**
         * Returns the size of the queue.
         */
    public int size()
    {
        return nEvents;
    }

        /**
         * Tells if the event queue is empty.
         */
    public boolean isEmpty()
    {
        return nEvents == 0;
    }

        /**
         * Adds a new event that will be processed when its turn comes.
         */
    public void addEvent(SimEvent event)
    {
        synchronized (queueMutex)
        {
                // resize the array if necessary
            if (events.length <= nEvents)
            {
                SimEvent e2[] = events;
                events = new SimEvent[(nEvents * 3) / 2];
                System.arraycopy(e2, 0, events, 0, nEvents);
                e2 = null;
            }

            int parent, child = nEvents++;

            while (child > 0 &&
                   events[parent = (child - 1) / 2].targetTime >
                                                    event.targetTime)
            {
                events[child] = events[parent];
                child = parent;
            }
            events[child] = event;
        }
    }

        /**
         * Peeks at the next event without removing it from the queue.
         */
    public SimEvent peekNextEvent()
    {
        synchronized (queueMutex)
        {
            return (nEvents > 0 ? events[0] : null);
        }
    }

        /**
         * Gets the next event for processing.
         */
    public SimEvent nextEvent()
    {
        synchronized (queueMutex)
        {
            if (nEvents == 0) return null;
            SimEvent result = events[0];
            SimEvent item = events[--nEvents];
            int child, parent = 0; 
            while ((child = (2 * parent) + 1) < nEvents)
            {
                if (child + 1 < nEvents && 
                        events[child].targetTime >
                                events[child + 1].targetTime)
                {
                    ++child;
                }
                if (item.targetTime > events[child].targetTime)
                {
                    events[parent] = events[child];
                    parent = child;
                } else {
                    break;
                }
            }
            events[parent] = item;
            return result;
        }
    }
}
