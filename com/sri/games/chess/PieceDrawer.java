
import java.io.*;
import java.awt.*;
import java.util.*;

/**
 * A class that takes care of drawing a piece.
 */
public class PieceDrawer
{
    protected FontMetrics fontMetrics = null;
        /**
         * The cell dimensions.
         */
    protected int cellWidth, cellHeight;

        /**
         * Colors for white and black squares.
         */
    public Color cellColors[] = new Color[] {
                                        new Color(200, 195, 101),
                                        new Color(119, 162, 109)
                                   };

        /**
         * Colour of the pieces.
         */
    public Color pieceColors[] = new Color[] {
                                        new Color(255,255,204),
                                        new Color(32, 32, 32)
                                    };

        /**
         * Sets the white cell color.
         */
    public void setWhiteCellColor(Color col)
    {
        this.cellColors[General.WHITE] = col;
    }

        /**
         * Sets the Black cell color.
         */
    public void setBlackCellColor(Color col)
    {
        this.cellColors[General.BLACK] = col;
    }

        /**
         * Sets the white piece color.
         */
    public void setWhitePieceColor(Color col)
    {
        this.pieceColors[General.WHITE] = col;
    }

        /**
         * Sets the Black piece color.
         */
    public void setBlackPieceColor(Color col)
    {
        this.pieceColors[General.BLACK] = col;
    }

        /**
         * Sets the size of a cell where the piece is drawn.
         */
    public void setCellSize(int w, int h, Graphics gr)
    {
        cellWidth = w;
        cellHeight = h;
        fontMetrics = gr.getFontMetrics();
            // do more here...
    }

        /**
         * Draws a piece.
         *
         * @param   g   The graphics object onto which the piece is to be
         *              drawn.
         * @param   x   The x coordinate of the piece.
         * @param   y   The y coordinate of the piece.
         * @param   piece 
         *              The piece to be drawn (ie rook, pawn, queen etc)
         * @param   col
         *              The color of the piece (General.WHITE or
         *              General.BLACK).
         * @param   inverted
         *              Whether the piece should be drawn upside down
         *              or not.
         */
    public void drawPiece(Graphics g, int x, int y,
                          int piece, int col, boolean inverted)
    {
            // the piece color
        Color pColor = pieceColors[col];
            // the cell color
        Color cColor = cellColors[col];

        if (fontMetrics == null) return ;

        g.setColor(pColor);

        int width = fontMetrics.stringWidth("W");
        int height = fontMetrics.getAscent();

        if (piece == Piece.ROOK)
        {
            g.drawString("R", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        } else if (piece == Piece.KNIGHT)
        {
            g.drawString("N", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        } else if (piece == Piece.BISHOP)
        {
            g.drawString("B", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        } else if (piece == Piece.QUEEN)
        {
            g.drawString("Q", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        } else if (piece == Piece.KING)
        {
            g.drawString("K", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        } else if (piece == Piece.PAWN)
        {
            g.drawString("P", (x + (cellWidth - width) / 2),
                              (y + (cellHeight + height) / 2));
        }
    }
}
