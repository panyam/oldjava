package svtool.core.adt;

import java.util.*;

public class LLNode
{
    public LLNode next, prev;
    public Object key;

        /**
         * Constructor.
         */
    public LLNode(Object key)
    {
        this.key = key;
        next = prev = null;
    }
}

