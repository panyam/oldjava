
package com.sri.apps.brewery.io;

import java.util.*;
import java.io.*;

/**
 * The type of a persistable object.
 *
 * @author Sri Panyam
 */
public class PersistableType
{
    private static byte typeCounter = 0;
    private static byte listCounter = 0;

        /**
         * The types of each field that can be persisted.
         */
    public final static byte NO_TYPE = typeCounter ++;
    public final static byte BOOL_TYPE = typeCounter ++;
    public final static byte BYTE_TYPE = typeCounter ++;
    public final static byte CHAR_TYPE = typeCounter ++;
    public final static byte SHORT_TYPE = typeCounter ++;
    public final static byte INT_TYPE = typeCounter ++;
    public final static byte FLOAT_TYPE = typeCounter ++;
    public final static byte LONG_TYPE = typeCounter ++;
    public final static byte DOUBLE_TYPE = typeCounter ++;
    public final static byte STRING_TYPE = typeCounter ++;
    public final static byte PERSISTABLE_TYPE = typeCounter ++;

        /**
         * Tells the class type for each one.
         */
    public final static Class typeClasses[] = new Class[]
    {
        null,
        boolean.class,
        byte.class,
        char.class,
        short.class,
        int.class,
        float.class,
        long.class,
        double.class,
        String.class,
        Persistable.class
    };

        /**
         * Difference between a collection and array is that
         * are base arrays which hold any type, where as collections are
         * objects that are inherited from the Collection interface.  Also,
         * it is assumed that the collection type means all items in the
         * collection are instances of the Persistable interface.
         */
    public final static byte NONE = listCounter++;
    public final static byte COLLECTION = listCounter++;
    public final static byte ARRAY = listCounter++;

    public byte type = NO_TYPE;
    public byte listType = NONE;
    public Class typeClass;

    protected static PersistableType typeCache[][] =
                        new PersistableType[listCounter][typeCounter];

    static
    {
        for (byte lType = 0;lType < listCounter;lType++)
        {
            for (byte pType = 0;pType < typeCounter;pType++)
            {
                typeCache[lType][pType] = new PersistableType(lType, pType);
            }
        }
    }

        /**
         * Constructor.
         * Cannot be instantiated externally.
         */
    protected PersistableType()
    {
        this(NO_TYPE, NONE);
    }

        /**
         * Constructor.
         * Cannot be instantiated externally.
         */
    protected PersistableType(byte type, byte listType)
    {
        this.type = type;
        this.listType = listType;
    }

        /**
         * Returns the type that matches the given item rather than
         * create a new item.  Follows the Flyweight design pattern.
         */
    public static PersistableType getType(byte lType, byte type)
    {
        return typeCache[lType][type];
    }
}
