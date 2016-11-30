package com.sri.utils.net.proxy;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Superclass of authentication methods for Socks5 protocol.
 */
class UPAuthMethod extends SocksAuthMethod
{
        /**
         * Constructor.
         */
    public UPAuthMethod()
    {
        super(0x02);
    }
}
