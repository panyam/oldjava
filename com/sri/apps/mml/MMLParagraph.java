package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A class in which symbols can be typed.
 */
public class MMLParagraph extends MMLNavigateableSymbol
{
	
        /**
         * List of symbols.
         */
    protected MMLSymbol symbols[][] = null;

        /**
         * Width of each line.
         */
    protected short lineWidths[];

        /**
         * Height of each line.
         */
    protected short lineHeights[];

        /**
         * The number of lines
         */
    protected short nLines = 0;

        /**
         * Number of symbols in each line.
         */
    protected short nSymbols[];

        /**
         * The List of style ranges.
         */
    protected MMLRangeList rlist = null;

        /**
         * The amount by which the capacity of the vector is automatically 
         * incremented when its size becomes greater than its capacity. If 
         * the capacity increment is <code>0</code>, the capacity of the 
         * vector is doubled each time it needs to grow. 
         */
    protected int capacityIncrement = 5;

        /**
         * Constructs an empty paragraph with the specified initial capacity and
         * capacity increment. 
         *
         * @param   initialCapacity     the initial capacity of the vector.
         * @param   capacityIncrement   the amount by which the capacity is
         *                              increased when the vector overflows.
         */
    public MMLParagraph(int initialCapacity, int capacityIncrement)
    {
        if (initialCapacity < 1) initialCapacity = 1;
        this.symbols = new MMLSymbol[initialCapacity][];
        this.nSymbols = new short[initialCapacity];
        lineWidths = new short[initialCapacity];
        lineHeights = new short[initialCapacity];
        for (int i = 0;i < initialCapacity;i++)
        {
            symbols[i] = new MMLSymbol[1];
            nSymbols[i] = 0;
        }
        this.capacityIncrement = capacityIncrement;
        nLines = 1;
    }

        /**
         * Constructs an empty vector with the specified initial capacity.
         *
         * @param   initialCapacity   the initial capacity of the vector.
         */
    public MMLParagraph(int initialCapacity)
    {
        this(initialCapacity, 0);
    }

        /**
         * Constructs an empty vector. 
         */
    public MMLParagraph()
    {
        this(1, 1);
    }

        /**
         * Recalculate the preferred size.
         */
    public void recalculateBounds(Graphics g)
    {
        width = height = 0;

            // so use the default one...
        if (rlist == null)
        {
                // basically look for the maximum width and
                // the total height...
            for (int i= 0; i < nLines ; i++)
            {
                lineWidths[i] = DEFAULT_LINE_WIDTH;
                lineHeights[i] = 0;
                for (int j = 0;j < nSymbols[i];j++)
                {
                    symbols[i][j].recalculateBounds(g);
                    lineWidths[i] += (symbols[i][j].width + 2);
                    if (lineHeights[i] < symbols[i][j].height)
                    {
                        lineHeights[i] = symbols[i][j].height;
                    }
                }
                if (width < lineWidths[i]) width = lineWidths[i];
				if (lineHeights[i] < DEFAULT_LINE_HEIGHT)
				{
					lineHeights[i] = DEFAULT_LINE_HEIGHT;
				}
                height += lineHeights[i] + 3;
            }
        } else
        {
                // default font metrics
            FontMetrics defaultFM = g.getFontMetrics();
            FontMetrics currFM = null;
            int currPos = 0, currLine = 0;
            int fl, fp, tl, tp;
            int currList = 0;
            int nLists = rlist.getStyleCount();
            MMLStyleRange curr;

                // basically get each and every StyleRange from the list
                // and go thru all the symbols...
            while (currList < nLists)
            {
                curr = rlist.getStyle(currList);

                        // at the start of the character
                        // set the line width and height to zero
                if (curr.font != null &&
                    curr.fromLine == currLine && curr.fromPos == currPos)
                {
                    currList++;
                    fl = curr.fromLine;
                    fp = curr.fromPos;
                    tl = curr.toLine;
                    tp = curr.toPos;
                    g.setFont(curr.font);
                    currFM = g.getFontMetrics();
                } else
                {
                    fl = currLine;
                    fp = currPos;
                    tl = curr.fromLine;
                    tp = curr.fromPos;
                    currFM = defaultFM;
                }

                    // apply it
                    //  Here:
                    //      First do all the symbols in fl...
                    //      then  from fl + 1 to tl - 1
                    //      then in tl
                if (fp == 0)
                {
                    lineWidths[fl] = lineHeights[fl] = 0;
                        // update previous lines width and height
                    if (fl > 0)
                    {
                        if (width < lineWidths[fl - 1]) width = lineWidths[fl - 1];
                        height += lineHeights[fl - 1];
                    }
                }

                    // if on the same line...
                    // then simply increase width and heights
                if (tl == fl)
                {
                    for (int j = fp; j < nSymbols[fl]; j ++)
                    {
                        symbols[fl][j].recalculateBounds(g);
                        lineWidths[fl] += symbols[fl][j].width;
                        if (lineHeights[fl] < symbols[fl][j].height)
                        {
                            lineHeights[fl] = symbols[fl][j].height;
                        }
                    }
                } else
                {
                    for (int j = fp; j < nSymbols[fl]; j ++)
                    {

                    }

                    for (int i = fl + 1; i < tl;i++)
                    {
                        for (int j = 0; j < nSymbols[i];j++)
                        {

                        }
                    }

                        // the last line in the range...
                    for (int j = 0;j < tp;j++)
                    {
                    }
                }

                    // set the current position...
                currPos = tp;
                currLine = tl;
            }
        }
        if (width < DEFAULT_LINE_WIDTH) width = DEFAULT_LINE_WIDTH;
        if (height < DEFAULT_LINE_HEIGHT) height = DEFAULT_LINE_HEIGHT;
    }

