
package com.sri.java.bytecode;

import java.util.*;
import java.io.*;
import com.sri.java.*;
import com.sri.java.attribute.*;

/**
 *  Given an expression and a method, generates the bytecode.
 *  Must be called ONLY for a method.
 *
 *  The constant pool table is also updated as we go.
 *
 *  Basically what we have is a Depth first traversal of the expression
 *  tree.
 *
 *  Here is the code generationa lgorithm
 *  Maintain a stack of offsets, which need to have the "jump" offsets
 */
public class ByteCodeGenerator
{
        // the curr offset
    int currOffset;
}
