package com.sri.java.blocks;

import com.sri.java.*;
import com.sri.java.expression.*;
import com.sri.java.attribute.*;
import com.sri.java.bytecode.*;
import com.sri.java.utils.*;
import java.util.*;
import java.io.*;

/**
 * Represents a block graph.  Each block
 * graph is pretty much a list of blocks.
 */
public class BlockGraph extends Graph
{
		/**
		 * The instruction table that holds all the instructions
		 * that are represented by this block graph.
		 */
    InstructionTable iTable = null;

		/**
		 * The derived sequence of this graph.
		 * It contains G, G1, G2... Gn
		 * 
		 * A derived sequence of a graph is a set of sub sections of 
		 * this graph that contain all the intervals as nodes.  So G1 is 
		 * G itself.  G2 (the second order graph) is all the intervals 
		 * collapsed into a node.  The immediate predecessors of the 
		 * collapsed nodes are immediate predecessors of the original
		 * header node which are not part of the interval.  The 
		 * immediate successors are all he immediate non-interval 
		 * successors of the original exit nodes.  Intervals of G2 are 
		 * computer with the interval algorithm and the graph 
		 * construction process is repeated until a limit flow graph
		 * Gn is reached.  Gn has the property of being a trivial graph
		 * ie a single node or an irreducible graph.
		 */
	protected Vector derivedSequence = new Vector();
    
        /**
         * Constructor.
         *
         * Takes the actual code bytes and an instruction
         * table that holds the offsets of each
         * instruction.
         */
    public BlockGraph(CodeAttribute ca)
    {
        this.iTable = ca.iTable;
        doFirstPass();
        doSecondPass();

        int ind = findBlockWithOffset(0);    // the block that has byte 0.
        if (ind >= 0)
        {
            this.rootNode = (Block)blocks.elementAt(ind);
        }
		
		blockIn = new short[blocks.size()];
		for (int i = 0;i < blockIn.length;i++) blockIn[i] = -1;
		
			/**
			 * Number all the nodes in terms of their
			 * DF search order.
			 */
        numberBlocks(rootNode);

			/**
			 * create all the intervals...
			 */
        generateIntervals();
		
			/**
			 * Create the derived sequence,
			 * G1..Gn.
			 */
		generateDerivedSequence();
		
			/**
			 * Now we structure all the swtich
			 * statements
			 */
		//structureNWayNodes();
    }
	
        /**
         * The first pass of block graph generation.
         * for the java byte code, we only consider
         * the following instructions as jump 
         * instructions.
         *     Conditional:
         *         ifeq, ifne, iflt, ifge, ifgt, ifle, if_icmpeq, if_icmpne,
         *         if_icmplt, if_icmpge, if_icmpgt, if_icmple
         *         ifnull, ifnonnull, if_acmpeq, if_acmpne,
         *     UnConditional:
         *         jsr, goto, goto_w, jsr_w, breakpont,
         *            tableswitch, lookupswitch
         *    since this is the first pass, we will create
         *    a enw block and use that as the current block.
         *    and at this stage we also wont add
         *    predecessor and sucessor nodes to the
         *    blocks.
         */
    //int id = 0;
    protected void doFirstPass()
    {
        int pc = 0;
        InstructionBlock currBlock = null;
        InstructionBlock prevBlock = null;
        for (int i = 0;i < iTable.numInstrs;i++)
        {
            pc = iTable.getInstructionOffset(i);
            int opcode = iTable.code[pc] & 0xff;

            if (currBlock == null) 
            {
                currBlock = new InstructionBlock(0,i,i,iTable);
                if (prevBlock != null)
                {
                    prevBlock.addSuccessor(currBlock);
                    currBlock.addPredecessor(prevBlock);
                }
            }
            else currBlock.lastInstr++;

            if (SJavaOpCode.instructionType[opcode] == SJavaOpCode.CONDITIONAL_INSTRUCTION || 
                opcode == SJavaOpCode.JSR || opcode == SJavaOpCode.JSR_W ||
                opcode == SJavaOpCode.GOTO || opcode == SJavaOpCode.GOTO_W ||
                opcode == SJavaOpCode.TABLESWITCH || opcode == SJavaOpCode.LOOKUPSWITCH)
            {
                prevBlock = null;
                if (SJavaOpCode.instructionType[opcode] == SJavaOpCode.CONDITIONAL_INSTRUCTION)
                {
                    prevBlock = currBlock;
                } 
                blocks.addElement(currBlock);
                currBlock = null;
            }
        }
        if (currBlock != null) blocks.addElement(currBlock);
    }
    
