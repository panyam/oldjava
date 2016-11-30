
/**
 * A node that has only characters and matches characters.
 */
public abstract class CharClassNode extends LeafNode
{
    public final static char NEGATION_CHAR = '^';

        /**
         * Tells if the "negative" of this match is to be "accepted".
         */
    protected boolean invert = false;

        /**
         * Tells if a char matches this class of characters.
         */
    public abstract boolean charMatches(char inchar);

        /**
         * Tells if a char matches this class of characters.
         */
    public abstract boolean matchesRange(int lo, int hi);
}
