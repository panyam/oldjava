package svtool.gui.views;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
import java.awt.*; 
import java.awt.event.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

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
 * This window holds all the queries and allows querying of the database.
 *
 * The key ideas are:
 *
 * Cache queries.
 * Delete queries from cache.
 * Add queries to cache only if not duplicate.
 * Show query results in table.
 */
public class QueryView extends DBView implements ListSelectionListener,
                                                   ActionListener,
                                                   KeyListener,
                                                   TableModelListener,
                                                   Configurable
{
    class QueryComponent extends AbstractCellEditor 
                         implements
                                TableCellRenderer,
                                TableCellEditor
    {
            /**
             * The renderer used in the table to edit and render the query.
             */
        protected JTextArea queryArea = null;

            /**
             * Constructor.
             */
        public QueryComponent(int nLines)
        {
            queryArea = new JTextArea(nLines, 70);
            queryArea.setLineWrap(true);
            queryArea.setWrapStyleWord(true);
        }

            /**
             * Get the current value.
             */
        public Object getCellEditorValue()
        {
            return queryArea.getText();
        }

            /**
             * Get the cell renderer component.
             */
        public Component getTableCellEditorComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               int row,
                                               int column)
        {
            queryArea.setText(value.toString());
            //if (isSelected) queryArea.transferFocus();
            return queryArea;
        }

            /**
             * Get the cell renderer component.
             */
        public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column)
        {
            queryArea.setText(value.toString());
            if (isSelected) queryArea.transferFocus();
            return queryArea;
        }
    }

        /**
         * Table where the query results are displayed.
         */
    protected JTable resultTable;

        /**
         * Label for showing the status of the query.
         */
    protected JLabel statusLabel = new JLabel("Execute Query.");

        /**
         * The submit button which when pressed lets the query to be
         * executed.
         */
    protected JButton submitButton = new JButton("Submit Query");
    protected JButton addButton = new JButton("Add Query");
    protected JButton moveUpButton = new JButton("Up");
    protected JButton moveDownButton = new JButton("Down");
    protected JButton delButton = new JButton("Delete Query");

        /**
         * Tells which row is currently selected.
         */
    protected int selectedRow = 0;

    protected DefaultTableModel queryListModel = new DefaultTableModel();
    protected JTable queryTable = new JTable(queryListModel);

    protected JSplitPane splitterPanel = null;

    protected QueryTableModel qtModel = new QueryTableModel();

        /**
         * Constructor.
         */
    public QueryView()
    {
        //super("SQL Query Executor...");

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setListeners();
        layComponents();

        queryListModel.addColumn("Query");

        TableColumn queryColumn = queryTable.getColumnModel().getColumn(0);
        queryColumn.setCellRenderer(new QueryComponent(4));
        queryColumn.setCellEditor(new QueryComponent(4));

        //pack();
        //setVisible(true);
        //setLocationRelativeTo(null);
    }

        /**
         * Sets all the listeners.
         */
    protected void setListeners()
    {
        //queryList.addListSelectionListener(this);
        addButton.addActionListener(this);
        delButton.addActionListener(this);
        moveUpButton.addActionListener(this);
        moveDownButton.addActionListener(this);
        submitButton.addActionListener(this);
        qtModel.addTableModelListener(resultTable);
        qtModel.addTableModelListener(this);

        //queryListModel.setTableModelListener(queryTable);
        queryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = queryTable.getSelectionModel();
        rowSM.addListSelectionListener(this);
    }

        /**
         * Lays out the components.
         */
    protected void layComponents()
    {
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();

        JScrollPane queryScroller = new JScrollPane(queryTable);
        // Or in two steps:
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.getViewport().setView(dataList);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(delButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);

        Container contPane = this;//getContentPane();
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add("Center", queryScroller);
        northPanel.add("South", buttonPanel);

        resultTable = new JTable(qtModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //resultTable.autoCreateColumnsFromModel(true);
        resultTable.setShowVerticalLines(true);
        resultTable.setShowHorizontalLines(true);

        JScrollPane tableScroller = new JScrollPane(resultTable);
        splitterPanel =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           northPanel, tableScroller);
        splitterPanel.setDividerLocation(180);

        contPane.setLayout(new BorderLayout());
        contPane.add("Center", splitterPanel);
        contPane.add("South", statusLabel);
    }

        /**
         * Sets the statusbar message.
         */
    public void setStatusMessage(String mesg)
    {
        statusLabel.setText(mesg);
    }

        /**
         * Tells when the value in the list has changed.
         */
    public void valueChanged(ListSelectionEvent e)  
    {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        selectRow(lsm.getMinSelectionIndex());
    }

    public void tableChanged(TableModelEvent tme)
    {
        if (tme.getSource() == qtModel)
        {
            // then resize the table...
            int nCols = resultTable.getColumnCount();
            TableColumnModel columnModel = resultTable.getColumnModel();
            for (int i = 0;i < nCols;i++)
            {
                TableColumn column = columnModel.getColumn(i);
                column.sizeWidthToFit();
            }
            resultTable.repaint();
        }
    }

    public void keyPressed(KeyEvent e){ }
    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e)
    {
    }

        /**
         * Select a given row./
         */
    public void selectRow(int row)
    {
        selectedRow = row;
        submitButton.setEnabled(selectedRow >= 0);
        addButton.setEnabled(selectedRow >= 0);
        delButton.setEnabled(selectedRow >= 0);
        moveUpButton.setEnabled(selectedRow >= 0);
        moveDownButton.setEnabled(selectedRow >= 0);

        if (row >= 0)
        {
            queryTable.changeSelection(selectedRow, 0, false, false);

                // also check if the selected row has any value in it...
            String cellValue =
                ((String)queryListModel.getValueAt(row, 0)).trim();

            submitButton.setEnabled(cellValue.length() > 0);
        } else
        {
            queryTable.clearSelection();
        }
    }

        /**
         * Action performed item listener.
         */
    public void actionPerformed(ActionEvent e)
    {
        if (currDatabase == null) return ;
        Connection connection = currDatabase.getConnection();
        Object src = e.getSource();
        if (src == addButton)
        {
            queryListModel.addRow(new String[] { "type in your query here..." });
            selectRow(queryListModel.getRowCount() - 1);
        } else if (src == delButton)
        {
                // remove the current row...
            int selRow = queryTable.getSelectedRow();
            int nRows = queryListModel.getRowCount();
            if (selRow < 0 || selRow >= nRows) 
            {
                selectRow(selRow < 0 ? 0 : nRows - 1);
                return ;
            }
            queryListModel.removeRow(selRow);
            nRows = queryListModel.getRowCount();
            selectRow(selRow >= nRows ? nRows - 1 : selRow);
        } else if (src == submitButton)
        {
            if (connection == null) 
            {
                setStatusMessage("Not connected to database.");
                return ;
            }
            try
            {
                    // execute the selected query...
                String cellValue =
                    ((String)queryListModel.getValueAt(selectedRow, 0)).trim();
                if (cellValue.length() > 0)
                {
                    qtModel.executeQuery(cellValue);
                }

                    // if query does not fail then add it to the list box
                    // if its not already there...
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }
    }

        /**
         * The model for the table that takes the result and
         * converts into table data that can be displayed into
         * the table.
         */
    public class QueryTableModel extends AbstractTableModel implements Runnable
    {
        Thread ourThread;
        boolean threadRunning = false;
        boolean threadStopped = false;
        Vector rows = new Vector();

        String headings[] = null;
        int nColumns = 0;
        String query = "";

        public void run()
        {
            threadStopped = false;
            threadRunning = true;
            try
            {
                rows.removeAllElements();

                if (currDatabase == null || currDatabase.getConnection() == null) 
                {
                    threadRunning = false;
                    threadStopped = true;
                    setStatusMessage("Database not open.");
                    doNotify();
                    return ;
                }
                Statement stmt = currDatabase.getConnection().createStatement();
                ResultSet rset = stmt.executeQuery(query);
                    // first get the column headings from the MetaData
                ResultSetMetaData metaData = rset.getMetaData();

                nColumns = metaData.getColumnCount();
                if (headings == null || nColumns > headings.length)
                {
                    headings = new String[nColumns];
                }

                for (int i = 0;i < nColumns;i++) 
                {
                    headings[i] = metaData.getColumnName(i + 1);
                }

                while (!threadStopped && threadRunning && rset.next())
                {
                    String row[] = new String[nColumns];
                    for (int i = 0;i < nColumns;i++)
                    {
                        row[i] = rset.getString(i + 1);
                    }
                    rows.addElement(row);
                }
                stmt.close();

                setStatusMessage("Query Succeeded - " + rows.size() + " rows.");

                fireTableStructureChanged();
            } catch (Exception ex)
            {
                setStatusMessage("Query failed: " + ex.getMessage());
                //ex.printStackTrace();
                nColumns = 0;
                rows.removeAllElements();
                threadRunning = false;
                fireTableStructureChanged();
            }
            threadRunning = false;
            threadStopped = false;
            doNotify();
        }

        protected void doNotify()
        {
            try
            {
                notifyAll();
            } catch (Exception exc)
            {
            }
        }

            /**
             * Updates the Table model with a new rule set.
             */
        public synchronized void executeQuery(String query)
        {
            ourThread = new Thread(this);
            threadStopped = true;

                // wait for the thread to stop running
            while (threadRunning)
            {
                try
                {
                    wait();
                } catch (Exception exc)
                {
                }
            }

            this.query = query;
            ourThread.start();
        }

            /**
             * Get the name of a given column.
             */
        public String getColumnName(int col)
        {
            return headings[col];
        }

            /**
             * Get the class of a given column.
             */
        public Class getColumnClass(int c)
        {
            return String.class;
        }

            /**
             * Gets the value at a givenlocation.
             */
        public Object getValueAt(int row, int col)
        {
            try
            {
                return ((String[])rows.elementAt(row))[col];
            } catch (Exception ex)
            {
            }
            return null;
        }

            /**
             * Returns the number of rows. 
             */
        public int getRowCount()
        {
            return rows.size();
        }

            /**
             * Returns the number of columns 
             */
        public int getColumnCount()
        {
            return nColumns;
        }
    }

        /**
         * Read the configurations.
         */
    public void readConfigurations(Document  configDoc)
    {
        if (configDoc == null) return ;

        Element docElem = configDoc.getDocumentElement();

            // remove all rows...
            // should this be done???
        for (int s = queryListModel.getRowCount() - 1, i = s;i >= 0;i--)
        {
            queryListModel.removeRow(i);
        }

            // get all "query" elements
        NodeList queryNodeList = docElem.getElementsByTagName(XMLUtils.QUERIES_ROOT_NAME);
        if (queryNodeList == null || queryNodeList.getLength() <= 0) return ;

        Element queryNode = (Element)queryNodeList.item(0);
        NodeList queries = queryNode.getElementsByTagName(XMLUtils.QUERY_ROOT_NAME);

        if (queries == null || queries.getLength() <= 0) return ;
        int nConns = queries.getLength();

        for (int i = 0;i < nConns;i++)
        {
            Element query = (Element)queries.item(i);

                // basically read the info here.
            String queryVal = query.getAttribute(XMLUtils.VALUE_ATTRIB).trim();
            addQueryToList(queryVal);
        }
        selectRow(0);
    }

    public void addQueryToList(String queryVal)
    {
        //System.out.println(queryVal);
        if (queryVal == null || queryVal.length() <= 0) return ;

        queryListModel.addRow(new String []{queryVal });
        //queryList.setListData(queryListModel);
    }

        /**
         * Creates an element for the configurations.
         */
    public Element getConfigurations(Document doc)
    {
        Element queriesElem = doc.createElement(XMLUtils.QUERIES_ROOT_NAME);

        int nQueries = queryListModel.getRowCount();

        for (int i = 0;i < nQueries;i++)
        {
            Element query = doc.createElement(XMLUtils.QUERY_ROOT_NAME);
            query.setAttribute(XMLUtils.VALUE_ATTRIB,
                              (String)queryListModel.getValueAt(i, 0));
            queriesElem.appendChild(query);
        }

        return queriesElem;
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
