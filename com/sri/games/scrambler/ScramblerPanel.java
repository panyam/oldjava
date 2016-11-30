package com.sri.games.scrambler;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;

public class ScramblerPanel extends Canvas 
							implements	MouseListener, 
										MouseMotionListener,
										Runnable
{
	protected static ColorModel dcm = new DirectColorModel(32,
														   0xff << 16,
														   0xff << 8, 
														   0xff << 0);
	
		/**
		 * The minimum block size.
		 */
	protected final static int MIN_BLOCK_SIZE = 50;

		/**
		 * show the grid?
		 */
	protected boolean isGridVisible = true;
	
		/**
		 * Different move types.
		 */
	public final static byte LEFT  = 0;
	public final static byte RIGHT = 1;
	public final static byte TOP = 2;
	public final static byte BELOW = 3;

	protected int incrementX = 5, incrementY = 5;
	
	protected final static byte opposite[] = { RIGHT, LEFT, BELOW, TOP};
		/**
		 * The current iamge that is to 
		 * be broken into blocks
		 */
	protected Image puzzleImage;
		
		/**
		 * The image of each block.
		 */
	protected Image blockImages[];
		/**
		 * blockId[i][j] tells the id of the image
		 * that is in row i and col j.
		 * The ids of the images are stored in blockImages.
		 */
	protected int blockId[][];

		/**
		 * The left and top coordinates of teh puzzle.
		 */
	int left, top;
		/**
		 * The block size.
		 */
	int blockWidth = -1, blockHeight = -1;
	
		/**
		 * Tells if the puzzle has been changed or not.
		 */
	protected boolean puzzleChanged = false;

		/**
		 * TElls which one the empty block is.
		 */
	protected int currEmptyBlock = 0;
	
		/**
		 * Number of rows and columns in the puzzle
		 */
	protected int nRows = 0, nCols = 0;
	
		/**
		 * The rpeferred size.
		 */
	public Dimension prefSize = new Dimension();
	
		/**
		 * Holds the "opposite" of all the moves
		 * that have been done so far so that when
		 * the puzzle needs to be solved all you do 
		 * is pop the moves of hte stack!!!
		 * Slick eh!!
		 */
	protected byte []solutionStack = new byte[1024];
	int nMoves = 0;
	
		/**
		 * Tells if we should animate a move or not.
		 */
	protected boolean animate = true;
	
		/**
		 * Double buffering parameters
		 */
	protected Dimension buffSize = new Dimension();
	protected Image buffer = null;
	protected Graphics buffG = null;

		/**
		 * Id of the block that is moving..
		 */
	protected int movingBlockId = -1;
	protected int movingX, movingY;
	protected int fRow, fCol, tRow, tCol;
	protected boolean threadRunning = false;
	
	protected final static int DEFAULT_THREAD_SPEED = 1;
	
		/**
		 * Time between updates.
		 */
	protected long threadSpeed = DEFAULT_THREAD_SPEED;
	
		/**
		 * The thread that is used for moving a piece
		 */
	protected Thread ourThread = null;

	protected short pressedRow = -1, pressedCol = -1;
	protected short currentRow = -1, currentCol = -1;
	
		/**
		 * Constructor.
		 */
	public ScramblerPanel(Image im, int nr, int nc)
	{
		setPuzzleImage(im);
		setPuzzleSize(nr, nc);
		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
		/**
		 * Set the puzzle image.
		 */
	public void setPuzzleImage(Image im)
	{
		this.puzzleImage = im;
		puzzleChanged = true;
		paint(getGraphics());
	}

		/**
		 * Set the # rows and cols of the pzuzle.
		 */
	public void setPuzzleSize(int nRows, int nCols)
	{
		if (this.nRows != nRows || this.nCols != nCols)
		{
			this.nRows = nRows;
			this.nCols = nCols;
			puzzleChanged = true;
			paint(getGraphics());
		}
	}
	
		/**
		 * Makes the actual puzzle.
		 */
	public boolean makePuzzle() throws Exception
	{
		if (nRows <= 0 || nCols <= 0 ) return false;
			// also set the puzzle size now.
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(puzzleImage, 0);
		try
		{
			mt.waitForAll();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		prefSize.width = puzzleImage.getWidth(null);
		prefSize.height = puzzleImage.getHeight(null);
		
		int nImages = nRows * nCols - 1;
		if (blockImages == null || blockImages.length < nImages)
		{
			blockImages = null;
			blockImages = new Image[nImages];
		}
		if (blockId == null || blockId.length < nRows * nCols)
		{
			blockId = null;
			blockId = new int[nRows][nCols];
		}
		
		
		for (int i = 0, counter = 0;i < nRows;i++)
		{
			for (int j = 0;j < nCols;j++)
			{
				blockId[i][j] = counter++;
			}
		}
			
			// last one is an empty block!!!
		blockId[nRows - 1][nCols - 1] = -1;
		this.currEmptyBlock = nImages;
		
			// now to make the actual block...
			// we will ensure that blocks are no less than
			// 40 x 40 in dimension to ensure that pieces
			// dont get too small!!!
		int bw = prefSize.width / nCols;
		int bh = prefSize.height / nRows;
		
		if (bw < MIN_BLOCK_SIZE || bh < MIN_BLOCK_SIZE)
		{
			throw new Exception("Blocks are too small.  " + 
								"Decrease the number of rows and/or columns");
		}
		
			// used for the image...
		int pixList[] = new int[bw * bh];

		for (int pic = 0;pic < nImages;pic++)
		{
			pixList = new int[bw * bh];
			int r = pic / nCols;
			int c = pic % nCols;
			int x = c * bw;
			int y = r * bh;
			PixelGrabber pg = new PixelGrabber(puzzleImage, x, y, 
											   bw, bh, 
											   pixList, 0, bw);
			try {
				pg.grabPixels(0);
				blockImages[pic] = createImage(new MemoryImageSource(bw,bh,
																	 dcm,
																	 pixList,
																	 0,bw));
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		puzzleChanged = false;
		return true;
	}

		/**
		 * Scramble the puzzle by applying nR random
		 * moves.
		 */
	public void scramble(int nMoves)
	{
			// make nMoves random moves...
		boolean an = animate;
		animate = false;
		threadSpeed = 1;
		int cRow = currEmptyBlock / nCols;
		int cCol = currEmptyBlock % nCols;
		for (int i = 0;i < nMoves;i++)
		{
				// which tells "which" direction
				// to move the block "from"
			byte which;
				// the row and col of the block
				// where the move happens...
			int fromRow, fromCol;
			cRow = currEmptyBlock / nCols;
			cCol = currEmptyBlock % nCols;
			// see if it is a valid move...
			boolean valid = true;
			do
			{
				which = (byte)(Math.random() * 4);
				valid = !((cRow == 0 && which == TOP) ||
						(cRow == nRows - 1 && which == BELOW) ||
						(cCol == 0 && which == LEFT) ||
						(cCol == nCols - 1 && which == RIGHT));
			} while (!valid);
			fromCol = cCol;		fromRow = cRow;
			if (which == TOP) fromRow = cRow - 1;
			else if (which == BELOW) fromRow = cRow + 1;
			else if (which == LEFT) fromCol = cCol - 1;
			else fromCol = cCol + 1;
			makeMove(fromRow, fromCol, cRow, cCol);
			try
			{
				Thread.sleep(10);
			} catch (Exception e)
			{
			}
			cRow = fromRow;
			cCol = fromCol;
		}
		animate = an;
		threadSpeed = DEFAULT_THREAD_SPEED;
	}
	
		/**
		 * Tells if the puzzle is solved or not
		 */
	public boolean isSolved()
	{
		int nImages = nRows * nCols - 1;
		for (int i = 0;i < nImages;i++)
		{
			if (blockId[i / nCols][i % nCols] < 
				blockId[(i - 1) / nCols][(i - 1) % nCols]) return false;
		}
		return true;
	}
	
		/**
		 * Solve the puzzle.
		 */
	public void solve(int method)
	{
			// solve using dfs!!!
		if (method == 0)
		{
			// basically keep doing till we get somewhere...
		}
	}
	
		/**
		 * Tells if the grid is to be shown.
		 */
	public void showGrid(boolean sg)
	{
		this.isGridVisible = sg;
		paint(getGraphics());
	}
	
		/**
		 * Tells if we need to animate or not
		 */
	public void setAnimated(boolean an)
	{
		this.animate = an;
	}

		/**
		 * Make the move...
		 */
	public synchronized void makeMove(int fRow, int fCol, int tRow, int tCol)
	{
			// wait till the thread is running
			// ie the piece is moving...
		while (threadRunning)
		{
			try
			{
				wait();
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

			// set the src and dest blocks
		this.fRow = fRow;
		this.fCol = fCol;
		this.tRow = tRow;
		this.tCol = tCol;
		movingBlockId = fRow * nCols + fCol;
		
		currEmptyBlock = (fRow * nCols) + fCol;
		if (!animate)
		{
			movingBlockId = -1;
			// see the mode in which we are in...
			int currId = blockId[fRow][fCol];
			blockId[fRow][fCol] = -1;
			//currEmptyBlock = (fRow * nCols) + fCol;
			blockId[tRow][tCol] = currId;
			paint(getGraphics());
			return ;
		}
		threadRunning = true;
		ourThread = new Thread(this);
		ourThread.start();
	}

		/**
		 * Thread method
		 */
	public void run()
	{
		int currId = blockId[fRow][fCol];
		movingX = left + fCol * blockWidth;
		movingY = top + fRow * blockHeight;
		int endX = left + tCol * blockWidth;
		int endY = top + tRow * blockHeight;

		int incX = 0;
		int incY = 0;
			
		if (endX > movingX) incX = incrementX;
		else if (endX < movingX) incX = -incrementX;
		if (endY > movingY) incY = incrementY;
		else if (endY < movingY) incY = -incrementY;

		while (threadRunning &&
			   (endY != movingY || endX != movingX))
		{
			//if (freeCell == 
			try
			{
				Thread.sleep(threadSpeed);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			movingX += incX;
			movingY += incY;
				
				// if we cant go another increment then sto phere...
			if ((endX - movingX) * (movingX + incX - endX) > 0) movingX = endX;
			if ((endY - movingY) * (movingY + incY - endY) > 0) movingY = endY;
			paint(getGraphics());
		}
		movingBlockId = -1;
		//currEmptyBlock = (cRow * nCols) + cCol;
			// set the current block to -1
		blockId[fRow][fCol] = -1;
		blockId[tRow][tCol] = currId;
		paint(getGraphics());
		threadRunning = false;
			//	notifyAll that we are done...
		//notify();
	}
	
	public void udpate(Graphics g)
	{
	}
	
		/**
		 * Paint method.
		 */
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		
			// first check if a puzzle has been created or
			// changed...
		try
		{
			if (puzzleChanged && !makePuzzle()) return ;
		} catch (Exception e)
		{
			return ;
		}
		calculateBlockSizes(d);
		if (!updateDoubleBuffer(d)) return ;

			// now do the drawing...
		int pos = 0;
		for (int i = 0, y = top; i < nRows; i++, y += blockHeight)
		{
			for (int j = 0, x = left; j < nCols; j++, x += blockWidth, pos++)
			{
				int id = blockId[i][j];
				if (id >= 0 && pos != movingBlockId)
				{
					buffG.drawImage(blockImages[id], x, y, blockWidth, blockHeight, null);
				}
			}
		}

			// draw the grids if we have to...
		if (this.isGridVisible)
		{
			int right = left + blockWidth * nCols;
			int down = top + blockHeight * nRows;
			buffG.setColor(Color.lightGray);
			for (int i = 0, y = top;i <= nRows;i++, y += blockHeight)
			{
				buffG.drawLine(left, y, right, y);
			}
			for (int i = 0, x = left;i <= nCols;i++, x += blockWidth)
			{
				buffG.drawLine(x, top, x, down);
			}
		}
		
			// now draw the moving block...
			// if a block is moving...
		if (movingBlockId >= 0)
		{
			buffG.drawImage(blockImages[blockId[movingBlockId / nCols]
												[movingBlockId % nCols]], 
							movingX, movingY, 
							blockWidth, blockHeight, null);
		}
		
		g.drawImage(buffer, 0, 0, null);
	}

		/**
		 * Update the back buffer for this panel.
		 */
	protected boolean updateDoubleBuffer(Dimension d)
	{
		if (buffer == null || d.width != buffSize.width ||
							  d.height != buffSize.height)
		{
			if (buffer != null) buffer.flush();
			buffSize.width = Math.max(d.width, 1);
			buffSize.height = Math.max(d.height, 1);
			buffer = createImage(buffSize.width, buffSize.height);
			if (buffer == null) return false;
			if (buffG != null) buffG.dispose();
			buffG = buffer.getGraphics();
			if (buffG == null) return false;
		}
		buffG.setColor(getBackground());
		buffG.fillRect(0, 0, d.width, d.height);
		return true;
	}
	
		/**
		 * Sets the block sizes and offsets.
		 */
	protected void calculateBlockSizes(Dimension d)
	{
		left = top = 0;
		blockWidth = prefSize.width / nCols;
		blockHeight = prefSize.height / nRows;
		if (d.width > prefSize.width)
		{
			left = (d.width - prefSize.width) / 2;
		} else if (d.width < prefSize.width)
		{
			blockWidth = Math.max(1, (d.width / nCols));
		}
		if (d.height > prefSize.height)
		{
			top = (d.height - prefSize.height) / 2;
		} else if (d.height < prefSize.height)
		{
			blockHeight = Math.max(1, (d.height / nRows));
		}
	}
	/////////////////////////////////////////////////////////////////////
	//			Game Engine Methods
	/////////////////////////////////////////////////////////////////////
		/**
		 * Given a cell find where the free cell is.
		 * Returns -1 if none and UP, LEFT, DOWN or RIGHT
		 * if one of these cells is free!!!
		 */
	protected byte getFreeCell(int row, int col)
	{
		if (row > 0 && blockId[row - 1][col] == -1) return TOP;
		if (row < nRows - 1 && blockId[row + 1][col] == -1) return BELOW;
		if (col > 0 && blockId[row][col - 1] == -1) return LEFT;
		if (col < nCols - 1 && blockId[row][col + 1] == -1) return RIGHT;
		return -1;
	}

	//////////////////////////////////////////////////////////////////
	//		Event Handlers
	//////////////////////////////////////////////////////////////////
		/**
		 * Mouse CLicked event.
		 */
	public void mouseClicked(MouseEvent e) { }
	
		/**
		 * Mouse Pressed event.
		 */
	public void mousePressed(MouseEvent e)
	{
		currentRow = pressedRow = (short)((e.getY() - top) / blockHeight);
		currentCol = pressedCol = (short)((e.getX() - left) / blockWidth);
	}
	
		/**
		 * Mouse Released event.
		 */
	public void mouseReleased(MouseEvent e)
	{
		if (pressedRow >= 0 && pressedCol >= 0)
		{
			currentRow = (short)((e.getY() - top) / blockHeight);
			currentCol = (short)((e.getX() - left) / blockWidth);
			
			if (currentRow == pressedRow &&
				currentCol == pressedCol)
			{
					// find where the free cell is and
					// move to that direction...
				int movingDir = getFreeCell(currentRow, currentCol);
				pressedRow = pressedCol = -1;
				if (movingDir >= 0)
				{
					// 
					int toRow = currentRow, toCol = currentCol;
					if (movingDir == LEFT) toCol--;
					else if (movingDir == RIGHT) toCol++;
					else if (movingDir == TOP) toRow--;
					else if (movingDir == BELOW) toRow++;
					makeMove(currentRow, currentCol, toRow, toCol);
				}
			}
		}
		pressedRow = currentRow = -1;
		pressedCol = currentCol = -1;
	}
	
		/**
		 * Mouse entered event.
		 */
	public void mouseEntered(MouseEvent e) { }
	
		/**
		 * Mouse Exited event.
		 */
	public void mouseExited(MouseEvent e) { }
	
		/**
		 * Mouse Dragged event.
		 */
	public void mouseDragged(MouseEvent e) { }
	
		/**
		 * Mouse Moved event.
		 */
	public void mouseMoved(MouseEvent e) { }
	
		/**
		 * Component resized event handler.
		 */
	public void componentResized(ComponentEvent e)
	{
		paint(getGraphics());
	}
	
		/**
		 * Component hidden event handler.
		 */
	public void componentHidden(ComponentEvent e) { }
	
		/**
		 * Component moved event handler.
		 */
	public void componentMoved(ComponentEvent e) { }
	
		/**
		 * Component shown event handler.
		 */
	public void componentShown(ComponentEvent e)
	{
		paint(getGraphics());
	}
}
