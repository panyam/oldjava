

package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;


/**
 * A class that does Limit.
 */
public class LimitMatch extends Match
{
    public final static String MATCH_NAME = "limit";
    static
    {
        registerMatch(MATCH_NAME, LimitMatch.class);
    }

        /**
         * Process a command and extract the parameters and return
         * the number of arguments "extracted"
         * index points to the first argument AFTER the -m matchName 
         * optiosn.
         *
         * limit match command is:
         * -m limit --limit rate --limit-burst rate
         */
    public int processCommand(String argv[], int argc, int index) 
        throws Exception
    {
        throw new Exception("Limit match not yet implemented.");
    }

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
}
