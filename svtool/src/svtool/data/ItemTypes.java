package svtool.data;

/**
 * Contains a few constants to identify device types and info.
 *
 * @author Sri Panyam
 */
public class ItemTypes
{
    public static int fnnTypeLists[][] = new int[][]
    {
        new int[] { 2001, 3012, 3081, 2053, 3142},
        new int[] { 2001, 3012 },
        new int[] { 3081},
        new int[] { 2053, 3142},
    };

    public static String fnnStrings[] = new String[]
    {
        "ALL",
        "VLAN",
        "VPN",
        "Port",
    };

    public static String getByStrings[] = new String[]
    {
        "Service ID", "Customer", "FNN", "Duplicates"
    };
}