        /**
         * Draws this list, given the view port and a graphics context.
         */
    public void draw(Graphics g, int sx, int sy, int ex, int ey, 
					 MMLCursor cursor, boolean drawBorder)
    {
        FontMetrics fm = g.getFontMetrics();
		int cursorX = -1;
		int cursorY = -1;
        int currX = sx, currY = sy;
        int line = 0, pos = 0;

                // do we have the cursor
        boolean hasCursor = (cursor.holder == this);

			// go thorugh the attribute list and use all relevant
			// attributes.
		Color fg = null, bg = null;
		if (attribs != null)
		{
			int nAttributes = attribs.length;
			for (int i = 0;i < nAttributes;i++)
			{
				if (attribs[i].id == MMLAttribute.FG_ID) 
				{
					fg = (Color)(((MMLObjectAttribute)attribs[i]).value);
				}
				else if (attribs[i].id == MMLAttribute.BG_ID) 
				{
					bg = (Color)(((MMLObjectAttribute)attribs[i]).value);
				}
			}
		}
		Color stored = g.getColor();
		if (bg != null)
		{
			g.setColor(bg);
			g.fillRect(sx, sy, width, height);
		}
		
		g.setColor(fg == null ? stored : fg);
		
				// draw a dashed rectangle around this box only
				// if the current line is empty...
		if (drawBorder || hasCursor && (nSymbols[cursor.currLine] == 0))
		{
			g.drawRect(sx, sy, width - 4, height);
			//drawDashedRectangle(g, sx, sy, width, height);
		}
		
                // if no range then draw normally...
        if (rlist == null)
        {
			int lh, nSyms, h;
			for (line = 0; line < nLines;line++)
			{
				nSyms = nSymbols[line];
				currX = sx + 4;
                lh = lineHeights[line];
				
				for (pos = 0;pos < nSyms;pos++)
				{
					h = symbols[line][pos].height;
						// then draw the cursor..
					if (cursor.currLine == line && cursor.currPos == pos && hasCursor)
					{
				                // draw the cursor here...
						cursor.cursorHeight = lh;
						g.drawLine(currX - 2, currY + 1, currX - 2, currY + lh);
							// set the current position of the cursor...
						cursor.globalX = currX - 2;
						cursor.globalY = currY + 1;
					}
						// if the symbol is within the view port
						// then draw it...
					if (currX >= sx && currX <= ex && currY >= sy && currY < ey)
					{
						symbols[line][pos].draw(g, currX, 
												currY + ((lh - h) >> 1), 
												ex, ey, cursor, drawBorder);
					}
                    currX += symbols[line][pos].width + 2;
                }
					// draw hte cursor here...
				if (cursor.currPos == nSymbols[line] && cursor.currLine == line && hasCursor)
				{
				                // draw the cursor here...
					cursor.cursorHeight = lh;
					g.drawLine(currX - 2, currY + 1, currX - 2, currY + lh);
						// set the current position of the cursor...
					cursor.globalX = currX - 2;
					cursor.globalY = currY + 1;
				}
                currY += lh + 2;
			}
        } else
        {
                // the harder part... drawing with styles and fonts
                // and colors and so on...
        }
		g.setColor(stored);
    }

