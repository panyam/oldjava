package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;

class SliderTester extends TesterFrame {
        /**
         * A standard Test function to test the slider.
         */
    SSlider mainOne = new SSlider(0, 100);  // slider that is bein tested
    CheckboxGroup pointerDirCB = new CheckboxGroup();
    Checkbox noDir = new Checkbox("None",true,pointerDirCB);
    Checkbox blDir = new Checkbox("Bottom/Left",false,pointerDirCB);
    Checkbox trDir = new Checkbox("Top/Right",false,pointerDirCB);
    Checkbox horiz = new Checkbox("Horizontal",false);
               // set fill parameters
    CheckboxGroup fillTypeCB = new CheckboxGroup();
    Checkbox noFill = new Checkbox("No Fill",true,fillTypeCB);
    Checkbox plFill = new Checkbox("Plain Fill",false,fillTypeCB);
    Checkbox blFill = new Checkbox("Block Fill",false,fillTypeCB);
    Checkbox lnFill = new Checkbox("Line Fill",false,fillTypeCB);
               // to use 3D???
    Checkbox enableSel = new Checkbox("Enable Selection",true);
    Checkbox useThreeD = new Checkbox("3D",true);
    Checkbox toggleTicks = new Checkbox("Ticks",false);
	public SliderTester() {
		super("Slider");
		
        toggleTicks.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            }
        });
        horiz.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mainOne.setOrientation(horiz.getState() ? SSlider.HORIZONTAL:SSlider.VERTICAL);
            }
        });

                // set direciton parameters for pointer
        noDir.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (noDir.getState()) {
                    mainOne.setPointerDirection(SSlider.NO_DIRECTION);
                }
            }
        });
        blDir.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (blDir.getState()) {
                    mainOne.setPointerDirection(SSlider.BOTTOM_LEFT);
                }
            }
        });
        trDir.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (trDir.getState()) {
                    mainOne.setPointerDirection(SSlider.TOP_RIGHT);
                }
            }
        });

        noFill.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (noFill.getState()) {
                    mainOne.setFillType(SSlider.NO_FILL);
                }
            }
        });
        plFill.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (plFill.getState()) {
                    mainOne.setFillType(SSlider.PLAIN_FILL);
                }
            }
        });
        blFill.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (blFill.getState()) {
                    mainOne.setFillType(SSlider.BLOCK_FILL);
                }
            }
        });
        lnFill.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (lnFill.getState()) {
                    mainOne.setFillType(SSlider.LINE_FILL);
                }
            }
        });

        enableSel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mainOne.enableSelection(enableSel.getState());
            }
        });
        useThreeD.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mainOne.setThreeD(useThreeD.getState());
            }
        });

        setLayout(new BorderLayout());
        add("Center",mainOne);
        Panel left = new Panel(new GridLayout(13,1,2,2));
        left.add(noDir);
        left.add(blDir);
        left.add(trDir);
        left.add(horiz);
        left.add(noFill);
        left.add(plFill);
        left.add(blFill);
        left.add(lnFill);
        left.add(enableSel);
        left.add(useThreeD);
        left.add(toggleTicks);
        add("East",left);

                // now text boxes to control aspects of slider...
        setBounds(150,150,300,300);
        setVisible(true);
	}
}
