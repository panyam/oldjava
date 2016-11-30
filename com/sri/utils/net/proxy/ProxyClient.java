package com.sri.utils.net.proxy;

import com.sri.utils.net.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Super class of all proxy clients classes.
 */
public abstract class ProxyClient implements SocketProducer
{
        /**
         * The port to which to connect to.
         */
    protected int proxyPort = -1;

        /**
         * The proxy hostname.
         */
    protected String proxyAddress = null;

        /**
         * Default constructor.
         */
    protected ProxyClient()
    {
    }

        /**
         * Constructor.
         */
    protected ProxyClient(String host, int port)
    {
        setHost(host);
        setPort(port);
    }

        /**
         * Sets the proxy port.
         */
    public void setPort(int p)
    {
        this.proxyPort = p;
    }

        /**
         * Gets the proxy port.
         */
    public int getPort()
    {
        return proxyPort;
    }

        /**
         * Sets the proxy host.
         */
    public void setHost(String address)
    {
        this.proxyAddress = address;
    }

        /**
         * Gets the proxy host.
         */
    public String getHost()
    {
        return proxyAddress;
    }
}
