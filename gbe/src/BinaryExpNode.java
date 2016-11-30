
public class BinaryExpNode extends ExpNode
{

        /**
         * Left and right expressions.
         */
    public ExpNode left, right;

        /**
         * Constructor.
         */
    public BinaryExpNode(int op, ExpNode l, ExpNode r)
    {
        super(op);
        this.left = l;
        this.right = r;

        int div = 1;
        /*switch (op)
        {
            case UNION:  hashValue = 0x120;     div = 0x10; break;
            case INTERSECTION:  hashValue = 0x130;      div = 0x10; break;
            case CONCAT:  hashValue = 0x140;      div = 0x10; break;
            case DIFF:  hashValue = 0x150;      div = 4; break;
            case PRODUCT:  hashValue = 0x160;      div = 4; break;
        }*/

        hashValue += ((left.hashCode() ^ right.hashCode()) / div);
    }

        /**
         * Print the expression.
         */
    public void print()
    {
        System.out.print("( ");
        left.print();
        System.out.print(" ) ");
        System.out.print((char)expType);
        System.out.print(" ( ");
        right.print();
        System.out.print(" ) ");
    }
}
