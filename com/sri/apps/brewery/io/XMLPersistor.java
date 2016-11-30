package com.sri.apps.brewery.io;

/** Core Classses   **/
import java.util.*;
import java.io.*;

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
 * A persistor that writes all objects as XML files.
 *
 * The schema for this is:
 *
 * @author Sri Panyam
 */
public class XMLPersistor implements Persistor
{
        /**
         * All the tags used in the XML file format.
         */
    protected final static String ROOT_NODE = "Grinder";
    protected final static String PERSISTABLE_NODE = "Persistable";
    protected final static String ARRAY_NODE = "array";
    protected final static String COLLECTION_NODE = "collection";
    protected final static String OBJECT_NODE = "object";
    protected final static String VAR_TYPE = "type";

    protected final static String BOOL_NODE = "boolean";
    protected final static String BYTE_NODE = "byte";
    protected final static String CHAR_NODE = "char";
    protected final static String SHORT_NODE = "short";
    protected final static String INT_NODE = "int";
    protected final static String LONG_NODE = "long";
    protected final static String FLOAT_NODE = "float";
    protected final static String DOUBLE_NODE = "double";
    protected final static String STRING_NODE = "String";

    protected final static String BOOL_TYPE = "boolean";
    protected final static String BYTE_TYPE = "byte";
    protected final static String CHAR_TYPE = "char";
    protected final static String SHORT_TYPE = "short";
    protected final static String INT_TYPE = "int";
    protected final static String LONG_TYPE = "long";
    protected final static String FLOAT_TYPE = "float";
    protected final static String DOUBLE_TYPE = "double";
    protected final static String STRING_TYPE = "String";
    protected final static String PERSISTABLE_TYPE = PERSISTABLE_NODE;

    protected final static String ARRAY_TYPE_ATTR = "array_type";
    protected final static String TYPE_ATTR = "type";
    protected final static String SIZE_ATTR = "size";
    protected final static String CLASS_ATTR = "class";
    protected final static String NAME_ATTR = "name";
    protected final static String VALUE_ATTR = "value";
    protected final static String INDEX_ATTR = "index";
    protected final static String FIELD_NAME_ATTR = "fName";
    protected final static String FIELD_INDEX_ATTR = "fIndex";

        /**
         * Constructor.
         */
    public XMLPersistor()
    {
    }

        /**
         * Write a persistable object from stream.
         */
    public void writeToStream(Persistable obj,
                              OutputStream oStream) throws Exception
    {
            // create the root document...
        Document xmlDoc = new DocumentImpl();
        Element xmlDocRoot =
            xmlDoc.createElement(ROOT_NODE);
        xmlDoc.appendChild(xmlDocRoot);

            // build the DOM tree of the persistable obj...
        xmlDocRoot.appendChild(makePersistableNode(obj, xmlDoc));

            // now write the XMLDocument to the output stream.
        OutputFormat format
            = new OutputFormat(xmlDoc, "UTF-8", true);
        format.setLineSeparator("\n");
        format.setLineWidth(90);
        format.setIndent(2);
        //format.setPreserveSpace(true);
        try
        {
            XMLSerializer serial =
                    new XMLSerializer( oStream, format );
            serial.asDOMSerializer(); // As a DOM Serializer
            serial.serialize( xmlDoc.getDocumentElement() );
        } catch (Exception exc)
        {
        }
    }

        /**
         * Makes an XML Element out of a Persistable object.
         */
    public Element makePersistableNode(Persistable persObj, Document xmlDoc)
    {
        String className = persObj.getClass().toString();

            // create the element.
        Element out = xmlDoc.createElement(PERSISTABLE_NODE);
        out.setAttribute(CLASS_ATTR, className);

            // get field count...
        int nFields = persObj.fieldCount();

            // create nodes for each field...
        for (int i = 0;i < nFields;i++)
        {
            PersistableType fType = persObj.getFieldType(i);
            byte objType = fType.type;
            byte lType = fType.listType;
            Object fValue = persObj.getFieldValue(i);
            String fieldName = persObj.getFieldName(i);
            Element node = null;

                // object is a collection type, so
                // get the iterator and write each one...
            if (lType == PersistableType.COLLECTION)
            {
                node = makeCollectionNode((Collection)fValue, xmlDoc);
            } else if (lType == PersistableType.ARRAY)
            {
                int arrayLen = persObj.getFieldLength(i);
                node = makeArrayNode(fValue, fType, 0, arrayLen, xmlDoc);
            } else
            {
                node = makeObjectNode(fValue, xmlDoc);
            }

                    // set the field ID and Name attributes
                    // and append to the final element...
            node.setAttribute(FIELD_INDEX_ATTR, "" + i);
            node.setAttribute(FIELD_NAME_ATTR, fieldName);
            out.appendChild(node);
        }

        return out;
    }