        /**

         * The second pass of block graph creation.
         * Now we go through the second pass.  Here is
         * where we look at the last instruction of each
         * block.  If this last instruction is a jump
         * instruction then we get the instrs' jump
         * target and verify it and split any blocks
         * if necessary.  In this pass we also add
         * predecessor and sucessor blocks to 
         * the blocks as needed.
         */

    protected void doSecondPass()
    {
        int pc = 0, opcode = 0;
        byte code[] = iTable.code;
        
        for (int i = 0;i < blocks.size();i++)
        {
            InstructionBlock currBlock = (InstructionBlock)blocks.elementAt(i);
            
            pc = iTable.getInstructionOffset(currBlock.lastInstr);
            opcode = iTable.code[pc] & 0xff;
            if (SJavaOpCode.instructionType[opcode] == SJavaOpCode.CONDITIONAL_INSTRUCTION || 
                opcode == SJavaOpCode.JSR || opcode == SJavaOpCode.JSR_W ||
                opcode == SJavaOpCode.GOTO || opcode == SJavaOpCode.GOTO_W)
            {
                int nParams = SJavaOpCode.numParameters[opcode];

                    // calcualte the target of the branch
                int target = code[pc + 1];
                for (int j = 2;j <= nParams;j++) target = ((target << 8) | (code[pc + j] & 0xff));
                target += pc;

                addSuccessorTo(currBlock,target);
            } else if (opcode == SJavaOpCode.TABLESWITCH || opcode == SJavaOpCode.LOOKUPSWITCH)
            {
                        // table switch and lookup switch needs a bit more care...
                        // as they have multiple exit paths, whereas normal
                        // blocks have only exit path. but the idea is the
                        // same as every block that you find will be split
                        // accordingly.
                int oldPC = pc;
                while (pc % 4 != 0) pc++;        // skip null bytes.
                int def = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                if (opcode == SJavaOpCode.TABLESWITCH) {
                    int lo = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                    int hi = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                    for (int j = lo;j <= hi;j++)
                    {
                        int offset = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                        addSuccessorTo(currBlock,oldPC + offset);
                    }
                } else 
                {
                    int npairs = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                    for (int j = 0;j < npairs;j++)
                    {
                        pc += 4;        // for the match value...
                        int offset = (code[pc++] << 24) | (code[pc++] << 16) | (code[pc++] << 8) | code[pc++];
                        addSuccessorTo(currBlock,oldPC + offset);
                    }
                }
            }
        }
    }

