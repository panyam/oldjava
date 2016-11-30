package com.sri.ttf;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import com.sri.gui.ext.*;

public class TTFTester extends Frame implements ActionListener
{
	MenuItem openMenuItem = new MenuItem("Open Font...");
	MenuItem exitMenuItem = new MenuItem("Exit");
	Buffer buffer = null;
	FileDialog fdialog = new FileDialog(this);
										
	public TTFTester()
	{
		super("TTF Renderer");
		Menu m = new Menu();
		m.add(openMenuItem);
		m.addSeparator();
		m.add(exitMenuItem);
		MenuBar mb = new MenuBar();
		mb.add(m);
		this.setMenuBar(mb);
		openMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
	}
	
	public void update(Graphics g) { }
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		if (buffer == null || buffer.getWidth() != d.width ||
							  buffer.getHeight() != d.height) 
		{
			if (buffer != null) buffer.destroy();
			buffer = new Buffer(d.width, d.height);
		}
		Image im = createImage(buffer.getMemoryImageSource());
		if (im != null)
		{
			g.drawImage(im, 0, 0, null);
		}
	}
	
		/**
		 * Opens the given font file name and
		 * processes it.
		 */
	public void openFontFile(String fname) throws Exception
	{
	}
	
		/**
		 * Action event handler.
		 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == openMenuItem)
		{
			fdialog.setVisible(true);
			String fname = fdialog.getFile();
			if (fname == null) return ;
			if (fdialog.getDirectory() != null)
			{
				fname = fdialog.getDirectory() + fname;
			}
			try
			{
				openFontFile(fname);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		} else if (e.getSource() == exitMenuItem)
		{
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
	
	public static void main(String args[])
	{
		try
		{
			TTFFont tfont = new TTFFont("Comic font", 
								new DataInputStream(
									new FileInputStream("fonts/COMIC.TTF")));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
