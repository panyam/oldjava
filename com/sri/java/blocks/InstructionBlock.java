package com.sri.java.blocks;

import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;
import com.sri.java.attribute.*;
import java.io.*;
import java.util.*;

/**
 * A block that holds instructions.
 * 
 * This instruction block also has higher level expression trees 
 * corresponding to individual expression that are formed as part 
 * of the code...
 */
public class InstructionBlock extends Block
{
    public int firstInstr;             // the index of hte first instruction
                                // in this block.

    public int lastInstr;              // the index of the last instruction
                                // in this block.
	
		/**
		 * If there is a loop, then the followNode indicates
		 * the node that is induced, as soon as the loop is 
		 * terminated.  This follow node is also used for 
		 * 2 way branch statement s(ie if an dif else) and for
		 * switch statements.
		 */
	Block followNode = null;
	
		/**
		 * This is nonnull only if theblock type is
		 * an IF_BLOCK.  IN this case, ifNode 
		 * represents the block that is reached on 
		 * the success of the condition.
		 */
	Block ifNode = null;

        /**
         * The instruction table.
         */
    InstructionTable iTable = null;

		/**
		 * The list of expressions that contain the instructions
		 * in this block.  If the last instruction instruction is
		 * a goto this is instruction wont be part of the 
		 * expression list.  conditional jump instructions are
		 * converted as well.
		 */
	ExpressionList expr = null;
	
        /**
         * Constructor.
         *
         * @param   The id of the block.
         * @param   The first instruction index.
         * @param   The last instruction index.
         * @param   The Instruction table that contains
         *          all the instructions.
         */
    public InstructionBlock(int id, int fInstr,int lInstr,InstructionTable iTable)
    {
		super(id);
        firstInstr = fInstr;
        lastInstr = lInstr;
        this.iTable = iTable;
		blockType = FOLLOW_BLOCK;
    }

        /**
         * Tells if a certain offset into the code array
         * is contained within the first and the last 
         * instruction offsets.
         */
    public boolean containsOffset(int offset)
    {
        return (iTable.getInstructionOffset(firstInstr) <= offset &&
                offset <= iTable.getInstructionOffset(lastInstr));
    }

	public boolean isSwitchBlock()
	{
		int li = iTable.getInstruction(lastInstr);
		return (li == SJavaOpCode.LOOKUPSWITCH || li == SJavaOpCode.TABLESWITCH);
	}
	
        /**
         * Splits the block at the given index and returns
         * the second half.
         * The block is only split if the instruction is not
         * the first instruction in the block.
         * If the block is not split, then a null value
         * is returned to indicate that no block was split.
         */
    public Block split(int instr)
    {
		if (instr > firstInstr)
		{
		    InstructionBlock splitted = new InstructionBlock(-1,instr,lastInstr,iTable);
				
				// now we copy all of the current block's successor
				// to the split block.  This is because, 
				// we HAVE to fall thru to the split block.
				// And all the successor blocks can only be
				// reached by going thru the split block.
			for (int i = 0;i < next.size();i++) 
            {
                Block bl = (Block)next.elementAt(i);
                bl.removePredecessor(this);
                bl.addPredecessor(splitted);
                splitted.addSuccessor(bl);
            }
			next.removeAllElements();
			addSuccessor(splitted);
	        splitted.addPredecessor(this);
	        lastInstr = instr - 1;
		    return splitted;
		}
		return null;
    }

    public void print(DataOutputStream dout,int level) throws IOException
    {
		super.print(dout,level);
        iTable.printInstructions(dout,firstInstr,lastInstr,level + 1);
    }
	
	public Expression getExpression()
	{
		return this.expr;
	}
	
		/**
		 * Creates the high level expression list representing all the
		 * byte code instructions in this block.
		 * 
		 * Initially the hashtable could be empty or all variables have
		 * names x_lv_i where x is the local variable type and i is the 
		 * ith local variable.  x has to be one of i,f,d,l or a, for 
		 * integer, float, double, long or reference type respectively.
		 * 
		 * As we realise more local variables from the instruction table,
		 * we put these into the hashtable or if it is already in the 
		 * hashtable, we look at the type and put that type in the hastable.
		 * 
		 * @param	The constant pool table.
		 * @param	The code attribute object to which this block belongs
		 * @param	A table containing information about all the local variables.
		 */
	public void createExpression(ConstantInfo[] cpool,CodeAttribute ca, Hashtable lvInfo)
	{
			// now we also determine the block type...
		int opcode = ca.iTable.getInstruction(lastInstr);
		
		if (opcode == SJavaOpCode.TABLESWITCH || opcode == SJavaOpCode.LOOKUPSWITCH) blockType = SWITCH_BLOCK;
		else if (SJavaOpCode.instructionType[opcode] == SJavaOpCode.CONDITIONAL_INSTRUCTION) blockType = IF_BLOCK;
		else if (opcode == SJavaOpCode.JSR || opcode == SJavaOpCode.JSR_W ||
				 opcode == SJavaOpCode.GOTO || opcode == SJavaOpCode.GOTO_W) blockType = GOTO_BLOCK;
		
        expr = new ExpressionList();
        Stack stack = new Stack();            // the stack of the code...
		
		for (int i = firstInstr;i < lastInstr;i++)
		{
			ExpressionGenerator.processOpcode(stack, cpool, ca, lvInfo, i);
		}
	}
}
