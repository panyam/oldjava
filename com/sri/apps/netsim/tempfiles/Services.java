package com.sri.apps.netsim; 


public class Services
{
        /**
         * Number of services that are run in a host.
         */
    public static int NUM_SERVICES = 10;

        /**
         * Ports of the services.
         */
    public final static int servicePorts[] = new int[]
    {
        19, 20, 23, 25, 80, 443, 500, 600, 700, 800,
    };

        /**
         * Names of all the services.
         */
    public final static String serviceNames[] = new String[]
    {
        "nntp", "ftp", "telnet", "smtp", "http", "https",
        "msg1", "msg2", "msg3", "msg4", 
    };

        /**
         * Checkbox strings.
         */
    public final static String checkBoxText[] = new String[]
    {
        "News Server       -       Port 19",
        "FTP               -       Port 20",
        "Telnet            -       Port 23",
        "SMTP (Mail)       -       Port 25",
        "HTTP (Web)        -       Port 80",
        "Secure HTTP       -       Port 443",
        "MSG Server 1      -       Port 500",
        "MSG Server 2      -       Port 600",
        "MSG Server 3      -       Port 700",
        "MSG Server 4      -       Port 800",
    };

        /**
         * Number of services.
         */
    public final static int getServiceCount()
    {
        return servicePorts.length;
    }

        /**
         * Gets an instance of the ith Server
         */
    public final static TCPServer getServiceInstance(
                        HostModule hostModule, int which)
    {
        return new MSGServer(hostModule,
                             serviceNames[which],
                             servicePorts[which]);
    }
}
