package svtool.data;

import svtool.core.*;

public class DeviceInfo
{
        /**
         * Attribute codes for TE devices.
         */
    public final static int teAtCodes[] = 
    {
        2051,   // DEVICE_NAME
        2000,   // VLAN_ID
        //2090,   // VLAN_ID
        2001,   // VLAN_FNN
        2052,   // PORT_NBR
        2053,   // PORT_FNN
        2081,   // VTP_DOMAIN
    };

        /**
         * Attribute codes for WIP devices.
         */
    public final static int wipAtCodes[] = 
    {
        3140,   // DEVICE_NAME
        3011,   // VLAN_ID
        //3028,   // VLAN_IDEXT
        3012,   // VLAN_FNN
        3141,   // PORT_NBR
        3142,   // PORT_FNN
        3001,   // VTP_DOMAIN
    };

    protected String hashKey = null;

    public final static int DEVICE_NAME_ATTR = 0;
    public final static int VLAN_ID_ATTR = 1;
    public final static int VLAN_FNN_ATTR = 2;
    public final static int PORT_FNN_ATTR = 3;
    public final static int PORT_NUMBER_ATTR = 4;
    public final static int VTP_DOMAIN_ATTR = 5;

    public String attribs[] = new String[]
    {
        "",
        "",
        "",
        "",
        "",
        "",
    };


    public void setAttribute(int code, String value)
    {
        if (value == null) value = "";
        int attCode = 0;
        switch (code)
        {
            case 3140: case 2051: attCode = DEVICE_NAME_ATTR ; break;
            case 3011: case 2000: attCode = VLAN_ID_ATTR ; break;
            case 2090 : case 3028: 
                if (value.length() > 0) attCode = VLAN_ID_ATTR ;
            break ;
            case 3012: case 2001: attCode = VLAN_FNN_ATTR ; break;
            case 3141: case 2052: attCode = PORT_NUMBER_ATTR ; break;
            case 3142: case 2053: attCode = PORT_FNN_ATTR ; break;
            case 3001: case 2081: attCode = VTP_DOMAIN_ATTR ; break;
            default: System.out.println("Invalid code: " + code);
        }
        attribs[attCode]  = value;
    }

        // compares with another device...
    public int compareTo(DeviceInfo another)
    {
        int comp = attribs[DEVICE_NAME_ATTR].
                        compareToIgnoreCase(another.attribs[DEVICE_NAME_ATTR]);

        if (comp == 0)
        {
            comp =  attribs[VLAN_ID_ATTR].
                    compareToIgnoreCase(another.attribs[VLAN_ID_ATTR]);

            if (comp == 0)
            {
                    // port number comparison could be a bit tricky because
                    // port names like gi3/2 are the same as 3/2.  So only
                    // the 3/2 bit needs to be compared
                String tp = attribs[PORT_NUMBER_ATTR];
                String ap = another.attribs[PORT_NUMBER_ATTR];

                int tpos = 0, apos = 0;
                int tl = tp.length();
                int al = ap.length();


                while (tpos < tl && ! Character.isDigit(tp.charAt(tpos)))
                    tpos++;

                while (apos < al && ! Character.isDigit(ap.charAt(apos)))
                    apos++;

                int ain = apos;
                int tin = tpos;

                while (tpos < tl && apos < al)
                {
                    char tc = tp.charAt(tpos);
                    char ac = ap.charAt(apos);
                    if (tc != ac) return tc - ac;
                    tpos++; apos++;
                }
                return (tl - tin) - (al - ain);

                //comp =  attribs[PORT_NUMBER_ATTR].  compareToIgnoreCase();
            }
        }

        return comp;
    }

        /**
         * Hashcode.
         */
    public int hashCode()
    {
        if (hashKey == null)
        {
            hashKey =   attribs[DEVICE_NAME_ATTR] + 
                        attribs[VLAN_ID_ATTR] + 
                        attribs[VLAN_FNN_ATTR] + 
                        attribs[PORT_FNN_ATTR];
        }
        return hashKey.hashCode();
    }

        /**
         * Gets the string representation.
         */
    public String toString()
    {
        String out = "";
        for (int i = 0;i < attribs.length;i++)
        {
            if (i > 0) out += ", ";
            out += attribs[i];
        }
        return out;
    }
}
