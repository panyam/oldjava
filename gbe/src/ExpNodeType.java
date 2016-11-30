

public class ExpNodeType
{
        /**
         * Anything but a given expression.
         */
    public final static int NOT_NODE = '!';

        /**
         * Anything upto the given expression.
         */
    public final static int UPTO_NODE = '~';

        /**
         * One or more instances.
         */
    public final static int PLUS_NODE = '+';

        /**
         * zero or more instances.
         */
    public final static int STAR_NODE = '*';

        /**
         * optional (zero or one) instance.
         */
    public final static int QUESTION_NODE = '?';

        /**
         * Difference = A - B
         */
    public final static int DIFF_NODE = '-';

        /**
         * Concatenation of A and B
         */
    public final static int CONCAT_NODE = '#';

        /**
         * Alteration  Union of A and B.
         */
    public final static int UNION_NODE = '|';

        /**
         * Intersection of A and B.
         */
    public final static int INTERSECTION_NODE = '&';

        /**
         * Interleave product = A ^ B
         */
    public final static byte PRODUCT_NODE = '^';
}
