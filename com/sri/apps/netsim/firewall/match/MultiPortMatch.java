
package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;
import java.util.*;


/**
 * A class that does MacMatch.
 */
public class MultiPortMatch extends Match
{
    public final static String MATCH_NAME = "multiport";
    static
    {
        registerMatch(MATCH_NAME, MultiPortMatch.class);
    }

        /**
         * The ports to match.
         * Instead of storing shorts, we are storing
         * the short as two bytes...
         */
    byte ports[];

    public final static byte SRC_PORT_MATCH = 0;
    public final static byte DST_PORT_MATCH = 1;
    public final static byte PORT_MATCH = 2;

        // number of ports to match
        // This also stores the match type...
        // ie whether its a destination port match or sourceport match
        // or ports match!! 
        // The number of ports info is in the first 4 bits
        // and the match type is stored in bits 4 and 5
    byte nPorts = 0;

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
        int type = nPorts >> 4;
        int nPorts = this.nPorts & 0x0F;
        byte data[] = packet.data;

            // get the header length!!
            // should be 5 or 6
            // we are doing << 2 because we want to multiply it by
            // 4 as this is the number of words that the ip header 
            // is sized as..
        int hlen = (data[IPUtils.IP_OFFSET] & 0x0F) << 2;

        int off1 = IPUtils.IP_OFFSET + hlen;

            // check if the mac address matches this packet...
        if (type == DST_PORT_MATCH)
        {
            off1 += 2;
        } else if (type == PORT_MATCH)
        {
                // with --port match, the source port MUST
                // equal the destination port!!!
            if (data[off1] != data[off1 + 2] ||
                data[off1] != data[off1 + 2])
            {
                return false;
            }
        }

        int off2 = off1 + 1;
        for (int i = 0;i < nPorts << 1;i += 2)
        {
                // see if any of the ports match...
            if (data[off1] == ports[i] && data[off2] == ports[i + 1])
                return true;
        }
        return false;
    }

        /**
         * Tells if another object is equal to this one.
         */
    public boolean equals(Object obj)
    {
        if (obj == this) return true;

        if (obj instanceof MultiPortMatch)
        {
            MultiPortMatch mpm = (MultiPortMatch)obj;
            if (mpm.nPorts != nPorts) return false;

                // now search if all the items in this.ports
                // are there in the other one ports
            int np = nPorts & 0x0F;
            int i, j;
            for (i = 0;i < np;i++)
            {
                for (j = 0;mpm.ports[i] != ports[i] && j < np;j++);
                if (j == np) return false;
            }
            return true;
        }
        return false;
    }
}
