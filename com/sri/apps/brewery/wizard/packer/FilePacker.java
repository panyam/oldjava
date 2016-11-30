/*
 * FilePacker.java
 *
 * Created on 29 July 2004, 12:52
 */

package com.sri.apps.brewery.wizard.packer;

import java.io.*;

/**
 * Super Interface of all packer objects.  These will "pack" a file into a 
 * package file.  This may also include encryption of the files.
 *
 * @author  Sri Panyam
 */
public interface FilePacker
{
        /**
         * Writes the setup information to the output stream.
         */
    public void storeSetupDetails(OutputStream oStream);

        /**
         * Writes the complete package information to the output stream.
         * This will be all the files, their structures and any and all
         * info about packages (eg, dependancies, file units and so on) and
         * may also include encryption.
         */
    public void storePackageDetails(OutputStream oStream);
}
