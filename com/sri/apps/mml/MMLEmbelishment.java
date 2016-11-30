package com.sri.apps.mml;


/**
 * A class to define the embelishment on a symbol.
 */
public class MMLEmbelishment
{
        /**
         * Underlining.
         */
    public static final MMLEmbelishment UNDERLINE = new MMLEmbelishment();

        /**
         * Strike through.
         */
    public static final MMLEmbelishment STRIKE_THRU = new MMLEmbelishment();

        /**
         * Double strike through.
         */
    public static final MMLEmbelishment DSTRIKE_THRU = new MMLEmbelishment();

        /**
         * Protected constructor in order to defeat instantiation.
         */
    protected MMLEmbelishment()
    {

    }
}
