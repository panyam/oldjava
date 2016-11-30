package com.sri.apps.netsim.firewall;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.*;
import com.sri.netsim.firewall.match.*;
import com.sri.netsim.firewall.*;
import com.sri.java.*;
import com.sri.java.bytecode.*;
import com.sri.java.expression.*;
import com.sri.java.blocks.*;
import com.sri.java.types.*;
import com.sri.java.utils.*;
import java.util.*;


/**
 * Defines a table.
 */
public class Table
{
        /**
         * Name of the table.
         */
    public String name;

        /**
         * List of chains for this table.
         */
    public Vector chains = null;

        /**
         * Constructor.
         */
    public Table(String n)
    {
        name = n;
    }

        /**
         * Get the chains by the given name.
         */
    public Chain getChain(String name)
    {
        for (Enumeration en = chains.elements();en.hasMoreElements();)
        {
            Chain ch = (Chain)en.nextElement();
            if (ch.equals(name)) return ch;
        }
        return null;
    }

        /**
         * Tells if this chain equals the given name.
         */
    public boolean equals(String name)
    {
        return this.name.equals(name);
    }

        /**
         * Add a new chain.
         */
    public Chain addChain(String name)
    {
        Chain newChain = new Chain(name);
        chains.addElement(newChain);
        return newChain;
    }
}

