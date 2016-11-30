package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;
import java.util.*;


/**
 * A match class.
 *  This is the super class of all matches.
 */
public abstract class Match
{
        // ALl match class must register them in here
    public static Hashtable matchTable = new Hashtable();

        /**
         * Register a match name in our table.
         */
    protected static void registerMatch(String name, Class className)
    {
        Object obj = matchTable.get(name);
        if (obj != null) return ;
        //RuleParser.Assert(obj == null, "Match \"" + name + "\" already exists.");

        matchTable.put(name, className);
    }

        /**
         * Gets a predefined match class.
         */
    public static Class getPredefinedMatch(String matchname)
    {
        Object matchClass = matchTable.get(matchname);
        return (Class)matchClass;
    }

        /**
         * Whether to invert the match.
         */
    public boolean invert = false;

        /**
         * Process a command and extract the parameters and return
         * the number of arguments "extracted"
         * index points to the first argument AFTER the -m matchName 
         * optiosn.
         */
    //public abstract int processCommand(String argv[], int argc, int index) 
        //throws Exception ;

        /**
         * Get the name of the match.
         */
    public abstract String getName();

        /**
         * See if a packet matches.
         */
    public abstract boolean packetMatches(Packet packet,
                                          int inIntId,
                                          int outIntId,
                                          Firewall caller);
}
