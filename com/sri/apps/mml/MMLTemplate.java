package com.sri.apps.mml;

import java.awt.*;
import java.util.*;

/**
 * A class in which symbols can be typed.
 */
public abstract class MMLTemplate extends MMLNavigateableSymbol
{
        /**
         * List of symbols.
         */
    MMLParagraph symbols[] = null;

        /**
         * Constructor.
         */
    public MMLTemplate()
    {
        this(1);
    }

        /**
         * Constructor.
         */
    public MMLTemplate(int nSyms)
    {
        symbols = new MMLParagraph[nSyms];
    }

        /**
         * Process a key and return an appropriate cursor.
         */
    public MMLNavigateableSymbol processKey(java.awt.event.KeyEvent e,
											MMLCursor cursor)
    {
		return null;
    }
}