        /**
         * Trims the capacity of this vector to be the vector's current 
         * size. An application can use this operation to minimize the 
         * storage of a vector. 
         */
    public void trimToSize()
    {
        int oldCapacity = symbols.length;
        if (nLines < oldCapacity)
        {
            MMLSymbol oldData[][] = symbols;
            symbols = new MMLSymbol[nLines][];
            System.arraycopy(oldData, 0, symbols, 0, nLines);
        }
    }

        /**
         *  Make sure that the given line has enough capacity.
         */ 
    private void ensureCapacity(int line, int minCapacity)
    {
        if (minCapacity < symbols[line].length) return ;
        int oldCapacity = symbols[line].length;
        MMLSymbol oldData[] = symbols[line];
        int newCapacity = (capacityIncrement > 0) ?
            (oldCapacity + capacityIncrement) : (oldCapacity * 2);
        if (newCapacity < minCapacity)
        {
            newCapacity = minCapacity;
        }
        symbols[line] = new MMLSymbol[newCapacity];
        System.arraycopy(oldData, 0, symbols[line], 0, nSymbols[line]);
    }

        /**
         * Ensures the number of lines are fine.
         */ 
    private void ensureCapacity(int minCapacity)
    {
        if (minCapacity < symbols.length) return ;
        int oldCapacity = symbols.length;
        MMLSymbol oldData[][] = symbols;
        int newCapacity = (capacityIncrement > 0) ?
            (oldCapacity + capacityIncrement) : (oldCapacity * 2);
        if (newCapacity < minCapacity)
        {
            newCapacity = minCapacity;
        }
        symbols = new MMLSymbol[newCapacity][];

        short oldList[] = nSymbols;
        nSymbols = new short[newCapacity];
        System.arraycopy(oldList, 0, nSymbols, 0, nLines);
        oldList = lineWidths;
        lineWidths = new short[newCapacity];
        System.arraycopy(oldList, 0, lineWidths, 0, nLines);
        oldList = lineHeights;
        lineHeights = new short[newCapacity];
        System.arraycopy(oldList, 0, lineHeights, 0, nLines);

            // now also increase the capacity for the line widths, heights
            // and nSymbols...
        System.arraycopy(oldData, 0, symbols, 0, nLines);
    }
    
        /**
         * Returns the current capacity of this vector.
         *
         * @return  the current capacity of this vector.
         */
    public int capacity()
    {
        return symbols.length;
    }

        /**
         * Returns the number of components in this vector.
         */
    public int paraSize()
    {
        return nLines;
    }

        /**
         * Returns the number of symbols in a given line.
         */
    public int lineSize(int line)
    {
        return nSymbols[line];
    }

        /**
         * Tests if this vector has no components.
         *
         * @return  <code>true</code> if this vector has no components;
         *          <code>false</code> otherwise.
         */
    public boolean isEmpty()
    {
        return nLines == 0;
    }

        /**
         * Returns the component at the specified index.
         *
         * @param      index   an index into this vector.
         * @return     the component at the specified index.
         * @exception  ArrayIndexOutOfBoundsException  if an invalid index was
         *               given.
         */
    public MMLSymbol symbolAt(int line, int index)
    {
        return symbols[line][index];
    }

        /**
         * Returns the first component of this vector.
         *
         * @return     the first component of this vector.
         */
    public MMLSymbol firstSymbol()
    {
        return symbols[0][0];
    }

        /**
         * Returns the first component of this vector in a given line.
         *
         * @return     the first component of this list in a given line.
         */
    public MMLSymbol firstSymbol(int line)
    {
        return symbols[line][0];
    }

        /**
         * Returns the last component of the vector.
         *
         * @return  the last component of the vector, i.e., the component
         *          at index <code>paraSize()&nbsp;-&nbsp;1</code>.
         */
    public MMLSymbol lastSymbol()
    {
        return symbols[nLines - 1][nSymbols[nLines - 1]];
    }

