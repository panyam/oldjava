


package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;
import java.util.*;


/**
 * A class that does Limit.
 */
public class StateMatch extends Match
{
    public final static String MATCH_NAME = "state";
    static
    {
        registerMatch(MATCH_NAME, StateMatch.class);
    }

        /**
         * Tells which stateflags to look for.
         */
    byte stateFlags = 0;

    public final static byte STATE_INVALID      = 1 << 0;
    public final static byte STATE_NEW          = 1 << 1;
    public final static byte STATE_ESTABLISHED  = 1 << 2;
    public final static byte STATE_RELATED      = 1 << 3;

        /**
         * Get the name of the match.
         */
    public String getName()
    {
        return MATCH_NAME;
    }

        /**
         * See if a packet matches.
         */
    public boolean packetMatches(Packet packet,
                                 int inIntId, int outIntId,
                                 Firewall caller)
    {
        return false;
    }

        /**
         * Tells if another object is equal to this one.
         */
    public boolean equals(Object obj)
    {
        if (obj == this) return true;

        return obj instanceof StateMatch &&
                (((StateMatch)obj).stateFlags == stateFlags);
    }
}
