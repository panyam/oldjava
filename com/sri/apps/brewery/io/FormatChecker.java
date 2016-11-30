
package com.sri.apps.brewery.io;

import java.io.*;

/**
 * The class that would check a format of an input file
 * in order to determine which SetupReader class to 
 * instantiate.
 *
 * This is a kind of a Factory object (a SetupReader factory).
 */
public class FormatChecker
{
        /**
         * Take a look at the input stream and determine
         * the type of the SetupReader to get.
         */
    public static SetupReader getSetupReader(InputStream iStream)
    {
        return null;
    }

        /**
         * Determines the SetupReader object to use given 
         * the name of the file.
         *
         * It is possible that this function MAY call the
         * getSetupReader(InputStream ) function to determine
         * the Reader type but this is not compulsory.
         */
    public static SetupReader getSetupReader(String fileName)
    {
        return null;
    }
}