        /**
         * Sets the component at the specified <code>index</code> of this 
         * vector to be the specified object. The previous component at that 
         * position is discarded. 
         * <p>
         * The index must be a value greater than or equal to <code>0</code> 
         * and less than the current size of the vector. 
         *
         * @param      obj     what the component is to be set to.
         * @param      index   the specified index.
         * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
         */
    public void setSymbolAt(MMLSymbol obj, int line, int index)
    {
        if (line >= nLines || index >= nSymbols[line])
        {
            throw new ArrayIndexOutOfBoundsException(
                            "Invalid index: " + line + ", " + index);
        }
        symbols[line][index] = obj;
    }

        /**
         * Removes n symbols from 'index' on 'line'
         */
    public void removeSymbols(int line, int index, int nSyms)
    {
        int j = nSymbols[line] - (index + nSyms);
        int p = index - 1;
        int n = index + nSyms;

        if (j > 0)
        {
            System.arraycopy(symbols[line], index + nSyms,
                             symbols[line], index, j);
        }

        nSymbols[line] -= nSyms;
        for (int i = 0;i < nSyms;i++) symbols[line][nSymbols[line] + i] = null;
    }

        /**
         * Inserts the specified object as a component in this vector at the 
         * specified <code>index</code>. Each component in this vector with 
         * an index greater or equal to the specified <code>index</code> is 
         * shifted upward to have an index one greater than the value it had 
         * previously. 
         * <p>
         * The index must be a value greater than or equal to <code>0</code> 
         * and less than or equal to the current size of the vector. 
         *
         * @param      obj     the component to insert.
         * @param      index   where to insert the new component.
         */
    public void insertSymbolAt(MMLSymbol obj, int line, int index)
    {
        int newcount = nSymbols[line] + 1;
        if (newcount > symbols[line].length)
        {
            ensureCapacity(line, newcount);
        }
        MMLSymbol cline[] = symbols[line];
        System.arraycopy(cline, index, cline,index + 1,nSymbols[line] - index);
        symbols[line][index] = obj;
        
        if (nLines <= line) nLines = (short)(line + 1);
        nSymbols[line]++;
    }

        /**
         * Add a blank line at the end.
         */
    public synchronized void addLine()
    {
        ensureCapacity(nLines + 1);
        lineWidths[nLines] = lineHeights[nLines] = nSymbols[nLines] = 0;
        symbols[nLines] = new MMLSymbol[1];
        nLines++;
    }

        /**
         * Insert a blank line at the given position.
         */
    public synchronized void insertLine(int pos)
    {
        ensureCapacity(nLines + 1);
        System.arraycopy(symbols, pos, symbols, pos + 1, nLines - pos);
        System.arraycopy(lineWidths, pos, lineWidths, pos + 1, nLines - pos);
        System.arraycopy(lineHeights, pos, lineHeights, pos + 1, nLines - pos);
        System.arraycopy(nSymbols, pos, nSymbols, pos + 1, nLines - pos);
        lineWidths[pos] = lineHeights[pos] = nSymbols[pos] = 0;
        symbols[pos] = new MMLSymbol[1];
        nLines++;
    }

        /**
         * Adds the specified component to the end of this vector, 
         * increasing its size by one. The capacity of this vector is 
         * increased if its size becomes greater than its capacity. 
         *
         * @param   obj   the component to be added.
         */
    public void addSymbol(MMLSymbol obj, int line)
    {
			// first check that we have enough number of lines...
		while (nLines <= line) addLine();		
        int newcount = nSymbols[line] + 1;
        if (newcount > symbols[line].length)
        {
            ensureCapacity(line, newcount);
        }
        symbols[line][nSymbols[line]++] = obj;
    }

        /**
         * Removes all components from this paragraph and sets its size to zero.
         */
    public void removeAllSymbols()
    {
        for (int i = 0; i < nLines; i++) 
        {
            symbols[i] = null;
            nSymbols[i] = 0;
        }
        nLines = 0;
    }

