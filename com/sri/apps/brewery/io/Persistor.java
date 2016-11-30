package com.sri.apps.brewery.io;

import java.util.*;
import java.io.*;

/**
 * The interface to handle persistance of screens to and from streams.
 *
 * @author Sri Panyam
 */
public interface Persistor
{
        /**
         * Write a persistable object from stream.
         */
    public void writeToStream(Persistable obj,
                              OutputStream oStream) throws Exception;

        /**
         * Read a persistable object from stream.
         */
    public Persistable readFromStream(InputStream iStream) throws Exception;
}
