package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

public class TTFVMTXTable extends TTFTable
{
        /**
         * Constructor.
         */
    public TTFVMTXTable(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		return 0;
    }
}
