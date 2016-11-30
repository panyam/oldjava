
package com.sri.apps.brewery.io;

import java.util.*;
import java.io.*;

/**
 * Persistor that will persist items in the customer Binary format.
 *
 * @author Sri Panyam
 */
public class DefaultBinaryPersistor implements Persistor
{
        /**
         * Write a persistable object from stream.
         *
         * The way an object is persisted is first by writing its class
         * name.  Then writing all its attributes.
         */
    public void writeToStream(Persistable persObj,
                              OutputStream oStream) throws Exception
    {
        String className = persObj.getClass().toString();

        DataOutputStream dout =
            (oStream instanceof DataOutputStream ? 
                (DataOutputStream)oStream : 
                new DataOutputStream(
                    oStream instanceof BufferedOutputStream ? 
                        oStream : new BufferedOutputStream(oStream)));

            // write the class Name first
        GrinderIOUtils.writeString(dout, className);

            // get the field count...
        int nFields = persObj.fieldCount();

            // Persist each field recursively...
        for (int i = 0;i < nFields;i++)
        {
            PersistableType fType = persObj.getFieldType(i);
            byte objType = fType.type;
            byte lType = fType.listType;
            Object fValue = persObj.getFieldValue(i);

                // object is a collection type, so
                // get the iterator and write each one...
            if (lType == PersistableType.COLLECTION)
            {
                writeCollection(dout, fValue);
            } else if (lType == PersistableType.ARRAY)
            {
                int arrayLen = persObj.getFieldLength(i);
                writeArray(dout, fValue, fType, 0, arrayLen);
            } else
            {
                writeObject(dout, fValue);
            }
        }
    }

        /**
         * Writes a collection to the output stream.
         */
    protected void writeCollection(DataOutputStream dout,
                                   Object collection)
        throws Exception
    {
        Iterator iter = ((Collection)collection).iterator();

            // write the size...
        dout.writeInt(((Collection)collection).size());

            // write each item...
        while (iter.hasNext())
        {
            writeObject(dout, iter.next());
        }
    }

        /**
         * Writes an array to the stream.
         *
         * All items in the array would be of type objType.
         */
    protected void writeArray(DataOutputStream dout, Object array,
                              PersistableType objType,
                              int offset, int len)
        throws Exception
    {
        byte itemType = objType.type;
        int end = offset + len;

        if (itemType == PersistableType.BOOL_TYPE)
        {
            boolean []anArray = (boolean [])array;
            for (int i = offset;i < end;i++) dout.writeBoolean(anArray[i]);
        } else if (itemType == PersistableType.BYTE_TYPE)
        {
            byte []anArray = (byte[])array;
            for (int i = offset;i < end;i++) dout.writeByte(anArray[i]);
        } else if (itemType == PersistableType.CHAR_TYPE)
        {
            char []anArray = (char[])array;
            for (int i = offset;i < end;i++) dout.writeChar(anArray[i]);
        } else if (itemType == PersistableType.SHORT_TYPE)
        {
            short []anArray = (short [])array;
            for (int i = offset;i < end;i++) dout.writeShort(anArray[i]);
        } else if (itemType == PersistableType.INT_TYPE)
        {
            int []anArray = (int[])array;
            for (int i = offset;i < end;i++) dout.writeInt(anArray[i]);
        } else if (itemType == PersistableType.LONG_TYPE)
        {
            long []anArray = (long[])array;
            for (int i = offset;i < end;i++) dout.writeLong(anArray[i]);
        } else if (itemType == PersistableType.FLOAT_TYPE)
        {
            float []anArray = (float[])array;
            for (int i = offset;i < end;i++) dout.writeFloat(anArray[i]);
        } else if (itemType == PersistableType.DOUBLE_TYPE)
        {
            double []anArray = (double[])array;
            for (int i = offset;i < end;i++) dout.writeDouble(anArray[i]);
        } else if (itemType == PersistableType.STRING_TYPE)
        {
            String []anArray = (String[])array;
            for (int i = offset;i < end;i++) 
            {
                GrinderIOUtils.writeString(dout, anArray[i]);
            }
        } else if (itemType == PersistableType.PERSISTABLE_TYPE)
        {
            Persistable []anArray = (Persistable [])array;
            for (int i = offset;i < end;i++) writeToStream(anArray[i], dout);
        }
    }

        /**
         * Writes basic object types object to the output stream.
         * These are usually called if the object is part of a collection
         * or is basic typed field within a Persistable object.
         */
    protected void writeObject(DataOutputStream dout, Object obj)
        throws Exception
    {
        if (obj instanceof Boolean)
        {
            dout.writeByte(PersistableType.BOOL_TYPE);
            dout.writeBoolean(((Boolean)obj).booleanValue());
        } else if (obj instanceof Byte)
        {
            dout.writeByte(PersistableType.BYTE_TYPE);
            dout.writeByte(((Byte)obj).byteValue());
        } else if (obj instanceof Character)
        {
            dout.writeByte(PersistableType.CHAR_TYPE);
            dout.writeChar(((Character)obj).charValue());
        } else if (obj instanceof Short)
        {
            dout.writeByte(PersistableType.SHORT_TYPE);
            dout.writeShort(((Short)obj).shortValue());
        } else if (obj instanceof Integer)
        {
            dout.writeByte(PersistableType.INT_TYPE);
            dout.writeInt(((Integer)obj).intValue());
        } else if (obj instanceof Long)
        {
            dout.writeByte(PersistableType.LONG_TYPE);
            dout.writeLong(((Long)obj).longValue());
        } else if (obj instanceof Float)
        {
            dout.writeByte(PersistableType.FLOAT_TYPE);
            dout.writeFloat(((Float)obj).floatValue());
        } else if (obj instanceof Double)
        {
            dout.writeByte(PersistableType.DOUBLE_TYPE);
            dout.writeDouble(((Double)obj).doubleValue());
        } else if (obj instanceof String)
        {
            dout.writeByte(PersistableType.STRING_TYPE);
            GrinderIOUtils.writeString(dout, (String)obj);
        } else if (obj instanceof Persistable)
        {
            dout.writeByte(PersistableType.PERSISTABLE_TYPE);
            writeToStream((Persistable)obj, dout);
        }
    }

