package com.sri.apps.mml;


import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * A component for viewing and editing MathML.
 */
public class MMLEditor extends Container 
						  implements 
							MouseListener, 
							MouseMotionListener, 
							AdjustmentListener, 
							KeyListener,
							ComponentListener
{
		/**
		 * This popup menu is used to modify the
		 * the attributes, look and feel of the symbols 
		 * that are currently at the cursor.
		 */
	PopupMenu popup = new PopupMenu("MML Options");

        /**
         * Buffer graphics.
         */
    Graphics bg = null;

        /**
         * Double buffer image.
         */
    Image buffer = null;

        /**
         * Size of the buffer.
         */
    Dimension bs = new Dimension();
	
		/**
		 * A thread that that updates the dimensions
		 * of the root each time a key is pressed.
		 * We want to do this in a thread because, 
		 * keys can be typed very fast and we dont wantt
		 * this function to hog the time.
		 */
	DimensionUpdaterThread duThread = new DimensionUpdaterThread();

        /**
         * Vertical scroll bar.
         */
    protected Scrollbar vScroll = new Scrollbar(Scrollbar.VERTICAL);

        /**
         * Horizontal scroll bar.
         */
    protected Scrollbar hScroll = new Scrollbar(Scrollbar.HORIZONTAL);

		/**
		 * Preferred height of the horizontal scroll bar.
		 */
	protected final static int HS_HEIGHT = new Scrollbar(Scrollbar.HORIZONTAL).getPreferredSize().height;

		/**
		 * Preferred width of the vertical scroll bar.
		 */
	protected final static int VS_WIDTH = new Scrollbar(Scrollbar.VERTICAL).getPreferredSize().width;
	
		/**
		 * A flag to remind ourselves that the size 
		 * of the paragraph hasnt been calculated yet.
		 */
    boolean calcSizeLater = false;

        /**
         * A cache of colors.  So that color values that existed before
         * dont need to be created again but can be referenced from 
         * this hash table.
         */
    static Hashtable colorHash = new Hashtable();

        /**
         * A cache of fonts.  So that fonts that already exited 
         * dont need to be created again but can be referenced from 
         * this hash table.
         */
    static Hashtable fontHash = new Hashtable();

        /**
         * Root of the document tree.
         */
    MMLParagraph root;

        /**
         * Refers to the index of the font stack.
         */
    protected final static int FONT_ATTR = 0;

        /**
         * Refers to the index of the foreground color stack.
         */
    protected final static int FG_COLOR_ATTR = 1;

        /**
         * Refers to the index of the background color stack.
         */
    protected final static int BG_COLOR_ATTR = 2;

        /**
         * Refers to the index of the justification stack.
         */
    protected final static int JUST_ATTR = 3;

        /**
         * Refers to the embelishment attribute index in the stack.
         */
    protected final static int EMBEL_ATTR = 4;

        /**
         * Refers to the cursor attribute index in the stack.
         */
    protected final static int CURSOR_ATTR = 5;

        /**
         * Refers to the cursor attribute index in the stack.
         */
    protected final static int PARENT_ATTR = 6;

        /**
         * Stacks for various attributes.
         */
    Stack attrStacks[] = new Stack[PARENT_ATTR + 1];

        /**
         * Current foreground color.
         */
    Color currFG = Color.black;

        /**
         * Current foreground color.
         */
    Color currBG = Color.lightGray;

        /**
         * Current font.
         */
    Font currFont = new Font("Serif", 12, Font.BOLD | Font.ITALIC);

        /**
         * Current justification.
         */
    MMLJustification currJust = MMLJustification.LEFT_JUSTIFY;

        /**
         * Current embelishment
         */
    MMLEmbelishment currEmb = null;

        /**
         * The current cursor information.
         */
    MMLCursor currCursor = null;

        /**
         * Constructor.
         */
    public MMLEditor()
    {
        setLayout(null);
        add(vScroll);
        add(hScroll);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addComponentListener(this);
		vScroll.addAdjustmentListener(this);
		hScroll.addAdjustmentListener(this);

		vScroll.setMinimum(0);
		hScroll.setMinimum(0);

			/**
			 * Also add the popup to ourself.
			 */
		add(popup);
		
		popup.add(new MenuItem("Item 1"));
		popup.add(new MenuItem("Item 2"));
		popup.add(new MenuItem("Item 3"));
		popup.add(new MenuItem("Item 4"));
		
        for (int i = 0;i < attrStacks.length;i++) attrStacks[i] = new Stack();

            // also generate a dummy document for test purposes...
        MMLParagraph root = new MMLParagraph();

        /*root.addSymbol(new MMLBracketTemplate(
								2, 
								MMLBracketTemplate.O_ANGLE_BRACKET, 
								MMLBracketTemplate.C_ANGLE_BRACKET, ","), 0);*/
        setDocument(root);
    }

		/**
		 * A new symbol is inserted at the current cursor
		 * position.
		 */
	public void insertSymbol(MMLSymbol s)
	{
		if (currCursor == null || root == null) return ;
		if (currCursor.holder instanceof MMLParagraph)
		{
				// add at the current position...
			MMLParagraph para = (MMLParagraph)currCursor.holder;
			para.insertSymbolAt(s, currCursor.currLine, currCursor.currPos);
			currCursor.currPos ++;
		} else if (currCursor.holder instanceof MMLMultiCharToken)
		{
				// then we need to break the token...
			MMLMultiCharToken tok = (MMLMultiCharToken)currCursor.holder;
            int l = currCursor.currLine;
            int c = currCursor.currPos;
            MMLMultiCharToken mct = tok.breakAt(c);
				// and pop this one and get the parent of this token...
			MMLCursor parent = (MMLCursor)peekAttribute(this.CURSOR_ATTR);
			if (parent.holder instanceof MMLParagraph)
			{
				((MMLParagraph)parent.holder).
								insertSymbolAt(s, 
											   parent.currSymLine, 
											   parent.currSymIndex + 1);
				((MMLParagraph)parent.holder).
								insertSymbolAt(mct,
											   parent.currSymLine,
											   parent.currSymIndex + 2);
				parent.currPos = parent.currSymIndex + 2;
			}
		}
		calcSizeLater = true;
		paint(getGraphics());
	}

        /**
         * Set a new document.
         */
    public void setDocument(MMLParagraph doc)
    {
        this.root = doc;
        currCursor = new MMLCursor(root);

            // clear the stacks...
        for (int i = 0;i < attrStacks.length;i++)
        {
            attrStacks[i].removeAllElements();
        }

		if (getGraphics() == null)
		{
			calcSizeLater = true;
		} else
		{
			calcSizeLater = false;
			paint(getGraphics());
		}
    }

        /**
         * Draws the whole thing.
         */
    public void paint(Graphics g)
    {
        Dimension d = getSize();

        if (vScroll.isVisible()) d.width -= (VS_WIDTH + 10);
        if (hScroll.isVisible()) d.height -= (HS_HEIGHT + 10);

        if (buffer == null || d.width != bs.width || d.height != bs.height)
        {
            buffer = null;
            bg = null;
            bs.width = d.width > 1 ? d.width : 1;
            bs.height = d.height > 1 ? d.height : 1;
            buffer = createImage(bs.width, bs.height);
            if (buffer == null) return ;
            if ((bg = buffer.getGraphics()) == null) return ;
        }

        if (bg != null && buffer != null)
        {
            bg.setColor(getBackground());
            bg.fillRect(0, 0, bs.width, bs.height);

            bg.setColor(getForeground());
			
            // calculate the size now if it hasnt been calculated yet...
			if (calcSizeLater)
		    {
	            calcSizeLater = false;
				duThread.updateDimensionsNow(g);
			}
			
			currCursor.globalX = currCursor.globalY = 0;
			
		        // draw the symbol...
			root.draw(bg, 2 - hScroll.getValue(), 2 - vScroll.getValue(), 
					  bs.width - 4, bs.height - 4, currCursor, false);
			
				// now check if we have the cursor location
				// is valid.  if it isnt then set the proper
				// location and repaint...
			boolean changed = false;
			if (currCursor.globalX < 0)
			{
				changed = true;
				hScroll.setValue(hScroll.getValue() - (hScroll.getMaximum() / 5));
			} else if (currCursor.globalX > bs.width)
			{
				changed = true;
				hScroll.setValue(hScroll.getValue() + (hScroll.getMaximum() / 5));
			}
			
			if (currCursor.globalY < 0)
			{
				changed = true;
				vScroll.setValue(vScroll.getValue() - (vScroll.getMaximum() / 10));
			} else if (currCursor.globalY > bs.height)
			{
				changed = true;
				vScroll.setValue(vScroll.getValue() + (vScroll.getMaximum() / 10));
			}
			
			if (changed)
				paint(g);
			else
				g.drawImage(buffer, 0, 0, null);
        }
    }

        /**
         * Update method.
         */
    public void update(Graphics g)
    {
    }

        /**
         * Layout all the components.
         */
    public void doLayout()
    {
        Dimension d = getSize();
        Dimension dv = vScroll.getPreferredSize();
        Dimension dh = hScroll.getPreferredSize();

        vScroll.setBounds(d.width-dv.width,0,
                          dv.width,
                          hScroll.isVisible() ? d.height-dh.height : d.height);
        hScroll.setBounds(0,d.height - dh.height,
                          vScroll.isVisible() ? d.width - dv.width : d.width,
                          dh.height);
    }

        /**
         * Tells that we CAN recieve focus.
         */
    public boolean isFocusTraversable()
    {
        return true;
    }

        /**
         * Take a peek at the top most value on the requested stack.
         */
    public Object peekAttribute(int type)
    {
        if (attrStacks[type].empty()) return null;
        return attrStacks[type].peek();
    }

        /**
         * Activates the most recently used requested attribute.
         */
    protected Object oldAttribute(int type)
    {
        if (attrStacks[type].empty()) return null;
        return attrStacks[type].pop();
    }

        /**
         * Push a new attribute.
         */
    protected void newAttribute(int type, Object value)
    {
        switch (type)
        {
            case FONT_ATTR : 
            {
                attrStacks[type].push(currFont);
                currFont = (Font)value;
            } break ;
            case FG_COLOR_ATTR : 
            {
                attrStacks[type].push(currFG);
                currFG = (Color)value;
            } break ;
            case BG_COLOR_ATTR : 
            {
                attrStacks[type].push(currBG);
                currBG = (Color)value;
            } break ;
            case JUST_ATTR : 
            {
                attrStacks[type].push(currJust);
                currJust = (MMLJustification)value;
            } break ;
            case EMBEL_ATTR : 
            {
                attrStacks[type].push(currEmb);
                currEmb = (MMLEmbelishment)value;
            } break ;
            case CURSOR_ATTR : 
            {
                attrStacks[type].push(currCursor);
                currCursor = (MMLCursor)value;
            } break ;
            case PARENT_ATTR : 
            {
            } break ;
            default: return ;
        }
    }

        /**
         * Event handler to listen to the changes in
         * scrollbar values.
         */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getSource() instanceof Scrollbar)
		{
			paint(getGraphics());
		}
	}
	
	public void setEnabled(boolean en)
	{
		if (en)
		{
			addKeyListener(this);
		} else
		{
			removeKeyListener(this);
		}
		super.setEnabled(en);
	}

		/**
		 * Component shown event handler.
		 */
	public void componentMoved(ComponentEvent e)
	{
	}

		/**
		 * Component shown event handler.
		 */
	public void componentHidden(ComponentEvent e)
	{
	}

		/**
		 * Component shown event handler.
		 */
	public void componentShown(ComponentEvent e)
	{
	}

		/**
		 * Component shown event handler.
		 */
	public void componentResized(ComponentEvent e)
	{
		duThread.updateDimensionsNow(getGraphics());
	}
	
        /**
         * Key typed event listener.
         */
    public void keyTyped(KeyEvent e) { }

        /**
         * Key pressed event listener.
         */
    public void keyPressed(KeyEvent e)
    {
        if (e.getSource() != this) return ;
        if (currCursor == null) return ;
		
		MMLNavigateableSymbol nextNav = 
			currCursor.holder.processKey(e, currCursor);

        MMLCursor oldCursor = null;

		while (nextNav != currCursor.holder)
		{
			oldCursor = currCursor;
					// means exit to parent...
			if (nextNav == null)
			{
				currCursor = (MMLCursor)oldAttribute(CURSOR_ATTR);
				if (currCursor == null) 
				{
					newAttribute(CURSOR_ATTR, currCursor = oldCursor);
					return ;
				}
				nextNav = currCursor.holder.exit(e, oldCursor, currCursor);
			} else
			{
						// means enter the child...
				newAttribute(CURSOR_ATTR, oldCursor);
				currCursor = new MMLCursor(0, 0, nextNav);
				nextNav = currCursor.holder.enter(e, oldCursor, currCursor);
			}
		}

			/**
			 * Check if the cursor's size has changed...
			 * Two things to consider here..
			 * If the size has changed then we need
			 * to recalculate the sizes.
			 * 
			 * One question that remains is whether we
			 * should repaint EVERY thing or simply redraw
			 * the buffered image.  The buffer should be updated
			 * if the view port has changed or size has changed or
			 * the colour has changed.
			 */
		if (currCursor.sizeChanged)
		{
				// calculate the root size...
				// this functino also calls the paint method
			calcSizeLater = true;
			currCursor.sizeChanged = false;
		}
		paint(getGraphics());
    }

        /**
         * Key released event listener.
         */
    public void keyReleased(KeyEvent e) { }

        /**
         * Mouse Moved Event listener.
         */
    public void mouseMoved(MouseEvent e)
    {
    }

        /**
         * Mouse Dragged Event listener.
         */
    public void mouseDragged(MouseEvent e)
    {
    }

        /**
         * Mouse Pressed Event listener.
         */
    public void mousePressed(MouseEvent e)
    {
        if (e.getSource() != this) return ;

        requestFocus();
		
		//if (true) return ;
			// see where the mouse was pressed and go replace
			// the cursor stack...
		currCursor.holder = root;
		Point p = e.getPoint();
		p.x += hScroll.getValue();
		p.y += vScroll.getValue();
		
			// clear the cursor stack...
		attrStacks[CURSOR_ATTR].removeAllElements();
		MMLNavigateableSymbol next = root.mousePressed(p, currCursor);
			// from the current symbol the next symbol can be found
			// or it can be null...  If it is null than we just stay at 
			// the current symbol...
		while (next != null)
		{
			attrStacks[CURSOR_ATTR].push(currCursor);
			next = next.mousePressed(p, currCursor = new MMLCursor(0, 0, next));
		}

		if (e.isMetaDown())
		{
			popup.show(this, e.getX(), e.getY());
		} else
		{
			paint(getGraphics());
		}
    }

        /**
         * Mouse Released Event listener.
         */
    public void mouseReleased(MouseEvent e)
    {
    }

        /**
         * Mouse Clicked Event listener.
         */
    public void mouseClicked(MouseEvent e)
    {
    }

        /**
         * Mouse Entered Event listener.
         */
    public void mouseEntered(MouseEvent e)
    {

    }

        /**
         * Mouse Exited Event listener.
         */
    public void mouseExited(MouseEvent e)
    {

    }
	
		/**
		 * A thread for updating the dimensions of the
		 * root symbol and updating the equationviewer
		 * scrollbars for proper viewing.
		 */
	class DimensionUpdaterThread implements Runnable
	{
			/**
			 * The graphics object that is required.
			 */
		Graphics g;
		
		Thread ourThread = new Thread(this);
			/**
			 * Constructor.
			 */
		public DimensionUpdaterThread()
		{
		}
		
		public void updateScrollerValues()
		{
			
				// find the values for hte vScroll and hScroll
				// objects...
			Dimension s = getSize();

			s.width -= (VS_WIDTH + 10);
			s.height -= (HS_HEIGHT + 10);

			int v = vScroll.getValue();
			int h = hScroll.getValue();
			
			int h2 = root.width - s.width;
			int v2 = root.height - s.height;
			vScroll.setMaximum(v2 > 0 ? 20 + v2 : 0);
			hScroll.setMaximum(h2 > 0 ? 20 + h2 : 0);
			
			hScroll.setBlockIncrement(1 + h2 / 5);
			vScroll.setBlockIncrement(1 + v2 / 10);
            boolean wereBothVis = hScroll.isVisible() || vScroll.isVisible();

            hScroll.setVisible(h2 > 0);
            vScroll.setVisible(v2 > 0);

			if (h >= h2) vScroll.setValue(0);
			if (v >= v2) hScroll.setValue(0);
            if (wereBothVis && (hScroll.isVisible() || vScroll.isVisible())) doLayout();
		}
		
		public void updateDimensionsNow(Graphics g)
		{
			if (g == null) return ;
			root.recalculateBounds(g);
			updateScrollerValues();
			paint(g);
		}
		
			/**
			 * Update the dimensions.
			 */
		public synchronized void updateDimensions(Graphics g)
		{
			if (g == null) return ;
			this.g = g;
				// if the thread is running
				// then kill it and start it again...
			if (ourThread.isAlive()) ourThread.stop();
			ourThread.start();
		}
		
			/**
			 * Main runner function.
			 */
		public void run()
		{
			root.recalculateBounds(g);
			
				// after update
			paint(getGraphics());
		}
	}
}
