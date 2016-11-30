package com.sri.gui.core.layouts;

import java.awt.*;

/**
 * ColumnLayout lays out components in a column.  At 
 * construction time, both horizontal orientation and vertical 
 * orientation may be specified, along with the gap to use 
 * between components.<p>
 *
 * Horizontal orientation must be one of the following:
 * <dl>
 * <dd> LEFT 
 * <dd> CENTER  
 * <dd> RIGHT
 * </dl>
 *
 * Vertical orientation must be one of the following:
 * <dl>
 * <dd> TOP
 * <dd> CENTER  
 * <dd> BOTTOM
 * </dl>
 *
 * @see     Orientation
 * @see     RowLayout
 */
public class ColumnLayout implements LayoutManager {
    static private int _defaultGap = 5;


        /**
         * If stretch is true then all the components in
         * this column will have the same width.
         */
    protected boolean stretch = false;

    private int         gap;
    private Orientation horizontalOrientation;
    private Orientation verticalOrientation;

    public ColumnLayout() {
        this(0);
    }

    public ColumnLayout(int gap) {
        this(gap,true);
    }

    public ColumnLayout(boolean stretch) {
        this(0,stretch);
    }

    public ColumnLayout(int gap,boolean stretch) {
        this(Orientation.LEFT,Orientation.CENTER,gap,true);
    }

    public ColumnLayout(Orientation horizontalOrient, 
                        Orientation verticalOrient) {
        this(horizontalOrient,verticalOrient,0,true);
    }

    public ColumnLayout(Orientation horizontalOrient, 
                        Orientation verticalOrient, boolean stretch) {
        this(horizontalOrient,verticalOrient,0,stretch);
    }

    public ColumnLayout(Orientation horizontalOrient, 
                        Orientation verticalOrient, int gap) {
        this(horizontalOrient,verticalOrient,gap,true);
    }

    public ColumnLayout(Orientation horizontalOrient, 
                        Orientation verticalOrient, int gap,boolean stretch) {
        setGap(gap);
        setHorizontalOrientation(horizontalOrient);
        setVerticalOrientation(verticalOrient);
        setStretch(stretch);
    }

    public void addLayoutComponent(String name, 
                                   Component comp) {
    }
    public void removeLayoutComponent(Component comp) { 
    }

    public Dimension preferredLayoutSize(Container target) {
        Insets    insets      = target.getInsets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.getComponentCount();
        Component comp;
        Dimension d;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.getPreferredSize();
                if(i > 0) 
                    dim.height += gap;

                dim.height += d.height;
                dim.width   = Math.max(d.width, dim.width);
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top  + insets.bottom;
        return dim;
    }
    public Dimension minimumLayoutSize(Container target) {
        Insets    insets      = target.getInsets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.getComponentCount();
        Component comp;
        Dimension d;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.getMinimumSize();

                dim.width  = Math.max(d.width, dim.width);
                dim.height += d.height;

                if(i > 0) dim.height += gap;
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top  + insets.bottom;

        return dim;
    }

    public void layoutContainer(Container target) {
        Insets    insets        = target.getInsets();
        int       top           = insets.top;
        int       left          = insets.left;
        int       ncomponents   = target.getComponentCount();
        Dimension preferredSize = target.getPreferredSize();
        Dimension targetSize    = target.getSize();
        int       width         = targetSize.width - insets.left - insets.right;
        Component comp;
        Dimension ps;

        if(verticalOrientation == Orientation.CENTER) {
            top += (targetSize.height/2) - (preferredSize.height/2);
        }
        else if(verticalOrientation == Orientation.BOTTOM) {
            top = targetSize.height - preferredSize.height + insets.top;
        }

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                ps = comp.getPreferredSize();

                if (!stretch) {
                    if(horizontalOrientation == Orientation.CENTER) {
                        left = (targetSize.width/2) - (ps.width/2);
                    }
                    else if(horizontalOrientation == Orientation.RIGHT) {
                        left = targetSize.width - ps.width - insets.right;
                    }
                }

                int w = (stretch ? width : ps.width);
                comp.setBounds(left,top,w,ps.height);
                top += ps.height + gap;
            }
        }
    }

    public void setGap(int gap) {
        if (gap < 0) throw new IllegalArgumentException("Cannot have negative gap");
        this.gap = gap;
    }

    public void setVerticalOrientation(Orientation or) {
        if (or != Orientation.TOP    &&
            or != Orientation.CENTER &&
            or != Orientation.BOTTOM) {
            throw new IllegalArgumentException (
                        "Invalid vertical orientation.");
        }
        this.verticalOrientation    = or;
    }

    public void setHorizontalOrientation(Orientation or) {
        if (or != Orientation.LEFT &&
            or != Orientation.CENTER &&
            or != Orientation.RIGHT) {
            throw new IllegalArgumentException (
                        "Invalid horizontal orientation.");
        }
        this.horizontalOrientation  = or;
    }

    public void setStretch(boolean str) {
        this.stretch = str;
    }
}
