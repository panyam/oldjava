

/**
 * A node that has only characters and matches characters.
 */
public class MultiCharNode extends CharClassNode
{
        // used to match beginning and end of line characters
    public final static int END_OF_LINE = 0;
    public final static int START_OF_LINE = 1;

    public final static char NEGATION_CHAR = '^';

        /**
         * Characters that match directly.
         */
    protected int directMatches[];

        /**
         * The characters to directly match.
         * Basically this list should be even in length.
         */
    protected int rangeMatches[];

        /**
         * Tells if the "negative" of this match is to be "accepted".
         */
    protected boolean invert = false;

        /**
         * Constructor.
         * Given a char range - '['[^](char|char'-'char)*']'
         * makes this class so that it will accept only certain characters.
         * Char is anything that is not one of :
         *      | ( ) { } [ ] < > \ . * + ? ^ $ / . " ~ ! *
         * This can include escape sequences:
         *          \n \r \t \f \b 
         *          a \ x followed by two hexadecimal digits [a-fA-F0-9]
         *                      (denoting a standard ASCII escape sequence),
         *          a \ u followed by four hexadecimal digits [a-fA-F0-9]
         *                      (denoting an unicode escape sequence),
         *          a backslash followed by a three digit octal number from
         *              000 to 377 (denoting a standard ASCII escape sequence),
         *              or
         *          a backslash followed by any other unicode character
         *          that stands for this character.
         *
         * Assumption is that the whole [...] has already been stripped of
         * the opening and closing square braces.
         */
    public MultiCharNode(char array[], int start, int end)
        throws RegExException
    {
        boolean foundMinus = false;
        int currCh, nextCh;
        int startChar = -1;
        int currChar = -1;
        int digit;

        int dTemp[] = new int[128];      // array for temporarily holding matched chars
        int dLen = 0;       // this will increment by 1 each time

        int rTemp[] = new int[256];      // array for temporariy holding range matches.
        int rLen = 0;       // this will increment by 2 each time

        int ptr = start;

        if (array[ptr] == NEGATION_CHAR)
        {
            invert = true;
            ptr++;
        }

        for (;ptr < end;)
        {
            currCh = array[ptr];
            nextCh = (ptr + 1 == end) ? -1 : array[ptr + 1];


                // if its an escape sequence
            if (currCh == '-')   // then we are doing a range here...
            {
                foundMinus = true;
                startChar = currChar;
                ptr++;
            } else 
            {
                long chVal = RegExUtils.extractChar(array, ptr, end);
                int nChars = (int)((chVal >> 32) & 0xffffffff);
                currChar = (int)(chVal & 0xffffffff);
                ptr += nChars;

                    // now if minus has already been foudn it means
                    // it was right before currChar  so set a range
                if (foundMinus)
                {
                    foundMinus = false;
                    // add a range of startChar to currChar as a range!!!
                    //addRangeMatch(startChar, currChar);
                    if (rLen >= rTemp.length)
                    {
                        rangeMatches = appendList(rangeMatches, rTemp, rTemp.length);
                        rLen = 0;
                    }
                    rTemp[rLen++] = startChar;
                    rTemp[rLen++] = currChar;
                } else
                {
                        // now insert the startChar at the right spot in the 
                        // direct match char list.
                    if (dLen >=  dTemp.length)
                    {
                        directMatches = appendList(directMatches, dTemp, dTemp.length);
                        dLen = 0;
                    }

                        // now check if the next char is a '-'.  if it is
                        // then dont store it in direct match list
                    if (ptr == end || array[ptr] != '-')
                    {
                        dTemp[dLen++] = currChar;
                        startChar = -1;
                    }
                }
                startChar = -1;
            }
        }
        if (dLen > 0) directMatches = appendList(directMatches, dTemp, dLen);
        if (rLen > 0) rangeMatches = appendList(rangeMatches, rTemp, rLen);
    }

        /**
         * Append the existing temp list of range matches to the
         * current list.
         */
    protected int []appendList(int matchList[], int list[], int nItems)
    {
        if (matchList == null)
        {
            matchList = new int[nItems];
            System.arraycopy(list, 0, matchList, 0, nItems);
        } else
        {
            int r2[] = matchList;
            int oldLen = matchList.length;
            matchList = new int[oldLen + nItems];
            System.arraycopy(r2, 0, matchList, 0, oldLen);
                // now copy he rest of it here as well...
            System.arraycopy(list, 0, matchList, oldLen, nItems);
        }
        return matchList;
    }

        /**
         * Constructor.
         */
    public MultiCharNode(int ch)
    {
        rangeMatches = null;
        directMatches = new int[] { (char)ch };
    }

        /**
         * Constructor.
         */
    public MultiCharNode(int lo, int hi)
    {
        directMatches = null;
        rangeMatches = new int[] { lo, hi };
    }

        /**
         * Gets the character.
         */
    public int getChar()
    {
        return directMatches[0];
    }

        /**
         * Print this node.
         */
    public void print()
    {
        System.out.print("[" + (invert ? "^ " : " "));
        int nD = directMatches == null ? 0 : directMatches.length;
        int nR = rangeMatches == null ? 0 : rangeMatches.length;
        for (int i = 0;i < nD;i++)
        {
            if (i > 0) System.out.print(", ");
            System.out.print((char)directMatches[i]);
        }

        for (int i = 0;i < nR;i+=2)
        {
            System.out.print((char)rangeMatches[i] + " - " + (char)rangeMatches[i + 1] + ", ");
        }
        System.out.print(" ] ");
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean matchesRange(int lo, int hi)
    {
        return false;
    }

        /**
         * Tells if a char matches this class of characters.
         */
    public boolean charMatches(char inchar)
    {
        boolean matched = false;
        int nDM = directMatches == null ? 0 : directMatches.length;
        int nRM = rangeMatches == null ? 0 : rangeMatches.length;

        for (int i = 0;! matched && i < nDM;i++)
        {
            if (inchar == directMatches[i]) matched = true;
        }

        for (int i = 0; !matched && i < nRM;i += 2)
        {
            if (inchar >= rangeMatches[i] && inchar <= rangeMatches[i + 1])
                matched = true;
        }

        return matched != invert;
    }

    public static void main(String args[]) throws Exception
    {
        int len = args[0].length();
        char array[] = args[0].toCharArray();
        MultiCharNode ccNode = new MultiCharNode(array, 0, len);
            // print out the ccNode

        ccNode.print();
        System.out.println("");
    }
}
