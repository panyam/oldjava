
package com.sri.apps.brewery.io;

/**
 * Super class of all CRC Genrator objects.
 */
public interface CRCGenerator
{
        /**
         * Initialises the CRC generator and indicates
         * that the crc produced must have atleast this 
         * many bytes.
         */
    public void initialise(int crcLength);

        /**
         * Process more bytes in order to incrementally 
         * compute the CRC.
         */
    public void consider(byte bytes[], int offset, int length);

        /**
         * Gets the CRC calculated sofar.
         *
         * This length of this CRC string would be as big
         * as the one specified in the argument to the
         * initialise function.
         */
    public byte []getCRC();
}
