package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;

/**
 * An object representing an RTSP request.
 */
public class RTSPRequest extends RTSPMessage
{
    public final static byte OPTIONS_METHOD = 0;
    public final static byte DESCRIBE_METHOD = 1;
    public final static byte ANNOUNCE_METHOD = 2;
    public final static byte SETUP_METHOD = 3;
    public final static byte PLAY_METHOD = 4;
    public final static byte PAUSE_METHOD = 5;
    public final static byte TEARDOWN_METHOD = 6;
    public final static byte GET_PARAMETER_METHOD = 7;
    public final static byte SET_PARAMETER_METHOD = 8;
    public final static byte REDIRECT_METHOD = 9;
    public final static byte RECORD_METHOD = 10;
    public final static byte EXTENDED_METHOD = -1;

        /** 
         * The kind of code.
         */
    protected byte methodCode;

    protected String method;
    protected String uri;
    protected String rtspVersion;

        /**
         * Constructor.
         * The allowable methods are described in RFC 2326 
         * in section 6.1
         */
    public RTSPRequest(String method, String uri)
    {
        this(method, uri, Client.RTSP_VERSION);
    }

        /**
         * Constructor.
         * The allowable methods are described in RFC 2326 
         * in section 6.1
         */
    public RTSPRequest(String method, String uri, String rtspVersion)
    {
        setMethod(method);
        this.uri = uri;
        this.rtspVersion = rtspVersion;
    }

    public void setMethod(String method)
    {
        this.method = method;
        if (method.equalsIgnoreCase("options"))
            methodCode = OPTIONS_METHOD;
        else if (method.equalsIgnoreCase("describe"))
            methodCode = DESCRIBE_METHOD;
        else if (method.equalsIgnoreCase("play"))
            methodCode = PLAY_METHOD;
        else if (method.equalsIgnoreCase("pause"))
            methodCode = PAUSE_METHOD;
        else if (method.equalsIgnoreCase("teardown"))
            methodCode = TEARDOWN_METHOD;
        else if (method.equalsIgnoreCase("get_parameter"))
            methodCode = GET_PARAMETER_METHOD;
        else if (method.equalsIgnoreCase("set_parameter"))
            methodCode = SET_PARAMETER_METHOD;
        else if (method.equalsIgnoreCase("redirect"))
            methodCode = REDIRECT_METHOD;
        else if (method.equalsIgnoreCase("record"))
            methodCode = RECORD_METHOD;
        else if (method.equalsIgnoreCase("announce"))
            methodCode = ANNOUNCE_METHOD;
        else if (method.equalsIgnoreCase("setup"))
            methodCode = SETUP_METHOD;
        else if (method.equalsIgnoreCase("options"))
            methodCode = OPTIONS_METHOD;
        else methodCode = EXTENDED_METHOD;
    }

        /**
         * Returns the method code.
         */
    public byte getMethodCode()
    {
        return methodCode;
    }

        /**
         * String representation of the header.
         */
    public String getHeaderString()
    {
        String str = method + " " + uri + " " + Client.RTSP_VERSION + "\r\n";
        str += super.getHeaderString();
        return str;
    }
}
