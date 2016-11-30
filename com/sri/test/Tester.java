package com.sri.test;

import java.awt.*;
import java.awt.event.*;

public class Tester {
    public static void main(String args[])
        throws Exception
    {
        Class testClass = Class.forName(args[0]);
        System.out.println("Class = " + testClass);
        TesterFrame et = (TesterFrame)testClass.newInstance();
    }
}
