package com.sri.apps.mml;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A class used for reading a MathML document.
 * This class parses everything with <math> and </math>
 * tags and skips everything else.
 */
public class MMLReader implements DocumentReader
{
		/**
		 * The input stream that is being read.
		 */
	protected DataInputStream din;
	
		/**
		 * Get a token from the input stream.
		 */
	protected int getToken(DataInputStream din)
	{
		return 0;
	}
	
        /**
         * Given a data input stream, reads the MathML
         * out of it.
         * 
         * Everything INSIDE the <math> </math> tags is 
         * MML.  
         * Everything OUTSIDE these tags are rejected.
         *
         * Good thing is MML is a clean grammar so we can get away by using
         * a simple LL parser.  So our design is greatly simplified.
         */
    public MMLSymbol readDocument(InputStream in) throws IOException
    {
		if (in instanceof DataInputStream)
		{
			this.din = (DataInputStream)in;
		} else
		{
			din = new DataInputStream(in);
		}
        ignoreUntilStart(din);
        return null;
    }

    protected void ignoreUntilStart(DataInputStream din) throws IOException
    {
    }
}
