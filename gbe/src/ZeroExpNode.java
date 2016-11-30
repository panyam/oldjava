

/**
 * Notes an empty set expression.
 */
public final class ZeroExpNode extends LeafNode
{
        /**
         * Constructor
         */
    public ZeroExpNode()
    {
        super(0);
        hashValue = 0;
    }

        /**
         * Prints this.
         */
    public void print()
    {
        System.out.print("_0_");
    }
}
