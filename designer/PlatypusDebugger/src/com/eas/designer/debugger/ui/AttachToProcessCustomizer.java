/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AttachToProcessCustomizer.java
 *
 * Created on 18.03.2011, 18:46:14
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.AttachSettings;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.netbeans.spi.debugger.ui.Controller;

/**
 *
 * @author mg
 */
 public class AttachToProcessCustomizer extends javax.swing.JPanel {

    protected AttachSettings settings;
    protected Controller controller;

    /**
     * Creates new form AttachToProcessCustomizer
     */
    public AttachToProcessCustomizer(AttachSettings aSettings, Controller aController) {
        initComponents();
        settings = aSettings;
        controller = aController;
        txtHost.setText(settings.getHost());
        txtPort.setText(String.valueOf(settings.getPort()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHost = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();

        lblHost.setText(org.openide.util.NbBundle.getMessage(AttachToProcessCustomizer.class, "AttachToProcessCustomizer.lblHost.text")); // NOI18N
        lblHost.setToolTipText(org.openide.util.NbBundle.getMessage(AttachToProcessCustomizer.class, "AttachToProcessCustomizer.lblHost.tooltiptext")); // NOI18N

        txtHost.setToolTipText(org.openide.util.NbBundle.getMessage(AttachToProcessCustomizer.class, "AttachToProcessCustomizer.lblHost.tooltiptext")); // NOI18N
        txtHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHostActionPerformed(evt);
            }
        });
        txtHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHostFocusLost(evt);
            }
        });

        lblPort.setText(org.openide.util.NbBundle.getMessage(AttachToProcessCustomizer.class, "AttachToProcessCustomizer.lblPort.text")); // NOI18N

        txtPort.setToolTipText(org.openide.util.NbBundle.getMessage(AttachToProcessCustomizer.class, "AttachToProcessCustomizer.txtPort.tooltiptext")); // NOI18N
        txtPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortActionPerformed(evt);
            }
        });
        txtPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPortFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblPort, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtHost, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHost)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPort)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtHostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHostActionPerformed
        settings.setHost(txtHost.getText());
        tryToCommit();
    }//GEN-LAST:event_txtHostActionPerformed

    private void txtHostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHostFocusLost
        settings.setHost(txtHost.getText());
    }//GEN-LAST:event_txtHostFocusLost

    private void txtPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortActionPerformed
        settings.setPort(Integer.valueOf(txtPort.getText()));
        tryToCommit();
    }//GEN-LAST:event_txtPortActionPerformed

    private void txtPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPortFocusLost
        settings.setPort(Integer.valueOf(txtPort.getText()));
    }//GEN-LAST:event_txtPortFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblPort;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtPort;
    // End of variables declaration//GEN-END:variables

    private void tryToCommit() {
        if (controller.isValid()) {
            Window w = SwingUtilities.getWindowAncestor(this);// ugly hack
            if (w instanceof JDialog) {
                JButton btn = ((JDialog)w).getRootPane().getDefaultButton();
                if(btn != null){
                    btn.getModel().setArmed(true);
                    btn.getModel().setPressed(true);
                    btn.getModel().setPressed(false);
                    btn.getModel().setArmed(false);
                }
            }
        }
    }
}