        /**
         * Process a key and return an appropriate cursor.
         *
         * What is returned is very important.  Currently what is returned
         * is the "next" symbol in which the cursor is present.  if the
         * next symbol is null, then it means that the cursor should be
         * "sent" back to the parent of this symbol.  Otherwise, it means
         * the focus is being sent to one of the child symbols.  However,
         * how do we handle the event where a new symbol is just being
         * created.  This can occur when ur on a new line and after you
         * press a key the new symbol will just be created.  We can
         * return the new symbol and even send the event to that symbol.
         * However, how do we return the fact that the new symbol has just
         * been created and the cursor should be in position 1 rather than
         * position 0 of THAT new symbol?
         */
    public MMLNavigateableSymbol processKey(KeyEvent e,
											MMLCursor cursor)
    {
        int l = cursor.currLine;
        int c = cursor.currPos;
        int kc = e.getKeyCode();
        int ch = e.getKeyChar();
        switch (kc)
        {
            case KeyEvent.VK_LEFT:
            {
                if (c <= 0) return null;
                else
                {
                            // see if the previous symboll
                            // is a holder...
                    if (symbols[l][c - 1] instanceof MMLNavigateableSymbol)
                    {
                        cursor.currSymIndex = c - 1;
                        return (MMLNavigateableSymbol)symbols[l][c - 1];
                    } else
                    {
                        cursor.currPos--;
                        return this;
                    }
                }
            }
            case KeyEvent.VK_RIGHT:
            { 
                if (c >= nSymbols[l]) return null;
                else
                {
                            // see if the previous symboll
                            // is a holder...
                    if (symbols[l][c] instanceof MMLNavigateableSymbol)
                    {
                        cursor.currSymIndex = c;
						cursor.currSymLine = l;
                        return (MMLNavigateableSymbol)symbols[l][c];
                    } else
                    {
                        cursor.currPos++;
                        return this;
                    }
                }
            }
            case KeyEvent.VK_UP:
            {
                if (l <= 0) return null;
                else
                {
                    cursor.currLine--;
                    if (cursor.currPos >= nSymbols[cursor.currLine])
                    {
                        cursor.currPos = nSymbols[cursor.currLine];
                    }
                    cursor.currSymIndex = cursor.currPos;
                    cursor.currSymLine = cursor.currLine;
                    return this;
                }
            }
            case KeyEvent.VK_DOWN:
            {
                if (l >= nLines - 1) return null;
                else
                {
                    cursor.currLine++;
                    if (cursor.currPos >= nSymbols[cursor.currLine])
                    {
                        cursor.currPos = nSymbols[cursor.currLine];
                    }
                    cursor.currSymIndex = cursor.currPos;
                    cursor.currSymLine = cursor.currLine;
                    return this;
                }
            }
            case KeyEvent.VK_HOME:
            {
                cursor.currPos = 0;
                return this;
            }
            case KeyEvent.VK_END:
            {
                cursor.currPos = nSymbols[cursor.currLine];
                return this;
            }

                    // means size changes...
            case KeyEvent.VK_DELETE:
            {
                cursor.sizeChanged = true;
            }
            case KeyEvent.VK_BACK_SPACE:
            {
                cursor.sizeChanged = true;
            } break;
            case KeyEvent.VK_ENTER:
            {
                    // it means, we break at this point and create a new
                    // line....
                if (nLines == 0 || l == nLines - 1) 
                {
                    addLine();
                } else
                {
                    insertLine(l + 1);
                }

                    // copy everything from the current line after the
                    // cursor to the next line...
                short ns = (short)(nSymbols[l] - c);
                ensureCapacity(l + 1, ns);
                System.arraycopy(symbols[l], c, symbols[l + 1], 0, ns);
                nSymbols[l] -= ns;
                nSymbols[l + 1] = ns;
                cursor.currLine ++;
                cursor.currPos = 0;
                cursor.sizeChanged = true;
            } break;
            default:
            {
                    // if it is a "typeable" key then enter the key
                    // appropriately and set the sizechanged flag to true.
                    // for the time being we only worry about letters
                    // and characters... just for testing...
                    // later we put in all the other ones...
                if ((ch >= 'a' && ch <= 'z') ||
                    (ch >= 'A' && ch <= 'Z') ||
                    (ch >= '0' && ch <= '9'))
                {
                        // if we are here it means we are currently
                        // empty...  so insert a new MultiCharToken symbol
                        // at the current spot and send it the event...
                    MMLMultiCharToken mcT = new MMLMultiCharToken("");
                        
                        // insert the new EMPTY symbol...
					cursor.currSymIndex = cursor.currPos;
					cursor.currSymLine = cursor.currLine;
                    insertSymbolAt(mcT, cursor.currLine, cursor.currPos);

                        // now return the new symbol...
                    return mcT;
                }
            } break;
        }
        return this;
    }
	
