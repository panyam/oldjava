package com.sri.utils.net.proxy;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Class for socks exceptions.
 */
public class Socks5Exception extends Exception
{
    int id;

    String message = "";

        /**
         * Constructor.
         */
    public Socks5Exception(String mesg, int id)
    {
        super(mesg);
        this.id = id;
    }
}