    protected void addSuccessorTo(Block currBlock,int target)
    {
            // the block that contains the requested offset...
        int targetBlockIndex = findBlockWithOffset(target);
        InstructionBlock targetBlock = null;

            // the instruction index at the target offset.
        int targetInstr = iTable.getInstructionAt(target);
                
            // if no block was found then we have an offset
            // that is pointing outside the method so it is invalid.
        if (targetBlockIndex < 0)    // then ok
        {
            throw new IllegalArgumentException("Invalid branch target.");
        } else
        {    
                // other wise we have a valid target.
            targetBlock = (InstructionBlock)blocks.elementAt(targetBlockIndex);

                // otherwise we need to split the target block at
                // the specified instruction
            Block splitBlock = targetBlock.split(targetInstr);

            if (splitBlock != null)
            {
                //splitBlock.id = id++;
                currBlock.addSuccessor(splitBlock);
                splitBlock.addPredecessor(currBlock);
                blocks.addElement(splitBlock);
            } else
            {
                currBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currBlock);
            }
        }
    }
    
        /**
         * Given an offset, tells which of the blocks
         * in this graph, contain this offset.
         */
    public int findBlockWithOffset(int offset)
    {
        for (int i = 0;i < blocks.size();i++)
        {
            Block bl = (Block)blocks.elementAt(i);
            if ((bl instanceof InstructionBlock) && 
				((InstructionBlock)bl).containsOffset(offset)) return i;
        }
        return -1;
    }
	
		/**
		 * Generates the derived sequence
		 * of this block graph.
		 */
	public void generateDerivedSequence()
	{
		if (intervals.isEmpty()) generateIntervals();
		derivedSequence.addElement(this);		// G1 is this...
		Graph currG = this;
		loop:
		while (!currG.isTrivial())
		{
			Graph nextG = new Graph(currG.intervals);
			if (nextG.getBlockCount() != currG.getBlockCount())
			{
				derivedSequence.addElement(nextG);
				currG = nextG;
			} else break loop;			
		}
	}
	
    public void print(DataOutputStream dout,int level) throws IOException
    {
		String front = Formats.getSpacingString(level);
		super.print(dout,level);
		dout.writeBytes(front + "Derived Sequence: \n");
		for (int i = 1;i < this.derivedSequence.size();i++)
		{
			Graph g = (Graph)derivedSequence.elementAt(i);
			dout.writeBytes(front + "G: " + i + " = \n" + front + "----------\n");
			g.print(dout,level + 1);
		}
	}
	
	public void structureNWayNodes()
	{
		Vector unresolved = new Vector();
		Vector nodes = getPostOrderNodes();
		
			/**
			 * Ok now we know that all the blocks
			 * are instruction blocks.
			 */
		for (int i = 0;i < nodes.size();i++)
		{
			InstructionBlock b = (InstructionBlock)nodes.elementAt(i);
			if (b.isSwitchBlock())
			{
				
			}
		}
	}
	
		/**
		 * All loops are identified and marked.
		 */
	public void structureLoops()
	{
		for (int i = 0;i < this.derivedSequence.size();i++)
		{
			Graph gi = (Graph)this.derivedSequence.elementAt(i);
			Vector ints = gi.getIntervals();
			Vector nodes = gi.blocks;
			for (int j = 0;j < ints.size();j++)
			{
				Interval inti = (Interval)ints.elementAt(j);
				Block hj = inti.blockAt(0);		// the header of hte current interval.
				int numP = hj.getNumPredecessors();
				
				for (int k = 0;k < numP;k++)
				{
					Block bk = hj.getPredecessor(k);
					if (bk.blockType == Block.NONE && blockIn[bk.id] == blockIn[hj.id])
					{
							// mark all nodes between bk and hj as in loop.
						markNodesBetween(bk.id,hj.id);
						int mn = Math.min(bk.id,hj.id);
						int mx = Math.max(bk.id,hj.id);
						for (int l = mn;l <= mx;l++)
						{
								// mark as true only if in the same interval.
							if (blockIn[l] == blockIn[hj.id])
							{
								((Block)blocks.elementAt(l)).inLoop = true;
							}
						}
					}
				}
				
				//hj.loopType = findLoopType(
			}
		}
	}
	
		/**
		 * Marks all nodes between x and h as between
		 * loop nodes.
		 */
	protected void markNodesBetween(int x,int h)
	{
	}
	
		/**
		 * Gets all the nodes in post order.
		 */
	public Vector getPostOrderNodes()
	{
		Vector out = new Vector();
		addNodes(rootNode,out);
		return out;
	}
	
	protected void addNodes(Block root,Vector list)
	{
		if (root.getNumSuccessors() == 0) list.addElement(root);
		else
		{
			int ns = root.getNumSuccessors();
			for (int i = 0;i < ns;i++) 
			{
				Block bl = root.getSuccessor(i);
				if (!list.contains(bl)) addNodes(root.getSuccessor(i),list);
			}
		}
	}
}
