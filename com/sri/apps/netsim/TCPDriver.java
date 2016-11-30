
package com.sri.apps.netsim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * The TCP Driver object.
 * Basically this driver is needed somewhere in the
 * stack if TCP based objects are to be running.
 */
public interface TCPDriver extends ProtocolDriver
{
        /**
         * Reads the Destination port.
         */
    public int getDestPort(Packet packet);

        /**
         * Writes the dest port.
         */
    public void setDestPort(Packet packet, int port);

        /**
         * Reads the dest port.
         */
    public int getSourcePort(Packet packet);

        /**
         * Writes the Source port.
         */
    public void setSourcePort(Packet packet, int port);

        /**
         * Gets a socket object associated with "connecting" to a
         * destination address on a given port.
         *
         * The return value is a socket identifier, which should be used to
         * send and recieve stuff.
         */
    int connect(int destAddress, int destPort);

        /**
         * Connects to an address and binds to the socket.
         */
    int bind(int destAddress, int destPort);

        /**
         * Closes the given socket.
         */
    int closeSocket(int socket);

        /**
         * Writes a set of bytes to a given socket.
         */
    int send(int socket, byte buffer[], int offset, int len);

        /**
         * Tries to read "len" bytes and returns the number
         * of bytes actually read.
         *
         * If no bytes are available then 0 is returned.
         */
    int recv(int socket, byte buffer[], int offset, int len);

        /**
         * What kind of child drivers can be used?
         * They need to listen to information about sockets 
         * ie, each socket maintains a state of where it is at.  For
         * example one socket could be in "waiting for data" state, while
         * other could be in a "waiting to accept connections" mode.
         * These states will be defined somewhere.
         */
    //public void registerChild(SocketListener child);
}
