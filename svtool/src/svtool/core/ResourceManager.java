package svtool.core;

import java.io.*;
import javax.swing.*;

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
 * Manages access to files and resources and relieves individual classes
 * the burden of having to open the files (from disk or network)
 * themselves.
 *
 * @author      Sri Panyam
 */
public interface ResourceManager
{
        /**
         * Opens a file and returns it as an input stream.
         *
         * @param   fileName    The file to open.
         * @return  The inputstream from the file.
         *
         * @author  Sri Panyam
         */
    public InputStream  readFile(String fileName) throws Exception;

        /**
         * Opens an image file and returns the ImageIcon.
         *
         * @param   imageFileName   The image file to open.
         * @return  The ImageIcon stored in the file.
         */
    public ImageIcon getImageIcon(String imageFileName);
}
