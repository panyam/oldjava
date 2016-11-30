package com.sri.gui.core.layouts;

import java.awt.*;
/**
 * RowLayout lays out components in a row.  At construction 
 * time, both horizontal orientation and vertical orientation 
 * may be specified, along with the gap to use between 
 * components.<p>
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
 * @see     RowLayout
 * @see     Orientation
 */
public class RowLayout implements LayoutManager {
    static private int _defaultGap = 5;


        /**
         * If stretch is true then all the components in
         * this column will have the same width.
         */
    protected boolean stretch = false;

		/**
		 * The vertical gap between components.
		 */
    private int         vgap;

		/**
		 * The horizontal gap between components.
		 */
    private int         hgap;
	
		/**
		 * The horizontal alignment of components
		 */
    private Orientation horizontalOrientation = Orientation.CENTER;
	
		/**
		 * The horizontal alignment of components
		 */
    private Orientation verticalOrientation = Orientation.CENTER;
	
		/**
		 * The limit on the number of components that can be placed vertically
		 */
	private int limit = -1;

	public RowLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }

		/**
		 * Sets the horizontal gap between components.
		 */
    public void setHGap(int hgap) {
        if (hgap < 0) throw new IllegalArgumentException("Cannot have negative horizontal gap");
        this.hgap = hgap;
    }

		/**
		 * Sets the vertical gap between components.
		 */
    public void setVGap(int vgap) {
        if (vgap < 0) throw new IllegalArgumentException("Cannot have negative vertical gap");
        this.vgap = vgap;
    }

		/**
		 * Sets the vertical orientation.
		 */
    public void setVerticalOrientation(Orientation or) {
        if (or != Orientation.TOP    &&
            or != Orientation.CENTER &&
            or != Orientation.BOTTOM) {
            throw new IllegalArgumentException (
                        "Invalid vertical orientation.");
        }
        this.verticalOrientation    = or;
    }

		/**
		 * Sets the horizontal orientation.
		 */
    public void setHorizontalOrientation(Orientation or) {
        if (or != Orientation.LEFT &&
            or != Orientation.CENTER &&
            or != Orientation.RIGHT) {
            throw new IllegalArgumentException (
                        "Invalid horizontal orientation.");
        }
        this.horizontalOrientation  = or;
    }

		/**
		 * Sets whether this layout
		 * stretches the components
		 * to the maximum size.
		 */
    public void setStretch(boolean str) {
        this.stretch = str;
    }
	
		/**
		 * Sets the limit on the number of items
		 * to be placed in a column.
		 */
	public void setLimit(int lim)
	{
		if (lim == 0)
		{
			throw new IllegalArgumentException("Cannot have a 0 limit");
		}
		this.limit = lim;
	}
	
		/**
		 * Returnst the preferredsize of a container.
		 */
    public Dimension preferredLayoutSize(Container target) {
        Insets    insets      = target.getInsets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.getComponentCount();
        Component comp;
        Dimension d;

		int totalH = 0;
		int currLineMaxH = 0;
		int maxLineW = 0;
		int currLineMaxW = 0;
		
		int which = 0;
		int nRows = (limit > 0 ? ncomponents / limit : 1);
		int nCols = (limit > 0 ? limit : ncomponents);
		for (int i = 0;i < nRows;i++)
		{
			for (int j = 0;j < nCols && which < ncomponents;j++, which++)
			{
	            comp = target.getComponent(which);
		        if(comp.isVisible()) {
	                d = comp.getPreferredSize();

					currLineMaxW += d.width;
					currLineMaxH = Math.max(d.height,currLineMaxH);
	
		            if(j > 0) currLineMaxW += hgap;
	            }
			}
			maxLineW = Math.max(currLineMaxW,maxLineW);
			currLineMaxW = 0;
			totalH += currLineMaxH;
			currLineMaxH = 0;
			if (i > 0) totalH += vgap;
		}
        dim.width  = insets.left + insets.right + maxLineW;
        dim.height = insets.top + insets.bottom + totalH;

        return dim;
    }
	
    public Dimension minimumLayoutSize(Container target) {
		return this.preferredLayoutSize(target);
    }
	
    public void layoutContainer(Container target) {
        Insets    insets      = target.getInsets();
        int       ncomponents = target.getComponentCount();
        int       top         = insets.top;
        int       left        = insets.left;
        Dimension targetSize  = target.getSize();
        Component comp;
        Dimension ps;
		
		int which = 0;
		int nRows = (limit > 0 ? ncomponents / limit : 1);
		int nCols = (limit > 0 ? limit : ncomponents);
		int currX = left, currY = top;
		int remW = targetSize.width - insets.left - insets.right;
		int remH = targetSize.height - insets.top - insets.bottom;

		for (int i = 0;i < nRows;i++)
		{
			int maxRowHeight = 0;		// maximum height for this row.
			currX = left;
			int rowHeight = 0;
			
				// if not left horiz alignment then find the max row width.
			if (horizontalOrientation != Orientation.LEFT || 
							verticalOrientation != Orientation.TOP || stretch)
			{
				int rowWidth = 0;
				int w = which;
				for (int j = 0;j < nCols && w < ncomponents;j++, w++)
				{
					comp = target.getComponent(w);
					if (comp.isVisible())
					{
						ps = comp.getPreferredSize();
						rowHeight = Math.max(rowHeight,ps.height);
						rowWidth += ps.width;
						if (j > 0) rowWidth += hgap;
					}
				}
				if (horizontalOrientation == Orientation.CENTER)
				{
					currX = left + (remW - rowWidth) / 2;
				} else 
				{
					currX = left + remW - rowWidth;
				}
			}

			int thisY = currY;
			
				// by now it knows what the row's maximum height 
				// and width are.... even if row's height are
				// know it will be recomputed in here..
			for (int j = 0;j < nCols && which < ncomponents;j++, which++)
			{
				comp = target.getComponent(which);
				if (comp.isVisible())
				{
					ps = comp.getPreferredSize();
					if (ps.height > rowHeight) rowHeight = ps.height;
					if (!stretch) {
				        if(verticalOrientation == Orientation.CENTER) {
			                currY += (rowHeight - ps.height)/2;
		                }
	                    else if(verticalOrientation == Orientation.TOP) {
						    currY = currY;
					    }
				        else if(verticalOrientation == Orientation.BOTTOM) {
			                currY += rowHeight - ps.height;
		                }
	                }

		            comp.setBounds(currX,currY,ps.width,(stretch ? rowHeight : ps.height));
	                currX += ps.width + hgap;
				}
			}
			currY = thisY + rowHeight + vgap;
		}
    }
}
