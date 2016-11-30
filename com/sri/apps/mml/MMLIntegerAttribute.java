package com.sri.apps.mml;

import java.awt.*;
import java.util.*;

/**
 * An attribute whose value is an integer.
 */
public class MMLIntegerAttribute extends MMLAttribute
{
        /**
         * Value of this attribute.
         */
    public short value;

        /**
         * The value of the attribute.
         */
    public MMLIntegerAttribute(String name, short val)
    {
        super(name);
        this.value = val;
    }
}
