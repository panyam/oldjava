package svtool.core;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import svtool.core.*;

/**
 * A few comonly used utilities.
 *
 * @author Sri Panyam
 */
public class SVUtils
{
        /**
         * The resource manager object for this application scope that
         * handles opening of resources like files, images and sounds.
         */
    public static ResourceManager resourceManager = null;

        /**
         * Logs and takes action on an exception.
         */
    public static void logException (Exception exc)
    {
        exc.printStackTrace();
    }

        /**
         * Logs an error.
         */
    public static void logError(String error)
    {
        System.out.println(error);
    }

        /**
         * Place a window on the window with vertical and horizontal
         * alignment.
         *
         * This function will only work propertly for Window or JWindow
         * objects.  Anything else, it will fail.
         */
    public static void alignComponent(Component comp,
                                      int vertAlign, int horizAlign)
    {
        int left = 0, top = 0;

        Dimension cs = comp.getSize();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

        if (vertAlign < 0) top = 0;
        else if (vertAlign == 0) top = (ss.height - cs.height) / 2;
        else top = ss.height - cs.height;

        if (horizAlign < 0) left = 0;
        else if (horizAlign == 0) left = (ss.width - cs.width) / 2;
        else top = ss.width - cs.width;
        comp.setLocation(left, top);
    }

        /**
         * Load a jar file with the class loader.
         */
    public static void loadJarToPath(String jarName)
    {
        if (jarName == null) return ;

        /**  TODO: Fix this up.
        try
        {
            URLClassLoader classLoader =
                new URLClassLoader(new URL[] { new URL(jarName) } );
            UIManager.put("ClassLoader", classLoader);
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }
        */
    }

        /**
         * Load a class and return a new instance of it, logging any errors
         * that may arise.
         */
    public static Object createInstance(String className)
    {
        if (className == null) return null;
        try
        {
            Class scClass = Class.forName(className);
            return scClass.newInstance();
        } catch (ClassNotFoundException cnfExc)
        {
            SVUtils.logError("Class not found: " + className);
        } catch (IllegalAccessException iaexc)
        {
            SVUtils.logError("Illegal class access: " + className);
        } catch (InstantiationException iexc)
        {
            SVUtils.logError("Could not instantiate class: " +
                                  className);
        }
        return null;
    }
}
