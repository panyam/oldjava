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

public class ServiceView extends DBView
                              implements
                                ActionListener,
                                ItemListener,
                                TreeModelListener,
                                TreeSelectionListener,
                                Configurable
{
        /**
         * The search engine that will find items
         * from the element tree.
         */
    TreeSearcher treeSearcher = new TreeSearcher();
    CriteriaMatcher attribValueMatcher = new AttributeValueMatcher();
    ListIterator currSearchIterator = null;

        /**
         * The main central document that is to be drawn.
         */
    //SVDocument ourDocument = null;

        /**
         * The Service data object that is being presented and visualised.
         */
    protected ServiceData serviceData = new ServiceData();

        /**
         * The panel the holds all the info.
         */
    JSplitPane splitterPanel;

        /**
         * The progressbar that will display the progress of certain
         * things.
         */
    JPanel progressPanel = new JPanel(new BorderLayout());
    JProgressBar progressBar;
    JButton cancelProgress = new JButton("Cancel");

        /**
         * The timer for document population task object.
         */
    javax.swing.Timer docPopTimer = new javax.swing.Timer(50, this);

        /**
         * The model for the tree.
         */
    ElementTreeModel elemTreeModel = new ElementTreeModel();

        /**
         * The tree where the queries XML tree is shown.
         */
    JTree elementTree = new JTree(elemTreeModel);

        /**
         * The table where the properties for each node are shown.
         */
    NodeAttributeTableModel attribModels[] = new NodeAttributeTableModel[]
    {
        new NodeAttributeTableModel(),
        new LogicalAttributeTableModel()
    };

    JTable propertiesTable[] = new JTable[]
    {
        new JTable(attribModels[0]),
        new JTable(attribModels[1])
    };

        /**
         * Panel for selecting the service and so on.
         */
    JTextField serviceNameText = new JTextField("1311, 1368");

    protected final static int GET_BY_SERVICE_ID = 0;
    protected final static int GET_BY_COMPANY = 1;
    protected final static int GET_BY_FNN = 2;
    protected final static int GET_BY_DUPLICATES = 3;

        /**
         * The ways in which items can be extracted and populated
         */
    ServiceDataPopulator docPops[] = new ServiceDataPopulator[]
    {
        new ByServicePopulator(),
        new ByCustomerPopulator(),
        new ByFNNPopulator(),
        new ByDuplicatesPopulator(),
    };
    ServiceDataPopulator currDocPop = null;

    JComboBox getByServicesCombo = new JComboBox(ItemTypes.getByStrings);
    JComboBox getByFNNCombo = new JComboBox(ItemTypes.fnnStrings);

    JButton getServicesButton = new JButton("Get Services");

        /**
         * Constructor.
         */
    public ServiceView()
    {
        super();
        setLayout(new BorderLayout());

        setListeners();
        layComponents();
    }

        /**
         * Sets a new database that can be shown and manipulated.
         *
         * @param   dbase   The database which is to be used.
         */
    public void setDatabase(Database dbase) throws Exception
    {
        super.setDatabase(dbase);
        refreshDBItems();
    }

        /**
         * Initial layout of the components.
         */
    protected void layComponents()
    {
        progressBar = new JProgressBar(0, 1000);
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        progressBar.setStringPainted(true); //get space for the string
        progressBar.setString("");          //but don't paint it
        progressPanel.add("Center", progressBar);
        progressPanel.add("East", cancelProgress);

        JPanel bottomPanel= new JPanel(new BorderLayout());
        JPanel getByPanel =new JPanel();
        getByPanel.add(new JLabel("Get By "));
        getByPanel.add(getByServicesCombo);
        getByPanel.add(getByFNNCombo);

        JPanel bottomNorthPanel = new JPanel(new BorderLayout());
        JPanel bottomSouthPanel = new JPanel(new BorderLayout());
        bottomNorthPanel.add("West", getByPanel);
        bottomNorthPanel.add("Center", serviceNameText);
        bottomNorthPanel.add("East", getServicesButton);
        bottomSouthPanel.add("Center", progressPanel);

        bottomPanel.add("North", bottomNorthPanel);
        bottomPanel.add("South", bottomSouthPanel);

        serviceNameText.setEnabled(true);
        getByFNNCombo.setVisible(false);

        JScrollPane tableScroller1 = new JScrollPane(propertiesTable[0]);
        JScrollPane tableScroller2 = new JScrollPane(propertiesTable[1]);
        JSplitPane tableSplitter =
                        new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                       tableScroller1,
                                       tableScroller2);

        JScrollPane treeScroller = new JScrollPane(elementTree);
        splitterPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                       treeScroller,
                                       tableSplitter);

        tableSplitter.setDividerLocation(150);
        splitterPanel.setDividerLocation(300);

        add("Center", splitterPanel);
        add("South", bottomPanel);
    }

        /**
         * Set the event listeners.
         */
    protected void setListeners()
    {
        cancelProgress.addActionListener(this);

        getByFNNCombo.addActionListener(this);
        getByServicesCombo.addActionListener(this);

        serviceNameText.addActionListener(this);

        getServicesButton.addActionListener(this);

        elemTreeModel.addTreeModelListener(this);
        elementTree.getSelectionModel().setSelectionMode
                    (TreeSelectionModel.SINGLE_TREE_SELECTION);
        //Listen for when the selection changes.
        elementTree.addTreeSelectionListener(this);
    }

        /**
         * Tree Model events.
         */
    public void treeNodesInserted(TreeModelEvent e) { }
    public void treeNodesRemoved(TreeModelEvent e) { }
    public void treeNodesChanged(TreeModelEvent e) { }
    public void treeStructureChanged(TreeModelEvent e)
    {
    }

        /**
         * Tree selection changed.
         */
    public void valueChanged(TreeSelectionEvent e)
    {
        if (e.getSource() == elementTree)
        {
            ElementTreeNode node = 
                (ElementTreeNode) elementTree.getLastSelectedPathComponent();
            if (node == null) return ;
            attribModels[0].setNode(node);
            attribModels[1].setNode(node);
                // other wise 
        }
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        int selIndex = getByServicesCombo.getSelectedIndex();
        String selItem = (String)getByServicesCombo.getSelectedItem();
        serviceNameText.setEnabled( selItem.indexOf("uplicates") < 0);
        getByFNNCombo.setVisible(selIndex == GET_BY_FNN ||
                                 selIndex == GET_BY_DUPLICATES);

        if (src == docPopTimer)
        {
            if (currDocPop != null)
            {
                int min = currDocPop.getMinimumValue();
                int max = currDocPop.getMaximumValue();
                int curr = currDocPop.getCurrentValue();
                int status = currDocPop.getTaskStatus();
                String msg = currDocPop.getMessage();

                if (progressBar.isIndeterminate() && max > min)
                {
                    progressBar.setIndeterminate(false);
                }
                progressBar.setValue(curr);
                progressBar.setString(msg);

                if (status == Task.SUCCEEDED || status == Task.CANCELLED)
                {
                    progressBar.setValue(0);
                    docPopTimer.stop();
                    progressBar.setString("Extraction Complete.");
                    progressBar.setIndeterminate(false);
                    //progressBar.setVisible(false);
                    Object res = currDocPop.getResult();
                    if (res instanceof Exception)
                    {
                        ((Exception)res).printStackTrace();
                    }
                    currDocPop = null;

                        // finally update the document...
                    progressBar.setIndeterminate(true);
                    progressBar.setString("Updating Document Tree View.");
                    updateDocument();
                    progressBar.setIndeterminate(false);
                    progressBar.setString(msg);
                    progressPanel.setVisible(false);

                        // also fetch all the quick searchable results
                        // into this...
                        //
                        // Now also load the searchable items here...
                    clearSearchables();

                    for (Iterator custs = serviceData.getCustomerIterator();
                          custs.hasNext();)
                    {
                        CustomerInfo cust = (CustomerInfo)custs.next();
                        addSearchable("Customer", cust.custName);
                        addSearchable("Customer ID", cust.custID);
                        Iterator services = cust.getServiceIterator();
                        while (services.hasNext())
                        {
                            ServiceInfo service = (ServiceInfo)services.next();
                            addSearchable("Service", service.serviceID);
                        }
                    }

                        // now the FNN searchables...
                    for (Iterator fnns = serviceData.getFNNIterator();
                         fnns.hasNext();)
                    {
                        FNNInfo fnn = (FNNInfo)fnns.next();
                        addSearchable("FNN", fnn.fnnID);
                    }

                        // now add the kinds of searchable items in here...
                    appMediator.viewChanged(this);
                }
                serviceData.fireDBDataEvent(msg);
            }
        } else if (src == cancelProgress)
        {
                // cancel the current task that 
                // is working on the progressbar...
            if (currDocPop != null)
            {
                currDocPop.stopTask();

                System.out.println("here?? ");
            }
        } else if (src == getServicesButton)
        {
                // list of items selected
            String itemList = serviceNameText.getText();

            currDocPop = docPops[selIndex];

            if (currDatabase == null || currDatabase.getConnection () == null)
            {
                serviceData.fireDBDataEvent("Not connected to database.");
                return ;
            }

            // go and get all the services!!!
            try
            {
                currDocPop.setDocument(serviceData);
                currDocPop.setDatabase(currDatabase);

                currDocPop.setItemList(itemList);

                if (selIndex == GET_BY_FNN ||
                    selIndex == GET_BY_DUPLICATES)
                {
                    int fnnTypeIndex = getByFNNCombo.getSelectedIndex();
                    ((ByFNNPopulator)currDocPop).
                        setFNNTypes(ItemTypes.fnnTypeLists[fnnTypeIndex]);
                }

                progressPanel.setVisible(true);
                progressBar.setIndeterminate(true);
                docPopTimer.start();
                currDocPop.startTask();
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }
    }

        /**
         * Get a set of services that need to be selected.
         */
    public StringTokenizer getServiceList()
    {
        if ( ! serviceNameText.isEnabled()) return null;

        return new StringTokenizer(serviceNameText.getText().trim(),
                                    " \t,", false);
    }

        /**
         * Called when document has changed.
         */
    public void updateDocument()
    {
        elemTreeModel.setDocument(serviceData);
    }

        /**
         * Item event handler.
         */
    public void itemStateChanged(ItemEvent e)
    {
    }

        /**
         * Show an object that is the result of a search.
         */
    public void showSearchItem(Object item)
    {
            // calculate the treepath to the node
        TreePath treePath =
            new TreePath(((ElementTreeNode)item).getPath());

        elementTree.addSelectionPath(treePath);
        elementTree.scrollPathToVisible(treePath);
    }


        /**
         * Gets a list of searched Items.
         */
    public ListIterator searchItems(String searchType,
                                    Object searchCriteria)
    {
        treeSearcher.setCriteriaMatcher(attribValueMatcher);
        return treeSearcher.doSearch(elemTreeModel.rootNode,
                                     searchCriteria);
    }

        /**
         * Super class of all objects that can match nodes.
         */
    protected interface CriteriaMatcher
    {
        public boolean nodeMatches(ElementTreeNode node);
        public void setCriteria(Object attrib);
    }

        /**
         * A node matches that matches a node if it has
         * at least one attribute whose value equals the
         * given attribute.
         */
    class AttributeValueMatcher implements CriteriaMatcher
    {
        String attribValue = "";

            /**
             * Sets the attribute value criteria.
             */
        public void setCriteria(Object attrib)
        {
            attribValue = (String)attrib;
        }
    
            /**
             * Tells if a node matches the criteria.
             */
        public boolean nodeMatches(ElementTreeNode node)
        {
            return node != null &&
                   (node.getAttributeID(attribValue, 0) >= 0 ||
                   node.getAttributeID(attribValue, 1) >= 0);
        }
    }

        /**
         * The search engine that searches the tree
         * for certain criteria.
         */
    class TreeSearcher
    {
            // the list where the matches are kept...
        java.util.List list = new java.util.LinkedList();
        CriteriaMatcher critMatcher;

            /**
             * Set the criteria matcher object for
             * matching nodes.
             */
        public void setCriteriaMatcher(CriteriaMatcher cm)
        {
            critMatcher = cm;
        }

            /**
             * Does the search.
             */
        public ListIterator doSearch(Object target, Object criteria)
        {
            list.clear();
            critMatcher.setCriteria(criteria);

            if (target != null) doSearch((ElementTreeNode)target, list);
            return list.listIterator();
        }

            /**
             * Does a search for objects matching the criteria.
             */
        protected void doSearch(ElementTreeNode node,
                                //Object criteria,
                                java.util.List outputList)
        {
            // basically teh criteria MUST:
            // STRING - Implies Match by Node Attribute Value.
            //          So all nodes that have this attribute value
            //          will match
            // STRING, STRING
            if (critMatcher.nodeMatches(node))
            {
                outputList.add(node);
            }

                    // now traverse down all the other nodes.
            Enumeration kids = node.children();
            while (kids.hasMoreElements())
            {
                Object kid = kids.nextElement();
                if (kid instanceof ElementTreeNode)
                {
                    doSearch((ElementTreeNode)kid, outputList);
                }
            }
        }
    }

        /**
         * The tree model that takes care of the tree data representation
         * of our service element tree.
         */
    class ElementTreeModel extends DefaultTreeModel
    {
        public ElementTreeNode rootNode = null;

        protected int numNodes = 0;

        public ElementTreeModel()
        {
            super(null);
        }

            /**
             * Start with the document and build the tree.
             */
        public void setDocument(ServiceData doc)
        {
            numNodes = 0;
            if (rootNode != null) rootNode.removeAllChildren();
            else
            {
                rootNode = new ElementTreeNode("Customers", 0);
                numNodes++;
            }

            if (doc == null)
            {
                setRoot(rootNode);
                return ;
            }

            Iterator custIterator = doc.getCustomerIterator();
            CustomerInfo currCust;
            ServiceInfo currService;
            ElementTreeNode currCustNode = null;
            int nCustAttribs = 2;

            while (custIterator.hasNext())
            {
                currCust = (CustomerInfo)custIterator.next();
                currCustNode= new ElementTreeNode(currCust.custName,
                                                   nCustAttribs);
                numNodes++;

                currCustNode.setAttribute("Customer ID", currCust.custID);
                currCustNode.setAttribute("Customer Name", currCust.custName);
                Iterator servIterator = currCust.getServiceIterator();
                while (servIterator.hasNext())
                {
                    currService = (ServiceInfo)(servIterator.next());
                    currCustNode.add(buildTree(currService.treeNode, true));
                }
                rootNode.add(currCustNode);
            }

            setRoot(rootNode);

                // fire a tree changed event...
            //fireTreeStructureChanged(null, null, null, null);
        }

            /**
             * Builds a tree recursively.
             */
        protected ElementTreeNode buildTree(Element element, boolean hasParent)
        {
                // get the document Name
            NamedNodeMap attrs = element.getAttributes();
            int numAttribs = attrs.getLength();
            //NodeList attrNodes =
                //element.getElementsByTagName(XMLUtils.ATTRIBUTES_ROOT_NAME);

            //Element attrNode = element.get

            String nodeName = element.getTagName();

            String idVal = element.getAttribute(XMLUtils.ID_ATTRIB);
            if (nodeName.equalsIgnoreCase("struct"))
            {
                String structID = element.getAttribute("STRUCTURE_ID");
                nodeName = serviceData.getStructureName(structID);
                idVal = element.getAttribute("STRUCTURE_ID").trim();
            } else
            {
                idVal = element.getAttribute(XMLUtils.ID_ATTRIB).trim();
            }
            if (idVal != null && idVal.length() > 0)
            {
                nodeName += " - " + idVal;
            }

            ElementTreeNode currNode =
                new ElementTreeNode(nodeName, numAttribs);
            numNodes++;

            for (int i = 0;i < numAttribs;i++)
            {
                Node attr = attrs.item(i);
                currNode.setAttribute(attr.getNodeName(),
                                      attr.getNodeValue());
            }

                // now add the children...

            NodeList children = element.getChildNodes();
            int nKids = children == null ? 0 : children.getLength();
            for (int i = 0;i < nKids;i++)
            {
                Node kid = children.item(i);
                    // woo hoo parse the kid
                if (kid instanceof Element)
                {
                        // if its an attribute child then put
                        // all of them here as well!!!
                    Element elem = (Element)kid;

                    // if its an attribute tree then do a logical attribute
                    // addition

                    String elemNodeName = elem.getTagName();
                    if (elemNodeName.equalsIgnoreCase(
                                    XMLUtils.ATTRIBUTES_ROOT_NAME))
                    {
                        NodeList attribs =
                            elem.getElementsByTagName(
                                    XMLUtils.ATTRIBUTE_ROOT_NAME);
                        int nAttribs = attribs.getLength();

                        currNode.createLogicalAttributes(nAttribs);
                        for (int j = 0;j < nAttribs;j++)
                        {
                            Element attrElem = (Element)(attribs.item(j));
                            currNode.setAttribute(
                                attrElem.getAttribute(
                                    XMLUtils.ATTRIB_CODE_ATTRIB),
                                attrElem.getAttribute(
                                    XMLUtils.ATTRIB_NAME_ATTRIB),
                                attrElem.getAttribute(
                                    XMLUtils.ATTRIB_VALUE_ATTRIB));
                        }
                    } else
                    {
                        currNode.add(buildTree(elem, true));
                        if ( ! hasParent)
                        {
                            setRoot(currNode);
                        }
                    }
                }
            }

            return currNode;
        }
    }

        /**
         * Read the configurations.
         */
    public void readConfigurations(Document  configDoc)
    {
        if (configDoc == null) return ;

        Element docElem = configDoc.getDocumentElement();
    }

        /**
         * Creates an element for the configurations.
         */
    public Element getConfigurations(Document doc)
    {
        return null;
    }

        /**
         * Refreshes the list of attribute names and their IDs for the CSOM
         * database.
         */
    protected void refreshDBItems() throws SQLException
    {
        serviceData.clearStructures();
        serviceData.clearAttributes();

            // first get all the attribute names and IDs
        String query1 = "select * from CSOM_ADMIN.structure_attribute " + 
                        "order by STRUCTURE_ATTRIB_ID";
        Statement stmt1 = currDatabase.newStatement();
        ResultSet initialSet = stmt1.executeQuery(query1);

        while (initialSet.next())
        {
                // this structureID tells to which structure this attribute
                // belongs to!!!
            String attribCode = initialSet.getString("STRUCTURE_ATTRIB_ID");
            serviceData.addAttribute(attribCode,
                                     initialSet.getString("ATTRIBUTE_NAME"));
        }
        stmt1.close();

            // now get all the structure names corersponding to the
            // structure IDs.
        String query2 = "select * from CSOM_ADMIN.structure " +
                        "order by STRUCTURE_ID";
        Statement stmt2 = currDatabase.newStatement();
        ResultSet rset2 = stmt2.executeQuery (query2);

        while (rset2.next())
        {
                // this structureID tells to which structure this attribute
                // belongs to!!!
            String structID = rset2.getString("STRUCTURE_ID");
            serviceData.addStructure(structID, rset2.getString("MODEL_NAME"));
        }
        stmt2.close();
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
}
