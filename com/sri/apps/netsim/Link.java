package com.sri.apps.netsim;

import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * Superclass of "communication links".
 * Eg. Fibre, Cat5, Packet Relay
 * The idea here is that a Link connects arbitrary number
 * of network elements.  How and with what quality is a what is to 
 * be subclassed.
 */
public class Link extends Connector implements NetworkEntity
{
        /**
         * The link latency in seconds.
         */
    public final static double LINK_LATENCY = 1e-4;

    protected final static byte NORMAL_MODE = 0;
    protected final static byte FINDING_FIRST_DEV = 1;
    protected final static byte FOUND_TEMP_FST = 2;
    protected final static byte FINDING_SECOND_DEV = 3;
    protected final static byte FOUND_TEMP_SND = 4;

	protected static int INTERFACE_ID_STRING_WIDTH = -1;
	protected static int INTERFACE_ID_STRING_HEIGHT = -1;

    protected static byte mode = NORMAL_MODE;

        /**
         * Tells if we are running or not.
         */
    protected boolean isRunning = false;
	
        /**
         * The console for the device.
         */
    protected Console deviceConsole = null;

        /**
         * Constructor.
         */
    public Link() { }

        /**
         * Constructor.
         */
    public Link(NetworkInterface int1, NetworkInterface int2)
    {
        setInterfaces(int1, int2);
    }

