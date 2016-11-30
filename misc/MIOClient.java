
import com.sri.utils.net.*;
import com.sri.utils.net.rtsp.*;

import java.io.*;
import java.net.*;

public class MIOClient implements RTSPClientListener
{
        /**
         * The current acknowledgement number.
         */
    int ackNumber = 1;
    Client rtspClient;
    String hostName;
    String fileName;
    int port;

        /**
         * Constructor.
         */
    public MIOClient(Client rtspClient, String host, String file, int port)
    {
        this.rtspClient = rtspClient;
        if (rtspClient != null) rtspClient.setClientListener(this);
        this.hostName = host;
        this.fileName = file;
        this.port = port;
    }

        /**
         * Processes a request from the server.
         */
    public synchronized void processRequest(RTSPRequest request)
                throws Exception
    {
            // other see waht we have here...
        System.out.println(request.getHeaderString());
    }

        /**
         * Processes a response from the server.
         */
    public synchronized void processResponse(RTSPRequest request,
                                             RTSPResponse response)
                throws Exception
    {
            // other see waht we have here...
        System.out.println(response.getHeaderString());

            // the first request is the OPTIONS request sent by the server.
            // the response will be one with the same seq number and with a
            // realchallenge1 header.  Now we compute the difficulty is to
            // compute the second challenge.
        if (response.cSeq == 1)
        {

            String code = response.getStatusCode();

                // check for a valid status code...
            if (code.charAt(0) != '2') return ;

            String realChallenge1 = response.getHeader("realchallenge1");
            System.out.println("Real Challenge 1 = " + realChallenge1);

                // compute the challenge here..
            String realChallenge2 = getRealChallenge(realChallenge1);
            String sdNumber = "8dda5e7a";

                // we may have to send a "DESCRIBE" request, but not as
                // yet.
            RTSPRequest setupRequest = new RTSPRequest("SETUP", 
                            "rtsp://" + hostName + ":" + port + "/" +
                                        fileName + "/streamid=0");

            setupRequest.setHeader("CSeq", "" + ackNumber++);
            setupRequest.setHeader("RealChallenge2", realChallenge2 + ", sd=" + sdNumber);
            setupRequest.setHeader("Transport",
                                        "x-pn-tng/tcp;mode=play,x-real-rdt/tcp;" + 
                                        "mode=play,rtp/avp/tcp;unicast;mode=play");

                // not sure if we need to send these two
            /*
            setupRequest.setHeader("If-Match", "1953161729-1");
            setupRequest.setHeader("Cookie", "cbid=fffggmdlgjcfkldmeooompgqorjrktlufkdgkidldjifllplqnmrnunqmoonqtnuefjgdmcl");
            */

            rtspClient.sendRequest(setupRequest);
            return ;
        }
    }

        /**
         * Calculate the response for a given challenge.
         */
    protected String getRealChallenge(String challenge)
    {
        return "a55b1f81ffe8c89af571319b8e2fdf45";
    }

        /**
         * Do the work.
         *
         * Looking at the packet sniffing log, this is what
         * we need to do.
         *
         */
    public void ripFile() throws Exception
    {
        if (rtspClient == null) return ;

        System.out.println("\nBeginning Ripper Process.....");
            // STEP 1: Options request from the client to the server
            // Basically we copy the request as it is from the sniffer
            // log!!!
        sendOptionsRequest();

            // now we just wait for the response and take it from there...
    }

