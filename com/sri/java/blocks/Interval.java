package com.sri.java.blocks;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;
import com.sri.java.attribute.*;
import com.sri.java.utils.*;

/**
 * A class that holds information about an interval.
 * The first block of the interval set is the header
 * node.
 */
public class Interval extends Block
{
	
	Vector blocks = new Vector();
	
		/**
		 * Constructor.
		 */
	public Interval(Block header)
    {
        super(-1);
		blocks.addElement(header);
    }
	
		/**
		 * Constructor.
		 */
	public Interval(int id,Block header)
    {
        super(id);
		blocks.addElement(header);
    }
    
		/**
		 * Adds a new block to the list.
		 */
	public void addBlock(Block bl)
	{
		if (!blocks.contains(bl)) blocks.addElement(bl);
	}
	
    public void print(DataOutputStream dout, int level) throws IOException	{
		String front = Formats.getSpacingString(level + 1);	
        Block currHeader = blockAt(0);
		super.print(dout,level);
        dout.writeBytes(front + "Interval @ " + currHeader.id + " -> ");
        for (int j = 0;j < size();j++)
        {
            Block bl = blockAt(j);
            dout.writeBytes(bl.id + ", ");
        }
        dout.writeBytes("\n");
	}
	
	public boolean contains(Object bl)
	{
		return blocks.contains(bl);
	}
	
	public int size()
	{
		return blocks.size();
	}
	
	public Block blockAt(int i)
	{
		return (Block)blocks.elementAt(i);
	}

        /**
         * Adds a successor block.
         */
    public void addSuccessor(Block bl)
    {
        if (!next.contains(bl) && bl instanceof Interval) next.addElement(bl);
    }
	
        /**
         * Adds a predecessor block.
         */
    public void addPredecessor(Block bl)
    {
        if (!prev.contains(bl) && bl instanceof Interval) prev.addElement(bl);
    }
}
