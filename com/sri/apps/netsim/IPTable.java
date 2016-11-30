
package com.sri.apps.netsim;
import com.sri.utils.*;
import com.sri.utils.adt.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A list of IPs for each network card.
 * Basically doing it this way so that we wont have to have
 * a router interface and so on.
 */
public class IPTable implements SharedObject
{
    int ipAddress[];
    byte subnetMask[];

    public IPTable(int nInts)
    {
        setSize(nInts);
    }

        /**
         * Set the size.
         */
    public void setSize(int nInts)
    {
        if (nInts != ipAddress.length)
        {
            ipAddress = new int[nInts];
            subnetMask = new byte[nInts];
        }
    }

        /**
         * Reads an XML Node.
         * Node is in the form:
         *
         * <shared_obj class="com.sri.apps.netsim.IPTable" size="num interfaces">
         *  <ip_entry ip="ipaddress" mask="mask"/>
         * </shared_obj>
         */
    public void readXMLNode(Element elem) throws Exception
    {
        int iptSize = Integer.parseInt(elem.getAttribute(XMLParams.IPT_SIZE));
        setSize(iptSize);

        int ip, id;
        byte mask;

        NodeList iptNodes =
            elem.getElementsByTagName(XMLParams.IPT_ENTRY_TAG);

        for (int i = 0;i < iptSize;i++)
        {
            Element iptNode = (Element)iptNodes.item(i);
            id = Integer.parseInt(iptNode.getAttribute(XMLParams.INTERFACE_ID));
            ip = Utils.stringToIP(iptNode.getAttribute(XMLParams.INTERFACE_IP));
            mask = (byte)Integer.parseInt(iptNode.getAttribute(XMLParams.INTERFACE_IP_MASK));

            ipAddress[id] = ip;
            subnetMask[id] = mask;
        }
    }

        /**
         * Creates an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element iptNode = doc.createElement(XMLParams.SHARED_OBJ_TAG);

        iptNode.setAttribute(XMLParams.CLASS_PARAM , "" + getClass());
        iptNode.setAttribute(XMLParams.IPT_SIZE, "" + ipAddress.length);

        for (int i = 0;i < ipAddress.length;i++)
        {
            Element iptEntry = doc.createElement(XMLParams.IPT_ENTRY_TAG);
            iptEntry.setAttribute(XMLParams.INTERFACE_ID, "" + i);
            iptEntry.setAttribute(XMLParams.INTERFACE_IP, "" +
                                        Utils.ipToString(ipAddress[i]));
            iptEntry.setAttribute(XMLParams.INTERFACE_IP_MASK, "" + subnetMask[i]);
            iptNode.appendChild(iptEntry);
        }
        return iptNode;
    }
}
