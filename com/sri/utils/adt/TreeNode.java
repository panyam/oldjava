
package com.sri.utils.adt;

import java.util.*;

/**
 * Defines a tree data structure with arbitrary number of nodes and 
 * a parent node.
 */
public class TreeNode
{
        /**
         * Parent node.
         */
    public TreeNode parent = null;

        /**
         * Child nodes.
         */
    public TreeNode kids[] = new TreeNode[2];
    public int nKids = 0;

        /**
         * Value of the node.
         */
    public Object key;

        /**
         * Default Constructor
         */
    public TreeNode()
    {
        this(null, null);
    }

        /**
         * Constructor
         */
    public TreeNode(TreeNode parent)
    {
        this(null, parent);
    }

        /**
         * Default Constructor
         */
    public TreeNode(Object key)
    {
        this(key, null);
    }

        /**
         * Constructor
         */
    public TreeNode(Object key, TreeNode parent)
    {
        this.key = key;
        this.parent = parent;
    }

        /**
         * Get the indexed child node.
         */
    public TreeNode getChildNode(int index)
    {
        return kids[index];
    }

        /**
         * Gets the height of the tree.
         */
    public int getHeight()
    {
        int maxHeight = 0;

            // find the height of all the child trees
        for (int i = 0;i < nKids;i++)
        {
            maxHeight = Math.max(maxHeight, kids[i].getHeight());
        }
        return 1 + maxHeight;
    }

        /**
         * Gets the total number of nodes in this tree.
         */
    public int getTreeSize()
    {
        int total = 1;
        for (int i = 0;i < nKids;i++)
        {
            total += kids[i].getTreeSize();
        }
        return total;
    }

        /**
         * Ensure we have the given capacity to add child nodes.
         */
    public final void ensureCapacity(int newCapacity)
    {
        if (kids == null) kids = new TreeNode[newCapacity];

        if (kids.length < newCapacity)
        {
            TreeNode p2[] = kids;
            kids = new TreeNode[newCapacity + 2];
            System.arraycopy(p2, 0, kids, 0, nKids);
        }
    }

        /**
         * Insert a tree node at a given spot.
         */
    public void insertChild(TreeNode node, int index)
    {
        ensureCapacity(nKids + 1);

        System.arraycopy(kids, index, kids, index + 1, nKids - index);
        kids[index] = node;
        nKids++;
    }

        /**
         * Add a child node.
         */
    public final void addChildNode(TreeNode node)
    {
        if (node == null) return ;
        ensureCapacity(nKids + 1);

        node.parent = this;
        kids[nKids++] = node;
    }
}
