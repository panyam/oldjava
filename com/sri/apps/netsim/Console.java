package com.sri.apps.netsim;


/**
 * A console class that is basically used by each device to print out its
 * own information.
 */
public interface Console
{
        /**
         * Process a message from the device.
         * Basically a device can call this console to write out
         * info or anything else it chooses.
         *
         * Ideally in real life situations, there would be one
         * console perdevice.  But what we are doing is one 
         * common listener and attach as many listeners as you want
         * to that one common listeners and let the listener do the
         * work as opposed to letting the device do all the work.
         *
         * @param       device      The device that generated the event.
         * @param       code        The event code.
         * @param       info        The object that holds the info.
         *                          Usually a string.
         */
    public void processDeviceMessage(Device device, DeviceEvent dev, Object info)
        throws Exception;

        /**
         * Same as above but for Link objects.
         */
    public void processLinkMessage(Link link, Integer code, Object info)
        throws Exception;
}
