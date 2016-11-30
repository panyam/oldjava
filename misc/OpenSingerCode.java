
import java.io.*;
import java.net.*;

public class OpenSingerCode
{
    public static void main(String args[]) throws Exception
    {
        String singerCode = args[0];
        String urlString = "http://www.musicindiaonline.com/music/l/" + singerCode;
        URL url = new URL(urlString);
        URLConnection urlConn = url.openConnection();
        System.out.println("User-Agent = " + urlConn.getRequestProperty("User-Agent"));
        urlConn.setRequestProperty("User-Agent", "Internet Explorer");
        System.out.println("User-Agent = " + urlConn.getRequestProperty("User-Agent"));

        DataInputStream din = new DataInputStream(urlConn.getInputStream());

        String line = din.readLine();
        while (line != null)
        {
            System.out.println(line);
            line = din.readLine();
        }
        //String songCode = args[0];
    }
}
