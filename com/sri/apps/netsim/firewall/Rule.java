
package com.sri.apps.netsim.firewall;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.*;
import com.sri.netsim.firewall.match.*;
import com.sri.netsim.firewall.target.*;

/**
 * A firewall rule.
 * Basically a rule is processed and returned to the firewall class
 * which then changes its own connection tracking tables to 
 * take action on packets as necessary.
 */
public class Rule implements JumpTarget
{
    public final static int MATCH_PROTO         = 1 << 0;
    public final static int MATCH_SRC           = 1 << 1;
    public final static int MATCH_DST           = 1 << 2;
    public final static int MATCH_IN_IFACE      = 1 << 3;
    public final static int MATCH_OUT_IFACE     = 1 << 4;

        // TCP, UDP and ICMP matches
        // we also need ways of having dependencies..
        // So we can check if other flags are turned on when
        // this is on...
    public final static int MATCH_SRC_PORT      = 1 << 5;
    public final static int MATCH_DST_PORT      = 1 << 6;
    public final static int MATCH_TCP_FLAGS     = 1 << 7;
    public final static int MATCH_SYN_FLAGS     = 1 << 8;
    public final static int MATCH_ICMP_TYPE     = 1 << 9;

        // multi port matches...
    public final static int MATCH_MULTI_SPORT   = 1 << 10;
    public final static int MATCH_MULTI_DPORT   = 1 << 11;
    public final static int MATCH_MULTI_PORT    = 1 << 12;

        // mac match...
    public final static int MATCH_MAC_SOURCE    = 1 << 12;

        // match limit and limit burst
    public final static int MATCH_LIMIT         = 1 << 13;
    public final static int MATCH_LIMIT_BURST   = 1 << 14;

        // mark match
    public final static int MATCH_MARK          = 1 << 15;

        // match the state of a connection
    public final static int MATCH_STATE         = 1 << 15;

        // the jump target
    JumpTarget target = BasicTarget.ACCEPT;

        /**
         * Objects for explicit matches.
         */
    Match matches[] = null;
    int nMatches = 0;

        /**
         * The match classes that are used here.
         */
    byte matchData[] = null;

        /**
         * Match date for the generic and implicit matches only
         */
    byte matchDataSize = 0;

        /**
         * Tells which flags are used
         */
    int flagsToUse = 0;

        /**
         * Tells which flags are inverted.
         */
    int flagsToInvert = 0;

        /**
         * Constructor.
         */
    public Rule()
    {
    }

    public void addExplicitMatch(Match match)
    {
        if (matches == null) matches = new Match[1];

        if (nMatches == matches.length)
        {
            Match temp[] = matches;
            matches = new Match[nMatches + 2];
            System.arraycopy(temp, 0, matches, 0, nMatches);
            temp = null;
        }

        matches[nMatches++] = match;
    }

        /**
         * Set the size of the data
         * required for implicit and explicity matches.
         */
    public void setMatchDataSize(int mdSize)
    {
        this.matchDataSize = (byte)(mdSize & 0xff);
        matchData = new byte[mdSize];
    }

        /**
         * Process a packet and follow all the rules.
         */
    public boolean processPacket(Packet packet, int inIntId, int outIntId,
                                 Firewall caller)
    {
        int offset = 0;

            // now that the xplicit matches have been set,
            // set the data for the implicity matches...
        if ((flagsToUse & MATCH_PROTO) != 0)
        {
            if ((flagsToInvert & MATCH_PROTO) == 0)
            {
            } else
            {
            }
        }
        if ((flagsToUse & MATCH_SRC) == 0)
        {
            return false;
        }
        if ((flagsToUse & MATCH_DST) == 0)
        {
            return false;
        }
        if ((flagsToUse & MATCH_IN_IFACE) == 0)
        {
            return false;
        }
        if ((flagsToUse & MATCH_OUT_IFACE) == 0)
        {
            return false;
        }
        if ((flagsToUse & MATCH_SRC_PORT) == 0)
        {
            if ((flagsToInvert & MATCH_SRC_PORT) == 0)
            {
            } else
            {
            }
        }
        if ((flagsToUse & MATCH_DST_PORT) == 0)
        {
            if ((flagsToInvert & MATCH_DST_PORT) == 0)
            {
            } else
            {
            }
        }
        if ((flagsToUse & MATCH_TCP_FLAGS) == 0)
        {
            if ((flagsToInvert & MATCH_TCP_FLAGS) == 0)
            {
            } else
            {
            }
        }
        if ((flagsToUse & MATCH_SYN_FLAGS) == 0)
        {
            if ((flagsToInvert & MATCH_SYN_FLAGS) == 0)
            {
            } else
            {
            }
        }
        if ((flagsToUse & MATCH_ICMP_TYPE) == 0)
        {
            if ((flagsToInvert & MATCH_ICMP_TYPE) == 0)
            {
            } else
            {
            }
        }

        for (int i = 0;i < nMatches;i++)
        {
            if (! matches[i].packetMatches(packet, inIntId, outIntId, caller))
            {
                return false;
            }
        }
        
        return true;
    }

        /**
         * Tells if this rule equals another rule.
         * Basically compares all the matches.
         */
    public boolean equals(Object obj)
    {
        if (! (obj instanceof Rule)) return false;
        if (obj == this) return true;
        Rule rule = (Rule)obj;

        return false;
    }
}
