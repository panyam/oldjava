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
 * Superclass of network device objects.
 * For example, routers, switches, hosts, firewalls etc.
 */
public abstract class Device extends BoundedShape
                             implements NetworkEntity, XMLObject
{
        /**
         * The console for the device.
         */
    private Console deviceConsole = null;

        /**
         * Name of this device.
         */
    protected String name = "";

        /**
         * Keeps track of how many devices we have in the system.
         */
    protected static int DEVICE_COUNT = 0;

        /**
         * The links that are affected as a result of this box
         * being moved.
         */
    protected Vector affectedLinks = new Vector();

        /**
         * A unique id per device.
         */
    public int device_id = DEVICE_COUNT++;

        /**
         * The mac address of this device.
         */
    public long macAddress = 0;

        /**
         * The interfaces.
         */
    public NetworkInterface interfaces[] = null;

        /**
         * Number of interfaces in here.
         */
    public int nInterfaces = 0;

        /**
         * A list of packet drivers.
         */
    protected DriverTable driverTable = new DriverTable();

        /**
         * List of "extra" objects that would belong to this device.
         * Example: Routing Tables, IP Tables and etc.
         */
    protected SharedObject sharedObjs[] = null;
    int nSharedObjects = 0;

        /**
         * Constructor.
         */
    protected Device() { }

        /**
         * Constructor.
         */
    protected Device(long macAddress)
    {
        setMacAddress(macAddress);
    }

        /**
         * Add a new shared object.
         */
    public synchronized void addSharedObject(SharedObject obj)
    {
        if (sharedObjs == null) sharedObjs = new SharedObject[1];
        if (sharedObjs.length <= nSharedObjects)
        {
            SharedObject ob2[] = sharedObjs;
            sharedObjs = new SharedObject[nSharedObjects + 1];
            System.arraycopy(ob2, 0, sharedObjs, 0, nSharedObjects);
            ob2 = null;
        }
        sharedObjs[nSharedObjects++] = obj;
    }

        /**
         * Finds a shared object of a certain class type.
         * If one isnt found then returns null.
         */
    protected SharedObject getSharedObject(Class objClass, boolean add) 
    {
        for (int i = 0;i < nSharedObjects;i ++)
        {
            if (sharedObjs[i].getClass() ==  objClass) 
            {
                return sharedObjs[i];
            }
        }

            // if not available then add a new instance of the
            // shared object, if asked for...
        if (add)
        {
            try
            {
                addSharedObject((SharedObject)objClass.newInstance());
            } catch (Exception ex)
            {
                return null;
            }
        }
        return null;
    }

        /**
         * Writes a message to teh console.
         */
    public void writeToConsole(DeviceEvent dev, Object info)
        throws Exception
    {
        if (deviceConsole != null)
        {
            deviceConsole.processDeviceMessage(this, dev, info);
        }
    }

        /**
         * Sets the console for this device.
         */
    public void setConsole(Console console)
    {
        deviceConsole = console;
    }

        /**
         * Sets the mac address of this device.
         */
    public void setMacAddress(long macAdd)
    {
        this.macAddress = macAdd;
    }

        /**
         * Sets the name of the device.
         */
    public void setName(String name)
    {
        this.name = name;
    }

        /**
         * Sets the name of the device.
         */
    public String getName()
    {
        return name;
    }

        /**
         * Ensures that we have enough number of interfaces.
         */
    public void ensureInterfaceCapacity(int nInts)
    {
        if (interfaces == null)
        {
            interfaces = new NetworkInterface[nInts];
        } else if (nInts < interfaces.length)
        {
            NetworkInterface if2[] = interfaces;
            interfaces = new NetworkInterface[nInts];
            System.arraycopy(if2, 0, interfaces, 0, if2.length);
            if2 = null;
        }
        this.nInterfaces = nInts;
        for (int i = 0;i < nInterfaces;i++)
        {
            if (interfaces[i] == null)
            {
                //interfaces[i] = new NetworkInterface(this, i, "" + i);
                //interfaces[i].setMacAddress(IANA.getMacAddress());
            }
            //interfaces[i].interfaceID = (short)i;
            //interfaces[i].name = "" + i;
            //interfaces[i].parentDevice = this;
        }
        //setMacAddress(interfaces[0].macAddress);
    }

        /**
         * Get the number of interfaces we have.
         */
    public int getInterfaceCount()
    {
        return interfaces.length;
    }

        /**
         * Draws this Link.
         */
    public void paint(Graphics g, int xOffset, int yOffset)
    {
        int tx = x + xOffset;
        int ty = y + yOffset;
        int w2 = width;
        int h2 = height;
		int t1 = tx + w2 / 2;


            // if the shape isnt visible why draw it?
        if (tx + width >= 0 && ty + height >= 0) 
        {

            if (w2 < 0) 
            {
                tx += w2;
                w2 = -w2;
            }
            if (h2 < 0) 
            {
                ty += h2;
                h2 = -h2;
            }


            g.setColor(Color.white);
            g.fillRect(tx, ty, w2, h2);

            g.setColor(Color.black);

            FontMetrics fm = g.getFontMetrics();
            String l2 = "MAC: " + macAddress;
            String nameStatus = name;
            //if (inThread != null && inThread.isAlive()) nameStatus += " (*)";
            int sw = fm.stringWidth(nameStatus);
            int sw2 = fm.stringWidth(l2);
            int sh = fm.getAscent();
            int totalH = (sh + 5) * 2;
            int y = ty + sh + ((h2 - totalH) / 2);
            g.drawString(nameStatus, tx + ((w2 - sw) / 2), y);
            y += sh + 5;
            g.drawString(l2, tx + ((w2 - sw2) / 2), y);
            //g.drawRect(tx, ty, w2, h2);

            super.paint(g, xOffset, yOffset);
        }
    }

        /**
         * Calculate the size automatically.
         * The rule is that size is "good" if the width is "slightly" 
         * greater than the height.
         */
    public void autoSize(Graphics g)
    {
        /*FontMetrics fm = g.getFontMetrics();
        if (fm == null) return ;

            // basicaly we have a list of words.. 
            // the best size is the
        int maxWordLength = 0;
        int totalWidth = 0;
        int stringHeight = fm.getMaxAscent();

        for (int i = 0;i < words.length;i++)
        {
            totalWidth += fm.stringWidth(words[i] + " ");
            maxWordLength = Math.max(words[i].length(), maxWordLength);
        }

        int sideExtra = 50;
        int root = (int)Math.sqrt(totalWidth * stringHeight);

        int width = Math.max(maxWordLength, root);
        width = (2 * sideExtra) + ((width / sideExtra) * sideExtra);

        int height = (totalWidth * stringHeight) / width;
        height = (stringHeight * (height / stringHeight)) + (3 * stringHeight);*/
    }

        /**
         * Request a repaint on this box.
         */
    public void requestRepaint()
    {
        for (int i = 0, len = affectedLinks.size();i < len;i++)
        {
            ((Link)affectedLinks.elementAt(i)).requestRepaint();
        }
    }

        /**
         * Add a new link to our list.
         */
    public void addLink(Link l)
    {
        //affectedLinks.addElement(l);
    }

        /**
         * Return all the links.
         */
    public Enumeration getLinks()
    {
        return affectedLinks.elements();
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         *
         * A device has the following info:
         * <device name="blah" class="blah">
         *  // other parameters...
         *  <param name="xpos" value="551"/>
         *  <param name="ypos" value="287"/>
         *  <param name="width" value="106"/>
         *  <param name="height" value="77"/>
         * <interfaces count="num ints" default = "default interface">
         *  <interface id="short value" mac = "mac address"/>
         *          ...
         * </interfaces>
         * <shared_objs>
         *  <shared_obj class="com.blah.blah">
         *  </shared_obj>
         *          ...
         * </shared_objs>
         * <drivers>
         *  <driver class="blah">
         *  </driver>
         *          ...
         * </drivers>
         * </device>
         */
    public void readXMLNode(Element deviceElement) throws Exception
    {
        String nameAttrib = deviceElement.getAttribute(XMLParams.DEVICE_NAME);
        if (nameAttrib == null)
        {
            throw new IllegalArgumentException("Invalid device name.");
        }
        setName(nameAttrib);

            // process the interfaces
        Element intNode =
            (Element)deviceElement.
                getElementsByTagName(XMLParams.DEVICE_INTERFACES).item(0);

        int nInterfaces =
            Integer.parseInt(intNode.getAttribute(XMLParams.COUNT_PARAM));

            // get all nodes with the tag <interface>
        NodeList intList =
            intNode.getElementsByTagName(XMLParams.DEVICE_INTERFACE_TAG_NAME);

        ensureInterfaceCapacity(nInterfaces);

        //NetworkInterface tempInterface = new NetworkInterface();

        for (int i = 0; i < nInterfaces;i++)
        {
            Element elem = (Element)(intList.item(i));
            interfaces[i] = (NetworkInterface)XMLParams.readXMLObject(elem);

            if (interfaces[i].interfaceID >= nInterfaces)
            {
                throw new Exception("Invalid interface ID: " +
                                        interfaces[i].interfaceID + ".  " + 
                                        "ID must be less than interface count.");
            }
            interfaces[i].parentDevice = this;
        }

            // process the Shared Object tag
        NodeList sharedObjList =
            ((Element)deviceElement.
                getElementsByTagName(XMLParams.SHARED_OBJS_ROOT_TAG).item(0)).
                    getElementsByTagName(XMLParams.SHARED_OBJ_TAG);

        this.sharedObjs = new SharedObject[sharedObjList.getLength()];
        nSharedObjects = 0;

        for (int j = 0; j < sharedObjs.length; j++)
        {
            Element elem = (Element)sharedObjList.item(j);
            SharedObject shObj = (SharedObject)XMLParams.readXMLObject(elem);
            addSharedObject(shObj);
        }
            
            // now process the packet drivers...
        NodeList driverList =
            ((Element)deviceElement.
                getElementsByTagName(XMLParams.PACKET_MODULES_ROOT_NAME).item(0)).
                    getElementsByTagName(XMLParams.PACKET_MODULE_TAG);

        //firstDriver = lastDriver = null;
        int nDrivers = sharedObjList.getLength();

        for (int j = 0; j < nDrivers; j++)
        {
            Element elem = (Element)driverList.item(j);
            ProtocolDriver pDriver =
                (ProtocolDriver)XMLParams.readXMLObject(elem);
            driverTable.addDriver(pDriver);
        }

            // now we may need to go through child "param" tags
            // and get all the parameters that are off interest..
        NodeList params =
            deviceElement.getElementsByTagName(XMLParams.PARAM_TAG_NAME);

        for (int j = 0;j < params.getLength();j++)
        {
			Element param = (Element)params.item(j);

                // TODO:: Get all the attributes that
                // are of interest
            processParamTag(param.getAttribute("name"),
                            param.getAttribute("value"));
        }
    }

        /**
         * Process the param tag.
         */
    protected void processParamTag(String nameAttrib, String valueAttrib)
    {
        if (nameAttrib.equalsIgnoreCase(XMLParams.DEVICE_XPOS))
            x = Integer.parseInt(valueAttrib);

        else if (nameAttrib.equalsIgnoreCase(XMLParams.DEVICE_YPOS))
            y = Integer.parseInt(valueAttrib);

        else if (nameAttrib.equalsIgnoreCase(XMLParams.DEVICE_WIDTH))
            width = Integer.parseInt(valueAttrib);

        else if (nameAttrib.equalsIgnoreCase(XMLParams.DEVICE_HEIGHT))
            height = Integer.parseInt(valueAttrib);
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc) throws Exception
    {
        Element device = doc.createElement(XMLParams.DEVICE_TAG_NAME);
        device.setAttribute(XMLParams.CLASS_PARAM, getClass().toString());
        device.setAttribute(XMLParams.DEVICE_NAME, name);

        device.appendChild(XMLParams.createParamNode(doc,
                                                      XMLParams.DEVICE_XPOS,
                                                      "" + x));
        device.appendChild(XMLParams.createParamNode(doc,
                                                      XMLParams.DEVICE_YPOS,
                                                      "" + y));
        device.appendChild(XMLParams.createParamNode(doc,
                                                      XMLParams.DEVICE_WIDTH,
                                                      "" + width));
        device.appendChild(XMLParams.createParamNode(doc,
                                                      XMLParams.DEVICE_HEIGHT,
                                                      "" + height));

        Element intElement = doc.createElement(XMLParams.DEVICE_INTERFACES);
        device.appendChild(intElement);

        int nInts = 0;

            // Write the itnerfaces to the xml node.
        if (interfaces != null)
        {
            for (int i = 0;i < nInterfaces;i++)
            {
                if (interfaces[i] != null)
                {
                    intElement.appendChild(interfaces[i].getXMLNode(doc));
                    nInts ++;
                }
            }
        }
        intElement.setAttribute(XMLParams.COUNT_PARAM, nInts + "");

            // write the shared object to the xml node
        Element shobjElement = doc.createElement(XMLParams.SHARED_OBJS_ROOT_TAG);
        device.appendChild(shobjElement);
        shobjElement.appendChild(driverTable.getXMLNode(doc));
        shobjElement.setAttribute(XMLParams.COUNT_PARAM, nSharedObjects+ "");
        if (sharedObjs != null)
        {
            for (int i = 0;i < nSharedObjects;i++)
            {
                shobjElement.appendChild(sharedObjs[i].getXMLNode(doc));
            }
        }

            // write the driver table to the xml node
        device.appendChild(driverTable.getXMLNode(doc));

        return device;
    }

        /**
         * Starts the thread.
         */
    public synchronized void start(Network parent, double currTime)
    {
        // start all the drivers...
        for (int i = 0,nL = driverTable.getLevelCount();i < nL;i++)
        {
            int nDrivers = driverTable.getDriverCount(i);
            for (int j = 0;j < nDrivers;j++)
            {
                driverTable.getDriver(i, j).start(parent, currTime);
            }
        }
    }

        /**
         * Tells if this device is running and whether it can handle
         * packets.
         */
    public boolean isRunning()
    {
        return false;
    }

        /**
         * Stops the thread.
         */
    public synchronized void stop(Network parent, double currTime)
    {
        // stop all the drivers...
        for (int i = 0,nL = driverTable.getLevelCount();i < nL;i++)
        {
            int nDrivers = driverTable.getDriverCount(i);
            for (int j = 0;j < nDrivers;j++)
            {
                driverTable.getDriver(i, j).stop(parent, currTime);
            }
        }
    }


        /**
         * The event handler function.
         */
    public double handleEvent(SimEvent event, double currTime, Network parent)
    {
        if (event instanceof Packet)
        {
                // send the driver through the drivers and send
                // it to the right driver...
                // Steps 1. Find which NIC the packet came from.
            Packet packet = (Packet)event;
            NetworkInterface nic = packet.inNic;

                // get the driver that can be used to handle packets
                // coming from this driver...
            Class driverClass = nic.getProtocolDriver();

            ProtocolDriver curr = driverTable.getDriver(driverClass, 0);

                // here is where we get to it...  Since the drivers are all
            int level = 0;
            while (curr != null)
            {
                curr.stripHeaders(packet, level);
                curr = curr.processInPacket(packet, this, parent);
                level++;
            }
        } else if (event instanceof NotifyEvent)
        {
            // if we are recieving a NotifyEvent this means that a
            // notification was requested by one of the drivers!!!  In
            // which case, send this straight to the driver.
            ProtocolDriver pdriver =
                (ProtocolDriver)(((NotifyEvent)event).target);
            return pdriver.handleEvent(event, currTime, parent);
        }
        return 0;
    }

        /**
         * Return the requested interface.
         */
    public NetworkInterface getInterface(int which)
    {
        return interfaces[which];
    }
}
