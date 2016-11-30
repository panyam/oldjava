
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * A terminal node which has only a string
 */
public class TerminalNode extends ParseTreeNode
{
        /**
         * The string that we are holding in this node.
         */
    protected StringBuffer valueBuffer = null;

        /**
         * Constructor.
         */
    public TerminalNode(int id, NonTerminalNode parent, String value)
    {
        super(id, parent);
        valueBuffer = new StringBuffer(value == null ? "" : value);
    } 

        /**
         * Break this token at the give nposition and
         * reutrn a new token.
         */
    public TerminalNode breakAt(int pos)
    {
        String v = valueBuffer.toString();
        valueBuffer = new StringBuffer(v.substring(0, pos));

            // also append the node after this node
        TerminalNode node = new TerminalNode(symbolID, parent,
                                             v.substring(pos));
        if (parent != null) parent.addChild(node, this);
        return node;
    }

        /**
         * Appends the next node with this one.
         */
    public void joinWithNext()
    {
        if (next != null && next instanceof TerminalNode)
        {
            valueBuffer.append(((TerminalNode)next).valueBuffer);
            if (parent != null) parent.removeChild(next);
        }
    }

        /**
         * Appends this value to the previous node.
         */
    public void joinWithPrevious()
    {
        if (prev != null && prev instanceof TerminalNode)
        {
            ((TerminalNode)prev).valueBuffer.append(valueBuffer);
            if (parent != null) parent.removeChild(this);
        }
    }
}
