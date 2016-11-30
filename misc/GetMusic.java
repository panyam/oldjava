import java.io.*;
import java.net.*;
import java.util.*;
import com.sri.utils.net.*;
import com.sri.utils.net.proxy.*;
import com.sri.utils.net.rtsp.*;

//
// Here are the steps to suck files out of MusicIndiaOnline.com
//
// First find the 10 Letter code, from the website...

// proxy should be netcache.ntcif.telstra.com.au
public class GetMusic
{
    static String songCode = "", outFile = "outfile";
    static String proxyHost = null;
    static int proxyPort = -1;

    static String webHost = null;
    static int webPort = -1;

    static SocketProducer socketProducer = null;
    static SocketProducer webSocketProducer = null;

    protected static void parseArguments(String args[])
    {
        for (int i = 0;i < args.length;i++)
        {
            if (args[i].equalsIgnoreCase("-song"))
            {
                songCode = args[++i];
            } else if (args[i].equalsIgnoreCase("-outfile"))
            {
                outFile = args[++i];
            } else if (args[i].equalsIgnoreCase("-proxyHost"))
            {
                proxyHost = args[++i];
            } else if (args[i].equalsIgnoreCase("-proxyPort"))
            {
                proxyPort = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-webHost"))
            {
                webHost = args[++i];
            } else if (args[i].equalsIgnoreCase("-webPort"))
            {
                webPort = Integer.parseInt(args[++i]);
            }
        }

        if (proxyPort > 0 && proxyHost != null)
        {
            socketProducer = new Socks5ProxyClient(proxyHost, proxyPort);
        } else
        {
            socketProducer = new DirectSocketProducer();
        }

        if (webHost == null || webPort < 0)
        {
            webPort = 80;
            webHost = "www.musicindiaonline.com";
        }
        //webSocketProducer = new DirectSocketProducer();
    }

        /**
         * Makes a HTTP Request and returns the input stream
         * stream from which the resposne can be read.
         */
    protected static ByteList makeHTTPRequest(String request)
        throws Exception
    {
        SocketProducer sp = new DirectSocketProducer();
        Socket socket = sp.getSocket(webHost, webPort, false);

        OutputStream sockOut = socket.getOutputStream();
        InputStream sockIn = socket.getInputStream();

                // send the request
        sockOut.write(request.getBytes());

                // now read the total response...
        byte list[] = new byte[1024];
        int pos = 0;

loop:
        while (true)
        {
            int nRead = sockIn.read(list, pos, 1024);
            if (nRead < 0) break loop;

            pos += nRead;

            if (list.length - pos < 1024)
            {
                byte list2[] = list;
                list = new byte[list.length + 1024];
                System.arraycopy(list2, 0, list, 0, pos);
            }

            System.out.println("Read: " + nRead + " bytes");
        }

            // good to go thru the whole response in here as well...
        return new ByteList(list, pos);
    }

    public static void main(String args[]) throws Exception
    {
        parseArguments(args);

        DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(outFile));

                // make the first request that opens up a new window 
                // with the player in it...
        String requestString = createRequest(songCode, webPort > 0, true);

        ByteList initialResponse = makeHTTPRequest(requestString);
        fileOut.write(initialResponse.list, 0, initialResponse.nBytes);

            // from this response, we need to look for the frameset
            // look for the line with "rtsp://"
        String rtspURI = initialResponse.getUptoCRLF();

        while (!rtspURI.startsWith("rtsp://"))
        {
            rtspURI = initialResponse.getUptoCRLF();
        }

        if (!rtspURI.startsWith("rtsp://"))
        {
            System.out.println("Invalid response.  Cannot find rtsp://");
        }

        System.out.println("Proxy = " + proxyHost);
        System.out.println("Song = " + songCode);
        System.out.println("Outfile = " + outFile);
        System.out.println("RTSP URI = " + rtspURI);

            // now use this rtsp connection to do the work...
        RTSPConnection rtspConn = new RTSPConnection(rtspURI, socketProducer);

        RTSPRequest rtspRequest = new RTSPRequest("SETUP", rtspURI);
        rtspRequest.addHeader("CSeq", "301");
        rtspRequest.addHeader("Transport", "RTP/AVP/TCP");

        //RTSPRequest rtspRequest = new RTSPRequest("PLAY", rtspURI);

        RTSPResponse rtspResponse = rtspConn.processRequest(rtspRequest);
    }

        /**
         * Makes a new request string.
         */
    protected static String createRequest(String songCode,
                                          boolean behindProxy,
                                          boolean asReal)
    {
        String firstLine = "Get ";

        if (behindProxy)
        {
            firstLine += "http://www.musicindiaonline.com";
        }

        if (asReal)
        {
            firstLine +=    ("/music/r/S2bVv1TLjwiqfMAaoFRI4yNY9/" +
                            songCode + "/rmfile.rm HTTP/1.0" +
                            "\r\n");
        }

        String rest =   "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*" + "\r\n" +
                        "Accept-Language: en-au" +  "\r\n" +
                        "Cookie: AdPopUR18784=yes; tridentdet=EKouXWze0FRyhrCYlpAXCtXqIoLukG5GUFqEpJLJtLMywdxHoetulES3bUMgIJvI8Mo%0A; tridentpref=AvhkAQ%0A; USERNAME=303c5721413b47455c2c3360522d433c572c4330562c3060600a" +  "\r\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; CLS4; (R1 1.1))" +  "\r\n" +
                        "Host: www.musicindiaonline.com" + "\r\n" +
                        "Proxy-Connection: Keep-Alive" +  "\r\n" +
                        "Proxy-Authorization: Basic YzkyMDg4NDpMZW5hMjM5Nw==" + "\r\n" +
                        "" + "\r\n";
        return firstLine + rest;
    }
}

    class ByteList
    {
        byte list[];
        int nBytes;
        int currPos = 0;

        public ByteList(byte list[], int nB)
        {
            this.list = list;
            this.nBytes = nB;
            currPos = 0;
        }

            /**
             * Get everything upto any of the following patters:
             * \r\n
             * \r
             * \n
             */
        protected String getUptoCRLF() throws Exception
        {
            String line = "";
            int crlfPos = 0;
            int markPos = currPos;
            char currByte = (char)(list[currPos++] & 0xff);

loop:
            while (true)
            {
                if (currByte == '\r')
                {
                    markPos = currPos;
                    currByte = (char)(list[currPos++] & 0xff);
                    if (currByte != '\n')
                    {
                        currPos = markPos;
                    }
                    break loop;
                } else if (currByte == '\n')
                {
                    break loop;
                }
                line += currByte;
                currByte = (char)(list[currPos++] & 0xff);
            }

            System.out.println("Read: " + line);
            return line;
        }
    }
