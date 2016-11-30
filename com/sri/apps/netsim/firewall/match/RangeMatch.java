package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;

/**
 * A match object that matches items within a range.
 */
public class RangeMatch
{
        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchByte(Packet packet,
                             int offset,
                             int min, int max,
                             boolean invert)
    {
        int val = (packet.data[offset] & 0x000000ff);
        return invert ? (offset < min || offset > max) :
                        (val >= min && val <= max);
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchShort(Packet packet,
                              int offset,
                              int min, int max,
                              boolean invert)
    {
        int val = packet.getShort(offset);
        return invert ? (offset < min || offset > max) :
                        (val >= min && val <= max);
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchInt(Packet packet,
                            int offset,
                            long min, long max,
                            boolean invert)
    {
        long val = packet.getInteger(offset) & 0xffffffffl;

        return invert ? (val <  min || val >  max) :
                        (val >= min && val <= max);
    }
}
