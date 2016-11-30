
package com.sri.utils.adt;

import java.util.*;
/**
 * A class to encapsulate a Queue.
 */
public class Queue
{
        /**
         * The head node.
         */
    protected QueueNode head = null;

        /**
         * The tail node.
         */
    protected QueueNode tail = null;

        /**
         * Number of items in the queue.
         */
    protected int size = 0;

        /**
         * Constructor.
         */
    public Queue()
    {
        size = 0;
        head = tail = null;
    }

        /**
         * Put an item in the queue.
         */
    public synchronized void put(Object obj)
    {
        QueueNode qn = new QueueNode(obj);
        if (head == null) tail = head = new QueueNode(obj);
        else 
        {
            tail.next = qn;
            tail = qn;
        }
        size++;
    }

        /**
         * Tells if the queue is empty.
         */
    public boolean isEmpty()
    {
        return size == 0;
    }


        /**
         * Get the head item.
         */
    public synchronized Object get()
    {
        if (size > 0)
        {
            size--;
            Object obj = head.item;
            head = head.next;
            return obj;
        }
        return null;
    }

        /**
         * Get the size of the queue.
         */
    public int getSize()
    {
        return size;
    }

        /**
         * A node in the queue.
         */
    public class QueueNode
    {
            /**
             * The actual item being stored.
             */
        public Object item;
        public QueueNode next;

            /**
             * Constructor.
             */
        public QueueNode(Object o)
        {
            this.item = o;
        }
    }
}
