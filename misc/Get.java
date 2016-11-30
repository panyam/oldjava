
import java.io.*;
import java.net.*;
import java.util.*;
import com.sri.utils.net.*;
import com.sri.utils.net.proxy.*;

//
// Here are the steps to suck files out of MusicIndiaOnline.com
//
// First find the 10 Letter code, from the website...

// proxy should be netcache.ntcif.telstra.com.au
public class Get
{
    public static void socketGet(String args[])
       throws Exception
    {
        String songName = args[0];
        String proxyHost = args[2];

        System.out.println("Proxy = " + proxyHost);
        System.out.println("Song = " + songName);
        System.out.println("Outfile = " + args[1]);

        SocketProducer sp = new DirectSocketProducer();
        Socket socket = sp.getSocket(proxyHost, 80, false);

        //String request = "GET http://www.musicindiaonline.com/xplayer/xload.cgi/S2bVv1TLjwiqfMAaoFRI4yNY9/" + songName + " HTTP/1.0" + "\r\n" + 
        String request = "GET http://www.musicindiaonline.com/music/r/S2bVv1TLjwiqfMAaoFRI4yNY9/WW000J0F04/rmfile.rm HTTP/1.0" + "\r\n" + 
                         "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*" + "\r\n" +
                         "Accept-Language: en-au" +  "\r\n" +
                         "Cookie: AdPopUR18784=yes; tridentdet=EKouXWze0FRyhrCYlpAXCtXqIoLukG5GUFqEpJLJtLMywdxHoetulES3bUMgIJvI8Mo%0A; tridentpref=AvhkAQ%0A; USERNAME=303c5721413b47455c2c3360522d433c572c4330562c3060600a" +  "\r\n" +
                         "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; CLS4; (R1 1.1))" +  "\r\n" +
                         "Host: www.musicindiaonline.com" + "\r\n" +
                         "Proxy-Connection: Keep-Alive" +  "\r\n" +
                         "Proxy-Authorization: Basic YzkyMDg4NDpMZW5hMjM5Nw==" + "\r\n" +
                         "" + "\r\n";
        DataInputStream din = new DataInputStream(socket.getInputStream());
        OutputStream sockOut = socket.getOutputStream();
        DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(args[1]));

        sockOut.write(request.getBytes());

        byte bytes[] = new byte[4096];
        while (true)
        {
            int nRead = din.read(bytes);
            if (nRead < 0) return ;
            System.out.println("Read: " + nRead + " bytes");
            fileOut.write(bytes, 0, nRead);
        }
    }

    public static void normalGet(String args[])
       throws Exception
    {
        URL url = new URL(args[0]);
        DataInputStream din = new DataInputStream(url.openStream());
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(args[1]));
        byte bytes[] = new byte[4096];
        while (true)
        {
            int nRead = din.read(bytes);
            if (nRead < 0) return ;
            System.out.println("Read: " + nRead + " bytes");
            dout.write(bytes, 0, nRead);
        }
    }

    public static void main(String args[]) throws Exception
    {
        normalGet(args);
        //socketGet(args);
    }
}
