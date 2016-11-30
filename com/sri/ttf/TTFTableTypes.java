package com.sri.ttf;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Holds the tags for all the table types.
 */
public class TTFTableTypes
{
		/**
		 * The following tables are REQUIRED on all
		 * font files.
		 */
		/**
		 * Character to glyph mapping
		 */
	public final static String CMAP_TABLE = "cmap";
		/**
		 * Glyph data table
		 */
	public final static String GLYF_TABLE = "glyf";
		/**
		 * Font header table.
		 */
	public final static String HEAD_TABLE = "head";
		/**
		 * Horizontal header table
		 */
	public final static String HHEA_TABLE = "hhea";
		/**
		 * Horizontal metrix table.
		 */
	public final static String HMTX_TABLE = "hmtx";
		/**
		 * Index to location table
		 */
	public final static String LOCA_TABLE = "loca";
		/**
		 * Maximum profuile table.
		 */
	public final static String MAXP_TABLE = "maxp";
		/**
		 * naming table.
		 */
	public final static String NAME_TABLE = "name";
		/**
		 * Post script information table
		 */
	public final static String POST_TABLE = "post";
		/**
		 * OS/2 and windows specific metrics.
		 */
	public final static String OS2_TABLE = "OS/2";
	
		/**
		 * The following are optional tables.
		 */
		/**
		 * Control value table.
		 */
	public final static String CVT_TABLE = "cvt ";
		/**
		 * Embedded bitmap data table
		 */
	public final static String EBDT_TABLE = "EBDT";
		/**
		 * Embedded bitmap location data table
		 */
	public final static String EBLC_TABLE = "EBLC";
		/**
		 * Embedded bit map scalling data table
		 */
	public final static String EBSC_TABLE = "EBSC";
		/**
		 * Font program table
		 */
	public final static String FPGM_TABLE = "fpgm";
		/**
		 * Grid=fitting and scan conversion procedure table
		 */
	public final static String GASP_TABLE = "gasp";
		/**
		 * horizontal device metrics table
		 */
	public final static String HDMX_TABLE = "hdmx";
		/**
		 * kerning table
		 */
	public final static String KERN_TABLE = "kern";
		/**
		 * linear threshold table
		 */
	public final static String LTSH_TABLE = "LTSH";
		/**
		 * CVT program table
		 */
	public final static String PREP_TABLE = "prep";
		/**
		 * PCL5 table
		 */
	public final static String PCLT_TABLE = "PCLT";
		/**
		 * vertical device metrics table
		 */
	public final static String VDMX_TABLE = "VDMX";
		/**
		 * vertical metrics header table
		 */
	public final static String VHEA_TABLE = "vhea";
		/**
		 * Vertical metrics table
		 */
	public final static String VMTX_TABLE = "vmtx";
	
}
