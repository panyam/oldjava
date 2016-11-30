
import java.util.*;

public class RegExUtils
{
        /**
         * Converts a digit from unicode or char to int.
         */
    public static final int hex2int(int digit)
    {
        if (digit >= '0' && digit <= '9')
        {
            return digit - '0';
            // leave as it is.
        } else if (digit >= 'a' && digit <= 'f')
        {
            return digit - 'a';
        } else if (digit >= 'A' && digit <= 'F')
        {
            return digit - 'A';
        }
        return -1;
    }

        /**
         * Get char class nodes.
         * Basically this is for items that are inside square brackets.
         */
    public static ExpNode extractRangeChars
                        (char array[], int start, int end)
                        throws RegExException
    {
        boolean invert = false;
        boolean foundMinus = false;
        int currCh, nextCh;
        int startChar = -1;
        int currChar = -1;
        int digit;

        ExpNode out = null;
        Vector ranges = new Vector();

        int ptr = start;

        if (array[ptr] == '^')
        {
            invert = true;
            ptr++;
        }

        //System.out.println("Invert: " + invert);

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
                long chVal = extractChar(array, ptr, end);
                int nChars = (int)((chVal >> 32) & 0xffffffff);
                currChar = (int)(chVal & 0xffffffff);
                ptr += nChars;

                    // now if minus has already been foudn it means
                    // it was right before currChar  so set a range
                if (foundMinus)
                {
                    foundMinus = false;

                        // add this range in the right spot...
                    insertRange(ranges, startChar, currChar, invert);
                } else
                {
                        // now check if the next char is a '-'.  if it is
                        // then dont store it in direct match list
                    if (ptr == end || array[ptr] != '-')
                    {
                        // add this range in the right spot...
                        insertRange(ranges, currChar, currChar, invert);
                    }
                }
                startChar = -1;
            }
        }

        int nR = ranges.size();
        for (int i = 0;i < nR;i++)
        {
            ExpNode curr = (ExpNode)ranges.elementAt(i);
            if (out == null)
            {
                out = curr;
            } else
            {
                out = new BinaryExpNode(ExpNodeType.UNION_NODE, out, curr);
            }
        }
        return out;
    }

        /**
         * Inserts a new item into the tree in the processing
         * splitting and joining the trees.
         */
    protected static void insertRange(Vector ranges, int startChar,
                                            int endChar, boolean invert)
    {
        if (ranges.isEmpty())
        {
            if (invert)
            {
                ranges.addElement(new RangeCharNode(0, startChar - 1));
                ranges.addElement(new RangeCharNode(endChar + 1, 2 << 15));
            } else
            {
                if (startChar != endChar)
                {
                    ranges.addElement(new RangeCharNode(startChar, endChar));
                } else
                {
                    ranges.addElement(new SingleCharNode(startChar));
                }
            }
        } else
        {
            if (invert)
            {
                    // this is interesting because the range WILL be a
                    // subset of one of the leaves in this tree because
                    // the tree (if in invert mode) will initially have the
                    // "entier" char set minus the one item (or the range
                    // item)...
                    //
                    // so the deal is to find out which node is the
                    // "superset" of this range...  how do we find this?
                    // the key is left.rightmost...
                
                    // root now WILL be a binaryexpnode or a RangeCharNode
                int nR = ranges.size();

                    // find the superset of the current range and break it
                int i = 0;
                for (;i < nR;i++)
                {
                    Object ccNode = ranges.elementAt(i);
                    if (ccNode instanceof SingleCharNode)
                    {
                        if (startChar == endChar)
                        {
                            // do nothing.. this is not 
                            if (((SingleCharNode)ccNode).charMatches((char)startChar))
                            {
                                // remove from list
                                ranges.removeElementAt(i);
                                return ;
                            }
                        }
                    } else if (ccNode instanceof RangeCharNode)
                    {
                        RangeCharNode rcn = (RangeCharNode)ccNode;
                        if (rcn.loChar == startChar)
                        {
                            rcn.loChar = endChar + 1;
                        }

                        if (rcn.hiChar == endChar)
                        {
                            rcn.hiChar = startChar - 1;
                        }

                        if (rcn.hiChar < rcn.loChar)
                        {
                            ranges.removeElementAt(i);
                            return ;
                        }

                            // otherwise we have a split...
                    }
                }
            } else
            {
                if (startChar != endChar)
                {
                    ranges.addElement(new RangeCharNode(startChar, endChar));
                } else
                {
                    ranges.addElement(new SingleCharNode(startChar));
                }
            }
        }
    }

        /**
         * Append the existing temp list of range matches to the
         * current list.
         */
    protected static int []appendList(int matchList[], int list[], int nItems)
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
         * Extracts a character encoded as  string and returns a long.
         * The first 4 bytes of the long determine how many characters have
         * been consumed, while the last 4 bytes tell what the character
         * extracted was.
         *
         * Basically it is consistent with the Unicode specification.
         */
    public static long extractChar(char array[], int start, int end)
        throws RegExException
    {
        int ptr = start;
        char currCh = array[ptr], nextCh;
        int currChar;
        int digit;

        if (currCh != '\\')
        {
                // otherwise we have a normal char... match as is
                // so for this see if foundMinus and startChar are
                // already set...
            return 0x0000000100000000l | array[ptr];
        } else
        {
            ptr ++;
            if (ptr == end)
            {
                throw new RegExException
                    (RegExException.UNTERMINATED_ESCAPE_SEQUENCE);
            }

            nextCh = array[ptr];
            if (nextCh == '$')
            {
                return 0x0000000200000000l | '$';
            } if (nextCh == 'n')
            {
                return 0x0000000200000000l | '\n';
            } else if (nextCh == 'r')
            {
                return 0x0000000200000000l | '\r';
            } else if (nextCh == 't')
            {
                return 0x0000000200000000l | '\t';
            } else if (nextCh == 'f')
            {
                return 0x0000000200000000l | '\f';
            } else if (nextCh == 'b')
            {
                return 0x0000000200000000l | '\b';
            } else if (nextCh == 'N')
            {
                    // then we are reading NAMES of the unicode char
                ptr++;
                if (array[ptr] == '{')
                {
                    ptr++;
                    System.out.print("Feature 'N' not yet implemented: ");
                    while (array[ptr] != '}')
                    {
                        System.out.print(array[ptr]);
                        ptr++;
                    }
                    ptr++;
                    System.out.println("");
                    return ((long)(ptr - start)) << 32;
                } else
                {
                    return 0x0000000200000000l | '\n';
                }
            } else if (nextCh == 'u')
            {
                    // read 4 hex digits - unicode
                    // corresponds to 16 bits
                currChar = 0;
                for (int i = 0;i < 4;i++)
                {
                    if (ptr >= end)
                    {
                        throw new RegExException
                                (RegExException.INCOMPLETE_UNICODE);
                    }
                    digit = hex2int(array[ptr++]);
    
                    if (digit < 0)
                    {
                        System.out.println("Invalid UC: " + array[ptr - 1]);
                        throw new RegExException
                                (RegExException.INVALID_UNICODE);
                    }
                    currChar = (currChar << 4) | digit;
                }
                return 0x0000000600000000l | currChar;
            } else if (nextCh >= '0' && nextCh <= '9')
            {
                    // then read the next 2 digits which form hex
                    // digits - ascii
                ptr --;
                currChar = 0;
                for (int i = 0;i < 3;i++)
                {
                    if (ptr >= end)
                    {
                        throw new RegExException
                                (RegExException.INCOMPLETE_OCTAL);
                    }
                    digit = hex2int(array[ptr++]);
                    if (digit >= '0' && digit <= '7')
                    {
                        digit = digit - '0';
                    } else
                    {
                        throw new RegExException
                                (RegExException.INVALID_OCTAL);
                    }
                    currChar = (currChar << 3) | digit;
                }
                return 0x0000000400000000l | currChar;
            } else if (nextCh == 'x')
            {
                    // then read the next 2 digits which form hex
                    // digits - ascii
                currChar = 0;
                for (int i = 0;i < 2;i++)
                {
                    if (ptr >= end)
                    {
                        throw new RegExException
                                (RegExException.INCOMPLETE_ASCII);
                    }
                    digit = hex2int(array[ptr++]);
    
                    if (digit < 0)
                    {
                        throw new RegExException
                                (RegExException.INVALID_ASCII);
                    }
                    currChar = (currChar << 4) | digit;
                }
                return 0x0000000300000000l | currChar;
            }
        }
        return 0x0000000100000000l;
    }
}
