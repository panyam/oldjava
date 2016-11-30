
/**
 * A node that has only one char to match.
 */
public class SingleCharNode extends CharClassNode
{
    int matchingChar;

        /**
         * Constructor.
         */
    public SingleCharNode(int ch)
    {
        matchingChar = ch;
    }

        /**
         * Gets the character.
         */
    public int getChar()
    {
        return matchingChar;
    }

        /**
         * Print this node.
         */
    public void print()
    {
        System.out.print((char)matchingChar);
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean charMatches(char inchar)
    {
        return (inchar == matchingChar) != invert;
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean matchesRange(int lo, int hi)
    {
        return (lo >= matchingChar && hi <= matchingChar) != invert;
    }
}
