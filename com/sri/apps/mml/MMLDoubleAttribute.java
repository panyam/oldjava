package com.sri.apps.mml;

import java.awt.*;
import java.util.*;

/**
 * An attribute whose value is an integer.
 */
public class MMLDoubleAttribute extends MMLAttribute
{
        /**
         * Value of this attribute.
         */
    public float value;

        /**
         * The Constructor
         */
    public MMLDoubleAttribute(String name, short val)
    {
        super(name);
        this.value = val;
    }
}
