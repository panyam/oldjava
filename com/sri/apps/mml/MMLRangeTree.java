package com.sri.apps.mml;

import java.util.*;
import java.awt.*;

/**
 * A Tree of ranges.  Implementation is a Red-Black tree as 
 * we need fast searches and reasonably fast insertions and deletions.
 *
 * Implementation from: http://softlab.od.ua/algo/data/rb2/s_rbt.txt
 */
public class MMLRangeTree
{
        /**
         * Root of this tree.
         */
    protected MMLStyleNode root = null;

        /**
         * Constructor.
         */
    public MMLRangeTree(MMLStyleRange range)
    {
        if (range == null)
            throw new IllegalArgumentException("MMLStyleRange cannot be null");

            // the root MUST be black
        root = new MMLStyleNode(range, null, true);
    }

        /**
         * Given a line and a position find the
         * appropriate StyleRange object.
         * Recursion is replaced with usage of an explicit
         * stacks as each recursive function call expensive.
         */
    public MMLStyleRange find(int line, int pos)
    {
        MMLStyleNode curr = root;
        while (curr != null)
        {
            int comp = curr.range.compare(line, pos);
            if (comp == 0) return curr.range;
            else if (comp < 0 && curr.left != null) curr = curr.left;
            else if (curr.right != null) curr = curr.right;
        }
        return null;
    }

        /**
         * Insert a style range into this tree.
         */
    public void insert(MMLStyleRange sr)
    {
        MMLStyleNode x = null;
        MMLStyleNode curr = root;
        while (x == null)
        {
            int comp = curr.range.compare(sr);
            if (comp == 0) return ;
            else if (comp < 0)
            {
                if (curr.left != null) curr = curr.left;
                else x = (curr.left = new MMLStyleNode(sr, curr, false));
            } else
            {
                if (curr.right != null) curr = curr.right;
                else x = (curr.right = new MMLStyleNode(sr, curr, false));
            }
        }

        insertFixup(x);
    }
        /**
         * Delete a style range from this tree.
         */
    public void delete(MMLStyleRange sr)
    {
        MMLStyleNode x = null, y = null, z = root;
loop:
        while (z != null)
        {
            int comp = z.range.compare(sr);
            if (comp == 0) break loop;
            else if (comp < 0) z = z.left;
            else z = z.right;
        }

        if (z == null) return ;

        if (z.left == null || z.right == null)
        {
            y = z;
        } else
        {
            y = z.right;
            while (y.left != null) y = y.left;
        } 

                // x is y's only child
        if (y.left != null)
        {
            x = y.left;
        } else
        {
            x = y.right;
        }

                // remove y from parent chain
        x.parent = y.parent;
        if (y.parent != null)
        {
            if (y == y.parent.left)
            {
                y.parent.left = x;
            } else
            {
                y.parent.right = x;
            }
        } else
        {
            root = x;
        }

        if (y != z)
        {
            z.range = y.range;
        }

        if (y.isBlack)
        {
            deleteFixup(x);
        }

        y = null;
    }

        /**
         * Maintain red-black tree balance after deleting a node x
         */
    protected void deleteFixup(MMLStyleNode x)
    {
        MMLStyleNode w, z;

        while (x != root && x.isBlack)
        {
            if (x == x.parent.left)
            {
                w = x.parent.right;
                if (!w.isBlack)
                {
                    w.isBlack = true;
                    x.parent.isBlack = false;
                    rotateLeft(x.parent);
                }
                if (w.left.isBlack && w.right.isBlack)
                {
                    w.isBlack = false;
                    x = x.parent;
                } else
                {
                    if (w.right.isBlack)
                    {
                        w.left.isBlack = true;
                        w.isBlack = false;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    w.isBlack = x.parent.isBlack;
                    x.parent.isBlack = true;
                    w.right.isBlack = true;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else
            {
                w = x.parent.left;
                if (!w.isBlack)
                {
                    w.isBlack = true;
                    x.parent.isBlack = false;
                    rotateRight(x.parent);
                }
                if (w.right.isBlack && w.left.isBlack)
                {
                    w.isBlack = false;
                    x = x.parent;
                } else
                {
                    if (w.left.isBlack)
                    {
                        w.right.isBlack = true;
                        w.isBlack = false;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    w.isBlack = x.parent.isBlack;
                    x.parent.isBlack = true;
                    w.left.isBlack = true;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.isBlack = true;
    }

        /**
         * Maintaining red-black tree balance after inserting x
         */
    protected void insertFixup(MMLStyleNode x)
    {
        MMLStyleNode y = null;
        while (x != root && !x.parent.isBlack)
        {
            if (x.parent == x.parent.parent.left)
            {
                y = x.parent.parent.right;
                if (!y.isBlack)
                {
                    x.parent.isBlack = true;
                    y.isBlack = true;
                    x.parent.parent.isBlack = false;
                    x = x.parent.parent;
                } else
                {
                    if (x == x.parent.parent)
                    {
                        x = x.parent;
                        rotateLeft(x);
                    }
                    x.parent.isBlack = true;
                    x.parent.parent.isBlack = false;
                    rotateRight(x.parent.parent);
                }
            } else
            {
                y = x.parent.parent.left;
                if (!y.isBlack)
                {
                    x.parent.isBlack = true;
                    y.isBlack = true;
                    x.parent.parent.isBlack = false;
                    x = x.parent.parent;
                } else
                {
                    if (x == x.parent.parent)
                    {
                        x = x.parent;
                        rotateRight(x);
                    }
                    x.parent.isBlack = true;
                    x.parent.parent.isBlack = false;
                    rotateLeft(x.parent.parent);
                }
            }
        }
        root.isBlack = true;
    }

        /**
         * Do a left rotation of a subset of the tree
         */
    protected void rotateLeft(MMLStyleNode x)
    {
        MMLStyleNode y = x.right;

        x.right = y.left;

        if (y.left != null) y.left.parent = x;

        if (y != null) y.parent = x.parent;

        if (x.parent != null)
        {
            if (x == x.parent.left)
            {
                x.parent.left = y;
            } else
            {
                x.parent.right = y;
            }
        } else
        {
            root = y;
        }

        y.left = x;
        if (x != null) x.parent = y;
    }

        /**
         * Do a right rotation of a subset of the tree
         */
    protected void rotateRight(MMLStyleNode x)
    {
        MMLStyleNode y = x.left;

        x.left = y.right;

        if (y.right != null) y.right.parent = x;

        if (y != null) y.parent = x.parent;

        if (x.parent != null)
        {
            if (x == x.parent.right)
            {
                x.parent.right = y;
            } else
            {
                x.parent.left = y;
            }
        } else
        {
            root = y;
        }

        y.right = x;
        if (x != null) x.parent = y;
    }

        /**
         * A class to maintain each node of the style tree.
         */
    class MMLStyleNode
    {
            /**
             * The left sub tree.
             */
        public MMLStyleNode left;

            /**
             * The right subtree.
             */
        public MMLStyleNode right;

            /**
             * The parent node.
             */
        public MMLStyleNode parent;

            /**
             * The actual style range.
             */
        public MMLStyleRange range;

            /**
             * Tells if the node is black or red.
             */
        public boolean isBlack = false;

            /**
             * Constructor.
             */
        public MMLStyleNode(MMLStyleRange range, MMLStyleNode parent, boolean black)
        {
            this.range = range;
            this.isBlack = black;
            left = right = null;
        }
    }
}
