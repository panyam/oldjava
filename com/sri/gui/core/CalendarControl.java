package com.sri.gui.core;

/**
 *      CalendarControl.java
 *      -------------
 * This control is used to display a monthly calendar.  This control takes a calendar object
 * and paints it.
 *
 *  @author Sri Panyam
 *  @version V0.1
 */

/*
 * @CalendarControl.java	0.1 29/02/00
 *
 * This software is the confidential and proprietary information
 * of Mainstream Computing Pty Ltd.. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall 
 * use it only in accordance with the terms of the license agreement
 * you entered into with Mainstream.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CalendarControl extends Container implements ItemSelectable
{
	protected Object ourSelf = this;
	EventHandler handler = new EventHandler();
	protected ActionListener actionlistener = null;
	protected ItemListener itemlistener = null;
	protected boolean hasFocus = false;
	protected short dateAt[][] = null;
	protected Dimension minSize = new Dimension(100,100);
	protected boolean sizeInited = false;

	/**
	 * The current calendar that needs to be displayed.
	 */
	protected Calendar  calendar = null;

	/**
	 * The starting day of the week. By default this
	 * is set to 0 implying sunday.
	 */
	protected int startDay;

	/**
	 * Toggle the display of week names.
	 */
	protected boolean showWeekNames = true;

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST  = 2;
	public static final int WEST  = 3;

	/**
	 * Where is the row showing the week day names to be placed.
	 * By default it is placed on the top.
	 */
	protected int placement = NORTH;

	/**
	 * Colors used for the week day showing rows.
	 */
	protected Color weekColors[] = null;

	/**
	 * Tells which day in the month are currently selected.
	 */
	protected int selectedDay = -1;

	/**
	 * The Names of the week to be displayed.
	 */
	protected String weekStrings [] = {
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
	};

	private Image buffer = null;
	private Graphics bg = null;
	private Dimension bs = new Dimension();

	/**
	 * Default Constructor.
	 */
	public CalendarControl() {
		this(Calendar.getInstance());
	}

	/**
	 * Constructor specifying which calendar to use.
	 */
	public CalendarControl(Calendar cal) {
		calendar = cal;
		addKeyListener(handler);
		addFocusListener(handler);
		addMouseListener(handler);
		addMouseMotionListener(handler);
	}

	/**Sets the current calendar.*/
	public synchronized void setCalendar(Calendar cal) {
		calendar = cal;
		repaint();
	}
	
	/**Get current calendar, be careful, it can be null*/
	public Calendar getCalendar()
	{
		return this.calendar;
	}

	/**
	 * Gets the current year.
	 */
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	private boolean isLeapYear(int y) {
		return (y % 400 == 0 || (y % 4 == 0 && y % 100 != 0));
	}

	/**
	 * Returns the first day in the month.
	 */
	public int getFirstDayInMonth() {
		int curr = getDate();
		calendar.set(Calendar.DAY_OF_MONTH,1);
		int w = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.set(Calendar.DAY_OF_MONTH,curr);
		return w - 1;
	}

	/**
	 * Returns the number of days in the current month.
	 */
	public int getDaysInMonth() {
		boolean isl = isLeapYear(getYear());
		int m = getMonth();
		int days[] = { 31,28,31,30,31,30,31,31,30,31,30,31 };

		return (isl && m == 1 ? 29 : days[m]);
	}

	/**
	 * Gets the current month.
	 */
	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * Gets the current month.
	 */
	public int getDate() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Gets the current month.
	 */
	public int getDay() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Returns the minimum size of the control.
	 */
	public Dimension getMinimumSize() {
		if (!sizeInited) {
			prepareBuffer();
			sizeInited = true;
		}
		return minSize;
	}

	/**
	 * Sets the current year.
	 */
	public synchronized void setYear(int year) {
		calendar.set(Calendar.YEAR,year);
		paint(getGraphics());
	}

	public synchronized void setMonth(int m) {
		calendar.set(Calendar.MONTH,m);
		paint(getGraphics());
	}

	/**
	 * Sets the current date.
	 */
	public synchronized void setDate(int d) {
		calendar.set(Calendar.DAY_OF_MONTH,d);
		paint(getGraphics());
	}

	public synchronized void selectDay(int d) {
		selectedDay = d;
		paint(getGraphics());
	}

	/**
	 * Sets the start day in the visible calendar.
	 */
	public synchronized void setStartDay(int which) {
		if (which < 0 || which > 6) {
			throw new IllegalArgumentException("Starting day must be between 0 and 7");
		}
		startDay = which;
		paint(getGraphics());
	}

	public synchronized void setPlacement(int dir) {
		placement = dir;
		paint(getGraphics());
	}

	public synchronized void setWeekNamesDisplay(boolean show) {
		showWeekNames = show;
		paint(getGraphics());
	}

	/**
	 * Paint method.
	 */
	public void paint(Graphics g) {
		if (g == null) return ;

		prepareBuffer();
		if (buffer != null) g.drawImage(buffer,0,0,this);
	}

	/**
	 * Prepares offscreen buffer.
	 */
	private void prepareBuffer() {
		Dimension d = getSize();

		if (buffer == null || d.width != bs.width || bs.height != d.height) {
			bs.width = d.width;
			bs.height = d.height;

			buffer = createImage(bs.width,bs.height);
			if (bg != null) bg.dispose();
			bg = buffer.getGraphics();
			if (bg == null) return ;
		}

		bg.setColor(getBackground());
		bg.fillRect(0,0,bs.width,bs.height);

		// now start painting on the bufer...
		
		// are we placing things horizontally or vertically
		boolean horiz = (placement == NORTH || placement == SOUTH);
		int nRows = (horiz ? 6 : 7);
		int nCols = (horiz ? 7 : 6);
		
		// if placing on north or west then we start from
		// col (or row) 1 other wise we start from col (or row) 0
		int start = (placement == NORTH || placement == WEST ? 1 : 0);

		if (showWeekNames) {
			if (horiz) nRows++;
			else nCols++;
		} else {
			start = 0;
		}

		bg.setColor(getForeground());
		//bg.drawRect(0,0,bs.width - 1,bs.height - 1);

		int cellW = bs.width / nCols;
		int cellH = bs.height / nRows;

		if (true) {
			for (int i = 0;i < nRows;i++) {
				bg.drawLine(0,i * cellH,d.width - 1,i * cellH);
			}
			for (int i = 0;i < nCols;i++) {
				bg.drawLine(i * cellW,0,i * cellW,d.height - 1);
			}
			bg.drawLine(0,bs.height - 1,bs.width - 1,bs.height - 1);
			bg.drawLine(bs.width - 1,0,bs.width - 1,bs.width - 1);
		}


		// now draw all the cells...
		FontMetrics fm = bg.getFontMetrics();
		int h = fm.getAscent();

		int numDays = getDaysInMonth();
		int currRow = start;
		int currCol = start;

		if (horiz) {
			currCol = getFirstDayInMonth() % 7;
			if (currCol >= startDay) currCol -= startDay;
			else currCol += (7 - startDay);
		} else {
			currRow = getFirstDayInMonth() % 7;
			if (currRow >= startDay) currRow -= startDay;
			else currRow += (7 - startDay);
		}

		int thisDay = getDate();

		dateAt = null;
		dateAt = new short[nRows][nCols];
		for (int i = 0;i < nRows;i++) {
			for (int j = 0;j < nCols;j++) dateAt[i][j] = -1;
		}

		int maxW = 0;

		for (int i = 0;i < numDays;i++) {
			if (horiz) {
				if (currCol == 7) {
					currCol = 0;
					currRow ++;
				}
			} else {
				if (currRow == 7) {
					currRow = 0;
					currCol ++;
				}
			}

			dateAt[currRow][currCol] = (short)(i + 1);

			String toDraw = "" + (i + 1);
			int w = fm.stringWidth(toDraw);

			if (maxW < w) maxW = w;

			int x = (cellW * currCol) + ((cellW - w) / 2);
			int y = (cellH * currRow) + ((cellH + h) / 2);

			if (thisDay == i + 1) {
				bg.setColor(hasFocus ? SystemColor.textHighlight : SystemColor.textHighlight.brighter());
				int cw = cellW, ch = cellH;
				if (currCol == nCols - 1) {
					cw = d.width - 1 - (currCol * cellW);
				}
				if (currRow == nRows - 1) {
					ch = d.height - 1 - (currRow * cellH);
				}
				bg.fillRect(cellW * currCol,cellH * currRow,cw,ch);
				bg.setColor(getBackground());
				bg.drawString(toDraw,x,y);
				bg.setColor(getForeground());
			} else {
				bg.drawString(toDraw,x,y);
			}


			// paint cell
			if (horiz) currCol++;
			else currRow++;
		}

		if (showWeekNames) {
			String weekNames[] = {
				"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
			};


			int offY = ((cellH + h) / 2);

			// finally draw the week names 
			for (int i = 0;i < 7;i++) {
				int c = (i + startDay) % 7;
				int w = fm.stringWidth(weekNames[c]);

				bg.setColor(SystemColor.controlHighlight);

				
				int offX = (cellW - fm.stringWidth(weekNames[c])) / 2;
				
				int cw = cellW, ch = cellH;
				if (i == 6) {
					cw = d.width - 1 - (i * cellW);
					ch = d.height - 1 - (i * cellH);
				}
				//bg.fillRect(cellW * currCol,cellH * currRow,cw,ch);

				if (maxW < w) maxW = w;
				switch (placement) {
				case NORTH : 
					bg.fill3DRect(cellW * i,0,cw,cellH,true);
					bg.setColor(getBackground());
					bg.drawString(weekNames[c],offX + cellW * i,offY); 
					break;
				case SOUTH : 
					bg.fill3DRect(cellW * i,6 * cellH,cw,d.height - 1 - (6 * cellH),true);
					bg.setColor(getBackground());
					bg.drawString(weekNames[c],offX + cellW * i,
								  6 * cellH + offY); 
					break;
				case EAST : 
					bg.fill3DRect(cellW * 6,i * cellH,d.width - 1 - (6 * cellW),ch,true);
					bg.setColor(getBackground());
					bg.drawString(weekNames[c],6 * cellW + offX,
								  offY + cellH * i); 
					break;
				case WEST : 
					bg.fill3DRect(0,i * cellH,cellW,ch,true);
					bg.setColor(getBackground());
					bg.drawString(weekNames[c],offX,
								  offY + cellH * i); 
					break;
				}
			}
		}

		minSize.width = (maxW * nCols) +2;
		minSize.height = ((h + 5) * nRows) + 2;
	}

	public boolean isFocusTraversable() {
		return true;
	}
	
	public void addItemListener(ItemListener l)
	{
		if (l != null)
		{
			itemlistener = AWTEventMulticaster.add(itemlistener, l);
		}
	}

	public void removeItemListener(ItemListener l)
	{
		if (l != null)
		{
			itemlistener = AWTEventMulticaster.remove(itemlistener, l);
		}
	}

	public Object[] getSelectedObjects() {
		return null;
	}
	
	public void processItemEvent ()
	{
		if (itemlistener != null)
		{
			itemlistener.itemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,this,ItemEvent.ITEM_STATE_CHANGED));
		}
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
	
	public static String ACTION_COMMAND = "END_EDIT";

	public void processActionEvent ()
	{
		if (actionlistener != null)
		{
			actionlistener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, ACTION_COMMAND));
		}
	}


	class EventHandler implements   MouseListener, KeyListener, 
		FocusListener, MouseMotionListener
	{
		public EventHandler() { }
		public void mousePressed(MouseEvent e) {
			requestFocus();
			// are we placing things horizontally or vertically
			boolean horiz = (placement == NORTH || placement == SOUTH);
			int nRows = (horiz ? 6 : 7);
			int nCols = (horiz ? 7 : 6);
			
			// if placing on north or west then we start from
			// col (or row) 1 other wise we start 
			// from col (or row) 0
			int start = (placement == NORTH || placement == WEST ? 1 : 0);

			// the row/col where weekname will be displayed.
			int weekNamePos = -1;

			if (showWeekNames) {
				if (horiz) nRows++;
				else nCols++;
			} else {
				start = 0;
			}

			int cellW = bs.width / nCols;
			int cellH = bs.height / nRows;

			int clickedCol = e.getX() / cellW;
			int clickedRow = e.getY() / cellH;

			if (clickedRow >= 0 && clickedCol >= 0 && clickedRow < nRows && clickedCol < nCols && 
				dateAt[clickedRow][clickedCol] >= 0) {
				setDate(dateAt[clickedRow][clickedCol]);
				processActionEvent();
			}
		}

		public void mouseClicked(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}

		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}

		/**
		 * Focus has been gained by this component.
		 */
		public void focusGained(FocusEvent e) {
			if (e.getSource() == this) {
				hasFocus = true;
				paint(getGraphics());
			}
		}

		/**
		 * Focus has been lost by this component.
		 */
		public void focusLost(FocusEvent e) {
			if (e.getSource() == this) {
				hasFocus = false;
				paint(getGraphics());
			}
		}

		public void keyPressed(KeyEvent e) {
			if (!isShowing() || e.getSource() != ourSelf) return;
			int kc = e.getKeyCode();
			int cc = (int)e.getKeyChar();
			boolean horiz = (placement == NORTH || placement == SOUTH);
			int prevnext = horiz ? 1 : 7;
			int updown = horiz ? 7 : 1;

			if (kc == KeyEvent.VK_LEFT) {
				int cd = getDate();
				setDate(cd - prevnext);
			} else if (kc == KeyEvent.VK_RIGHT) {
				int cd = getDate();
				setDate(cd + prevnext);
			} else if (kc == KeyEvent.VK_UP) {
				int cd = getDate();
				setDate(cd - updown);
			} else if (kc == KeyEvent.VK_DOWN) {
				int cd = getDate();
				setDate(cd + updown);
			} else if (kc == KeyEvent.VK_HOME || kc == KeyEvent.VK_PAGE_UP) {
				int cd = getDate();
				setDate(1);
			} else if (kc == KeyEvent.VK_END || kc == KeyEvent.VK_PAGE_DOWN) {
				int cd = getDate();
				setDate(getDaysInMonth());
			}
			if (cc == KeyEvent.VK_ENTER) {
				processActionEvent();
				return ;
			}
			processItemEvent ();
		}
		public void keyTyped(KeyEvent e) {
		}
		public void keyReleased(KeyEvent e) { }
	}
}
