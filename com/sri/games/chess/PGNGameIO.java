
import java.io.*;
import java.util.*;

/**
 * An interface for reading games.
 */
public class PGNGameIO implements GameIO
{
        /**
         * The event where the game is taking place.
         */
    protected final static int EVENT_TAG = 0;
        
        /**
         * The site where the game is taking place.
         */
    protected final static int SITE_TAG = 1;
        
        /**
         * The date when the game is taking place.
         */
    protected final static int DATE_TAG = 2;

        /**
         * The playing round ordinal of the game.
         */
    protected final static int ROUND_TAG = 3;

        /**
         * Player of the white pieces.
         */
    protected final static int WHITE_TAG = 4;

        /**
         * Player of the black pieces.
         */
    protected final static int BLACK_TAG = 5;

        /**
         * Result of the game.
         */
    protected final static int RESULT_TAG = 6;

        /**
         * Names of the basic tags.
         */
    protected final static String tagNames[] = {
        "Event", "Site", "Date", "Round", "White", "Black", "Result"
    };

        /**
         * Value of the basic tags.
         */
    protected String basicTags[] = new String[7];

        /**
         * The set of tags and their values.
         */
    protected Hashtable tags = new Hashtable();

        /**
         * Reads an input stream thats in the PGN format.
         */
    public Game readGame(InputStream iStream) throws Exception
    {
        return null;
    }

        /**
         * Writes a game to an export type PGN format.
         */
    public void writeGame(Game game, OutputStream iStream) throws Exception
    {

    }

        /**
         * Set the value of a tag.
         */
    public void setTagValue(String tag, String value)
    {
        for (int i = 0;i < tagNames.length;i++)
        {
            if (tag.equalsIgnoreCase(tagNames[i])) 
            {
                basicTags[i] = value;
                return ;
            }
        }

            // if tag already exists then return;
        if (tags.contains(tag)) return ;

        tags.put(tag, value);
    }

        /**
         * Get the value of a given tag.
         */
    public String getTagValue(String tag)
    {
        for (int i = 0;i < tagNames.length;i++)
        {
            if (tag.equalsIgnoreCase(tagNames[i])) 
            {
                return basicTags[i];
            }
        }

        return (String)tags.get(tag);
    }
}
