package com.sri.utils.net;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Basically returns a socket on a given port and the given address.
 */
public interface SocketProducer
{
        /**
         * Gets a new socket.
         */
    public Socket getSocket(String address, int port, boolean bindSocket)
            throws Exception;
}
