
package com.sri.apps.netsim;

import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import javax.xml.parsers.*;

/**
 *  Some stuff for our network format.
 */
public class NetworkFormat
{
    public final static String DEVICES_ROOT_NAME = "devices";
    public final static String LINKS_ROOT_NAME = "links";

    public final static String DEVICE_TAG_NAME = "device";
    public final static String LINK_TAG_NAME = "link";
}
