package com.sri.java;
import java.util.*;

import java.io.*;
import java.awt.*;

public class test
{
    public test()
    {
        int loc1, loc2;
        int loc3 = 5;
        int loc4 = loc3 * 5;
        double loc6 = loc3 % loc4;

        //if (loc6 < 5.5) System.out.println("True");

        Point p = new Point();
        System.out.println("loc6 = " + loc6);
        p.x = 3;
        if (loc3 < loc4)
        {
            loc3 = loc3 * loc4;
            if (loc3 * 4 <= loc4) loc4 = loc4 << 3;
            else loc3 = loc3 << 3;
        }
        loc1 = 0;

        while (loc1 < 10)
        {
            loc2 = loc1;
            do
            {
                loc2 = loc2 + 1;
                System.out.println("" + loc1 + ", " + loc2);
            } while (loc2 < 5);
            loc1 = loc1 + 1;
        }

        if (loc3 < loc4 || ((loc4 * 2) > loc3))
        {
            loc3 = (loc3 + loc4) - 10;
            loc4 = loc4 / 2;
        }
        System.out.println("" + loc3 + ", " + loc4);
    }

    public test(boolean bool)
    {
        long a = 0;
        for (int i = 0;i <= 1000000;i++) System.out.println("" + i);
        Thread i = new Thread();
        a++;
        i.start();
    }

    public static void main(String args[])
    {
        test t = new test();
    }
}
