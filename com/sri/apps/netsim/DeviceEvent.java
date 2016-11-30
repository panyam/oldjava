package com.sri.apps.netsim;
import java.awt.*;
import java.util.*;

/**
 * Holds constants for Device Events in general.
 */
public class DeviceEvent
{
    static int msgCounter = 0;

    int code = 0;
    String eventString = "";

        /**
         * If the device has been stopped.
         */
	public final static DeviceEvent DEVICE_STOPPED =
                        new DeviceEvent(msgCounter++, "DEVICE STOPED");

        /**
         * If packets are sent.
         */
    public final static DeviceEvent PACKET_SENT = 
                        new DeviceEvent(msgCounter++, "PACKET SENT");

        /**
         * When packet is fragmented.
         */
    public final static DeviceEvent PACKET_FRAGMENTED = 
                        new DeviceEvent(msgCounter++, "PACKET FRAGMENTED");

        /**
         * When route chosen.
         */
    public final static DeviceEvent ROUTE_CHOSEN = 
                        new DeviceEvent(msgCounter++, "ROUTE CHOSEN");

        /**
         * If packets are dropped.
         */
    public final static DeviceEvent PACKET_DROPPED = 
                        new DeviceEvent(msgCounter++, "PACKET DROPPED");

        /**
         * If packets are "allowed".
         */
    public final static DeviceEvent PACKET_ALLOWED = 
                        new DeviceEvent(msgCounter++, "PACKET ALLOWED");

        /**
         * For when a route is selected.
         */
    public final static DeviceEvent ROUTE_SELECTED = 
                        new DeviceEvent(msgCounter++, "ROUTE SELECTED");

        /**
         * Sent only if we are dealing with 
         * packets that neither UDP nor TCP.
         */
    public final static DeviceEvent UNKNOWN_PROTOCOL = 
                    new DeviceEvent(msgCounter++, "UNKNOWN PROTOCOL");

        /**
         * Thrown if respondign to a point.
         */
    public final static DeviceEvent RESPONDING = 
                    new DeviceEvent(msgCounter++, "RESPONDING");

        /**
         * REsponse by a client.
         */
    public final static DeviceEvent GOT_REQUEST_FROM_CLIENT = 
                    new DeviceEvent(msgCounter++, "GOT REQUEST FROM CLIENT");

        /**
         * REsponse by a client.
         */
    public final static DeviceEvent GOT_REPLY_FROM_SERVER = 
                        new DeviceEvent(msgCounter++, "GOT REPLY FROM SERVER");

        /**
         * No Service code.
         */
    public final static DeviceEvent NO_SERVICE = 
                        new DeviceEvent(msgCounter++, "SERVICE NOT RUNNING");

        /**
         * Client Added.
         */
    public final static DeviceEvent CLIENT_RECEIVED = 
                        new DeviceEvent(msgCounter++, "CLIENT ADDED");

        /**
         * This is to drop all packets that arent
         * destined to this host.
         */
    public final static DeviceEvent INVALID_DEST_IP = 
                        new DeviceEvent(msgCounter++, "INVALID DEST IP");

        /**
         * New client received.
         */
    public final static DeviceEvent RECEIVED_NEW_HOST = 
                        new DeviceEvent(msgCounter++, "RECEIVED NEW HOST");

        /**
         * Syn Ack Sent event.
         */
    public final static DeviceEvent SYN_ACK_SENT = 
                        new DeviceEvent(msgCounter++, "SYN_ACK_SENT");

        /**
         * Sent only if we are dealing with 
         * packets that neither UDP nor TCP.
         */
    public final static DeviceEvent PACKET_FORWARDED = 
                        new DeviceEvent(msgCounter++, "PACKET FORWARDED");

        /**
         * Packets are flooded.
         */
    public final static DeviceEvent PACKET_FLOODED   = 
                        new DeviceEvent(msgCounter++, "PACKET FLOODED");


        /**
         * Constructor.
         */
    public DeviceEvent(int code, String es)
    {
        this.code = code;
        this.eventString = es;
    }
}
