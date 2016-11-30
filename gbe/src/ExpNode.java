import java.util.*;

/**
 * The super class of expression nodes in the regular Expression tree.
 */
public abstract class ExpNode
{
        /**
         * Tells if the node is nullable.
         */
    public boolean nullable = false;

        /**
         * The id of the exp node.
         * Basically used to provide an index into the array of exp nodes
         * for a particular expression.
         */
    public int nodeID;

        /**
         * The state that denotes for this expression.
         */
    public int state;

    public int hashValue = 0;

        /**
         * The type of expressions.
         */
    public int expType;

        /**
         * Tells if this expression is on stack or not.
         */
    public boolean onStack = false;

        /**
         * Name of the expression.
         * If null it means it has no name and just is to be matched.
         */
    public String expName = null;

        /**
         * Default constructor that should/can not be called.
         */
    protected ExpNode()
    {
    }

        /**
         * Constructor.
         */
    public ExpNode(int type)
    {
        this.expType = type;
        onStack = false;
        state = 0;
        hashValue = 0;
    }

    public abstract void print();

        /**
         * The hash value.
         */
    public int hashCode()
    {
        return hashValue;
    }
}
