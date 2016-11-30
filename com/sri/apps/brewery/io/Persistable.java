package com.sri.apps.brewery.io;

import java.util.*;
import java.io.*;

/**
 * Super class of all grinder objects that can be persisted and loaded
 * from a stream.
 *
 * In order for the screen (or any other information to be persisted), its
 * "persistable" fields have to be known. Each persistable field is a
 * triplet of (name, type, value);
 *
 * So a persistable object when requested will return an iterator over all
 * persistable fields.  The fields themselves can be other Persistable
 * objects.
 *
 * It would be easy to persist non-recursive data types.  But the issue is
 * how to persist items like lists from a persistable object.  For a
 * Persistable type, it can be persisted easily, but other serializable
 * types are the problem.  The collection can be a type as well, in which
 * case how the collection gets persisted will be an issue.  For example
 * with Maps and Sets, how would one know to persist the matching key as
 * well?  If these are stored as Serializable objects then how is the
 * serialization guaranteed?  For example, LinkedList and ArrayList classes
 * are serializable.  Is the object serialized or only the reference?  The
 * idea is that the objects should be persisted rather than serialized.
 * Because, ultimately, these are collections of persistable objects.  On
 * the other hand, if there is a type of Persistable collection, then each
 * item in the collection can be persisted.  But what if the collection is
 * not containing persistable collection?  How can these be persisted?
 * Even simple things like IntLists or arrays or strings, these will need
 * smarter ways of persistance.
 *
 * One way of persisting this non-persistable data would be to create a
 * wrapper class and implement the persistable interface which would work
 * with the arbitrary data.  But then for simple types like Strings or
 * Dates, there would have to be a PersistableString class that would
 * contain the string it is persisting.  This is a waste of resources.  For
 * this, the Serializable property will have to be taken care of.  So we
 * will need Serializable and SerilazableCollection types as well.  The
 * problem is that the object will ONLY be serialized in the object
 * specific way.
 *
 * @author Sri Panyam
 */
public interface Persistable
{
        /**
         * Sets the value of a field.
         */
    public void setFieldValue(String fieldName, Object value);

        /**
         * Sets the value of a field given its index.
         */
    public void setFieldValue(int fieldIndex, Object value);

        /**
         * Get the value of a field.
         */
    public Object getFieldValue(String fieldName);

        /**
         * Get the name of the field at the given index.
         */
    public String getFieldName(int index);

        /**
         * Get the index of a given field.
         */
    public int getFieldIndex(String fieldName);

        /**
         * For a field at the given position, returns its length.  This is
         * ONLY valid if this field is an ARRAY.
         */
    public int getFieldLength(int index);

        /**
         * Get the field value.
         */
    public Object getFieldValue(int index);

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(String fieldName);

        /**
         * Get the field type.
         */
    public PersistableType getFieldType(int index);

        /**
         * Gets a field iterator.
         * Returns the (name, type, value) triplet in each
         * "item" in the iterator.
         */
    //public Iterator fieldIterator();

        /**
         * Tells how many fields are there to be persisted.
         */
    public int fieldCount();
}
