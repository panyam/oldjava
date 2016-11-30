package com.sri.apps.off;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Extracts the contents of a tar file.
 */
public class TarExtractor extends InputStream
{
    public final static byte LF_OLDNORMAL = (byte)0;
    public final static byte LF_NORMAL = (byte)'0';
    public final static byte LF_LINK = (byte)'1';
    public final static byte LF_SYMBLINK = (byte)'2';
    public final static byte LF_CHR = (byte)'3';
    public final static byte LF_BLK = (byte)'4';
    public final static byte LF_DIR = (byte)'5';
    public final static byte LF_FIFO = (byte)'6';
    public final static byte LF_CONTIG = (byte)'7';

    protected int bytesRead = 0;

    public final static int NAME_SIZE = 100;
    public final static int RECORDSIZE = 512;
    public final static int TUNMLEN = 32;
    public final static int TGNMLEN = 32;

    protected byte fileBuffer[] = new byte[512];
    protected int bytesLeftInFile = 0;
    int padding;
    protected int filePos = 0, fileBufLen = 0;

    protected InputStream input;

    String name;
    byte buffer[] = new byte[512];

    byte mode[] = new byte[8];
    byte uid[] = new byte[8];
    byte gid[] = new byte[8];
    int size;
    byte mtime[] = new byte[12];
    byte checksum[] = new byte[8];
    byte linkflag;
    String linkName;
    byte magic[] = new byte[8];
    String uname, gname;
    byte devmajor[] = new byte[8];
    byte devminor[] = new byte[8];


    public TarExtractor(InputStream ins)
    {
        input = ins;
    }

    protected int fillFileBuffer() throws IOException
    {
        if (bytesLeftInFile <= 0) return -1;
        filePos = 0;

        int nRead = 0;
        fileBufLen = 0;
        int amLeft = Math.min(fileBuffer.length, bytesLeftInFile);
        while (nRead >= 0 && fileBufLen < amLeft)
        {
            nRead = input.read(fileBuffer,fileBufLen,amLeft - fileBufLen);
            if (nRead >= 0) fileBufLen += nRead;
        }
		
		//String temp = new String(fileBuffer, 0, 10);
		//System.out.println("bl, 10 Bytes = " + fileBufLen + " - " + temp);
        bytesRead += fileBufLen;
        if (fileBufLen < 0) return -1;
        //bytesLeftInFile -= fileBufLen;
        return fileBufLen;
    }

    public int read() throws IOException 
    {
        if (bytesLeftInFile <= 0) return -1;
        if (filePos >= fileBufLen && filePos >= fileBufLen)
        {
            int res = fillFileBuffer();
            if (res < 0) return -1;
            else if (res == 0) return 0;
        }
		bytesLeftInFile--;
        return fileBuffer[filePos++];
    }

    public int read(byte bytes[]) throws IOException
    {
        return read(bytes, 0, bytes.length);
    }

    public int read(byte bytes[], int offset, int len) throws IOException
    {
        if (filePos >= fileBufLen && bytesLeftInFile <= 0) return -1;
        if (filePos >= fileBufLen)
        {
            int res = fillFileBuffer();
            if (res < 0) return -1;
            else if (res == 0) return 0;
        }
        int left = fileBufLen - filePos;
        if (left >= len)
        {
            int to = offset + len;
            for (int i = offset;i < to;i++)
            {
                bytes[i] = fileBuffer[filePos++];
            }
			bytesLeftInFile -= len;
            return len;
        } else
        {
            int to = offset + left;
            for (int i = offset;i < to;i++)
            {
                bytes[i] = fileBuffer[filePos++];
            }

            int nr = read(bytes, to, len - left);

			bytesLeftInFile -= left;
            if (nr < 0) return left;
            else 
			{
				bytesLeftInFile -= nr;
				return left + nr;
			}
        }
    }

