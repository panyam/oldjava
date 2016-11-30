package com.sri.gui.core;
/**
 *      NumField class
 *      -------------
 * This class is an extension of the STextField and sole purpose of this class is to 
 * enable ONLY numeric information to be entered in the text field.  All character apart
 * from numeric data are discarded.
 *
 *  @author Sri Panyam
 *  @version V0.1
 */

import java.awt.*;
import java.awt.event.*;

public class NumField extends STextField {
    public final static int POSITIVE = 0;
    public final static int NEGATIVE = 1;

    public final static int INT = 0;
    public final static int FLOAT   = 1;

    protected int sign = NEGATIVE;
    protected int type = FLOAT;

    public NumField() {
        this("0",FLOAT, NEGATIVE,-1);
    }

    public NumField(String s,int type,int sign) {
		this(s,type,sign,-1);
	}
	
    public NumField(String s,int type,int sign,int nCols) {
        super(s);
        this.type = type;
        this.sign = sign;
        setColumns(nCols);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSign(int sn) {
        this.sign = sn;
    }

    public void keyTyped(KeyEvent e) {
        if (e.getSource() != this) return ;
        char c = e.getKeyChar ();
        int cc = (int)c;

        // We check each key and if it is not supposed to be in a numberfield
        // we just consume and pretend nothing happend :-)

		//int arrow = (cc = 
        if (!Character.isDigit(c) && cc != KeyEvent.VK_BACK_SPACE && cc != KeyEvent.VK_ENTER) {
            switch (c) {
                // If it is decimal
                case '.':  
                    if (type == INT) // If integer field noway
                            e.consume ();
                    else if (!firstDecimal ()) // in floatfield
                            e.consume ();
					else super.keyTyped(e);
                    break;
                case '-':
                    if (sign == POSITIVE) e.consume ();
                    else if (!firstMinus ()) e.consume ();
					else super.keyTyped(e);
                    break;
                default: e.consume();
                break;
            }
        }
		else super.keyTyped(e);
    }

    private boolean firstDecimal () { return (getText().indexOf(".") < 0); }

    private boolean firstMinus () {
        return ((getText().indexOf('-') < 0) && (caratPos == 0));
    }
}
