package com.sri.netsim.firewall.target;

import com.sri.utils.*;
import com.sri.utils.adt.*;
import com.sri.netsim.routing.*;
import com.sri.netsim.*;
import com.sri.netsim.firewall.match.*;

/**
 * A basic target.
 */
public class BasicTarget implements JumpTarget
{
        // define a few targets...
    public final static BasicTarget ACCEPT   = new BasicTarget();
    public final static BasicTarget REJECT   = new BasicTarget();
    public final static BasicTarget DROP     = new BasicTarget();
    public final static BasicTarget QUEUE    = new BasicTarget();
}