        /**
         * Makes an xml node out of a given array object.
         */
    protected Element makeArrayNode(Object array, PersistableType objType,
                                    int offset, int len, Document xmlDoc)
    {
        Element out = xmlDoc.createElement(ARRAY_NODE);

        byte itemType = objType.type;
        int end = offset + len;
        StringBuffer arrayBuff = new StringBuffer();

        if (itemType == PersistableType.BOOL_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, BOOL_TYPE);
            boolean []anArray = (boolean [])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.BYTE_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, BYTE_TYPE);
            byte []anArray = (byte[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.CHAR_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, CHAR_TYPE);
            char []anArray = (char[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.SHORT_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, SHORT_TYPE);
            short []anArray = (short [])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.INT_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, INT_TYPE);
            int []anArray = (int[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.LONG_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, LONG_TYPE);
            long []anArray = (long[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.FLOAT_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, FLOAT_TYPE);
            float []anArray = (float[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.DOUBLE_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, DOUBLE_TYPE);
            double []anArray = (double[])array;
            for (int i = offset;i < end;i++) 
            {
                if (i > offset) arrayBuff.append(", ");
                arrayBuff.append(anArray[i]);
            }
            out.appendChild(xmlDoc.createTextNode(arrayBuff.toString()));
        } else if (itemType == PersistableType.STRING_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, STRING_TYPE);
            String []anArray = (String[])array;
            for (int i = offset;i < end;i++) 
            {
                Element childNode = xmlDoc.createElement(STRING_NODE);
                childNode.appendChild(xmlDoc.createTextNode(anArray[i]));
                out.appendChild(childNode);
            }
        } else if (itemType == PersistableType.PERSISTABLE_TYPE)
        {
            out.setAttribute(ARRAY_TYPE_ATTR, PERSISTABLE_TYPE);
            Persistable []anArray = (Persistable [])array;
            for (int i = offset;i < end;i++) 
            {
                out.appendChild(makePersistableNode(anArray[i], xmlDoc));
            }
        }

        return out;
    }

        /**
         * Makes an xml node out of a given collection object.
         */
    protected Element makeCollectionNode(Collection collection,
                                         Document xmlDoc)
    {
        Element out = xmlDoc.createElement(COLLECTION_NODE);

        Iterator iter = ((Collection)collection).iterator();

            // write the size...
        out.setAttribute(SIZE_ATTR, "" + ((Collection)collection).size());

            // write each item...
        while (iter.hasNext())
        {
            out.appendChild(makeObjectNode(iter.next(), xmlDoc));
        }

        return out;
    }

        /**
         * Makes an xml node out of a given object.
         */
    protected Element makeObjectNode(Object obj, Document xmlDoc)
    {
        if (obj instanceof String)
        {
            Element out = xmlDoc.createElement(STRING_NODE);
            out.appendChild(xmlDoc.createTextNode((String)obj));
            return out;
        } else if (obj instanceof Persistable)
        {
            return makePersistableNode((Persistable)obj, xmlDoc);
        }

        Element out = null;
        if (obj instanceof Boolean)
        {
            out = xmlDoc.createElement(BOOL_NODE);
        } else if (obj instanceof Byte)
        {
            out = xmlDoc.createElement(BYTE_NODE);
        } else if (obj instanceof Character)
        {
            out = xmlDoc.createElement(CHAR_NODE);
            System.err.println("Character storage unknown yet...");
        } else if (obj instanceof Short)
        {
            out = xmlDoc.createElement(SHORT_NODE);
        } else if (obj instanceof Integer)
        {
            out = xmlDoc.createElement(INT_NODE);
        } else if (obj instanceof Long)
        {
            
            out = xmlDoc.createElement(LONG_NODE);
        } else if (obj instanceof Float)
        {
            out = xmlDoc.createElement(FLOAT_NODE);
        } else if (obj instanceof Double)
        {
            out = xmlDoc.createElement(DOUBLE_NODE);
        }
        out.setAttribute(VALUE_ATTR, obj.toString());

        return out;
    }

        /**
         * Read a persistable object from stream.
         */
    public Persistable readFromStream(InputStream iStream) throws Exception
    {
        DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document xmlDoc = builder.parse(iStream);

        Element rootElem = xmlDoc.getDocumentElement();
        Element grinderElement = 
            (Element)(rootElem.getElementsByTagName(ROOT_NODE).item(0));

            // only one persistable object can be returned?
        NodeList persElems =
            grinderElement.getElementsByTagName(PERSISTABLE_NODE);

        for (int i = 0, l = persElems.getLength();i < l;i++)
        {
            Persistable persObj =
                readPersistableNode((Element)persElems.item(i));

                // what to do with this one object?  just return it?
            return persObj;
        }

            // now get this doc and strip things out of this...
        return null;
    }

        /**
         * REad the object within a node and return it.
         */
    protected Object readObjectNode(Element objNode)
        throws Exception
    {
        String nodeVal = objNode.getNodeValue();

        if (nodeVal.equalsIgnoreCase(PERSISTABLE_NODE))
        {
            return readPersistableNode(objNode);
        } else if (nodeVal.equalsIgnoreCase(STRING_NODE))
        {
            return objNode.getFirstChild().getNodeValue();
        }

        String valueAttr = objNode.getAttribute(VALUE_ATTR);
        if (nodeVal.equalsIgnoreCase(BOOL_NODE))
        {
            return Boolean.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(BYTE_NODE))
        {
            return Byte.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(CHAR_NODE))
        {
            System.err.println("Character storage unknown yet...");
            return null;//Character.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(SHORT_NODE))
        {
            return Short.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(INT_NODE))
        {
            return Integer.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(LONG_NODE))
        {
            return Long.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(FLOAT_NODE))
        {
            return Float.valueOf(valueAttr);
        } if (nodeVal.equalsIgnoreCase(DOUBLE_NODE))
        {
            return Double.valueOf(valueAttr);
        }
        return null;
    }

        /**
         * Read a DOM Element node and construct a Persistable object from
         * it and return it.
         */
    protected Persistable readPersistableNode(Element node)
        throws Exception
    {
        String className = node.getAttribute(CLASS_ATTR);
        Persistable persObj =
                (Persistable)Class.forName(className).newInstance();

        int nFields = persObj.fieldCount();
        NodeList kids = node.getChildNodes();
        int nKids = kids.getLength();
        int currField = 0;
        
            // read each field...
        for (int i = 0;i < nFields;i++)
        {
            if ( ! (kids.item(i) instanceof Element))
            {
                continue;
            }
            Element kid = (Element)kids.item(i);

            PersistableType fType = persObj.getFieldType(currField);
            byte objType = fType.type;
            byte lType = fType.listType;
            Object fValue = null;

                // object is a collection type, so
                // get the iterator and write each one...
            if (lType == PersistableType.COLLECTION)
            {
                fValue = readCollectionNode(kid);
            } else if (lType == PersistableType.ARRAY)
            {
                fValue = readArrayNode(kid);
            } else
            {
                fValue = readObjectNode(kid);
            }
            persObj.setFieldValue(currField, fValue);

            currField++;
        }
        return persObj;
    }

        /**
         * Read a collection of persistable objects from a node.
         * This is similar to the way the array is read..
         */
    protected Object readArrayNode(Element arrNode)
        throws Exception
    {
        int length = Integer.parseInt(arrNode.getAttribute(SIZE_ATTR));
        String arrayType = arrNode.getAttribute(ARRAY_TYPE_ATTR);

        if (arrayType.equalsIgnoreCase(STRING_TYPE))
        {
            String []anArray = new String[length];
            NodeList strings =
                arrNode.getElementsByTagName(STRING_NODE);
                // check that nElems == length

            for (int i = 0;i < length;i++)
            {
                anArray[i] = strings.item(i).getFirstChild().getNodeValue();
                System.out.println("TNValue: -----\n" + anArray[i]);
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(PERSISTABLE_TYPE))
        {
            NodeList persElems =
                arrNode.getElementsByTagName(PERSISTABLE_NODE);
            int nElems = persElems.getLength();

                // check that nElems == length
            Persistable []anArray = new Persistable[length];

            for (int i = 0;i < length;i++)
            {
                anArray[i] = readPersistableNode((Element)persElems.item(i));
            }
            return anArray;
        }

            // other wise the child will be a coma (and/or space limited)
            // bunch of numbers or bools or what ever...
        Node child = arrNode.getFirstChild();
        String chValue = child.getNodeValue();
        System.out.println("Child Value: \n" + chValue);
        StringTokenizer tokens =
            new StringTokenizer(chValue, " \t\n\r,", false);
        int nTokens = tokens.countTokens();
        int i = 0;

        if (arrayType.equalsIgnoreCase(BOOL_TYPE))
        {
            boolean anArray[] = new boolean[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Boolean.
                            valueOf(tokens.nextToken()).booleanValue();
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(BYTE_TYPE))
        {
            byte anArray[] = new byte[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Byte.parseByte(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(CHAR_TYPE))
        {
            char anArray[] = new char[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = (char)Integer.parseInt(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(SHORT_TYPE))
        {
            short anArray[] = new short[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Short.parseShort(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(INT_TYPE))
        {
            int anArray[] = new int[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Integer.parseInt(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(LONG_TYPE))
        {
            long anArray[] = new long[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Long.parseLong(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(FLOAT_TYPE))
        {
            float anArray[] = new float[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Float.parseFloat(tokens.nextToken());
            }
            return anArray;
        } else if (arrayType.equalsIgnoreCase(DOUBLE_TYPE))
        {
            double anArray[] = new double[nTokens];
            while (tokens.hasMoreTokens())
            {
                anArray[i++] = Double.parseDouble(tokens.nextToken());
            }
            return anArray;
        }

        return null;
    }

        /**
         * Read a collection of persistable objects from a node.
         * This is similar to the way the array is read..
         */
    protected Collection readCollectionNode(Element collNode)
        throws Exception
    {
        List collection = new LinkedList();

        int nItems = Integer.parseInt(collNode.getAttribute(SIZE_ATTR));

        NodeList kids = collNode.getChildNodes();
        int nKids = kids.getLength();
        int currChild = 0;

        for (int i = 0;i < nKids;i++)
        {
            if (! (kids.item(i) instanceof Element))
            {
                continue;
            }
                // get the child node
            Element kid = (Element)kids.item(i);

                // add it to the collection...
            collection.add(readObjectNode(kid));
            currChild++;
        }

        return collection;
    }
}
