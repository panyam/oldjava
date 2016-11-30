package com.sri.utils.adt;

import java.util.*;

/**
 * Defines a binary tree data structure.
 */
public class BTreeNode extends TreeNode
{
        /**
         * Default Constructor
         */
    public BTreeNode()
    {
        this(null, null);
    }

        /**
         * Constructor
         */
    public BTreeNode(TreeNode parent)
    {
        this(null, parent);
    }

        /**
         * Default Constructor
         */
    public BTreeNode(Object key)
    {
        this(key, null);
    }

        /**
         * Constructor
         */
    public BTreeNode(Object key, TreeNode parent)
    {
        super(key, parent);

            // make sure we have enough space for exactly two kids
        ensureCapacity(2);

        kids[0] = kids[1] = null;
    }

        /**
         * Compares an object.
         */
    public BTreeNode insert(Object obj, ObjectComparer comparer)
    {
        return this.insert(new BTreeNode(obj), comparer);
    }

        /**
         * Inserts a tree node at the given spot.
         */
    public BTreeNode insert(BTreeNode btn, ObjectComparer comparer)
    {
            // should we make this check???  to save time
        if (btn == null || comparer == null) return null;

        BTreeNode curr = this;
        BTreeNode out = btn;
        int res = comparer.compare(key, btn.key);

        while (curr != null)
        {
            if (res < 0)
            {
                    // then insert into the left subtree
                if (kids[0] == null)
                {
                    kids[0] = btn;
                    btn.parent = curr;
                    curr = null;
                } else 
                {
                    curr = (BTreeNode)kids[0];
                }
            } else if (res > 0)
            {
                    // then insert into the right subtree
                if (kids[1] == null)
                {
                    kids[1] = btn;
                    btn.parent = curr;
                    curr = null;
                } else 
                {
                    curr = (BTreeNode)kids[1];
                }
            } else 
            {
                out = curr;
                curr = null;
            }
        }
        return out;
    }
}
