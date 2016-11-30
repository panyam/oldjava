/*
 * DecryptedInputStream.java
 *
 * Created on 30 July 2004, 16:09
 */

package com.sri.utils.crypt;


import java.io.*;


/**
 * Inputstream where the input is decrypted for a given input of symbols.
 * @author  Sri Panyam
 */
public abstract class DecryptedInputStream extends InputStream
{
        /**
         * The input stream that is beign decrypted.
         */
    protected InputStream iStream;

    /** Creates a new instance of DecryptedInputStream */
    public DecryptedInputStream (InputStream iStream)
    {
        this.iStream = iStream;
    }
}
