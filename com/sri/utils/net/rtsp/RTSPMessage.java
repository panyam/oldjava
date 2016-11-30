
package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;

/**
 * An object representing an RTSP message.
 * This could be either a request or a response.
 * Basically corresponds to a packet at the transport layer.
 */
public abstract class RTSPMessage
{
        /**
         * Holds the header fields and values.
         */
    protected Hashtable fieldTable = new Hashtable();

        /**
         * The sequence on each packet.
         * This will also be one of the fields, but
         * since this is a required header, we will make 
         * it a class member.
         */
    public int cSeq = 0;

        /**
         * Length of the content.
         */
    public int contentLength = 0;

        /**
         * The message list.
         */
    protected byte[] messageBody = null;

    public String getHeader(String header)
    {
        String lcH = header.toLowerCase();

        if (lcH.equals("cseq"))
        {
            return ("" + cSeq);
        } else if (lcH.equals("content-length"))
        {
            return "" + contentLength;
        }

                // then replace the value
        RTSPHeader rtspHeader = (RTSPHeader)fieldTable.get(lcH);
        if (rtspHeader != null)
        {
            return rtspHeader.headerValue;
        }
        return null;
    }

        /**
         * Add a new header to our list.
         */
    public void setHeader(String header, String value)
    {
                // not sure if headers are case insensitve or
                // case sensitive
        String lcH = header.toLowerCase();

        if (lcH.equals("cseq"))
        {
            cSeq = Integer.parseInt(value);
            return ;
        } else if (lcH.equals("content-length"))
        {
            contentLength = Integer.parseInt(value);
            return ;
        }

                // then replace the value
        RTSPHeader rtspHeader = (RTSPHeader)fieldTable.get(lcH);
        if (rtspHeader != null)
        {
            rtspHeader.headerValue = value;
        } else
        {
            rtspHeader = new RTSPHeader(header, value);
            fieldTable.put(lcH, rtspHeader);
        }
    }

        /**
         * Add the actual message.
         * If message was already added, then the message 
         * is appended to the existing message body.
         */
    public void setMessage(byte list[])
    {
        messageBody = list;
        contentLength = messageBody.length;
    }

        /**
         * Get the message byte list.
         */
    public byte[] getMessageBody()
    {
        return messageBody;
    }

        /**
         * String representation of the header.
         */
    public String getHeaderString()
    {
        String str;
        str  = "CSeq:" + cSeq + "\r\n";

        if (contentLength > 0)
        {
            str += "Content-Length:" + contentLength + "\r\n";
        }

        for (Enumeration en = fieldTable.elements();en.hasMoreElements();)
        {
            RTSPHeader header = (RTSPHeader)en.nextElement();
            str += header.headerName + ": " + header.headerValue + "\r\n";
        }
        return str + "\r\n";
    }

        /**
         * Write the message to an output stream.
         */
    public void writeMessage(OutputStream oStream) throws Exception
    {
        String outString = getHeaderString();

        System.out.println(outString);
        oStream.write(outString.getBytes());
        oStream.write(messageBody, 0, contentLength);
    }

    public static RTSPMessage readMessage(InputStream iStream) throws Exception
    {
        String []firstLineWords = new String[3];

        String line = RTSPUtils.getUptoCRLF(iStream);

            // check if the first line is a response
            // line or a request line
            // the first line is the version and status code line...
            // A response's first line has VERSION CODE Phrase
            // where as a request has METHOD URI VERSION
        StringTokenizer tokens = new StringTokenizer(line, " ", false);

        if (tokens.countTokens() != 3)
        {
            throw new Exception("Invalid message line: " + line);
        }

        for (int i = 0;i < 3;i++) firstLineWords[i] = tokens.nextToken();

        RTSPMessage message = null;

            // check if the second word is a status code 
            // if it is then this is a response message so parse
            // according...
        if (Character.isDigit(firstLineWords[1].charAt(0)) &&
            Character.isDigit(firstLineWords[1].charAt(1)) &&
            Character.isDigit(firstLineWords[1].charAt(2)))
        {
            message = new RTSPResponse(firstLineWords[0],
                                       firstLineWords[1],
                                       firstLineWords[2]);

        } else
        {
            message = new RTSPRequest(firstLineWords[0],
                                      firstLineWords[1],
                                      firstLineWords[2]);
        }

        line = RTSPUtils.getUptoCRLF(iStream);

            // read each line and parse the headers...
            // stop when we get to an empty line,
            // which signals the end of the headers
            // and the begin of the message body...
        int l = 0;
        while (line != "")
        {
            parseHeaderLine(message, line);

            line = RTSPUtils.getUptoCRLF(iStream);
        }

            // what ever is now remaining is basically
            // how do we know how many bytes are available?
            // This is denoted by the content-length header
            // we can be sure that this will be set because, 
            // if this field was missing
            // TODO:: This bit is a bit inefficient as we can
            // use a buffered stream or other ways but for 
            // now the message is reading only byte by byte.
        if (message.messageBody == null || 
            message.messageBody.length < message.contentLength)
        {
            message.messageBody = new byte[message.contentLength + 1];
        }

        System.out.println("Content Length: " + message.contentLength);
        for (int i = 0;i < message.contentLength;i++)
        {
            message.messageBody[i] = (byte)iStream.read();
        }

        System.out.println("Message: \n" + new String(message.messageBody));
        return message;
    }

        /**
         * Given a header line, parses it.
         */
    protected static void parseHeaderLine(RTSPMessage message, String line)
        throws Exception
    {
        int colonIndex = line.indexOf(':');

        if (colonIndex < 0)
        {
             throw new Exception("Invalid Header Line: " + line);
        }

        String header = line.substring(0, colonIndex).trim();
        String value = line.substring(colonIndex + 1).trim();

        message.setHeader(header, value);
    }
}
