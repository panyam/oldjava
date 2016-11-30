package com.sri.apps.mml;

/**
 * A class to define justification of a symbol.
 */
public final class MMLJustification 
{
        /**
         * Left Justification.
         */
    public static final MMLJustification LEFT_JUSTIFY = new MMLJustification();

        /**
         * Left Justification.
         */
    public static final MMLJustification RIGHT_JUSTIFY = new MMLJustification();

        /**
         * Center Justification.
         */
    public static final MMLJustification CENTER_JUSTIFY = new MMLJustification();

        /**
         * Bottom Justification.
         */
    public static final MMLJustification BOTTOM_JUSTIFY = new MMLJustification();

        /**
         * Top Justification.
         */
    public static final MMLJustification TOP_JUSTIFY = new MMLJustification();

        /**
         * Protected constructor in order to defeat instantiation.
         */
    protected MMLJustification()
    {

    }
}
