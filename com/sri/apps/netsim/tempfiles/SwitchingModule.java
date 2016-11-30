

package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A Switching module.
 * When attached to a device, takes care of the Switching of packets.
 */
public class SwitchingModule extends PacketModule
{
        // the size of the bridging table
    int btSize = 0;
    long macs[] = new long[128];
    short intIDs[] = new short[128];

        /**
         * Constructor.
         */
    protected SwitchingModule(Device dev)
    {
        super(dev);
    }

        /**
         * Process an outgoing packet.
         */
    public boolean processOutPacket(Packet packet) throws Exception
    {
        return processInPacket(packet);
    }

        /**
         * Process an incoming packet.
         */
    public boolean processInPacket(Packet packet) throws Exception
    {
            // we do all this only if the NIC is unknown.
            // For example, we may already have determined 
            // in an earlier stage of the packet module chain
            // that this packet should be sent out of a certain
            // interface, in which case no switching should be 
            // done on the packet.
        if (packet.outNic != Packet.UNKNOWN_NIC) return true;

            // get the source and destination mac addresses of the packet
        long srcMAC = EthernetUtils.readSrcMac(packet);
        long destMAC = EthernetUtils.readDestMac(packet);

            // Note that we are doing a very basic version of bridging
            // here.  No VLANs and no Spanning Tree protocols.  So
            // the only constraint is that you dont put two switches
            // in the same "loop"

            // Step 1: See if the source mac is in the 
            //         bridging table... if not 
            //         add it in.
        int intIndex = -1;
        if (packet.inNic >= 0)
        {
            intIndex = findInterface(srcMAC);
            if (intIndex < 0)
            {
                if (macs.length == btSize)
                {
                    long mac2[] = macs;
                    short ints[] = intIDs;
                    macs = new long[btSize * 3 >> 1];
                    intIDs = new short[btSize * 3 >> 1];
                    System.arraycopy(ints, 0, intIDs, 0, btSize);
                    System.arraycopy(mac2, 0, macs, 0, btSize);
                }
                macs[btSize] = srcMAC;
                intIDs[btSize] = packet.inNic;
                intIndex = btSize;
                btSize++;
            }
        }

            // Step 2: Now as to what do with the packet, see if there is 
            //    an entry in the bridging table for the destMac
            //    if there isnt then send the packet out on all the 
            //    interfaces except the one where the packet arrived
        int destInt = findInterface(destMAC);
        if (destInt < 0)
        {
                // flood the packet on all interfaces
                // except to the interface from which 
                // the packet was received
            parent.writeToConsole(DeviceEvent.PACKET_FLOODED, null);
                // if (i != nic.interfaceID && i != intIDs[intIndex])
            packet.outNic = Packet.ALL_NICS;
        } else
        {
            parent.writeToConsole(DeviceEvent.PACKET_FORWARDED,
                            "Interface: " + destInt);
            packet.outNic = intIDs[destInt];
        }
        packet.packetModule = this;
            // also tell the parent to "resend" this packet
        parent.transmitPacket(packet);
        return false;
    }

        /**
         * Given an interface, finds the interface on which the
         * mac address can be reached.
         */
    protected int findInterface(long mac)
    {
        for (int i = 0;i < btSize;i++)
        {
            if (macs[i] == mac) return i;
        }
        return -1;
    }

        /**
         * Reads an XML Node.
         */
    public void readXMLNode(Element elem) throws Exception
    {
    }

        /**
         * Creates an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element moduleElement = 
            doc.createElement(XMLParams.PACKET_MODULE_TAG);

        moduleElement.setAttribute(XMLParams.CLASS_PARAM, "" + getClass());
        return moduleElement;
    }
}
