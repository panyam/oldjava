package com.sri.apps.brewery.io;

import javax.swing.*;
import java.util.*;
import java.io.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * Super class of all classes that can read the setup files
 * and pass information to the main grinder object.
 */
public interface SetupReader
{
        /**
         * Read the input stream for the setup information.
         */
    public void read(InputStream iStream, Blend gData) throws Exception;
}
