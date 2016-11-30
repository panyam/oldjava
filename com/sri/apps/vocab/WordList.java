
package com.sri.apps.vocab;

import java.util.*;
import com.sri.apps.vocab.filters.*;

/**
 * List of words.
 *
 * This list will be sorted.
 *
 * @author  Sri Panyam
 */
public class WordList
{
        /**
         * Tells if the list is sorted or not.
         */
    public boolean isSorted = false;

        /**
         * The array of words.
         */
    public Word words[];

        /**
         * Number of words in this list.
         */
    public int nWords;

        /**
         * Default constructor.
         */
    public WordList()
    {
        this(5);
    }

        /**
         * Constructs a list with a given intial capacity.
         *
         * @param   capacity    The initial capacity.
         */
    public WordList(int capacity)
    {
        nWords = 0;
        words = new Word[capacity];
    }

        /**
         * Copies the list to a new list.
         */
    public void copyTo(WordList newList)
    {
        newList.ensureCapacity(nWords);
        newList.nWords = nWords;
        newList.isSorted = isSorted;
        System.arraycopy(words, 0, newList.words, 0, nWords);
    }

        /**
         * Clone the list.
         */
    public Object clone()
    {
        WordList out = new WordList(words.length);
        copyTo(out);
        return out;
    }

        /**
         * Returns a subset of the word list as another list
         *
         * @return a new list whose items will be within the indices from
         * and to-1 inclusive.
         */
    public void getSubset(int from, int to, WordList outList)
    {
        int newSize = to - from > nWords ? to - from : nWords;
        outList.ensureCapacity(newSize + 5);
        System.arraycopy(words, from, outList.words, 0, newSize);
    }

        /**
         * Gets a list of all words that match (or not) a specific filter.
         */
    public void getFilteredWords(Vector filterList, WordList outList)
    {
        outList.ensureCapacity(nWords);
        int nFilters = filterList.size();

        outList.nWords = 0;

        for (int i = 0;i < nWords;i++)
        {
            boolean wordMatched = true;
            for (int j = 0;wordMatched && j < nFilters;j++)
            {
                if ( !
                    ((WordFilter)filterList.elementAt(j)).
                                            wordMatches(words[i]))
                {
                    wordMatched = false;
                }
            }
            if (wordMatched)
            {
                outList.words[outList.nWords++] = words[i];
            }
        }
    }

        /**
         * Adds a new word to the list.
         */
    public void addWord(Word word)
    {
        ensureCapacity(nWords + 1);

        words[nWords++] = word;
        isSorted = false;
    }

        /**
         * Sorts the word list.
         */
    public void sortList()
    {
        EQListSorter.sort(words, 0, nWords - 1);
        isSorted = true;
    }

        /**
         * Add a new word to the list.
         *
         * The word is added in its sorted position.
         *
         * @param   word    The word to be added.
         * @return  True if word added otherwise false.
         */
    public int addWordSorted(Word word)
    {
        int lo = 0, hi = nWords - 1;
        int mid;

        if (nWords == 0)
        {
            words[nWords++] = word;
            return 0;
        }

        while (lo < hi)
        {
            mid = (lo + hi) / 2;
            Word midWord = words[mid];
            int comp = midWord.compareTo(word.wordValue);

            if (comp == 0)
            {
                return mid;
            } else if (comp < 0)
            {
                hi = (mid == lo ? lo : mid - 1);
            } else
            {
                lo = mid + 1;
            }
        }

                // then insert above or below it...
        if (lo == hi)
        {
            int comp = words[lo].compareTo(word.wordValue);
            if (comp == 0) return lo;
            else if (comp > 0)
            {
                insertWord(lo, word);
                    // fire??
                return lo;
            } else
            {
                insertWord(lo + 1, word);

                    // fire??
                return lo + 1;
            }
        } else
        {
            System.out.println("lo, hi, loItem, hiItem: " +
                                lo + ", " + hi + ", " +
                                words[lo].wordValue + ", " +
                                words[hi].wordValue);
        }
        return lo;
    }

        /**
         * Insert a word at a given index.
         */
    protected void insertWord(int index, Word word)
    {
        ensureCapacity(nWords + 1);

        System.arraycopy(words, index, words, index + 1, nWords - index);
        words[index] = word;
        nWords ++;
    }

        /**
         * Get the word at a given index.
         */
    public Word getWord(int index)
    {
        return words[index];
    }

        /**
         * Gets a word object given a word as a string.
         */
    public Word getWord(String word)
    {
        int ind = indexOf(word);
        return ind < 0 ? null : words[ind];
    }

        /**
         * Gets the index of a given word.
         */
    public int indexOf(String word)
    {
        if (isSorted)
        {
            int lo = 0, hi = nWords - 1;
            int mid;

            while (lo < hi)
            {
                mid = (hi + lo) / 2;
                int comp = words[mid].compareTo(word);

                if (comp == 0) return mid;
                else if (comp < 0)
                {
                    hi = mid - 1;
                } else
                {
                    lo = mid + 1;
                }
            }
        } else
        {
            for (int i = 0;i < nWords;i++)
            {
                if (words[i].equals(word)) return i;
            }
        }

        return -1;
    }

        /**
         * Tells if the list is sorted or not.
         */
    public boolean isSorted()
    {
        return isSorted;
    }

    public int indexOf(Word word)
    {
        return indexOf(word.wordValue);
    }

        /**
         * Ensures that the word array has this much capacity.
         * @param   newCapacity The new minimum capacity which is to be
         *                      ensured.
         */
    public void ensureCapacity(int newCapacity)
    {
        if (words == null) 
        {
            words = new Word[newCapacity];
            return ;
        }

        if (words.length < newCapacity)
        {
            Word w2[] = words;
            words = new Word[(newCapacity * 3) / 2];
            System.arraycopy(w2, 0, words, 0, nWords);
        }
    }
}
