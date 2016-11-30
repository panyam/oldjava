package com.sri.java.blocks;

import java.io.*;
import java.util.*;

/**
 * A general graph object.
 */
public class Graph
{	
		/**
		 * Is the graph reducible...
		 */
	protected boolean isReducible = true;
	
		/**
		 * For any block, this array tells which interval
		 * this block is held in.
		 */
	protected short blockIn[] = null;

        /**
         * The root block.
         */
    protected Block rootNode = null; 

        /**
         * One thing about all the blocks is
         * that each block's first and last
         * instructions dont over lap.
         *
         * So these instructions an be stored
         * in a sorted order.
         */
    protected Vector blocks = new Vector();
	
        /**
         * The intervals of this block graph.
         * 
         * What are intervals?
         * -------------------
         * Given a node h, an interval I(h) is the maximal, single-entry 
         * subgraph in which h is the only entry node and in which all 
         * closed paths contain h.  The unique interval node h is 
         * called the interval head or simply the header node.
         * 
         * By selecting the correct of header nodes, G can be partitioned
         * into a unique set of disjoint intervals I = {I(h1), I(h2)..I(hn)}.
         */
    protected Vector intervals = new Vector();
		/**
		 * Null default constructor.
		 */
	protected Graph()
	{
	}
	
		/**
		 * Given a set of blocks, creates a graph
		 * of blocks.
		 */
	public Graph(Vector blcks)
	{
		rootNode = (Block)blcks.elementAt(0);
		for (Enumeration e = blcks.elements();e.hasMoreElements();)
		{
			blocks.addElement(e.nextElement());
		}
		this.numberBlocks(rootNode);
		blockIn = new short[blocks.size()];
		for (int i = 0;i < blockIn.length;i++) blockIn[i] = -1;
		generateIntervals();
	}
	
		/**
		 * Tells if the requested block
		 * is within this graph.
		 */
	public boolean contains(Block bl)
	{
		return blocks.contains(bl);
	}
    
        /**
         * This function generates all the intervals for
         * this Graph (N, E, h) where N is the set of 
         * nodes, E is the set of edges and h is the
         * root node.
         */
    public void generateIntervals()
    {
		for (int i = 0;i < blockIn.length;i++) blockIn[i] = -1;
		
		Vector headers = new Vector();
        intervals.removeAllElements();
        headers.addElement(rootNode);

        for (int i = 0; i < headers.size();i++)
        {
            Block currHeader = (Block)headers.elementAt(i);
			
				// insert the new header node into the list of
				// header nodes.  Insert in sorted order of
				// ids.
			int j = 0;
			
			Interval interval = null;
			short currIntervalIndex = blockIn[currHeader.id];
            if (currIntervalIndex < 0)
            {
                interval = new Interval(i,currHeader);
				currIntervalIndex = blockIn[currHeader.id] = (short)intervals.size();
				intervals.addElement(interval);				
            } 
			interval = (Interval)intervals.elementAt(currIntervalIndex);
        
            updateIntervalList2(interval,currHeader);
            
                // now to the list H add all blocks m from the
                // set of Nodes, if m does not already belong
                // to H or interval and atleast of the
                // predecessors of m belongs to interval.
            for (j = 0;j < blocks.size();j++)
            {
                Block bl = (Block)blocks.elementAt(j);
                if (!headers.contains(bl) && blockIn[bl.id] != interval.id)//!interval.contains(bl))
                {
                    Vector preds = bl.prev;
                    boolean add = false;
                    for (int k = 0;!add && k < preds.size();k++)
                    {
                        add = interval.contains(preds.elementAt(k)); 
                    }
                    if (add) 
					{
						headers.addElement(bl);
					}
                }
            }
        }

			/**
			 * After the intervals have been set, we also 
			 * need to find each interval's successor and
			 * predecessor intervals.
			 */
		for (int i = 0;i < blocks.size();i++)
		{
			Block curr = (Block)blocks.elementAt(i);
			int fromInt = blockIn[curr.id];
			Block from = (Block)intervals.elementAt(fromInt);
			int toInt = 0;
			for (int j = 0;j < curr.getNumSuccessors();j++)
			{
				Block succ = curr.getSuccessor(j);
				toInt = blockIn[succ.id];
				if (succ.id != curr.id && toInt != fromInt)
				{
					Block to = (Block)intervals.elementAt(toInt);
					from.addSuccessor(to);
					to.addPredecessor(from);
				}
			}
		}
    }

