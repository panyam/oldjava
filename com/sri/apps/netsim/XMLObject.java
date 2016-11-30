package com.sri.apps.netsim;

import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;
import com.sri.utils.*;
import com.sri.utils.adt.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Interface for objects that can be read and written
 * as XML data.
 */
public interface XMLObject 
{

        /**
         * Reads an XML Node.
         */
    public abstract void readXMLNode(Element elem) throws Exception;

        /**
         * Creates an XML node.
         */
    public abstract Element getXMLNode(Document doc) throws Exception;
}
