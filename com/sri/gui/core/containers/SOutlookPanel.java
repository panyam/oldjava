package com.sri.gui.core.containers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.sri.gui.core.button.*;
import com.sri.gui.core.*;

/**
 * Creates a panel where items are separated by buttons 
 * 
 * TODO:: Make the selection smooth rather than quick - more like outlook express.
 */
public class SOutlookPanel extends JComponent
                          implements Runnable, ComponentListener
{
    Thread moverThread = null;
    
        /**
         * The current item whose panel is being displayed
         */
    protected int currItem = 0;
    
        /**
         * The number of items that are on the top.
         */
    protected int nTop = 1;
    
        /**
         * Default Constructor.
         */
    public SOutlookPanel()
    {
        setLayout(null);
        addComponentListener(this);
    }
    
    public void addImpl(Component c,Object constraints, int index)
    {
        throw new IllegalArgumentException("Use \"addItem\" instead of the \"add\" method.");
    }    
    
        /**
         * Adds a new item.
         */
    public void addItem(JButton button, Component comp)
    {
        super.addImpl(comp,null,getComponentCount());
        super.addImpl(button,null,-1);
        
        button.setVisible(false);
        comp.setVisible(false);
        //button.setTextPosition(SButton.EAST);
        //button.setContentAllignment(SButton.CENTER);
        //button.setRollOver(false);
        
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Object src = e.getSource();
                if (src instanceof JButton)
                {
                    int index = getControllerIndex((JButton)src);
                    if (index >= 0 && currItem != index)
                    {
                        selectItem(index);
                    }
                }
            }
        });
        doLayout();
    }
    
        /**
         * Set the component at the given location.
         */
    public void setComponent(int i, Component c)
    {
        throw new IllegalArgumentException("Not implemented yet.");
    }
    
        /**
         * Returns the component count.
         */
    public int getComponentCount()
    {
        return super.getComponentCount() >> 1;
    }
    
        /**
         * Returns the component at the given index.
         */
    public Component getComponent(int which)
    {
        return super.getComponent(which);
    }
    
        /**
         * Returns the component at the given index.
         */
    public JButton getController(int which)
    {
        return (JButton)super.getComponent(getComponentCount() + which);
    }
    
    public void removeItem(JButton button)
    {
    }
    
        /**
         * Runnable interface function.
         */
    public void run()
    {
        Thread currThread = Thread.currentThread();
        Panel temp = new Panel(null);
    }

        /**
         * Returns the preferred size of this container.
         */
    public Dimension getPreferredSize()
    {
        int maxWidth = 0;
        int maxHeight = 0;
        int totalHeight = 0;
        int nc = getComponentCount();
        
        for (int i = 0;i < nc;i++)
        {
                    // get the current button...
            JButton btn = getController(i);
            Component curr = getComponent(i);
            Dimension ss = btn.getPreferredSize();
            Dimension cs = curr.getPreferredSize();
            if (maxWidth < ss.width) maxWidth = ss.width;
            if (maxWidth < cs.width) maxWidth = cs.width;
            totalHeight += ss.height;
            if (maxHeight < cs.height) maxHeight = cs.height;
        }
        
        return new Dimension(maxWidth,maxHeight + totalHeight);
    }

        /**
         * Returns the index of the requested 
         * controller.
         * 
         * TODO:: Need to make search a binary one rather than a linear one.
         */
    public int getControllerIndex(JButton btn)
    {
        int nc = getComponentCount();
        for (int i = 0;i < nc;i++)
        {
            if (btn == getController(i)) return i;
        }
        return -1;
    }
    
        /**
         * Selects the current item.
         */
    public void selectItem(int which)
    {
        currItem = which;
        nTop = which + 1;
        //validate();
        doLayout();
    }
    
        /**
         * Lays out the components.
         */
    public synchronized void doLayout()
    {
        boolean useAutoHeights = false;
        if (!isVisible() || getParent() == null) return ;
        Dimension d = getSize();
        Insets in = getInsets();
        int    dh = d.height - in.top - in.bottom;
        int dw = d.width - in.left - in.right;
        int nc = getComponentCount();
        
        Component curr = getComponent(currItem);
        //Dimension cs = curr.getPreferredSize();
        //if (cs.width > dw) cs.width = dw;

        int totalButtonHeight = 0;
        for (int i = 0;i < nc;i++)
        {
                    // get the current button...
            getComponent(i).setVisible(false);
            JButton btn = getController(i);
            btn.setVisible(false);
            Dimension ss = btn.getPreferredSize();
            btn.setSize(ss);
            totalButtonHeight += ss.height;
        }

        if (totalButtonHeight > dh - 10)
        {        // then automatically adjust all the buttons' height to fit in.
            useAutoHeights = true;
            totalButtonHeight = dh - 10;
        }

            // now we set the size of the current...
        int rem = dh - totalButtonHeight;
        //if (cs.height > rem) cs.height = rem;
        //cs.width = dw;
        //cs.height = rem;
        
        //curr.setSize(cs);
        int x = in.left, y = in.top;
        int total1 = 0, total2 = 0;
        for (int i = 0, ah = totalButtonHeight / nc;i < nTop;i++)
        {
                    // get the current button...
            JButton btn= getController(i);
            btn.setLocation(x,y);
            btn.setSize(dw,useAutoHeights ? ah : btn.getSize().height);
            y += btn.getSize().height;
            btn.setVisible(true);
        }
        total1 = y;
        
        y = d.height - in.bottom;
        for (int i = nc - 1, ah = totalButtonHeight / nc; i >= nTop;i--)
        {
                    // get the current button...
            JButton btn = getController(i);
            btn.setSize(dw,useAutoHeights ? ah : btn.getSize().height);
            y -= btn.getSize().height;
            btn.setLocation(x,y);
            btn.setVisible(true);
        }
            // finally place the current component
        //curr.setSize(dw,rem);
        //curr.setLocation(in.left,total1);
        curr.setBounds(in.left,total1, dw,rem);
        curr.setVisible(true);
    }
    
    public void componentResized(ComponentEvent e)
    {
        if (e.getSource() == this)
        {
            doLayout();
        }
    }
    
    public void componentMoved(ComponentEvent e) { }
    public void componentShown(ComponentEvent e) { componentResized(e); }
    public void componentHidden(ComponentEvent e) { }
}
