package com.sri.gui.core.button;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;

public class SActionButton extends SButton {
		/**
		 * The action listener.
		 */
    protected transient ActionListener actionListener = null;
	
		/**
		 * The action command.
		 */
    protected String actionCommand = "";

		/**
		 * Default constructor.
		 */
	public SActionButton() {
        this(null,"");
    }

		/**
		 * Constructor with title.
		 */
	public SActionButton(String title)
	{
		this(null,title);
	}
	
		/**
		 * Constructor specifying all mouse out, mouse pressed 
		 * images and the title.
		 */
    public SActionButton(Image img, String title) {
        super();
        setMouseOutImage(img);
        setMousePressedImage(img);
		setMouseOverImage(img);
        setText(title);

        addMouseListener(this);
        addMouseMotionListener(this);
    }
	
		/**
		 * Sets the action command.
		 */
    public synchronized void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

		/**
		 * Gets the action command.
		 */
    public String getActionCommand() {
        return actionCommand;
    }

		/**
		 * Adds an action listener.
		 */
    public synchronized void addActionListener(ActionListener l) {
        if (l != null) {
	        actionListener = AWTEventMulticaster.add(actionListener, l);
        }
    }

		/**
		 * Removes action listener
		 */
    public synchronized void removeActionListener(ActionListener l) {
        if (l != null) {
	        actionListener = AWTEventMulticaster.remove(actionListener, l);
        }
    }
	
		/**
		 * Mouse action event handler.
		 */
	protected void processActionEvent (ActionEvent e) {
		if (isEnabled() && actionListener != null && e != null) {
			actionListener.actionPerformed(e);
		}
	}

		/**
		 * Mouse pressed event handler.
		 */
    public void mousePressed(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
	        requestFocus();
			mouseDragged(e);
		}
    }

		/**
		 * Mouse relesed event handler.
		 */
    public void mouseReleased(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
			if (contains(e.getPoint())) {
                if ((flags & PRESSED_INSIDE_MASK) != 0) {
                    processActionEvent(new ActionEvent(
                                        this,ActionEvent.ACTION_PERFORMED,
                                        actionCommand));
					flags &= (0xffffffff ^ PRESSED_INSIDE_MASK);
                }
				setMouseState(MOUSE_OVER);
			} else
			{
				setMouseState(isRollOver() ? MOUSE_OUT : MOUSE_OVER);
			}
            paint(getGraphics());
        }
    }

		/**
		 * Mouse entered event handler.
		 */
    public void mouseEntered(MouseEvent e) {
        if (isEnabled() && e.getSource() == this && getMouseState() != MOUSE_OVER) {
            setMouseState(MOUSE_OVER);
            paint(getGraphics());
        }
    }

		/**
		 * Mouse exited event handler.
		 */
    public void mouseExited(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
			int ms = getMouseState();
			int ns = (isRollOver() ? MOUSE_OUT : MOUSE_OVER);
			if (ns != ms)
			{
	            setMouseState(ns);
		        paint(getGraphics());
			}
        }
    }

		/**
		 * Mouse moved event handler.
		 */
    public void mouseMoved(MouseEvent e) {
        if (isEnabled() && e.getSource() == this && getMouseState() != MOUSE_OVER) {
			setMouseState(MOUSE_OVER);
			paint(getGraphics());
        }
    }

		/**
		 * Mouse dragging event handler.
		 */
    public void mouseDragged(MouseEvent e) {
        if (isEnabled() && e.getSource() == this) {
            if (getMouseState() != MOUSE_PRESSED) {
                setMouseState(MOUSE_PRESSED);
				flags |= PRESSED_INSIDE_MASK;
                paint(getGraphics());
            }
        }
    }
}
