/*
 * EncryptedOutputStream.java
 *
 * Created on 30 July 2004, 16:08
 */

package com.sri.utils.crypt;

import java.io.*;

/**
 * Output stream where the bytes written are encrypted before
 * actually written to the output stream.
 *
 * @author  Sri Panyam
 */
public abstract class EncryptedOutputStream extends OutputStream
{
        /**
         * The outputstream where bytes are encrypted
         * before written into.
         */
    protected OutputStream oStream;
    
    /** Creates a new instance of EncryptedInputStream */
    public EncryptedOutputStream (OutputStream oStream)
    {
        this.oStream = oStream;
    }
}