        /**
         * Disconnects from the endPoint.
         * Plus this is more or less a gracefull way
         * of calling other routins and stuff.
         */
    public void disconnect()
    {
        for (int i = 0;i < endPoint.length;i++)
        {
            ((NetworkInterface)endPoint[i]).connected = false;
            ((NetworkInterface)endPoint[i]).isEnabled = false;
            ((NetworkInterface)endPoint[i]).link = null;
            endPoint[i] = null;
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
         * See if this links connects the given
         * device.
         */
    public boolean connectsTo(Device dev)
    {
        if (endPoint[0] != null &&
                ((NetworkInterface)endPoint[0]).parentDevice == dev) return true;
        if (endPoint[1] != null &&
                ((NetworkInterface)endPoint[1]).parentDevice == dev) return true;
        return false;
    }

        /**
         * Set the endPoint.
         */
    public void setInterfaces(NetworkInterface int1, NetworkInterface int2)
    {
        if (int1 == null || int2 == null)
        {
            endPoint[0] = endPoint[1] = null;
            return ;
        }
        this.endPoint[0] = int1;
        this.endPoint[1] = int2;
        ((NetworkInterface)endPoint[0]).link =
           ((NetworkInterface)endPoint[1]).link = this;
        ((NetworkInterface)endPoint[0]).connected =
           ((NetworkInterface)endPoint[1]).connected = true;
    }


        /**
         * Get the cost of the link.
         * Will depend on a lot of factors.
         */
    public int getCost()
    {
            // just a hack... According to IEEE standards it assumes
            // that 19 is the cost of a 100MBps link
        return 19;
    }

        /**
         * Does this shape "contain" the given
         * point.
         * How to check if a point lies "around" a line?
         * TODO::
         */
    public boolean contains(int x, int y)
    {
        return false;
    }

        /**
         * Processes a mouse released event.
         */
    public int mouseReleased(MouseEvent e,
                             int mX, int mY,
                             //int pressedX, int pressedY,
                             SceneViewer renderer)
    {
        return 0;
    }

        /**
         * Processes a mouse pressed event.
         */
    public int mousePressed(MouseEvent e,int mX, int mY,  SceneViewer renderer)
    {
            //currentX = pressedX = mX;
            //currentY = pressedY = mY;
        /*if (mode == NORMAL_MODE)
        {
            // do nothing for now...
            // TODO: work on highlights
        } else if (mode == FOUND_TEMP_FST)
        {
            SceneElement temp = renderer.getElementAt(mX, mY);
            if (endPoint[0] != null)
            {
                mode = FOUND_TEMP_FST;
            }
        }*/
        return 0;
    }

        /**
         * Processes a mouse dragged event.
         */
    public int mouseDragged(MouseEvent e,
                            int mX, int mY,
                            //int pressedX, int pressedY,
                            SceneViewer renderer)
    {
        return 0;
    }

        /**
         * Processes a mouse move event.
         */
    public int mouseMoved(MouseEvent e, int mX, int mY, SceneViewer renderer)
    {
        /*if (mode == NORMAL_MODE)
        {
        } else if (mode == FINDING_FIRST_DEV)
        {
            Shape sh = renderer.getElementAt(mX, mY);
            if (sh != null && sh instanceof Device)
            {
                mode = FOUND_TEMP_FST;
                endPoint[0] = sh;
            }
        } else if (mode == FINDING_SECOND_DEV)
        {
            endPoint[1] = renderer.getElementAt(mX, mY);
            if (endPoint[1] != null)
            {
                mode = FOUND_TEMP_FST;
            }
        }*/

        return 0;
    }

        /**
         * Default paint method.
         * Paints at the current bounds and as FIXED_MODE
         */
    public void paint(Graphics g, int xOffset, int yOffset)
    {
			// calculate what the string width and heights would be
		if (INTERFACE_ID_STRING_WIDTH < 0)
		{
			FontMetrics fm = g.getFontMetrics();
			INTERFACE_ID_STRING_WIDTH = fm.stringWidth("99");
			INTERFACE_ID_STRING_HEIGHT = fm.getAscent();
		}
		
        if (endPoint[0] != null && endPoint[1] != null)
        {
            Device dev1 = ((NetworkInterface)endPoint[0]).parentDevice;
            Device dev2 = ((NetworkInterface)endPoint[1]).parentDevice;
			
            int sx = xOffset + dev1.x + (dev1.width / 2);
            int sy = yOffset + dev1.y + (dev1.height / 2);
            int ex = xOffset + dev2.x + (dev2.width / 2);
            int ey = yOffset + dev2.y + (dev2.height / 2);
			
			String int1String = ((NetworkInterface)endPoint[0]).interfaceID + "";
			String int2String = ((NetworkInterface)endPoint[1]).interfaceID + "";
			
			int deltaX1 = 0, deltaY1 = 0,
                deltaX2 = 0, deltaY2 = 0;

            g.setColor(Color.black);
            g.drawLine(sx, sy, ex, ey);

                // now to find the interface ids of the endPoint

                // the slope of the connecting segment...
            int dyLine = ey - sy;
            int dxLine = ex - sx;

                // the slope of the first device...
            int devHeight = dev1.height;
            int devWidth = dev1.width;
			
            //System.out.println("dxLine, dyLine, height, width, dy*width, dx*height: " + dxLine + ", " + dyLine + ", " + devHeight + ", " + devWidth + ", " + (dyLine * devWidth) + ", " + (dxLine * devHeight));

			if ((dyLine < 0 ? -dyLine : dyLine) * devWidth > 
                  (dxLine < 0 ? -dxLine : dxLine) * devHeight)
			{
						// then we deal with the "top" or the "bottom"
						// sides of the device...
				if (ey > sy)
				{
						// then segment intersecs the "bottom" side
					deltaY1 = (devHeight >> 1) + 5;
                    if (ex > sx) deltaX1 = -INTERFACE_ID_STRING_WIDTH;
				} else
				{
						// then segment intersecs the "top" side
					deltaY1 = -5 - (devHeight >> 1) - INTERFACE_ID_STRING_HEIGHT;
				}
				deltaX1 += ((deltaY1 >> 1) * dxLine) / dyLine;
                //if (ex < sx) deltaX1 -= INTERFACE_ID_STRING_WIDTH;
			} else
			{
						// otherwise we are dealing with the "left"
						// or "right" side of the device.
                deltaY1 = -INTERFACE_ID_STRING_HEIGHT;
				if (ex > sx)
				{
						// then segment intersecs the "bottom" side
					deltaX1 = 5 + (devWidth >> 1);
				} else
				{
						// then segment intersecs the "top" side
					deltaX1 = -5 - (devWidth >> 1) - INTERFACE_ID_STRING_WIDTH;
                    deltaY1 = 0;
				}
                if (dxLine == 0) dxLine++;
				deltaY1 += (dyLine * (deltaX1 >> 1)) / dxLine;

			}

                // the slope of the ending device...
            devHeight = dev2.height;
            devWidth = dev2.width;

			if ((dyLine < 0 ? -dyLine : dyLine) * devWidth > 
                  (dxLine < 0 ? -dxLine : dxLine) * devHeight)
			{
						// then we deal with the "top" or the "bottom"
						// sides of the device...
				if (sy > ey)
				{
						// then segment intersecs the "bottom" side
					deltaY2 = (devHeight >> 1) + 5;
                    if (sx > ex) deltaX2 = -INTERFACE_ID_STRING_WIDTH;
				} else
				{
						// then segment intersecs the "top" side
					deltaY2 = -5 - (devHeight >> 1) - INTERFACE_ID_STRING_HEIGHT;
				}
				deltaX2 += ((deltaY2 >> 1) * dxLine) / dyLine;
                //if (ex < sx) deltaX1 -= INTERFACE_ID_STRING_WIDTH;
			} else
			{
						// otherwise we are dealing with the "left"
						// or "right" side of the device.
                deltaY2 = -INTERFACE_ID_STRING_HEIGHT;
				if (sx > ex)
				{
						// then segment intersecs the "bottom" side
					deltaX2 = 5 + (devWidth >> 1);
				} else
				{
						// then segment intersecs the "top" side
					deltaX2 = -5 - (devWidth >> 1) - INTERFACE_ID_STRING_WIDTH;
                    deltaY2 = 0;
				}
				deltaY2 += (dyLine * (deltaX2 >> 1)) / dxLine;
			}

				// now draw the two interface IDs
			g.drawString("" + ((NetworkInterface)endPoint[0]).interfaceID, 
						 sx + deltaX1, 
						 sy + deltaY1 + INTERFACE_ID_STRING_HEIGHT);

			g.drawString("" + ((NetworkInterface)endPoint[1]).interfaceID, 
						 ex + deltaX2, 
						 ey + deltaY2 + INTERFACE_ID_STRING_HEIGHT);
		}
    }

        /**
         * Get the information about a link as
         * an XML node.
         */
    public Element getXMLNode(Document doc)
    {
        if (endPoint[0] == null || endPoint[1] == null) return null;

        Element link = doc.createElement(XMLParams.LINK_TAG_NAME);

        Element dev1 = doc.createElement(XMLParams.DEVICE_TAG_NAME);
        dev1.setAttribute(XMLParams.DEVICE_NAME,
              ((NetworkInterface)endPoint[0]).parentDevice.getName());
        dev1.setAttribute(XMLParams.DEVICE_INTERFACE, "" +
              ((NetworkInterface)endPoint[0]).interfaceID);

        Element dev2 = doc.createElement(XMLParams.DEVICE_TAG_NAME);
        dev2.setAttribute(XMLParams.DEVICE_NAME,
              ((NetworkInterface)endPoint[1]).parentDevice.getName());
        dev2.setAttribute(XMLParams.DEVICE_INTERFACE, "" +
              ((NetworkInterface)endPoint[1]).interfaceID);

        link.appendChild(dev1);
        link.appendChild(dev2);

        /*
        link.setAttribute(XMLParams.LINK_STARTING_DEVICE,
                            ((NetworkInterface)endPoint[0]).parentDevice.getName());

        link.setAttribute(XMLParams.LINK_STARTING_INTERFACE,
                            "" + ((NetworkInterface)endPoint[0]).interfaceID);

        link.setAttribute(XMLParams.LINK_ENDING_DEVICE,
                            ((NetworkInterface)endPoint[1]).parentDevice.getName());

        link.setAttribute(XMLParams.LINK_ENDING_INTERFACE,
                            "" + ((NetworkInterface)endPoint[1]).interfaceID);
        */

        return link;
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         */
    public void processXMLNode(Scene scene, Element linkElement)
    {
        //processParamTags(deviceElement);
        
        NodeList devices = linkElement.getElementsByTagName(XMLParams.DEVICE_TAG_NAME);

        String nodeValue = linkElement.getNodeValue();

        for (int i = 0;i < 2;i++)
        {
            Element curr = (Element)devices.item(i);

			String nameAttrib = curr.getAttribute(XMLParams.DEVICE_NAME);
			String intAttrib = curr.getAttribute(XMLParams.DEVICE_INTERFACE);

            if (nameAttrib == null || intAttrib == null)
            {

            }

            try
            {
                Device device = Utils.findDevice(nameAttrib, scene);
                int iface = Integer.parseInt(intAttrib);

                if (i == 0)
                {
                    endPoint[0] = device.getInterface(iface);
                } else
                {
                    endPoint[1] = device.getInterface(iface);
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
                setInterfaces(null, null);
                return ;
            }
        }
        setInterfaces((NetworkInterface)endPoint[0],
                      (NetworkInterface)endPoint[1]);
    }

        /**
         * Tells if we are in "creation" mode.
         */
    public boolean isCreating()
    {
        return mode == NORMAL_MODE;
    }

        /**
         * Tells if we are in "creation" mode.
         */
    public void setCreating(boolean creating)
    {
        mode = FINDING_FIRST_DEV;
    }

        /**
         * Tells if this shape is currently undergoing any transformation
         */
    public boolean isTransforming()
    {
        return false;
    }

        /**
         * Sets the transformation flag for this element.
         */
    public void setTransforming(boolean isTransforming) 
    {
    }

        /**
         * Starts the device.
         */
    public void start(Network parent, double currTime)
    {
        isRunning = true;
    }

        /**
         * Stops the device.
         */
    public void stop(Network parent, double currTime)
    {
        isRunning = false;
    }

        /**
         * Tells if he device is running or not.
         */
    public boolean isRunning()
    {
        return isRunning;
    }

        /**
         * Network entities must process a packet and return the time taken
         * to process the packet.  The time returned is in clock periods.
         */
    public double handleEvent(SimEvent event,
                              double currTime,
                              Network parent)
    {
            // basically if it is a packet, then the packet is
            // returned 
        if (event instanceof Packet)
        {
            Packet packet = (Packet)event;
                // in this case, the event.src should be a "Device"
                // and target would be "this" link.
            Device d1 = endPoint[0] != null ? 
                        ((NetworkInterface)endPoint[0]).parentDevice : null;
            Device d2 = endPoint[1] != null ? 
                        ((NetworkInterface)endPoint[1]).parentDevice : null;

            packet.dest = (event.src == d1 ? d2 : d1);
            packet.inNic =
                (NetworkInterface)(event.src == d1 ? endPoint[0] : endPoint[1]);
            packet.src = this;

                // now how do we 
            packet.targetTime += LINK_LATENCY;
            parent.addEvent(event);
        }
        return 0;
    }

        /**
         * Reads the XML node from an XML file.
         */
    public void readXMLNode(Element deviceElement) throws Exception
    {
    }
}
