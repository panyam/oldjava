
package com.sri.utils.net.rtsp;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.net.*;
import com.sri.utils.adt.*;

/**
 * A interface for processing responses and requests received by the
 * client.
 *
 * @Auther: Sriram Panyam
 * @Data: 18/1/2003
 */
public interface RTSPClientListener
{
    public void processResponse(RTSPRequest request, RTSPResponse response)
                                        throws Exception;
    public void processRequest(RTSPRequest request)
                                        throws Exception;
}
