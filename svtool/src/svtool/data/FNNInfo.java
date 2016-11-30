package svtool.data;

import svtool.core.*;


/**
 * Describes an FNN object.
 */
public class FNNInfo
{
    public String fnnID;
    public ServiceInfo parentService;

    public boolean equals(Object obj)
    {
        return ((obj instanceof FNNInfo) &&
                ((FNNInfo)obj).fnnID.equalsIgnoreCase(fnnID));
    }
}
