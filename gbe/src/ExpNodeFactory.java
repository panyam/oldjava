
import java.util.*;

/**
 * A factory for creating expression nodes given the regex string. 
 * Makes an expression out of a given expresion string.
 * The expression syntax is this:
 *      [a-b]        -   All characters between a and b.
 *      [^a-b]       -   All characters not within a and b.
 *      .            -   Match any character.
 *      \xx          -   Escape sequency - Can be unicode as well.
 *      (A)          -   A group.
 *      A{m,n}       -   Between m and n occurances of A.
 *                       if A is unspecified then atleast 
 *                       m occurances
 *      A*           -   Same as A{0, }
 *      A+           -   Same as A{1, }
 *      A?           -   Same as A{0,1}
 *      A|B          -   match A or B
 *      A#B          -   Intersection of expression A 
 *                       and expression B
 *      AB           -   A followed by B.
 *      A-B          -   Difference.  Get the mathches in A if 
 *                       they dont match B
 *      A^B          -   Product of expressions A and B.
 *
 *      Precedence in this is paramount as this is what dictates what
 *      happens when.  Also items with equal precedence will be grouped
 *      to the right.
 *
 *      Quantifiers (+, *, ? and { } ops) have higher precedence over
 *      binary operators.  More importantly, they only apply to one
 *      element to their left, unless that element is enclosed in
 *      paranthesis.
 *      
 *      Binary operators are grouped to the right.  Eg:
 *      a | b ^ c & d  = a | (b ^ (c & d))
 *
 *      This means, input is to be read left to right while LL(1)
 *      parsing is done all the time.
 *
 *      So always keep track of curr and next chars and determine
 *      whether to consume the character or recurse down to the right
 *      part of the tree.
 *
 *      Or is bottom up parsing required?  How?
 *      Keep reading symbols and putting them onto a stack.  If there is
 *      a rule then reduce it.  With shift-reduce conflicts always shift
 *      first.  In this grammar will there be shift-reduce conflicts?
 */
public class ExpNodeFactory
{
        /**
         * A few items that can be used on the parse stack.
         */
    protected final static Object zeroExpNode = new Integer(0);
    protected final static Object oneExpNode = new Integer(1);
    protected final static Object openParanNode = new Integer('(');
    protected final static Object closeParanNode = new Integer(')');
    protected final static Object unionOpNode
                        = new Integer(ExpNodeType.UNION_NODE);
    protected final static Object intersectOpNode
                        = new Integer(ExpNodeType.INTERSECTION_NODE);
    protected final static Object diffOpNode
                        = new Integer(ExpNodeType.DIFF_NODE);
    protected final static Object productOpNode
                        = new Integer(ExpNodeType.PRODUCT_NODE);
    protected final static Object notOpNode
                        = new Integer(ExpNodeType.NOT_NODE);
    protected final static Object uptoOpNode
                        = new Integer(ExpNodeType.UPTO_NODE);

        /**
         * Given a string, returns the appropriate expnode.
         */
    public ExpNode createExpression(String expr)
        throws RegExException
    {
        return createExpression(expr.toCharArray(), 0, expr.length());
    }

