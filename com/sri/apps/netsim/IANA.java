package com.sri.apps.netsim; 


/**
 * A class for generating Mac Addresses
 */
public class IANA
{
        /**
         * Current mac address that we are upto.
         */
    protected static long macCounter = 1;

        /**
         * Number of macaddresses in the pool
         */
    protected static int nInPool = 0;

        /**
         * The pool of unused mac addresses
         */
    protected static long macPool[] = new long[1024];

        /**
         * Reset the IANA mac address table.
         */
    public synchronized static void reset()
    {
        nInPool = 0;
        macCounter = 1;
    }

        /**
         * Get the next available mac address.
         */
    public synchronized static long getMacAddress()
    {
        if (nInPool > 0) return macPool[--nInPool];
        return macCounter++;
    }
    
        /**
         * Put a mac address back in the queue so it 
         * can be given to another object.
         */
    public synchronized void returnMacAddress(long macAddress)
    {
        if (macPool.length == nInPool)
        {
            long l2[] = macPool;
            macPool = new long[nInPool * 3 >> 1] ;
            System.arraycopy(l2, 0, macPool, 0, nInPool);
        }
        macPool[nInPool] = macAddress;
        nInPool++;
    }
}
