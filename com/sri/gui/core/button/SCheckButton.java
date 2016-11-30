package com.sri.gui.core.button;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.core.*;

/**
 * Creates an imaged check button.
 */
public class SCheckButton extends SButton implements ItemSelectable {
    protected transient ItemListener itemListener = null;
    protected boolean state = false;
    protected SCheckGroup group = null;
	
		/**
		 * Default constructor.
		 */
	public SCheckButton() {
        this(null,null,null);
    }

		/**
		 * Constructor with title and check group.
		 */
	public SCheckButton(String title, SCheckGroup gr) {
        this(null,title,gr);
    }

		/**
		 * Constructor with title.
		 */
	public SCheckButton(String title) {
        this(null,title,null);
    }

		/**
		 * Constructor with title and title.
		 */
	public SCheckButton(Image allMouse, String title) {
        this(allMouse,title,null);
    }

		/**
		 * Constructor with mosue out and pressed image and title and check group.
		 */
    public SCheckButton(Image img, String title, SCheckGroup group)
    {
        super();
        setMouseOutImage(img);
        setMousePressedImage(img);
		setMouseOverImage(img);
        setText(title);
		this.setCheckGroup(group);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

        /**
         * Returns the state of the check button.
         */
    public boolean getState() {
        return state;
    }

        /**
         * Sets the state of the check button.
         */
    public synchronized void setState(boolean st) {
		setState(st,false);
    }

        /**
         * Sets the state of the check button.
         */
    public synchronized void setState(boolean st, boolean internalOnly) {
        if (group == null) {
            state = st;
        } else {
            if (st) {
                state = true;
                if (!internalOnly) group.setSelected(this);
            } else if (group.getSelected() == this && group.isMutex()) {
                state = true;
			} else {
				state = false;
				if (group.getSelected() == this) {
					group.setSelected(null);
				}
			}
        }
		setMouseState(state ? MOUSE_PRESSED : (isRollOver() ? MOUSE_OUT : MOUSE_OVER));
        paint(getGraphics());
    }

        /**
         * Adds an item listener to this check box.
         */
    public synchronized void addItemListener(ItemListener l) {
        if (l != null) {
            itemListener = AWTEventMulticaster.add(itemListener, l);
        }
    }
	
	protected void processItemEvent(ItemEvent e) {
		if (e != null && itemListener != null) {
			itemListener.itemStateChanged(e);
		}
	}

        /**
         * Removes an item listener to this check box.
         */
    public synchronized void removeItemListener(ItemListener l) {
        if (l != null) {
            itemListener = AWTEventMulticaster.remove(itemListener, l);
        }
    }

    public Object [] getSelectedObjects() {
        if (state) {
            Object out[] = { this };
            return out;
        } 
        return null;
    }

    public SCheckGroup getCheckGroup() {
        return group;
    }

    public synchronized void setCheckGroup(SCheckGroup g) {
        this.group = g;
    }
	
		/**
		 * Mouse pressed event handler.
		 */
    public void mousePressed(MouseEvent e) {
		requestFocus();
		mouseDragged(e);
    }

		/**
		 * Mouse dragging event handler.
		 */
    public void mouseDragged(MouseEvent e) {
        if (isEnabled() && e.getSource() == this && getMouseState() != MOUSE_PRESSED) {
            setMouseState(MOUSE_PRESSED);
			flags |= PRESSED_INSIDE_MASK;
            paint(getGraphics());
        }
    }
	
		/**
		 * Mouse relesed event handler.
		 */
    public void mouseReleased(MouseEvent e) {
		if (isEnabled() && e.getSource() == this) {
			if (this.contains(e.getPoint())) {
				setState(!state);
				processItemEvent(new ItemEvent(this,
											   state ? ItemEvent.SELECTED : 
													   ItemEvent.DESELECTED,
											   this,
											   ItemEvent.ITEM_STATE_CHANGED));
					flags &= (0xffffffff ^ PRESSED_INSIDE_MASK);
			} else {
				setMouseState(state ? MOUSE_PRESSED : MOUSE_OUT);
			}
			paint(getGraphics());
		}
    }

		/**
		 * Mouse entered event handler.
		 */
    public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
    }

		/**
		 * Mouse moved event handler.
		 */
    public void mouseMoved(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
			int ms = getMouseState();
			int ns = state ? MOUSE_PRESSED : MOUSE_OVER;
			if (ns != ms)
			{
				setMouseState(state ? MOUSE_PRESSED : MOUSE_OVER);
	            paint(getGraphics());
			}
        }
    }

		/**
		 * Mouse exited event handler.
		 */
    public void mouseExited(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
			setMouseState(state ? MOUSE_PRESSED : (isRollOver() ? MOUSE_OUT : MOUSE_OVER));
            paint(getGraphics());
        }
    }
}