    public boolean hasMoreFiles() throws IOException
    {
//        System.out.println("hmf, Fp, Fl = " + bytesLeftInFile + ", " + filePos + ", " + fileBufLen);
        if (bytesLeftInFile > 0) return true;

        int total = 0, nRead = 0;
        bytesLeftInFile = 0;
        size = -1;

        while (nRead >= 0 && total < buffer.length)
        {
            nRead = input.read(buffer, total, buffer.length - total);
            if (nRead >= 0) total += nRead;
        }

        bytesRead += total;
        if (total == 0) return false;

        int len = 0;
        while (len < buffer.length && buffer[len] != 0) len++;
        name = new String(buffer, 0, len);
        if (name.length() == 0) return false;

        //System.out.println("Name = " + name);

        int pos = NAME_SIZE;
        for (int i = 0;i < 8;i++) mode[i] = buffer[pos++];
        for (int i = 0;i < 8;i++) uid[i] = buffer[pos++];
        for (int i = 0;i < 8;i++) gid[i] = buffer[pos++];

        size = 0;
        for (int i = 0;i < 12;i++, pos++) 
        {
            if (buffer[pos] >= (byte)'0' && buffer[pos] <= (byte)'9')
            {
                size = (size * 8) + (buffer[pos] - (byte)'0');
            }
        }
        bytesLeftInFile = size;

        if (size % 512 != 0)
        {
            bytesLeftInFile = size + (512 - (size % 512));
        } 

        padding = bytesLeftInFile - size;

        for (int i = 0;i < 12;i++) mtime[i] = buffer[pos++];
        for (int i = 0;i < 8;i++) checksum[i] = buffer[pos++];
        linkflag = buffer[pos++];

        len = 0;
        while (pos + len < buffer.length && buffer[pos + len] != 0) len++;
        linkName = new String(buffer, pos, len);
        pos += NAME_SIZE;

        for (int i = 0;i < 8;i++) magic[i] = buffer[pos++];

        len = 0;
        while (pos + len < buffer.length && buffer[pos + len] != 0) len++;
        uname = new String(buffer, pos, len);
        pos += TUNMLEN;

        len = 0;
        while (pos + len < buffer.length && buffer[pos + len] != 0) len++;
        gname= new String(buffer, pos, len);
        pos += TGNMLEN;

        for (int i = 0;i < 8;i++) devmajor[i] = buffer[pos++];
        for (int i = 0;i < 8;i++) devminor[i] = buffer[pos++];

        return true;
    }

    public InputStream nextFile() throws IOException
    {
        if (bytesLeftInFile > 0) return this;
        else
        {
            if (hasMoreFiles()) return this;
            else return null;
        }
    }

    public String getFileName()
    {
        return name;
    }

        // Returns the size of the next file
        // -1 if no more files exist
        // directories are skipped
    public int nextFileSize()
    {
        return size;
    }

    public static void main(String args[])
    {
        try
        {
            InputStream input = new FileInputStream(args[0]);

            if (args[0].endsWith(".gz"))
            {
                input = new GZIPInputStream(input);
            }
            TarExtractor te = new TarExtractor(input);
            byte bytes[] = new byte[512];
            while (te.hasMoreFiles())
            {
                InputStream in = te.nextFile();
                int s = te.nextFileSize();
				DataOutputStream dout = new DataOutputStream(new FileOutputStream(te.getFileName()));
                System.out.println("Name = " + te.getFileName());
                System.out.println("Size = " + s);
                int nRead = 0;
                int nTotal = 0;
                while (nRead >= 0)
                {
                    nRead = in.read(bytes);
                    //System.out.println("Nread = " + nRead);
                    if (nRead >= 0) 
					{
						nTotal += nRead;
						dout.write(bytes, 0, nRead);
					}
                }
				dout.close();
                System.out.println("Read  " + nTotal + " bytes");
                System.out.println("F Offset " + te.bytesRead + " bytes");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
