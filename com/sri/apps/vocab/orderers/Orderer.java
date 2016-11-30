
package com.sri.apps.vocab.orderers;

import com.sri.apps.vocab.filters.*;
import com.sri.gui.core.containers.*;
import com.sri.apps.vocab.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.applet.*;

/**
 * Rearranges the words in a word list arbitrarily.  Some order types are
 * AscendingOrderer, RandomOrderer, DescendingOrderer etc.
 */
public interface Orderer
{
        /**
         * Returns a short name indicating the type of the iterator.  Eg
         * "Increasing", "Descending", "Random" etc.
         */
    public String getType();

        /**
         * Given a word list creates a new wordlist with a given 
         */
    public void arrangeList(WordList inList, WordList outList);
}
