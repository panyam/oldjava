package svtool.gui.views;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
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

/**
 * This panel is used to show the comparison results of the DM01 table and
 * the service-data tables.
 */
public class NetworkDiscoveryView extends DBView
                              implements
                                ListSelectionListener,
                                ComponentListener,
                                ItemListener,
                                ActionListener
{

        /**
         * The network data that this panel will be displaying.
         */
    protected NetworkData networkData = new NetworkData();

    protected boolean autoArrangeFrames = false;
    protected boolean autoMatch = true;

        /**
         * The button to refresh the DM01 table.
         */
    JButton refreshDMButton = new JButton("Refresh DM01 View");
    JButton refreshSDButton = new JButton("Refresh Service Data View");
    JButton compareButton = new JButton("Compare Tables");
    JButton autoArrangeButton = new JButton("Auto Arrange");
    JToggleButton autoMatchButton = new JToggleButton("Auto Match", autoMatch);

        /**
         * Model and table for showing the data from the dm01
         * table.
         */
    protected DM01TableModel dm01Model = new DM01TableModel(networkData);
    protected JTable dm01Table = new JTable(dm01Model);

    JDesktopPane desktopPane = new JDesktopPane();
    JInternalFrame dm01Frame = new JInternalFrame("DM01 Table", true,
                                                        false, true, true);
    JInternalFrame svdataFrame = new JInternalFrame("Service Data Table", true,
                                                        false, true, true);
    JInternalFrame resultsFrame = new JInternalFrame("Comparison Results", true,
                                                        false, true, true);

    protected JLabel svdataStatusBar = new JLabel("-");
    protected JLabel dm01StatusBar = new JLabel("-");

        /**
         * Selection models for the three tables.
         */
    protected ListSelectionModel compResultSM, dm01SM, sdataSM;

        /**
         * Model and table for showing the data from the service_data
         * table.
         */
    protected ServiceDataTableModel sdataModel =
                new ServiceDataTableModel(networkData);

    protected JTable sdataTable = new JTable(sdataModel);

        /**
         * Model and table for showing the results of the DM01 and Service
         * data table comparison.
         */
    protected CompResultTableModel compModel = new CompResultTableModel();
    protected JTable compResultTable= new JTable(compModel);

        /**
         * Constructor.
         */
    public NetworkDiscoveryView()
    {
        super();
        setLayout(new BorderLayout());

        //setDatabase(dbase);
        setListeners();
        layComponents();
    }

        /**
         * Initial layout of the components.
         */
    protected void layComponents()
    {
        JPanel topPanel = new JPanel();
        topPanel.add(refreshDMButton);
        topPanel.add(refreshSDButton);
        topPanel.add(compareButton);
        topPanel.add(autoMatchButton);
        topPanel.add(autoArrangeButton);

        JScrollPane compScroller = new JScrollPane(compResultTable);
        compResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        compResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        compResultSM = compResultTable.getSelectionModel();
        compResultSM.addListSelectionListener(this);

        //resultsFrame.getContentPane().setLayout(new BorderLayout());
        //dm01Frame.getContentPane().add("Center", compScroller);

        JScrollPane dm01Scroller = new JScrollPane(dm01Table);
        dm01Table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dm01Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dm01SM = dm01Table.getSelectionModel();
        dm01SM.addListSelectionListener(this);
        dm01Frame.getContentPane().setLayout(new BorderLayout());
        dm01Frame.getContentPane().add("Center", dm01Scroller);
        dm01Frame.getContentPane().add("South", dm01StatusBar);

        JScrollPane svdataScroller = new JScrollPane(sdataTable);
        sdataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sdataSM = sdataTable.getSelectionModel();
        sdataSM.addListSelectionListener(this);
        sdataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        svdataFrame.getContentPane().setLayout(new BorderLayout());
        svdataFrame.getContentPane().add("Center", svdataScroller);
        svdataFrame.getContentPane().add("South", svdataStatusBar);

        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        //desktopPane.setLayout(new GridLayout(1, 2));
        desktopPane.add(dm01Frame);
        desktopPane.add(svdataFrame);
        //desktopPane.add(resultsFrame);

        dm01Frame.setVisible(true);
        dm01Frame.setResizable(true);
        dm01Frame.pack();

        resultsFrame.setVisible(true);
        resultsFrame.setResizable(true);
        resultsFrame.pack();

        svdataFrame.setVisible(true);
        svdataFrame.setResizable(true);
        svdataFrame.pack();

        JSplitPane mainSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                        desktopPane, compScroller);

        add("North", topPanel);
        //add("Center", desktopPane);
        add("Center", mainSplitter);
        mainSplitter.setDividerLocation(600);
    }

        /**
         * Set the event listeners.
         */
    protected void setListeners()
    {
        refreshDMButton.addActionListener(this);
        refreshSDButton.addActionListener(this);
        compareButton.addActionListener(this);
        autoMatchButton.addActionListener(this);
        autoArrangeButton.addActionListener(this);
        dm01Model.addTableModelListener(dm01Table);
        compModel.addTableModelListener(compResultTable);
        sdataModel.addTableModelListener(sdataTable);
        compResultTable.addComponentListener(this);
    }

        /**
         * Auto arranges the internal frames.
         */
    protected void autoArrangeInternalFrames()
    {
        Dimension aSize = desktopPane.getSize();
        int resHeight = Math.min((int)(aSize.height * 0.3), 150);
        int halfW = aSize.width / 2;
        dm01Frame.setBounds(0, 0, halfW, aSize.height);
        svdataFrame.setBounds(halfW, 0, halfW, aSize.height);
        //dm01Frame.setVisible(true);
        //svdataFrame.setVisible(true);
        //resultsFrame.setVisible(true);
    }

        /**
         * Tells when the value in the comparison result table has changed
         * selection.
         */
    public void valueChanged(ListSelectionEvent e)  
    {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            // now select the appropriate valeus from each table
            // and select them
        int selRow = lsm.getMinSelectionIndex();
        if (selRow < 0) return ;

        setStatusMessage(svdataStatusBar, "-");
        setStatusMessage(dm01StatusBar, "-");
        if (lsm == compResultSM)
        {
            ComparisonResult compResult = compModel.getResult(selRow);

            if (compResult.dmIndex >= 0)
            {
                dm01Table.changeSelection(compResult.dmIndex, 0, false, false);
            }
            if (compResult.sdIndex >= 0)
            {
                sdataTable.changeSelection(compResult.sdIndex, 0, false, false);
            }
        } else if (lsm == dm01SM)
        {
            if ( ! autoMatch) return ;
            DeviceInfo dmDev = dm01Model.getDevice(selRow);
                // find the appriate device in the "other" table
            int ind = sdataModel.findDevice(dmDev);
            if (ind < 0)
            {
                setStatusMessage(svdataStatusBar, dmDev.attribs[DeviceInfo.DEVICE_NAME_ATTR] + " not found in Service Data table.");
            } else
            {
                sdataTable.changeSelection(ind, 0, false, false);
            }
        } else if (lsm == sdataSM)
        {
            if ( ! autoMatch) return ;
            DeviceInfo sdDev = sdataModel.getDevice(selRow);
                // find the appriate device in the "other" table
            int ind = dm01Model.findDevice(sdDev);
            if (ind < 0)
            {
                setStatusMessage(dm01StatusBar,
                        sdDev.attribs[DeviceInfo.DEVICE_NAME_ATTR] +
                        " not found in DM01 table.");
            } else
            {
                dm01Table.changeSelection(ind, 0, false, false);
            }
        }
    }

        /**
         * Component Moved event handler.
         */
    public void componentMoved(ComponentEvent ce) { }

        /**
         * Component Hidden event handler.
         */
    public void componentHidden(ComponentEvent ce) { }

        /**
         * Component Shown event handler.
         */
    public void componentShown(ComponentEvent ce) { }

        /**
         * Component Resized event handler.
         */
    public void componentResized(ComponentEvent ce)
    {
        if (ce.getSource() == compResultTable)
        {
            //System.out.println("Auto Resize = " + autoArrangeFrames);
            if (autoArrangeFrames) autoArrangeInternalFrames();
        }
    }

        /**
         * Item Event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src == refreshDMButton)
        {
            //compareButton.setEnabled(false);
            refreshDMData();
            appMediator.viewChanged(this);
        } else if (src == refreshSDButton)
        {
            //compareButton.setEnabled(false);
            refreshSDData();
            appMediator.viewChanged(this);
        } else if (src == compareButton)
        {
            compModel.startComparisons();
            appMediator.viewChanged(this);
        } else if (src == autoArrangeButton)
        {
            autoArrangeInternalFrames();
        } else if (src == autoMatchButton)
        {
            autoMatch = autoMatchButton.isSelected();
        }
    }

        /**
         * Refreshes the data from the DM01 table.
         */
    public void refreshDMData()
    {
        if (currDatabase == null ||
                currDatabase.getConnection() == null) 
        {
            networkData.fireDBDataEvent("Database not connected.");
            return ;
        }
        dm01Model.refreshData();
    }

        /**
         * Refresh the data from the service data table.
         */
    public void refreshSDData()
    {
        if (currDatabase == null ||
                currDatabase.getConnection() == null) 
        {
            networkData.fireDBDataEvent("Database not connected.");
            return ;
        }
        sdataModel.refreshData();
    }

        /**
         * Set the message in a specified status bar.
         */
    protected void setStatusMessage(JLabel statusBar, String mesg)
    {
        statusBar.setText(mesg);
    }


        /**
         * A comparison result of devices found in two different tables.
         */
    class ComparisonResult
    {
        String deviceName;
            /**
             * The following are indices of the device (indicated by
             * deviceName) in the dm01 and sdata table.
             *
             * At most one of these two indices can be negative.
             * If they are both positive then it means that the device
             * exists in both tables but differs in value.
             *
             * If one of them is negative it means that it only exists in
             * one table and not in the other!!
             */
        int dmIndex;    // the index of this device in the dm01 table.
        int sdIndex;    // the index of this device in the sdata table.

            /**
             * The text to describe the result.
             */
        String resultText;
    }

        /**
         * The table with all the results.
         */
    public class CompResultTableModel extends ThreadedTableModel
    {
        java.util.List compResults = new ArrayList();
        String columnNames[] = new String[]
        {
            "Device", "DM Table Index", "SD Table Index", "Message"
        };

            /**
             * Starts the comparisons of the two tables.
             */
        public synchronized void startComparisons()
        {
            threadStopped = true;

            while (threadRunning)
            {
                try
                {
                    wait(100);
                } catch (Exception exc) { }
            }
            threadStopped = false;
            threadRunning = true;
            ourThread =new Thread(this);
            ourThread.start();
        }

            /**
             * Get the comparison result at the selected row.
             */
        public ComparisonResult getResult(int row)
        {
            return (ComparisonResult)compResults.get(row);
        }

            /**
             * Gets the value in a certain row.
             */
        public Object getValueAt(int row, int col)
        {
            ComparisonResult cResult =
                (ComparisonResult)compResults.get(row);
            switch (col)
            {
                case 0: return cResult.deviceName;
                case 1:
                    return cResult.dmIndex < 0 ? "-" : cResult.dmIndex + "";
                case 2:
                    return cResult.sdIndex < 0 ? "-" : cResult.sdIndex + "";
                case 3: return cResult.resultText;
            }
            return null;
        }

            /**
             * Stop the comparisons.
             */
        public synchronized void stopComparisons()
        {
            threadStopped = true;

            while (threadRunning)
            {
                try
                {
                    wait(100);
                } catch (Exception exc) { }
            }
            threadStopped = false;
        }

            /**
             * Get the name of a given column.
             */
        public String getColumnName(int col)
        {
            return columnNames[col];
        }

            /**
             * Returns the number of rows. 
             */
        public int getRowCount()
        {
            return compResults.size();
        }

            /**
             * Returns the number of columns 
             */
        public int getColumnCount()
        {
            return columnNames.length;
        }

            /**
             * Thread run method.
             */
        public void run()
        {
            int dmPos = 0;
            int sdPos = 0;
            int prevDM = -1;
            int prevSD = -1;
            int nDM = dm01Model.getRowCount();
            int nSD = sdataModel.getRowCount();
            int nErrors = 0;

            DeviceInfo currDM = null, currSD = null;

            threadStopped = false;
            threadRunning = true;

            compResults.clear();

                // Since both tables are sorted, basically go through the
                // tables in a sorted manner and get the items.
            while (dmPos < nDM && sdPos < nSD)
            {
                    // dont compare duplicate devices.
                    // eg, dm01 table has duplicate entries

                    // get the current element from each table...
                if (prevDM != dmPos) currDM = dm01Model.getDevice(dmPos);
                if (prevSD != sdPos) currSD = sdataModel.getDevice(sdPos);

                    // first check if the DM value has vlanID and
                    // protNumber as not null
                if (currDM.attribs[DeviceInfo.VLAN_ID_ATTR].length() == 0 ||
                    currDM.attribs[DeviceInfo.PORT_NUMBER_ATTR].length() == 0)
                {
                    ComparisonResult cResult = new ComparisonResult();
                    cResult.deviceName = 
                            currDM.attribs[DeviceInfo.DEVICE_NAME_ATTR];
                    cResult.dmIndex = dmPos;
                    cResult.sdIndex = -1;
                    cResult.resultText =
                        "VLAN_ID (" +
                        currDM.attribs[DeviceInfo.VLAN_ID_ATTR] +
                        ") and/or Port Number (" +
                        currDM.attribs[DeviceInfo.PORT_NUMBER_ATTR] +
                        ") for " + 
                        currDM.attribs[DeviceInfo.DEVICE_NAME_ATTR] +
                        " was not discovered.  Port may be shutdown";
                    compResults.add(cResult);
                    dmPos ++;
                } else
                {
                    int comp = currDM.compareTo(currSD);
                    if (comp == 0)
                    {
                        prevSD = sdPos;
                        prevDM = dmPos;
                        sdPos ++;
                        dmPos ++;
                    } else if (comp < 0)
                    {
                        ComparisonResult cResult = new ComparisonResult();
                        cResult.deviceName =
                            currDM.attribs[DeviceInfo.DEVICE_NAME_ATTR];
                        cResult.dmIndex = dmPos;
                        cResult.sdIndex = -1;
                        cResult.resultText = 
                            currDM.attribs[DeviceInfo.DEVICE_NAME_ATTR] +
                            " not found in Service Data Table.";
                        prevDM = dmPos;
                        dmPos++;
                        compResults.add(cResult);
                    } else
                    {
                        ComparisonResult cResult = new ComparisonResult();
                        cResult.deviceName =
                            currSD.attribs[DeviceInfo.DEVICE_NAME_ATTR];
                        cResult.dmIndex = -1;
                        cResult.sdIndex = sdPos;
                        cResult.resultText = 
                            currSD.attribs[DeviceInfo.DEVICE_NAME_ATTR] +
                            " not found in DM01 Table.";
                        prevSD = sdPos;
                        sdPos++;
                        compResults.add(cResult);
                    }
                }
            }

            //setStatusMessage(svdataStatusBar,
                        //"Read " + devList.size() + " devices' info.");

            fireTableDataChanged();
            threadRunning = false;
            threadStopped = false;
            doNotify();
        }
    }

        /**
         * The table model for the DM01 and SD tables.
         *
         * Holds information about the devices that were found by the
         * Network Discovery process and also for all devices in the
         * Service Data table.
         *
         * @author Sri Panyam
         */
    public abstract class DeviceInfoTableModel extends ThreadedTableModel
        implements Runnable
    {
        protected NetworkData networkData;
        protected java.util.List devList = null;
        protected int tableID = -1;

            /**
             * Constructor.
             */
        public DeviceInfoTableModel(NetworkData networkData, String tableName)
        {
            this.networkData = networkData;
            tableID = networkData.addTable(tableName);
            devList = networkData.getDeviceList(tableName);
        }

            /**
             * Returns the device at a given index.
             *
             * @param   index   The index of the device to obtain.
             */
        public DeviceInfo getDevice(int index)
        {
            return (DeviceInfo)devList.get(index);
        }

            /**
             * Get the name of a given column.
             */
        public String getColumnName(int col)
        {
            return NetworkData.getColumnName(col);
        }

            /**
             * Returns the number of rows. 
             */
        public int getRowCount()
        {
            return devList.size();
        }

            /**
             * Returns the number of columns 
             */
        public int getColumnCount()
        {
            return NetworkData.getColumnCount();
        }

            /**
             * Gets the value at a givenlocation.
             */
        public Object getValueAt(int row, int col)
        {
            try
            {
                DeviceInfo dev = (DeviceInfo)devList.get(row);
                return dev.attribs[col];
            } catch (Exception ex)
            {
            }
            return null;
        }

            /**
             * Refresh the data in this list.
             */
        public abstract void refreshData();

            /**
             * fidn the index of a given device.
             */
        protected int findDevice(DeviceInfo dev)
        {
            int lo = 0, hi = devList.size() - 1, mid = -1;

            while (lo < hi)
            {
                mid = (lo + hi) / 2;
                int comp = dev.compareTo(getDevice(mid));

                if (comp == 0)
                {
                    return mid;
                } else if (comp < 0)
                {
                    if (mid == lo) return -1;
                    hi = mid - 1;
                } else
                {
                    lo = mid + 1;
                }
            }

            //System.out.println("Here lo, hi = " + lo + ", " + hi);

            if (hi == lo && 
                dev.compareTo(getDevice(lo)) == 0)
            {
                return lo;
            }

            return -1;
        }
    }


        /**
         * Data model for the DM01 Data.
         *
         * Holds information in the DM01 table which is populated by
         * Network Discovery elements in the EpiDM network.
         *
         * @author Sri Panyam
         */
    public class DM01TableModel extends DeviceInfoTableModel
    {
            /**
             * Constructor.
             */
        public DM01TableModel(NetworkData netData)
        {
            super(netData, "DM01");
        }

            /**
             * Thread run method.
             */
        public void run()
        {
            try
            {
                String query =  "select ";
                for (int i = 0, nCols = NetworkData.getColumnCount();i < nCols;i++)
                {
                    if (i > 0) query += ", ";
                    query += NetworkData.getColumnName(i);
                }
                query +=   (" from CSOM_Admin.DM01_DATA " +
                            "order by device_name, vlan_id, port_nbr");

                Statement stmt =
                    currDatabase.getConnection().createStatement();
                ResultSet rset = stmt.executeQuery(query);

                    // remove all rows...
                    // should this be done???
                devList.clear();

                    // put it in this table...
                while (!threadStopped && threadRunning && rset.next())
                {
                    DeviceInfo newDev = new DeviceInfo();

                    for (int i = 0, nCols = NetworkData.getColumnCount();
                            i < nCols;i++)
                    {
                        newDev.setAttribute(DeviceInfo.wipAtCodes[i],
                                    rset.getString(NetworkData.getColumnName(i)));
                        setStatusMessage(dm01StatusBar,
                                ("Adding Device: " +
                                newDev.attribs[DeviceInfo.DEVICE_NAME_ATTR]));
                    }

                    if (devList.isEmpty() || devList.size() % 100 == 0)
                    {
                        fireTableDataChanged();
                    }
                    int pos = networkData.insertDevice(tableID, newDev);

                    //devList.addElement(newDev);
                }
                setStatusMessage(dm01StatusBar,
                            "Read " + devList.size() + " entries.");
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
            threadStopped = false;
            threadRunning = false;
            doNotify();
        }

            /**
             * Refresh the data from the DM01 table.
             */
        public synchronized void refreshData()
        {
            threadStopped = true;

            while (threadRunning)
            {
                try
                {
                    wait(100);
                } catch (Exception exc) { }
            }
            threadStopped = false;
            threadRunning = true;
            ourThread =new Thread(this);
            ourThread.start();
        }
    }

        /**
         * The model used to show info about a device that 
         * was found from the service data table.
         *
         * This is the information that has actually been entered.
         *
         * @author Sri Panyam
         */
    public class ServiceDataTableModel extends DeviceInfoTableModel
    {
        String attrList =   "(2051, 2000, 2090, 2001, 2052, 2053, 2081, " +
                            "3140, 3142, 3028, 3012, 3141, 3011, 3001)";
        String devAtCode = "(2051, 3140)";

            /**
             * A class for holding the "interesting" values in a service
             * structure instant object.
             * Basically its a list of "attrib, value" pairs.
             *
             * This can be one of the following:
             *
             * OverAll access Object: Only vtp domain is stored.
             * VLAN Object: VLANID, VLANIDEXT and VLANNAME are stored.
             * Port Object: port name, port number and device name are
             *              stored.
             *
             * Attributes stored are:
             *  VLANID or Port ID: index = 0 
             *  VLAN FNN or Port FNN: index = 1
             *  vtpDomain or DeviceName: index = 2
             */
        class SDStructure
        {
            int structInstID;
            int parentInstID;
            int structureID;
            int parentRow;
            int serviceID;

            String values[] = new String[3];
            int attrCodes[] = new int[3];

                /**
                 * Constructor.
                 */
            public SDStructure()
            {
                values[0] = values[1] = values[2] = null;
                attrCodes[0] = attrCodes[1] = attrCodes[2] = -1;
            }

                /**
                 * Prints this structure out.
                 */
            public void print()
            {
                System.out.println(structInstID + ", " +
                                   parentInstID + ", " +
                                   structureID + ", " +
                                   serviceID + ", " +
                                   "(" + attrCodes[0] + "," + values[0] + "), " +
                                   "(" + attrCodes[1] + "," + values[1] + "), " +
                                   "(" + attrCodes[2] + "," + values[2] + ") ");
            }

                /**
                 * Sets specific values:
                 */
            public void setAttribute(int attr, String value)
            {
                if (value == null) value = "";
                switch (attr)
                {
                    // VLAN Number/ID 
                    case 3011: case 2000: case 2090: case 3028:
                    // PORT Number
                    case 3142: case 2053:
                        values[0] = value;  attrCodes[0] = attr; return ;
                    // VLAN FNN
                    case 3012: case 2001: 
                    //  or PORT FNN
                    case 3141: case 2052:
                        values[1] = value;  attrCodes[1] = attr; return ;
                    // Device NAME 
                    case 3140: case 2051: 
                    //  or VTP Domain
                    case 3001: case 2081:
                        values[2] = value;  attrCodes[2] = attr; return ;
                }
            }

                /**
                 * Given an attribute, returns the string value.
                 */
            public String getAttribute(int attr)
            {
                if (attrCodes[0] == attr) return values[0];
                else if (attrCodes[1] == attr) return values[1];
                else if (attrCodes[2] == attr) return values[2];
                return null;
            }
        }

            /**
             * Number of structure instant ids found.
             */
        int nStructInsts = 0;

            /**
             * The table that holds all the structures.
             */
        protected SDStructure structTable[]  = null;

            /**
             * Tells what resides in each column of the table.
             */
        protected final static int STRUCT_ID_COLUMN = 0;
        protected final static int PARENT_ID_COLUMN = 1;
        protected final static int STRUCTURE_ID_COLUMN = 2;
        protected final static int SERVICE_ID_COLUMN = 3;
        protected final static int PARENT_ROW_COLUMN = 4;

            /**
             * Constructor.
             */
        public ServiceDataTableModel(NetworkData netData)
        {
            super(netData, "Service Data");
        }

            /**
             * Find the row index where the given structInstID resides.
             */
        protected int findStructInstantIndex(int structInstID)
        {
            int lo = 0, hi = nStructInsts - 1, mid = -1;

            while (lo < hi)
            {
                mid = (lo + hi) / 2;

                if (structInstID == structTable[mid].structInstID)
                {
                    return mid;
                } else if (structInstID < structTable[mid].structInstID)
                {
                    if (mid == lo) return -1;
                    hi = mid - 1;
                } else
                {
                    lo = mid + 1;
                }
            }

            //System.out.println("Here lo, hi = " + lo + ", " + hi);

            if (hi == lo && 
                structInstID == structTable[lo].structInstID) 
            {
                return lo;
            }

            return -1;
        }

            /**
             * Obtain all the structure instant IDs from
             * the databse.
             *
             * A structure is a tuple of:
             * (struct_instant_id, parent_inst_id, structure_type_id,
             * service_id)
             *
             * The idea is that since they are already sorted (by
             * instant_id but any other ordering is also valid), the data
             * structure for storing them needs to allow easy searches as
             * each device that is found will be checked against these.
             *
             * So for example when a device is found in the DM01 table, it
             * has to be validated with the corresponding entry in this
             * table.  Which means that the device's structure instant and
             * all its parents have to be matched with whats in this table.
             * Hence a struct_instant_id has to be mapped to an index in
             * constant time.  At the moment, since the table is sorted, a
             * search can be made in nlog(n) time.
             */
        protected void obtainStructureInstantIDs()
            throws Exception
        {
            final String structureQuery = 
                "select  struct_instant_id, parent_Struct_inst_id, " + 
                "           structure_id, service_id, max(order_id) " +
                "   from CSOM_ADMIN.service_structure " + 
                "   where structure_id " + 
                "       in (1405, 1420, 1450, 1470, 1302, 1305, 1315, 1330)" +
                "       and status <> 16" +
                "   group by service_id, struct_instant_id, " +
                "       parent_struct_inst_id, structure_id " +
                "   order by struct_instant_id, structure_id";

            nStructInsts = 0;
            int structCapacity = 0;

            Connection conn = currDatabase.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet countSet = stmt.executeQuery(
                    "select count(struct_instant_id) from (" +
                                structureQuery + ")");

            while (countSet.next())
            {
                structCapacity = Integer.parseInt(countSet.getString(1));
            }
            stmt.close();

                // ensure there is enough capacity in the table.
            if (structTable == null)
                structTable = new SDStructure[structCapacity + 1];

            if (structTable.length < structCapacity)
            {
                SDStructure s2[] = structTable;
                structTable = new SDStructure[structCapacity + 1];
                System.arraycopy(s2, 0, structTable, 0, s2.length);
            }

                // reset all values to 0
            for (int i = 0;i < structTable.length;i++)
            {
                if (structTable[i] == null) structTable[i] = new SDStructure();
            }

                // now do another query this time for 
                // the struct instants themselves...
            stmt = conn.createStatement();

            ResultSet structSet = stmt.executeQuery(structureQuery);
            int tenP = (int)(0.1 * structCapacity);

                // structSet is a list of (struct_inst_id,
                // structure_id, parent_struct_inst_id, service_id and
                // service_id)  Duplicate instants id will be replaced
                // with the ones with a higher order_id
            nStructInsts = 0;
            while (!threadStopped && threadRunning && structSet.next())
            {
                structTable[nStructInsts].structInstID = 
                    Integer.parseInt(structSet.getString("STRUCT_INSTANT_ID"));
                structTable[nStructInsts].parentInstID = 
                    Integer.parseInt(
                            structSet.getString("PARENT_STRUCT_INST_ID"));
                structTable[nStructInsts].structureID = 
                    Integer.parseInt(
                            structSet.getString("STRUCTURE_ID"));
                structTable[nStructInsts].serviceID = 
                    Integer.parseInt(
                            structSet.getString("SERVICE_ID"));
                structTable[nStructInsts].parentRow = -1;

                if (nStructInsts % tenP == 0)
                {
                    setStatusMessage(svdataStatusBar,
                            "Extracted: " +
                            nStructInsts + " / " + structCapacity +
                            " structures.");
                }

                nStructInsts++;
            }
            System.out.println("Capacity = " + structCapacity);

            setStatusMessage(svdataStatusBar,
                    "Completed Struct InstantID Extraction: " +
                    nStructInsts + " unique struct instances.");

            stmt.close();

                // another optimisation is applied here:
                // Each row in the structTable consists of structInstID and
                // the parentInstID.  instead of storing the parentInstID,
                // why not also store the index in the table where the
                // parentInstID resides?  If the parent does not exist in
                // the table then it will have a row Index of -1  but this
                // does not matter at all..
            for (int i = 0;i < nStructInsts;i++)
            {
                structTable[i].parentRow =
                    findStructInstantIndex(structTable[i].parentInstID);
            }
        }

            /**
             * Obtain all values for each extracted structure instant ID
             * and further populate the table with values.  For example, at
             * the moment, the structTable contains only struct instant ids
             * and their corresponding parent instant ids and structure
             * ids.
             *
             * Now go through the database but this time get the instant
             * information for relevatn attribtues (eg 3140, 2051 etc)
             * resident in these instances.
             */
        protected void obtainStructureInstantValues()
            throws Exception
        {
            final String queryString = 
                "select struct_instant_id, " +
                "       structure_attrib_id, attribute_value, max(order_id)" + 
                "   from CSOM_ADMIN.service_data" + 
                "   where structure_attrib_id in  " + 
                "       (3140, 3142, 3011, 3012, 3141, 3001, " +
                "       2051, 2000, 2001, 2052, 2053, 2081, 3028, 2090)" +
                "   group by struct_instant_id, " +
                "       structure_attrib_id, attribute_value" + 
                "   order by struct_instant_id, " +
                "       structure_attrib_id, attribute_value";

            Connection conn = currDatabase.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(queryString);

            int currRow = 0;
            SDStructure currStruct = null;
            int prevStructInstID = -1;
            int currStructInstID = -1;
            String stInstID = null;

            while (!threadStopped && threadRunning && rset.next())
            {
                // at this point, the rset queries and structTable
                // are both sorted/ordered by struct_instant_ids
                // Where the structTable has one structinstant PER 
                // row, the rset result set will have one
                // attribute per line.  So you would have multiple
                // instantids rows in the rset each one with a new
                // attribute id/value.
                prevStructInstID = currStructInstID;

                currStructInstID =
                    Integer.parseInt(rset.getString("STRUCT_INSTANT_ID"));


                if (currStructInstID != prevStructInstID)
                {
                        // get currRow to point to 
                        // the current row
                    while (structTable[currRow].structInstID <
                                currStructInstID) currRow++;

                    setStatusMessage(svdataStatusBar,
                            "Populating Structure Instance: " +
                            currStructInstID);
                }
                int attr = Integer.parseInt(
                        rset.getString("STRUCTURE_ATTRIB_ID"));
                String attrVal = rset.getString("ATTRIBUTE_VALUE");

                //System.out.println("stID, currID, row, Attr, val = " + structTable[currRow].structInstID + ", " + currStructInstID + ", " + currRow + ", " + attr + ", " + attrVal);
                structTable[currRow].setAttribute(attr, attrVal);
            }
            stmt.close();

                // print all the valeus.
            //for (int i = 0;i < nStructInsts;i++) structTable[i].print();
        }


            /**
             * Given a resultset and a list of devices, extracts
             * the devices and inserts it into the list in sorted order.
             */
        public synchronized void refreshData()
        {
            threadStopped = true;

            while (threadRunning)
            {
                try
                {
                    wait(100);
                } catch (Exception exc) { }
            }
            threadStopped = false;
            threadRunning = true;
            ourThread =new Thread(this);
            ourThread.start();
        }

            /**
             * Getting related info from the service data table
             * is a bit tricky.  Basically first pass is to build a tree of
             * which instance_ids are parents of which ones.  We only need
             * to build the tree of "interesting" items.  Eg for the WIP
             * devices, we only need struct_instance_id which have
             * structure_ids 1405 (overall access), 1420 (VLAN), 1450,
             * (Location) and 1470 (Port).
             *
             * Once this tree is built, its a matter of getting all
             * instances which have the required attribute_ids (3001, 3012
             * etc) then this with this list, they need to be matches with
             * the corresponding instances in the above query to see if
             * they are related.
             */
        public void run()
        {
            threadStopped = false;
            threadRunning = true;
            devList.clear();
            int prevAttrib = -1;
            DeviceInfo currDevice = null;
            int nDevices = 1;

            try
            {
                    // first query is to build the table of
                    // which instant is a child of which isntant
                obtainStructureInstantIDs();

                    // Bascially get all the relevant structures 
                    // from the structure_data table and populate
                    // the structure tables with this info
                obtainStructureInstantValues();

                    // now go through all the devices and create 
                    // the list of "learnt" devices.
                setStatusMessage(svdataStatusBar, "Creating Device List");
                devList.clear();
                for (int i = 0;i < nStructInsts &&
                                    threadRunning &&
                                        !threadStopped;i++)
                {
                    if (structTable[i].structureID != 1470 &&
                        structTable[i].structureID != 1330) continue;

                    String devName = structTable[i].getAttribute(3140);
                    if (devName == null) 
                       devName = structTable[i].getAttribute(2051);

                    if (devName == null) continue;

                        // now take this isntant and traverse "up" 
                        // the parent chain to create a new device isntant
                    DeviceInfo newDev = new DeviceInfo();
                    int currRow = i;
                    while (currRow > 0)
                    {
                        for (int j = 0;j < 3;j++)
                        {
                            if (structTable[currRow].attrCodes[j] > 0)
                            {
                                newDev.setAttribute(
                                        structTable[currRow].attrCodes[j],
                                        structTable[currRow].values[j]);
                            }
                        }
                        currRow = structTable[currRow].parentRow;
                    }
                    int pos = networkData.insertDevice("Service Data", newDev);
                    setStatusMessage(svdataStatusBar,
                            "Inserted Device " + devName + " at: " + pos);
                }
                setStatusMessage(svdataStatusBar,
                        devList.size() +
                        " devices extracted from service_Data table.");
            } catch (Exception ex)
            {
                setStatusMessage(svdataStatusBar,
                                 "Service data extraction failed: " + 
                                ex.getMessage());
                ex.printStackTrace();
                devList.clear();
                threadRunning = false;
            }

            setStatusMessage(svdataStatusBar,
                        "Read " + devList.size() + " devices' info.");

            fireTableDataChanged();
            threadRunning = false;
            threadStopped = false;

            doNotify();
        }
    }

        /**
         * Called when the database is being closed.
         *
         * This function should be implemented so that when the database is
         * called, the view may change to reflect this or to clear out all
         * data that is currently displaying.
         */
    public void viewClosing()
    {
    }

        /**
         * Show an object that is the result of a search.
         */
    public void showSearchItem(Object item)
    {
    }

        /**
         * Gets a list of searched Items.
         */
    public ListIterator searchItems(String searchType,
                                    Object searchCriteria)
    {
        return null;
    }
}
