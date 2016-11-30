

package com.sri.apps.netsim;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.*;
import com.sri.gui.core.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;
import com.sri.gui.ext.drawing.selectors.*;
import com.sri.gui.ext.dialogs.*;
import java.awt.image.*;
import com.sri.utils.*;
import com.sri.utils.adt.*;
import java.io.*;
import java.net.*;

/**
 * Panel used to show the stencil network elements and operations...
 */
public class NetworkStencil extends Stencil implements ActionListener, ItemListener
{
        /**
         * The network to which this scene belongs to.
         */
    protected Network parentNetwork;

    protected ConsoleThread consoleThread = null;

        /**
         * File dialog for opening and saving
         * files.
         */
    protected FileDialog fDialog = null;

        /**
         * Filter for showing only files with the extension ".net"
         */
    protected FilenameFilter fnameFilter = new DotNetFilter();

    ConsoleDialog consoleDialog = null;

        /**
         * The dialog for adding new devices.
         */
    protected AddDeviceDialog adDialog = null;

        /**
         * The dialog for editing device properties.
         */
    //protected PropertiesDialog propDialog = null;

        /**
         * The dialog for adding new links.
         */
    protected AddLinkDialog alDialog = null;

        /**
         * The dialog for generating traffic.
         */
    //protected GTDialog gtDialog = null;

    protected final static int ADD_ROUTER = 0;
    protected final static int ADD_LAN_SWITCH = 1;
    protected final static int ADD_ATM_SWITCH = 2;
    protected final static int ADD_HOST = 3;
    protected final static int ADD_FIREWALL = 4;
    protected final static int ADD_LINK = 5;
    protected final static int START_DEVICES = 6;
    protected final static int STOP_DEVICES = 7;
    protected final static int NEW_NETWORK = 8;
    protected final static int LOAD_NETWORK = 9;
    protected final static int SAVE_NETWORK = 10;
    protected final static int PRINT_NETWORK = 11;
    protected final static int SHOW_CONSOLE = 12;
    protected final static int GENERATE_TRAFFIC = 13;

    protected ButtonInfo bInfo[] = 
    {
        new ButtonInfo("Add Router", new Rectangle(41, 6, 35, 25)),
        new ButtonInfo("Add LAN Switch", new Rectangle(2, 2, 32, 33)),
        new ButtonInfo("Add ATM Switch", new Rectangle(84, 4, 26, 26)),
        new ButtonInfo("Add Host", new Rectangle(118, 1, 28, 30)),
        new ButtonInfo("Add Firewall", new Rectangle(183, 4, 22, 22)),
        new ButtonInfo("Add Link", new Rectangle(153, 5, 23, 18)),
        new ButtonInfo("Start all devices", new Rectangle(40, 44, 40, 19)),
        new ButtonInfo("Stop all devices", new Rectangle(2, 38, 32, 32)),
        new ButtonInfo("New Network", new Rectangle(159, 45, 11, 13)),
        new ButtonInfo("Load Network", new Rectangle(88, 81, 18, 15)),
        new ButtonInfo("Save Network", new Rectangle(53, 81, 15, 15)),
        new ButtonInfo("Print Network", new Rectangle(10, 80, 17, 17)),
        new ButtonInfo("Show Console", new Rectangle(124, 80, 16, 15)),
        new ButtonInfo("Generate Traffic", new Rectangle(320, 20, 20, 20)),
    };

    protected SButton buttons[] = null;

