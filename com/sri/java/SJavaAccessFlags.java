package com.sri.java;

/**
 * A class that holds constants regarding
 * access types for fields and methods.
 */
public class SJavaAccessFlags
{
    public final static short ACC_PUBLIC = 0x0001;
    public final static short ACC_PRIVATE = 0x0002;
    public final static short ACC_PROTECTED = 0x0004;
    public final static short ACC_STATIC = 0x0008;
    public final static short ACC_FINAL = 0x0010;
    public final static short ACC_SUPER = 0x0020;
    public final static short ACC_SYNCHRONIZED = 0x0020;
    public final static short ACC_VOLATILE = 0x0040;
    public final static short ACC_TRANSIENT = 0x0080;
    public final static short ACC_NATIVE = 0x0080;
    public final static short ACC_INTERFACE = 0x0200;
    public final static short ACC_ABSTRACT = 0x0400;
    public final static short ACC_STRICT = 0x0800;

        /**
         * The accessflags.
         */
    public short accessFlags = ACC_SUPER;

        /**
         * Tells if this class is an interface.
         */
    public boolean isInterface()
    {
        return (accessFlags & ACC_INTERFACE) != 0;
    }

        /**
         * Sets or clears the interface flag.
         */
    public void setInterface(boolean isInt)
    {
        if (isInt) accessFlags |= ACC_ABSTRACT | ACC_INTERFACE | ACC_PUBLIC;
        else accessFlags &= (0xffff ^ ACC_INTERFACE);
    }

        /**
         * Sets or clears the abstract flag.
         */
    public void setAbstract(boolean isAbstract)
    {
        if (isAbstract) 
		{
			int f = (0x0000ffff ^ 
					 (ACC_FINAL | ACC_STATIC | ACC_PRIVATE | 
					  ACC_NATIVE | ACC_STRICT | ACC_SYNCHRONIZED));
			accessFlags = (short)((accessFlags & f) | ACC_ABSTRACT);
		}
        else accessFlags &= (0xffff ^ ACC_ABSTRACT);
    }

        /**
         * Tells if this class is abstract.
         */
    public boolean isAbstract()
    {
        return (accessFlags & ACC_ABSTRACT) != 0;
    }

        /**
         * Sets or clears the strict flag.
         */
    public void setStrict(boolean isStrict)
    {
        if (isStrict) accessFlags |= ACC_STRICT;
        else accessFlags &= (0xffff ^ ACC_STRICT);
    }

        /**
         * Tells if this class is strict.
         */
    public boolean isStrict()
    {
        return (accessFlags & ACC_STRICT) != 0;
    }

        /**
         * Sets or clears the super flag.
         */
    public void setNative(boolean isNative)
    {
        if (isNative) accessFlags |= ACC_NATIVE;
        else accessFlags &= (0xffff ^ ACC_NATIVE);
    }

        /**
         * Tells if this class is super.
         */
    public boolean isNative()
    {
        return (accessFlags & ACC_NATIVE) != 0;
    }

        /**
         * Sets or clears the super flag.
         */
    public void setSynchronized(boolean isSynchronized)
    {
        if (isSynchronized) accessFlags |= ACC_SYNCHRONIZED;
        else accessFlags &= (0xffff ^ ACC_SYNCHRONIZED);
    }

        /**
         * Tells if this class is super.
         */
    public boolean isSynchronized()
    {
        return (accessFlags & ACC_SYNCHRONIZED) != 0;
    }

        /**
         * Sets or clears the super flag.
         */
    public void setSuper(boolean isSuper)
    {
        if (isSuper) accessFlags |= ACC_SUPER;
        else accessFlags &= (0xffff ^ ACC_SUPER);
    }

        /**
         * Tells if this class is super.
         */
    public boolean isSuper()
    {
        return (accessFlags & ACC_SUPER) != 0;
    }

        /**
         * Sets private flag.
         */
    public void setPrivate(boolean isPrivate)
    {
        if (isPrivate) accessFlags = (short)((accessFlags | ACC_PRIVATE) & (0xffff ^ (ACC_PUBLIC | ACC_PROTECTED)));
        else accessFlags &= (0xffff ^ ACC_PRIVATE);
    }

        /**
         * Tells if this class is final.
         */
    public boolean isPrivate()
    {
        return (accessFlags & ACC_PRIVATE) != 0;
    }

        /**
         * Sets public flag.
         */
    public void setPublic(boolean isPublic)
    {
        if (isPublic) accessFlags = (short)((accessFlags | ACC_PUBLIC) & (0xffff ^ (ACC_PRIVATE | ACC_PROTECTED)));
        else accessFlags &= (0xffff ^ ACC_PUBLIC);
    }

        /**
         * Tells if this class is final.
         */
    public boolean isPublic()
    {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

        /**
         * Sets protected flag.
         */
    public void setProtected(boolean isProtected)
    {
        if (isProtected) accessFlags = (short)((accessFlags | ACC_PROTECTED) & (0xffff ^ (ACC_PUBLIC | ACC_PRIVATE)));
        else accessFlags &= (0xffff ^ ACC_PROTECTED);
    }

        /**
         * Tells if this class is final.
         */
    public boolean isProtected()
    {
        return (accessFlags & ACC_PROTECTED) != 0;
    }

        /**
         * Sets final flag.
         */
    public void setFinal(boolean isFinal)
    {
        if (isFinal) accessFlags = (short)((accessFlags | ACC_FINAL) & (0xffff ^ ACC_VOLATILE));
        else accessFlags &= (0xffff ^ ACC_FINAL);
    }

        /**
         * Tells if this class is final.
         */
    public boolean isFinal()
    {
        return (accessFlags & ACC_FINAL) != 0;
    }

        /**
         * Sets volatile flag.
         */
    public void setVolatile(boolean isVolatile)
    {
        if (isVolatile) accessFlags = (short)((accessFlags | ACC_VOLATILE) & (0xffff ^ ACC_FINAL));
        else accessFlags &= (0xffff ^ ACC_PUBLIC);
    }

        /**
         * Tells if this class is volatile.
         */
    public boolean isVolatile()
    {
        return (accessFlags & ACC_VOLATILE) != 0;
    }

        /**
         * Sets static flag.
         */
    public void setStatic(boolean isStatic)
    {
        if (isStatic) accessFlags = (short)(accessFlags | ACC_STATIC);
        else accessFlags &= (0xffff ^ ACC_STATIC);
    }

        /**
         * Tells if this class is static.
         */
    public boolean isStatic()
    {
        return (accessFlags & ACC_STATIC) != 0;
    }

        /**
         * Sets transient flag.
         */
    public void setTransient(boolean isTrans)
    {
        if (isTrans) accessFlags = (short)(accessFlags | ACC_TRANSIENT);
        else accessFlags &= (0xffff ^ ACC_TRANSIENT);
    }

        /**
         * Tells if this class is transient.
         */
    public boolean isTransient()
    {
        return (accessFlags & ACC_TRANSIENT) != 0;
    }

    public void print()
    {
		String out = "Access flags: ";
		if (isPublic()) out += " Public, ";
		if (isProtected()) out += " Protected, ";
		if (isPrivate()) out += " Private, ";
		if (isStatic()) out += " Static, ";
		if (isTransient()) out += " Transient, ";
		if (isVolatile()) out += " Volatile, ";
		if (isFinal()) out += " Final, ";
		if (isSuper()) out += " Super, ";
		if (isAbstract()) out += " Abstract, ";
		if (isInterface()) out += " Interface, ";
		System.out.println(out);
    }
}
