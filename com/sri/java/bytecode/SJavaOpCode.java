package com.sri.java.bytecode;

import java.util.*;


/**
 * Class holding information about various opcodes.
 */
public class SJavaOpCode
{
        /**
         * NOP_INSTRUCTION are NOP instructions.
         *
         * NOP_INSTRUCTION includes
         * <br>
         * nop instruction only.
         */
     public static final short NOP_INSTRUCTION = 0;
  
        /**
         * CONSTANT_INSTRUCTION are instructions that deal with constants,
         * or with constant pool table.
         *
         * CONSTANT_INSTRUCTION includes
         * <br>
         * aconst_null, iconst_m1, iconst_0, iconst_1, iconst_2,
         * iconst_3, iconst_4, iconst_5, lconst_0, lconst_1, 
         * fconst_0, fconst_1, fconst_2, dconst_0, dconst_1,
         * bipush, sipush, ldc, ldc_w, ldc2_w instructions.
         */
    public static final short CONSTANT_INSTRUCTION = 1;
  
        /**
         * LOAD_INSTRUCTION are load type instructions.
         * 
         * LOAD_INSTRUCTION includes
         * <br>
         * iload, lload, fload, dload, aload, iload_0, iload_1, iload_2, iload_3,
         * lload_0, lload_1, lload_2, lload_3, fload_0, fload_1, fload_2, fload_3,
         * dload_0, dload_1, dload_2, dload_3, aload_0, aload_1, aload_2, aload_3,
         * iaload, laload, faload, daload, aaload, baload, caload, saload instructions.
         */
     public static final short LOAD_INSTRUCTION = 2;
  
        /**
         * STORE_INSTRUCTION are store type instructions.
         * 
         * STORE_INSTRUCTION includes
         * <br>
         * istore, lstore, fstore, dstore, astore, istore_0, istore_1, istore_2, istore_3,
         * lstore_0, lstore_1, lstore_2, lstore_3, fstore_0, fstore_1, fstore_2, fstore_3,
         * dstore_0, dstore_1, dstore_2, dstore_3, astore_0, astore_1, astore_2, astore_3,
         * iastore, lastore, fastore, dastore, aastore, bastore, castore, sastore instructions.
         */
    public static final short STORE_INSTRUCTION = 3;
  
        /**
         * STACK_INSTRUCTION are instrctions that manipulate the stack.
         *
         * STACK_INSTRUCTION includes
         * <br>
         * pop, pop2, dup, dup_x1, dup_x2, dup2, dup2_x1, dup2_x2, swap instructions.
         */
     public static final short STACK_INSTRUCTION = 4;
  
        /**
         * ARITHMETIC_INSTRUCTION are arithmetic instructions.
         * 
         * ARITHMETIC_INSTRUCTION includes
         * <br>
         * iadd, ladd, fadd, dadd, isub, lsub, fsub, dsub, imul, lmul, fmul, dmul,
         * idiv, ldiv, fdiv, ddiv, irem, irem, frem, drem, ineg, lneg, fneg, dneg,
         * ishl, lshl, ishr, lshr, iushr, lushr, iinc instructions.
         */
     public static final short ARITHMETIC_INSTRUCTION = 5;
  
        /**
         * LOGICAL_INSTRUCTION are logical instructions.
         * 
         * LOGICAL_INSTRUCTION includes
         * <br>
         * iand, iand, ior, lor, ixor, lxor instructions.
         */
     public static final short LOGICAL_INSTRUCTION = 6;
     
        /** CONVERSION_INSTRUCTION instructions convert types.
         *
         * CONVERSION_INSTRUCTION includes
         * <br>
         * i2l, i2f, i2d, l2i, l2f, l2d, f2i, f2l, f2d, d2i, d2l, d2f,
         * i2b, i2c, i2s instructions.
         */
     public static final short CONVERSION_INSTRUCTION = 7;
     
        /** 
         * COMPARISON_INSTRUCTION instructions do comparison.
         *
         * COMPARISON_INSTRUCTION includes
         * <br>
         * lcmp, fcmpl, fcmpg, dcmpl, dcmpg instructions.
         */
     public static final short COMPARISON_INSTRUCTION = 8;
     
