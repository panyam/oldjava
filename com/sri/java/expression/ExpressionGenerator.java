package com.sri.java.expression;

import java.io.*;
import java.util.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.blocks.*;
import com.sri.java.bytecode.*;
import com.sri.java.attribute.*;

/**
 * A class that takes a block of code and generates the
 * appropriate Expression tree from it.
 */
public class ExpressionGenerator
{
	static SJavaLocalVariable getLocalVariable(Hashtable lvInfo, int prefix, int index)
	{
		return (SJavaLocalVariable)lvInfo.get(SJavaLocalVariable.getSampleName(prefix,index));
	}
	
		/**
		 * Given the hashtable of local variables, the type of the local variable
		 * the index of it and the type of the variable, adds it to the
		 * hashtable of local variables.  If the lv with type and index already
		 * exists, 
		 */
	static SJavaLocalVariable loadVariable(Hashtable lvInfo, int prefix,
                               int index, VariableType vType)
	{
			// so we are using local var lv_<type>_<index>
		String name = SJavaLocalVariable.getSampleName(prefix,index);
		
            // now there are two things... one
            // if the object already exists...
            // the other is if it does not..
            // if the name already exists, then
            // we add a enw type to its table..
            // otherwise we create one and 
            // add in a type...
		if (!lvInfo.containsKey(name))
		{
			SJavaLocalVariable info = new SJavaLocalVariable(name);
			lvInfo.put(name,info);
		}
		SJavaLocalVariable info = (SJavaLocalVariable)lvInfo.get(name);			// now it cannot be null
		
		if (info.moreTypesAddable)	
		{		// if more types can be added.. ie 
				// the type is now known for sure
				// then we add it to it..
			info.addType(vType);
		}
		return info;
	}
	
        /**
         * So this function takes the expression list, the stack of expressions,
         * and code attributes and the constant pool and processes a give 
         * instruction.  In this process, the expression list and the stack
         * are also changed.  
         * 
         * At times we may have to choose between pushing onto the stack or
         * reducing the current expression.  In this case, we push only if
         * the stack size limit has not been reached (given by the maximum
         * stack size)...
         * 
         * TODO::  After every instruction, we must see what expression's were  used/modified and add more information regarding its type...
         * Most information for this step can be found in a function call expression, 
         * when we evaluate input types and so on.
         */
    public static void processOpcode(Stack stack, ConstantInfo[] cpool, 
									 CodeAttribute ca, Hashtable lvInfo,
									 int which)
    {
		boolean wide = false;		// says if the current opcode
                                    // needs to be widened...
        LNTableAttribute lns = ca.lnTable;
        LVTableAttribute lvs = ca.lvTable;
        InstructionTable iTable = ca.iTable;
        byte code[] = iTable.code;
        int pc = iTable.getInstructionOffset(which);
        int opcode = iTable.getInstruction(which);
        Expression out = null;

        switch (opcode)
        {
					/**
					 * The following load load values from
					 * local variables.  Here is where we 
					 * need to access and modify the lvInfo
					 * hashtable.
					 * The trouble is which lv are we referring to?
					 * ie there could be a lv_i_0, lv_f_0, lv_a_0..
					 * Which off these to choose.. this is what we must decide
					 * so il comback to it later...
                     * To decide this, we need to know the return type of
                     * this function.  Once we know that, we can 
                     * decide which of the lv_t's to choose.
                     *
                     * So if the lv is of an object type, then we also 
                     * see that the local variable, has the type which
                     * equals the output type of this method.  And
                     * now we need to analyse all the present types
                     * and choose the most appropriate one.
					 */
			case SJavaOpCode.RET :
			{
				//int which = code[pc + 1] & 0xff;
				//TODO::  The Return statements...
			} break;
            case SJavaOpCode.IINC :            // ++ 
            {
				int index = code[pc + 1] & 0xff;
				int cnst = code[pc + 1] & 0xff;

				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.INT_PREFIX,index,BasicType.INT_TYPE);
				
					// now we need an expression of the type:
					// info += const...
				BinaryExpression exp = new BinaryExpression();
				exp.create(BinaryExpression.ADD_EQ,info,CValueExpression.bytes[cnst]);
				
					// push it on the stack...
				stack.push(exp);
            } break;
            case SJavaOpCode.FLOAD_0 : case SJavaOpCode.FLOAD_1 : case SJavaOpCode.FLOAD_2 : case SJavaOpCode.FLOAD_3 : 
            {
				int index = opcode - SJavaOpCode.FLOAD_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.FLOAT_PREFIX,index,BasicType.FLOAT_TYPE);
					// now we know that info HAS to be of a float type...
				info.removeAllTypes();
				info.addType(BasicType.FLOAT_TYPE);
				info.moreTypesAddable = false;
				
                stack.push(info.identExp);
            } break;
            case SJavaOpCode.LLOAD_0 : case SJavaOpCode.LLOAD_1 : case SJavaOpCode.LLOAD_2 : case SJavaOpCode.LLOAD_3 : 
            {
				int index = opcode - SJavaOpCode.LLOAD_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.LONG_PREFIX,index,BasicType.LONG_TYPE);
					// now we know that info HAS to be of a long type...
				info.removeAllTypes();
				info.addType(BasicType.LONG_TYPE);
				info.moreTypesAddable = false;
				
