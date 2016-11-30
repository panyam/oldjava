package com.sri.utils.net.proxy;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Class for creating sockets that needs to pass through a sock proxy.
 * Supports version 5.
 *
 * TODO:: Support for extendible socks authentication methods.
 */
public class Socks5ProxyClient extends ProxyClient
{
    public static final byte VERSION     = 0x05;
    public static final byte RESERVED    = 0x00;
    public static final byte CONNECT     = 0x01;
    public static final byte BIND        = 0x02;
    public static final byte UDP_ASSOC   = 0x03;

    public static final byte IP4_HOST       = 0x01;
    public static final byte DOMAIN_HOST    = 0x03;
    public static final byte IP6_HOST       = 0x04;

        // Some response codes.
    public static final byte REQUEST_SUCCEEDED = 0;
    public static final byte REQUEST_GENERAL_ERROR = 0x01;
    public static final byte REQUEST_NOT_ALLOWED = 0x02;
    public static final byte REQUEST_NET_UNREACHABLE = 0x03;
    public static final byte REQUEST_HOST_UNREACHABLE = 0x04;
    public static final byte REQUEST_CONN_REFUSED = 0x05;
    public static final byte REQUEST_TTL_EXPIRED = 0x06;
    public static final byte REQUEST_COMMAND_UNSUPPORTED = 0x07;
    public static final byte REQUEST_ADDR_UNSUPPORTED = 0x08;
    public static final byte REQUEST_UNASSIGNED = 0x09;

        /**
         * Set of authentication methods.
         */
    protected Vector authMethods = new Vector();

        /**
         * Constructor.
         */
    public Socks5ProxyClient(String host, int port)
    {
        super(host, port);

        authMethods.addElement(new UPAuthMethod());
    }

        /**
         * Returns a socksified socket!!!
         */
    public synchronized Socket getSocket(String address,
                                         int port,
                                         boolean bindSocket)
                    throws Exception
    {
        byte dataBuffer[] = new byte[512];
        int nRead = 0;
        int nWritten = 0;
        Socket outSocket = null;
        Socket connectSocket = null;

        connectSocket = new Socket(proxyAddress, proxyPort);

        if (connectSocket == null) return null;

        OutputStream outStream = connectSocket.getOutputStream();
        InputStream inStream = connectSocket.getInputStream();

            // version info
        dataBuffer[nWritten++] = VERSION;

            // see how many authentication methods we support...
        dataBuffer[nWritten++] = (byte)((1 + authMethods.size()) & 0xff);
        dataBuffer[nWritten++] = 0;      // no authentication required
        for (int i = 0, s = authMethods.size();i < s;i++)
        {
            dataBuffer[nWritten++] = ((SocksAuthMethod)authMethods.elementAt(i)).getID();
        }
        outStream.write(dataBuffer, 0, nWritten);

            // now see what the reply from the server is and process it...
        nRead = inStream.read(dataBuffer, 0, 2);
        System.out.println("Read: " + nRead + ", " + dataBuffer[0] + ", " + dataBuffer[1]);

            // see if we need method depandant
            // sub-negotiation/authentication...
        if (dataBuffer[1] == 0xff)
        {
                // then no authentication was suitable...
            return null;
        } else if (dataBuffer[1] != 0)
        {
            throw new Exception("Authentication to be implemented...");
        }

            // now send back a request to the server...
        nWritten = fillDataBuffer(dataBuffer, CONNECT, address, port, outStream);

            // some debug printing
        for (int i = 0;i < nWritten;i++)
        {
            if (i > 0) System.out.print(", ");
            System.out.print("" + (0x000000ff & dataBuffer[i]));
        }
        int ver = inStream.read();

        System.out.println("Ver: " + ver);
        if (ver != VERSION)
        {
            System.out.println("Version error in Response Expected: 0x05, Received: " + ver);
        }

        int requestReply = inStream.read();

        if (requestReply != REQUEST_SUCCEEDED )
        {
            throw new Socks5Exception("Request Failed: " + requestReply, requestReply);
        }

        if (inStream.read() != RESERVED)
        {
            System.out.println("Reserved Field not set to " + RESERVED + ".");
        }

        String newHost = readAddress(inStream);
        int newPort = ((inStream.read() & 0xff) << 8) |
                       (inStream.read() & 0xff);

        System.out.println("host, port = " + newHost + ", " + newPort);

            // now send a bind request...
        if (bindSocket)
        {
            nWritten = fillDataBuffer(dataBuffer, BIND, address, port, outStream);
        }

        System.out.println("Socks socket connection established.");
        
        return connectSocket;//new Socket(newHost, newPort);
    }

