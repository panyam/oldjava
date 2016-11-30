import java.io.*;
import java.awt.*;
import java.util.*;

/**
 * A class for representing a chess game.
 */
public class Game implements Runnable
{
    protected Thread ourThread = null;
    protected boolean threadRunning = false;
    protected boolean threadStopped = false;

        /**
         * List of all the moves that have occured so far.
         */
    protected Vector moveList = new Vector();

    protected Player players[] = new Player[2];

        /**
         * The chess board on which the game is being played.
         */
    protected Board chessBoard = null;

    int currentPlayer = General.WHITE;

        /**
         * Constructor.
         */
    public Game(Player white, Player black)
    {
        chessBoard = new Board();
        players[0] = white;
        players[1] = black;
        resetGame();
    }

        /**
         * Starts the game.
         */
    public synchronized void startGame()
    {
        if (threadRunning) return ;

        threadRunning = true;
        threadStopped = false;
        ourThread = null;
        ourThread = new Thread(this);
        ourThread.start();
    }

        /**
         * Stops the game and resets.
         */
    public synchronized void stopGame()
    {
        if (!threadRunning) return ;
        threadStopped = true;
        notifyAll();
        while (threadRunning)
        {
            try
            {
                wait();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        ourThread = null;
        threadStopped = false;
        resetGame();
    }

        /**
         * Resets the game.
         */
    public synchronized void resetGame()
    {
        if (threadRunning) stopGame();
        threadRunning = threadStopped = false;
        ourThread = null;
        if (chessBoard != null) chessBoard.reset();

        moveList.removeAllElements();

        players[General.WHITE] = players[General.BLACK] = null;
        currentPlayer = General.WHITE;
    }

        /**
         * The game is started from what ever the current position is.
         */
    public void run()
    {
        synchronized (this)
        {
            if (chessBoard == null ||
                players[General.WHITE] == null ||
                players[General.BLACK] == null) return ;

            while (threadRunning && !threadStopped)
            {
                    // make the current player make the move...
                    // when the move is made, the following can
                    // be updated:
                    //
                players[currentPlayer].nextMove();

                    // next player plays...
                currentPlayer = (currentPlayer + 1) % 2;
            }
            threadRunning = threadStopped = false;
            notifyAll();
        }
    }

        /**
         * Return the current board.
         * The board can be manipulated and so on.
         */
    public Board getBoard()
    {
        return chessBoard;
    }
}
