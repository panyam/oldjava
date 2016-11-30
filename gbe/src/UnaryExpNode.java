
/**
 * Unary expressions.
 */
public class UnaryExpNode extends ExpNode
{

    public ExpNode child;

        /**
         * Constructor.
         */
    public UnaryExpNode(int op, ExpNode child)
    {
        super(op);
        this.child = child;

        int div = 1;
        /*switch (op)
        {
            case NOT: hashValue = 0x00; div = 8; break;
            case UPTO: hashValue = 0x20; div = 8; break;
            case PLUS: hashValue = 0x40; div = 0x10; break;
            case STAR: hashValue = 0x60; div = 0x10; break;
            case QUESTION: hashValue = 0x80; div = 0x10; break;
        }*/

        hashValue += (child.hashCode() / div) ;
    }

        /**
         * Print the expression.
         */
    public void print()
    {
        System.out.print(" ( ");
        child.print();
        System.out.print(" ) ");
        System.out.print((char)expType + " ");
    }
}
