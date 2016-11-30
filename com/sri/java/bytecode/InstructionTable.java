package com.sri.java.bytecode;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.attribute.*;

/**
 * This is basically an object that makes
 * it easy to tell the offset of the ith instruction.
 */
public class InstructionTable
{
        /**
         * A table offsets where
         * each of the offsets[i] 
         * is the offset into the code
         * array of the begining of 
         * instruction i.
         */
    protected int offsets[] = null;
        
        /**
         * Number of instructions.
         */
    public int numInstrs = 0;

        /**
         * Code attribute.
         */
	public CodeAttribute codeAttr = null;
	
        /**
         * The actual code array.
         */
    public byte code[];

        /**
         * This constructor is usually called if
         * the byte code is being generated.
         */
    public InstructionTable(SJavaClass parentClass) throws IOException
    {
        this.codeAttr = new CodeAttribute(parentClass);
        code = new byte[1024];
        //setCode(code,len);
    }

    public InstructionTable(CodeAttribute ca,DataInputStream din) throws IOException
    {
		this.codeAttr = ca;
        int len = din.readInt();
        code = new byte[len];
        din.readFully(code,0,len);
        setCode(code,len);
    }

        /**
         * The constructor.
         */
    public InstructionTable(CodeAttribute ca,byte code[],int codeLength)
    {
		this.codeAttr = ca;
        setCode(code,codeLength);
    }

        /**
         * Sets the code bytes array and
         * the array is parsed for 
         * start of instruction offsets.
         */
    public void setCode(byte code[],int codeLength)
    {
        this.code = code;
        int pc = 0;
        int opcode = 0;
            // the number of instructions...
        numInstrs = 0;

        final int sizeIncr = 10;

        offsets = new int[20];

        for (;pc < codeLength;)
        {
            opcode = code[pc] & 0xff;

            if (offsets.length == numInstrs)       // if array full then need to resize it.
            {
                int newArray[] = new int[offsets.length + sizeIncr];
                System.arraycopy(offsets,0,newArray,0,numInstrs);
                offsets = newArray;
            }
            offsets[numInstrs++] = pc++;

            switch (opcode)
            {
                case SJavaOpCode.LOOKUPSWITCH :
                {
                    while (pc % 4 != 0) pc++;   // skip null bytes.
                    
                    int def = (code[pc++] << 24) | 
                              (code[pc++] << 16) |
                              (code[pc++] << 8) |
                               code[pc++];

                    int npairs = (code[pc++] << 24) | 
                                 (code[pc++] << 16) |
                                 (code[pc++] << 8) |
                                  code[pc++];

                        // now there are npairs number of match-offset pairs.
                        // so each pair takes 8 bytes 4 for the match key and
                        // 4 for the offset.  So we need a total 8 * npairs
                        // bytes.
                    pc += (npairs << 3);
                } break;
                case SJavaOpCode.TABLESWITCH:
                {
                    while (pc % 4 != 0) pc++;   // skip null bytes.
                    int def = 0;
                    int low = 0;
                    int hi = 0;

                    def = (code[pc++] << 24) | 
                          (code[pc++] << 16) |
                          (code[pc++] << 8) |
                           code[pc++];

                    low = (code[pc++] << 24) | 
                          (code[pc++] << 16) |
                          (code[pc++] << 8) |
                           code[pc++];

                    hi  = (code[pc++] << 24) | 
                          (code[pc++] << 16) |
                          (code[pc++] << 8) |
                           code[pc++];

                        // now after the 4 bytes for the hi value
                        // we have hi - low + 1 integer index
                        // so that is (hi - low + 1) * 4 bytes.
                    pc += ((hi - low + 1) << 2);
                } break;
                case SJavaOpCode.WIDE:
                {
                    pc += (opcode == SJavaOpCode.IINC ? 5 : 3);
                } break;
                default :
                {
                    pc += SJavaOpCode.numParameters[opcode];
                } break;
            }
        }

            // garbage collect coz after all those newArray 
            // creations, there will be quite a lot of wasted
            // memory.
        System.gc();
    }