        /**
         * CONDITIONAL_INSTRUCTION instructions branch based on a condition.
         *
         * CONDITIONAL_INSTRUCTION includes
         * <br>
         * ifeq, ifne, iflt, ifge, ifgt, ifle, if_icmpeq, if_icmpne,
         * if_icmplt, if_icmpge, if_icmpgt, if_icmple, if_acmpeq, if_acmpne,
         * ifnull, ifnonnull instructions.
         */
     public static final short CONDITIONAL_INSTRUCTION = 9;
     
        /** 
         * UNCONDITIONAL_INSTRUCTION instructions branch unconditionally.
         *
         * UNCONDITIONAL_INSTRUCTION includes
         * <br>
         * goto, jsr, ret, tableswitch, lookupswitch, ireturn, lreturn,
         * freturn, dreturn, areturn, return,
         * invokevirtual, invokenonvirtual, invokestatic, 
         * invokeinterface, goto_w, jsr_w, ret_w, breakpoint instructions.
         */
     public static final short UNCONDITIONAL_INSTRUCTION = 10;
     
        /**
         * CLASS_INSTRUCTION instructions deal with class components.
         *
         * CLASS_INSTRUCTION includes
         * <br>
         * getstatic, putstatic instructions.
         */
     public static final short CLASS_INSTRUCTION = 11;
     
        /**
         * OBJECT_INSTRUCTION instructions deal with object components.
         *
         * OBJECT_INSTRUCTION includes
         * <br>
         * getfield, putfield, new, newarray, anewarray, arraylength instructions.
         */
     public static final short OBJECT_INSTRUCTION = 12;
     
        /**
         * EXCEPTION_INSTRUCTION instructions deal with exceptions.
         *
         * EXCEPTION_INSTRUCTION includes
         * <br>
         * none.
         */
     public static final short EXCEPTION_INSTRUCTION = 13;
     
        /**
         * INSTRUCTIONCHECK_INSTRUCTION instructions deal with types.
         *
         * INSTRUCTIONCHECK_INSTRUCTION includes
         * <br>
         * checkcast, instanceof instructions.
         */
     public static final short INSTRUCTIONCHECK_INSTRUCTION = 14;
     
        /**
         * MONITOR_INSTRUCTION instructions deal with monitors.
         * 
         * MONITOR_INSTRUCTION includes
         * <br>
         * monitorenter, monitorexit instructions.
         */
     public static final short MONITOR_INSTRUCTION = 15;
     
        /**
         * OTHER_INSTRUCTION instructions
         *
         * OTHER_INSTRUCTION include
         * <br>
         * wide, impdep1, impdep2, and all other invalid opcodes.
         */
     public static final short OTHER_INSTRCTION = -1;


  
        /** 
         * String representation of different types of instructions.
         */
    public static final String instructionTypeName[] = {
        "NOP_INSTRUCTION",
        "CONSTANT_INSTRUCTION",
        "LOAD_INSTRUCTION",
        "STORE_INSTRUCTION",
        "STACK_INSTRUCTION",
        "ARITHMETIC_INSTRUCTION",
        "LOGICAL_INSTRUCTION",
        "CONVERSION_INSTRUCTION",
        "COMPARISON_INSTRUCTION",
        "CONDITIONAL_INSTRUCTION",
        "UNCONDITIONAL_INSTRUCTION",
        "CLASS_INSTRUCTION",
        "OBJECT_INSTRUCTION",
        "EXCEPTION_INSTRUCTION",
        "INSTRUCTIONCHECK_INSTRUCTION",
        "MONITOR_INSTRUCTION"
    };

