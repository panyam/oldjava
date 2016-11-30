package com.sri.apps.netsim; 


import java.awt.*;

/**
 * A choice box where you can select a subnet mask.
 */
public class SubnetChoice extends Choice
{
        /**
         * Constructor.
         */
    public SubnetChoice()
    {
        for (int i = 0;i <= 32; i++)
        {
            super.addItem("/" + i + " - " + Utils.ipToString(0xffffffff << (32 - i)));
        }
    }

        /**
         * Override addItem so it doesnt do anything.
         */
    public void addItem()
    {
    }
}
