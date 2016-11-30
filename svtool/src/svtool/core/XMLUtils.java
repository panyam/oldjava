package svtool.core;

import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
//import org.apache.xerces.parsers.*;
import javax.xml.parsers.*;

/**
 *  Some stuff for writing to an XML File.
 */
public class XMLUtils
{
    public final static String CONFIGS_ROOT_NAME = "svtool";
    public final static String CONNECTIONS_ROOT_NAME = "connections";
    public final static String QUERIES_ROOT_NAME = "querytable";
    public final static String CONNECTION_ROOT_NAME = "connection";
    public final static String QUERY_ROOT_NAME = "query";
    public final static String LAF_ROOT_NODE = "laf_list";
    public final static String LAF_NODE = "laf";
    public final static String DBVIEW_ROOT_NODE = "dbviews";
    public final static String DBVIEW_NODE = "dbview";
    public final static String ICON_ROOT_NODE = "icons";
    public final static String ICON_NODE = "icon";

    public final static String SERVICES_ROOT_NAME = "services";
    public final static String ATTRIBUTES_ROOT_NAME = "attributes";
    public final static String FNN_ROOT_NAME = "fnn";
    public final static String SERVICE_ROOT_NAME = "service";
    public final static String ATTRIBUTE_ROOT_NAME = "attribute";

    public final static String CUSTOMER_ID_ATTRIB = "cust_id";
    public final static String CUSTOMER_NAME_ATTRIB = "cust_name";


    public final static String ID_ATTRIB = "id";
    public final static String NAME_ATTRIB = "name";
    public final static String ICON_ATTRIB = "icon";
    public final static String URI_ATTRIB = "uri";
    public final static String CLASS_ATTRIB = "class";
    public final static String JAR_ATTRIB = "jar";
    public final static String USERID_ATTRIB = "userid";
    public final static String SID_ATTRIB = "sid";
    public final static String PASSWORD_ATTRIB = "password";
    public final static String VALUE_ATTRIB = "value";
    public final static String ATTRIB_CODE_ATTRIB = "code";
    public final static String ATTRIB_NAME_ATTRIB = "name";
    public final static String ATTRIB_VALUE_ATTRIB = "value";


    /*public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";
    public final static String _ROOT_NAME = "network";*/

    public final static String _TAG_NAME = "network";

    public final static String PARAM_TAG_NAME = "param";

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
    /*public static XMLObject readXMLObject(Element elem)
        throws Exception
    {
        String className = elem.getAttribute(CLASS_PARAM);

        Class objClass = Class.forName(className);
        XMLObject xmlObj = (SharedObject)objClass.newInstance();
        xmlObj.readXMLNode(elem);
        return xmlObj;
    }*/

    public static int compareStrings(String a, String b)
    {
        if (a == null)
        {
            if (b == null) return 0;
            else return -1;
        } else if (b == null) return 1;
        return a.compareTo(b);
    }
}