        /**
         * The following instructions need thought:
         * lookupswitch(171)
         * tableswitch (170)
         * wide (196)
         */
        /**
         * The number of parameters required for each op code.
         */
    public final static byte numParameters[] = 
    {
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,		                // 0 - 9
         0, 0, 0, 0, 0, 0, 1, 2, 1, 2,			            // 10 - 19
         2, 1, 1, 1, 1, 1, 0, 0, 0, 0,			            // 20 - 29
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 30 - 39
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 40 - 49
         0, 0, 0, 0, 1, 1, 1, 1, 1, 0,			            // 50 - 59
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 60 - 69
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 70 - 79
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 80 - 89
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 90 - 99
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 100 - 109
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 110 - 119
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 120 - 129
         0, 0, 2, 0, 0, 0, 0, 0, 0, 0,			            // 130 - 139
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0,			            // 140 - 149
         0, 0, 0, 2, 2, 2, 2, 2, 2, 2,			            // 150 - 159
         2, 2, 2, 2, 2, 2, 2, 2, 2, 1,			            // 160 - 169
         -1, -1, 0, 0, 0, 0, 0, 0, 2, 2,			        // 170 - 179
         2, 2, 2, 2, 2, 4, -2, 2, 1, 2,			            // 180 - 189
         0, 0, 2, 2, 0, 0, 9, 3, 2, 2,			            // 190 - 199
         4, 4, 0, -2, -2, -2, -2, -2, -2, 2,		        // 200 - 209
         -2, -2, -2, -2, -2, -2, -2, -2, -2, -2,            // 210 - 219
         -2, -2, -2, -2, -2, -2, -2, -2, -2, -2,   	        // 220 - 229
         -2, -2, -2, -2, -2, -2, -2, -2, -2, -2,   	        // 230 - 239
         -2, -2, -2, -2, -2, -2, -2, -2, -2, -2,	        // 240 - 249
         -2, -2, -2, -2, 0, 0
    };
    
       /** 
        * InstructionTypeTable is an array indicating which type an instruction is.
        * One can modify this table if one wants to classify the instructions
        * differently than I have.
        */
    public static final short instructionType[] = {
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1,		    // 0 - 9
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,			// 10 - 19
        1, 2, 2, 2, 2, 2, 2, 2, 2, 2,			// 20 - 29
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,			// 30 - 39
        2, 2, 2, 2, 2, 2, 2, 2,	2, 2,			// 40 - 49
        2, 2, 2, 2, 3, 3, 3, 3, 3, 3,			// 50 - 59
        3, 3, 3, 3,	3, 3, 3, 3, 3, 3,			// 60 - 69
        3, 3, 3, 3, 3, 3, 3, 3, 3, 3,			// 70 - 79
        3, 3, 3, 3, 3, 3, 3, 4, 4, 4,			// 80 - 89
        4, 4, 4, 4, 4, 4, 5, 5, 5, 5,			// 90 - 99
        5, 5, 5, 5, 5, 5, 5, 5, 5, 5,			// 100 - 109
        5, 5, 5, 5, 5, 5, 5, 5, 5, 5,			// 110 - 119
        5, 5, 5, 5, 5, 5, 6, 6,	6, 6,			// 120 - 129
        6, 6, 5, 7, 7, 7, 7, 7, 7, 7,			// 130 - 139
        7, 7, 7, 7,	7, 7, 7, 7, 8, 8,			// 140 - 149
        8, 8, 8, 9, 9, 9, 9, 9, 9, 9,			// 150 - 159
        9, 9, 9, 9, 9, 9, 9, 10, 10, 10,		// 160 - 169
        10, 10, 10, 10, 10, 10, 10, 10, 11, 11,	// 170 - 179
        12, 12, 10, 10, 10, 10, -1, 12, 12, 12,	// 180 - 189
        12, 10, 14, 14, 15, 15, -1, 12, 9, 9,	// 190 - 199
        10, 10, 10, -1, -1, -1, -1, -1, -1, 10  // 200 - 209
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 210 - 219
        -1, -1, -1, -1,	-1, -1, -1, -1, -1, -1,	// 220 - 229
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 230 - 239
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 240 - 249
        -1, -1, -1, -1, -1, -1
    };