        /**
         * Exit into this container from a CHILD symbol.
         *
         * @param kc    The key stroke used to enter this container.
         * @param old   The cursor information of the "exiting" container
         * @param curr  The cursor information of the current container.
         *              This will be updated by the enter function.
         */
    public MMLNavigateableSymbol exit (KeyEvent e, MMLCursor old, MMLCursor curr)
	{
        int kc = e.getKeyCode();
        switch (kc)
        {
            case KeyEvent.VK_LEFT:
            {
				curr.currPos = curr.currSymIndex;
            } break;
            case KeyEvent.VK_RIGHT:
            { 
					// see where we are exiting from...
				curr.currPos = Math.min(nSymbols[curr.currLine], 
										curr.currSymIndex + 1);
            } break;
            case KeyEvent.VK_UP:
            {
                if (curr.currLine <= curr.currSymLine)
                {
                    curr.currLine = curr.currSymLine;
                } else
                {
                    curr.currLine = curr.currSymLine - 1;
                }
				if (curr.currLine < 0)
				{
					curr.currLine ++;
					return null;
				}
            } break;
			case KeyEvent.VK_ENTER:
			{
						// this means
                if (old.holder instanceof MMLMultiCharToken)
                {
                    int l = curr.currLine;
                    int c = old.currSymIndex;
                    MMLMultiCharToken mct = ((MMLMultiCharToken)old.holder).breakAt(old.currPos);
                    if (l == nLines - 1)
                    {
                        addLine();
                    } else
                    {
                        insertLine(l + 1);
                    }

                        // copy everything from the current line after the
                        // cursor to the next line...
                    short ns = (short)(nSymbols[l] - c - 1);
                    ensureCapacity(l + 1, ns + 1);
                    System.arraycopy(symbols[l], c + 1, symbols[l + 1], 1, ns);
                    symbols[l + 1][0] = mct;
                    nSymbols[l] -= (ns);
                    nSymbols[l + 1] = (short)(ns + 1);
                    curr.currLine ++;
                    curr.currPos = 0;
                    curr.sizeChanged = true;
                }
			} break;
            case KeyEvent.VK_DOWN:
            {
                if (curr.currLine > curr.currSymLine)
                {
                    curr.currLine = curr.currSymLine;
                } else
                {
                    curr.currLine = curr.currSymLine + 1;
                }
				if (curr.currLine >= nLines)
				{
					curr.currLine --;
					return null;
				}
            } break;
        }
        return this;
	}

        /**
         * Enter into the next container.
         *
         * @param kc    The key stroke used to enter this container.
         * @param old   The cursor information of the "exiting" container
         * @param curr  The cursor information of the current container.
         *              This will be updated by the enter function.
         */
    public MMLNavigateableSymbol enter (KeyEvent e, MMLCursor old, MMLCursor curr)
    {
        int kc = e.getKeyCode();
        switch (kc)
        {
            case KeyEvent.VK_LEFT:
            { 
				curr.currLine = 0;
				curr.currPos = nSymbols[0];
				if (curr.currPos < 0) curr.currPos = 0;
            } break;
            case KeyEvent.VK_RIGHT:
            { 
				curr.currPos = 0;
				curr.currLine = 0;
            } break;
            case KeyEvent.VK_UP:
            {
				curr.currPos = 0;
				curr.currLine = nLines - 1;
            } break;
            case KeyEvent.VK_DOWN:
            {
				curr.currLine = curr.currPos = 0;
            } break;
        }
        return this;
    }

		/**
		 * Given a point where the mouse was pressed, returns the symbol
		 * that is at the point.
		 * The cursor information MUST be set by the symbol.
		 * null is returned if no navigateable symbol is found
		 * at the point.
		 */
	public MMLNavigateableSymbol mousePressed(Point p,MMLCursor cursor)
	{
		int i = 0;
		int y = 0;
		while (i < nLines && ((y + lineHeights[i]) < p.y)) 
		{
			y += lineHeights[i];
			i++;
		}
		cursor.currLine = Math.min(nLines - 1, i);
		if (i == nLines) return null;
		int j = 0;
		int x = 0;
		int ns = nSymbols[i];
		while (j < ns && ((x + symbols[i][j].width) < p.x)) 
		{
			x += symbols[i][j].width;
			j++;
		}
		cursor.currPos = j;
		if (j == ns) return null;
		if (symbols[i][j] instanceof MMLNavigateableSymbol)
		{
			p.x -= x;
			p.y -= y;
			return (MMLNavigateableSymbol)symbols[i][j];
		}
		return null;
	}
}
