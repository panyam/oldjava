/*
 * TestForm.java
 *
 * Created on 9 August 2004, 16:27
 */

package com.sri.apps.brewery.wizard;

/**
 *
 * @author  c920884
 */
public class TestForm extends javax.swing.JFrame
{
    
    /** Creates new form TestForm */
    public TestForm ()
    {
        initComponents ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        jPanel1 = new javax.swing.JPanel();
        sectionToolbar = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        sectionPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        sectionToolbar.setRollover(true);
        jToggleButton1.setText("jToggleButton1");
        sectionToolbar.add(jToggleButton1);

        jToggleButton2.setText("jToggleButton2");
        sectionToolbar.add(jToggleButton2);

        jToggleButton3.setText("jToggleButton3");
        sectionToolbar.add(jToggleButton3);

        jPanel1.add(sectionToolbar, java.awt.BorderLayout.SOUTH);

        jPanel1.add(sectionPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Menu");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm (java.awt.event.WindowEvent evt)//GEN-FIRST:event_exitForm
    {
        System.exit (0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main (String args[])
    {
        new TestForm ().show ();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JPanel sectionPanel;
    private javax.swing.JToolBar sectionToolbar;
    // End of variables declaration//GEN-END:variables
    
}
