package com.sri.apps.netsim;

import java.io.*;
import java.awt.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A Firewall class.
 * Used as a fireweall router.
 * we extend a router because a Stateful-Inspecting-Firwall-Router is
 * basically a layer 3 device that handles packets based on rules
 * on top of basic routing...
 *
 * Now to have smart ways of having firewall rules...
 *
 * Here are the features:
 *  1) Packet Filtering:
 *      Matching packets based on certain fields.
 *
 *  2) NATing
 *      Source NAT and Destination NAT
 *
 *  3) Connection Tracking
 *      Source NAT and Destination NAT
 *
 *  Need packetclasses - to avoid multiple checks on packets.
 *  Basically what chains are used in tables.
 *
 *  3 Situations:
 *  Input,
 *  Output and
 *  Forwarding
 *
 *  NAT and packet filtering are simple as they are basically
 *  packetfiltering with actions on top of it.
 *
 *  Connection Tracking is the issue. How do we make a packet be associated
 *  with a connection?  We can have a connection table as well.  So each
 *  connection tracking module can take a packet and decide what to do
 *  with it.  Or do we just "accept" the packet and let the filteringmodule
 *  take care of it.
 *
 *  we can have packet classes and also connection classes.  For example we
 *  could have IP connections.   IP is connectionless but connection
 *  implies packets seen in both ways or according to any other criteria
 *  that is described by the module.
 *
 *  So for example, we could have an IP connection module.  This could send
 *  the packet to further modules depending on the data in the packet 
 *  and whats held in the state tables in the firewall.  So one good thing
 *  about this is that when we come down to a certain modules, all its
 *  "super" modules have been tested for.  So issues are:
 *
 *      What is the architecture?
 *      What is the rule set/language for describing how packets should be
 *      dealt with?
 *
 *  How does IP tables deal with connections?  For example, the first
 *  packet is allowed and then how do the connection tracking and NAT
 *  modules take over?  The whole Established/New/Invalid is just a
 *  specialised case of connection tracking.  We cant just send a packet to
 *  every damn module but we could have modules for 1000 different
 *  protocols.  Whats worse is we could only be expecting packets of 5
 *  protocols..  We shouldnt be sending this to everyone of this.  How
 *  does the ftp connection tracking module do it?  Its a basic example.
 *
 *  How about this:
 *      A packet that comes in will be in the table if its accepted.
 *      Assuming we know what to allocate the "first" packet to.  Worry
 *      about this a bit later.  So the first packet that is accepted (for
 *      now we only worry about IP packets.  We will add more packets later
 *      on.) will be put in our table and given the name of the module that
 *      is associated with it.  Note that for this to happen there HAS to
 *      be an "ACCEPT" rule.  A packet that is dropped wont get into that
 *      table.
 *
 *      First issue is after a packet gets accepted because of a rule (eg
 *      accept mac = xx and so and so), which module to we give it to (if
 *      any at all).
 *
 *      Second issue, how would the module control which future packets are
 *      associated with this connection.  A connection is associated with
 *      the source and destination IP address.  So the good thing would
 *      be that we can associate a src,dest ip address pair as a key for
 *      identify which packet belongs to which connection.  So each module
 *      will get a packet with the given source IP and dest IP. So given a
 *      IP packet, we can associate it with a connection tracking module.
 *      However, what happens when you have multiple packets with same
 *      source and dest IP, but with different ports or other connection
 *      specific identifiers?  This basically means you have packets from
 *      the same source to the same hosts but are on different sessions.
 *      So do we let one module take care of all sessions or have multiple
 *      instances of the module? What are the pros and cons?  With having
 *      one instance but the module keeping track of multiple sessions its
 *      good because, the connection specific module can take care of how
 *      it handles it.  The badpart is how will the kernel know how to
 *      handle session specific paramaters of the protocol?
 *
 *      Back to the first issue.  After a packet is accepted, if its not
 *      already in a connection (associated by a SRC/DEST IP pair) how do
 *      we associated it with a specific connection tracking module.  For
 *      every accept rule, we could associate it with a connection tracking
 *      module.  Would this be good?  The thing with a bruteforce "auto
 *      detect" would be that if its a "match" then only the first packet
 *      needs to be matched to find the appropriate connection tracking
 *      module.  But the trouble is every unmatched module will also be
 *      searched and this is inefficient.  So better to have an "explicit"
 *      association of a rule with the module.  By default all packets will
 *      be associated with the IP module.  So TCPConnTrack would extend IP
 *      conntrack and FTP conntrack would extend TCPConnTrack.
 *
 *      Third issue is what are the problems associated with combining NAT
 *      with connection tracking?  Need to find out.
 *
 *  First deal with packet filtering and NAT
 *
 *  matchOptions action
 */
