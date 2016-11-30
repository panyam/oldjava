
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
 * This class is responsible for visually managing the list of all
 * sessions.
 *
 * @author  Sri Panyam
 */
public class DBSessionControlPanel extends JPanel
{
        /**
         * The tab pane where each tab is for one unique db session name.
         */
    protected JTabbedPane sessionTab = new JTabbedPane();

        // renderer for drawing the icon view for the item in the JLists...
    protected ListCellRenderer cellRenderer = new DBViewListCellRenderer();

        /**
         * List of JList objects.
         *
         * Each JList in this list is associated to exactly one item in the
         * JTabbedPane object.
         */
    protected java.util.List sessionList = new ArrayList();

        /**
         * The parent object that acts as a mediator.
         * This is only a hack.  Ideally ti should be a proper mediator
         * interface that would take care rather than explicit reference to
         * the parent frame.
         */
    protected AppMediator appMediator = null;

        /**
         * Constructor.
         */
    public DBSessionControlPanel(AppMediator appMediator)
    {
        setLayout(new BorderLayout());

        this.appMediator = appMediator;

        sessionTab.setTabPlacement(JTabbedPane.BOTTOM);
        sessionTab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);


        add("Center", sessionTab);
    }

        /**
         * Handles changes in list selection.
         */
    protected class ViewListSelectionListener implements ListSelectionListener
    {
        protected JList viewList;

            /**
             * Constructor.
             */
        public ViewListSelectionListener(JList viewList)
        {
            this.viewList = viewList;
        }

            /**
             * Event handler.
             */
        public void valueChanged(ListSelectionEvent e)
        {
            int selIndex = viewList.getSelectedIndex();

            SessionViewListModel lModel =
                (SessionViewListModel)viewList.getModel();

            DBView dbView = (DBView)lModel.getElementAt(selIndex);

            appMediator.selectView(dbView);
        }
    }

        /**
         * Adds a new "empty" database session to the list if it doesnt
         * already exist.
         *
         * @param   dbSession   The database session that is being added.
         * @return The index at which the dbSession is added.
         */
    public int addDBSession(Database dbSession)
    {
        String tabTitle = getTabTitle(dbSession);

        int sessionID = dbSession.getSessionID();
        String tabName = tabTitle + " - " + sessionID;

        int tIndex = getDBIndex(dbSession);
        if (tIndex < 0)
        {
            JList viewList = createJList(dbSession);

            sessionTab.addTab(tabTitle, viewList);
            sessionList.add(dbSession);

            tIndex = sessionTab.getTabCount() - 1;
        }

        return tIndex;
    }

        /**
         * Gets the most active view.
         */
    public DBView getCurrentView()
    {
        int currDB = getSelectedDBIndex();
        if (currDB < 0) return null;

        JList list = (JList)sessionTab.getComponentAt(currDB);

        if (list == null) return null;

        int selIndex = list.getSelectedIndex();

        if (selIndex < 0) return null;

        SessionViewListModel lModel = (SessionViewListModel)list.getModel();

        return (DBView)lModel.getElementAt(selIndex);
    }

        /**
         * Get the current JList object that is showing the iconified
         * views.
         */
    public JList getCurrentViewList()
    {
        int ind = getSelectedDBIndex();
        if (ind >= 0)
        {
            return (JList)sessionTab.getComponentAt(ind);
        }

        return null;
    }

        /**
         * Add a mouselistener.
         */
    public void addMouseListener(MouseListener listener)
    {
        sessionTab.addMouseListener(listener);
        super.addMouseListener(listener);
    }

        /**
         * Get the JList at the given index.
         */
    public JList getViewJList(int index)
    {
        return (JList)sessionTab.getComponentAt(index);
    }

        /**
         * Find the index of the database.
         */
    public int getDBIndex(Database dbSession)
    {
        int index = 0;
        Iterator iter = sessionList.iterator();

        for(;iter.hasNext();index++)
        {
            Database currDB = (Database)iter.next();
            if ((currDB.getSessionID() == dbSession.getSessionID()) &&
                (currDB.getHost().equals(dbSession.getHost())) &&
                (currDB.getLogin().equals(dbSession.getLogin())))
            {
                return index;
            }
        }
        return -1;
    }

        /**
         * Returns the index of the selected DB session.
         *
         * @return  The index of the selected database.
         */
    public int getSelectedDBIndex()
    {
        return sessionTab.getSelectedIndex();
    }

        /**
         * Attach a new view to a db session.
         *
         * If the view already exists then it is not listed again.  If the
         * db does not exist, a new tab is added for it.
         *
         * @param   dbSession   The datbase session to which the view is to
         *                      be attached.
         * @param   dbView      The view object to attach to the db
         *                      session.
         */
    public void attachView(Database dbSession, DBView dbView)
    {
        String tabTitle = getTabTitle(dbSession);

        int sessionID = dbSession.getSessionID();
        String tabName = tabTitle + " - " + sessionID;

        int tIndex = getDBIndex(dbSession);

            // add a new tab...
        JList viewList = null;

            // find the corresonding JList and add 
            // if it doesnt exist...
        if (tIndex < 0)
        {
            viewList = createJList(dbSession);

            sessionTab.addTab(tabTitle, viewList);
            sessionList.add(dbSession);
        } else
        {
            viewList = (JList)sessionTab.getComponentAt(tIndex);
        }

            // add the dbView info to the JList object...
        SessionViewListModel lModel = 
                (SessionViewListModel)viewList.getModel();

        lModel.addView(dbView);
    }

        /**
         * Close the current session.
         */
    public void closeCurrentSession()
    {
        sessionTab.remove(getSelectedDBIndex());
        sessionList.remove(getSelectedDBIndex());
    }

        /**
         * Gets the current database.
         */
    public Database getSelectedDB()
    {
        return (Database)sessionList.get(getSelectedDBIndex());
    }

        /**
         * Gets a list of all views associated with the current session.
         */
    public java.util.List getSelectedViews()
    {
        int curr = getSelectedDBIndex();
        JList viewList = (JList)sessionTab.getComponentAt(curr);
        SessionViewListModel lModel = 
                (SessionViewListModel)viewList.getModel();

        return lModel.getViewList();
    }

        /**
         * Create a new JList object associated with a DB session.
         *
         * @param   dbSession   The DB session to which the list is
         *                      associated.
         * @return  The newly created JList object.
         */
    protected JList createJList(Database dbSession)
    {
        JList viewList = new JList(new SessionViewListModel(dbSession));

        viewList.setToolTipText("This view shows the current views " +
                                "attached to this session in icon form.  " + 
                                "Right click here to add attach more views.");
        viewList.setCellRenderer(cellRenderer);
        viewList.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        viewList.addListSelectionListener(
                new ViewListSelectionListener(viewList));
        viewList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        viewList.setVisibleRowCount(-1);
        return viewList;
    }

        /**
         * Tells how many sessions are on.
         */
    public int getSessionCount()
    {
        return sessionTab.getTabCount();
    }

        /**
         * Detach a view from the list, effectively closing the view.
         *
         * @param   dbView  The view object to detach and remove.
         */
    public void detachView(DBView dbView)
    {
        Database dbSession = dbView.getDatabase();
        String tabTitle = getTabTitle(dbSession);

        int sessionID = dbSession.getSessionID();
        String tabName = tabTitle + " - " + sessionID;

        int tIndex = getDBIndex(dbSession);

        if (tIndex < 0) return ;

        JList viewList = (JList)sessionTab.getComponentAt(tIndex);
        SessionViewListModel lModel = 
                (SessionViewListModel)viewList.getModel();

        dbView.viewClosing();
        dbView.setVisible(false);

            // remove the view from the list...
        lModel.removeView(dbView);
    }

        /**
         * Given a database, calculates and returns its tab title.
         *
         * @param   dbSession   The database session whose tab title is to
         *                      be calculated.
         * @return  The title of the Tab.
         */
    protected static String getTabTitle(Database dbSession)
    {
        String host = dbSession.getHost();
        String login = dbSession.getLogin();
        return login + " @ " + host;
    }

        /**
         * Renders the info regarding a view within the JList.
         */
    protected class DBViewListCellRenderer extends JLabel
                                           implements ListCellRenderer
    {
            /**
             * Constructor.
             */
        public DBViewListCellRenderer()
        {
            //setVerticalAlignment(SwingConstants.BOTTOM);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
        }

            /**
             * Returns the cell renderer object for a specified value to be
             * drawn in the JList object.
             */
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)
        {
            DBView viewObj = (DBView)value;
            Database dbase = viewObj.getDatabase();
            String title = viewObj.getViewName();

            setIcon(viewObj.getIcon());
            setText(title);

            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

        /**
         * The list model for the view list for each session.
         */
    protected class SessionViewListModel extends AbstractListModel
    {
            /**
             * List of DBView objects associated with this model.
             */
        protected java.util.List views = new ArrayList();

            /**
             * The database session object that is attached to all the
             * views.
             */
        protected Database currDB = null;

            /**
             * Constructor.
             */
        public SessionViewListModel(Database dbase)
        {
            currDB = dbase;
        }

            /**
             * Adds a new view to the list.
             *
             * @param   view    The view to be added.
             */
        public void addView(DBView view)
        {
            int index = views.indexOf(view);
            if (index >= 0) return ;

            views.add(view);
            fireContentsChanged(this, views.size() - 1, views.size() - 1);
        }

            /**
             * Remvoes an existing view to the list.
             *
             * @param   view    The view to be removed.
             */
        public void removeView(DBView view)
        {
            int index = views.indexOf(view);
            if (index < 0) return ;

            views.remove(index);
            fireContentsChanged(this, index, index);
        }

            /**
             * Return the list of views.
             */
        public java.util.List getViewList()
        {
            return views;
        }

            /**
             * Get the size of this model.
             *
             * @return  The number of items in the model.
             */
        public int getSize()
        {
            return views.size();
        }

            /**
             * Returns the value at the specified index.
             *
             * @param   index       the requested index.
             * @return              the value at index 
             */
        public Object getElementAt(int index)
        {
            return views.get(index);
        }
    }
}
