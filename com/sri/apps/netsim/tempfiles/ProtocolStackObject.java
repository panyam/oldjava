
package com.sri.apps.netsim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A process object.  Basically an object that generates and receives
 * generated packets.
 */
public abstract class ProtocolStackObject extends ProcessObject
{
        /**
         * Contains a protocol "driver" for each IP protocol
         * that is registered.
         * The objects are sorted by the protocol value in
         * the IP packet's protocol field.
         * Basically the idea is that any of the objects
         * in the procObjects can assume that the packet is an
         * IP Packet because, the packet wouldnt/shoudlnt come to
         * that protocol stack object unless it has passed through
         * the parent object.
         */
    protected ProcessObject procObjects[] = null;
    protected int nProcObjects = 0;

        /**
         * Add a new process object to our self.
         *
         * TODO:: TO have some kind of sortability in these process
         * objects... for example with TCP objects they could be sorted
         * based on the source port numbers for example!!!  This has to 
         * taken into account so that we could have faster access to
         * protocol drivers rather than doing a complete search of all
         * stored items.
         */
    public synchronized void addProcessObject(ProcessObject procObject)
    {
        ensureCapacity(nProcObjects + 1);

        procObject.parent = this;
        
            // also need to find the position where this
            // object is to be inserted...  

            // also need to do a sorted insert...
        procObjects[nProcObjects++] = procObject;
    }

        /**
         * Ensures the capacity of the object list.
         */
    public synchronized void ensureCapacity(int num)
    {
        if (procObjects == null) procObjects = new ProcessObject[num];

            // resize if necessary...
        if (procObjects.length < num)
        {
            ProcessObject pso[] = procObjects;
            procObjects = new ProcessObject[num];
            System.arraycopy(pso, 0, procObjects, 0, nProcObjects);
        }
    }

        /**
         * Tells if a packet is a broad cast packet.
         * Once again depends on the protocol and the
         * layer/offset at which are talking.
         */
    public boolean isPacketBroadcast(Packet packet)
    {
        return false;
    }

        /**
         * Returns the length of the header of this
         * protocol level.  Works based on the "offset" value.
         */
    public int getHeaderLength(Packet packet)
    {
        return 0;
    }
}