public class Firewall extends Device
{
    ConnectionTable connTable = new ConnectionTable();

        /**
         * The rulesets that we will be using.
         */
    protected Ruleset filterRules = new Ruleset();
    protected Ruleset snatRules = new Ruleset();
    protected Ruleset dnatRules = new Ruleset();

        /**
         * Constructor.
         */
    public Firewall(String name, int nInterfaces)
    {
        setName(name);
        ensureInterfaceCapacity(nInterfaces);
        IPTable ipTable = new IPTable(nInterfaces);
        addSharedObject(ipTable);
        //routingModule = new RoutingModule(this, ipTable);
    }

        /**
         * Gives a string rep.
         */
    public String toString()
    {
        return "Firewall: " + name;
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element device = super.getXMLNode(doc);
        device.setAttribute(XMLParams.DEVICE_TYPE, "firewall");

        Element ruleSetNode = doc.createElement(XMLParams.RULE_SETS_NODE);
        Element snatRuleNode = snatRules.getXMLNode(doc, "snat");
        Element dnatRuleNode = dnatRules.getXMLNode(doc, "dnat");
        Element filterRuleNode = filterRules.getXMLNode(doc, "filter");

        ruleSetNode.appendChild(dnatRuleNode);
        ruleSetNode.appendChild(filterRuleNode);
        ruleSetNode.appendChild(snatRuleNode);

        device.appendChild(ruleSetNode);

        return device;
    }

        /**
         * Process an XML node.
         */
    /*public void readXMLNode(Element deviceElement)
    {
        super.processXMLNode(deviceElement);

        Element ruleSetNode = (Element)deviceElement.getElementsByTagName(XMLParams.RULE_SETS_NODE).item(0);

        NodeList ruleSets = deviceElement.getElementsByTagName(XMLParams.RULE_SET_NODE_NAME);
		
		if (filterRules == null) filterRules = new Ruleset();
		if (snatRules == null) snatRules= new Ruleset();
		if (dnatRules == null) dnatRules= new Ruleset();

        for (int i = 0;i < 3;i++)
        {
            Element el = (Element)ruleSets.item(i);
            String name = el.getAttribute(XMLParams.RULE_SET_NAME);
            if (name.equalsIgnoreCase("snat"))
            {
                snatRules.processXMLNode(el, this);
            } else if (name.equalsIgnoreCase("dnat"))
            {
                dnatRules.processXMLNode(el, this);
            } else if (name.equalsIgnoreCase("filter"))
            {
                filterRules.processXMLNode(el, this);
            }
        }
    }*/

        /**
         * Draws this Link.
         */
    public void paint(Graphics g, int xOffset, int yOffset)
    {
        super.paint(g, xOffset, yOffset);


        int tx = x + xOffset;
        int ty = y + yOffset;


            // if the shape isnt visible why draw it?
        if (tx + width >= 0 && ty + height >= 0) 
        {
            FontMetrics fm = g.getFontMetrics();
            int sw = fm.stringWidth("FW");
            int sh = fm.getAscent();
            g.setColor(Color.black);
            g.fillRect(tx, ty, sw + 6, sh + 6);
            g.setColor(Color.white);
            g.drawString("FW", tx + 3, ty + sh + 3);
        }
    }

        /**
         * Write a packet down a given interface.
         * This method should be called when writing out
         * an interface.  This can be overridden by 
         * the child devices based on the device itself.
         */
    protected void writeToInterface(NetworkInterface nic, Packet packet)
        throws Exception
    {
            // before the packet is written, we check it through the
            // SNAT rules and see what needs to be done...
        processSNATRules(packet);

        //super.writeToInterface(nic, packet);
    }

        /**
         * Pass the packet through a whole bunch of rules
         * and see if the packet needs to drop or not.
         */
    protected Rule processFilteringRules(Packet packet)
    {
        int nRules = filterRules.nRules;

        for (int i = 0;i < nRules;i++)
        {
            Rule rule = filterRules.rules[i];

            if (rule.packetMatches(packet)) return rule;
        }

            // what is the default behaviour???
        return null; //filterRules.defaultRule;
    }

