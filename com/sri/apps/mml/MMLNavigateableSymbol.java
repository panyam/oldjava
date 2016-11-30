package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;

    /**
     * Symbol that allows navigation within it.
     */
public abstract class MMLNavigateableSymbol extends MMLSymbol
{
        /**
         * Process a key and return an appropriate cursor.
         */
    public abstract MMLNavigateableSymbol processKey(KeyEvent e,
                                                  MMLCursor curr);

        /**
         * Enter into the next container.
         */
    public abstract MMLNavigateableSymbol enter (KeyEvent e,
                                              MMLCursor prev,
                                              MMLCursor next);

        /**
         * Exit into the next container.
         */
    public abstract MMLNavigateableSymbol exit	(KeyEvent e,
											 MMLCursor prev,
                                             MMLCursor next);

		/**
		 * Given a point where the mouse was pressed, returns the symbol
		 * that is at the point.
		 * The cursor information MUST be set by the symbol.
		 * null is returned if no navigateable symbol is found
		 * at the point.
		 */
	public abstract MMLNavigateableSymbol mousePressed(Point p,
													MMLCursor currCursor);
}
