package com.sri.utils.net;

import java.util.*;
import java.net.*;
import java.io.*;
import com.sri.utils.adt.*;

/**
 * A class for a MIME Parsing.
 * Refer to RFC 1341 for details.
 * This class is used by all protocols that are based on MIME
 * like SMTP, HTTP and RTSP and so on.
 * What is required is a way of conviniently obtaining the various
 * parts of the MIME header part of the PDU.
 *
 * @Auther: Sriram Panyam
 * @Data: 04/01/2003
 */
public class MimeParser
{
        /**
         * Constructor.
         */
    public MimeParser()
    {
    }

        /**
         * Takes an input stream and parses the mime information.
         * Forms the basis of most mime based protocols like
         * HTTP and RTSP and so on.
         */
    public void parseInput(InputStream in) throws Exception
    {
        
    }
}
