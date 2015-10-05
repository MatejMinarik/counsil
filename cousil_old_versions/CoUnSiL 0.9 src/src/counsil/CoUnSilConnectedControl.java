/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counsil;

import core.ControlPeer;
import core.Main;
import counsil.UserController;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import p2p.CoUniverseMessage;
import p2p.MessageType;
import p2p.NetworkConnector;
import p2p.NodeGroupIdentifier;

/**
 *
 * @author Jarek
 */
public class CoUnSilConnectedControl extends javax.swing.JFrame implements counsil.Displayable {

    private boolean raisedHand = false;
    private final NetworkConnector networkConnector;

    /**
     * Creates new form CoUnSilConnectedControl
     *
     */
    public CoUnSilConnectedControl() {
        initComponents();
        System.out.println("My role is:" + Main.getUniversePeer().getLocalNode().getMyEndpointUserRole().getMyRole());
        if(Main.getUniversePeer().getLocalNode().getMyEndpointUserRole().getMyRole().equals("interpreter")) {
            System.out.println("Removing button");
            getContentPane().remove(WantToTalkButton);
            invalidate();
            repaint();
        }
        networkConnector = ControlPeer.getNetworkConnector();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        WantToTalkButton = new javax.swing.JButton();
        DisconnectButton = new javax.swing.JButton();
        ResetLayoutJButton = new javax.swing.JButton();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);

        WantToTalkButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        WantToTalkButton.setText("Raise hand");
        WantToTalkButton.setMaximumSize(new java.awt.Dimension(109, 25));
        WantToTalkButton.setMinimumSize(new java.awt.Dimension(109, 25));
        WantToTalkButton.setPreferredSize(new java.awt.Dimension(117, 31));
        WantToTalkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WantToTalkButtonActionPerformed(evt);
            }
        });

        DisconnectButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        DisconnectButton.setText("Quit");
        DisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectButtonActionPerformed(evt);
            }
        });

        ResetLayoutJButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ResetLayoutJButton.setText("Reset layout");
        ResetLayoutJButton.setPreferredSize(new java.awt.Dimension(117, 31));
        ResetLayoutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetLayoutJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DisconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(WantToTalkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(ResetLayoutJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(WantToTalkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResetLayoutJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DisconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void WantToTalkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WantToTalkButtonActionPerformed
        if (raisedHand) {
            WantToTalkButton.setText("Raise hand");
            networkConnector.sendMessageToGroup(new CoUniverseMessage(MessageType.COUNSIL_DO_NOT_WANT_TO_TALK, new Serializable[]{Main.getUniversePeer().getLocalNode()}, networkConnector.getConnectorID(), null), NodeGroupIdentifier.ALL_NODES);

        } else {
            WantToTalkButton.setText("Lower hand");
            networkConnector.sendMessageToGroup(new CoUniverseMessage(MessageType.COUNSIL_WANT_TO_TALK, new Serializable[]{Main.getUniversePeer().getLocalNode()}, networkConnector.getConnectorID(), null), NodeGroupIdentifier.ALL_NODES);

        }
        raisedHand = !raisedHand;

    }//GEN-LAST:event_WantToTalkButtonActionPerformed

    private void DisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectButtonActionPerformed
        String message = "Do you really want to quit CoUnSil?";
        String title = "Quit CoUnSil?";
        int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            quitCoUnSil();
        }
    }//GEN-LAST:event_DisconnectButtonActionPerformed

    private void ResetLayoutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetLayoutJButtonActionPerformed
        // TODO add your handling code here:
        UserController.getInstance().refreshLayout();
    }//GEN-LAST:event_ResetLayoutJButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CoUnSilConnectedControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CoUnSilConnectedControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CoUnSilConnectedControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CoUnSilConnectedControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CoUnSilConnectedControl().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DisconnectButton;
    private javax.swing.JButton ResetLayoutJButton;
    private javax.swing.JButton WantToTalkButton;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void resize(int x, int y, int width, int height) {
        setLocation(x, y);
        setSize(width, height);
        repaint();
    }

    @Override
    public void changePosition(int x, int y) {
        setLocation(x, y);
        repaint();
    }

    private void quitCoUnSil() {
        Main.getUniversePeer().applicationControl().stopMediaApplications();
        Main.getUniversePeer().leaveUniverse();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoUnSilConnectedControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
        System.exit(0);
    }
}
