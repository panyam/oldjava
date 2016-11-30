
package com.sri.apps.netsim; 

/**
 * This is an event that is generated in order to make network events
 * execute at certain times.  Most often, the source and destination of
 * these events would be the same.
 */
public class NotifyEvent extends SimEvent
{
        /**
         * The type of notification.
         *
         * For example this could be a "Broadcast RIP" or what ever.
         */
    public int notificationType;

        /**
         * Not sure how to explain this.
         */
    public Object target;
}
