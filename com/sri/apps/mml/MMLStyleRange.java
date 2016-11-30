package com.sri.apps.mml;

import java.util.*;
import java.awt.*;

/**
 * A class for style ranges within a paragraph.
 */
public class MMLStyleRange
{
        /**
         * The starting symbol of the range.
         */
    public int fromLine;
        
        /**
         * The ending symbol of the range.
         */
    public int toLine;

        /**
         * Starting position in the first line.
         */
    public int fromPos;

        /**
         * Ending position in the last line.
         */
    public int toPos;

        /**
         * Foreground of this range.
         */
    public Color fg;

        /**
         * Background of this range.
         */
    public Color bg;

        /**
         * Font in this range.
         */
    public Font font;

        /**
         * Justification in this range.
         */
    public MMLJustification just = MMLJustification.LEFT_JUSTIFY;

        /**
         * Embelishment in this range.
         */
    public MMLEmbelishment emb = null;


        /**
         * Tells if the given range's line and position 
         * are contained within this range.
         */
    public int compare(MMLStyleRange range)
    {
        if (range.fromLine == fromLine) return range.fromPos - fromPos;
        return range.fromLine - fromLine;
    }

        /**
         * Tells if the given line and position is contained
         * within this range.
         */
    public int compare(int line, int pos)
    {
        if (line == fromLine) return pos - fromPos;
        return line - fromLine;
    }
}
