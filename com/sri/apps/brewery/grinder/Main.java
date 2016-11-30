
package com.sri.apps.brewery.grinder;

import java.util.*;
import java.io.*;
//import java.awt.*;
import com.sri.utils.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.io.*;
import com.sri.apps.brewery.grinder.screens.*;
import com.sri.apps.brewery.grinder.containers.*;

/**
 * Class that does the loading of the files and so on and calls
 * a Grinder object to perform the installation.
 *
 * @author Sri Panyam
 */
public class Main
{
        /**
         * Describes each argument, its flag, name and default value.
         */
    protected static HashMap arguments = new HashMap();

    protected final static int ARG_NAME_INDEX = 0;
    protected final static int ARG_DESC_INDEX = 1;
    protected final static int ARG_DEFAULT_VAL_INDEX = 2;
    protected final static int ARG_VALUE_INDEX = 3;

        // Default Files.
    protected static String DEFAULT_SETUP_FILE = "setup.info";
    protected static String DEFAULT_PACKAGE_FILE = "package.jar";
    protected static String DEFAULT_PERSISTOR_CLASS = 
                    "com.sri.apps.brewery.io.DefaultBinPersistor";
    protected static String DEFAULT_CONTAINER_CLASS =
        "com.sri.apps.brewery.grinder.containers.SingleFrameContainer";

        // Actual files and classes used during isntallation.
    protected static String setupFile = DEFAULT_SETUP_FILE;
    protected static String packageArchive = DEFAULT_PACKAGE_FILE;
    protected static String persistorClass = DEFAULT_PERSISTOR_CLASS;
    protected static String containerClass = DEFAULT_CONTAINER_CLASS;

    static
    {
        arguments.put("-s",
                      new String[] { "Setup File",
                                     "Describes the setup information, " +
                                        "ie files, directories, steps, etc",
                                     DEFAULT_SETUP_FILE,
                                     ""});

        arguments.put("-c",
                      new String[] { "Container Class",
                                     "Specifies the User Interface template.",
                                     DEFAULT_CONTAINER_CLASS,
                                     ""});

        arguments.put("-p",
                      new String[] { "Persistor Class",
                                     "The class that will read the package file.",
                                     DEFAULT_PERSISTOR_CLASS,
                                     ""});

        /*arguments.put("-a",
                      new String[] { "Package Archive",
                                     "The archive where the program files " +
                                     "are packed in.",
                                     DEFAULT_PACKAGE_FILE,
                                     ""});*/
    }

        /**
         * The usage for this program.
         */
    protected static String usageString()
    {
        String out = "Usage: Main options package_file\n";
        out += "Options: \n";

        Iterator keys = arguments.keySet().iterator();

        while (keys.hasNext())
        {
            String opt = (String)(keys.next());

            String[] optInfo = (String[])(arguments.get(opt));
            out += opt + "    <" + optInfo[0] + ">\n";
            out += "     Description: " + optInfo[1] + "\n";
            out += "     Default: " + optInfo[2] + "\n";
        }

        return out;
    }

        /**
         * Process the arguments and store them.
         */
    protected static boolean processArguments(String args[])
    {
        for (int i = 0;i < args.length - 1;i++)
        {
            if (args[i].equalsIgnoreCase("-s"))
            {
                setupFile = args[++i];
            } else if (args[i].equalsIgnoreCase("-p"))
            {
                persistorClass = args[++i];
            } else if (args[i].equalsIgnoreCase("-a"))
            {
                packageArchive = args[++i];
            } else if (args[i].equalsIgnoreCase("-c"))
            {
                containerClass = args[++i];
            } else
            {
                System.out.println(usageString());
                return false;
            }
        }
        packageArchive = args[args.length - 1];

        return true;
    }

        /**
         * Main entry function.
         */
    protected static void realMain(String args[]) throws Exception
    {
            // if arguments cannot be processed then quit
        if ( ! processArguments(args))
        {
            AppLauncher.programQuit();
        }

            // now open up the files and go through it...
            // Now the compactness issue is sorted because everything 
            // is in the one file.  However issue of security also needs to
            // be addressed.  The main issue is that class files should be
            // encrypted with either key or any other Encryption algorithm
            // if possible.  These "algorithms" will be in the installer
            // class package.
        Grinder grinder = new Grinder();

        Persistor persistor =
                (Persistor)
                (Class.forName(persistorClass).newInstance());

            // or alternatively use the FormatChecker to see what 
            // kind of persistor is required to decode this file...
            // Like: SetupReader setupReader =
            //          FormatChecker.getSetupReader(setupFile);

        //grinder.setPersistor(persistor);

        GrinderContainer gContainer =
                (GrinderContainer)
                (Class.forName(containerClass).newInstance());

        grinder.setData(
            (Blend)persistor.
                readFromStream(new FileInputStream(setupFile)));

            // now that the setup is read, run the Grinder object
        grinder.run();
    }

        /**
         * Main function.
         */
    public static void main(String args[])
        throws Exception
    {
        AppLauncher launcher = new AppLauncher();

        launcher.launch("com.sri.apps.brewery.grinder.Main",
                        "realMain",
                        args,
                        args.length);
    }
}
