package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;

/**
 * A match object that matches the flags in a packet.
 */
public class FlagMatch
{
        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchByte(Packet packet,
                             int offset,
                             int flags,
                             boolean allFlags,
                             boolean invert)
    {
        int val = (packet.data[offset] & 0x000000ff) & flags;
        return invert ? (allFlags ? val != flags : val == 0) :
                        (allFlags ? val == flags : val != 0);
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchShort(Packet packet,
                              int offset,
                              int flags,
                              boolean allFlags,
                              boolean invert)
    {
        int val = packet.getShort(offset);

        return invert ? (allFlags ? val != flags : val == 0) :
                        (allFlags ? val == flags : val != 0);
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchInt(Packet packet,
                            int offset,
                            int flags,
                            boolean allFlags,
                            boolean invert)
    {
        int val = packet.getInteger(offset);

        return invert ? (allFlags ? val != flags : val == 0) :
                        (allFlags ? val == flags : val != 0);
    }
}
