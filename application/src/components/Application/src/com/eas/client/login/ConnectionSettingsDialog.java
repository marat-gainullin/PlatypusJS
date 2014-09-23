/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ConnectionSettingsDialog.java
 *
 * Created on Jun 25, 2009, 8:49:30 PM
 */
package com.eas.client.login;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author pk
 */
public class ConnectionSettingsDialog extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    private int returnStatus = RET_CANCEL;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/eas/client/login/Bundle");
    private final AddConnectionAction addConnectionAction = new AddConnectionAction();

    /**
     * Creates new form ConnectionSettingsDialog
     *
     * @param parent
     * @param modal
     */
    public ConnectionSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tfConnectionUrl.requestFocus();
        getRootPane().setDefaultButton(btnOk);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        tfName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfConnectionUrl = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnSelectH2File = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("ConnectionSettingsDialog.title")); // NOI18N
        setLocationByPlatform(true);

        btnCancel.setText(bundle.getString("Dialog.CancelButton.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setAction(addConnectionAction);
        btnOk.setText(bundle.getString("Dialog.OKButton.text")); // NOI18N

        jLabel4.setLabelFor(tfName);
        jLabel4.setText(bundle.getString("ConnectionSettingsDialog.lblConnectionTitle.text")); // NOI18N

        jLabel1.setLabelFor(tfConnectionUrl);
        jLabel1.setText(bundle.getString("ConnectionSettingsDialog.lblConnectionUrl.text")); // NOI18N

        btnSelectH2File.setText("...");
        btnSelectH2File.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectH2FileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tfName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(tfConnectionUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectH2File, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfConnectionUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnSelectH2File))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCancelActionPerformed
    {//GEN-HEADEREND:event_btnCancelActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectH2FileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectH2FileActionPerformed
        JFileChooser chooser = new JFileChooser();
        for (FileFilter ff : chooser.getChoosableFileFilters()) {
            chooser.removeChoosableFileFilter(ff);
        }
        chooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".h2.db");
            }

            @Override
            public String getDescription() {
                return bundle.getString("h2DatabaseFile");
            }
        });
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(bundle.getString("h2DatabaseFileSelector"));
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null) && chooser.getSelectedFile() != null) {
            tfConnectionUrl.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSelectH2FileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSelectH2File;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField tfConnectionUrl;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the returnStatus
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    public String getUrl() {
        return tfConnectionUrl.getText().trim();
    }

    public String getConnectionName() {
        return tfName.getText();
    }

    public void setUrl(String url) {
        tfConnectionUrl.setText(url);
    }

    public void setConnectionName(String name) {
        tfName.setText(name);
    }

    private class AddConnectionAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            String url = tfConnectionUrl.getText();
            if (url != null) {
                url = url.trim();
            }
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(ConnectionSettingsDialog.this, bundle.getString("ConnectionSettingsDialog.EmptyUrlMessage"), bundle.getString("ConnectionSettingsDialog.BadSettingsMessage"), JOptionPane.ERROR_MESSAGE);
                tfConnectionUrl.requestFocus();
            } else {
                doClose(RET_OK);
            }
        }
    }
}