    public final static short AALOAD = 50;
    public final static short AASTORE = 83;
    public final static short ACONST_NULL = 1;
    public final static short ALOAD = 25;
    public final static short ALOAD_0 = 42;
    public final static short ALOAD_1 = 43;
    public final static short ALOAD_2 = 44;
    public final static short ALOAD_3 = 45;
    public final static short ANEWARRAY = 189;
    public final static short ARETURN = 176;
    public final static short ARRAYLENGTH = 190;
    public final static short ASTORE = 58;
    public final static short ASTORE_0 = 75;
    public final static short ASTORE_1 = 76;
    public final static short ASTORE_2 = 77;
    public final static short ASTORE_3 = 78;
    public final static short ATHROW = 191;
    public final static short BALOAD = 51;
    public final static short BASTORE = 84;
    public final static short BIPUSH = 16;
    public final static short BREAKPOINT = 202;
    public final static short CALOAD = 52;
    public final static short CASTORE = 85;
    public final static short CHECKCAST = 192;
    public final static short D2F = 144;
    public final static short D2I = 142;
    public final static short D2L = 143;
    public final static short DADD = 99;
    public final static short DALOAD = 49;
    public final static short DASTORE = 82;
    public final static short DCMPG = 152;
    public final static short DCMPL = 151;
    public final static short DCONST_0 = 14;
    public final static short DCONST_1 = 15;
    public final static short DDIV = 111;
    public final static short DLOAD = 24;
    public final static short DLOAD_0 = 38;
    public final static short DLOAD_1 = 39;
    public final static short DLOAD_2 = 40;
    public final static short DLOAD_3 = 41;
    public final static short DMUL = 107;
    public final static short DNEG = 119;
    public final static short DREM = 115;
    public final static short DRETURN = 175;
    public final static short DSTORE = 57;
    public final static short DSTORE_0 = 71;
    public final static short DSTORE_1 = 72;
    public final static short DSTORE_2 = 73;
    public final static short DSTORE_3 = 74;
    public final static short DSUB = 103;
    public final static short DUP = 89;
    public final static short DUP2 = 92;
    public final static short DUP2_X1 = 93;
    public final static short DUP2_X2 = 94;
    public final static short DUP_X1 = 90;
    public final static short DUP_X2 = 91;
    public final static short F2D = 141;
    public final static short F2I = 139;
    public final static short F2L = 140;
    public final static short FADD = 98;
    public final static short FALOAD = 48;
    public final static short FASTORE = 81;
    public final static short FCMPG = 150;
    public final static short FCMPL = 149;
    public final static short FCONST_0 = 11;
    public final static short FCONST_1 = 12;
    public final static short FCONST_2 = 13;
    public final static short FDIV = 110;
    public final static short FLOAD = 23;
    public final static short FLOAD_0 = 34;
    public final static short FLOAD_1 = 35;
    public final static short FLOAD_2 = 36;
    public final static short FLOAD_3 = 37;
    public final static short FMUL = 106;
    public final static short FNEG = 118;
    public final static short FREM = 114;
    public final static short FRETURN = 174;
    public final static short FSTORE = 56;
    public final static short FSTORE_0 = 67;
    public final static short FSTORE_1 = 68;
    public final static short FSTORE_2 = 69;
    public final static short FSTORE_3 = 70;
    public final static short FSUB = 102;
    public final static short GETFIELD = 180;
    public final static short GETSTATIC = 178;
    public final static short GOTO = 167;
    public final static short GOTO_W = 200;
    public final static short I2B = 145;
    public final static short I2C = 146;
    public final static short I2D = 135;
    public final static short I2F = 134;
    public final static short I2L = 133;
    public final static short I2S = 147;
    public final static short IADD = 96;
    public final static short IALOAD = 46;
    public final static short IAND = 126;
    public final static short IASTORE = 79;
    public final static short ICONST_0 = 3;
    public final static short ICONST_1 = 4;
    public final static short ICONST_2 = 5;
    public final static short ICONST_3 = 6;
    public final static short ICONST_4 = 7;
    public final static short ICONST_5 = 8;
    public final static short ICONST_M1 = 2;
    public final static short IDIV = 108;
    public final static short IFEQ = 153;
    public final static short IFGE = 156;
    public final static short IFGT = 157;
    public final static short IFLE = 158;
    public final static short IFLT = 155;
    public final static short IFNE = 154;
    public final static short IFNONNULL = 199;
    public final static short IFNULL = 198;
    public final static short IF_ACMPEQ = 165;
    public final static short IF_ACMPNE = 166;
    public final static short IF_ICMPEQ = 159;
    public final static short IF_ICMPGE = 162;
    public final static short IF_ICMPGT = 163;
    public final static short IF_ICMPLE = 164;
    public final static short IF_ICMPLT = 161;
    public final static short IF_ICMPNE = 160;
    public final static short IINC = 132;
    public final static short ILOAD = 21;
    public final static short ILOAD_0 = 26;
    public final static short ILOAD_1 = 27;
    public final static short ILOAD_2 = 28;
    public final static short ILOAD_3 = 29;
    public final static short IMPDEP1 = 203;
    public final static short IMPDEP2 = 204;
    public final static short IMUL = 104;
    public final static short INEG = 116;
    public final static short INSTANCEOF = 193;
    public final static short INVOKEINTERFACE = 185;
    public final static short INVOKESPECIAL = 183;
    public final static short INVOKESTATIC = 184;
    public final static short INVOKEVIRTUAL = 182;
    public final static short IOR = 128;
    public final static short IREM = 112;
    public final static short IRETURN = 172;
    public final static short ISHL = 120;
    public final static short ISHR = 122;
    public final static short ISTORE = 54;
    public final static short ISTORE_0 = 59;
    public final static short ISTORE_1 = 60;
    public final static short ISTORE_2 = 61;
    public final static short ISTORE_3 = 62;
    public final static short ISUB = 100;
    public final static short IUSHR = 124;
    public final static short IXOR = 130;
    public final static short JSR = 168;
    public final static short JSR_W = 201;
    public final static short L2D = 138;
    public final static short L2F = 137;
    public final static short L2I = 136;
    public final static short LADD = 97;
    public final static short LALOAD = 47;
    public final static short LAND = 127;
    public final static short LASTORE = 80;
    public final static short LCMP = 148;
    public final static short LCONST_0 = 9;
    public final static short LCONST_1 = 10;
    public final static short LDC = 18;
    public final static short LDC2_W = 20;
    public final static short LDC_W = 19;
    public final static short LDIV = 109;
    public final static short LLOAD = 22;
    public final static short LLOAD_0 = 30;
    public final static short LLOAD_1 = 31;
    public final static short LLOAD_2 = 32;
    public final static short LLOAD_3 = 33;
    public final static short LMUL = 105;
    public final static short LNEG = 117;
    public final static short LOOKUPSWITCH = 171;
    public final static short LOR = 129;
    public final static short LREM = 113;
    public final static short LRETURN = 173;
    public final static short LSHL = 121;
    public final static short LSHR = 123;
    public final static short LSTORE = 55;
    public final static short LSTORE_0 = 63;
    public final static short LSTORE_1 = 64;
    public final static short LSTORE_2 = 65;
    public final static short LSTORE_3 = 66;
    public final static short LSUB = 101;
    public final static short LUSHR = 125;
    public final static short LXOR = 131;
    public final static short MONITORENTER = 194;
    public final static short MONITOREXIT = 195;
    public final static short MULTIANEWARRAY = 197;
    public final static short NEW = 187;
    public final static short NEWARRAY = 188;
    public final static short NOP = 0;
    public final static short POP = 87;
    public final static short POP2 = 88;
    public final static short PUTFIELD = 181;
    public final static short PUTSTATIC = 179;
    public final static short RET = 169;
    public final static short RETURN = 177;
    public final static short SALOAD = 53;
    public final static short SASTORE = 86;
    public final static short SIPUSH = 17;
    public final static short SWAP = 95;
    public final static short TABLESWITCH = 170;
    public final static short WIDE = 196;
    public final static short XXXUNUSEDXXX1 = 186;

