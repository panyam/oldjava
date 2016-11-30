
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
 * Describes a chain
 */
public class Chain
{
        /**
         * Name of the table.
         */
    public String name;
    public Expression exp;

        /**
         * Constructor.
         */
    public Chain(String n)
    {
        name = n;
    }

        /**
         * Tells if this chain equals the given name.
         */
    public boolean equals(String name)
    {
        return this.name.equals(name);
    }
}