                stack.push(info.identExp);
            } break ;
            case SJavaOpCode.DLOAD_0 : case SJavaOpCode.DLOAD_1 : case SJavaOpCode.DLOAD_2 :  case SJavaOpCode.DLOAD_3 :
            {
				int index = opcode - SJavaOpCode.DLOAD_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.DOUBLE_PREFIX,index,BasicType.DOUBLE_TYPE);
					// now we know that info HAS to be of a double type...
				info.removeAllTypes();
				info.addType(BasicType.DOUBLE_TYPE);
				info.moreTypesAddable = false;
				
                stack.push(info.identExp);
            } break;
            case SJavaOpCode.ILOAD_0 : case SJavaOpCode.ILOAD_1 : case SJavaOpCode.ILOAD_2 : case SJavaOpCode.ILOAD_3 : 
            {
				int index = opcode - SJavaOpCode.ILOAD_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.INT_PREFIX,index,BasicType.INT_TYPE);
					// however, here we cant be sure that the type is of an int as
					// it can also be a short or a byte or a char or an in...
                stack.push(info.identExp);
            } break;
            case SJavaOpCode.ALOAD_0 : case SJavaOpCode.ALOAD_1 : case SJavaOpCode.ALOAD_2 : case SJavaOpCode.ALOAD_3 :
            {
				int index = opcode - SJavaOpCode.ALOAD_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.REF_PREFIX,index,ObjectType.OBJECT_TYPE);
                stack.push(info.identExp);
            } break;
            case SJavaOpCode.ALOAD : 
            {
                int index = code[pc + 1] & 0xff;    // the offset into the local variable table...
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.REF_PREFIX,index,ObjectType.OBJECT_TYPE);
                stack.push(info.identExp);
            } break;
			case SJavaOpCode.FLOAD : 
			{
                int index = code[pc + 1] & 0xff;    // the offset into the local variable table...
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.FLOAT_PREFIX,index,BasicType.FLOAT_TYPE);
                stack.push(info.identExp);
			} break;
			case SJavaOpCode.ILOAD : 
			{
                int index = code[pc + 1] & 0xff;    // the offset into the local variable table...
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.INT_PREFIX,index,BasicType.INT_TYPE);
                stack.push(info.identExp);
			} break;
			case SJavaOpCode.LLOAD :
			{
                int index = code[pc + 1] & 0xff;    // the offset into the local variable table...
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.LONG_PREFIX,index,BasicType.LONG_TYPE);
                stack.push(info.identExp);
			} break;
			case SJavaOpCode.DLOAD :
			{
                int index = code[pc + 1] & 0xff;    // the offset into the local variable table...
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.DOUBLE_PREFIX,index,BasicType.DOUBLE_TYPE);
                stack.push(info.identExp);
			} break;
			  
					/**
					 * The following store instructiosn store
					 * values into local variables.
					 * Here is where we need to access 
					 * and modify the lvInfo hashtable.
					 * Same deal as loads ... how ever, now we actually
					 * put in a binary expression like a = b...
					 */
            case SJavaOpCode.FSTORE_0 : case SJavaOpCode.FSTORE_1 :
            case SJavaOpCode.FSTORE_2 : case SJavaOpCode.FSTORE_3 : 
            {
				int index = opcode - SJavaOpCode.FSTORE_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.FLOAT_PREFIX,index,BasicType.FLOAT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.ISTORE_0 : case SJavaOpCode.ISTORE_1 :
            case SJavaOpCode.ISTORE_2 : case SJavaOpCode.ISTORE_3 : 
            {
				int index = opcode - SJavaOpCode.ISTORE_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.INT_PREFIX,index,BasicType.INT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.DSTORE_0 : case SJavaOpCode.DSTORE_1 : case SJavaOpCode.DSTORE_2 :
            case SJavaOpCode.DSTORE_3 :  
            {
				int index = opcode - SJavaOpCode.DSTORE_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.DOUBLE_PREFIX,index,BasicType.DOUBLE_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.LSTORE_0 : case SJavaOpCode.LSTORE_1 : case SJavaOpCode.LSTORE_2 : case SJavaOpCode.LSTORE_3 : 
            {
				int index = opcode - SJavaOpCode.LSTORE_0;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.LONG_PREFIX,index,BasicType.LONG_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.ASTORE_0 : case SJavaOpCode.ASTORE_1 : case SJavaOpCode.ASTORE_2 :
            case SJavaOpCode.ASTORE_3 :
            {
				int index = opcode - SJavaOpCode.ASTORE_0;
				// TODO::  Here we need to actually use the correct type insted of OBJECT_TYPE..
				// i think this can be done by checkin the popped expression's type and using
				// as the type.  However, i need to check it more...
				// the same goes for all the astore instructions....
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.REF_PREFIX,index,ObjectType.OBJECT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.ASTORE : 
            {
				int index = code[pc + 1] & 0xff;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.REF_PREFIX,index,ObjectType.OBJECT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,info.identExp,(ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
			case SJavaOpCode.ISTORE :
            {
				int index = code[pc + 1] & 0xff;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.INT_PREFIX,index,BasicType.INT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
            case SJavaOpCode.DSTORE : 
            {
				int index = code[pc + 1] & 0xff;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.DOUBLE_PREFIX,index,BasicType.DOUBLE_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
			case SJavaOpCode.LSTORE : 
            {
				int index = code[pc + 1] & 0xff;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.LONG_PREFIX,index,BasicType.LONG_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
			case SJavaOpCode.FSTORE : 
            {
				int index = code[pc + 1] & 0xff;
				SJavaLocalVariable info = loadVariable(lvInfo,SJavaLocalVariable.FLOAT_PREFIX,index,BasicType.FLOAT_TYPE);
                BinaryExpression assign = new BinaryExpression();
                assign.create(BinaryExpression.ASSIGN,
                              (ValuedExpression)info.identExp,
                              (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;

							/**
							 * Local variable modify instructions
							 * are now over.
							 */
					/**
					 * push constants from cpool table onto the stack...
					 * with these load constant information no info 
					 * about any of the local variables can be found out...
					 */
            case SJavaOpCode.LDC : case SJavaOpCode.LDC_W : case SJavaOpCode.LDC2_W : 
            {
				int index = code[pc + 1] & 0xff;
				if (opcode != SJavaOpCode.LDC) index = (index << 8) | (code[pc + 2] & 0xff);
				byte tag = cpool[index].tag;
				CValueExpression cnst = new CValueExpression();
				if (tag == ConstantInfo.CONSTANT_DOUBLE) 
					cnst.value = new Double(cpool[index].getDoubleValue());
				else if (tag == ConstantInfo.CONSTANT_LONG) 
					cnst.value = new Long(cpool[index].getLongValue());
				else if (tag == ConstantInfo.CONSTANT_FLOAT) 
					cnst.value = new Float(cpool[index].getFloatValue());
				else if (tag == ConstantInfo.CONSTANT_INTEGER) 
					cnst.value = new Integer(cpool[index].getIntValue());
				else if (tag == ConstantInfo.CONSTANT_UTF8) 
					cnst.value = cpool[index].getString();
				stack.push(cnst);
            } break;
			  
				// the following load instructtions dont give much information
				// about the variabls coz we are only loading stuff TO the stack..
			case SJavaOpCode.AALOAD : case SJavaOpCode.BALOAD : case SJavaOpCode.CALOAD :
            case SJavaOpCode.DALOAD :  case SJavaOpCode.FALOAD : case SJavaOpCode.LALOAD :
            case SJavaOpCode.IALOAD : case SJavaOpCode.SALOAD :
            {                        // loads a reference from array...  
                                    // the operands are the array name and the
                                    // index into the array. and the value is
                                    // put on the stack... curr at this stage
                                    // should be a null and would become an
                                    // array reference expression...
                out = new BinaryExpression();
                ValuedExpression  indexExp = (ValuedExpression)stack.pop();
                Object arrayRef = stack.pop();
                ((BinaryExpression)out).create(BinaryExpression.ARRAY_ACCESS,
                                               arrayRef,indexExp);
                stack.push(out);
            } break;
				// this could give some info about local vars...
				//
            case SJavaOpCode.AASTORE : case SJavaOpCode.BASTORE : case SJavaOpCode.CASTORE :
            case SJavaOpCode.DASTORE : case SJavaOpCode.FASTORE : case SJavaOpCode.LASTORE :
            case SJavaOpCode.IASTORE : case SJavaOpCode.SASTORE : 
            {                        // stores into a reference array.
                                    // so the expression would be a assignment
                                    // expression whose left child is an array
                                    // access and right is a normal one..
                ValuedExpression value = (ValuedExpression)stack.pop();
                ValuedExpression index = (ValuedExpression)stack.pop();
                ValuedExpression array = (ValuedExpression)stack.pop();
                    
                BinaryExpression arrayAcc = new BinaryExpression();

                arrayAcc.create(BinaryExpression.ARRAY_ACCESS,array,index);
                BinaryExpression ass = new BinaryExpression();

                ass.create(BinaryExpression.ASSIGN,arrayAcc,value);
				stack.push(ass);
            } break;
			  
					/**
					 * The following instructions push constants
					 * onto the stack.
					 */
            case SJavaOpCode.ACONST_NULL : 
            {
                stack.push(CValueExpression.NULL);
            } break;
			case SJavaOpCode.BIPUSH : case SJavaOpCode.SIPUSH :
            {
                if (opcode == SJavaOpCode.BIPUSH) stack.push(CValueExpression.bytes[code[pc + 1]]);
				else stack.push(new CValueExpression(new Short((short)(((code[pc + 1] & 0xff) << 8) | (code[pc + 2] & 0xff)))));
            } break;
            case SJavaOpCode.DCONST_0 : case SJavaOpCode.DCONST_1 :
            {
                stack.push(CValueExpression.doubles[opcode - SJavaOpCode.DCONST_0]);
            } break;
            case SJavaOpCode.FCONST_0 : case SJavaOpCode.FCONST_1 : case SJavaOpCode.FCONST_2 : 
            {
                stack.push(CValueExpression.floats[opcode - SJavaOpCode.FCONST_0]);
            }break;
            case SJavaOpCode.ICONST_M1 : case SJavaOpCode.ICONST_0 : case SJavaOpCode.ICONST_1 :
            case SJavaOpCode.ICONST_2 : case SJavaOpCode.ICONST_3 :  case SJavaOpCode.ICONST_4 :
            case SJavaOpCode.ICONST_5 : 
            {
                stack.push(CValueExpression.ints[opcode - SJavaOpCode.ICONST_0]);
            } break;
            case SJavaOpCode.LCONST_0 : case SJavaOpCode.LCONST_1 : 
            {
                stack.push(CValueExpression.longs[opcode - SJavaOpCode.LCONST_0]);
            } break;
			  
						/**
						 * The following instructiosn
						 * do type conversion between various
						 * integer/long/short/double/char/boolean
						 * types.
						 */
            case SJavaOpCode.I2B : case SJavaOpCode.I2C : case SJavaOpCode.I2S : 
            {
                BinaryExpression cast = new BinaryExpression();
				BasicType bt = BasicType.BYTE_TYPE;
				if (opcode == SJavaOpCode.I2C) bt = BasicType.CHAR_TYPE;
				else if (opcode == SJavaOpCode.I2S) bt = BasicType.SHORT_TYPE;
                cast.create(BinaryExpression.CASTING,
                            BasicType.BYTE_TYPE,
                            (ValuedExpression)stack.pop());
                stack.push(cast);
            } break;
			  
						/**
						 * Convert from float/int/long to double
						 */
            case SJavaOpCode.F2D : case SJavaOpCode.I2D :  case SJavaOpCode.L2D : 
            {
                BinaryExpression cast = new BinaryExpression();
                cast.create(BinaryExpression.CASTING,
                            BasicType.DOUBLE_TYPE,
                            (ValuedExpression)stack.pop());
                stack.push(cast);
            } break;
			  
						/**
						 * Convert from double/int/long to float
						 */
            case SJavaOpCode.D2F : case SJavaOpCode.I2F : case SJavaOpCode.L2F : 
            {
                BinaryExpression cast = new BinaryExpression();
                cast.create(BinaryExpression.CASTING,
                            BasicType.FLOAT_TYPE,
                            (ValuedExpression)stack.pop());
                stack.push(cast);
            } break;
			  
						/**
						 * Convert from float/double/long to int
						 */
            case SJavaOpCode.L2I : case SJavaOpCode.D2I : case SJavaOpCode.F2I : 
            {
                BinaryExpression cast = new BinaryExpression();
                cast.create(BinaryExpression.CASTING,
                            BasicType.INT_TYPE,
                            (ValuedExpression)stack.pop());
                stack.push(cast);
            } break;
			  
						/**
						 * Convert from float/int/double to long
						 */
            case SJavaOpCode.D2L : case SJavaOpCode.F2L : case SJavaOpCode.I2L : 
            {
                BinaryExpression cast = new BinaryExpression();
                cast.create(BinaryExpression.CASTING,
                            BasicType.LONG_TYPE,
                            (ValuedExpression)stack.pop());
                stack.push(cast);
            } break;
		
					/**
					 * Put a new array onto the stack.
					 */
			case SJavaOpCode.NEW:
			{
				int index = (code[pc + 1] << 8) | code[pc + 2];
				FunctionExpression fe = new FunctionExpression();
				fe.isConstructor = true;
				//TODO::  This must be in junction with invokespecial.
				// so when u do an invoke special, it should be after
				// a NEW instruction.
			} break;
			case SJavaOpCode.ANEWARRAY : case SJavaOpCode.NEWARRAY : 
            {
                ValuedExpression count = (ValuedExpression)stack.pop();
                int index = (code[pc + 1] << 8) | code[pc + 2];
                ArrayCreaterExpression newArray = new ArrayCreaterExpression();
                VariableType vt = null;
				if (opcode == SJavaOpCode.ANEWARRAY)
				{
					int ni = cpool[index].getClassOrNameIndex();
					String typeName = cpool[ni].getString();
					vt = new ObjectType(typeName);
				} else
				{
					int atype = code[pc + 1] & 0xff;
					if (atype == 4) vt = BasicType.BOOLEAN_TYPE;
					else if (atype == 5) vt = BasicType.CHAR_TYPE;
					else if (atype == 6) vt = BasicType.FLOAT_TYPE;
					else if (atype == 7) vt = BasicType.DOUBLE_TYPE;
					else if (atype == 8) vt = BasicType.BYTE_TYPE;
					else if (atype == 9) vt = BasicType.SHORT_TYPE;
					else if (atype == 10) vt = BasicType.INT_TYPE;
					else if (atype == 11) vt = BasicType.LONG_TYPE;
				}
                newArray.create(vt,count);
                stack.push(newArray);
            } break;
					
					/**
					 * Calculates array length...
					 */
            case SJavaOpCode.ARRAYLENGTH : 
            {
                ValuedExpression arrayRef = (ValuedExpression)stack.pop();
                BinaryExpression arrlength = new BinaryExpression();
                arrlength.create(BinaryExpression.DOT_FIELD,arrayRef,CValueExpression.LENGTH);
            } break;
			  
				/**
				 * For instructions that transfer control
				 * from the function or the scope.
				 */
            case SJavaOpCode.ARETURN : case SJavaOpCode.DRETURN : case SJavaOpCode.FRETURN : 
			case SJavaOpCode.LRETURN : case SJavaOpCode.IRETURN : case SJavaOpCode.ATHROW : 
			case SJavaOpCode.RETURN :
            {
                RTBExpression rtbExp = new RTBExpression();
				if (opcode == SJavaOpCode.ATHROW)
				{
	                rtbExp.create(RTBExpression.THROW,(ValuedExpression)stack.pop());
				} else if (opcode == SJavaOpCode.RETURN)
				{
					rtbExp.create(RTBExpression.RETURN,null);
				} else
				{
	                rtbExp.create(RTBExpression.RETURN,(ValuedExpression)stack.pop());
				}
				stack.push(rtbExp);
            } break;

			case SJavaOpCode.CHECKCAST : 
            {
                int index = ((code[pc + 1] << 8) | code[pc + 2]) & 0xffffffff;
            } break;
			  
					/**
					 * The following instructions do binary operations
					 * on expressions.
					 */
					/**
					 * Additions....
					 */
            case SJavaOpCode.DADD : case SJavaOpCode.FADD :  case SJavaOpCode.IADD : case SJavaOpCode.LADD :
            {
                BinaryExpression add = new BinaryExpression();
                add.create(BinaryExpression.ADD,
                           (ValuedExpression)stack.pop(),
                           (ValuedExpression)stack.pop());
                stack.push(add);
            } break;
			  
					/**
					 * Divisions....
					 */
            case SJavaOpCode.DDIV:case SJavaOpCode.FDIV:case SJavaOpCode.IDIV:case SJavaOpCode.LDIV:
            {
                BinaryExpression div = new BinaryExpression();
                div.create(BinaryExpression.DIV,
                           (ValuedExpression)stack.pop(),
                           (ValuedExpression)stack.pop());
                stack.push(div);
            } break;
			  
					/**
					 * Multiplications....
					 */
            case SJavaOpCode.DMUL : case SJavaOpCode.FMUL : case SJavaOpCode.IMUL : case SJavaOpCode.LMUL :  
            {
                BinaryExpression mul = new BinaryExpression();
                mul.create(BinaryExpression.MUL,
                           (ValuedExpression)stack.pop(),
                           (ValuedExpression)stack.pop());
                stack.push(mul);
            } break;
			  
					/**
					 * Unary negations....
					 */
            case SJavaOpCode.DNEG : case SJavaOpCode.FNEG : case SJavaOpCode.INEG : case SJavaOpCode.LNEG :
            {
                UnaryExpression neg =
                   new UnaryExpression(UnaryExpression.NEG,
                                       (ValuedExpression)stack.pop());
                stack.push(neg);
            } break;
			  
					/**
					 * Subtractions....
					 */
            case SJavaOpCode.DSUB : case SJavaOpCode.FSUB : case SJavaOpCode.ISUB : case SJavaOpCode.LSUB :  
            {
                BinaryExpression sub = new BinaryExpression();
                sub.create(BinaryExpression.SUB,
                           (ValuedExpression)stack.pop(),
                           (ValuedExpression)stack.pop());
                stack.push(sub);
            } break;
			  
					/**
					 * REmainders....
					 */
            case SJavaOpCode.FREM : case SJavaOpCode.DREM : case SJavaOpCode.IREM : case SJavaOpCode.LREM : 
            {
                BinaryExpression mod = new BinaryExpression();
                mod.create(BinaryExpression.MOD,
                                  (ValuedExpression)stack.pop(),
                                  (ValuedExpression)stack.pop());
                stack.push(mod);
            } break;
            case SJavaOpCode.GETFIELD : 
            {
                int index = (code[pc + 1] << 8) | code[pc + 2];
                ValuedExpression objectRef = (ValuedExpression)stack.pop();
                BinaryExpression getfield = new BinaryExpression();
                int nameIndex = cpool[index].getNameAndTypeIndex();
                CValueExpression fieldName = new CValueExpression();
                fieldName.value = cpool[nameIndex].getString();
                getfield.create(BinaryExpression.DOT_FIELD,objectRef,fieldName);
                stack.push(getfield);
            } break;
            case SJavaOpCode.GETSTATIC : 
            {
                int index = (code[pc + 1] << 8) | code[pc + 2];
                BinaryExpression getfield = new BinaryExpression();
                ConstantInfo ci = cpool[index]; 
                int nameIndex = ci.getNameAndTypeIndex();
                int classIndex = ci.getClassIndex();
                VariableType className = new ObjectType(cpool[classIndex].getString());
                CValueExpression fieldName = new CValueExpression();
                fieldName.value = cpool[nameIndex].getString();
                getfield.create(BinaryExpression.DOT_FIELD,className,fieldName);
                stack.push(getfield);
            } break;
            case SJavaOpCode.PUTFIELD : 
            {
				int index = ((code[pc + 1] & 0xff) << 8) | (code[pc + 2] & 0xff);
				int nti = cpool[index].getNameAndTypeIndex();
				String fname = cpool[cpool[nti].getClassOrNameIndex()].getString();
				
				ValuedExpression val = (ValuedExpression)stack.pop();
				ValuedExpression objRef = (ValuedExpression)stack.pop();
				
				BinaryExpression bin = new BinaryExpression();
				bin.create(BinaryExpression.DOT_FIELD,
						   objRef,
						   new CValueExpression(fname));
				
				BinaryExpression assign = new BinaryExpression();
				assign.create(BinaryExpression.ASSIGN, bin, val);
				stack.push(assign);
            } break;
            case SJavaOpCode.PUTSTATIC : 
            {
				int index = ((code[pc + 1] & 0xff) << 8) | (code[pc + 2] & 0xff);
					/// cpool[index] is a field ref info object...
				int ci = cpool[index].getClassOrNameIndex();
				int nti = cpool[index].getNameAndTypeIndex();
				String cname = cpool[cpool[ci].getClassOrNameIndex()].getString();
				String fname = cpool[cpool[nti].getClassOrNameIndex()].getString();
				
				BinaryExpression bin = new BinaryExpression();
				bin.create(BinaryExpression.DOT_FIELD,
						   new CValueExpression(cname),
						   new CValueExpression(fname));
				
				BinaryExpression assign = new BinaryExpression();
				assign.create(BinaryExpression.ASSIGN,
							  bin,
							  (ValuedExpression)stack.pop());
				stack.push(assign);
            } break;
			  
					/**
					 * Logical and, or and other bitwise operatiosn
					 * on ints and longs...
					 */
            case SJavaOpCode.IAND : case SJavaOpCode.IOR : case SJavaOpCode.IXOR : 
			case SJavaOpCode.ISHL : case SJavaOpCode.ISHR : case SJavaOpCode.LAND :
            case SJavaOpCode.LOR : case SJavaOpCode.LSHL : case SJavaOpCode.LSHR : 
            case SJavaOpCode.LXOR : 
            {
                BinaryExpression exp = new BinaryExpression();
				int op = BinaryExpression.LOG_AND;
				if (opcode == SJavaOpCode.IAND || opcode == SJavaOpCode.LAND) op = BinaryExpression.LOG_AND;
				else if (opcode == SJavaOpCode.IOR || opcode == SJavaOpCode.LOR) op = BinaryExpression.LOG_OR;
				else if (opcode == SJavaOpCode.ISHL || opcode == SJavaOpCode.LSHL) op = BinaryExpression.LSHIFT;
				else if (opcode == SJavaOpCode.ISHR || opcode == SJavaOpCode.LSHR) op = BinaryExpression.RSHIFT;
				else if (opcode == SJavaOpCode.IXOR || opcode == SJavaOpCode.LXOR) op = BinaryExpression.XOR;
                exp.create(op,
						   (ValuedExpression)stack.pop(),
						   (ValuedExpression)stack.pop());
                stack.push(exp);
            } break;
			  
					/**
					 * Floating point and double precision comparisons...
					 */
            case SJavaOpCode.FCMPG :    case SJavaOpCode.FCMPL : case SJavaOpCode.DCMPG : case SJavaOpCode.DCMPL :
            case SJavaOpCode.LCMP : 
            {
                ValuedExpression result2 = (ValuedExpression)stack.pop();
                ValuedExpression result1 = (ValuedExpression)stack.pop();
                int op = (opcode == SJavaOpCode.FCMPG || opcode == SJavaOpCode.DCMPG) ?
                         BooleanExpression.GE : BooleanExpression.LE;
                         
                BooleanExpression comp = 
                    new BooleanExpression(op,result1,result2);
            } break;
            case SJavaOpCode.INSTANCEOF : 
            {
                int index = (code[pc + 1] << 8) | code[pc + 2];
                int ci = cpool[index].getClassOrNameIndex();
                VariableType className = new ObjectType(cpool[index].getString());
                BooleanExpression instanceOf = 
                    new BooleanExpression(BooleanExpression.INSTANCE_OF,
                                            className,
                                            (ValuedExpression)stack.pop());
                stack.push(instanceOf);
            } break;
            case SJavaOpCode.IF_ACMPEQ : case SJavaOpCode.IF_ICMPEQ : case SJavaOpCode.IFEQ :        // == 
            case SJavaOpCode.IF_ACMPNE : case SJavaOpCode.IF_ICMPNE : case SJavaOpCode.IFNE :        // !=
            case SJavaOpCode.IF_ICMPGE : case SJavaOpCode.IFGE :                                // >= 
            case SJavaOpCode.IF_ICMPGT : case SJavaOpCode.IFGT :                                // >
            case SJavaOpCode.IF_ICMPLE : case SJavaOpCode.IFLE :                                // <=
            case SJavaOpCode.IFLT : case SJavaOpCode.IF_ICMPLT :                                // <
            {
                // get the previous expression from the expression list...
                // if the last instruction was a boolean one then
                // we dont do any thing... other wise if it is 
                // a binary expression then we do an == ..
                int op = 0;
                if (opcode == SJavaOpCode.IF_ACMPEQ || 
                    opcode == SJavaOpCode.IF_ICMPEQ || 
                    opcode == SJavaOpCode.IFEQ)
                {
                    op = BooleanExpression.EQ;
                } else if (opcode == SJavaOpCode.IF_ACMPNE || 
                           opcode == SJavaOpCode.IF_ICMPNE || 
                           opcode == SJavaOpCode.IFNE)
                {
                    op = BooleanExpression.NE;
                } else if (opcode == SJavaOpCode.IFGE || 
                           opcode == SJavaOpCode.IF_ICMPGE)
                {
                    op = BooleanExpression.GE;
                } else if (opcode == SJavaOpCode.IFGT || 
                           opcode == SJavaOpCode.IF_ICMPGT)
                {
                    op = BooleanExpression.GT;
                } else if (opcode == SJavaOpCode.IFLE || 
                           opcode == SJavaOpCode.IF_ICMPLE)
                {
                    op = BooleanExpression.LE;
                } else if (opcode == SJavaOpCode.IFLT || 
                           opcode == SJavaOpCode.IF_ICMPLT)
                {
                    op = BooleanExpression.LT;
                }
                
                    // now we have decided what the operator shoud be...
                    // so get the topmost expression...
                ValuedExpression top = (ValuedExpression)stack.peek();
                if (top instanceof BooleanExpression)
                {
                        // now check if this boolean expression has 
                        // the same sign as op.
                        // if it does than we do nothing 
                        // otherwise we change the sign to
                        // op and still do nothing...
                    ((BooleanExpression)top).opType = op;
                } else
                {
                        // if it is not a boolean expression
                        // then we pop another expression of the
                        // stack and do expr1 op expr2 as the
                        // new boolean expression and put that
                        // on ths stack...
                    Object prev = stack.pop();
                    BooleanExpression boolExp = 
                        new BooleanExpression(op,prev,top);
                    stack.push(boolExp);
                }
            } break;
            case SJavaOpCode.IFNONNULL :        // != null
            case SJavaOpCode.IFNULL :        // == null 
            {
                ValuedExpression e1 = (ValuedExpression)stack.pop();

                BooleanExpression boolExp = 
                   new BooleanExpression(
                        (opcode == SJavaOpCode.IFNONNULL ? 
                                BooleanExpression.NE : BooleanExpression.EQ),
                             e1,new CValueExpression());

                stack.push(boolExp);
            } break;
			  
			  
				/**
				 * Following 4 instructions deal with 
				 * invoking various kinds of methods...
				 */
            case SJavaOpCode.INVOKEINTERFACE : case SJavaOpCode.INVOKEVIRTUAL :
            case SJavaOpCode.INVOKESPECIAL :	case SJavaOpCode.INVOKESTATIC :
            {
						// the index into the cpool table...
				int index = ((code[pc + 1] & 0xff) << 8) | (code[pc + 2] & 0xff);
				
						// now get the name and type descriptor from
						// the cpool table...
				int ntIndex = cpool[index].getNameAndTypeIndex();
				String name = cpool[cpool[ntIndex].getClassOrNameIndex()].getString();
				String descr = cpool[cpool[ntIndex].getDescriptorIndex()].getString();
				
					// now from the descriptor index, get the number of
					// arguments required and pop this many expressions
					// of the stack.  This becomes the params[] array
					// for the fcall expression.
				MethodType mType = (MethodType)TypeParser.evaluateType(descr.toCharArray(),0,descr.length() - 1);
				int nargs = mType.getNumParameters();
				ValuedExpression params[] = new ValuedExpression[nargs];
				for (int i = nargs - 1;i >= 0;i --)
				{
					params[i] = (ValuedExpression)stack.pop();
					
					// TODO:: Here is where we check the type of each parameter.
					// If this parameter is an identifier expression, then
					// we associate the type of the ith parameter to this identifier
					// and if it happens to be a local vriable... then that is fine tooo..
					VariableType currType = mType.getArgumentType(i);
				}
				ValuedExpression target = null;
				
				if (opcode == SJavaOpCode.INVOKESTATIC)
				{
						// other wise create a cvalue exp with
						// the class name as the target...
					target = new CValueExpression();
					int cind = cpool[cpool[index].getClassIndex()].getClassOrNameIndex();
					((CValueExpression)target).value = cpool[cind].getString();
					
				} else
				{
					target = (ValuedExpression)stack.pop();
				}
				FunctionExpression fe = new FunctionExpression();
				boolean isCons = (opcode == SJavaOpCode.INVOKEINTERFACE || 
								  opcode == SJavaOpCode.INVOKESTATIC ||
								  opcode == SJavaOpCode.INVOKEVIRTUAL);
				fe.create(isCons,target,params,mType);
				
				stack.push(fe);
            } break;
            case SJavaOpCode.MULTIANEWARRAY : 
            {
				int index = ((code[pc + 1] & 0xff) << 8) | (code[pc + 2] & 0xff);
				int nd = code[pc + 3] & 0xff;
				ValuedExpression params[] = new ValuedExpression[nd];
				for (int i = nd - 1;i >= 0;i--)
				{
					params[i] = (ValuedExpression)stack.pop();
					//TODO:: Once again we need to check types of all local vars...
				}
				String name = cpool[cpool[index].getClassOrNameIndex()].getString();
				VariableType type = new ObjectType(name);
				ArrayCreaterExpression newArray = new ArrayCreaterExpression();
				newArray.create(type,params);
				stack.push(newArray);
            } break ;
			  
					/**
					 * The following instructions perform some normal 
					 * operations on the stack.
					 */
            case SJavaOpCode.SWAP : 
            {
				Object val1 = stack.pop();
				Object val2 = stack.pop();
				stack.push(val1);
				stack.push(val2);
            } break;
            case SJavaOpCode.POP : 
            {
				System.out.println("Found pop at PC = " + pc + ".  Not sure what to do.");
				stack.pop();
            } break;
            case SJavaOpCode.POP2 : 
            {
				stack.pop();
				//TODO:: First we have to check if the second item on
				// the stack is a long or a double... 
				stack.pop();
            } break;
			 
				/**
				 * The following DUP_??? instructions make duplicates of 
				 * values already on the stack...  By duplicating
				 * we are not cloning objects.  We are basically pushing
				 * the same object twice on the stack.  So this way
				 * both the expression (original and duplicated) will 
				 * have the same expression tree.  As a result, any change on
				 * one will reflect on the other...
				 */
            case SJavaOpCode.DUP : 
            {
				stack.push(stack.peek());
            } break;
            case SJavaOpCode.DUP2 : 
            {		// duplicate top two values...
				Object val1 = stack.pop();
				Object val2 = stack.pop();
				
				stack.push(val2); stack.push(val1);
				stack.push(val2); stack.push(val1);
            } break;
            case SJavaOpCode.DUP2_X1 : 
            {
				Object val1 = stack.pop();
				Object val2 = stack.pop();
				Object val3 = stack.pop();
				stack.push(val2); stack.push(val1);
				stack.push(val3); stack.push(val2); stack.push(val1);
            } break;
            case SJavaOpCode.DUP2_X2 : 
            {
				Object vals[] = new Object[4];
				for (int i = 1;i < vals.length;i++) vals[i] = stack.pop();
				stack.push(vals[1]);		// value 2
				stack.push(vals[0]);		// value 1
				for (int i = 3;i >= 0;i --) stack.push(vals[i]);
            } break;
            case SJavaOpCode.DUP_X1 : 
            {
				Object val1 = stack.pop();
				Object val2 = stack.pop();
				stack.push(val1); stack.push(val2); stack.push(val1);
            } break;
            case SJavaOpCode.DUP_X2 : 
            {
				Object val1 = stack.pop();
				Object val2 = stack.pop();
				Object val3 = stack.pop();
				stack.push(val1); stack.push(val3); stack.push(val2); stack.push(val1);
            } break;
            case SJavaOpCode.IUSHR : case SJavaOpCode.LUSHR : 
            {
            } break;
			  
				/**
				 * We skip the jsr and jsr_w instructions
				 * just like we skipped the goto ones...
				 */
			case SJavaOpCode.JSR : case SJavaOpCode.JSR_W :
            {
            } break;
			  
				/**
				 * The following monitor instructions
				 * need to be dealt carefully... these
				 * deal with synchronization and soo on...
				 * Theese are a bit like exception handler.. these
				 * need to be checked out before we build blocks...
				 */
            case SJavaOpCode.MONITORENTER : 
            {
            } break;
            case SJavaOpCode.MONITOREXIT : 
            {
            } break;
			  
				/**
				 * These also will be dealt separately...
				 */
            case SJavaOpCode.LOOKUPSWITCH : case SJavaOpCode.TABLESWITCH : 
            {
            } break;
            case SJavaOpCode.WIDE : 
            {
				wide = true;
            } break;
            case SJavaOpCode.GOTO : case SJavaOpCode.GOTO_W : 
            {
            } break;
					/**
					 * The following are unused instructions..
					 * so we dont worry about them...
					 */
            case SJavaOpCode.XXXUNUSEDXXX1 : 
            {
            } break;
            case SJavaOpCode.IMPDEP1 :        // unused
            {
            } break;
            case SJavaOpCode.IMPDEP2 :        // unused
            {
            } break;
            case SJavaOpCode.BREAKPOINT :        // one of the reserved opcodes
            {
            } break;
            default : break;
        }
    }
}
