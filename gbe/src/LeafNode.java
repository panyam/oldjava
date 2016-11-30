

/**
 * Defines nodes that cannot have any more children.
 * These are the nodes that are at the bottom most of the expression tree
 * and will have a position assigned to them.  The position indicates the
 * order in which they are visited by a Depth First Traversal.
 */
public abstract class LeafNode extends ExpNode
{
        /**
         * Used for calculating the "position"
         */
    public int position;

        /**
         * Default constructor that cannot be called.
         */
    protected LeafNode()
    {
    }

        /**
         * Constructor.
         */
    public LeafNode(int op)
    {
        super(op);
    }
}