        /** 
         * Send an options request.
         */
    protected void sendOptionsRequest() throws Exception
    {
            //
        RTSPRequest optRequest = new RTSPRequest("OPTIONS",
                                                 "rtsp://" + hostName +
                                                      ":" + port);
        
        optRequest.setHeader("CSeq", "" + ackNumber++);
        optRequest.setHeader("User-Agent",
                             "RealMedia Player Version 6.0.9.1762 (win32)");
        optRequest.setHeader("ClientChallenge",
                             "cef0633a37c1639c8a2d25a4cb6aaf5a");
        optRequest.setHeader("ClientID", 
                             "WinNT_5.0_6.0.11.853_RealPlayer_RN10PD_en-US_686");
        optRequest.setHeader("CompanyID", "0KTcexu8s928XIyPQUrCjg==");
        optRequest.setHeader("GUID", "00000000-0000-0000-0000-000000000000");

            // for now let us not include this header...
            // 
        //optRequest.setHeader("PlayerStarttime", "[20/01/2003:08:18:33 10:00]");
        optRequest.setHeader("Pragma", "initiate-session");
        optRequest.setHeader("RegionData", "0");
        rtspClient.sendRequest(optRequest);
    }




    public static void main(String args[]) throws Exception
    {
        String userAgentString = "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)";
        String songCode = args[0];
        String magicNumber = "asungjnfvoigngugn76lknfgoij87ml1fdmokj/" + songCode;
        String urlString = "http://www.musicindiaonline.com/xplayer/xload.cgi/" + magicNumber + "/";
        urlString = "http://www.musicindiaonline.com/music/r/asungjnfvoigngugn76lknfgoij87ml1fdmokj/" + songCode + "/rmfile.rm";

        URL url = new URL(urlString);
        URLConnection urlConn = url.openConnection();
        urlConn.setRequestProperty("User-Agent", userAgentString);
        urlConn.connect();

        DataInputStream din = new DataInputStream(urlConn.getInputStream());

        String line = din.readLine();

loop:
        while (line != null)
        {
            String lowerCase = line.toLowerCase();
            if (lowerCase.startsWith("rtsp://") ||
                lowerCase.startsWith("rtspu://"))
            {
                break loop;
            }

            line = din.readLine();
        }

        MIOClient mioClient = parseLocation(line);
        mioClient.ripFile();
    }

        /**
         * Parse the file name and return an RTSP client object.
         */
    protected static MIOClient parseLocation(String line) throws Exception
    {
        String afterQ = "";
        int port = Client.DEFAULT_PORT;
        line = line.trim();
        int stripLen = 0;
        boolean isUDP = false;
        String lowerCase = line.toLowerCase();

        if (lowerCase.startsWith("rtsp://"))
        {
            stripLen = 7;
        } else if (lowerCase.startsWith("rtspu://"))
        {
            isUDP = true;
            stripLen = 8;
        } else
        {
            System.out.println("LC = " + lowerCase);
            throw new Exception("Invalid Protocol.  Host must begin " +
                                "with \"rtspu\"");
        }

            // strip of the initial rtsp:// or rtspu://
        line = line.substring(stripLen);

            // find the index where the fileName starts
        int slashIndex = line.indexOf('/');

        String hostPart = line;
        String filePart = "";

            // seperate out the host and the filename parts
            // in the URI
        if (slashIndex > 0)
        {
            hostPart = line.substring(0, slashIndex);
            filePart = line.substring(slashIndex + 1);
        }

        int colonIndex = hostPart.indexOf(':');
        if (colonIndex > 0)
        {
            port = Integer.parseInt(hostPart.substring(colonIndex));
            hostPart = hostPart.substring(0, colonIndex);
        }

            // now remove the parameters which will be after the "?"
        int qMarkIndex = filePart.indexOf("?");
        if (qMarkIndex > 0)
        {
            afterQ = filePart.substring(qMarkIndex + 1);
            filePart = filePart.substring(0, qMarkIndex);
        }

        System.out.println("Host: " + hostPart);
        System.out.println("File: " + filePart);
        System.out.println("Port: " + port);
        System.out.println("Parameters: " + afterQ);

        Client rtspClient = null;
        if (isUDP)
        {
            rtspClient = new UDPClient(hostPart, filePart, port);
        } else
        {
            rtspClient = new TCPClient(hostPart, filePart, port,
                                 new DirectSocketProducer());
        }

            // after the connection, we do our work
        if (rtspClient != null) rtspClient.connect();

        return new MIOClient(rtspClient, hostPart, filePart, port);
    }
}
