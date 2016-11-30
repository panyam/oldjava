package com.sri.java.blocks;

import com.sri.java.bytecode.*;
import com.sri.java.utils.*;
import java.util.*;
import java.io.*;

/**
 * Represents a basic block of intermediate code.
 */
public abstract class Block 
{
		/**
		 * No loop.
		 */
	public final static byte NONE = -1;

		/**
		 * A Pre tested loop.
		 */
	public final static byte PRE_TESTED_LOOP = 0;

		/**
		 * A Post tested loop.
		 */
	public final static byte POST_TESTED_LOOP = 2;

		/**
		 * An endless loop.
		 */
	public final static byte ENDLESS_LOOP = 3;
	
		/**
		 * A block that has two exit points..
		 * as for all if statements..
		 */
	public final static byte IF_BLOCK = 4;
	
		/**
		 * A block whose last instruction is a switch
		 * statement.
		 */
	public final static byte SWITCH_BLOCK = 5;
	
        /**
         * A block that simply follows thru.
         * ie the last instruction is not a jump
         * instruction.
         */
    public final static byte FOLLOW_BLOCK = 6;
	
        /**
         * A block that contains an unconditional
         * jump instruction as the last instruction.
         * eg goto.
         */
    public final static byte GOTO_BLOCK = 7;

        /**
         * A block that leads no where. ie it has no
         * other successor blocks.
         */
    public final static byte END_BLOCK = 8;

		/**
		 * The integer id of this block.
		 */
    int id;
	
		/**
		 * The type of loop.
		 */
    byte blockType = NONE;

		/**
		 * Is this block inside a loop.
		 */
	boolean inLoop = false;
	
        /**
         * Set of successor blocks.
         */
    protected Vector next = new Vector(5,2);

        /**
         * Set of predecessor blocks.
         */
    protected Vector prev = new Vector(5,2);

        /**
         * Constructor.
         *
         * @param   The id of the block.
         * @param   The first instruction index.
         * @param   The last instruction index.
         * @param   The Instruction table that contains
         *          all the instructions.
         */
    protected Block(int id)
    {
		this.id = id;
    }

        /**
         * Adds a successor block.
         */
    public void addSuccessor(Block bl)
    {
        if (!next.contains(bl)) next.addElement(bl);
    }

		/**
		 * Returns the number of successors.
		 */
	public int getNumSuccessors()
	{
		return next.size();
	}
	
        /**
         * Adds a predecessor block.
         */
    public void addPredecessor(Block bl)
    {
        if (!prev.contains(bl)) 
        {
            prev.addElement(bl);
        }
    }

		/**
		 * Returns the number of predecessors.
		 */
	public int getNumPredecessors()
	{
		return prev.size();
	}
        /**
         * Removes a predecessor block.
         */
    public void removePredecessor(Block bl)
    {
        if (prev.contains(bl)) prev.removeElement(bl);
    }

		/**
		 * Tells if this block is a descendant of block bl.
		 */
	public boolean isDescendantOf(Block block)
	{
		Block curr = this;
		Vector visited = new Vector();
		Stack stack = new Stack();
		stack.push(curr);
		while (!stack.isEmpty())
		{
			curr = (Block)stack.pop();
			if (curr == block) return true;
			if (!visited.contains(curr))
			{
				visited.addElement(curr);
				for (int i = 0;i < curr.prev.size();i++)
				{
					Object bl = curr.prev.elementAt(i);
					if (bl == block) return true;
					if (!visited.contains(bl)) stack.push(bl);
				}
			}
		}
		return false;
	}
	
		/**
		 * Tells if bl is one of the successors.
		 */
	public boolean goesTo(Block bl)
	{
		return next.contains(bl);
	}
	
		/**
		 * Tells if bl is one of the predecessors.
		 */
	public boolean comesFrom(Block bl)
	{
		return prev.contains(bl);
	}
	
	public void print(DataOutputStream dout, int level) throws IOException
	{
		String front = Formats.getSpacingString(level);
		dout.writeBytes(front + "Block #" + id + " : ");
		if (!next.isEmpty())
		{
			dout.writeBytes(" -> ");
			for (int j = 0;j < next.size();j++)
			{
			    Block b = (Block)next.elementAt(j);
			    dout.writeBytes("(" + b.id + "), ");
			}
			dout.writeBytes(" | ");
		}
			
		if (!prev.isEmpty())
		{
			dout.writeBytes("<- ");
			for (int j = 0;j < prev.size();j++)
			{
			    Block b = (Block)prev.elementAt(j);
			    dout.writeBytes("(" + b.id + "), ");
			}
		}
		dout.writeBytes("\n");
        dout.writeBytes(front + "--------\n");
	}
	
	public Block getSuccessor(int i)
	{
		return (Block)next.elementAt(i);
	}
	
	public Block getPredecessor(int i)
	{
		return (Block)prev.elementAt(i);
	}
	
	public boolean directPathExistsTo(Block bl)
	{
		Vector queue[] = { new Vector(), new Vector() };
		Vector temp = new Vector();
		
		int which = 0, next = 1, cs,np;
		queue[which].addElement(this);
		int p[] = {0, 0};
		
		cs = queue[which].size();
		int ns = queue[next].size();
		while (ns > p[next] || cs > p[which])
		{
			for (;p[which] < cs;p[which]++)
			{
				Block curr = (Block)queue[which].elementAt(p[which]);
				
				np = curr.getNumPredecessors();
				for (int j = 0;j < np;j++)
				{
					Block pred = curr.getPredecessor(j);
					if (pred != bl)
					{
						if (!queue[next].contains(pred)) 
						{
							if (!temp.contains(pred)) temp.addElement(pred);
						}
						else return false;
					}
				}
			}
			for (int i = 0;i < temp.size();i++) queue[next].addElement(temp.elementAt(i));
			temp.removeAllElements();
			which = next;
			next = (which + 1) % 2;
			cs = queue[which].size();
			ns = queue[next].size();
		}
		return true;
	}
}