        /**
         * Returns the opcode of the ith instruction.
         */
    public final int getInstruction(int i)
    {
        return code[offsets[i]];
    }

        /**
         * Returns the offset of the ith instruction.
         */
    public final int getInstructionOffset(int i)
    {
        return offsets[i];
    }

        /**
         * Given an offset of a byte into the code array,
         * tells the index of the instruction that is just after
         * this offset.
         *
         * eg:  if the offsets are 0, 3, 5, 8, 10.
         * And if offset is 4.  This function would return 2,
         * indicating the instruction 2 is the one that is after 
         * byte 4.
         *
         * This is done simply using a binary search.
         *
         * The parameter inclusive tells if this offset it self
         * should be included in the search.  If the offset is
         * 5, then if inclusive is true, the return value is 2
         * (for the second instruction).
         */
    public int indexAfter(int offset,boolean inclusive)
    {
        int lo = 0;
        int hi = numInstrs - 1;
        int mid = 0;

        while (true)
        {
            if (offsets[hi - 1] < offset)
            {
                
            }
            mid = ((lo + hi) >> 1);
            if (offsets[mid] > offset) hi = mid;
            else lo = mid;
        }
    }

        /**
         * Given an offset of a byte into the code array,
         * tells the index of the instruction that is just before
         * this offset
         *
         * eg:  if the offsets are 0, 3, 5, 8, 10.
         * And if offset is 4.  This function would return 1,
         * indicating the instruction 1 is the one that is before byte 4.
         *
         * This is done simply using a binary search.
         *
         * The parameter inclusive tells if this offset it self
         * should be included in the search.  If the offset is
         * 5, then if inclusive is true, the return value is 2
         * (for the second instruction).
         */
    public int indexBefore(int offset,boolean inclusive)
    {
        int lo = 0;
        int hi = numInstrs - 1;
        int mid = 0;

        while (true)
        {
            mid = ((lo + hi) >> 1);
            if (offsets[mid] > offset) hi = mid;
            else lo = mid;
        }
    }

        /**
         * Given an offset into the code array,
         * returns the index of the instruction.
         * If there is no isntruction at this
         * offset, then it is an invalid offset.
         * This function is called when we usually
         * want to compute the target of jump statements.
         * So if there is no isntruction at this
         * offset, it means we have an invalid
         * jump target.  So we just
         * throw an exception.
         */
    public int getInstructionAt(int offset)
    {
        int lo = 0;
        int hi = numInstrs - 1;
        int mid = 0;

        while (true)
        {
            if (lo == hi)
            {
                if (offsets[lo] == offset) return lo;
                else throw new IllegalArgumentException ("Invalid offset.");
            } else if (lo == hi - 1)
            {
                if (offsets[lo] == offset) return lo;
                else if (offsets[hi] == offset) return hi;
                else throw new IllegalArgumentException ("Invalid offset.");
            }
            mid = ((lo + hi) >> 1);     // right shift = divide by 2
            if (offsets[mid] < offset) lo = mid;
            else if (offsets[mid] > offset) hi = mid;
            else return mid;
        }
    }

    public void printInstructions(DataOutputStream dout,int from,int to,int level) throws IOException
    {
		String front = "";
		for (int i = 0;i < level;i++) front += "    ";

        for (int i = from;i <= to;i++)
        {
            int off = offsets[i];
            int opCode = code[off] & 0xff;
			int nP = SJavaOpCode.numParameters[opCode];
			int param = 0;
			
			for (int j = 1;j <= nP;j++) param = (param << 8) | code[off + j];

            dout.writeBytes(front + "(" + i + ")" + "    " + off + "    " + SJavaOpCode.opcodeNames[opCode] + "    ");
			if (nP > 0) dout.writeBytes(param + "\n");
			else dout.writeBytes("\n");
        }
    }
}
