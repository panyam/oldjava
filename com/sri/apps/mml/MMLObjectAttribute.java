package com.sri.apps.mml;

/**
 * An attribute class for representing the generic 
 * value.
 */
public class MMLObjectAttribute extends MMLAttribute
{
		/**
		 * Value of this attribute.
		 */
	protected Object value;

        /**
         * The Constructor
         */
    public MMLObjectAttribute(String name, Object val)
    {
        super(name);
        this.value = val;
    }
}
