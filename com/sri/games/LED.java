package com.sri.games;

import java.awt.*;
import java.util.*;

public class LED extends Canvas{
    Image buff;
    Graphics gc;
    Color c;
    int max;
    String num;
    long number;
	Dimension bs = new Dimension();
    int w,h;
    int t = 3;
    int g = 1;
    int digits,DIGWIDTH = 10;
    boolean zfill;
    int just = 1;       // 0 = left, 1 = right;
    boolean inited = false;
    boolean list[][] = 
           {{false,false,false,false,false,false,false,}, //  = empty
            {true,true,true,true,true,true,false},      // 0
            {false,true,true,false,false,false,false},  // 1
            {true,true,false,true,true,false,true},
            {true,true,true,true,false,false,true},
            {false,true,true,false,false,true,true},
            {true,false,true,true,false,true,true},
            {true,false,true,true,true,true,true},
            {true,true,true,false,false,false,false},
            {true,true,true,true,true,true,true},
            {true,true,true,true,false,true,true},
            {false,false,false,false,false,false,true} };    // "-"

    public LED(Color c,String num,int digs,int just,boolean zfill) {
        this.c = c;
        this.num = num;
        this.number = (new Integer(num)).intValue();
        this.digits = digs;
        this.just = just;
        this.zfill = zfill;
        if (digits < num.length()) digits = num.length();
        w = (DIGWIDTH * digits) + (digits - 1);
        h = 23;
        setSize(w,h);
    }

    public LED(Color c,String num) { this(c,num,num.length(),1,true); }

    private void drawLight(Graphics g,int x,int y,int which,boolean en) {
        Polygon p = new Polygon();
        Color col = (en ? c.brighter():c.darker().darker().darker().darker());
        int wid = 25;
        switch (which) {
            case 0 : {
                p.addPoint(x + 1,y + 1); p.addPoint(x + 9,y + 1);
                p.addPoint(x + 7,y + 3); p.addPoint(x + 3,y + 3); 
                p.addPoint(x + 1,y + 1); 
            } break;
            case 1 : {
                p.addPoint(x + 10,y + 2); p.addPoint(x + 10,y + 10);
                p.addPoint(x + 8,y + 8); p.addPoint(x + 8,y + 4);
                p.addPoint(x + 10,y + 2); 
            } break;
            case 2 : {
                p.addPoint(x + 10,y + 12); p.addPoint(x + 10,y + 20);
                p.addPoint(x + 8,y + 18); p.addPoint(x + 8,y + 14);
                p.addPoint(x + 10,y + 12);             
            } break;
            case 3 : {
                p.addPoint(x + 9,y + 21); p.addPoint(x + 7,y + 19);
                p.addPoint(x + 3,y + 19); p.addPoint(x + 1,y + 21);
                p.addPoint(x + 9,y + 21); 
            } break;
            case 4 : {
                p.addPoint(x + 0,y + 20); p.addPoint(x + 2,y + 18);
                p.addPoint(x + 2,y + 14); p.addPoint(x + 0,y + 12);
                p.addPoint(x + 0,y + 20);
            } break;
            case 5 : {
                p.addPoint(x + 0,y + 10); p.addPoint(x + 2,y + 8);
                p.addPoint(x + 2,y + 4); p.addPoint(x + 0,y + 2);
                p.addPoint(x + 0,y + 10);
            } break;
            default : {
                p.addPoint(x + 2,y + 11); p.addPoint(x + 3,y + 10);
                p.addPoint(x + 7,y + 10); p.addPoint(x + 8,y + 11);
                p.addPoint(x + 7,y + 12); p.addPoint(x + 3,y + 12);
                p.addPoint(x + 2,y + 11); 
            }break;
        }
        g.setColor(col);
        g.fillPolygon(p);
    }

    private void drawDigit(Graphics g,int x,int y,char c) {
        for (int i = 0;i < 7;i++) {
            if (c == '-') drawLight(g,x,y,i,list[11][i]);
            else drawLight(g,x,y,i,list[(c - '/')][i]);
        }
    }

    public void paint(Graphics g) {
		if (g == null) return ;
        String s = num;
        if (buff == null || bs.width != w || bs.height != h)
		{
			bs.height = h;
			bs.width = w;
			buff = createImage(w,h);
			if (buff == null) return ;
			gc = buff.getGraphics();
		}
		if (gc == null || buff == null) return ;
        gc.setColor(Color.black);
        gc.fillRect(0,0,w,h);
        int max = Math.max(s.length(),digits);
        int min = Math.min(s.length(),digits);
        int diff = max - min;
        switch (just) {
            case 0 : {                
                for (int i = 0; i < max; i++) {
                    int x = (DIGWIDTH + 1) * i, y = 0;
                    drawDigit(gc,x,y,(i < s.length() ? s.charAt(i) : 
                                (zfill ? '0' : '/')));
                }
            }break;
            case 1 : {
                for (int i = 0;i < digits;i++) {
                    int x = (DIGWIDTH + 1) * i, y = 0; 
                    drawDigit(gc,x,y,(i >= diff ? s.charAt(i - diff) :
                                (zfill ? '0' : '/')));
                }
            }break;
        }
        if (buff != null) g.drawImage(buff,0,0,null);
    }
    
    public void update(Graphics g) {
    }

    public void changeBy(long dec) {
        number += dec;
        num = number + "";
        paint(getGraphics());
    }

    public void setJust(int just) { this.just = just; }
    public void setColor(Color c) { this.c = c; }
    public void setVal(long num) { setVal(num,digits); }
    public void setVal(long num,int digs) {
        this.number = num;
        this.num = num + "";
        this.digits = digs;
        if (digits < this.num.length()) digits = this.num.length();
        w = (DIGWIDTH * digits) + (digits - 1);
        h = 23;
        setSize(w,h);
        paint(getGraphics());
    }
}
