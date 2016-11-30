
package com.sri.apps.brewery.core;

import java.awt.*;
import javax.swing.*;

/**
 * A few comonly used utilities.
 *
 * @author Sri Panyam
 */
public class BreweryUtils
{
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
         * Load a class and return a new instance of it, logging any errors
         * that may arise.
         */
    public static Object createInstance(String className)
    {
        try
        {
            Class scClass = Class.forName(className);
            return scClass.newInstance();
        } catch (ClassNotFoundException cnfExc)
        {
            BreweryUtils.logError("Class not found: " + className);
        } catch (IllegalAccessException iaexc)
        {
            BreweryUtils.logError("Illegal class access: " + className);
        } catch (InstantiationException iexc)
        {
            BreweryUtils.logError("Could not instantiate class: " +
                                  className);
        }
        return null;
    }
}
