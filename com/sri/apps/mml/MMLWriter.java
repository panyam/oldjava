package com.sri.apps.mml;

import java.io.*;

/**
 * This class takes a symbol and writes it to the output
 * stream.
 */
public class MMLWriter implements DocumentWriter
{
		/**
		 * The data output stream.
		 */
	protected DataOutputStream dout;
	
		/**
		 * Writes the document to the output stream.
		 */
	public void writeDocument(OutputStream out, 
							  Object doc)
						throws IOException
	{
		if (doc instanceof MMLSymbol)
		{
			writeSymbol(out, (MMLSymbol)doc);
		} else
		{
			throw new IllegalArgumentException("Invalid document type.");
		}
	}
	
		/**
		 * Writes the document to the output stream.
		 */
	public void writeSymbol(OutputStream out, 
							MMLSymbol sym) throws IOException
	{
		if (out instanceof DataOutputStream)
		{
			dout = (DataOutputStream)out;
		} else
		{
			dout = new DataOutputStream(out);
		}
		//if (sym instanceof MMLParagraphSymbol
		this.dout.close();
	}
}
