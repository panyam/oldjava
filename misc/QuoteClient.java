
import java.io.*;
import java.net.*;
import java.util.*;
import com.sri.utils.net.*;
import com.sri.utils.net.proxy.*;

/**
 * A sample client to the Quote Server.
 */
public class QuoteClient
{
    public static void main(String args[])
    {
        int port = 1234;
        String host = "";
        boolean asTCP = true;

        if (args.length < 3)
        {
            System.err.println("Usage: QuoteServer host port asTCP:(true or false)");
            System.exit(0);
        }

        host = args[0];
        port = Integer.parseInt(args[1]);
        asTCP = !args[2].equalsIgnoreCase("false");

        try
        {
            if (asTCP)
            {
                SocketProducer sp = new Socks5ProxyClient(
                                            "socks-gw.fwall.telstra.com.au",
                                            1080);
                Socket clientSocket = sp.getSocket(host, port, false);
                String line = (new DataInputStream(clientSocket.getInputStream())).readLine();
                System.out.println("Received: " + line);
            } else
            {
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
