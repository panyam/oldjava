
package com.sri.apps.netsim;

/**
 * This is the super class of network objects.  This object runs while
 * generating and consuming events (packets).
 */
public interface NetworkEntity extends XMLObject
{
        /**
         * Starts the device.
         */
    public void start(Network parent, double currTime);

        /**
         * Stops the device.
         */
    public void stop(Network parent, double currTime);

        /**
         * Tells if he device is running or not.
         */
    public boolean isRunning();

        /**
         * Network entities must process a packet and return the time taken
         * to process the packet.  The return is the time taken.
         */
    public double handleEvent(SimEvent event,
                              double currTime,
                              Network parent);
}
