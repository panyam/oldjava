
package svtool.gui.dialogs;

import svtool.*;
import svtool.core.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 

/***    XML Classes     ***/
import  org.w3c.dom.*;
import  org.apache.xerces.dom.DocumentImpl;
import  org.apache.xerces.dom.DOMImplementationImpl;
import  org.w3c.dom.Document;
import  org.apache.xml.serialize.OutputFormat;
import  org.apache.xml.serialize.Serializer;
import  org.apache.xml.serialize.SerializerFactory;
import  org.apache.xml.serialize.XMLSerializer;
import javax.xml.parsers.*;

/**
 * Dialog for handling the connections.
 */
public class ProgressDialog extends OkCancelDialog
{
        /**
         * Shows the status of this task as a value.
         *
         * The display is as a x/y.  Or as a percent.
         */
    protected JLabel statusValueLabel = new JLabel("Completed 0%");

        /**
         * Shows the status message of the task that is running.
         */
    protected JLabel statusMessageLabel = new JLabel("Task Message.");

        /**
         * The bar to show the progress.
         */
    protected JProgressBar progressBar = new JProgressBar(0, 1000);

        /**
         * The timer to monitor the set task at set intervals.
         */
    protected javax.swing.Timer taskTimer = new javax.swing.Timer(50, this);

        /**
         * The current task that is running.
         */
    protected Task currTask = null;

        /**
         * Constructor.
         *
         * @param   parent  The parent frame.
         *          title   The title of the dialog box.
         *          modal   Tells if the dialog box should be modal or not.
         *
         * @author Sri Panyam
         */
    public ProgressDialog(JFrame parent)
    {
        super(parent, "Progress Dialog", true);

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel centerSouthPanel = new JPanel(new GridLayout(2, 1));

        centerSouthPanel.add(progressBar);
        centerSouthPanel.add(statusValueLabel);

        centerPanel.add("North", statusMessageLabel);
        centerPanel.add("South", centerSouthPanel);

        getContentPane().add("Center", centerPanel);

        wasCancelled = false;
        setVisible(false);
        setResizable(true);
        setLocationRelativeTo(parent);
        pack();

        setSize(400, 150);
    }

        /**
         * Starts a given task and runs it.
         *
         * Waits for the task to finish and then hides.  When the dialog
         * loads it will be loaded in a modal way or a non modal way.
         *
         * @param   task    The current task to be executed.
         *          title   The title of the dialog box to indicate the
         *                  current task.
         *          modal   Indicates whether the dialog is to be modal or
         *                  not.
         *
         * @author  Sri Panyam
         */
    public void startAndShowTask(Task task, String title, boolean modal)
    {
        setTitle(title);
        setModal(modal);

        if (currTask != null && currTask.getTaskStatus() == Task.EXECUTING)
        {
            this.currTask.stopTask();
        }
        taskTimer.stop();

        this.currTask = task;
        taskTimer.start();
        currTask.startTask();
        progressBar.setIndeterminate(true);

        setVisible(true);
        toFront();
    }

        /**
         * Action event handler.
         *
         * @param   ae  The action event to handle.
         */
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == taskTimer)
        {
            int taskStatus = currTask.getTaskStatus();
            if (taskStatus == Task.EXECUTING)
            {
                int min = currTask.getMinimumValue();
                int max = currTask.getMaximumValue();
                int curr = currTask.getCurrentValue();
                double pcent = currTask.getPercentComplete();

                if (pcent >= 0)
                {
                    progressBar.setIndeterminate(false);
                    progressBar.setMinimum(min);
                    progressBar.setMaximum(max);
                    progressBar.setValue(curr);
                    statusValueLabel.setText(
                            "Completed " + curr + " of " +  max + ".");
                    statusMessageLabel.setText(currTask.getMessage());
                } else
                {
                    statusValueLabel.setText("Executing...");
                }
            } else
            {
                progressBar.setValue(0);
                taskTimer.stop();
                progressBar.setString(
                        taskStatus == Task.SUCCEEDED ?
                            "Completed." : "Failed.");
                progressBar.setIndeterminate(false);

                wasCancelled = false;
                setVisible(false);
            }
        } else
        {
            if (ae.getSource() == cancelButton)
            {
                taskTimer.stop();
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
                statusValueLabel.setText("Cancelled.");
                currTask.stopTask();
            }
            super.actionPerformed(ae);
        }
    }
}
