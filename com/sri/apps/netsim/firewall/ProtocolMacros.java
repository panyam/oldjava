package com.sri.apps.netsim.firewall;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.*;
import com.sri.netsim.firewall.match.*;
import java.util.*;

/**
 *  Contains protocol macros.  
 *  ie what should get expanded to what
 *  eg ip.protocol would get expended to something like data[offset] & 0xff
 *  ad so on..
 */
public class ProtocolMacros extends Router
{
    protected static ProtocolComparer protoComp = null;
    protected static FieldComparer fieldComp = null;

        /**
         * List of all our protocols.
         */
    protected static BinarySearchList protocols = new BinarySearchList();

        /**
         * Constructor.
         */
    public ProtocolMacros()
    {
        if (protoComp == null)
        {
            protoComp = new ProtocolComparer();
        }
        if (fieldComp == null)
        {
            fieldComp = new FieldComparer();
        }
    }

        /**
         * Get the protocol given the name.
         */
    public Protocol getProtocol(String name)
    {
        return (Protocol)
                protocols.items[protocols.findItem(name, protoComp, false)];
    }

    public class Protocol
    {

            /**
             * Name of the protocol.
             */
        public String name;

            /**
             * The friends of the protocol.
             */
        public BinarySearchList fields = new BinarySearchList();

            /**
             * Add a new field.
             */
        public void addField(String name, Object value)
        {
            fields.findItem(name, fieldComp, true);
        }

            /**
             * Get the value of a given field.
             * null if the field doesnt exist.
             */
        public Object getField(String name)
        {
            return (ProtocolField)
                        fields.items[fields.findItem(name, fieldComp, false)];
        }
    }

        /**
         * A class for the protocol.
         */
    public class ProtocolField
    {
            /**
             * Name of the field.
             */
        public String name;

            /**
             * Value for the field.
             * This could be an expression tree
             * or just the macro.
             * Havent decided yet.
             */
        public Object value;
    }

        /**
         * A object that compares two protocol field objects.
         */
    class FieldComparer extends ObjectComparer
    {
        public int compare(Object obj1, Object obj2)
        {
            return ((ProtocolField)obj1).name.
                        compareTo(((ProtocolField)obj2));
        }
    }

        /**
         * A object that compares two protocol objects.
         */
    class ProtocolComparer extends ObjectComparer
    {
        public int compare(Object obj1, Object obj2)
        {
            return ((Protocol)obj1).name.compareTo(((Protocol)obj2));
        }
    }
}
