


/**
 * Denotes an empty string.  Not the same as an empty set.
 */
public final class OneExpNode extends LeafNode
{
        /**
         * Constructor
         */
    public OneExpNode()
    {
        super(1);
        hashValue = 1;
    }

        /**
         * Prints this node.
         */
    public void print()
    {
        System.out.print("_1_");
    }
}
