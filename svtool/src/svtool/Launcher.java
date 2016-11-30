
package svtool;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * This ensures that all tools related to this product are launched part of
 * the same VM rather than spawning multiple VMs!!!  Allows for much much
 * more compact memory usage.
 *
 * Now we may want different applications to be launched by different VMs.
 * So the idea is that there can be two options. 
 *
 * One option is the launcher can directly launch an application.
 *
 * Second option is that an application that is aware of the existance of
 * the Launcher class can "request" to be loaded by this process.
 *
 * Also, this class, being a server, will allow communication between the
 * launcher and the launchee for things like querying how many processes
 * are there and so on.
 */
public class Launcher
{
        /**
         * Default function name that will be executed.
         */
    public final static String DEFAULT_FUNCTION_NAME = "main";

        /**
         * Default port where the launcher should be started on.
         */
    public final static int DEFAULT_APP_LAUNCHER_PORT = 4567;

        /**
         * THe port on which we listen for new launches.
         */
    protected Integer socketPort = null;

        /**
         * Tells how many programs are currently running.
         */
    protected static int runningPrograms = 0;

        /**
         * Default Constructor.
         */
    public Launcher()
    {
        this(DEFAULT_APP_LAUNCHER_PORT);
    }

        /**
         * Constructor.
         */
    public Launcher(int port)
    {
        this.socketPort = new Integer(port);
    }

        /**
         * Launch the class only from a request.
         *
         * This is called, when an application calls "Launcher.launch"
         * by itself rather than be called by the user.
         *
         * This would be useful, if the user does not know about
         * Launcher objects but the application knows about it.
         *
         * Note that there is also a "funcName" parameter.  This is
         * because, if the application calls the "launch" function, it may
         * most likely be from the class's "main" function.  In which case,
         * the main function should not be invoked again, which may result
         * in recursive calls.  However, if another function is called say
         * main2, with the same parameters then this recursion wont occur.
         *
         * However, most likely this will be the "main" method (thought not
         * necessarily).
         *
         * The arguments are just that - ONLY arguments that are passed to
         * the function.  These will be string arguemnts.
         */
    public synchronized void launch(String className,
                                    String funcName,
                                    String args[],
                                    int nArgs)
    {
        boolean launched = false;
        while (!launched)
        {
            System.out.println("Trying to launch: " + className);
            Socket s = findService();
            if (s != null)
            {
                System.out.println("Found service...  ");
                System.out.println("Sending Details for launch...");
                try
                {
                    OutputStream oStream = s.getOutputStream();

                        // write the name of the class....
                    System.out.println("Sending class name...");
                    byte[] bytes = className.getBytes();
                    oStream.write(bytes.length);
                    oStream.write(bytes);

                        // now write the name of the function to execute
                    System.out.println("Sending function name...");
                    bytes = funcName.getBytes();
                    oStream.write(bytes.length);
                    oStream.write(bytes);

                        // now write the argument count and 
                        // each of the arguments to the function
                        // Note that the arguments can ONLY be a
                        // list of Strings.
                    System.out.println("Sending " + nArgs + " arguments...");
                    oStream.write(nArgs);
                    for (int i = 0;i < args.length;i++)
                    {
                        bytes = args[i].getBytes();
                        oStream.write(bytes.length);
                        oStream.write(bytes);
                    }
                    oStream.close();
                    launched = true;
                    System.out.println("Class written to stream: " + className);
                } catch (IOException e)
                {
                    System.out.println("Couldn't talk to service");
                }
            } else
            {
                try
                {
                    System.out.println("Starting new launcher on port: " +
                                       socketPort.intValue());

                    ServerSocket server = new ServerSocket(socketPort.intValue());
                    Launcher.go(className, funcName, args);
                    Thread listener = new ListenerThread(server);
                    listener.start();
                    launched = true;
                    System.out.println("started service listener");
                } catch (IOException e)
                {
                    System.out.println("Socket contended, will try again");
                }
            }
        }
    }

        /**
         * Get a socket where this service is running.
         * If the launcher is not running on this machine it means
         * this is the first of its kind, so run it.
         */
    protected synchronized Socket findService()
    {
        try
        {
            Socket s = new Socket(InetAddress.getLocalHost(),
                                  socketPort.intValue());
            return s;
        } catch (IOException e)
        {
            // couldn't find a service provider
            return null;
        }
    }

    public static void go(final String className,
                          final String funcName,
                          final String args[])
    {
        System.out.println("Running: " + className);
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    Class clazz = Class.forName(className);
                    Class[] argsTypes = {String[].class};
                    //Object[] args = {new String[0]};
                    Method method = clazz.getMethod(funcName, argsTypes);
                    synchronized(this)
                    {
                        runningPrograms++;
                    }
                    method.invoke(clazz, new Object[] { args });

                    System.out.println("Method Invoked...");
                } catch (Exception e)
                {
                    runningPrograms --;
                    e.printStackTrace();
                    System.out.println("couldn't run the " + className);
                    if (runningPrograms <= 0)
                    {
                        System.exit(0);
                    }
                }
            }
        }; // end thread sub-class
        thread.start();
    }

        /**
         * Called when a program (running on the specific port) is quitting.
         */
    public synchronized static void programQuit()
    {
        programQuit(DEFAULT_APP_LAUNCHER_PORT);
    }
    public synchronized static void programQuit(int port)
    {
        runningPrograms--;
        if (runningPrograms <= 0)
        {
            System.exit(0);
        }
    }

        /**
         * Prints the usage.
         */
    public static void usage()
    {
        System.err.println("Usage: Launcher <options> " +
                           "target_class_name target_class_arguments");
        System.err.println("Options: ");
        System.err.println("-port       Start or connect to server on this port.");
    }

        /**
         * Main function.
         */
    public static void main(String[] args)
    {
            // parse arguments...

        int portNum = DEFAULT_APP_LAUNCHER_PORT;
        String className = "";
        int classNamePos = 0;

        if (args[0].equalsIgnoreCase("-port"))
        {
            portNum = Integer.parseInt(args[1]);

            classNamePos += 2;
        }

        className = args[classNamePos++];

            // now strip off the arguments
        for (int i = 0;i < args.length - classNamePos;i++)
        {
            args[i] = args[i + classNamePos];
        }

        Launcher l = new Launcher(portNum);

        l.launch(className, DEFAULT_FUNCTION_NAME,
                 args, args.length - classNamePos);
    }

        /**
         * The thread that executes each launch in a separate thread.
         */
    protected class ListenerThread extends Thread
    {
        ServerSocket server;

        public ListenerThread(ServerSocket socket)
        {
            this.server = socket;
        }

        public void run()
        {
            try
            {
                while (true)
                {
                    System.out.println("about to wait");
                    Socket socket = server.accept();
                    System.out.println("opened socket from client");
                    InputStream iStream = socket.getInputStream();

                    int classNameLength = iStream.read();
                    byte[] classNameBytes = new byte[classNameLength];
                    iStream.read(classNameBytes);
                    String className = new String(classNameBytes);

                    int funcNameLength = iStream.read();
                    byte[] funcNameBytes = new byte[funcNameLength];
                    iStream.read(funcNameBytes);
                    String funcName = new String(funcNameBytes);

                    int nArgs = iStream.read();
                    String args[] = new String[nArgs];
                    for (int i = 0;i < nArgs;i++)
                    {
                        int argLength = iStream.read();
                        byte []argBytes = new byte[argLength];
                        iStream.read(argBytes);
                        args[i] = new String(argBytes);
                    }

                        // also we need to read the arguments!!!
                    Launcher.go(className, funcName, args);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Failed to start");
            }
        }
    }
}
