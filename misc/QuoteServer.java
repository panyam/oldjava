import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A sample server for listening as a UDP or a TCP server on a 
 * specified port.  Can use this to test our firewall and so on.
 */
public class QuoteServer implements Runnable
{
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    Thread ourThread = null;

        /**
         * Port on which the server listens to.
         */
    int port;

        /**
         * Tells if the server was killed
         */
    boolean serverKilled = false;

        /**
         * Tells if the server is running
         */
    boolean serverRunning = false;

        /**
         * Are we running as tcp or udp
         */
    boolean asTCP;

        /**
         * Constructor
         */
    public QuoteServer(int port, boolean asTCP)
    {
        this.port = port;
        this.asTCP = asTCP;
    }

        /**
         * Starts the server
         */
    public void startServer() throws Exception
    {
        synchronized (this)
        {
            if (serverRunning && !serverKilled) return ;
            stopServer();
            ourThread = new Thread(this);
            ourThread.start();
        }
    }

        /**
         * Kills the server
         */
    public void stopServer() throws Exception
    {
        synchronized(this)
        {
            if (!serverRunning  || serverKilled) return ;

            serverRunning = false;
            serverKilled = true;
            if (serverSocket  != null) serverSocket.close();
            if (clientSocket  != null) clientSocket.close();
            notifyAll();
        }
    }

        /**
         * Sends a random text message on the output stream
         */
    public void sendRandomMessage(DataOutputStream dout)
    {
        int i = (int)(Math.random() * 1000000);
        try
        {
            System.out.println("Sending Message: " + i);
            dout.writeBytes("" + i + "\n");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

        /**
         * Main thread loop
         */
    public void run()
    {
        DataOutputStream dout;
        serverRunning = true;
        serverKilled = false;
        try
        {
            if (asTCP)
            {
                serverSocket = new ServerSocket(port);
                while (serverRunning && !serverKilled)
                {
                        // wait for a client
                    clientSocket = serverSocket.accept();

                    System.out.println("Recieved: Host = " + clientSocket.getInetAddress());
                    System.out.println("        : Local Port = " + clientSocket.getLocalPort());
                    System.out.println("        : Remote Port = " + clientSocket.getPort());

                        // send down a message
                    dout = new DataOutputStream(clientSocket.getOutputStream());
                    sendRandomMessage(dout);
                    dout.close();

                    clientSocket.close();
                }
            } else
            {
            }
            serverRunning = false;
            serverKilled = false;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception
    {
        int port = 1234;
        boolean asTCP = true;
        int nArgs = 0;

        if (args.length < 2)
        {
            System.err.println("Usage: QuoteServer port asTCP:(true or false)");
            System.exit(0);
        }

        port = Integer.parseInt(args[0]);
        asTCP = !args[1].equalsIgnoreCase("false");

        (new QuoteServer(port, asTCP)).startServer();
    }
}
