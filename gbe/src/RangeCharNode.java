
/**
 * A node that has only characters and matches characters.
 */
public class RangeCharNode extends CharClassNode
{
    public final static char NEGATION_CHAR = '^';

        /**
         * The characters to directly match.
         * Basically this list should be even in length.
         */
    public int loChar, hiChar;

        /**
         * Constructor.
         */
    public RangeCharNode(int loChar, int hiChar)
    {
        this.loChar = loChar;
        this.hiChar = hiChar;
    }

        /**
         * Gets the character.
         */
    public int getChar()
    {
        return loChar;
    }

        /**
         * Print this node.
         */
    public void print()
    {
        System.out.print("[" + (invert ? "^ " : " ") + 
                            (char)loChar + "-" + (char)hiChar + "]");
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean charMatches(char inchar)
    {
        return (inchar >= loChar && inchar <= hiChar) == invert;
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean matchesRange(int lo, int hi)
    {
        return (lo == loChar && hi == hiChar) != invert;
    }
}
