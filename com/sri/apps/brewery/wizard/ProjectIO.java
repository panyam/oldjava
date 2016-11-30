package com.sri.apps.brewery.wizard;

import java.io.*;

/**
 * An interface for objects that can load or save objects to
 * storage.
 */
public interface ProjectIO
{
        /**
         * Save the project.
         */
    public void saveProject(OutputStream oStream);

        /**
         * Load the project.
         */
    public void loadProject(InputStream iStream);
}