        /**
         * A list of all the opcodes...
         */
    public final static String opcodeNames[] = {
        "nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2",
        "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0",
        "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc",
        "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0",
        "iload_1", "iload_2", "iload_3", "lload_0", "lload_1", "lload_2",
        "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0",
        "dload_1", "dload_2", "dload_3", "aload_0", "aload_1", "aload_2",
        "aload_3", "iaload", "laload", "faload", "daload", "aaload", "baload",
        "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore",
        "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1",
        "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3",
        "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1",
        "astore_2", "astore_3", "iastore", "lastore", "fastore", "dastore",
        "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup", "dup_x1",
        "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd",
        "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul", "dmul",
        "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg",
        "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr",
        "iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f", "i2d",
        "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b",
        "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl", "dcmpg", "ifeq", "ifne",
        "iflt", "ifge", "ifgt", "ifle", "if_icmpeq", "if_icmpne", "if_icmplt",
        "if_icmpge", "if_icmpgt", "if_icmple", "if_acmpeq", "if_acmpne", "goto",
        "jsr", "ret", "tableswitch", "lookupswitch", "ireturn", "lreturn",
        "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic",
        "getfield", "putfield", "invokevirtual", "invokespecial", "invokestatic",
        "invokeinterface", "xxxunusedxxx1", "new", "newarray", "anewarray",
        "arraylength", "athrow", "checkcast", "instanceof", "monitorenter",
        "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull", "goto_w",
        "jsr_w", "breakpoint", "203", "204", "205", "206", "207", "208", "209", 
        "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", 
        "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", 
        "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", 
        "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", 
        "250", "251", "252", "253", "impdep1", "impdep2", 
    };
}
