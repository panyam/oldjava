
package com.sri.apps.vocab.orderers;

import com.sri.apps.vocab.filters.*;
import com.sri.gui.core.containers.*;
import com.sri.apps.vocab.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.applet.*;

/**
 * Arranges the words in decreasing order.
 */
public class DescendingOrderer implements Orderer
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

            // now reverse the order
        int nWords = outList.nWords;
        int half = nWords / 2;
        Word temp = null;
        for (int i = 0;i < half;i++)
        {
            temp = outList.words[i];
            outList.words[i] = outList.words[(nWords - i) - 1];
            outList.words[(nWords - i) - 1] = temp;
        }
    }
}
