
package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;
import com.sri.utils.adt.*;

/**
 * A class for a RTSP connection.  Supports both TCP and UDP based
 * connections
 *
 * @Auther: Sriram Panyam
 * @Data: 4/1/2003
 * So far this client is a basic playback client.
 */
public class TCPClient extends Client
{
        /**
         * The socket in which to get the data.
         */
    protected Socket socket = null;

        /**
         * The socket producer that we will use.
         */
    protected SocketProducer socketProducer = null;

        /**
         * Constructor.
         * The host is in the form::
         * ("rtsp:" | "rtspu:") "//" host [":"port] [abs_path]
         */
    public TCPClient(String host, String file, int port, SocketProducer sp)
        throws Exception
    {
        this.socketProducer = sp;
        this.filename = file;
        this.hostname = host;
        this.port = port;

        socket = sp.getSocket(hostname, this.port, true);
    }

        /**
         * Returns the next message.
         */
    protected RTSPMessage getNextMessage() throws Exception
    {
        if (socket == null) return null;

        return RTSPMessage.readMessage(socket.getInputStream());
    }

        /**
         * Sends the message.
         */
    public void sendMessage(RTSPMessage message) throws Exception
    {
        OutputStream oStream = socket.getOutputStream();

        message.writeMessage(oStream);
    }

        /**
         * Send out a setup request.
         */
    public void setup() throws Exception
    {
        RTSPRequest request = new RTSPRequest("SETUP", "rtsp://" + hostname + "/" + filename, RTSP_VERSION);
        requestTable.put(ackNumber + "", request);

            // store the request for now so that 
            // when we get a response, we can match
            // up the response and see what is required.
            // Once we get the response, we can get 
            // rid of the request from the table.
        int clientPort = socket.getPort();
        System.out.println("Port = " + clientPort);
        request.setHeader("CSeq", ackNumber + "");
        request.setHeader("Transport", "RTP/AVP/TCP;unicast");
        request.setHeader("User-Agent", " RealMedia Player Version 6.0.7.1423 (win32)");
        sendMessage(request);
        ackNumber++;
    }
}
