package com.sri.ttf;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Given an input stream, reads the bytes and finds out what 
 * the current table is and returns that table.
 */
public class TTFTableGenerator
{
        /**
         * The name for each tag.
         */
    protected final static String tagNames[] = 
    {
        TTFTableTypes.CMAP_TABLE, TTFTableTypes.GLYF_TABLE, 
		TTFTableTypes.HEAD_TABLE, TTFTableTypes.HHEA_TABLE, 
		TTFTableTypes.HMTX_TABLE, TTFTableTypes.LOCA_TABLE,
		TTFTableTypes.MAXP_TABLE, TTFTableTypes.NAME_TABLE,
		TTFTableTypes.POST_TABLE, TTFTableTypes.OS2_TABLE, 
        TTFTableTypes.CVT_TABLE, TTFTableTypes.EBDT_TABLE, 
		TTFTableTypes.EBLC_TABLE, TTFTableTypes.EBSC_TABLE,
		TTFTableTypes.FPGM_TABLE, TTFTableTypes.GASP_TABLE,
		TTFTableTypes.HDMX_TABLE, TTFTableTypes.KERN_TABLE, 
		TTFTableTypes.LTSH_TABLE, TTFTableTypes.PREP_TABLE, 
        TTFTableTypes.PCLT_TABLE, TTFTableTypes.VDMX_TABLE,
		TTFTableTypes.VHEA_TABLE, TTFTableTypes.VMTX_TABLE, 
    };

        /**
         * The class for each tag.
         */
    protected final static Class tagClasses[] = 
    {
        TTFCMAPTable.class, TTFGLYFTable.class, TTFHEADTable.class, 
		TTFHHEATable.class, TTFHMTXTable.class, TTFLOCATable.class, 
		TTFMAXPTable.class, TTFNAMETable.class, TTFPOSTTable.class,
		TTFOS2Table.class, TTFCVTTable.class, TTFEBDTTable.class,
		TTFEBLCTable.class, TTFEBSCTable.class, TTFFPGMTable.class, 
		TTFGASPTable.class, TTFHDMXTable.class, TTFKERNTable.class,
		TTFLTSHTable.class, TTFPREPTable.class, TTFPCLTTable.class,
		TTFVDMXTable.class, TTFVHEATable.class, TTFVMTXTable.class, 
    };

        /**
         * Read the header information about this table.
         */
    public static TTFTable readTable(TTFFont curr, 
									 DataInputStream din) throws IOException
    {
        int t = din.readInt();
        int checkSum = din.readInt();
        int offset = din.readInt();
        int length = din.readInt();
        byte bytes[] = new byte[4];
        bytes[3] = (byte)((t & 0xff) & 0xff);
        bytes[2] = (byte)(((t >> 8) & 0xff) & 0xff);
        bytes[1] = (byte)(((t >> 16) & 0xff) & 0xff);
        bytes[0] = (byte)(((t >> 24) & 0xff) & 0xff);
        String tag = new String(bytes, 0, 4).trim();
        
		Constructor constructor;
		Class argsclass[] = new Class[]
		{
			TTFFont.class, String.class, int.class, int.class, int.class
		};
		
        Object args[] = new Object[] 
        {
			curr, tag, new Integer(checkSum), 
			new Integer(offset), new Integer(length)
        };

            // now see what kind of tag it is.. and
            // read that kind of table
        for (int i = 0;i < tagNames.length;i++)
        {
            if (tagNames[i].equals(tag))
            {
				System.out.println("Reading " + tag + " table");
				
				try
				{
					constructor = tagClasses[i].getConstructor(argsclass);
					return (TTFTable)constructor.newInstance(args);
				} catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
            }
        }
			// if you are here then read the table as an unknown table...
        return new TTFUnknownTable(curr, tag, checkSum, offset, length);
    }
}
