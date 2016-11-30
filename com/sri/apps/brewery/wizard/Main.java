

package com.sri.apps.brewery.wizard;

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
 * loads the Wizard object to perform the installation.
 *
 * @author Sri Panyam
 */
public class Main
{
        /**
         * Main entry function.
         */
    public static void realMain(String args[]) throws Exception
    {
            // now open up the files and go through it...
            // Now the compactness issue is sorted because everything 
            // is in the one file.  However issue of security also needs to
            // be addressed.  The main issue is that class files should be
            // encrypted with either key or any other Encryption algorithm
            // if possible.  These "algorithms" will be in the installer
            // class package.
        BreweryWizard bWizard = new BreweryWizard("wizard.conf");

        bWizard.startGUI();
    }

        /**
         * Main function.
         */
    public static void main(String args[]) throws Exception
    {
        AppLauncher launcher = new AppLauncher();

        launcher.launch("com.sri.apps.brewery.wizard.Main",
                        "realMain",
                        args,
                        args.length);
    }
}
