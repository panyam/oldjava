package com.sri.apps.mml;

import java.awt.*;
import java.util.*;

/**
 * The super class of all attributes.
 */
public abstract class MMLAttribute
{
        /**
         * Some predefined ids.
         */
        /**
         * Id of the foreground color attribute.
         */
    public static short FG_ID = registerName("fg");

        /**
         * Id of the background color attribute.
         */
    public static short BG_ID = registerName("bg");

        /**
         * This hashtable holds the ids of the various attribute names.
         */
    protected static Hashtable nameIds = new Hashtable();

        /**
         * A counter that keeps track of the number distinct types of
         * attributes.
         */
    protected static short ATTRIBUTE_ID = 0;

        /**
         * The name of the attribute.
         * These are insensitive.
         */
    protected String name;

        /**
         * The id attribute of the attribute.
         * All attributes of the same name will have the same id.
         */
    protected short id;

        /**
         * Constructor.
         */
    protected MMLAttribute(String name)
    {
        id = registerName(name);
    }

        /**
         * Register an attribute name and
         * return the appropriate attribute id.
         */
    public synchronized static short registerName(String name)
    {
        Object obj = nameIds.get(name);
        if (obj == null)
        {
            obj = new Short(ATTRIBUTE_ID++);
            nameIds.put(name, obj);
        }
        return ((Short)obj).shortValue();
    }

        /**
         * Get the name of this attribute.
         */
    public String getName()
    {
        return name;
    }
}
