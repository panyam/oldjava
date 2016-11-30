package com.sri.gui.core.containers;

import com.sri.gui.core.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SplitterPanel extends SContainer 
                           implements 
                                MouseMotionListener, 
                                MouseListener,
                                ComponentListener
{
    protected boolean horizontal = false;
    protected boolean dragRepaint = false;
    protected boolean isDragging = false;
    protected Point pressedAt = null;
    protected int draggingWhich = -1;
    protected Point prevLoc = null;

        /**
         * Default constructor.
         */
    public SplitterPanel() {
        this(false);
    }

        /**
         * Constructor specifying the orientation.
         */
    public SplitterPanel(boolean horiz) 
    {
        addComponentListener(this);
        horizontal = horiz;
    }

    public void addImpl(Component comp,Object constraints,int index)
    {
        int count = super.getComponentCount();
        int half = count >> 1;
        if (index < 0 && count > 0) index = 1 + (count >> 1);
        
        if (count > 0)
        {
            Splitter another = new Splitter(horizontal);
            another.addMouseListener(this);
            another.addMouseMotionListener(this);
            
            super.addImpl(another,constraints,-1);
        }

            // this is what we need to do...
            // if comp is a lightweight container or a component
            // then add it as it is... otherwise add the component
            // to a SContainer and add this container...
        if (comp instanceof java.awt.Panel ||
            comp instanceof java.awt.Button ||
            comp instanceof java.awt.TextComponent ||
            comp instanceof java.awt.List ||
            comp instanceof java.awt.Choice ||
            comp instanceof java.awt.Checkbox ||
            comp instanceof java.awt.Label ||
            comp instanceof java.awt.Canvas)
        {
            Container p = new SContainer();
            p.setLayout(new BorderLayout());
            super.addImpl(p,null,index);
            p.add("Center",comp);
            p.validate();
        } else
        {
            super.addImpl(comp,null,index);
        }
        
        validate();
        //autoArrange();
    }
    
    public int getComponentCount()
    {
        int cn = super.getComponentCount();
        return (cn == 0 ? 0 : 1 + (cn >> 1));
    }
    
        /**
         * Returns the component at the requested index.
         */
    public Component getComponent(int at)    
    {
        //return super.getComponent((super.getComponentCount() >> 1) + at);
        if (at >= getComponentCount())
        {
            throw new IllegalArgumentException ("No element at index: " + at);
        }
        return super.getComponent(at);
    }

        /**
         * Returns the splitter at the given index.
         */
    protected Splitter getSplitter(int index)
    {
        int s = getComponentCount();
        return (index >= s ?
                        null:
                        (Splitter)super.getComponent(index + s));
    }
    
    public Component[] getComponents()
    {
        Component out[] = new Component[getComponentCount()];
        for (int i = 0;i < out.length;i++)
        {
            out[i] = super.getComponent(i);
        }
        return out;
    }
    
        /**
         * Sets the location/offset of the given splitter bar.
         * 
         * 'which' is the splitter bar whose position is to be set
         * offset is a decimal number between 0 and 1 representing
         * the ratio of the current width/height excluding the insets
         * at which the new position is to be set.
         */
    public void setOffset(int which,double offset) {
        int cnt = getComponentCount();
        
        Component [] comps = super.getComponents();
        if (which < 0 || which >= cnt || cnt == 1) return ;
        Dimension d = getSize();
        Insets in = getInsets();
        int nspl = cnt - 1;
        Splitter spl = getSplitter(which);
        Splitter prev = (which > 0 ? getSplitter(which - 1) : null);
        Splitter next = (which < nspl - 1 ? getSplitter(which + 1) : null);
        
        Dimension thisSize = spl.getSize();
        Point thisLoc = spl.getLocation();
        
            // get absolute lower bound
        int lb = (horizontal ? in.top : in.left);
        
            // get absolute upper bound
        int ub = d.height - (horizontal ? in.bottom : in.right);
        
            // adjust lower bound according to previous
            // splitters location
        if (prev != null) 
        {
            Point prevLoc = prev.getLocation();
            Dimension prevSize = prev.getSize();
            lb = (horizontal ? prevLoc.y + prevSize.height : 
                               prevLoc.x + prevSize.width);
        }
        
            // adjust lower bound according to next
            // splitters location
        if (next != null) 
        {
            Point nextLoc = next.getLocation();
            ub = (horizontal ? nextLoc.y - thisSize.height : 
                               nextLoc.x - thisSize.width);
        }
        
        int pos = (int)(offset * (horizontal ? d.height : d.width));
        
                // some error checking
                // the new position MUST be between
                // the previous and next splitters
        if (pos < lb || pos > ub) 
        {
            throw new IllegalArgumentException(
                "Offset must be bound by previous and next splitter bars.");
        }
        
        if (horizontal)
        {
            spl.setLocation(thisLoc.x,pos);
        } else 
        {
            spl.setLocation(pos,thisLoc.y);
        }
        doLayout();
    }
    
        /**
         * Automatically arranges the components of the 
         * splitter panel.
         */
    public void autoArrange() {
        Dimension d = getSize();
        Insets in = getInsets();
        Component comps[] = getComponents();
        
        if (comps.length <= 1) return ;
        
        int numSplitters = (comps.length - 1);
        int wLeft = d.width - in.left - in.right;
        int hLeft = d.height - in.top - in.bottom;
        
        int numPerSplitter = (horizontal ? hLeft : wLeft) / (numSplitters + 1);
        int currX = in.left, currY = in.top;
        for (int i = 0;i < numSplitters;i++) {
            Splitter curr = this.getSplitter(i);
            if (horizontal) currY += numPerSplitter;
            else currX += numPerSplitter;
            int t = curr.getThickness();
            curr.setBounds(currX,currY,horizontal ? wLeft : t,horizontal ? t : hLeft);
        }
        doLayout();
    }    

        /**
         * Lays out the components that this panel holds.
         */
    public void doLayout() {
        int numComponents = getComponentCount();
        Insets insets = getInsets();
        Dimension dim = getSize();
        int wLeft = dim.width - insets.left - insets.right;
        int hLeft = dim.height - insets.top - insets.bottom;
        int currX = insets.left, currY = insets.top;
        
        Component components[] = getComponents();
        for (int i = 0;i < numComponents;i++) {
            Component curr = components[i];
            if (i == numComponents - 1) {    // if the last one
                                            // then dont use a splitter.
                curr.setBounds(currX,currY,wLeft - currX,hLeft - currY);
            } else {
                        // otherwise we have to use the next
                        // available splitter.
                Splitter spl = getSplitter(i);
                Dimension ss = spl.getSize();
                if (horizontal) 
                {
                    int currH = spl.getLocation().y - currY;
                    int currW = wLeft;
                    curr.setBounds(currX,currY,currW,currH);
                    currY += currH;
                    spl.setBounds(spl.getLocation().x,currY,currW,spl.getThickness());
                    currY += ss.height;
                } else 
                {
                    int currW = spl.getLocation().x - currX;
                    int currH = hLeft;
                    curr.setBounds(currX,currY,currW,currH);
                    currX += currW;
                    spl.setBounds(currX,spl.getLocation().y,spl.getThickness(),currH);
                    currX += ss.width;
                }
            }
            //curr.validate();
        }        
        paint(getGraphics());
    }
    
        /**
         * Does a layout only on the components surrounding
         * the particular component.
         * 
         * This way we dont need to lay out all the components
         * but only need to change the sizes of two components.
         * 
         * THis would make the layout much faster.  Especially when
         * there are a large number of components.
         * 
         * We assume that if which is not less than
         * then there will always be a component before
         * and after it.
         */
    private void doLayout(int which) {
        if (which < 0) return ;
        int ns = getComponentCount() - 1;
        Component before = getComponent(which);
        Component after = getComponent(which + 1);
        Splitter sp = getSplitter(which);
        Point ll = before.getLocation();
        Point al = after.getLocation();
        Dimension as = after.getSize();
        Point sl = sp.getLocation();
        Dimension ss = sp.getSize();

        if (horizontal) {
            before.setBounds(ll.x,ll.y,before.getSize().width,sl.y - ll.y);
            
                 // The bottom y coordinate of the second component ends.
            int yBottom = al.y + as.height;
                // the new y coordinate of the secondcomponent
            int y2 = sl.y + ss.height;
            after.setBounds(al.x,y2,as.width,yBottom - y2);
        } else {
            before.setBounds(ll.x,ll.y,sl.x - ll.x,before.getSize().height);
                 // The right y coordinate of the second component ends.
            int xRight = al.x + as.width;
                // the new x coordinate of the secondcomponent
            int x2 = sl.x + ss.width;
            after.setBounds(x2,al.y,xRight - x2,as.height);
        }
        before.validate();
        after.validate();
    }
    
        /**
         * Returns the index of the requested
         * splitter component
         * 
         * TODO:: Must speed up search by using binary search...
         */
    protected int indexOfSplitter(Splitter spl) {    
                    /**
                     * For the time we are doin a linear
                     * search.. but this should be 
                     * speeded up by makin it a binary search.
                     * The reason is that all components are 
                     * alligned one after the other.
                     * So all splitters will be sorted
                     * by either their x or their y coordinates
                     * depending on whether horizontally
                     * arranged or not...
                     */
        int cnt = getComponentCount() - 1;
        for (int i = 0;i < cnt;i++)
        {
            if (getSplitter(i) == spl) return i;
        }
        return -1;
    }
    
    public void mouseDragged(MouseEvent e) {
        Object src = e.getSource();
        
        if (src instanceof Splitter) {
            Splitter curr = (Splitter)src;
            int index = draggingWhich;
            if (index < 0) return ;
            int p = index - 1;
            int n = index + 1;
            int numSplitters = getComponentCount() - 1;
            Splitter prev = (p < 0 ? null : getSplitter(p));
            Splitter next = (n >= numSplitters ? null : getSplitter(n));
            
            Dimension ds = getSize();
            Insets in = getInsets();
            Rectangle pb = (prev == null ? new Rectangle() : prev.getBounds());
            Rectangle nb = (next == null ? new Rectangle() : next.getBounds());
            Dimension cs = curr.getSize();
            Point currLoc = ((Splitter)src).getLocation();
            int wLeft = ds.width - in.left - in.right;
            int hLeft = ds.height - in.top - in.bottom;

            curr.paint(getGraphics(),prevLoc.x,prevLoc.y,false);
            
            if (horizontal) {
                int nextY = e.getY() + currLoc.y - pressedAt.y;
                int lowerbound = in.top;
                int upperbound = lowerbound + hLeft - cs.height;
                
                if (prev != null) lowerbound = pb.y + pb.height;
                if (next != null) upperbound = nb.y - cs.height;
                
                if (nextY < lowerbound) nextY = lowerbound;
                if (nextY > upperbound) nextY = upperbound;
                curr.setBounds(in.left,nextY,wLeft,curr.getThickness());
            } else {
                int nextX = e.getX() + currLoc.x - pressedAt.x;
                int lowerbound = in.left;
                int upperbound = lowerbound + wLeft - cs.width;
                
                if (prev != null) lowerbound = pb.x + pb.width;
                if (next != null) upperbound = nb.x - cs.width;
                
                if (nextX < lowerbound) nextX = lowerbound;
                if (nextX > upperbound) nextX = upperbound;
                curr.setBounds(nextX,in.top,curr.getThickness(),hLeft);
            }
            if (dragRepaint) doLayout(draggingWhich);
        }
    }
    
    
    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mousePressed(MouseEvent e) {
        Object src = e.getSource();
        if (src instanceof Splitter) {
            Splitter spl = (Splitter)src;
            if (spl.isEnabled())
            {
                draggingWhich = indexOfSplitter(spl);
                if (draggingWhich >= 0) {
                    prevLoc = ((Splitter)src).getLocation();
                    pressedAt = e.getPoint();
                    isDragging = true;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) { 
        if (isDragging) {
            isDragging = false;
            draggingWhich = -1;
            doLayout();
            validate();
        }
    }
    public void mouseClicked(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }
    
    public void componentResized(ComponentEvent e) {
        if (e.getSource() == this) doLayout();
    }
    
    public void componentShown(ComponentEvent e) {
        if (e.getSource() == this) doLayout();        
    }
    
    public void componentHidden(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
    }
    
}

class Splitter extends Canvas
                      implements 
                        MouseListener,
                        MouseMotionListener
{
    protected boolean isDragging = false;
    
    protected boolean horizontal = true;

    Splitter() {
        this(false);
    }

    Splitter(boolean horizontal) {
        this.horizontal = horizontal;

        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(new Color(173,156,148));
        
        if (horizontal) {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        }
    }

    public int getWidth() {
        return getSize().width;
    }

    public int getHeight() {
        return getSize().height;
    }

    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == this) {
            requestFocus();
            isDragging = true;
            paint(getGraphics());
        }
    }

    public void mouseReleased(MouseEvent e) { 
        if (isDragging) {
            isDragging = false;
            paint(getGraphics());
        }
    }
    public void mouseClicked(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }

    public void paint(Graphics g,int x,int y,boolean isd)
    {
        g.setColor(getBackground());
        Dimension d = getSize();
        g.fillRect(x,y,d.width,d.height);
        g.setColor(Color.lightGray);
        g.fill3DRect(x,y,d.width - 1,d.height - 1,!isd);
    }
    
    public void paint(Graphics g) {
        paint(g,0,0,isDragging);
    }
    
    public boolean isHorizontal() {
        return horizontal;
    }
    
    public int getThickness() {
        return 6;
    }
}
