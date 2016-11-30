
package com.sri.games.mine;

import com.sri.games.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class OptionsPanel extends Panel implements ItemListener{
    CheckboxGroup cbg = new CheckboxGroup();
    Checkbox begin = new Checkbox("Beginner",cbg,true);
    Checkbox inter = new Checkbox("Intermediate",cbg,false);
    Checkbox expert= new Checkbox("Expert",cbg,false);
    Checkbox custom= new Checkbox("Custom",cbg,false);
    TextField Height = new TextField("16",10),
              Width = new  TextField("16",10),
              Mines = new TextField("40",10);
    Label heightlbl = new Label("Height : ",Label.LEFT),
          widthlbl = new Label("Width : ",Label.LEFT),
          mineslbl = new Label("Mines : ",Label.LEFT);
    boolean cancelled = true;
    int top = 20,right = 6,bottom = 6,left = 50;

    public OptionsPanel() {
        setFont(new Font("Serif",Font.BOLD,15));
        setLayout(new GridLayout(7,2,3,3));
        add(begin); add(new Label(""));
        add(inter); add(new Label(""));
        add(expert); add(new Label(""));
        add(custom); add(new Label(""));
		
		begin.addItemListener(this);
		inter.addItemListener(this);
		expert.addItemListener(this);
		custom.addItemListener(this);
        add(heightlbl);add(Height);
        add(widthlbl); add(Width);
        add(mineslbl); add(Mines);
        heightlbl.setEnabled(false); Height.setEnabled(false);
        widthlbl.setEnabled(false); Width.setEnabled(false);
        mineslbl.setEnabled(false); Mines.setEnabled(false);
        setSize(200,200);
    }

    public Insets getInsets() { return new Insets(top,left,bottom,right); }

    public int getLevel() { 
        if (begin.getState()) return 0;
        else if (inter.getState()) return 1;
        else if (expert.getState()) return 2;
        else return 3;
    }
    public int getRows(){return Integer.valueOf(Height.getText()).intValue(); }
    public int getCols(){return Integer.valueOf(Width.getText()).intValue(); }
    public int getMines(){return Integer.valueOf(Mines.getText()).intValue(); }

	public void itemStateChanged(ItemEvent e)
	{
        if (e.getSource() instanceof Checkbox) {
            if (e.getSource() == custom) {
                heightlbl.setEnabled(true); Height.setEnabled(true);
                widthlbl.setEnabled(true); Width.setEnabled(true);
                mineslbl.setEnabled(true); Mines.setEnabled(true);
            }
            else {
                heightlbl.setEnabled(false); Height.setEnabled(false);
                widthlbl.setEnabled(false); Width.setEnabled(false);
                mineslbl.setEnabled(false); Mines.setEnabled(false);
            }
        }
    }

    /*
    public void paint(Graphics g) {
        Dimension d = size();
        int w = d.width - 2;
        int h = d.height - 3;
        int x = 2;
        Color dg = new Color(128,128,128);
        FontMetrics f = g.getFontMetrics();
        int asc = (int)(f.getAscent()/2);
        int wid = f.stringWidth(title) + 10;
        Color store = g.getColor();
        g.setColor(Color.white);
        g.drawLine(x,asc + 1,x+3,asc + 1);
        g.drawLine(x+1,asc + 1,x+1,h);
        g.drawLine(wid,asc + 1,w - 2,asc + 1);
        g.drawLine(w,asc,w,h);
        g.drawLine(w,h,3,h);
        g.setColor(dg);
        g.drawLine(x,asc,x+4,asc);
        g.drawLine(x,asc,x,h-1);
        g.drawLine(x,h - 1,w-1,h-1);
        g.drawLine(w - 1,h - 1,w - 1,asc);
        g.drawLine(wid,asc,w - 1,asc);
        g.setColor(Color.black);
        g.drawString(title,8,asc + asc - 2);
        g.setColor(store);
    }
    */
}
