
package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;

/**
 * An object storing RTSP header name and value pairs.
 */
public class RTSPHeader
{
    public String headerName = "";
    public String headerValue = "";

        /**
         * Default constructor.
         */
    public RTSPHeader()
    {
    }

        /**
         * constructor.
         */
    public RTSPHeader(String name, String value)
    {
        this.headerName = name;
        this.headerValue = value;
    }
}
