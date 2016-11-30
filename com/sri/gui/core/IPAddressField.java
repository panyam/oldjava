package com.sri.gui.core;

import java.awt.*;
import java.awt.event.*;

/**
 * A field for entering IP Addresses
 */
public class IPAddressField extends Panel
							implements 
							KeyListener,
							ActionListener
{
		/**
		 * The various values.
		 */
	protected HexDigitField fields[];

		/**
		 * Gap between fields.
		 */
	protected final static int FIELD_GAP = 10;
	
	protected ActionListener actionlistener = null;
	protected KeyListener keylistener = null;

	protected Dimension prefSize = new Dimension();
	/**
	 * Default Constructor.
	 */
	public IPAddressField(int nFields)
	{
		fields = new HexDigitField[nFields];
		for (int i = 0;i < nFields;i++)
		{
			fields[i] = new HexDigitField();
			fields[i].addKeyListener(this);
			fields[i].addActionListener(this);
			System.out.println("PS = " + fields[i].getPreferredSize());
			add(fields[i]);
		}
		setBackground(SystemColor.window);
		setForeground(SystemColor.textText);
		setLayout(null);
	}

		/**
		 * Lays out the components.
		 */
	public void doLayout()
	{
		Dimension d = getSize(), ps;
		int x = 0;
		int totalWidth = (FIELD_GAP * (fields.length - 1)) +
						 (fields.length * 
						  fields[0].getPreferredSize().width);
		if (totalWidth < d.width)
		{
			x = (d.width - totalWidth) / 2;
		}
		for (int i = 0;i < fields.length;i++)
		{
			ps = fields[i].getPreferredSize();
			fields[i].setBounds(x, 0, ps.width, d.height);
			x += ps.width + FIELD_GAP;
		}
	}
	
	/**
	 * Clears the text field.
	 */
	public synchronized void clearText()
	{
		for (int i = 0;i < fields.length;i++) fields[i].setText("");
	}

		/**
		 * Does the painting.
		 */
	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();
		Dimension currSize;
		//prepareBuffer();
		int x = fields[0].getLocation().x, y = 0;
		for (int i = 0;i < fields.length - 1;i++)
		{
			currSize = fields[i].getSize();
			x += currSize.width;
			g.fillOval(x + (FIELD_GAP / 2) - 2, y + (d.height / 2) - 2, 4, 4);
			x += FIELD_GAP;
		}
	}
	
		/**
		 * Update function.
		 */
	public void update(Graphics g)
	{
	}

		/**
		 * Get the preferred size.
		 */
	public Dimension getPreferredSize()
	{
		Dimension d = null;
		prefSize.width = prefSize.height = 0;
		for (int i = 0;i < fields.length;i++)
		{
			d = fields[i].getPreferredSize();
			prefSize.width += d.width;
			if (prefSize.height < d.height) prefSize.height = d.height;
		}
		prefSize.width += FIELD_GAP * (fields.length - 1);
		return prefSize;
	}
	
		/**
		 * Find which event generated a given field.
		 */
	public int findFieldWithEvent(AWTEvent e)
	{
		for (int i = 0;i < fields.length;i++)
		{
			if (e.getSource() == fields[i]) return i;
		}
		return -1;
	}
		/**
		 * Action event handler.
		 */
	public void actionPerformed(ActionEvent e)
	{
		int tfi = findFieldWithEvent(e);
		if (tfi < 0) return ;
		processActionEvent(new ActionEvent(this, 
										   ActionEvent.ACTION_PERFORMED, 
										   "Field_" + tfi));
	}
	
		/**
		 * Key Typed event handler.
		 */
	public synchronized void keyTyped(KeyEvent e)
	{
		int tfi = findFieldWithEvent(e);
		if (tfi < 0) return ;
		/*char ch = e.getKeyChar();
		int cc = (int)ch;//e.getKeyCode();
		int kc = e.getKeyCode();*/

		System.out.println("Pressed on: " + tfi);
		processKeyTypedEvent(new KeyEvent(this, e.getID(), e.getWhen(), 
										  e.getModifiers(), e.getKeyCode(), 
										  e.getKeyChar()));
	}

		/**
		 * Key Pressed event listener.
		 */
	public synchronized void keyPressed(KeyEvent e)
	{
		processKeyTypedEvent(new KeyEvent(this, e.getID(), e.getWhen(), 
										  e.getModifiers(), e.getKeyCode(),
										  e.getKeyChar()));
	}

		/**
		 * Key Released event listener.
		 */
	public void keyReleased(KeyEvent e)
	{
		processKeyTypedEvent(new KeyEvent(this, e.getID(), e.getWhen(), 
										  e.getModifiers(), e.getKeyCode(),
										  e.getKeyChar()));
	}

		/**
		 * Add an actionlistener.
		 */
	public void addActionListener(ActionListener l)
	{
		if (l != null)
		{
			actionlistener = AWTEventMulticaster.add(actionlistener, l);
		}
	}

		/**
		 * Remove an action listener
		 */
	public void removeActionListener(ActionListener l)
	{
		if (l != null)
		{
			actionlistener = AWTEventMulticaster.remove(actionlistener, l);
		}
	}

		/**
		 * Process action event.
		 */
	public void processActionEvent (ActionEvent e)
	{
		if (actionlistener != null)
		{
			actionlistener.actionPerformed(e);
		}
	}

		/**
		 * Add a key listener
		 */
	public void addKeyListener(KeyListener l)
	{
		if (l != null)
		{
			keylistener = AWTEventMulticaster.add(keylistener, l);
		}
	}

		/**
		 * Remove a key listener
		 */
	public void removeKeyListener(KeyListener l)
	{
		if (l != null)
		{
			keylistener = AWTEventMulticaster.remove(keylistener, l);
		}
	}

		/**
		 * Process key typed event.
		 */
	public void processKeyTypedEvent(KeyEvent e)
	{
		if (keylistener != null)
		{
			keylistener.keyTyped(e);
		}
	}

		/**
		 * Process a key pressed event.
		 */
	public void processKeyPressedEvent(KeyEvent e)
	{
		if (keylistener != null)
		{
			keylistener.keyPressed(e);
		}
	}

		/**
		 * Processa key released event.
		 */
	public void processKeyReleasedEvent(KeyEvent e)
	{
		if (keylistener != null)
		{
			keylistener.keyReleased(e);
		}
	}

		/**
		 * A text field that can take only 3 characters at most
		 * and that too can be only 0-9 and a-f
		 */
	class HexDigitField extends TextField implements KeyListener, FocusListener
	{
		public HexDigitField()
		{
			super(3);
			this.addKeyListener(this);
			this.addFocusListener(this);
		}
		
			/**
			 * Key typed event handler
			 */
		public void keyTyped(KeyEvent e)
		{
		}
		
			/**
			 * Key pressed event handler
			 */
		public void keyPressed(KeyEvent e)
		{
			char ch = e.getKeyChar();
			int kc = e.getKeyCode();
			if ((ch >= 'a' && ch <= 'z') ||
				(ch >= 'A' && ch <= 'Z') ||
				(ch >= '0' && ch <= '9'))
			{
				if ((ch > 'f' && ch <= 'z') ||
					(ch > 'F' && ch <= 'Z') || getText().length() >= 3)
				{
					e.consume();
					return ;
				}
			} else if (kc == KeyEvent.VK_BACK_SPACE ||
					   kc == KeyEvent.VK_RIGHT ||
					   kc == KeyEvent.VK_LEFT ||
					   kc == KeyEvent.VK_UP ||
					   kc == KeyEvent.VK_DOWN ||
					   kc == KeyEvent.VK_END ||
					   kc == KeyEvent.VK_HOME ||
					   kc == KeyEvent.VK_PAGE_UP ||
					   kc == KeyEvent.VK_PAGE_DOWN||
					   kc == KeyEvent.VK_SPACE||
					   kc == KeyEvent.VK_INSERT||
					   kc == KeyEvent.VK_DELETE||
					   kc == KeyEvent.VK_TAB||
					   kc == KeyEvent.VK_DECIMAL||
					   kc == KeyEvent.VK_ENTER)
			{
			} else
			{
				e.consume();
			}
		}
		
			/**
			 * Key released event handler
			 */
		public void keyReleased(KeyEvent e)
		{
		}
		
			/**
			 * Focus gained event handler
			 */
		public void focusGained(FocusEvent e)
		{
		}
		
			/**
			 * Focus Lost event handler
			 */
		public void focusLost(FocusEvent e)
		{
				// check if the number is valid.
				// if it isnt then set it back to
				// a good value
			if (e.getSource() == this)
			{
				int value = getValue();
				if (value < 0) setText("0");
				else if (value > 255) setText("255");
			}
		}
		
			/**
			 * Get the value of this field.
			 */
		public int getValue()
		{
			String str = getText().toLowerCase();
			int value = 0;
			try
			{
				value = Integer.parseInt(str);
			} catch (NumberFormatException nfe)
			{
					// try hexadecimal values then...
				String all = "0123456789abcdef";
				int ind = -1;
				for (int i = 0;i < str.length();i++)
				{
					ind = all.indexOf(str.charAt(i));
					if (ind < 0) return -1;
					value = (value * 16) + ind;
				}
			}
			return value;
		}
	}
	
	public static void main(String args[])
	{
		Frame f = new Frame("Testing IP Address Field");
		f.setLayout(new BorderLayout());
		f.add("Center", new IPAddressField(4));
		f.toFront();
		f.setVisible(true);
		f.pack();
	}
}