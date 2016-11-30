package com.sri.apps.mml;


/**
 * Class to hold information about the cursor.
 */
public class MMLCursor
{
        /**
         * Flag is set if size of the current
         * navigateablesymbol has changed.
         */
    public boolean sizeChanged = false;

        /**
         * The current position of the cursor.
         */
    public int currPos = 0;

        /**
         * The current line in which the cursor is.
         */
    public int currLine = 0;

		/**
		 * The height of the cursor.
		 */
	public int cursorHeight = 10;
	
        /**
         * The symbol that currently holds the cursor.
         */
    public MMLNavigateableSymbol holder;

        /**
         * The line number of the current symbol.
         */
    public int currSymLine = 0;

        /**
         * The index of the current symbol.
         */
    public int currSymIndex = 0;

		/**
		 * The position of this symbol in the
		 * global view.
		 * These values are set by the symbol
		 * itself.
		 */
	public int globalX = 0, globalY = 0;
	
		/**
		 * The location of the holder.
		 */
	//public int symbolX = 0, symbolY = 0;
	
		/**
		 * The number of symbols that are showing 
		 * in the current line.
		 */
	public int endingCol = 0;
	
		/**
		 * The starting column in the
		 * current line.
		 */
	public int startingCol = 0;
	
		/**
		 * The last visible line in the current
		 * paragraph (1 in the case of a token)
		 */
	public int endingLine = 0;
	
		/**
		 * The starting line in the current
		 * paragraph.
		 */
	public int startingLine = 0;
	
        /**
         * Constructor.
         */
    public MMLCursor(MMLNavigateableSymbol h)
    {
        this(0, 0, h);
    }

        /**
         * Constructor.
         */
    public MMLCursor(int pos, int line, MMLNavigateableSymbol h)
    {
        this.holder = h;
        currPos = pos;
        currLine = line;
    }
}
