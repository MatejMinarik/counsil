/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counsil;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle;

/**
 *
 * @author desanka
 */
public class InteractionMenu extends JFrame {
    
    /**
     * Represents current state of raise hand button
     */
    private boolean raisedHand;
    
    /**
     * Represents buttons in current menu instance
     */
    private final List<JButton> buttons;

    /**
     * Represents button types
     */
    private enum ButtonType { 
        ABOUT, EXIT, ATTENTION, MUTE, VOLUME 
    }
    
    /**
     * Represents current state of sound
     */
    private boolean muted;
    
    /**
     * Initializes menu
     * @param role role of current user
     * @param position menu position
     */
    public InteractionMenu(String role, Position position){    
        
        super("CoUnSil");         
        setLayout(new GridBagLayout());
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));        
        setBackground(new Color(0, 0, 0, (float) 1)); 
        setAlwaysOnTop(true);
        setResizable(false);
        setSize(150, 200);
        setLocationRelativeTo(null);
        setLocation(position.x, position.y);
        setVisible(true);
        
        buttons = new ArrayList<>();
        raisedHand = false;
        muted = false;
        initComponents(getButtonsByRole(role));   
      
        buttons.stream().forEach((button) -> {
            add(button);
        });
     
        JFrame.setDefaultLookAndFeelDecorated(true);
    }
    
   /**
    * Creates list of button types according to user role
    * @param role
    * @return list of buttons types, which will menu contain
    */
    private List<InteractionMenu.ButtonType> getButtonsByRole(String role){
         List<InteractionMenu.ButtonType> list = new ArrayList<>();
                  
         if ("student".equals(role.toLowerCase())){
              list.add(InteractionMenu.ButtonType.ATTENTION);             
         }
         else {
             list.add(InteractionMenu.ButtonType.MUTE);
             list.add(InteractionMenu.ButtonType.VOLUME);
         }
         
         list.add(InteractionMenu.ButtonType.ABOUT);
         list.add(InteractionMenu.ButtonType.EXIT);
         
         return list;
    }
    
    /**
     * Creates buttons for menu, according to button types
     * @param descriptions types of buttons, to be used
     */
    private void initComponents(List<InteractionMenu.ButtonType> descriptions){   
       
        for (ButtonType type : descriptions) {
            
            JButton button = new JButton();
            button.setFont(new java.awt.Font("Tahoma", 0, 18));
            button.setMaximumSize(new java.awt.Dimension(150, 25));
            button.setMinimumSize(new java.awt.Dimension(109, 25));
            button.setPreferredSize(new java.awt.Dimension(130, 31));

            setSpecificAttributes(button, type);  
            buttons.add(button);        
        }        
        
        setAlwaysOnTop(true);  
        setResizable(false);        
       
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);        
                   
        GroupLayout.ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        buttons.stream().forEach((button) -> {
            hGroup.addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        });
               
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        buttons.stream().forEach((button) -> {
            vGroup.addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        });
        
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);

        pack();  
 
    }
    
    /**
     * Sets specific attributes to menu button (title, action, ...)
     * @param button instance of button
     * @param type type to be button associated with
     */
    private void setSpecificAttributes (JButton button, InteractionMenu.ButtonType type){
       
       if (type == InteractionMenu.ButtonType.EXIT){
           button.setText("Exit");
           button.addActionListener((ActionEvent evt) -> {
               ExitButtonActionPerformed();
           });
       }
       else if (type == InteractionMenu.ButtonType.ABOUT){
           button.setText("About");
           button.addActionListener((ActionEvent evt) -> {
               AboutButtonActionPerformed();
           });
       }
       else if (type == InteractionMenu.ButtonType.ATTENTION){
            button.setText("Raise hand");
            button.addActionListener((ActionEvent evt) -> {
                AttentionButtonActionPerformed(button);
            });
       }
       else if (type == InteractionMenu.ButtonType.MUTE){
           button.setText("Mute");
           button.addActionListener((ActionEvent evt) -> {
               MuteButtonActionPerformed(button);
           });           
       }
       
       else if (type == InteractionMenu.ButtonType.VOLUME){
           button.setText("Volume");
           //! todo
       }     
       
    }   
    
   /**
    * Shows message after "About" button is clicked
    */
   private void AboutButtonActionPerformed() {                                                 
        JOptionPane.showMessageDialog(null, " CoUnSiL\n" +"(CoUniverse for Sign Language)\n" +"\n" +"\n" +
            "Videoconferencing environment for remote interpretation of sign language.");           
    }   
   
    /**
    * Starts exiting program when "Exit" button is clicked
    */
    private void ExitButtonActionPerformed() {                                                 
        String message = "Do you really want to quit CoUnSil?";
        String title = "Quit CoUnSil?";
        int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {                        
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(InteractionMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
            System.exit(0);
        }
    }   
   
    /**
     * Starts raise/lower hand interaction when button is clicked
     * @param button clicked button 
     */
    private void AttentionButtonActionPerformed(JButton button) {                                                 
        if (raisedHand) {
            button.setText("Raise hand");            
        } else {
            button.setText("Lower hand");             
        }
        raisedHand = !raisedHand;

    }  
    
    //! TODO: test this 
    
    /**
     * Mutes/unmutes sound
     * @param button clicked button
     */
    private void MuteButtonActionPerformed(JButton button) {
        if (muted){
            button.setText("Mute");           
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            for (Mixer.Info info: infos) {
                Mixer mixer = AudioSystem.getMixer(info);
                for (Line line : mixer.getSourceLines()){
                    BooleanControl bc = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                    if (bc != null) {
                        bc.setValue(false); 
                    }
                }
            }
        }
        else {
            button.setText("Unmute");
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            for (Mixer.Info info: infos) {
                Mixer mixer = AudioSystem.getMixer(info);
                for (Line line : mixer.getSourceLines()){
                    BooleanControl bc = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                    if (bc != null) {
                        bc.setValue(true); 
                    }
                }
            }
        }
        
        muted = !muted;        
    }
    
}