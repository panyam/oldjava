package com.sri.apps.netsim; 



import java.util.*;

/**
 * A class that holds a range of Ports
 * so that we can see if an Port matches
 * a given address.
 */
public class PortRange
{
        /**
         * Tells if we are doing a negation rule.
         */
    boolean negate = false;

        /**
         * Port Ranges.
         * This contains 2 things:
         * The first two bytes of rangePorts[i] contains the
         * starting Port and the last 2 bytes contains
         * the ending Port.
         */
    public int portRanges[] = null;
    int nRanges = 0;

        /**
         * Individual Ports that can be matched.
         */
    public int ports[] = null;
    int nPorts = 0;

        /**
         * Constructor.
         */
    public PortRange()
    {
    }

        /**
         * Constructor.
         */
    public PortRange(String portRange)
    {
        parseRange(portRange);
    }

        /**
         * Parses an Port range given as a string.
         * Format of a range is:
         *
         * portranges ::= portRange "," portranges
         *          |   portRange
         *          ;
         *
         * portRange ::= Port
         *          | Port "-" Port
         */
    public void parseRange(String portRange)
    {
        portRange = portRange.trim();

            // check if its a negation rule
        negate = false;
        if (portRange.charAt(0) == '!')
        {
            negate = true;
            portRange = portRange.substring(1);
        }


        int startingPort, endingPort;
        StringTokenizer ranges = new StringTokenizer(portRange, ",", false);

        while (ranges.hasMoreTokens())
        {
            String currRange = ranges.nextToken().trim();

            int dashIndex = currRange.indexOf('-');

            if (dashIndex >= 0)
            {
                startingPort =
                    Integer.parseInt(currRange.substring(0, dashIndex));

                endingPort =
                    Integer.parseInt(currRange.substring(dashIndex + 1));

                addPortRange(startingPort, endingPort);
            } else
            {
                addPort(Integer.parseInt(currRange));
            }
        }
    }

        /**
         * Add an Port Range.
         */
    public void addPortRange(int firstPort, int lastPort)
    {
        if (portRanges == null) portRanges = new int[1];
        if (nRanges >= portRanges.length)
        {
            int range2[] = portRanges;
            portRanges = new int[nRanges + 2];
            System.arraycopy(range2, 0, portRanges, 0, nRanges);
        }
        portRanges[nRanges++] = ((firstPort & 0x0000ffff) << 16) |
                                 (lastPort & 0x0000ffff);
    }

        /**
         * Add an Port.
         */
    public void addPort(int port)
    {
        if (ports == null) ports = new int[1];
        if (nPorts >= ports.length)
        {
            int port2[] = ports;
            ports = new int[nPorts + 2];
            System.arraycopy(port2, 0, ports, 0, nPorts);
        }
        ports[nPorts++] = port & 0x0000ffff;
    }

        /**
         * See if the given address matches
         * any of the values in this class.
         */
    public boolean portMatches(int port)
    {
        for (int i = 0;i < nPorts;i++)
        {
            if (ports[i] == port) return negate ? false : true;
        }

        for (int i = 0;i < nRanges;i++)
        {
            if (port >= ((portRanges[i] >> 16) & 0x0000ffff) ||
                port <= (portRanges[i] & 0x0000ffff))
            {
                return negate ? false : true;
            }
        }
        return negate ? true : false;
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

        for (int i = 0;i < nPorts;i++)
        {
            if (canAddComas) out += (multiLine ? "\n    " : ", ");
            else canAddComas = true;
            out += Utils.ipToString(ports[i]);
        }

        for (int i = 0;i < nRanges;i++)
        {
            if (canAddComas) out += (multiLine ? "\n    " : ", ");
            else canAddComas = true;
            out += Utils.ipToString((int)((portRanges[i] >> 32) & 0xffffffff));
            out += "-";
            out += Utils.ipToString((int)(portRanges[i] & 0xffffffff));
        }

        if (negate) out = " ! " + out;
        return out;
    }
}