        /**
         * Given an regex as a char array returns the expression tree that
         * matches this regex.
         */
    public ExpNode createExpression(char expr[], int start, int end)
        throws RegExException
    {
        Stack expStack = new Stack();

        int ptr = start;
        int currCh, nextCh;
        ExpNode left, right, third, newNode, head2, newHead;

            // Algorithm is simple:
            // this so would be easy.  Keep reading characters
            // and create "ExpNodes out of them if they can 
            // be reduced

        ExpNode prev = null, prevprev = null;

        for (ptr = start;ptr < end;ptr++)
        {
            currCh = expr[ptr];
            nextCh = (ptr + 1) < end ? expr[ptr + 1] : -1;

            if (currCh == ')')
            {
                    // if this is teh first char is this an error?
                    // or should the char be matches as is...
                if (expStack.size() < 2)
                {
                    if (currCh == ')')
                        throw new
                            RegExException(RegExException.UNMATCHED_PARANTHESIS);
                }

                if (!reduceStack(expStack, openParanNode))
                {
                    throw new RegExException
                        (RegExException.UNMATCHED_PARANTHESIS);
                }
            //} else if (currCh == '$')
            //{
            } else if (currCh == ExpNodeType.NOT_NODE)
            {
                    // now prefix unary operators. so basically 
                    // plonk it on the stack
                expStack.push(notOpNode);
            } else if (currCh == ExpNodeType.UPTO_NODE)
            {
                    // now prefix unary operators. so basically 
                    // plonk it on the stack
                expStack.push(uptoOpNode);
            } else if (currCh == ExpNodeType.STAR_NODE ||
                       currCh == ExpNodeType.PLUS_NODE ||
                       currCh == ExpNodeType.QUESTION_NODE)
            {
                    // now postfix unary operators.
                    // then take the head item of the stack
                    // and replace it with a UnaryNode
                if (expStack.isEmpty())
                {
                        // error - a * cannot be the first item
                        // unless preceded by a "\"
                    throw new RegExException(RegExException.UNARY_AT_START);
                }

                    // pop the item of the stack, and combine it with
                    // an unary operator and pop it back in the stack
                expStack.push(new UnaryExpNode(currCh,
                                               (ExpNode)expStack.pop()));

            } else if (currCh == ExpNodeType.UNION_NODE)
            {
                expStack.push(unionOpNode);
            } else if (currCh == ExpNodeType.INTERSECTION_NODE)
            {
                expStack.push(intersectOpNode);
            } else if (currCh == ExpNodeType.PRODUCT_NODE)
            {
                expStack.push(productOpNode);
            } else if (currCh == ExpNodeType.DIFF_NODE)
            {
                expStack.push(diffOpNode);
            } else if (currCh == '[')
            {
                    // beginning of "character classes"
                ptr++;
                int sqStart = ptr;
                System.out.println("ptr, sqs, First char = " + ptr + ", " + sqStart + ", " + expr[ptr]);
                while (ptr < end)
                {
                    if (expr[ptr] == ']' && expr[ptr - 1] != '\\') break;
                    ptr++;
                }

                if (ptr == end)
                {
                    throw new RegExException(RegExException.UNTERMINATED_SQ_BRACKET);
                }
                //MultiCharNode chNode = new MultiCharNode(expr, sqStart, ptr);
                ExpNode ccNode = RegExUtils.extractRangeChars
                                            (expr, sqStart, ptr);

                    // now push this on the stack!!!
                expStack.push(ccNode);
            } else
            {
                // or we have single chars so go for it...
                long chVal = RegExUtils.extractChar(expr, ptr, end);
                int nChars = (int)((chVal >> 32) & 0xffffffff);
                int currChar = (int)(chVal & 0xffffffff);
                ptr += nChars;
                ptr --; // rewinde back so the loop 
                        // ending ptr++ can have effect
                if (currChar == '(')
                {
                    expStack.push(openParanNode);
                } else
                {
                    expStack.push(new SingleCharNode(currChar));
                }
            }

            reducePrefixUnaryNodes(expStack);
                // see if any reduction is possible
            //head = reduceStack(head);
        }
        reduceStack(expStack, null);

        System.out.println("Stack size: " + expStack.size());
        return (ExpNode)expStack.top();
    }

        /**
         * Keep reducing all unary prefix expressions on the stack.
         */
    protected boolean reducePrefixUnaryNodes(Stack stack)
    {
        while (stack.size() > 1)
        {
            Object item2 = stack.head.next.item;
            if ((item2 == notOpNode || item2 == uptoOpNode) &&
                  ! (stack.top() instanceof Integer))
            {
                ExpNode top = (ExpNode)stack.pop();
                stack.pop();        // pop off the unary operator.
                stack.push(new UnaryExpNode
                            (((Integer)item2).intValue(), top));
            } else
            {
                return true;
            }
        }
        return true;
    }

        /**
         * Right groups the nodes in a stack by repeatedly "concatenating"
         * expressions on the top of stack.
         *
         * also if there are any binary operators those get reduced too
         * before the concatenation...
         */
    protected boolean reduceStack(Stack stack, Object matchNode)
    {
        //while ((currCh == ')' && head.next != openParanNode) || 
        //       (nextCh <= 0 && head.next != null))
        ExpNode newNode = null;
        boolean finished = true;

        while (stack.size() > 1)
        {
                // means we have an unmatched bracket or error
            if (stack.head.next == null) return false;

            Object top = (stack.head.next.item);

            if (top == unionOpNode ||
                top == intersectOpNode ||
                top == diffOpNode ||
                top == productOpNode)
            {
                ExpNode right = (ExpNode)stack.pop();
                Integer op = (Integer)stack.pop();
                ExpNode left = (ExpNode)stack.pop();

                stack.push(new BinaryExpNode(op.intValue(), left, right));
            } else
            {
                    // then do a right grouping on the stack
                ExpNode right = (ExpNode)stack.pop();
                Object left = stack.pop();

                    // concatenate only if its not the match node
                    // if match node than replace (left, right) on the
                    // stackwith jsut right
                if (left == matchNode)
                {
                    //finished = true;
                    stack.push(right);
                    return true;
                } else
                {
                    stack.push(new BinaryExpNode
                                    (ExpNodeType.CONCAT_NODE, (ExpNode)left, right));
                }
            }
        }
        return true;
    }
}
