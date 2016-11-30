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
 */
public abstract class Client implements Runnable
{
        /**
         * A set of client listeners that will be notified 
         * when we receive messages from the server.
         */
    public RTSPClientListener clientListener = null;

    public final static String RTSP_VERSION = "RTSP/1.0";

        /**
         * Hashtable that stores all the requests.
         */
    protected Hashtable requestTable = new Hashtable();

        /**
         * The queue of vectors.
         */
    public Queue requestQueue = new Queue();

        /**
         * Default port to use.
         */
    public final static int DEFAULT_PORT = 554;

        /**
         * The thread used to listen for responses from
         * the server.
         */
    protected Thread ourThread = null;
    protected boolean clientStopped = false;
    protected boolean clientRunning = false;

        /**
         * The host to which to connect to.
         */
    protected String hostname;

        /**
         * Tells if we are connected or not.
         */
    protected boolean isConnected = false;

        /**
         * Name of the file that we are getting within
         * the server.
         */
    protected String filename;

        /**
         * The port on which to connect to.
         */
    protected int port = DEFAULT_PORT;

        /**
         * The current acknowledgement number.
         */
    int ackNumber = 1;

        /**
         * Sets the client listener
         */
    public void setClientListener(RTSPClientListener listener)
    {
        clientListener = listener;
    }

        /**
         * Main thread method.
         * Here we keep listening to packets from the server.
         * These packets could be requests or responses to 
         * requests created by this client.  Both these should
         * be handled in this run method.  The packets could
         * be out of order and should be re arranged.
         */
    public void run()
    {
        synchronized(this)
        {
            while (clientRunning && !clientStopped)
            {
                try
                {
                    System.out.println("Waiting for next message: ");
                    RTSPMessage message = getNextMessage();
    
                        // if there are no messages then we just quit...
                    if (message == null)
                    {
                        return ;
                    }

                    if (clientListener != null)
                    {
                        if (message instanceof RTSPRequest)
                        {
                            clientListener.
                                processRequest((RTSPRequest)message);
                        } else
                        {
                                processResponseMessage((RTSPResponse)message);
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        clientRunning = false;
        clientStopped = false;
        notifyAll();
    }

        /**
         * Send a given request.
         */
    public void sendRequest(RTSPRequest request) throws Exception
    {
            // check ack numbers and store request in tables
            // if necessary...

        String req = "" + request.cSeq;
        requestTable.put(req, request);
        sendMessage(request);
    }

        /**
         * Send a given response.
         */
    public void sendResponse(RTSPResponse response) throws Exception
    {
        sendMessage(response);
    }

        /**
         * This is the function that needs to be completed
         * by the child classes.
         */
    protected abstract RTSPMessage getNextMessage() throws Exception;

        /**
         * Responsible for sending the message at the transport layer.
         * Will be extended by the child classes.
         */
    protected abstract void sendMessage(RTSPMessage message) throws Exception;

        /**
         * Connect to the server.
         * Must be called before any requests can be processed.
         */
    public synchronized void connect() throws Exception
    {
        if (clientRunning && !clientStopped) 
        {   // then stop the client
            clientStopped = true;
            notifyAll();
            while (clientRunning)
            {
                wait();
            }
        }

        if (ourThread == null) ourThread = new Thread(this);
        isConnected = true;
        clientStopped = false;
        clientRunning = true;
        ourThread.start();
    }

        /**
         * Disconnects.
         */
    public synchronized void disconnect() throws Exception
    {
        if (ourThread == null || ! ourThread.isAlive()) return ;
        isConnected = false;
        ourThread.stop();
    }

        /**
         * Processes a request message from the server to the client.
         */
    protected synchronized void processResponseMessage(RTSPResponse response)
        throws Exception
    {
        String cseq = response.cSeq + "";
        RTSPRequest request = (RTSPRequest)requestTable.get(cseq);

            // if no matching request then return 
        if (request == null) return ;

            // otherwise the clientListener's event handler
            // Should see if this functionality should be here
            // or in the listener class.
        clientListener.processResponse(request, response);
    }
}
