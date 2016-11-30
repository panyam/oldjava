package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;

/**
 * An objec storing constants for an RTSP connection
 */
public class RTSPHeaders
{
    protected final static byte GENERAL_HEADER = 0;
    protected final static byte REQUEST_HEADER = 1;
    protected final static byte RESPONSE_HEADER = 2;
    protected final static byte ENTITY_HEADER = 3;

        /**
         * Set of request headers.
         */
    protected final static String requestHeaders[] = 
    {
        "Accept",
        "Accept-Encoding",
        "Accept-Language",
        "Authorization",
        "From",
        "If-Modified-Since",
        "Range",
        "Referer",
        "User-Agent",
    };

        /**
         * Set of response headers.
         */
    protected final static String responseHeaders[] = 
    {
        "Location",
        "Proxy-Authenticate",
        "Public",
        "Retry-After",
        "Server",
        "Vary",
        "WWW-Authenticate",
    };

        /**
         * Set of entity headers.
         */
    protected final static String entityHeaders[] = 
    {
        "Allow",
        "Content-Base",
        "Content-Encoding",
        "Content-Language",
        "Content-Length",
        "Content-Location",
        "Content-Type",
        "Expires",
        "Last-Modified",
    };

        /**
         * Set of general headers.
         */
    protected final static String generalHeaders[] = 
    {
        "Cache-Control",
        "Conection",
        "Date",
        "Via"
    };

        /**
         * Tells if key is in list.
         */
    protected static boolean inList(String list[], String key)
    {
        for (int i  = 0;i < list.length;i++)
        {
            if (list[i].equals(key)) return true;
        }
        return false;
    }

    public static int getHeaderType(String header)
    {
        if (inList(requestHeaders, header)) return REQUEST_HEADER;
        else if (inList(generalHeaders, header)) return GENERAL_HEADER;
        else if (inList(entityHeaders, header)) return ENTITY_HEADER;
        else if (inList(responseHeaders, header)) return RESPONSE_HEADER;
        return -1;
    }
}