        /**
         * Constructor.
         */
    public NetworkStencil(SceneViewer scene, InfoListener infoListener, Image srcImage)
    {
        super(scene, infoListener);

        setLayout(new GridLayout(4, 4));
        //setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageMap iMap = new ImageMap(srcImage);

        buttons = new SButton[bInfo.length];
        for (int i = 0;i < bInfo.length;i++)
        {
            if (bInfo[i].isCheckButton)
            {
                buttons[i] = new SCheckButton(iMap.getImage(bInfo[i].bounds),
                                              bInfo[i].title);
                ((SCheckButton)buttons[i]).addItemListener(this);
            } else
            {
                buttons[i] = new SActionButton(iMap.getImage(bInfo[i].bounds),
                                               bInfo[i].title);
                ((SActionButton)buttons[i]).addActionListener(this);
            }
            buttons[i].addMouseListener(this);
            buttons[i].addMouseMotionListener(this);
            buttons[i].setTextPosition(SButton.CENTER);
            buttons[i].setContentAllignment(SButton.CENTER);
            buttons[i].showText(false);
            buttons[i].setRollOver(true);
            add(buttons[i]);
        }

        consoleThread = new ConsoleThread();
        consoleThread.start();
    }

        /**
         * Starts all the devices.
         * Basicaly all devices start running
         * again and the Hosts will start sending
         * packets again from the start.
         */
    public void startDevices()
    {
                // start all devices
        Scene ourScene = sceneViewer.ourScene;
        for (int i = 0; i < ourScene.nShapes;i++)
        {
            try
            {
                if (ourScene.shapes[i] instanceof Device)
                {
                    //((Device)ourScene.shapes[i]).start(parentNetwork);
                }
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }

        sceneViewer.repaint();
    }

        /**
         * Stops all the devices.
         */
    public void stopDevices()
    {
        Scene ourScene = sceneViewer.ourScene;
        for (int i = 0; i < ourScene.nShapes;i++)
        {
            try
            {
                if (ourScene.shapes[i] instanceof Device)
                {
                    //((Device)ourScene.shapes[i]).stop(parentNetwork);
                }
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }

        sceneViewer.repaint();
    }

        /**
         * Ensures that the dialogs are not null.
         */
    protected void verifyDialogs()
    {
        Frame parentFrame = Utils.getParentFrame(this);

        /*if (gtDialog == null)
        {
            gtDialog = new GTDialog(parentFrame);
        }

        if (propDialog == null)
        {
            propDialog = new PropertiesDialog(parentFrame);
        }*/
        if (consoleDialog == null)
        {
            consoleDialog = new ConsoleDialog(parentFrame);
        }

        if (alDialog == null)
        {
            alDialog = new AddLinkDialog(parentFrame, sceneViewer.ourScene);
        }

        if (adDialog == null)
        {
            adDialog = new AddDeviceDialog(parentFrame);
        }

        adDialog.pack();
        adDialog.centerDialog();
    }

        /**
         * Item Event listener.
         */
    public void itemStateChanged(ItemEvent e)
    {
    }

        /**
         * Action Performed Event Handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();

        if (src instanceof SActionButton)
        {
            verifyDialogs();
            int pos = -1;
            for (pos = 0;pos < buttons.length && buttons[pos] != src;pos++) ;

            switch (pos)
            {
                case ADD_LAN_SWITCH: 
                    showAddDeviceDialog(AddDeviceDialog.ADD_LAN_SWITCH);
                break;
                case ADD_ROUTER: 
                    showAddDeviceDialog(AddDeviceDialog.ADD_ROUTER);
                break;
                case ADD_ATM_SWITCH: 
                break;
                case ADD_HOST: 
                    showAddDeviceDialog(AddDeviceDialog.ADD_HOST);
                break;
                case ADD_FIREWALL: 
                    showAddDeviceDialog(AddDeviceDialog.ADD_FIREWALL);
                break;
                case ADD_LINK: 
                    addNewLink();
                break;
                case NEW_NETWORK: 
                    sceneViewer.clear();
                break;
                case START_DEVICES: 
                    System.out.println("Starting Devices...");
                    startDevices();
                break;
                case STOP_DEVICES:
                    System.out.println("Stopping Devices...");
                    stopDevices();
                break;
                case GENERATE_TRAFFIC:
                    try
                    {
                        generateTraffic();
                    } catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                break;
                case SHOW_CONSOLE:
                    consoleDialog.setVisible(true);
                    Utils.placeWindow(consoleDialog,
                                      Alignment.TOP,
                                      Alignment.RIGHT,
                                      true);
                    consoleDialog.toFront(); 
                break;

                case PRINT_NETWORK:
                    sceneViewer.print();
                break;

                case LOAD_NETWORK: case SAVE_NETWORK:
                    boolean isOpen = pos == LOAD_NETWORK;

                    if (fDialog == null)  return ;

                    fDialog.setFilenameFilter(fnameFilter);
                    fDialog.setTitle(isOpen ? "Load Network Data..." :
                                              "Save Network Data...");
                    fDialog.setMode (isOpen ? FileDialog.LOAD : FileDialog.SAVE);
                    fDialog.setVisible(true);

                    String dir = fDialog.getDirectory();
                    String fname = fDialog.getFile();
                    if (dir == null || fname == null) return ;

                    String file = dir + fname;

                    try
                    {
                        if (isOpen)
                        {
                            readNetwork(new FileInputStream(file));
                        } else
                        {
                            writeNetwork(new FileWriter(file));
                        }
                    } catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                break;
            }
        }
    }

        /**
         * Called when a new link is added.
         */
    protected void addNewLink()
    {
        alDialog.setVisible(true);

        if (alDialog.wasCancelled()) return ;

            // get the new link information..
        Device startingDevice = alDialog.getStartingDevice();
        Device endingDevice = alDialog.getEndingDevice();
        int si = alDialog.getStartingInterface();
        int ei = alDialog.getEndingInterface();
        if (si >= 0 && ei >= 0) 
        {
            NetworkInterface int1 = ((Device)startingDevice).getInterface(si);
            NetworkInterface int2 = ((Device)endingDevice).getInterface(ei);

            Link currLink = new Link(int1, int2);

                // add the link to our network...
            sceneViewer.ourScene.addConnector(currLink);
            sceneViewer.repaint();
            currLink.setConsole(consoleThread);
        }
    }

        /**
         * Shows the "Add Device" dialog and 
         * takes care of drawing mode.
         */
    protected void showAddDeviceDialog(int type)
    {
        verifyDialogs();
        adDialog.show(type);
        if (adDialog.wasCancelled()) return ;

        String hostName = adDialog.getHostName();
        int nInts = adDialog.getInterfaceCount();
        Scene ourScene = sceneViewer.ourScene;

        if (Utils.findDevice(hostName, ourScene) != null)
        {
            processError("Device \"" + hostName + "\" already exists.");
            return ;
        }

        if (nInts < 1)
        {
            processError("Invalid interface count.  Must be greater than 0");
            return ;
        }

        if (type == AddDeviceDialog.ADD_HOST)
        {
            /*Host host = new Host(hostName, nInts);
            AddDeviceDialog.HOST_COUNT++;
            AddDeviceDialog.ipCounter++;

            ((RouterInterface)host.interfaces[0]).ipAddress
                                            = adDialog.getIPAddress();
            sceneViewer.addElement(host, true);*/
        } else if (type == AddDeviceDialog.ADD_FIREWALL)
        {
            AddDeviceDialog.FW_COUNT++;
            //sceneViewer.addElement(new Firewall(hostName, nInts), true);
        } else if (type == AddDeviceDialog.ADD_ROUTER)
        {
            AddDeviceDialog.ROUTER_COUNT++;
            sceneViewer.addElement(new Router(hostName, nInts), true);
        } else 
        {
            AddDeviceDialog.LAN_SWITCH_COUNT++;
            sceneViewer.addElement(new Switch(hostName, nInts), true);
        }
    }

        /**
         * Generates the traffic.
         */
    protected void generateTraffic()
        throws Exception
    {
        /*if (gtDialog == null)
        {
            gtDialog = new GTDialog(Utils.getParentFrame(this));
        }
        Scene ourScene = sceneViewer.ourScene;

        gtDialog.pack();
        gtDialog.centerDialog();
        gtDialog.show(ourScene, null);

        if ( ! gtDialog.wasCancelled())
        {
                 // Get the client.
            PortClient newClient = null;

                //PortService 
                // now add a new client on the given host..
            Host sourceHost = (Host)gtDialog.getSourceDevice();
            Device targetHost = gtDialog.getTargetDevice();

            int port = gtDialog.getDestPort();
            int dPort = gtDialog.getDestPort();
            int destIP = gtDialog.getDestIP();

            switch (gtDialog.getTrafficType())
            {
                case GTDialog.UDP_TRAFFIC:
                    newClient = new PacketClient(sourceHost, 1, true); break;
                case GTDialog.TCP_TRAFFIC:
					newClient = new PacketClient(sourceHost, 1, false); break;
                default: newClient = new MSGClient(sourceHost, 1);
            }

            sourceHost.addClient(newClient);

                // do we start the client??
            sourceHost.start();

            newClient.connectTo(targetHost.macAddress, destIP, dPort);
        }*/
    }

        /**
         * Reads a network from file.
         */
    public void readNetwork(FileInputStream fin) throws Exception
    {
    }


        /**
         * Writes a network to file.
         */
    public void writeNetwork(FileWriter fWriter) throws Exception
    {
    }

        /**
         * Process a given error.
         */
    protected void processError(String error)
    {
        System.out.println(error);
    }

        /**
         * Component Shown Event Handler.
         */
    public void componentShown(ComponentEvent e)
    {
        //if (e.getSource() == this && cdialog == null)
        {
            verifyDialogs();
        }
    }

        /**
         * Mouse Moved event handler.
         */
    public void mouseMoved(MouseEvent e)
    {
        if (e.getSource() instanceof SActionButton)
        {
            infoListener.setInfo(((SActionButton)e.getSource()).getText());
        } else
        {
            infoListener.setInfo("");
        }
    }

        /**
         * Filters files with the extension ".net"
         */
    class DotNetFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            //System.out.println("dir = " + dir + ", fname = " + name);
            return name.toLowerCase().endsWith(".net");
        }
    }

