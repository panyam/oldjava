package com.sri.netsim.firewall.match;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.firewall.*;
import com.sri.netsim.*;

/**
 * A match object that matches items from a set of items.
 */
public class ValueMatch
{
        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchByte(Packet packet, int offset,
                             int nVals, int values[])
    {
        int val = (packet.data[offset] & 0x000000ff);
        for (int i = 0;i < nVals;i++)
        {
            if (values[i] == val) return true;
        }
        return false;
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchShort(Packet packet, int offset,
                             int nVals, int values[])
    {
        int val = packet.getShort(offset);
        for (int i = 0;i < nVals;i++)
        {
            if (values[i] == val) return true;
        }
        return false;
    }

        /**
         * Tells if a packet can match this criteria.
         */
    public boolean matchInt(Packet packet, int offset,
                             int nVals, int values[])
    {
        int val = packet.getInteger(offset);

        for (int i = 0;i < nVals;i++)
        {
            if (values[i] == val) return true;
        }
        return false;
    }
}
