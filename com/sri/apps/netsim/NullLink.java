package com.sri.apps.netsim;

/**
 *  A NULL Link.
 *  Basically packets written to this link dont go anywhere.
 *  Its like a blackhole of packets.
 */
public class NullLink extends Link
{
    public final static Link DEFAULT_LINK = new NullLink();
        /**
         * Constructor.
         */
    public NullLink()
    {
    }

        /**
         * Indicates that a packet is being written onto this
         * link by the source element.
         */
    public void writePacket(Packet packet, NetworkInterface source)
        throws Exception
    {
        // we do noting here..
    }
}
