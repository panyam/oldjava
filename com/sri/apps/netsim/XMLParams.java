package com.sri.apps.netsim;

import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
//import org.apache.xerces.parsers.*;
import javax.xml.parsers.*;

/**
 *  Some stuff for our network format.
 */
public class XMLParams
{

    public final static String NETWORK_ROOT_NAME = "network";
    public final static String DEVICES_ROOT_NAME = "devices";
    public final static String LINKS_ROOT_NAME = "links";

    public final static String DEVICE_TAG_NAME = "device";
    public final static String DEVICE_INTERFACES = "interfaces";
    public final static String SHARED_OBJS_ROOT_TAG = "shared_objs";
    public final static String PACKET_MODULES_ROOT_NAME = "modules";
    public final static String PACKET_MODULE_TAG = "module";

    public final static String IPT_SIZE = "ipt_size";
    public final static String IPT_ENTRY_TAG = "ip_entry";
    public final static String CLASS_PARAM = "class";
    public final static String DEVICE_INTERFACE_TAG_NAME = "interface";
    public final static String SHARED_OBJ_TAG = "shared_obj";
    public final static String ROUTER_DEFAULT_INT = "default";
    public final static String COUNT_PARAM = "count";
    public final static String INTERFACE_ID = "interface_id";
    public final static String INTERFACE_MAC = "mac";
    public final static String INTERFACE_IP = "ip";
    public final static String INTERFACE_IP_MASK = "mask";
    public final static String DEVICE_ID = "device_id";
    public final static String DEVICE_TYPE = "type";
    public final static String DEVICE_INTERFACE = "interface";
    public final static String DEVICE_NAME = "name";
    public final static String DEVICE_XPOS = "xpos";
    public final static String DEVICE_YPOS = "ypos";
    public final static String DEVICE_WIDTH  = "width";
    public final static String DEVICE_HEIGHT = "height";

    public final static String LINK_TAG_NAME = "link";
    public final static String LINK_STARTING_DEVICE = "start_device";
    public final static String LINK_ENDING_DEVICE = "end_device";
    public final static String LINK_STARTING_INTERFACE = "start_interface";
    public final static String LINK_ENDING_INTERFACE = "end_interface";

    public final static String PARAM_TAG_NAME = "param";

    public final static String ROUTING_TABLE_ROOT_NAME = "routing_table";
    public final static String ROUTE_TAG_NAME   = "route";
    public final static String ROUTE_DEST_NET = "dest";
    public final static String ROUTE_SUBNET = "mask";
    public final static String ROUTE_INTERFACE = "interface";
    public final static String ROUTE_TYPE = "type";

    public final static String RULE_SETS_NODE = "rulesets";
    public final static String RULE_SET_NODE_NAME = "ruleset";
    public final static String RULE_SET_NAME = "name";
    public final static String RULE_TAG_NAME   = "rule";
    public final static String RULE_ID  = "ruleID";
    public final static String RULE_MATCH_NODE = "match_criteria";
    public final static String RULE_ACTION_NODE = "action";
    public final static String RULE_PROTOCOL = "proto";
    public final static String RULE_ALLOW_ESTABLISHED = "established";
    public final static String RULE_SRC_IP = "src_ip";
    public final static String RULE_DEST_IP = "dest_ip";
    public final static String RULE_SRC_PORT = "sport";
    public final static String RULE_DEST_PORT = "dport";
    public final static String RULE_ACTION_TYPE = "type";
    public final static String RULE_NAT_IP = "nat_ip";
    public final static String RULE_NAT_PORT = "nat_port";

        /**
         * Creates a "<param>" tag.
         */
    public static Element createParamNode(Document doc,
                                          String name, String value)
    {
        Element param = doc.createElement(PARAM_TAG_NAME);
        param.setAttribute("name", name);
        param.setAttribute("value", value);
        return param;
    }

        /**
         * Reads an XML node Element and returns the appropriate XML
         * Object.
         */
    public static XMLObject readXMLObject(Element elem)
        throws Exception
    {
        String className = elem.getAttribute(CLASS_PARAM);

        Class objClass = Class.forName(className);
        XMLObject xmlObj = (SharedObject)objClass.newInstance();
        xmlObj.readXMLNode(elem);
        return xmlObj;
    }
}
