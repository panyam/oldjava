package com.sri.ttf;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This table is required by OS/2 and Windows and defines 
 * a set of metrics required by these OSes.
 * A description of the attributes of this table can 
 * be found the actual TrueType specification.
 */
public class TTFOS2Table extends TTFTable
{
	public final static byte PANOSE_B_FAMILY_TYPE = 0;
	public final static byte PANOSE_B_SERIF_STYLE = 1;
	public final static byte PANOSE_B_WEIGHT = 2;
	public final static byte PANOSE_B_PROPORTION = 3;
	public final static byte PANOSE_B_CONTRAST = 4;
	public final static byte PANOSE_B_STROKE_VARIATION = 5;
	public final static byte PANOSE_B_ARM_STYLE = 6;
	public final static byte PANOSE_B_LETTER_FORM = 7;
	public final static byte PANOSE_B_MIDLINE = 8;
	public final static byte PANOSE_B_X_HEIGHT = 9;
	
	int xAvgCharWidth;
	int usWeightClass;
	int usWidthClass;
	int fsType;
	int ySubscriptXSize;
	int ySubscriptYSize;
	int ySubscriptXOffset;
	int ySubscriptYOffset;
	int ySuperscriptXSize;
	int ySuperscriptYSize;
	int ySuperscriptXOffset;
	int ySuperscriptYOffset;
	int yStrikeoutSize;
	int yStrikeoutPosition;
	int sFamilyClass;
	
		/**
		 * This describes 10 digits each of which currently
		 * describes upto 16 variations.
		 */
	byte panose[] = new byte[10];
	int ulUnicodeRange1;	// bits 0 - 31
	int ulUnicodeRange2;	// bits 32 - 63
	int ulUnicodeRange3;	// bits 64-95
	int ulUnicodeRange4;	// Bits 96-127
	byte achVendID[] = new byte[4];
	int fsSelection;
	int usFirstCharIndex;
	int usLastCharIndex;
	int sTypoAscender;
	int sTypoDescender;
	int sTypoLineGap;
	int usWinAscent;
	int usWinDescent;
	int ulCodePageRange1;
	int ulCodePageRange2;

        /**
         * Constructor.
         */
    public TTFOS2Table(TTFFont par, String tag, 
						int checkSum, int offset, int length)
    {
        super(par, tag, checkSum, offset, length);
    }

        /**
         * Read the rest of the font from the stream.
         */
    public int readInputStream(DataInputStream din) throws IOException
    {
		this.tableVersion = din.readShort();
		this.xAvgCharWidth = din.readShort();
		this.usWeightClass = din.readShort();
		this.usWidthClass = din.readShort();
		this.fsType = din.readShort();
		this.ySubscriptXSize = din.readShort();
		this.ySubscriptYSize = din.readShort();
		this.ySubscriptXOffset = din.readShort();
		this.ySubscriptYOffset = din.readShort();
		this.ySuperscriptXSize = din.readShort();
		this.ySuperscriptYSize = din.readShort();
		this.ySuperscriptXOffset = din.readShort();
		this.ySuperscriptYOffset = din.readShort();
		this.yStrikeoutSize = din.readShort();
		this.yStrikeoutPosition = din.readShort();
		this.sFamilyClass = din.readShort();
		for (int i = 0;i < 10;i++) panose[i] = din.readByte();
		this.ulUnicodeRange1 = din.readInt();
		this.ulUnicodeRange2 = din.readInt();
		this.ulUnicodeRange3 = din.readInt();
		this.ulUnicodeRange4 = din.readInt();
		for (int i = 0;i < 4;i++) achVendID[i] = din.readByte();
		this.fsSelection = din.readShort();
		this.usFirstCharIndex = din.readShort();
		this.usLastCharIndex = din.readShort();
		this.sTypoAscender = din.readShort();
		this.sTypoDescender = din.readShort();
		this.sTypoLineGap = din.readShort();
		this.usWinAscent = din.readShort();
		this.usWinDescent = din.readShort();
		this.ulCodePageRange1 = din.readInt();
		this.ulCodePageRange2 = din.readInt();
		return 86;
    }
}
