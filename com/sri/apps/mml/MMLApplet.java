package com.sri.apps.mml;

import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.util.*;
import java.applet.*;

public class MMLApplet extends Applet implements ItemListener
{
    public static final String ALL_SYMBOLS = "ALL SYMBOLS";

        /**
         * The hash table stores all the images.
         */
    Hashtable imageTable = null;

    MMLEditor editor = null;
    Dialog mmlDialog = null;
    MMLToolbar toolbar = null;
    Checkbox toggleToolbar = null;

        /**
         * MUST BE Called when initialised as an application.
         */
    public void initApplication()
    {
        Image im = Toolkit.getDefaultToolkit().getImage("images/mml_templates.gif");
        imageTable = new Hashtable();
        imageTable.put(ALL_SYMBOLS, im);
        init();
    }
    
        /**
         * Initialises the applet.
         */
    public void init()
    {
        //setLayout(new BorderLayout());
        //add("Center", newFrame = new Button("Spawn New Frame"));
        //newFrame.setFont(new Font("Serif", Font.BOLD, 20));
        //newFrame.addActionListener(this);
        
        if (imageTable == null) 
        {
            Image im = getImage(getDocumentBase(), "images/mml_templates.gif");
            if (imageTable == null) imageTable = new Hashtable();
            imageTable.put(ALL_SYMBOLS, im);
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(im, 0);
            try
            {
                mt.waitForAll();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
            // then start up a new frame...
        editor = new MMLEditor();

        Frame parent = getParentFrame(this);
        if (parent == null) parent = new Frame();
        mmlDialog = new Dialog((Frame)parent, "MML Toolbar", false);
        toolbar = new MMLToolbar(imageTable);

        toggleToolbar = new Checkbox("Show Toolbar Dialog", true);
        toggleToolbar.addItemListener(this);

        setLayout(new BorderLayout());
        add("Center", editor);
        add("South", toggleToolbar);
        editor.requestFocus();
    
        toolbar.setCurrentEditor(editor);
        mmlDialog.setBounds(600, 100, 184, 370);
        mmlDialog.setVisible(true);
        mmlDialog.setLayout(new BorderLayout());
        mmlDialog.add("Center", toolbar);
        mmlDialog.invalidate();
        mmlDialog.validate();
    }

        /**
         * Item event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        if (ie.getSource() == toggleToolbar)
        {
            mmlDialog.setVisible(toggleToolbar.getState());
        }
    }

        /**
         * Gets the parent frame of a component.
         */
    public static Frame getParentFrame(Component comp)
    {
        if (comp == null) return null;
        Container parent = comp.getParent();
        while (parent != null && ! (parent instanceof Frame))
        {
            parent = parent.getParent();
        }
        return (Frame)parent;
    }
    
    public static void main(String args[])
    {
        Frame mainFrame = new Frame("MML Tester - Sri Panyam");
        MMLApplet applet = new MMLApplet();
        applet.initApplication();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add("Center", applet);
        mainFrame.setBounds(100, 100, 500, 400);
        mainFrame.setVisible(true);
        mainFrame.toFront();
        mainFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                //System.exit(0);
                ((Frame)e.getSource()).setVisible(false);
                ((Frame)e.getSource()).dispose();
            }
        });
    }
}
