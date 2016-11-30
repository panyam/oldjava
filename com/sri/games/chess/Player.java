
import java.io.*;
import java.awt.*;
import java.util.*;

/**
 * A class for a Player.
 */
public abstract class Player
{
        /**
         * Tells if this player is playing black or white.
         */
    public boolean isWhite = true;

        /**
         * Default constructor, shouldnt be called.
         */
    protected Player()
    {
    }

        /**
         * Constructor.
         */
    public Player(boolean isWhite)
    {
        this.isWhite = isWhite;
    }

        /**
         * Make the next move.
         */
    public synchronized ChessMove nextMove()
    {
        return null;
    }
}