        /**
         * Read a persistable object from stream.
         */
    public Persistable readFromStream(InputStream iStream) throws Exception
    {
        DataInputStream din = 
            iStream instanceof DataInputStream ? 
            (DataInputStream)iStream :
            new DataInputStream(iStream instanceof BufferedInputStream ? 
                                iStream : 
                                new BufferedInputStream(iStream));

        String className = GrinderIOUtils.readString(din);
        Class objClass = Class.forName(className);
        Persistable persObj = (Persistable)objClass.newInstance();

        int nFields = persObj.fieldCount();

        for (int i = 0;i < nFields;i++)
        {
            PersistableType fType = persObj.getFieldType(i);
            byte objType = fType.type;
            byte lType = fType.listType;
            Object fValue = null;

                // object is a collection type, so
                // get the iterator and write each one...
            if (lType == PersistableType.COLLECTION)
            {
                fValue = readCollection(din);
            } else if (lType == PersistableType.ARRAY)
            {
                fValue = readArray(din, objType);
            } else
            {
                fValue = readObject(din);
            }
            persObj.setFieldValue(i, fValue);
        }

        return persObj;
    }

        /**
         * Reads a collection from the input stream.
         */
    protected Collection readCollection(DataInputStream din)
        throws Exception
    {
        List collection = new LinkedList();

        int nItems = din.readInt();

        for (int i = 0;i < nItems;i++)
        {
            collection.add(readObject(din));
        }

        return collection;
    }

        /**
         * Read an array of itemType objects from the input stream.
         */
    protected Object readArray(DataInputStream din, byte itemType)
        throws Exception
    {
        int length = din.readInt();

        if (itemType == PersistableType.BOOL_TYPE)
        {
            boolean []anArray = new boolean[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readBoolean();
            return anArray;
        } else if (itemType == PersistableType.BYTE_TYPE)
        {
            byte []anArray = new byte[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readByte();
            return anArray;
        } else if (itemType == PersistableType.CHAR_TYPE)
        {
            char []anArray = new char[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readChar();
            return anArray;
        } else if (itemType == PersistableType.SHORT_TYPE)
        {
            short []anArray = new short[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readShort();
            return anArray;
        } else if (itemType == PersistableType.INT_TYPE)
        {
            int []anArray = new int[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readInt();
            return anArray;
        } else if (itemType == PersistableType.LONG_TYPE)
        {
            long []anArray = new long[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readLong();
            return anArray;
        } else if (itemType == PersistableType.FLOAT_TYPE)
        {
            float []anArray = new float[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readFloat();
            return anArray;
        } else if (itemType == PersistableType.DOUBLE_TYPE)
        {
            double []anArray = new double[length];
            for (int i = 0;i < length;i++) anArray[i] = din.readDouble();
            return anArray;
        } else if (itemType == PersistableType.STRING_TYPE)
        {
            String []anArray = new String[length];
            for (int i = 0;i < length;i++)
                    anArray[i] = GrinderIOUtils.readString(din);
            return anArray;
        } else if (itemType == PersistableType.PERSISTABLE_TYPE)
        {
            Persistable []anArray = new Persistable [length];
            for (int i = 0;i < length;i++) 
                anArray[i] = readFromStream(din);
            return anArray;
        }
        return null;
    }

        /**
         * Read an object from the input stream.
         */
    protected Object readObject(DataInputStream din) throws Exception
    {
        byte itemType = din.readByte();

        if (itemType == PersistableType.BOOL_TYPE)
        {
            return new Boolean(din.readBoolean());
        } else if (itemType == PersistableType.BYTE_TYPE)
        {
            return new Byte(din.readByte());
        } else if (itemType == PersistableType.CHAR_TYPE)
        {
            return new Character(din.readChar());
        } else if (itemType == PersistableType.SHORT_TYPE)
        {
            return new Short(din.readShort());
        } else if (itemType == PersistableType.INT_TYPE)
        {
            return new Integer(din.readInt());
        } else if (itemType == PersistableType.LONG_TYPE)
        {
            return new Long(din.readLong());
        } else if (itemType == PersistableType.FLOAT_TYPE)
        {
            return new Float(din.readFloat());
        } else if (itemType == PersistableType.DOUBLE_TYPE)
        {
            return new Double(din.readDouble());
        } else if (itemType == PersistableType.STRING_TYPE)
        {
            return GrinderIOUtils.readString(din);
        } else if (itemType == PersistableType.PERSISTABLE_TYPE)
        {
            return readFromStream(din);
        }

        return null;
    }
}
