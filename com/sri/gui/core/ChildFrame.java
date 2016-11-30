package com.sri.gui.core;

import java.awt.*;
import java.awt.event.*;

public class ChildFrame extends SContainer 
						implements 
							FocusListener, 
							MouseListener, 
							MouseMotionListener 
{
	protected final static char NO_WHERE = 0;
	protected final static char AT_E_BORDER = 1;
	protected final static char AT_NE_BORDER = 2;
	protected final static char AT_N_BORDER = 3;
	protected final static char AT_NW_BORDER = 4;
	protected final static char AT_W_BORDER = 5;
	protected final static char AT_SW_BORDER = 6;
	protected final static char AT_S_BORDER = 7;
	protected final static char AT_SE_BORDER = 8;
	protected final static char AT_CLOSE_BUTTON = 9;
	protected final static char AT_MAXIMIZE_BUTTON = 10;
	protected final static char AT_ICON = 11;
	protected final static char AT_MOVING_AREA = 12;

	protected boolean hasFocus = true;
	protected boolean isMoveAble = true;
	protected boolean isResizable = true;
	protected boolean showIcon = true;
	protected Point pressedAt = null;
	protected boolean isDragging = false;
    protected WindowListener windowListener = null;
	protected String title = "";

    protected Insets insets = new Insets(0,0,0,0);
    protected boolean showMaximize = true;
    protected boolean showMinimize = true;
    protected boolean showRestore = true;
    protected boolean showSystemMenu = true;

	public ChildFrame (String t) {
		title = t;
		addFocusListener(this);
	}
	
	public void focusLost(FocusEvent e) {
		if (e.getSource() == this) {
			hasFocus = false;
			paint(getGraphics());
		}
	}
	
	public void focusGained(FocusEvent e) {
		if (e.getSource() == this) {
			hasFocus = true;
			paint(getGraphics());
		}
	}

	public boolean isFocusTraversable() {
		return true;
	}
	
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			requestFocus();
		    pressedAt = e.getPoint();
			isDragging = true;
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (e.getSource() == this) {
			if (isMoveAble && isDragging) {
	            Point currLoc = getLocation();

				int nx = e.getX() + currLoc.x - pressedAt.x;
				int ny = e.getY() + currLoc.y - pressedAt.y;
				setLocation(nx,ny);
			}
        }
	}
	
    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
	
    public void mouseReleased(MouseEvent e) { 
		if (isDragging) {
			isDragging = false;
			paint(getGraphics());
		}
	}
    public void mouseClicked(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }
	
    public void paint(Graphics g) {
		super.paint(g);
    }

    public synchronized void addWindowListener(WindowListener l) {
        if (l != null) {
	        windowListener = AWTEventMulticaster.add(windowListener, l);
        }
    }

    public synchronized void removeWindowListener(WindowListener l) {
        if (l != null) {
	        windowListener = AWTEventMulticaster.remove(windowListener, l);
        }
    }
	
	protected char getMouseLocation(int x,int y) {
		Dimension d = getSize();
		
		return NO_WHERE;
	}
}
