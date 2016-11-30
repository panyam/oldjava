
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
public class RTSPUtils
{
        /**
         * Get everything upto any of the following patterns:
         * \r\n
         * \r
         * \n
         */
    public static String getUptoCRLF(InputStream instream)
        throws Exception
    {
        String line = "";
        int crlfPos = 0;
        int currPos = 0;
        char currByte = (char)(instream.read() & 0xff);
        //System.out.println("Currbyte: " + (int)currByte + ".");

loop:
        while (true)
        {
            /*if (currByte == '\r')
            {
                instream.mark(1);
                currByte = (char)(instream.read() & 0xff);
        System.out.println("Currbyte: " + (int)currByte + ".");
                if (currByte != '\n')
                {
                    instream.reset();
                }
                break loop;
            } else*/
            if (currByte == '\n')
            {
                break loop;
            }
            if (currByte != '\r')
                line += currByte;
            currByte = (char)(instream.read() & 0xff);
        //System.out.println("Currbyte: " + currByte + ".");
        }
        return line;
    }
}
