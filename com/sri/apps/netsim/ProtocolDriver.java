package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Super class of all Drivers that processe a packet.
 * Note that these are just interfaces.  Different driver implementations
 * can be written or extended from each other.
 *
 * So each driver knows about what implementations are written and so on.
 */
public interface ProtocolDriver extends NetworkEntity
{
        /**
         * Writes the headerLength and driverOffset in a packet at the
         * given level.  And this WILL be called with inbound packets and
         * only by the parent Device.  Any other use is illegal.
         */
    public void stripHeaders(Packet packet, int level);

        /**
         * This is one of the tricky functions.
         * When a ProtocolDriver needs to send out some data, 
         * it knows how much it needs to send.  Say this is l bytes.  It
         * asks the parent to return with "atleast" l bytes.  So this way
         * the parent ensure the packet is atleast l bytes and also bytes
         * for the header.
         *
         * More importantly, this function must be used by a child driver
         * because it doesnt know what "path" to take.  By path we mean the
         * path along the mesh of protocol drivers in the driver table.
         *
         * For example the TCP driver may know to use the IP driver and ask
         * the ip parent to format a packet.  But it is upto the IP driver
         * to choose which link layer driver it may want to use.  eg Token
         * Ring or Ethernet and so on.
         *
         * So the assumptions are that this driver has at least one valid
         * parent.
         *
         * This function MUST be called by one of the child drivers.  Eg if
         * a TCP driver needs a packet with 10 bytes, it would call
         * ipParent or the parentDriver.getPacket(X).
         *
         * Similarly if a TCP child is calling for X bytes, this function
         * would return X + h bytes where h is the size of the returning
         * TCP header.
         */
    public Packet getPacket(int length);

        /**
         * Sets the layer in which this driver is being used.
         */
    public void setLayer(int layer);

        /**
         * Returns the layer in which this driver is being used.
         */
    public int getLayer();

        /**
         * Add a protocol as a parent that can be used to
         * encapsulate these packets.
         */
    public void addParent(ProtocolDriver parent);

        /**
         * Processes an incoming packet before returning it to the parent.
         * If output is null then it means that the packet has been
         * consumed and it will not be availale to the device again.
         *
         * Returns the child packet driver that is capable of handling this
         * packet, after the headers are stripped.
         *
         * If no driver is returned, then packet doesnt traverse further
         * down.
         *
         * With this way of doing it, do we need a isPacketValid function?
         * No, because the parent will decide which child to call and so
         * on.  So how can more drivers be added?
         */
    public ProtocolDriver processInPacket(Packet packet,
                                          Device parent, 
                                          Network network);

        /**
         * Processes an outgoing packet before returning it to the parent.
         * If output is null then it means that the packet has been
         * consumed and it will not be availale to the device again.
         *
         * At this point, the packet.offset and packet.length refer to the
         * where the "child" layer's packet is encapsulated. 
         * So basically when the PacketDriver at the lowermost level writes
         * to a packet, it starts writing from a certain offset from a
         * packet rather than having to write at the start and have each
         * layer resize the packet and copy the payload each time.
         *
         * Basically this function should be called ONLY if a packet has
         * been called previously by a child driver object!!!  basically
         * doign pointer-y kind of stuff here.
         *
         * Unlike the processInPacket function this doesnt return the
         * "next" ProtocolDriver because these must already be defined from
         * the "getPacket()" function call.
         */
    public int processOutPacket(Packet packet, Device parent);
}
