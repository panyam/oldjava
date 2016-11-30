package svtool.core.adt;

import java.util.*;

/**
 * A Linked list object.
 */
public class LinkedList
{
        // when a node is removed it will be stored here 
        // so that new objects arent unnecessarily created!!!
    static Vector nodePool = new Vector();
    static Integer nodeMutex = new Integer(0);

    public LLNode head;
    public LLNode tail;
    protected int nItems;

        /**
         * Constructor.
         */
    public LinkedList()
    {
        head = tail = null;
        nItems = 0;
    }

        /**
         * Appends an item into the list.
         */
    public void addItem(Object item)
    {
        LLNode newNode;
        synchronized (nodeMutex)
        {
            if (nodePool.size() > 0)
            {
                newNode = (LLNode)nodePool.lastElement();
                newNode.key = item;
                newNode.next = newNode.prev = null;
                nodePool.removeElementAt(nodePool.size() - 1);
            } else
            {
                newNode = new LLNode(item);
            }
        }

        if (head == null)
        {
            head = tail = newNode;
        } else
        {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        nItems++;
    }

        /**
         * Removes a given node.
         */
    public void removeItem(LLNode node)
    {
        LLNode prev = node.prev;
        LLNode next = node.next;

        if (prev != null) prev.next = next;
        else head = next;

        if (next != null) next.prev = prev;
        else tail = prev;
        nItems--;
        synchronized (nodeMutex)
        {
            nodePool.addElement(node);
        }
    }

        /**
         * Remove all items in the list.
         */
    public void removeAllItems()
    {
        head = tail = null;
        nItems = 0;
    }

        /**
         * Removes all nodes within a range - start and end nodes are
         * included in the removal.
         */
    public void removeItemBetween(LLNode startNode, LLNode endNode)
    {
        int numRemoved = 0;
        if (startNode == null) startNode = head;
        if (endNode == null) endNode = tail;

        for (LLNode temp = startNode;
             temp != null && temp != endNode;
             temp = temp.next, numRemoved++);

        LLNode prev = startNode.prev;
        LLNode next = endNode.next;

        if (prev != null) prev.next = next;
        else head = next;

        if (next != null) next.prev = prev;
        else tail = prev;

        nItems -= numRemoved;
        synchronized (nodeMutex)
        {
            for (LLNode node = startNode;
                    node != endNode.next;node = node.next)
            {
                nodePool.addElement(node);
            }
        }
    }

        /**
         * Get the size of the list.
         */
    public int size()
    {
        return nItems;
    }
}
