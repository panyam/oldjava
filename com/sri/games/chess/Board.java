import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.event.*;

/**
 * A class for a chess board also acts as a gui.
 * Technically we should separate the board and the gui object
 * but since there is a one-to-one relationship, we can clamp
 * it down in one class.
 */
public class Board extends Container
                   implements
                        MouseListener,
                        MouseMotionListener
{
        /**
         * The status of the player if no piece has been
         * selected.
         */
    protected final static int NO_STATUS = 0;

        /**
         * The status of the player if a piece has been selected.
         */
    protected final static int PIECE_SELECTED = 1;

        /**
         * Tells that the destination is selected.
         */
    protected final static int DEST_SELECTED = 2;

    public final static int INVALID_MOVE = -1;
    public final static int VALID_MOVE = 0;
    /*
        // In the current board, this holds
        // all the possible moves for the black player.
    protected int blackMoves[100];
        // number of possible black moves.
    int nBlackMoves = 0;
        // In the current board, this holds
        // all the possible moves for the white player.
    protected int whiteMoves[100];
        // number of possible white moves.
    int nWhiteMoves = 0;
    */
        /**
         * Denotes an empty square.
         */
    public final static int EMPTY = 0;

        /**
         * Tells what pieces are in what squares.
         *
         * Basically row 0 and 1 are initially occupied by white pieces
         * and row 6 and 7 by black pieces.
         */
    int pieceAt[][] = new int[8][8];

        /**
         * The color of the piece occupying square i,j
         */
    int colorAt[][] = new int[8][8];

        /**
         * Rank of the two kings.
         */
    int kingRank[] = new int[2];

        /**
         * Shows information about a given piece and a given
         * color.
         */
    short position[][] = new short[2][6];

        /**
         * File of the two kings.
         */
    int kingFile[] = new int[2];

        /**
         * The status of the mouse.  Used for representing the
         * games states.
         */
    int status = NO_STATUS;

        /**
         * Color of the current player.
         */
    int currColor = General.WHITE;

        /**
         * Colour of the cell highlight.
         */
    Color highlightColor = Color.black;

        // the row and column on which the mouse was pressed.
    int fromRank = -1, fromFile= -1;
    int toRank = -1, toFile = -1;
        // the rank and file over which the mouse current is.
    int currentRank = -1, currentFile = -1;

        /**
         * If board is inverted, then the black pieces are shown at 
         * the bottom of the board.
         */
    boolean invertBoard = false;
    int boardX, boardY;
    int boardSize = 1, cellSize = 1;

        /**
         * A class for drawing the piece.
         */
    protected PieceDrawer pieceDrawer = new PieceDrawer();

        /**
         * Double buffering info.
         */
	protected Image buffImage = null;
	protected Dimension buffSize = new Dimension();
	protected Graphics buffGraphics = null;

        /**
         * Constructor.
         */
    public Board()
    {
        reset();
        setBackground(Color.red.brighter().brighter());
        addMouseListener(this);
        addMouseMotionListener(this);
    }

        /**
         * The pieceAt is reset, thereby reinstating all
         * pieces.
         */
    public void reset()
    {
        for (int i = 0;i < 8;i++)
        {
            for (int j = 0;j < 8;j++)
            {
                pieceAt[i][j] = Piece.NO_PIECE;
                colorAt[i][j] = -1;
            }
        }

                // set the colours of pieces
                // on the board
        for (int i = 0;i < 8;i++)
        {
            colorAt[0][i] = colorAt[1][i] = General.WHITE;
            colorAt[6][i] = colorAt[7][i] = General.BLACK;
        }

            // now set the pawns
        for (int j = 0;j < 8;j++)
        {
            pieceAt[1][j] = pieceAt[6][j] = Piece.PAWN;
        }
            // set the white pieces
        pieceAt[0][0] = pieceAt[0][7] = 
        pieceAt[7][0] = pieceAt[7][7] = Piece.ROOK;

        pieceAt[0][1] = pieceAt[0][6] = 
        pieceAt[7][1] = pieceAt[7][6] = Piece.KNIGHT;

        pieceAt[7][2] = pieceAt[7][5] = 
        pieceAt[0][2] = pieceAt[0][5] = Piece.BISHOP;

        pieceAt[0][4] = pieceAt[7][4] = Piece.KING;
        kingRank[General.WHITE] = 0;
        kingRank[General.BLACK] = 7;
        kingFile[General.WHITE] = 4;
        kingFile[General.BLACK] = 4;

        pieceAt[0][3] = pieceAt[7][3] = Piece.QUEEN;

            // also reset the "position" values of all the
            // pieces
    }

		/**
		 * Paint method.
		 */
	public synchronized void paint(Graphics g)
	{
		Dimension d = getSize();
        FontMetrics fm = null;
        int stringWidth = 0, stringHeight = 0;
		if (buffImage == null ||
            buffSize.width != d.width || buffSize.height != d.height)
		{
			if (buffImage != null) buffImage.flush();
			if (buffGraphics != null) buffGraphics.dispose();
			
			buffSize.width = Math.max(1,d.width);
			buffSize.height = Math.max(1,d.height);
			
			buffImage = createImage(buffSize.width,buffSize.height);
			if (buffImage == null) return ;
			buffGraphics = buffImage.getGraphics();

            // now draw the board itself...
            // the board size would be the minimum size of the buffer's
            // width and height and the maximum multiple of 8 to that
            // figure.
            fm = buffGraphics.getFontMetrics();
            stringHeight = fm.getMaxAscent() + 10;
            stringWidth = fm.stringWidth("W") + 10;

		    boardSize = Math.min(buffSize.width - stringWidth,
                                 buffSize.height - stringHeight);

		    boardSize = ((boardSize / 8) * 8) - 16;

            boardX = (buffSize.width - boardSize) / 2;
            boardY = (buffSize.height - boardSize) / 2;
            cellSize = boardSize / 8;
            pieceDrawer.setCellSize(cellSize, cellSize, buffGraphics);
		}

        fm = buffGraphics.getFontMetrics();
        stringHeight = fm.getMaxAscent();
        stringWidth = fm.stringWidth("W");

		if (buffGraphics == null) return ;
		
		buffGraphics.setColor(getBackground());
		buffGraphics.fillRect(0,0,buffSize.width,buffSize.height);

        for (int i = 7, y = boardY, col; i >= 0; i--, y += cellSize)
        {
            col = (i + 1) % 2;
            for (int j = 0, x = boardX;j < 8;j++, x += cellSize)
            {
                buffGraphics.setColor(pieceDrawer.cellColors[col]);
                buffGraphics.fillRect(x, y, cellSize, cellSize);
                if (colorAt[i][j] == General.WHITE ||
                    colorAt[i][j] == General.BLACK)
                {
                    pieceDrawer.drawPiece(buffGraphics, x, y,
                                        pieceAt[i][j],
                                        colorAt[i][j],
                                        false);
                }
                col = (col + 1) % 2;
            }
        }

        buffGraphics.setColor(Color.black);
        for (int i = 0,
                    x = boardX + (cellSize - stringWidth) / 2,
                    y = boardY + (cellSize + stringHeight) / 2;
             i < 8;i++)
        {
            buffGraphics.drawString("" + (char)('a' + i), x, boardY + boardSize + stringHeight);
            buffGraphics.drawString((8 - i) + "", boardX - stringWidth, y);
            x += cellSize;
            y += cellSize;
        }

            // now draw the highlights if necessary
        if (fromRank >= 0)
        {
            buffGraphics.setColor(highlightColor);
            buffGraphics.drawRect(boardX + (fromFile * cellSize),
                                  boardY + boardSize - (fromRank * cellSize)
                                         - cellSize,
                                  cellSize, cellSize);
            buffGraphics.drawRect(1 + boardX + (fromFile * cellSize),
                                  1 + boardY +
                                    boardSize - (fromRank * cellSize)
                                         - cellSize,
                                  cellSize - 2, cellSize - 2);
        } 

        if (toRank >= 0)
        {
            buffGraphics.setColor(highlightColor);
            buffGraphics.drawRect(boardX + (toFile * cellSize),
                                  boardY + boardSize - (toRank * cellSize)
                                         - cellSize,
                                  cellSize, cellSize);
            buffGraphics.drawRect(1 + boardX + (toFile * cellSize),
                                  1 + boardY +
                                    boardSize - (toRank * cellSize)
                                         - cellSize,
                                  cellSize - 2, cellSize - 2);
        }

		buffGraphics.setColor(Color.black);
		buffGraphics.drawRect(boardX,boardY,boardSize, boardSize);
		if (buffImage != null) g.drawImage(buffImage,0,0,this);
	}
	

        /**
         * Mouse Pressed Event handler.
         */
    public void mousePressed(MouseEvent e)
    {
        toRank = toFile = -1;
        if (status == NO_STATUS)
        {
            fromRank = 7 - ((e.getY() - boardY) / cellSize);
            fromFile = (e.getX() - boardX) / cellSize;
                // we need to check if the correct coloured 
                // piece was selected AND that the piece has a move left...
                // so we have to compute a piece's "available" moves 
                // as well and have them stored.
                // For now we wont worry about "valid" move piece
                // and notify the player about an invalid piece AFTER
                // the move is made
            if (fromRank >= 0 && fromRank < 8 &&
                fromFile >= 0 && fromFile < 8 &&
                colorAt[fromRank][fromRank] == currColor)
            {
                status = PIECE_SELECTED;
            }
        } else if (status == PIECE_SELECTED)
        {
            toRank = 7 - ((e.getY() - boardY) / cellSize);
            toFile = (e.getX() - boardX) / cellSize;
                // we need to check if the correct coloured 
                // piece was selected AND that the piece has a move left...
                // so we have to compute a piece's "available" moves 
                // as well and have them stored.
                // For now we wont worry about "valid" move piece
                // and notify the player about an invalid piece AFTER
                // the move is made
            if (toRank >= 0 && toRank < 8 &&
                toFile >= 0 && toFile < 8)
            {
                    // do nothing till the mouse is released
            } else
            {
                status = NO_STATUS;
            }
        }
        paint(getGraphics());
    }

        /**
         * Mouse Released Event handler.
         */
    public void mouseReleased(MouseEvent e)
    {
        int rank = 7 - ((e.getY() - boardY) / cellSize);
        int file = (e.getX() - boardX) / cellSize;

        if (status == PIECE_SELECTED)
        {
            if (rank == fromRank && file == fromFile)
            {
                if (toRank >= 0) status = DEST_SELECTED;
            } else
            {
                toRank = toFile = fromRank = fromFile = -1;
                status = NO_STATUS;
            }
        } else if (status == DEST_SELECTED)
        {
            if (rank == toRank && file == toFile)
            {
                    // check if the move is valid and
                    // make the move
                int result = makeMove(fromRank, fromFile,
                                      toRank, toFile, currColor);
                if (result != INVALID_MOVE)
                {
                        // and also toggle teh colours...
                    currColor = General.oppositeColor[currColor];
                }
            }
            toRank = toFile = fromRank = fromFile = -1;
            status = NO_STATUS;
        }
        paint(getGraphics());
    }

        /**
         * Mouse Clicked Event handler.
         */
    public void mouseClicked(MouseEvent e)
    {
    }

        /**
         * Mouse Entered Event handler.
         */
    public void mouseEntered(MouseEvent e)
    {
    }

        /**
         * Mouse Exited Event handler.
         */
    public void mouseExited(MouseEvent e)
    {
    }

        /**
         * Mouse Moved Event handler.
         */
    public void mouseMoved(MouseEvent e)
    {
        currentRank = 7 - ((e.getY() - boardY) / cellSize);
        currentFile = (e.getX() - boardX) / cellSize;
    }

        /**
         * Mouse Dragged Event handler.
         */
    public void mouseDragged(MouseEvent e)
    {
        currentRank = 7 - ((e.getY() - boardY) / cellSize);
        currentFile = (e.getX() - boardX) / cellSize;
    }

        /**
         * Sets the white cell color.
         */
    public void setWhiteCellColor(Color col)
    {
        if (pieceDrawer != null) pieceDrawer.setWhiteCellColor(col);
        paint(getGraphics());
    }

        /**
         * Sets the Black cell color.
         */
    public void setBlackCellColor(Color col)
    {
        if (pieceDrawer != null) pieceDrawer.setBlackCellColor(col);
        paint(getGraphics());
    }

        /**
         * Sets the white piece color.
         */
    public void setWhitePieceColor(Color col)
    {
        if (pieceDrawer != null) pieceDrawer.setWhitePieceColor(col);
        paint(getGraphics());
    }

        /**
         * Sets the Black piece color.
         */
    public void setBlackPieceColor(Color col)
    {
        if (pieceDrawer != null) pieceDrawer.setBlackPieceColor(col);
        paint(getGraphics());
    }

        /**
         * Draws the pieceAt onto a graphics context.
         */
    public void drawBoard(Graphics g, int x, int y)
    {
    }

        /**
         * Returns true if a given cell is under check.
         */
    public boolean isCellInCheck(int rank, int file)
    {
    }

        /**
         * Tells if a given move is valid or not.
         * The move is represented in the direct-algebraic notation
         * where the current and next position of a piece are specified.
         * Basically here is where we go through all the cases and
         * see if the move is valid (ie by checking for attacks, checks,
         * and etc).
         */
    public int makeMove(int r1, int c1, int r2, int c2, int color)
    {
            // the direction that would be "forward"
            // given the colour.
        int forward = color == General.BLACK ? -1 : 1;

            // check if the coordinates are valid
            // and whether the r1,c1 is differnet from r2,c2
        if ((r1 == r2 && c1 == c2) ||
            !coordinatesValid(r1, c1) ||
            !coordinatesValid(r2, c2) ||
            pieceAt[r1][c1] == EMPTY ||
            colorAt[r1][c1] == colorAt[r2][c2])
        {
            return INVALID_MOVE;
        }

            // colors to reset to if this move results in a check.
        int backoutPiece1 = pieceAt[r1][c1];
        int backoutPiece2 = pieceAt[r2][c2];
        int backoutColor1 = colorAt[r1][c1];
        int backoutColor2 = colorAt[r2][c2];


        if (pieceAt[r1][c1] == Piece.PAWN)
        {
        } else if (pieceAt[r1][c1] == Piece.KING)
        {
        } else if (pieceAt[r1][c1] == Piece.QUEEN)
        {
        } else if (pieceAt[r1][c1] == Piece.KNIGHT)
        {
            // dy * dx must be equal + or - 2 then its 
            // a valid jump...
        } else if (pieceAt[r1][c1] == Piece.BISHOP)
        {
        } else if (pieceAt[r1][c1] == Piece.ROOK)
        {
            // if the piece is a rook, then the following must hold:
            // 1    -   r1 must equal r2 OR c1 must equal c2
            // 2    -   All squares between r1,c1 and r2,c2 must be empty
            int dx = (c2 - c1) / Math.abs(c2 - c1);
            int dy = (r2 - r1) / Math.abs(r2 - r1);

            if (dx == 0 || dy == 0)
            {
                    // now check for the second rule...
                return VALID_MOVE;
            } else
            {
                return INVALID_MOVE;
            }
        }

            // first see what kind of piece occupies the 
            // square r1, c1
            //
            // then see if the move can be made (for now forget
            // possibility of check).
            //
            // Copy the "old" version and see if the new position results
            // in a check of the current player.
            // if so then return false.
        return INVALID_MOVE;
    }

        /**
         * Tells if given coordinates are valid or not.
         */
    protected static boolean coordinatesValid(int r, int c)
    {
        return (r >= 0 && r < 8 && c >= 0 && c < 8);
    }
}
