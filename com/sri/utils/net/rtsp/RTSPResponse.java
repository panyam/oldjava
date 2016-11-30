
package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;

/**
 * An object representing an RTSP request.
 */
public class RTSPResponse extends RTSPMessage
{
        /**
         * Version of the response.
         */
    protected String version = "1.1";

        /**
         * The status code.
         */
    protected String statusCode = "xxx";

        /**
         * The reason phrase.
         */
    protected String reasonPhrase = "";

        /**
         * Constructor.
         */
    public RTSPResponse(String version, String statusCode, String reasonPhrase)
    {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    protected void parseStatusLine(String line)
        throws Exception
    {
        int spIndex = line.indexOf(' ');

        if (spIndex < 0)
        {
            throw new Exception("Invalid Status Line");
        }

        this.version = line.substring(0, spIndex);
        line = line.substring(spIndex);

        if ((spIndex = line.indexOf(' ')) < 0)
        {
            throw new Exception("Invalid Status Line");
        }

                // For more info on the status code,
                // refer to RFC 2326, section 7.1.1
        this.statusCode = line.substring(0, spIndex);
        this.reasonPhrase = line.substring(spIndex);

        System.out.println("Status line = " + version + ", " + statusCode + ", " + reasonPhrase);
    }

        /**
         * Returns the status code.
         */
    public String getStatusCode()
    {
        return statusCode;
    }

        /**
         * String representation of the header.
         */
    public String getHeaderString()
    {
        String str = version + " " + statusCode + " " + reasonPhrase + "\r\n";
        str += super.getHeaderString();
        return str;
    }
}
