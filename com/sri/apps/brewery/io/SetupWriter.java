

package com.sri.apps.brewery.io;

import javax.swing.*;
import java.util.*;
import java.io.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;

/**
 * Super class of all classes that can serialise the setup information
 * to the output stream.
 */
public interface SetupWriter
{
        /**
         * Write the setup information to the output stream.
         */
    public void write(OutputStream oStream, Blend gData) throws Exception;
}
