package svtool;

/**
 * Holds version information for the Service Validator Tool.
 *
 *@author   Sri Panyam
 */
public class SVVersion
{
        /**
         * The major version of the application.
         */
    protected static String MAJOR_VERSION  = "2";

        /**
         * The minor version of the application.
         */
    protected static String MINOR_VERSION = "00";

        /**
         * The build version of the application.
         */
    protected static String BUILD_NUMBER = "005";

        /**
         * Get the title for the application frame.
         *
         * @return  The title string for the application frame.
         */
    public static String getApplicationFrameTitle()
    {
        return "Service Validator V" +
                SVVersion.getVersion();
    }

        /**
         * Return the version as a string.
         */
    public static String getVersion()
    {
        return MAJOR_VERSION + "__" +
               MINOR_VERSION + "__" +
               BUILD_NUMBER;
    }
}
