package com.sri.apps.brewery.wizard;


/**
 * Holds information about the Brewery application.
 *
 * @author Sri Panyam
 */
public class BreweryVersion
{
    protected static String MAJOR_VERSION  = "0";
    protected static String MINOR_VERSION = "10";
    protected static String BUILD_NUMBER = "005";

        /**
         * Get the title for the application frame.
         */
    public static String getApplicationFrameTitle()
    {
        return "Brewery V" + getVersion();
    }

        /**
         * Get the version string.
         */
    public static String getVersion()
    {
        return MAJOR_VERSION + "." +
               MINOR_VERSION + "." +
               BUILD_NUMBER;
    }
}
