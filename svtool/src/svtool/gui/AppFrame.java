package svtool.gui;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.gui.dialogs.*;
import svtool.gui.views.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 

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

public class AppFrame extends JFrame
                      implements
                        ItemListener, ActionListener, AppMediator,
                        WindowListener, ResourceManager, TaskMonitor
{
    protected final static String DEFAULT_CONFIG_FILE = "config.xml";
    protected String configFile = null;

        /**
         * The map for associating a DBView object with its respective
         * internal frame.
         */
    protected Map viewFrameMap = new HashMap();

        /**
         * List of ImageIcon objects.
         */
    protected Map iconMap = new HashMap();

        /**
         * List of LAF sorted by name.
         */
    protected Map lafMap = new HashMap();

        /**
         * List of all the db view names that are known.
         *
         * This list will be loaded from the config file on startup.
         */
    protected java.util.Map dbViewClasses = new java.util.HashMap();

        /**
         * List of currently opened databases.
         *
         * Each db can have a separate view associated with it.
         * This is the list of all individual database sessions.
         */
    protected java.util.List dbList = new ArrayList();

        /**
         * List of views that can extract information from the database.
         *
         * So far the only views that are associated with a database are
         * 1) Query Windows
         * 2) Service Windows
         * 3) Network Discovery Windows
         */
    protected java.util.List dbViewList = new ArrayList();

        /**
         * The task in which the database connection happens.
         */
    protected DatabaseConnector dbConnector = new DatabaseConnector();

        /**
         * The popupmenu for Views.
         */
    protected JPopupMenu dbViewMenu = new JPopupMenu("DB Views");
    protected JButton attachButton = new JButton("Attach");
    protected JToolBar appToolBar = new JToolBar();
    protected PopupListener popupListener = new PopupListener(dbViewMenu);

    /********************************************************************
     *      Menu Item Stuff                                             *
     ********************************************************************/
    AppMenuBar appMenuBar = new AppMenuBar();

    /********************************************************************
     *      Necessary Dialogs                                           *
     ********************************************************************/
    protected AboutDialog aboutDialog = new AboutDialog(this, "About Service Validator...", 30);

    protected JLabel statusLabel = new JLabel("Welcome...");

        /**
         * Dialog to initiate a new connection.
         */
    protected ConnectDialog connectDialog = new ConnectDialog(this);

        /**
         * The progress dialog to handle progress monitorable tasks.
         */
    protected ProgressDialog progressDialog = new ProgressDialog(this);

        /**
         * The panel for doing the searches.
         */
    protected JToolBar searchToolBar = new JToolBar("Search...");
    protected SearchPanel searchPanel = new SearchPanel();

    final JFileChooser fileChooser = new JFileChooser();

        // Session controller panel...
    JDesktopPane viewHolderPanel = new JDesktopPane();

        // holds the list of internal panes that are needed for showing
        // each of the views in their own seperate "windows"
    protected java.util.Map internalPaneMap = new HashMap();

    DBSessionControlPanel dbsControlPanel = new DBSessionControlPanel(this);
    JSplitPane mainSplitter =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                               viewHolderPanel, dbsControlPanel);

        /**
         * Constructor.
         */
    public AppFrame(String configFile)
    {
        super (SVVersion.getApplicationFrameTitle());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        readConfigurations(configFile);

        createMenus();

        setListeners();
        layComponents();

        //setLocationRelativeTo(null);
        setVisible(true);

    }

        /**
         * Reload the configurations from a given file.
         *
         * @param   inFile  The input file from which the configs are read.
         */
    public void readConfigurations(String inFile)
    {
        if (inFile == null) return ;
        try
        {
            configFile = inFile;

            InputStream istream = new FileInputStream(inFile);

            DocumentBuilder domImp = DocumentBuilderFactory.newInstance().
                                            newDocumentBuilder();
            Document configDoc = domImp.parse(istream);

            readIconInfo(configDoc);
            readLAFConfigs(configDoc);
            readViewClassConfigs(configDoc);

            //queryPanel.readConfigurations(configDoc);
            connectDialog.readConfigurations(configDoc);
        } catch (IOException ioe)
        {
            sendMessage("Cannot open config file: " + inFile);
        } catch (Exception exc)
        {
            SVUtils.logException(exc);
        }
    }

        /**
         * Read configuration information for the various icons.
         *
         * @param   configDoc   The XML document that holds the config
         *                      info.
         */
    protected void readIconInfo(Document configDoc)
    {
        Element rootElem = configDoc.getDocumentElement();

        Element iconListNode =
            (Element)rootElem.
                getElementsByTagName(XMLUtils.ICON_ROOT_NODE).item(0);

        NodeList iconElems =
            iconListNode.getElementsByTagName(XMLUtils.ICON_NODE);

        for (int i = 0, l = iconElems.getLength();i < l;i++)
        {
            Element iconNode = (Element)iconElems.item(i);
            String iconName = iconNode.getAttribute(XMLUtils.NAME_ATTRIB);
            String iconURI = iconNode.getAttribute(XMLUtils.URI_ATTRIB);
            ImageIcon iconImage = new ImageIcon(iconURI);
            iconMap.put(iconName, iconImage);
        }
    }

        /**
         * Starts a search process on the current view.
         */
    protected void doSearch(DBView dbView)
    {
        searchPanel.initSearch(dbView);
    }

        /**
         * Read configuration information for the various view classes.
         *
         * @param   configDoc   The XML document that holds the config
         *                      info.
         */
    protected void readViewClassConfigs(Document configDoc)
    {
        Element rootElem = configDoc.getDocumentElement();

        Element viewListNode =
            (Element)rootElem.
                getElementsByTagName(XMLUtils.DBVIEW_ROOT_NODE).item(0);

        NodeList viewElems =
            viewListNode.getElementsByTagName(XMLUtils.DBVIEW_NODE);

        for (int i = 0, l = viewElems.getLength();i < l;i++)
        {
            Element viewNode = (Element)viewElems.item(i);
            String viewName = viewNode.getAttribute(XMLUtils.NAME_ATTRIB);
            String className = viewNode.getAttribute(XMLUtils.CLASS_ATTRIB);

            //LookAndFeel laf = (LookAndFeel)SVUtils.createInstance(className);
            JMenuItem menuItem =
                new JMenuItem(new AddViewAction(className, viewNode));
            menuItem.setText(viewName);
            dbViewMenu.add(menuItem);
            //dbViewClasses.put(className, viewNode);
        }
    }

        /**
         * Read configuration information for the Look and Feels.
         *
         * @param   configDoc   The XML document that holds the config
         *                      info.
         */
    protected void readLAFConfigs(Document configDoc)
    {
        Element rootElem = configDoc.getDocumentElement();

        Element lafListNode =
            (Element)rootElem.getElementsByTagName(XMLUtils.LAF_ROOT_NODE).item(0);

        NodeList lafElems =
            lafListNode.getElementsByTagName(XMLUtils.LAF_NODE);

        appMenuBar.addLAFMenuItem(
                "System Default",
                UIManager.getSystemLookAndFeelClassName(), null);

        appMenuBar.addLAFMenuItem(
                "Swing Default",
                UIManager.getCrossPlatformLookAndFeelClassName(), null);

        for (int i = 0, l = lafElems.getLength();i < l;i++)
        {
            Element lafNode = (Element)lafElems.item(i);
            String lafName = lafNode.getAttribute(XMLUtils.NAME_ATTRIB);
            String className = lafNode.getAttribute(XMLUtils.CLASS_ATTRIB);
            String jarName = lafNode.getAttribute(XMLUtils.JAR_ATTRIB);

            appMenuBar.addLAFMenuItem(lafName, className, jarName);
        }
    }

        /**
         * Save configurations to file.
         */
    public void saveConfigurations(String fname)
    {
        /*if (fname == null) 
        {
            sendMessage("Invalid file.");
            System.out.println("No configuration file found: ");
            return ;
        }

        sendMessage("Saving configurations to " + fname + "....");

        Document configDoc = new DocumentImpl();
        Element configDocRoot =
                configDoc.createElement(XMLUtils.CONFIGS_ROOT_NAME);
        configDoc.appendChild(configDocRoot);

        Element elem = connectDialog.getConfigurations(configDoc);
        if (elem != null) configDocRoot.appendChild(elem);

        elem = queryPanel.getConfigurations(configDoc);
        if (elem != null) configDocRoot.appendChild(elem);


        OutputFormat format
            = new OutputFormat(configDoc, "UTF-8", true);
        format.setLineSeparator("\n");
        format.setLineWidth(90);
        format.setIndent(2);
        format.setPreserveSpace(true);

        try
        {
            FileWriter  writer = new FileWriter(fname);
            XMLSerializer serial =
                    new XMLSerializer( writer, format );
            serial.asDOMSerializer(); // As a DOM Serializer
            serial.serialize( configDoc.getDocumentElement() );
            System.out.println("Done.");
        } catch (IOException ex)
        {
            sendMessage("Could not save configurations to: " + fname);
        }*/
    }

        /**
         * Sets up the event listeners.
         */
    protected void setListeners()
    {
        addWindowListener(this);

        //viewHolderPanel.addMouseListener(popupListener);
        //attachButton.addMouseListener(popupListener);
        dbsControlPanel.addMouseListener(popupListener);
    }

        /**
         * Sets up the components initially.
         */
    protected void layComponents()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension ssize = tk.getScreenSize();
        int tHeight = (int)(ssize.height * 1);
        //pack();
        setBounds(0, 0, ssize.width, tHeight);

        System.out.println("Creating tabs...");

        System.out.println("Adding components...");

        appToolBar.add(attachButton);

        searchToolBar.add(searchPanel);
        JPanel toolBarPanel = new JPanel();
        //toolBarPanel.add(appToolBar);
        toolBarPanel.add(searchToolBar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("North", toolBarPanel);
        getContentPane().add("Center", mainSplitter);
        getContentPane().add("South", statusLabel);
        mainSplitter.setDividerLocation((int)(tHeight * 0.7));

        invalidate();
        validate();
    }

        /**
         * Creates the menus.
         */
    protected void createMenus()
    {
        setJMenuBar(appMenuBar);
    }

        /**
         * Notified when the status of a task has changed.
         */
    public void taskStatusChanged(Task task)
    {
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
    }

        /**
         * Kill of the current session.
         */
    protected void endCurrentSession()
    {
        java.util.List currViews = dbsControlPanel.getSelectedViews();

        Iterator vIter = currViews.iterator();

        while (vIter.hasNext())
        {
            DBView dbV = (DBView)vIter.next();
            DBViewFrame dbvf = (DBViewFrame)viewFrameMap.get(dbV);

            dbvf.setVisible(false);
            dbvf.dispose();

            ViewInfo vInfo = (ViewInfo)viewFrameMap.get(dbV);
            viewFrameMap.remove(dbV);
            appMenuBar.removeViewWindowMI(vInfo.windowMI);
            dbsControlPanel.detachView(dbV);
        }
        dbsControlPanel.closeCurrentSession();
    }

        /**
         * Tells that a view has changed.
         */
    public void viewChanged(DBView view)
    {
        if (searchPanel.getView() == view)
        {
            searchPanel.initSearch(view);
        }
    }

        /**
         * Selects the current view object that is to be brought to the
         * front.
         */
    public void selectView(DBView view)
    {
        ViewInfo vInfo = (ViewInfo)viewFrameMap.get(view);

        if (vInfo == null) return ;
        viewHolderPanel.setSelectedFrame(vInfo.dbViewFrame);
        vInfo.dbViewFrame.setVisible(true);
        vInfo.dbViewFrame.toFront();

            // now update the searchPanel here...
        searchPanel.initSearch(view);

        //vInfo.dbViewFrame.requestFocus();
    }

        /**
         * Holds info about a view.
         */
    class ViewInfo
    {
        DBView dbView;
        JRadioButtonMenuItem windowMI;
        DBViewFrame dbViewFrame;

        public ViewInfo(DBView dbV, JRadioButtonMenuItem jrbMI,
                        DBViewFrame dbvFrame)
        {
            dbView = dbV;
            windowMI = jrbMI;
            dbViewFrame = dbvFrame;
        }
    }

        /**
         * Exits the program.
         */
    public void exitProgram()
    {
        sendMessage("Exiting...");
        saveConfigurations(configFile);
        setVisible(false);
        dispose();
        Launcher.programQuit();
    }

        /**
         * Window Closing event handler.
         */
    public void windowClosing(WindowEvent e)
    {
        if (e.getSource() == this)
        {
            exitProgram();
        }
    }
    public void windowClosed(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }

        /**
         * Item event handler.
         */
    public void itemStateChanged(ItemEvent e)
    {
        Object src = e.getSource();
    }

        /**
         * Document event handler.
         */
    public void sendActionUpdate(int percent)
    {
    }

        /**
         * New document extracted from database or file.
         */
    public void serviceInfoExtracted()
    {
    }

        /**
         * Document event handler.
         */
    public void sendMessage(String mesg)
    {
        statusLabel.setText(mesg);
    }

        /**
         * Does the main work.
         */
    public static void realMain(String args[])
    {
        String configFile = null;
        if (args.length <= 0)
        {
            System.out.println("Warning: Configuration file not specified.");
        } else
        {
            configFile = args[0];
        }

            // set the look and feel
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        AppFrame svf = new AppFrame(configFile);

        if (SVUtils.resourceManager == null) SVUtils.resourceManager = svf;
    }

        /**
         * Main entry function.
         */
    public static void main(String args[])
    {
        Launcher launcher = new Launcher();

        launcher.launch("svtool.gui.AppFrame",
                        "realMain",
                        args,
                        args.length);
    }

        /**
         * Opens a file and returns it as an input stream.
         *
         * @param   fileName    The file to open.
         * @return  The inputstream from the file.
         *
         * @author  Sri Panyam
         */
    public InputStream  readFile(String fileName) throws Exception
    {
        return new FileInputStream(fileName);
    }

        /**
         * Opens an image file and returns the ImageIcon.
         *
         * @param   imageFileName   The image file to open.
         * @return  The ImageIcon stored in the file.
         */
    public ImageIcon getImageIcon(String imageName)
    {
        return (ImageIcon)iconMap.get(imageName);
    }

        /**
         * An internal frame for viewing DBView objects.
         */
    protected class DBViewFrame extends JInternalFrame
                                implements
                                    InternalFrameListener,
                                    ActionListener
    {
            /**
             * The DBView object that we are showing.
             */
        protected DBView dbView;

            /**
             * The associated menu item in teh window list which controls
             * the show and hide of this internal frame.
             */
        protected JRadioButtonMenuItem dbMenuItem = null;

            /**
             * The Button object that initiates the search.
             */
        protected JButton searchButton = new JButton("Search ...");

            /**
             * Constructor.
             */
        public DBViewFrame(String title, DBView dbView,
                           JRadioButtonMenuItem menuItem)
        {
            super(title, true, true, true, true);
            this.dbView = dbView;
            this.dbMenuItem = menuItem;

            dbMenuItem.addActionListener(this);

            JPanel southPanel = new JPanel();

            southPanel.add(searchButton);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add("Center", dbView);
            getContentPane().add("South", southPanel);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setResizable(true);
            addInternalFrameListener(this);
            searchButton.addActionListener(this);
        }

            /**
             * Does the layout of the only view object available.
             */
        /*public void doLayout()
        {
            Dimension s = getContentPane().getSize();
            Dimension ts = getSize();
            System.out.println("Internal Fame and normal size: " + s + 
                               ", " + getSize());
            dbView.setBounds(0, 0, ts.width, ts.height);
        }*/

            /**
             * Action event handler for the Menu item.
             */
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();
            if (src == dbMenuItem)
            {
                selectView(dbView);
                //setVisible(true);
                //toFront();
            } else if (src == searchButton)
            {
                doSearch(dbView);
            }
        }

            /**
             * Internal frace Activated Event Handler.
             * Invoked when an internal frame is activated. 
             */
        public void internalFrameActivated(InternalFrameEvent e) { }

            /**
             * Invoked when an internal frame has been closed. 
             */
        public void internalFrameClosed(InternalFrameEvent e) { }

           /**
            * Invoked when an internal frame is in the process of being
            * closed.
            */
        public void internalFrameClosing(InternalFrameEvent e)
        {
                // if this window is being closed...
            if (e.getSource() == this)
            {
                System.out.println("Window closing...");
                appMenuBar.removeViewWindowMI(dbMenuItem);
                dbsControlPanel.detachView(dbView);
                viewFrameMap.remove(dbView);
                setVisible(false);
                toBack();
                dispose();
            }
        }

            /**
             * Invoked when an internal frame is de-activated. 
             */
        public void internalFrameDeactivated(InternalFrameEvent e) { }

            /**
             * Invoked when an internal frame is de-iconified. 
             */
        public void internalFrameDeiconified(InternalFrameEvent e) { }

            /**
             * Invoked when an internal frame is iconified. 
             */
        public void internalFrameIconified(InternalFrameEvent e) {}

            /**
             * Invoked when a internal frame has been opened. 
             */
        public void internalFrameOpened(InternalFrameEvent e) {}
    }

        /**
         * The listener that shows the popupmenu when necessary.
         */
    protected class PopupListener extends MouseAdapter
    {
        protected JPopupMenu popupMenu;

        public PopupListener(JPopupMenu p)
        {
            popupMenu = p;
        }

        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

        /**
         * Default action object.
         */
    protected abstract class DefaultAction implements Action
    {
            /**
             * Sets the enabled state of the Action.
             */
        public void setEnabled(boolean b)
        {
        }

            /**
             * Adds a property change listener object.
             */
        public void addPropertyChangeListener(PropertyChangeListener pcl)
        {
        }

            /**
             * Tells if the action is enabled or not.
             */
        public boolean isEnabled()
        {
            return true;
        }

            /**
             * Removes a property change listener object.
             */
        public void removePropertyChangeListener(PropertyChangeListener pcl)
        {
        }

            /**
             * Sets the value of a given key.
             */
        public void putValue(String key, Object value)
        {
        }

            /**
             * Gets the value of a given key.
             */
        public Object getValue(String key)
        {
            return null;
        }
    }

        /**
         * The action object that changes the Look and Feel.
         */
    protected class LAFSelectorAction extends DefaultAction
    {
        private LookAndFeel laf = null;
        private String lafClassName = null;
        private String jarName = null;

            /**
             * Constructor.
             */
        public LAFSelectorAction(String className)
        {
            this(className, null);
        }

            /**
             * Constructor.
             */
        public LAFSelectorAction(String className, String jarName)
        {
            this.lafClassName = className;
            this.jarName = jarName;

            SVUtils.loadJarToPath(jarName);
        }

            /**
             * Get the look and feel associated with this menu item.
             */
        public LookAndFeel getLookAndFeel()
        {
            if (laf == null)
            {
                this.laf = (LookAndFeel)SVUtils.createInstance(lafClassName);
            }
            return laf;
        }

            /**
             * Action event handler.
             *
             * @param   ae  The action event object to handle.
             */
        public void actionPerformed(ActionEvent ae)
        {
            try
            {
                LookAndFeel laf = getLookAndFeel();

                if (laf == null) 
                {
                    sendMessage("Invalid L&F: " + lafClassName);
                    return ;
                }
                UIManager.setLookAndFeel(getLookAndFeel());

                SwingUtilities.updateComponentTreeUI(aboutDialog);
                SwingUtilities.updateComponentTreeUI(fileChooser);
                SwingUtilities.updateComponentTreeUI(connectDialog);
                SwingUtilities.updateComponentTreeUI(progressDialog);
                SwingUtilities.updateComponentTreeUI(AppFrame.this);
            }
            catch (Exception exc)
            {
                sendMessage("Error loading L&F: " + lafClassName);
            }
            return ;
        }
    }

        /**
         * An action for a adding a new View object to the current databse
         * session if it exists.
         */
    protected class AddViewAction extends DefaultAction
    {
            /**
             * Name of the view class to create.
             */
        protected String viewClassName;

            /**
             * The node that holds details about a view class.
             */
        protected Element viewNode;

            /**
             * Constructor.
             */
        public AddViewAction(String className, Element node)
        {
            this.viewClassName = className;
            this.viewNode = node;
        }

            /**
             * Action event handler.
             *
             * @param   ae  The action event object to handle.
             */
        public void actionPerformed(ActionEvent ae)
        {
            int dbIndex = dbsControlPanel.getSelectedDBIndex();
                
            if (dbIndex < 0) return ;

            Database dbSession = (Database)dbList.get(dbIndex);
            if (dbSession == null) return;

            DBView dbView =
                (DBView)SVUtils.createInstance(viewClassName);

            if (dbView == null) return;

            try
            {
                dbView.setDatabase(dbSession);
            } catch (Exception exc)
            {
                SVUtils.logException(exc);
                return ;
            }

                // Create the db view object and set its attributes
            String viewName = viewNode.getAttribute(XMLUtils.NAME_ATTRIB);
            String viewIcon = viewNode.getAttribute(XMLUtils.ICON_ATTRIB);
            ImageIcon icon = getImageIcon(viewIcon);
            dbView.setViewName(viewName == null ? viewClassName : viewName);
            dbView.setViewIcon(icon);
            dbView.setMediator(AppFrame.this);

                // create the menu item for this view...
            JRadioButtonMenuItem dbViewMenuItem =
                    new JRadioButtonMenuItem(dbView.getViewName());

            appMenuBar.addViewWindowMI(dbViewMenuItem);

                // create the view frame for the view object...
            DBViewFrame viewFrame =
                    new DBViewFrame(dbView.getViewName(),
                                    dbView,
                                    dbViewMenuItem);

            viewFrameMap.put(dbView,
                             new ViewInfo(dbView, dbViewMenuItem, viewFrame));

                // set the ICON
            viewFrame.setFrameIcon(icon);
            viewFrame.setVisible(true);
            viewFrame.toFront();
            try
            {
                viewFrame.setLocation(0, 0);
                viewFrame.setSize(viewHolderPanel.getSize());
            } catch (Exception pve)
            {
                SVUtils.logException(pve);
            }

            viewHolderPanel.add(viewFrame);

            dbsControlPanel.attachView(dbSession, dbView);

            dbViewList.add(dbView);
        }
    }

        /**
         * Application menu bar.
         */
    class AppMenuBar extends JMenuBar implements ActionListener
    {
        protected TextField serviceNames = new TextField();

        protected JMenu databaseMenu = new JMenu("Database");
        protected JMenuItem newConnectionMI = new JMenuItem("New Connection...");
        protected JMenuItem endConnectionMI = new JMenuItem("End Connection");
        protected JMenuItem endAllConnectionsMI =
                    new JMenuItem("End all Connections");

        protected JMenuItem saveMI = new JMenuItem("Save to File (TODO)...");
        protected JMenuItem readMI = new JMenuItem("Read from File (TODO)...");
        protected JMenuItem exitMI = new JMenuItem("Exit");

        protected JMenu optionsMenu = new JMenu("Options");

        protected JMenu windowMenu = new JMenu("Window");
        protected JMenuItem tileHorizontallMI =
                    new JMenuItem("Tile Horizontally (TODO)");
        protected JMenuItem tileVerticallyMI =
                    new JMenuItem("Tile Vertically (TODO)");
        protected JMenuItem cascadeMI = new JMenuItem("Cascade (TODO)");

        protected JMenuItem arrangeAllMI = new JMenuItem("Arrange All (TODO)");
        protected JMenuItem windowListMenu = new JMenu("Window List");
        protected ButtonGroup windowListGroup = new ButtonGroup();

        protected JMenu helpMenu = new JMenu("Help");
        protected JMenuItem aboutMI = new JMenuItem("About...");

        protected JMenuItem lafMenu = new JMenu("Look and Feel");
        protected ButtonGroup lafGroup = new ButtonGroup();

            /**
             * Constructor.
             */
        public AppMenuBar()
        {
            super();

                // create the menus
            databaseMenu.getAccessibleContext().setAccessibleDescription(
            "This menu provides access to the database.");
            newConnectionMI.getAccessibleContext().setAccessibleDescription(
            "Connects to the nominated database.");
            exitMI.getAccessibleContext().setAccessibleDescription(
            "Exits the program.");

            databaseMenu.add(newConnectionMI);
            databaseMenu.add(endConnectionMI);
            databaseMenu.add(endAllConnectionsMI);
            databaseMenu.addSeparator();
            databaseMenu.add(saveMI);
            saveMI.setEnabled(false);
            databaseMenu.add(readMI);
            readMI.setEnabled(false);
            databaseMenu.addSeparator();
            databaseMenu.add(exitMI);

            windowMenu.add(tileHorizontallMI);
            windowMenu.add(tileVerticallyMI);
            windowMenu.add(cascadeMI);
            windowMenu.add(arrangeAllMI);
            windowMenu.addSeparator();
            windowMenu.add(windowListMenu);

            optionsMenu.addSeparator();
            optionsMenu.add(lafMenu);

                // now create the look and feel menus
            helpMenu.add(aboutMI);

            add(databaseMenu);
            add(optionsMenu);
            add(windowMenu);
            add(helpMenu);

                    // set lsiteners.
            newConnectionMI.addActionListener(this);
            endConnectionMI.addActionListener(this);
            endAllConnectionsMI.addActionListener(this);

            saveMI.addActionListener(this);
            readMI.addActionListener(this);
            exitMI.addActionListener(this);
            aboutMI.addActionListener(this);

            tileHorizontallMI.addActionListener(this);
            tileVerticallyMI.addActionListener(this);
            cascadeMI.addActionListener(this);
            arrangeAllMI.addActionListener(this);
            windowListMenu.addActionListener(this);
        }

            /**
             * Adds a new LAF MenuItem.
             */
        public void addLAFMenuItem(String menuLabel,
                                   String className,
                                   String jarName)
        {
            JRadioButtonMenuItem lafMI =
                    new JRadioButtonMenuItem(
                            new LAFSelectorAction(className, jarName));
            lafMI.setText(menuLabel);
            lafGroup.add(lafMI);
            lafMenu.add(lafMI);
        }

            /**
             * Adds a new window to the view window menu item.
             */
        public void addViewWindowMI(JMenuItem mi)
        {
            windowListGroup.add(mi);
            windowListMenu.add(mi);
        }

            /**
             * Removes a menu item from the "WindowList" menu
             */
        public void removeViewWindowMI(JMenuItem mi)
        {
            windowListMenu.remove(mi);
            windowListGroup.remove(mi);
        }

            /**
             * Action event handler.
             */
        public void actionPerformed(ActionEvent e)
        {
            Object src = e.getSource();

            if (src == newConnectionMI)
            {
                statusLabel.setText("");
                connectDialog.setVisible(true);
                if (connectDialog.wasCancelled()) return ;

                    // if connection dialog wasnt cancelled...
                String dbName = connectDialog.getDatabaseName();
                String login = connectDialog.getLogin();
                String pwd = connectDialog.getPassword();

                String stLabel = "Connecting to: " +
                                login + "@" + dbName + "...";
                sendMessage(stLabel);

                    // now do a new connection here!!!
                Database currDatabase = new Database();

                try
                {
                    currDatabase.setParameters(dbName, login, pwd);
                } catch (Exception exc)
                {
                    SVUtils.logException(exc);
                    return ;
                }
                dbConnector.setDatabase(currDatabase);

                progressDialog.startAndShowTask(dbConnector,
                                                "Connecting to Database",
                                                true);

                if (progressDialog.wasCancelled()) return ;

                Object res = dbConnector.getResult();
                System.out.println("Here: res = " + res);
                if (res instanceof Exception)
                {
                    //exc.printStackTrace();
                    statusLabel.setText("Connection failed: " +
                                    ((Exception)res).getMessage());
                } else
                {
                    setTitle(SVVersion.getApplicationFrameTitle() + 
                             " --------- " + 
                             currDatabase.getLogin() + "@"  +
                             currDatabase.getHost());

                    sendMessage("Connected to database: " +
                                currDatabase.getLogin() + "@" +
                                currDatabase.getHost());

                        // now add the db to the session list...
                    dbList.add(currDatabase);
                    int ind = dbsControlPanel.addDBSession(currDatabase);
                    if (ind >= 0)
                    {
                        JList list = dbsControlPanel.getViewJList(ind);
                        list.addMouseListener(popupListener);
                    }
                }
            } else if (src == endConnectionMI)
            {
                endCurrentSession();
            } else if (src == endAllConnectionsMI)
            {
                while (dbsControlPanel.getSessionCount() > 0)
                {
                    endCurrentSession();
                }
            } else if (src == saveMI)
            {
            } else if (src == aboutMI)
            {
                aboutDialog.setVisible(true);
            } else if (src == exitMI)
            {
                exitProgram();
            }
        }
    }
}