        /**
         * Creates/Updates the actual interval list.
         * 
         * Consider all of the header block's successors.
         * For each successor see if all the predecessor
         * is in the interval list.  If it is, then add
         * THAT successor to the interval list and put
         * all the successor of that successor in our
         * queue and keep going until all successors
         * can be added.  So it is basically a BFS.
         */
    protected void updateIntervalList(Interval interval,Block header)
    {
		Vector rejected = new Vector();	// holds all nodes that rejected
		Vector temp = new Vector();
		int which = blockIn[header.id];

        for (int i = 0;i < interval.size();i++)
        {
            Block curr = interval.blockAt(i);
			if (!rejected.contains(curr))
			{
				Vector next = curr.next;
				for (int j = 0;j < next.size();j++)
				{
				    Block block = (Block)next.elementAt(j);
				    if (blockIn[block.id] == -1 && !rejected.contains(block))
				    {
				        Vector preds = block.prev;
				        boolean add = true;
				        for (int k = 0;add && k < preds.size();k++) 
				        {
				            add = interval.contains(preds.elementAt(k));
				        }
				        if (add) 
						{
							if (!temp.contains(block)) temp.addElement(block);
						}
				        else rejected.addElement(block);
				    }
				}
				
				for (int k = 0;k < temp.size();k++) 
				{
					Block t = (Block)temp.elementAt(k);
					interval.addBlock(t);
					blockIn[t.id] = (short)which;
				}
				temp.removeAllElements();
			}
        }
        rejected.removeAllElements();
		temp.removeAllElements();
		temp = null;
		rejected = null;
    }
	
    protected void updateIntervalList2(Interval interval,Block header)
    {
		Vector rejected = new Vector(), queue = new Vector();
		int bs = blocks.size();
		for (int i = 0;i < bs;i++) 
		{
			Block curr = (Block)blocks.elementAt(i);
			//if (!interval.contains(curr) && !rejected.contains(curr)) 
		    if (blockIn[curr.id] < 0 && !rejected.contains(curr))
			{
				boolean temp = curr.directPathExistsTo(header);
				if (temp) 
				{
					queue.addElement(curr);
					for (int j = 0;j < queue.size();j++)
					{
						Block bl = (Block)queue.elementAt(j);
						if (blockIn[bl.id] != interval.id)
						{
							blockIn[bl.id] = (short)interval.id;
							interval.addBlock(bl);
						}
							// now add all predecessors to the queue...
						int np = bl.getNumPredecessors();
						for (int k = 0;k < np;k++)
						{
							Block currPred = bl.getPredecessor(k);
							if (blockIn[currPred.id] != interval.id)
							{
								queue.addElement(currPred);
							}
						}
					}
				} else
				{
					rejected.addElement(curr);
				}
			}
		}
		rejected.removeAllElements();
    }

		/**
		 * If all the predecessors of curr can be reached from header,
		 * then curr is added to interval, along with all of curr's 
		 * predecessors and their predecessors and so on.
		 */
	protected void checkBlockPreds(Interval interval, Block curr, Block header, Vector queue)
	{
	}
	
		/**
		 * Removes all the intervals.
		 * So that some space can be cleared.
		 */
	public void freeIntervals()
	{
		this.intervals.removeAllElements();
		System.gc();
	}
	
		/**
		 * Returns the intervals of this graph.
		 * If the intervals have not been generated yet, 
		 * they are generated.
		 */
	public Vector getIntervals()
	{
		if (intervals.isEmpty()) this.generateIntervals();
		return this.intervals;
	}

        /**
         * Given the root node all the blocks
         * are numbered startign from 0.
         * The ordering is basically a 
         * depth first search of the whole
         * block graph.
         */
    protected void numberBlocks(Block rootNode)
    {
        Stack stack = new Stack();
        Vector visited = new Vector();
        stack.addElement(rootNode);
        int id = 0;
        while (!stack.isEmpty())
        {
            Block curr = (Block)stack.pop();
            if (!visited.contains(curr))
            {
                visited.addElement(curr);
                curr.id = id++;
                Vector succs = curr.next;
                for (int i = succs.size() - 1;i >= 0;i--)
                //for (int i = 0;i < succs.size();++i)
                {
                    Object next = succs.elementAt(i);
                    if (!visited.contains(next)) stack.push(next);
                }
            }
        }
        blocks.removeAllElements();
        for (Enumeration e = visited.elements();e.hasMoreElements();blocks.addElement(e.nextElement()));
    }
	
		/**
		 * Tells if this graph is reducible or not.
		 */
	public boolean isReducible()
	{
		return this.isReducible;
	}

    public void print(DataOutputStream dout,int level) throws IOException
    {
        String front = "";
        for (int i = 0;i < level;i++) front += "    ";

        dout.writeBytes(front + "#Blocks = " + blocks.size() + "\n");
        for (int i = 0;i < blocks.size();i++)
        {
            ((Block)blocks.elementAt(i)).print(dout,level + 1);
		}
		for (int j = 0;j < intervals.size();j++) ((Interval)intervals.elementAt(j)).print(dout,level);
    }

		/**
		 * Returns the number of nodes in this graph.
		 */
	public int getBlockCount()
	{
		return blocks.size();
	}
	
		/**
		 * Returns the interval that contains the specified block.-
		 */
	public Interval findIntervalOfBlock(Block bl)
	{
		if (intervals.contains(bl))
		{
			return (Interval)intervals.elementAt(blockIn[bl.id]);
		}
		return null;
	}
	
		/**
		 * Tells if this is trivial graph or not.
		 */
	public boolean isTrivial()
	{
		return blocks.size() == 1;
	}
}
