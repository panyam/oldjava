import java.awt.*;
import java.awt.event.*;

/**
 * An element in the parser stack.
 */
public abstract class ParseTreeNode
{
        /**
         * The parent node.
         */
    public NonTerminalNode parent;

        /**
         * The previous and next ParseTreeNodes in the list.
         */
    public ParseTreeNode prev, next;

        /**
         * The id of the symbol that is currently in the node.
         */
    public int symbolID;

        /**
         * Constructor.
         */
    public ParseTreeNode(int id, NonTerminalNode parent)
    {
        this.symbolID = id;
        this.parent = parent;
    }
}
