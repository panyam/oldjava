package com.sri.utils.net.proxy;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Superclass of authentication methods for Socks5 protocol.
 */
abstract class SocksAuthMethod
{
        /**
         * Id of the authentication method.
         */
    protected byte id;

        /**
         * Constructor.
         */
    protected SocksAuthMethod(int id)
    {
        this.id = (byte)(id & 0xff);
    }

        /**
         * gets the id of the method.
         */
    public byte getID()
    {
        return id;
    }
}
