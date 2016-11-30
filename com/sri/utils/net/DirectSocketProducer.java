package com.sri.utils.net;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Basically returns a socket on a given port and the given address.
 */
public class DirectSocketProducer implements SocketProducer
{
    public static DirectSocketProducer defaultSocketProducer 
                                    = new DirectSocketProducer();

        /**
         * Gets a new socket.
         */
    public Socket getSocket(String address, int port, boolean bindSocket)
            throws Exception
    {
        return new Socket(address, port);
    }
}
