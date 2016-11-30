
package com.sri.apps.vocab.orderers;

import com.sri.apps.vocab.filters.*;
import com.sri.gui.core.containers.*;
import com.sri.apps.vocab.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.applet.*;

/**
 * Arranges the words in increasing order.
 */
public class AscendingOrderer implements Orderer
{
        /**
         * Returns a short name indicating the type of the iterator.  Eg
         * "Increasing", "Descending", "Random" etc.
         */
    public String getType()
    {
        return "Ascending";
    }

        /**
         * Given a word list creates a new wordlist with a given 
         */
    public void arrangeList(WordList inList, WordList outList)
    {
        inList.copyTo(outList);
        outList.sortList();
    }
}
