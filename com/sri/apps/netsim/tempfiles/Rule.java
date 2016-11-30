package com.sri.apps.netsim; 


import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Defines a normal Rule.
 */
public class Rule //implements XMLObject
{
    public final static int TCP = 0;
    public final static int UDP = 1;
    public final static int ICMP = 2;

        /**
         * The ID of the rule.
         */
    int ruleID = 0;

        /**
         * Protocol type.
         * Must All, TCP, UDP or ICMP
         */
    int protocol = 0;

        /**
         * Tells whether we take only established
         * connections.
         */
    public boolean allowEstablished = false;

        /**
         * The source ip range.
         */
    public IPRange srcIPRange = null;

        /**
         * Destination IP Range.
         */
    public IPRange destIPRange = null;

        /**
         * Source Port range.
         */
    public PortRange srcPortRange = null;

        /**
         * Destination Port range.
         */
    public PortRange destPortRange = null;

        /**
         * The action for the rule.
         */
    public byte action = Action.ACCEPT;

        /**
         * IP To which nat occurs.
         * Used only for SNAT and DNAT.
         */
    public int natIP = 0;

        /**
         * The port to which the nat happens.
         * This is used only for DNAT.
         */
    public int natPort = -1;

        /**
         * Constructor.
         */
    public Rule()
    {
    }

        /**
         * Gives a string representation.
         */
    public String toString()
    {
        String out = "Rule " + ruleID + ": ";
        if (srcIPRange != null)
        {
            out += "Src IP: " + srcIPRange.toString();
        }

        if (srcPortRange != null)
        {
            out += "Src Port: " + srcPortRange.toString();
        }

        if (destIPRange != null)
        {
            out += "Dest IP: " + destIPRange.toString();
        }

        if (destPortRange != null)
        {
            out += "Dest Port: " + destPortRange.toString();
        }

        if (allowEstablished) out += "    established";
        return out;
    }

        /**
         * Prints out the route info.
         */
    public void print()
    {
        System.out.println(toString());
    }

        /**
         * Tells if a packet matches
         * this rule.
         */
    public boolean packetMatches(Packet packet)
    {
        int proto = IPUtils.readProtocol(packet);
        if (srcIPRange != null &&
            ! srcIPRange.ipMatches(IPUtils.readSrcIP(packet)))
        {
            return false;
        }

        if (destIPRange != null &&
            ! destIPRange.ipMatches(IPUtils.readDestIP(packet)))
        {
            return false;
        }

        if (srcPortRange != null)
        {
            if (proto == Protocols.UDP)
            {
                return srcPortRange.portMatches(UDPUtils.readSrcPort(packet));
            } else if (proto == Protocols.TCP)
            {
                return srcPortRange.portMatches(TCPUtils.readSrcPort(packet));
            } else
            {
                return false;
            }
        }

        if (destPortRange != null)
        {
            if (proto == Protocols.UDP)
            {
                return destPortRange.portMatches(UDPUtils.readDestPort(packet));
            } else if (proto == Protocols.TCP)
            {
                return destPortRange.portMatches(TCPUtils.readDestPort(packet));
            } else
            {
                return false;
            }
        }

        return true;
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc)
    {
        Element ruleNode = doc.createElement(XMLParams.RULE_TAG_NAME);

        ruleNode.setAttribute(XMLParams.RULE_ID, "" + ruleID);

        Element matchNode = doc.createElement(XMLParams.RULE_MATCH_NODE);
        Element actionNode = doc.createElement(XMLParams.RULE_ACTION_NODE);

        if (protocol > 0)
        {
            matchNode.setAttribute(XMLParams.RULE_PROTOCOL, "" + protocol);
        }
        if (allowEstablished)
        {
            matchNode.setAttribute(XMLParams.RULE_ALLOW_ESTABLISHED, "true");
        }

        if (srcIPRange != null)
        {
            matchNode.setAttribute(XMLParams.RULE_SRC_IP, srcIPRange.toString());
        }

        if (destIPRange != null)
        {
            matchNode.setAttribute(XMLParams.RULE_DEST_IP, destIPRange.toString());
        }

        if (srcPortRange != null)
        {
            matchNode.setAttribute(XMLParams.RULE_SRC_PORT, srcPortRange.toString());
        }

        if (destPortRange != null)
        {
            matchNode.setAttribute(XMLParams.RULE_DEST_PORT,
                                   destPortRange.toString());
        }

        actionNode.setAttribute(XMLParams.RULE_ACTION_TYPE,
                                Action.actionNames[action]);

        if (action == Action.SNAT || action == Action.DNAT)
        {
            actionNode.setAttribute(XMLParams.RULE_NAT_IP,
                                    Utils.ipToString(natIP));
            if (natPort >= 0)
            {
                actionNode.setAttribute(XMLParams.RULE_NAT_PORT,
                                        "" + natPort);
            }
        }

        ruleNode.appendChild(matchNode);
        ruleNode.appendChild(actionNode);
        return ruleNode;
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         */
    public void readXMLNode(Element element, Firewall fwall)
    {
        String val;
        this.ruleID =
            Integer.parseInt(element.getAttribute(XMLParams.RULE_ID));

        Element actionNode = 
            (Element)element.
                getElementsByTagName(XMLParams.RULE_ACTION_NODE).item(0);
        Element matchNode =
            (Element)element.
                getElementsByTagName(XMLParams.RULE_MATCH_NODE).item(0);


        srcIPRange = destIPRange = null;
        srcPortRange = destPortRange = null;

        val = matchNode.getAttribute(XMLParams.RULE_PROTOCOL);
        if (val != null) protocol = Integer.parseInt(val);

        val = matchNode.getAttribute(XMLParams.RULE_ALLOW_ESTABLISHED);
        if (val != null) allowEstablished = val.equalsIgnoreCase("true");

        val = matchNode.getAttribute(XMLParams.RULE_SRC_IP);
        if (val != null) srcIPRange = new IPRange(val);

        val = matchNode.getAttribute(XMLParams.RULE_DEST_IP);
        if (val != null) destIPRange = new IPRange(val);

        val = matchNode.getAttribute(XMLParams.RULE_SRC_PORT);
        if (val != null) srcPortRange = new PortRange(val);

        val = matchNode.getAttribute(XMLParams.RULE_DEST_PORT);
        if (val != null) destPortRange = new PortRange(val);


        action = Action.getActionType(
                actionNode.getAttribute(XMLParams.RULE_ACTION_TYPE));

        if (action == Action.SNAT || action == Action.DNAT)
        {
            natIP =
                Utils.stringToIP(
                        actionNode.getAttribute(XMLParams.RULE_NAT_IP));

            val = actionNode.getAttribute(XMLParams.RULE_NAT_PORT);

            natPort = -1;
            if (val != null) natPort = Integer.parseInt(val);
        }
    }
}

