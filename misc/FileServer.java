
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Basically does basic file operations.
 * Similar to a ftp server but very simple runs
 * on only two ports.
 */
public class FileServer implements Runnable
{
    public final static int DEFAULT_CMD_PORT = 2222;
    public final static int DEFAULT_FILE_PORT = 2223;

    Thread ourThread = null;
    boolean isRunning = false, threadStopped = false;
    int commandPort = DEFAULT_CMD_PORT;
    int filePort = DEFAULT_FILE_PORT;

        /**
         * The socket for accepting commands.
         */
    ServerSocket ftSocket, commandSocket = null;

        /**
         * Constructor.
         */
    public FileServer() throws Exception
    {
        this(DEFAULT_CMD_PORT, DEFAULT_FILE_PORT);
    }

        /**
         * Constructor.
         */
    public FileServer(int cmdPrt, int flPrt) throws Exception
    {
        this.commandPort = cmdPrt;
        this.filePort = flPrt;

        commandSocket = new ServerSocket(commandPort);
        ftSocket = new ServerSocket(filePort);
    }

        /**
         * Start the file server.
         */
    public synchronized void startServer() throws Exception
    {
        if (isRunning) 
        {
            if (threadStopped)
            {
                while (threadStopped) { wait(); }
            } else return ;
        }

        ourThread = null;
        ourThread = new Thread(this);
        isRunning = true;

        ourThread.start();
    }

        /**
         * Stop the file server.
         */
    public synchronized void stopServer() throws Exception
    {
        if (!isRunning) return;

        threadStopped = true;
        notifyAll();

        while (isRunning) wait();

        ourThread = null;
        threadStopped = isRunning = false;
    }

        /**
         * Main thread method.
         */
    public void run()
    {
        Socket clientSocket = null;
        try
        {
            commandSocket = new ServerSocket(commandPort);
        } catch (Exception e)
        {
            e.printStackTrace();
            return ;
        }

loop:
        try
        {
            while (isRunning && !threadStopped)
            {
                // accept a connection
                clientSocket = commandSocket.accept();

                if (threadStopped) break loop;

                CommandServer cmdServer = new CommandServer(clientSocket);

                cmdServer.startServer();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        threadStopped = isRunning = false;
        notifyAll();
    }

    public class CommandServer implements Runnable
    {
        Thread ourThread = null;
        boolean isRunning = false, threadStopped = false;
        Socket clientSocket = null;
        DataInputStream inStream = null;
        DataOutputStream outStream = null;

            /**
             * Constructor.
             */
        public CommandServer(Socket clientSocket)
            throws Exception
        {
            inStream = new DataInputStream(clientSocket.getInputStream());
            outStream = new DataOutputStream(clientSocket.getOutputStream());
        }

            /**
             * Start the file server.
             */
        public synchronized void startServer() throws Exception
        {
            if (isRunning) 
            {
                if (threadStopped)
                {
                    while (threadStopped) { wait(); }
                } else return ;
            }

            ourThread = null;
            ourThread = new Thread(this);
            isRunning = true;

            ourThread.start();
        }

            /**
             * Stop the file server.
             */
        public synchronized void stopServer() throws Exception
        {
            if (!isRunning) return;

            threadStopped = true;
            notifyAll();

            while (isRunning) wait();

            ourThread = null;
            threadStopped = isRunning = false;
        }
        
            /**
             * Main thread method.
             */
        public void run()
        {
            Command command = null;
loop:
            try
            {
                while (isRunning && !threadStopped)
                {
                    command = getCommand();

                    if (threadStopped) break loop;
                    processCommand(command);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            isRunning = threadStopped = true;
        }

            /**
             * Get a command from the input stream of the
             * socket.
             * Following commands are supported:
             * get file     -       For getting files
             * put file     -       For Putting files
             * ls/dir       -       For listing contents of a dir
             * cd dir       -       Changing to a directory
             * pwd          -       Showing current directory
             * mkdir dir    -       for creating a directory
             * ren f1 f2    -       Renaming f1 to f2
             * rm opt file  -       for deleting/removing files
             *                      opt is the set of options in unix style
             *                      -r, -R for recursive (only if a directory)
             *                      -i, -I prompt before deleting each file
             *                      if rm is used on a directory, its
             *                      contents (only files) will be removed.
             *                      To remove the directory itself -R or -r
             *                      should be used.
             *                      More than one file can be specified
             *                      files are typical unix style
             *
             * The client may support other commands but these commands
             * can only be understood by the server... as only these
             * relate to server side files
             */
        public Command getCommand() throws Exception
        {
            Command out = new Command();

            String line = inStream.readLine();

            return out;
        }

        public void processCommand(Command cmd) throws Exception
        {
        }

        class Command
        {
                /**
                 * The command.
                 */
            String cmd;

                /**
                 * The list of parameters.
                 */
            Vector args = new Vector();
        }
    }
}
