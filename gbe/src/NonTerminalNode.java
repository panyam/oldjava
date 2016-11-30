import java.awt.*;
import java.awt.event.*;

/**
 * A non-terminal node which can have other child nodes.
 */
public class NonTerminalNode extends ParseTreeNode
{
        /**
         * A Linked list of nodes.
         */
    protected ParseTreeNode head = null, tail = null;
    protected int nKids = 0;

        /**
         * Constructor.
         */
    public NonTerminalNode(int id, NonTerminalNode parent)
    {
        super(id, parent);
    }

        /**
         * Adds a new child after a given position.
         * If position is null then add it at the end of the list.
         *
         * Returns the newly added node.
         */
    public ParseTreeNode addChild(ParseTreeNode node,
                                  ParseTreeNode position)
    {
        if (position == null) position = tail;
        node.next = node.prev = null;
        node.parent = this;

        if (position == null)
        {
            head = tail = node;
        } else
        {
            ParseTreeNode next = position.next;

            position.next = node;
            node.prev = position;

            node.next = next;
            if (next != null) next.prev = node;

            if (tail.next != null) tail = tail.next;
        }
        nKids ++;
        return node;
    }

        /**
         * Remove the node pointed by "node".
         * Precondition: node.parent MUST be this AND node must NOT equal
         * null.
         */
    public ParseTreeNode removeChild(ParseTreeNode node)
    {
        ParseTreeNode prev = node.prev;
        ParseTreeNode next = node.next;
        if (prev != null) prev.next = next;
        if (next != null) next.prev = prev;
        nKids--;
        return node;
    }

        /**
         * Adds a new child to the end of the list.
         */
    public ParseTreeNode insertChild(ParseTreeNode node,
                                     ParseTreeNode position)
    {
        if (position == null) position = head;
        node.next = node.prev = null;
        node.parent = this;

        if (position == null)
        {
            head = tail = node;
        } else
        {
            ParseTreeNode prev = position.prev;

            node.prev = prev;
            if (prev != null) prev.next = node;

            node.next = position;
            position.prev = node;

            if (head.prev != null) head = head.next;
        }
        nKids ++;
        return node;
    }
}
