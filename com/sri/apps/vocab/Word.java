package com.sri.apps.vocab;

import java.util.*;

/**
 * The main word.
 *
 * @author Sri Panyam
 */
public class Word
{
        /**
         * The ID of the word.
         */
    public int wordID;

        /**
         * The actual value of the word.
         */
    public String wordValue;

        /**
         * Gets the lowercase equivalent of the word.
         * Stores for efficient computation in order to avoid recalculation
         * each time.
         */
    public String lowerCase;

        /**
         * Meaning of the word.
         */
    public String meaning;

        /**
         * List of all synonyms.
         */
    protected WordList synonyms;

        /**
         * List of all antonyms
         */
    protected WordList antonyms;

        /**
         * Constructor.
         */
    public Word(String val, String meaning)
    {
        this.wordValue = val;
        this.lowerCase = wordValue.toLowerCase();
        this.meaning = meaning;
    }

        /**
         * Clones the word.
         */
    public Object clone()
    {
        Word out = new Word(wordValue, meaning);
        out.synonyms = this.synonyms;
        out.antonyms = this.antonyms;
        return out;
    }

        /**
         * Compare to another word.
         */
    public int compareTo(String word)
    {
        return lowerCase.compareTo(word.toLowerCase());
    }

        /**
         * Compare with another word.
         */
    public int compareTo(Word word)
    {
        return lowerCase.compareTo(word.lowerCase);
    }

        /**
         * Tells if this equals another object.
         */
    public boolean equals(Object another)
    {
        if (another == this) return true;
        else if (another instanceof String)
            return ((String)another).toLowerCase().equals(lowerCase);
        else if (another instanceof Word)
        {
            return ((Word)another).lowerCase.equals(lowerCase);
        }
        return false;
    }
}