        /**
         * The console thread.
         */
    class ConsoleThread extends SThread implements Console
    {
            /**
             * Queue where the messages are queued.
             */
        protected com.sri.utils.adt.Queue messageQueue = new com.sri.utils.adt.Queue();

            /**
             * Run method for the message processor.
             */
        public synchronized void run()
        {
loop:
            while (isAlive())
            {
                    // wait for a packet to arrive first...
                while (consoleDialog == null || (messageQueue.getSize() < 3 && isAlive()))
                {
                    try { this.wait(); }
                    catch (InterruptedException e) { }
                }

                if ( ! isAlive()) break loop;

                try
                {
                        // Print out the message to the window...
                    if (consoleDialog != null)
                    {
                        consoleDialog.processDeviceMessage(
                            (Device)messageQueue.get(),
                            (DeviceEvent)messageQueue.get(),
                            messageQueue.get());
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
            }

            isRunning = false;
        }

            /**
             * Process a message from the device.
             */
        public synchronized void processDeviceMessage(Device device,
                                                      DeviceEvent dev,
                                                      Object info)
            throws Exception
        {

                // write it directly to the consoleDialog
                // instead of queing it and letting the
                // thread take over...
            //consoleDialog.processDeviceMessage(device, code, info);

            if ( ! isAlive()) return ;

            messageQueue.put(device);
            messageQueue.put(dev);
            messageQueue.put(info);
            this.notifyAll();
        }

            /**
             * Process a message from a link.
             */
        public synchronized void processLinkMessage(Link link,
                                                    Integer code,
                                                    Object info)
            throws Exception
        {
        }
    }

    class ButtonInfo
    {
        String title;
        Rectangle bounds;
        boolean isCheckButton = false;

        public ButtonInfo(String title, Rectangle b)
        {
            this(title, b, false);
        }

        public ButtonInfo(String title, Rectangle b, boolean isc)
        {
            this.title = title;
            this.bounds = b;
            this.isCheckButton = isc;
        }
    }
}
