
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
public class GetRM
{
    public static void main(String args[]) throws Exception
    {
        String host = "rtsp://66.28.21.90/S2bVv1TLjwiqfMAaoFRI4yNY9/ra/carnatic/vocal/ariyakkudi/Aparama.rm?cloakport=\"80,554,7070\"";

        RTSPConnection rtspConn = new RTSPConnection(host,
                                    new Socks5ProxyClient(
                                        "socks-gw.fwall.telstra.com.au",
                                        1080));

        RTSPRequest request = new RTSPRequest("PLAY", host);

        RTSPResponse response = rtspConn.processRequest(request);
    }
}
