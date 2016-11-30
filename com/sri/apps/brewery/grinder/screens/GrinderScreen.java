package com.sri.apps.brewery.grinder.screens;

import java.util.*;
import java.io.*;
import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.io.*;

/**
 * This is the interface for all Screens that need to be shown by an
 * installer.
 *
 * This an interface rather than a GUI class because it should be possible
 * for the screen to be either a GUI based one OR a text/console based
 * screen.  
 *
 * What are all the possible types of screens?
 *
 * GUI and Console are two possibilities.
 *
 * Also, the functions here are called by the Grinder object.
 *
 * When the screen class is first loaded (with all the parameters) its
 * initialise() function is invoked.  This function is called ONLY once.
 *
 * Then when a screen is to be entered from another screen, its onEntry
 * function is called before it is displayed.
 *
 * when the Next and Previous buttons are pressed, the onForward and
 * onBackwards are called before the screen is closed and the next screen
 * is displayed.
 *
 * When the cancel button is pressed, the screen's onCancel function is 
 * invoked before the screen and its parent dialog box are closed.
 *
 * However, the best pattern here is the Bridge design pattern.  There will
 * be an implementation that will do things like, persistance (reading and
 * writing screen info to and from streams), building GUI to represent
 * particular aspects of the screens.
 *
 * @author Sri Panyam
 */
public interface GrinderScreen extends Persistable
{
        /**
         * Get the title of this screen.
         */
    public String getTitle();

        /**
         * Get the description of this screen.
         */
    public String getDescription();

        /**
         * Initialise the screen.
         */
    public void initialise() throws Exception;

        /**
         * Sets the Grinder that will coordinate and control
         * the screens.
         */
    public void setEnvironment(Grinder grinder);

        /**
         * Called when this state is entered.
         *
         * This is like an initialisation code for each entry 
         * into this screen.
         */
    public void onEntry(GrinderScreen from);

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "previous" button is pressed.
         *
         * The return value is whether it is possible to exit from this
         * screen or not.
         */
    public boolean onBackward(GrinderScreen to);
    //{ return true; }

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "cancel" button is pressed.
         */
    public Object onCancel();
    //{ return null; }

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "next" button is pressed.
         * This also includes the case if the "finish" button was pressed.
         *
         * The return value is whether it is possible to exit from this
         * screen or not.
         */
    public boolean onForward(GrinderScreen to);
    //{ return true; }
}