        /**
         * Fills the data buffer with the specific
         * request message
         */
    protected static int fillDataBuffer(byte dataBuffer[], byte request,
                                        String address, int port,
                                        OutputStream outStream)
        throws Exception
    {
        int nWritten = 0;
        dataBuffer[nWritten++] = VERSION;
        dataBuffer[nWritten++] = request;
        dataBuffer[nWritten++] = RESERVED;
        nWritten += writeAddressToBuffer(address, dataBuffer, nWritten);

        dataBuffer[nWritten++] = (byte)((port >> 8) & 0xff);
        dataBuffer[nWritten++] = (byte)(port & 0xff);

        outStream.write(dataBuffer, 0, nWritten);
        return nWritten;
    }

        /**
         * Reads the input stream and determines the type
         * of address.
         */
    protected static String readAddress(InputStream inStream) throws Exception
    {
        int addrType = inStream.read();
        String out = "";

        if (addrType == IP4_HOST)
        {
            out = (inStream.read() & 0xff) + "." + 
                  (inStream.read() & 0xff) + "." + 
                  (inStream.read() & 0xff) + "." + 
                  (inStream.read() & 0xff);
        } else if (addrType == IP6_HOST)
        {
            throw new Exception("IP6 to be done...");
        } else
        {
            char ch = (char)(inStream.read() & 0xff);
            while (ch != 0)
            {
                out += ch;
                ch = (char)(inStream.read() & 0xff);
            }
        }
        System.out.println("Add = " + out);
        return out;
    }

        /**
         * Writes the address to the request buffer.
         */
    protected static int writeAddressToBuffer(String address, byte buffer[], int offset)
    {
        boolean hasDots = address.indexOf('.') >= 0;
        boolean hasColons = address.indexOf(':') >= 0;
        int pos = offset;

            // means we are writing an IPv6 host
        if (hasColons && !hasDots) 
        {
            StringTokenizer tokens = new StringTokenizer(address, ":", false);
            buffer[pos++] = IP6_HOST;
            while (tokens.hasMoreTokens())
            {
                int val = Integer.parseInt(tokens.nextToken(), 16);
                buffer[pos++] = (byte)((val >> 8) & 0xff);
                buffer[pos++] = (byte)(val & 0xff);
            }
            return pos - offset;
        }
        
            // check if its an IP4 address
        if (hasDots)
        {
            int nDots = 0;
            int nNonDigs = 0;
            for (int i = 0, l = address.length(); i < l;i++)
            {
                char ch = address.charAt(i);
                if (ch == '.') nDots++;
                else if (ch < '0' || ch > '9') nNonDigs++;
            }

                // then we have IP4
            if (nDots == 3 && nNonDigs == 0)
            {
                buffer[pos++] = IP4_HOST;
                StringTokenizer tokens = new StringTokenizer(address, ".", false);
                while (tokens.hasMoreTokens())
                {
                    buffer[pos++] =
                        (byte)(Integer.parseInt(tokens.nextToken()) & 0xff);
                }
                
                return pos - offset;
            }
        }

            // if all else fails write the host as it is...
        buffer[pos++] = DOMAIN_HOST;
        System.out.println("Writing as Domain Host.");
        buffer[pos++] = (byte)address.length();
        for (int i = 0, l = address.length(); i < l;i++)
        {
            buffer[pos++] = (byte)(address.charAt(i) & 0xff);
        }
        return pos - offset;
    }
}
