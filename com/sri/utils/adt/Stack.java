
package com.sri.utils.adt;

import java.util.*;
/**
 * A class to encapsulate a Stack.
 */
public class Stack
{
        /**
         * The head node.
         */
    public StackNode head = null;

        /**
         * The tail node.
         */
    public StackNode tail = null;

        /**
         * Number of items in the stack.
         */
    protected int size = 0;

        /**
         * Constructor.
         */
    public Stack()
    {
        size = 0;
        head = tail = null;
    }

        /**
         * Put an item in the stack.
         */
    public synchronized void push(Object obj)
    {
        StackNode qn = new StackNode(obj);
        if (head == null) tail = head = new StackNode(obj);
        else
        {
            qn.next = head;
            head = qn;
        }
        size++;
    }

        /**
         * Tells if the stack is empty.
         */
    public boolean isEmpty()
    {
        return size == 0;
    }


        /**
         * Get the head item.
         */
    public synchronized Object pop()
    {
        if (size > 0)
        {
            size--;
            StackNode temp = head;
            head = temp.next;
            temp.next = null;
            return temp.item;
        }
        return null;
    }

        /**
         * Get the size of the stack.
         */
    public int getSize()
    {
        return size;
    }

        /**
         * A node in the stack.
         */
    public class StackNode
    {
            /**
             * The actual item being stored.
             */
        public Object item;
        public StackNode next;

            /**
             * Constructor.
             */
        public StackNode(Object o)
        {
            this.item = o;
        }
    }
}