        /**
         * Process the next packet.
         */
    protected void processPacket(Packet packet)
        throws Exception
    {
/*            // basically here we go through the rules
            // and see what needs to be done...
            // First we go through the DNAT rules and 
            // see if any NAT is to be done on the packet
        processDNATRules(packet);

            // see which rule matches in the filter...
        Rule matchedRule = processFilteringRules(packet);

        int proto = IPUtils.readProtocol(packet);
        int srcIP = IPUtils.readSrcIP(packet);
        int destIP = IPUtils.readDestIP(packet);
        int srcPort = 0;
        int destPort = 0;

        int action = Action.ACCEPT;

        if (proto == Protocols.TCP)
        {
            srcPort = TCPUtils.readSrcPort(packet);
            destPort = TCPUtils.readDestPort(packet);
        } else
        {
            srcPort = UDPUtils.readSrcPort(packet);
            destPort = UDPUtils.readDestPort(packet);
        }

            // if no rule matched
        if (matchedRule == null)
        {
            Connection whichConn =
                connTable.processPacket(packet,
                                        srcIP, srcPort, destIP, destPort);

                // if packet is not part of a connection
                // then follow the default action...
            if (whichConn == null)
            {
                action = filterRules.defaultRule.action;
            }
        } else if (matchedRule.action == Action.ACCEPT)
        {
            if (matchedRule.allowEstablished)
            {
                Connection whichConn =
                    connTable.processPacket(packet,
                                            srcIP, srcPort,
                                            destIP, destPort);

                if (whichConn == null)
                {
                        // see if we can add one?
                    whichConn = connTable.createConnection(packet, proto,
                                                           srcIP, srcPort,
                                                           destIP, destPort);
                }

                if (whichConn == null)
                {
                    action = Action.DROP;
                }
            }
        }

        if (action == Action.ACCEPT)
        {
            super.processPacket(packet);
        } else if (action == Action.DROP)
        {
            String out = "    Src: " +
                           Utils.ipToString(srcIP) + " (" + srcIP + ")\n" +
                         "    Dst: " +
                           Utils.ipToString(destIP) + " (" + destIP + ")\n";

            if (srcPort != 0) out += "    Source Port: " + srcPort + "\n";
            if (destPort != 0) out += "    Destination Port: " + destPort + "\n";
            writeToConsole(DeviceEvent.PACKET_DROPPED, out);
        }*/
    }

        /**
         *  Does source NATing.
         */
    protected void processSNATRules(Packet packet)
    {
        int ri = 0;

        for (;
             ri < snatRules.nRules &&
             ! snatRules.rules[ri].packetMatches(packet); ri++);

            // if no rule matches then just return and do nothing...
        if (ri >= snatRules.nRules) return ;

            // change the source ip...
            // The trouble is in changing the source port
            // as we need to maintain a table of ports
            // that are being currently changed!!!
            // SO a source port is identified by
            // Source IP (before change),
            // Source Port (before change)
            // Destination IP and Destination Port
        /*IPUtils.writeDestIP(packet, dnatRules.rules[ri].natIP);

        int proto = IPUtils.readProtocol(packet);
            // change the dest Port if necessary
        if (dnatRules.rules[ri].natPort >= 0)
        {
            if (proto == Protocols.TCP)
            {
                TCPUtils.writeDestPort(packet, dnatRules.rules[ri].natPort);
            } else if (proto == Protocols.UDP)
            {
                UDPUtils.writeDestPort(packet, dnatRules.rules[ri].natPort);
            }
        }*/
        
    }

        /**
         * Get the packet through a series of 
         * DNAT rules and see which one matches the
         * packet.
         */
    protected void processDNATRules(Packet packet)
    {
        int ri = 0;

        for (;
             ri < dnatRules.nRules &&
             ! dnatRules.rules[ri].packetMatches(packet); ri++);

            // if no rule matches then just return and do nothing...
        if (ri >= dnatRules.nRules) return ;

            // change the destination ip...
        IPUtils.writeDestIP(packet, dnatRules.rules[ri].natIP);

        int proto = IPUtils.readProtocol(packet);
            // change the dest Port if necessary
        if (dnatRules.rules[ri].natPort >= 0)
        {
            if (proto == Protocols.TCP)
            {
                TCPUtils.writeDestPort(packet, dnatRules.rules[ri].natPort);
            } else if (proto == Protocols.UDP)
            {
                UDPUtils.writeDestPort(packet, dnatRules.rules[ri].natPort);
            }
        }
    }

        /**
         * Gets the ruleset.
         */
    public Ruleset getFilterRuleset()
    {
        return filterRules;
    }

        /**
         * Gets the ruleset.
         */
    public Ruleset getSNATRuleset()
    {
        return snatRules;
    }

        /**
         * Gets the ruleset.
         */
    public Ruleset getDNATRuleset()
    {
        return dnatRules;
    }

        /**
         * Enables or disables an interface.
         */
    public void enableInterface(NetworkInterface nic, boolean enable)
    {
    }
}
