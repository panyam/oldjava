package com.sri.apps.netsim;

import java.util.*;

/**
 * A class that holds a range of IPs
 * so that we can see if an IP matches
 * a given address.
 */
public class IPRange
{
        /**
         * Tells if we are doing a negation rule.
         */
    boolean negate = false;

        /**
         * Range IPs.
         * This contains 2 things:
         * The first four bytes of rangeIPs[i] contains the
         * starting IP address and the last 4 bytes contains
         * the ending IP.
         */
    public long ipRanges[] = null;
    int nRanges = 0;

        /**
         * Individual IPs that can be matched.
         */
    public int ips[] = null;
    int nIPs = 0;

        /**
         * Constructor.
         */
    public IPRange()
    {
    }

        /**
         * Constructor.
         */
    public IPRange(String ipRange)
    {
        parseRange(ipRange);
    }

        /**
         * Parses an IP range given as a string.
         * Format of a range is:
         *
         * ipranges ::= ipRange "," ipranges
         *          |   ipRange
         *          ;
         *
         * ipRange ::= IP
         *          | IP "-" IP
         *          | IP "/" mask
         */
    public void parseRange(String ipRange)
    {
        ipRange = ipRange.trim();

            // check if its a negation rule
        negate = false;
        if (ipRange.charAt(0) == '!')
        {
            negate = true;
            ipRange = ipRange.substring(1);
        }

        int startingIP, endingIP, maskLength;
        StringTokenizer ranges = new StringTokenizer(ipRange, ",", false);

        while (ranges.hasMoreTokens())
        {
            String currRange = ranges.nextToken().trim();

            int dashIndex = currRange.indexOf('-');

            if (dashIndex >= 0)
            {
                startingIP =
                    Utils.stringToIP(currRange.substring(0, dashIndex - 1));
                endingIP =
                    Utils.stringToIP(currRange.substring(dashIndex + 1));

                addIPRange(startingIP, endingIP);
            } else
            {
                int slashIndex = currRange.indexOf('/');
                if (slashIndex >= 0)
                {
                    maskLength =
                        Utils.stringToMaskLength(
                                currRange.substring(slashIndex));

                    int netMask = 0xffffffff << (32 - maskLength);

                    startingIP =
                        Utils.stringToIP(
                            currRange.substring(0, slashIndex - 1))
                                                        & netMask;

                    System.out.println("lsip, SIP = " + (((long)startingIP) & 0x00000000ffffffffL) + ", " + Utils.ipToString(startingIP));

                    addIPRange(startingIP,
                               (((long)startingIP) & 0x00000000ffffffffL) + 
                               (((long)(1 << (32 - maskLength))) & 0x00000000ffffffffL) - 1);
                } else
                {
                    addIP(Utils.stringToIP(currRange));
                }
            }
        }
    }

        /**
         * Add an IP Range.
         */
    public void addIPRange(long firstIP, long lastIP)
    {
        if (ipRanges == null) ipRanges = new long[1];
        if (nRanges >= ipRanges.length)
        {
            long range2[] = ipRanges;
            ipRanges = new long[nRanges + 2];
            System.arraycopy(range2, 0, ipRanges, 0, nRanges);
        }
        ipRanges[nRanges++] = ((firstIP & 0xffffffff) << 32) |
                               (lastIP &  0xffffffff);
    }

        /**
         * Add an IP.
         */
    public void addIP(int ip)
    {
        if (ips == null) ips = new int[1];
        if (nIPs >= ips.length)
        {
            int ip2[] = ips;
            ips = new int[nIPs + 2];
            System.arraycopy(ip2, 0, ips, 0, nIPs);
        }
        ips[nIPs++] = ip;
    }

        /**
         * See if the given address matches
         * any of the values in this class.
         */
    public boolean ipMatches(int ip)
    {
        for (int i = 0;i < nIPs;i++)
        {
            if (ips[i] == ip) return (negate ? false : true);
        }

        long val = ip & 0x00000000ffffffffL;

        for (int i = 0;i < nRanges;i++)
        {
            if (val >= (0x00000000ffffffffL & (ipRanges[i] >> 32)) ||
                val <= (0x00000000ffffffffL & ipRanges[i]))
            {
                return (negate ? false : true);
            }
        }
        return (negate ? true : false);
    }

        /**
         * Converts to a string.
         */
    public String toString()
    {
        return toString(false);
    }

        /**
         * Converts to a string.
         */
    public String toString(boolean multiLine)
    {
        String out = "";
        boolean canAddComas = false;

        for (int i = 0;i < nIPs;i++)
        {
            if (canAddComas) out += (multiLine ? "\n    " : ", ");
            else canAddComas = true;
            out += Utils.ipToString(ips[i]);
        }

        for (int i = 0;i < nRanges;i++)
        {
            if (canAddComas) out += (multiLine ? "\n    " : ", ");
            else canAddComas = true;
            out += Utils.ipToString((int)((ipRanges[i] >> 32) & 0xffffffff));
            out += "-";
            out += Utils.ipToString((int)(ipRanges[i] & 0xffffffff));
        }

        if (negate) out = " ! " + out;
        return out;
    }
}
