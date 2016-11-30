package com.sri.apps.mml;

import java.io.*;

/**
 * This class writes a given root symbol to 
 * an output.
 */
public interface DocumentWriter
{
		/**
		 * writes a given symbol to the 
		 * specified output stream.
		 */
	public void writeDocument(OutputStream out, Object doc) 
							throws IOException;
}
