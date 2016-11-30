package com.sri.apps.brewery.io;

import javax.swing.*;
import java.util.*;
import java.io.*;
//import java.awt.*;
import com.sri.utils.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * Just a few utilities to handle IO operations.
 *
 * @author Sri Panyam
 */
public class GrinderIOUtils
{
        /**
         * Write a string as a len, bytes pair.
         */
    public static void writeString(DataOutputStream dout, String str)
        throws IOException
    {
        byte out[] = str.getBytes();
        int len = out.length;
        dout.writeInt(len);
        dout.write(out, 0, len);
    }

        /**
         * Read a string which is stored as a (length, string) pair.
         */
    public static String readString(DataInputStream din)
        throws IOException
    {
        int len = din.readInt();
        byte bytes[] = new byte[len];
        din.readFully(bytes, 0, len);
        return new String(bytes);
    }
}
