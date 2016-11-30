

package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;
import com.sri.utils.adt.*;

/**
 * A class for a RTSP connection based on UDP.
 *
 * @Auther: Sriram Panyam
 * @Data: 4/1/2003
 */
public class UDPClient extends Client
{
        /**
         * Constructor.
         * The host is in the form::
         * "rtspu://" host [":"port] [abs_path]
         */
    public UDPClient(String host, String file, int port)
        throws Exception
    {
        this.hostname = host;
        this.filename = file;
        this.port = port;
    }

        /**
         * Returns the next message.
         */
    protected RTSPMessage getNextMessage() throws Exception
    {
        return null;
    }

        /**
         * Sends the message using data grams.
         */
    public void sendMessage(RTSPMessage message) throws Exception
    {
    }
}
