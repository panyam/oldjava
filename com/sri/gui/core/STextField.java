package com.sri.gui.core;
/**
 *      STextField class
 *      -------------
 *  This is a lightweight version of the jdk's TextField component.  This was created
 *  in order to eliminate the problems associated with jdk's heavy weight component's drawing
 *	problem.  All the features of the native text field are available.
 *
 *  @author Sri Panyam
 *  @version V0.1
 */

import java.awt.*;
import java.awt.event.*;

public class STextField extends Component
	implements 
	MouseListener, 
	Runnable,
	MouseMotionListener,
	KeyListener,
	FocusListener
{
	protected StringBuffer buffer = new StringBuffer(10);

	protected int selectionStart = -1;          // the char at which 
	// selection begins

	protected int selectionEnd= -1;             // the char at which 
	// selection ends..

	protected int startChar = 0;
	// string starts...  this only 
	// changes once the cursor update
	// is set to true...

	int caratPos = 0;

	int colLimit = -1;

	protected boolean hasFocus = false;

	Image buffImage = null;
	Graphics bg = null;
	Dimension bs = new Dimension();

	protected ActionListener actionlistener = null;

	Thread ourThread = new Thread(this);

	/**
	 * Default Constructor.
	 */
	public STextField()
	{
		this("",-1);
	}

	/**
	 * Constructor specifying the initial string 
	 * in the text field.
	 */
	public STextField(String s)
	{
		this(s,-1);
	}


	/**
	 * Constructor with limit on number of columns.
	 */
	public STextField(int colLimit)
	{
		this("",colLimit);
	}

	/**
	 * Complete Constructor.
	 */
	public STextField(String s,int colLimit)
	{
		setText(s);
		setColumns(colLimit);

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addFocusListener(this);
		setBackground(SystemColor.window);
		setForeground(SystemColor.textText);
	}

	/**
	 * Sets the number of columns.
	 *
	 * The text in the text field is reduced to the number of
	 * characters.
	 */
	public synchronized void setColumns(int num)
	{
		if (num == colLimit) return ;
		colLimit = num;
		System.out.println("Setting Col Limit = " +num);
		if (num > 0 && num < buffer.length())
		{
			colLimit = num;
			String sb = buffer.toString();
			buffer = null;
			buffer = new StringBuffer(sb.substring(0,num));
			caratPos = Math.min(caratPos,buffer.length());
			buffImage = null;
			System.out.println("sb,buff = " + sb + " , " + buffer);
		}
		paint(getGraphics());
	}

	/**
	 * Clears the text field.
	 */
	public synchronized void clearText()
	{
		caratPos = 0;
		buffer = new StringBuffer(10);
		startChar = 0;
		paint(getGraphics());
	}

	/**
	 * Sets the text in the textfield.
	 * Once again the string is cut to fit in the required number
	 * of columns.
	 */
	public synchronized boolean setText(String st)
	{
		
		buffer = new StringBuffer(st);
		if (colLimit > 0 && colLimit < buffer.length())
		{
			String sb = buffer.toString();
			buffer = null;
			buffer = new StringBuffer(sb.substring(0,colLimit));
		}
		if (caratPos > buffer.length()) {
			caratPos = startChar = 0;
		}
		selectionStart = selectionEnd = -1;
		paint(getGraphics());
		return true;
	}

	/**
	 * Adds a character to the end of the text.
	 *
	 * The output is true if a character was added.
	 * If all the columns have been filled a false is returned
	 * and the character is not inserted.
	 */
	public synchronized boolean addChar(char c)
	{
		if (buffer.length() == colLimit) return false;
		buffer.append(c);
		return true;
	}
	
	public void selectAll()
	{
		this.selectionStart = 0;
		this.selectionEnd = this.getText ().length ();		
	}

	public synchronized void deleteChars(int f,int t)
	{
		if (f == t) return ;
		int from = Math.min(f,t);
		int to = Math.max(f,t);
		
		// now we basically need to 
		
		String text = buffer.toString();
		String pre = text.substring(0,from);
		String post = text.substring(to);
		buffer = new StringBuffer(pre + post);
		caratPos = Math.min(from,to);
		selectionStart = selectionEnd = -1;
		paint(getGraphics());
	}
	
	
	public synchronized void deleteCharAt(int i)
	{
		deleteCharAt(i,true);
	}

	public synchronized void deleteCharAt(int i,boolean before)
	{
		if ((before && i <= 0) || (!before && i >= buffer.length()))
		{
			//System.out.println("i,bufflen = " + i + ", " + buffer.length());
			return ;
		}

		String s = buffer.toString(), pre = "", post = "";
		if (before)
		{
			pre = (i > 0 ? s.substring(0,i - 1) : "");
			post = (i < s.length() ? s.substring(i) : "");
			caratPos = i - 1;
		} else
		{
			pre = (i > 0 ? s.substring(0,i) : "");
			post = (i < s.length() - 1 ? s.substring(i + 1) : "");
			caratPos = i;
		}

		buffer = new StringBuffer(pre + post);
		paint(getGraphics());
	}

	/**
	 * Inserts a string at the requested position in the 
	 * The output is true if a character was added.
	 * If all the columns have been filled a false is returned
	 * and the character is not inserted.   * text field.
	 */
	public synchronized void insertCharAt(char c,int offset)
	{
		if (buffer.length() == colLimit) return ;

		buffer.insert(offset,c);
		caratPos = offset + 1;
		paint(getGraphics());
	}

		/**
		 * Returns the position of the carat.
		 */
	public int getCaratPosition() {
		return caratPos;
	}
	
	/**
	 * Returns the character at the specified index.
	 */
	public char getCharAt(int i)
	{
		return '\0';
	}

	/**
	 * Returns the text in the textfield.
	 */
	public String getText()
	{
		return buffer.toString();
	}

	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();

		prepareBuffer();

		g.drawImage(buffImage,0,0,this);
	}

	public void prepareBuffer()
	{
		Dimension d = getSize();

		if (d == null || d.width <= 0 || d.height <= 0) return;

		if (d.width != bs.width || d.height != bs.height) {
			bs.width = d.width;
			bs.height = d.height;

					// create buffer
			buffImage = createImage(bs.width,bs.height);

					// reclaim used spaice..
			if (bg != null) bg.dispose();

			bg = buffImage.getGraphics();
		}
		if (bg == null) return ;

		bg.setColor(getBackground());
		bg.fillRect(0,0,bs.width,bs.height);

		bg.setColor(getForeground());

		FontMetrics fm = bg.getFontMetrics();

		int h = fm.getAscent();
		int y = (bs.height + h) / 2;
		
		
		// need to check where the carat is.
		// if it is beyond the widths of the field
		// then we need to modified  startingChar
		// appropriately and we also need to check where
		// the cstart pos is...
		String text = buffer.toString();
		String first = "";
		String end = "";
		int fw = 0, ew = 0;
		int currW = bs.width;
		
		if (text.length() > 0)
		{
			boolean lengthfine = false;
			while (!lengthfine)
			{
				if (startChar < caratPos)
				{
					first = text.substring(startChar,caratPos);
				}
				end = text.substring(caratPos);
				fw = fm.stringWidth(first);
				ew = fm.stringWidth(end);
				if (fw <= 0 && startChar > 0) startChar --;
				else if (fw >= currW && startChar < text.length()) startChar ++;
				else lengthfine = true;
			} 
		}
		
		// if nothing is highlighted yet
		if (selectionStart == selectionEnd)
		{
			bg.drawString(first,0,y);
			if (hasFocus) bg.drawLine(fw,y + 3,fw,y - h - 1);
			bg.drawString(end,fw,y);
		} else
		{
			// if selectionStart is less than startX then we start from startX to endX in sel mode
			//
			// now what we do is we print from startX to selectionStart
			int from = Math.min(selectionStart,selectionEnd);
			int to = Math.max(selectionStart,selectionEnd);
			
			String p1 = "",p2 = "",p3 = "";
			int w1 = 0, w2 = 0, w3 = 0;
			if (startChar < from)
			{
				p1 = text.substring(startChar,from);
				w1 = fm.stringWidth(p1);
				bg.drawString(p1,0,y);
			}
			
			bg.setColor(SystemColor.textHighlight);
			w2 = fm.stringWidth(p2 = text.substring(from,to));
			w3 = fm.stringWidth(p3 = text.substring(to));
			bg.fillRect(w1,y - h - 1,w2,h + 4);
			bg.setColor(Color.white);
			bg.drawString(p2,w1,y);
			bg.setColor(getForeground());
			bg.drawString(p3,w2 + w1,y);
		}
	}

	// now we need to calculate the character 
	// given the mouse x coodinate..
	// this is what this functions does...
	//
	// how ???
	//
	// so we know what the starting char is and also what the 
	public int mouse2pos(int x)
	{
		if (x < 0) return Math.max(0,startChar - 1);
		String st = buffer.toString();

		int l = buffer.length();
		Graphics g = getGraphics();

		if (g == null) return 0;
		FontMetrics fm = g.getFontMetrics();
		if (fm == null) return 0;

		int w = fm.stringWidth(st.substring(startChar));
		if (l > 0)
		{
			char chars[] = new char[l];
			buffer.getChars(startChar,l,chars,0);
			for (int i = 0;i < l - startChar;i++)
			{
				if (fm.charsWidth(chars,0,i) > x)
				{
					return Math.max(startChar + i - 1,0);
				}
			}
		}

		if (w > x) return Math.min(caratPos + 1,l);
		else return l;
	}
	
	public void mouseClicked(MouseEvent e)
	{ }
	public void mousePressed(MouseEvent e)
	{
		if (e.getSource() == this)
		{
				// mark selection start
			selectionEnd = caratPos = selectionStart = mouse2pos(e.getX());
			requestFocus();
			paint(getGraphics());
		}
	}
	public void mouseEntered(MouseEvent e)
	{ 
		if (e.getSource() == this)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}
	}
	public void mouseExited(MouseEvent e)
	{
		if (e.getSource() == this)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	public void mouseReleased(MouseEvent e)
	{ }
	public void mouseDragged(MouseEvent e)
	{ 
		if (e.getSource() == this && hasFocus)
		{
			// mark selection ending
			if (selectionStart != -1)
			{
				caratPos = selectionEnd = mouse2pos(e.getX());
				paint(getGraphics());
			}
		}
	}
	public void mouseMoved(MouseEvent e)
	{ }

	public synchronized void keyTyped(KeyEvent e)
	{
		if (e.getSource() != this) return ;
		char ch = e.getKeyChar();
		int cc = (int)ch;//e.getKeyCode();
		int kc = e.getKeyCode();

		if (cc == KeyEvent.VK_BACK_SPACE)
		{
			if (selectionStart != selectionEnd)
			{
				// if some text is selected then
				// delete that text but the caratPos does
				// not change.
				deleteChars(selectionStart,selectionEnd);
			} else
			{
				deleteCharAt(caratPos,true);
			};
		} else if (cc == KeyEvent.VK_ENTER)
		{
			processActionEvent(new ActionEvent (this,ActionEvent.ACTION_PERFORMED,"STextField"));
		} else
		{
			if (cc == KeyEvent.VK_TAB || cc == KeyEvent.VK_ESCAPE) return ;
			
			// if some thing is selected then we delete it first
			if (selectionStart != selectionEnd)
			{
				deleteChars(selectionStart,selectionEnd);
			}
			insertCharAt(ch,caratPos);
		}
	}

	public void keyPressed(KeyEvent e)
	{
		if (e.getSource() != this) return ;
		char ch = e.getKeyChar();
		int cc = (int)ch;//e.getKeyCode();
		int kc = e.getKeyCode();

		boolean selectionStarted = (selectionStart != selectionEnd);
		boolean shift = e.isShiftDown();
		boolean ctrl = e.isControlDown();
		
		// with the arrow and end and home keys
		// we also need to check if the shift has been pressed
		// if so we need to start selection.
		if (kc == KeyEvent.VK_LEFT)
		{
			if (caratPos > 0)
			{
				
				// nextPos for the time bein is being set to 
				// one character left of caratPos.  However
				// if the ctrl key is also held then
				// it should go to the start of the previous
				// word.  same applies for the right arrow
				// key as welll
				int nextPos = caratPos - 1;
				if (shift)
				{
					if (!selectionStarted) selectionStart = caratPos;
					selectionEnd = (caratPos = nextPos);
				} else
				{
					selectionStart = selectionEnd = -1;
					caratPos = nextPos;
				}
				paint(getGraphics());
			} else if (!shift)
			{
				selectionStart = selectionEnd = -1;
				paint(getGraphics());
			}
		} else if (kc == KeyEvent.VK_RIGHT)
		{
			if (caratPos < buffer.length())
			{
				int nextPos = caratPos + 1;
				if (shift)
				{
					if (!selectionStarted) selectionStart = caratPos;
					selectionEnd = (caratPos = nextPos);
				} else
				{
					selectionStart = selectionEnd = -1;
					caratPos = nextPos;
				}
				paint(getGraphics());
			} else if (!shift)
			{
				selectionStart = selectionEnd = -1;
				paint(getGraphics());
			}
		} else if (kc == KeyEvent.VK_END)
		{
			if (caratPos != buffer.length())
			{
				if (shift)
				{
					if (!selectionStarted) selectionStart = caratPos;
					selectionEnd = (caratPos = buffer.length());
				} else
				{
					selectionStart = selectionEnd = -1;
					caratPos = buffer.length();
				}
				paint(getGraphics());
			} else if (!shift)
			{
				selectionStart = selectionEnd = -1;
				paint(getGraphics());
			}
		} else if (kc == KeyEvent.VK_HOME)
		{
			if (caratPos != 0)
			{
				if (shift)
				{
					if (!selectionStarted) selectionStart = caratPos;
					selectionEnd = caratPos = 0;
				} else
				{
					selectionStart = selectionEnd = -1;
					caratPos = 0;
				}
				paint(getGraphics());
			} else if (!shift)
			{
				selectionStart = selectionEnd = -1;
				paint(getGraphics());
			}
		} else if (kc == KeyEvent.VK_DELETE)
		{
			if (selectionStart != selectionEnd)
			{
				selectionStart = selectionEnd = -1;
				// if some text is selected then
				// delete that text but the caratPos does
				// not change.
				deleteChars(selectionStart,selectionEnd);
			} else
			{
				deleteCharAt(caratPos,false);
			};
		}
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void addActionListener(ActionListener l)
	{
		if (l != null)
		{
			actionlistener = AWTEventMulticaster.add(actionlistener, l);
		}
	}

	public void removeActionListener(ActionListener l)
	{
		if (l != null)
		{
			actionlistener = AWTEventMulticaster.remove(actionlistener, l);
		}
	}

	public void processActionEvent (ActionEvent e)
	{
		if (actionlistener != null)
		{
			actionlistener.actionPerformed(e);
		}
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

	public synchronized void run()
	{
	}

	public void focusLost(FocusEvent e)
	{
		if (e.getSource() == this)
		{
			hasFocus = false;
			paint(getGraphics());
		}
	}

	public void focusGained(FocusEvent e)
	{
		if (e.getSource() == this)
		{
			hasFocus = true;
			paint(getGraphics());
		}
	}
}
