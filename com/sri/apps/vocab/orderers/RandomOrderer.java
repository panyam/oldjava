
package com.sri.apps.vocab.orderers;

import com.sri.apps.vocab.filters.*;
import com.sri.gui.core.containers.*;
import com.sri.apps.vocab.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.applet.*;

/**
 * Arranges the words in random order.
 */
public class RandomOrderer implements Orderer
{
        /**
         * Returns a short name indicating the type of the iterator.  Eg
         * "Increasing", "Descending", "Random" etc.
         */
    public String getType()
    {
        return "Random";
    }

        /**
         * Given a word list creates a new wordlist with a given 
         */
    public void arrangeList(WordList inList, WordList outList)
    {
        int nWords = inList.nWords;

        outList.ensureCapacity(nWords);

            // clear word list first..
        for (int i = 0;i < nWords;i++) outList.words[i] = null;

        for (int i = 0;i < nWords;i++)
        {
            int randPos = (int)(Math.random() * nWords);

            while (outList.words[randPos] != null)
            {
                randPos++;
                if (randPos == nWords) randPos = 0;
            }
            outList.words[randPos] = inList.words[i];
        }
    }
}
