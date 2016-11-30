package svtool.core;

/***    XML Classes     ***/
import  org.w3c.dom.*;
import  org.apache.xerces.dom.DocumentImpl;
import  org.apache.xerces.dom.DOMImplementationImpl;
import  org.w3c.dom.Document;
import  org.apache.xml.serialize.OutputFormat;
import  org.apache.xml.serialize.Serializer;
import  org.apache.xml.serialize.SerializerFactory;
import  org.apache.xml.serialize.XMLSerializer;
import javax.xml.parsers.*;


/**
 * Parent interface of all objects that can read and write configurations
 * from an XML document.
 *
 * @author      Sri Panyam
 */
public interface Configurable
{
        /**
         * Reads the configuration from a Document object.
         *
         * @param   configDoc   The XML Document object which holds the
         *                      configuration information.
         *
         * @author  Sri Panyam
         */
    public void readConfigurations(Document  configDoc);

        /**
         * Creates an XML Element Node encapsulating the information held
         * within this object.
         *
         * @param   doc     The parent XML document to which this node will
         *                  belong to.
         * @return  Returns the element node that holds the configuration
         *          information.
         *
         * @author  Sri Panyam
         */
    public Element getConfigurations(Document doc);
}
