package com.sri.apps.mml;

import java.io.*;

/**
 * This class is responsible for reading a document 
 * and generating the appropriate symbols from 
 * it.
 */
public interface DocumentReader
{
		/**
		 * Reads an input stream and returns the
		 * document.
		 */
	public MMLSymbol readDocument(InputStream in) throws IOException;
}
