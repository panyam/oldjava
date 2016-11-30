package com.sri.utils.net;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A class for a HTTP connection.  For both version 1.0 and 1.1
 *
 * @Auther: Sriram Panyam
 * @Data: 18/07/2002
 */
public class HTTPConnection implements Runnable
{
        /**
         * The thread used to listen for responses from
         * the server.
         */
    protected Thread listenerThread = null;

        /**
         * The host to which to connect to.
         */
    protected String host;

        /**
         * The port on which to connect to.
         */
    protected int port;

        /**
         * The socketin which to get the data.
         */
    protected Socket socket;

        /**
         * Constructur.
         */
    public HTTPConnection(String host, int port, SocketProducer sp)
        throws Exception
    {
        this.host = host;
        this.port = port;

        socket = sp.getSocket(host, port, false);
    }

        /**
         * Main thread method.
         */
    public void run()
    {
    }
}
