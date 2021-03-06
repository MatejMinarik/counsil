package counsil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author xminarik
 */
public final class OptionsMainMenuWindow extends JFrame{
    JPanel mainPanel, visualizationPanel, videoAudioPanel, miscsPanel;
    JTabbedPane mainTabPanel;
    Font fontButtons;
    List<VideoDevice> videoDevices;
    List<AudioDevice> audioConsumers;
    List<AudioDevice> audioProducers;
    List<IPServerSaved> ipAddresses;
    boolean correctUv;
    boolean havePortaudio;
    boolean haveCoreaudio;
    boolean haveALSA;
    boolean haveGL;
    boolean haveSDL;
    Process uvProcess;
    JSONObject configuration;
    String uvPathString;
    String layoutPathString;
    JTextField audioStatusTextField;
    JTextField displayStatusTextField;
    JTextField uvStatusTextField;
    Color raiseHandColor;
    Color talkingColor;
    JColorChooser raiseHandcolorChooser;   
    JColorChooser talkingColorChooser;
    JTextField setResizeAmount;
    File configurationFile;
    JTextField myIpSetTextField;
    InitialMenuLayout imt;    //need to reload server choosing window
    String displaySetting;
    boolean presentationUsed;
    boolean studentOnly;
    String role;
    String audioInSettingConfiguration;
    String audioOutSettingConfiguration;
    
    //need to be global so they can be set from different tab
    JComboBox mainCameraBox;
    JComboBox mainCameraPixelFormatBox;
    JComboBox mainCameraFrameSizeBox;
    JComboBox mainCameraFPSBox;
    JComboBox presentationBox;
    JComboBox presentationPixelFormatBox;
    JComboBox presentationFrameSizeBox;
    JComboBox presentationFPSBox;
    JComboBox displayBox;
    JComboBox displaySettingBox;
    JComboBox audioInComboBox;
    JComboBox audioOutComboBox;
    JComboBox languageCombobox;
    JTextField cameraSettingText;
    JTextField presentationSettingText;
    
    ResourceBundle languageBundle;
    
    /**
     * constructor
     */ 
    OptionsMainMenuWindow(Font fontButtons, File configurationFile, InitialMenuLayout initialMenuLayout, ResourceBundle languageBundle, String role)
    {
        super(languageBundle.getString("COUNSIL_OPTIONS"));
        this.fontButtons = fontButtons;
        this.languageBundle = languageBundle;
        videoDevices = new ArrayList<>();
        audioConsumers = new ArrayList<>();
        audioProducers = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        correctUv = false;
        havePortaudio = false;
        haveCoreaudio = false;
        haveALSA = false;
        haveGL = false;
        haveSDL = false;
        uvPathString = "";
        layoutPathString = "";
        audioStatusTextField = new JTextField();
        audioStatusTextField.setEditable(false);
        audioStatusTextField.setBackground(this.getBackground());
        audioStatusTextField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        displayStatusTextField = new JTextField();
        displayStatusTextField.setEditable(false);
        displayStatusTextField.setBackground(this.getBackground());
        displayStatusTextField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        uvStatusTextField = new JTextField();
        uvStatusTextField.setEditable(false);
        uvStatusTextField.setBackground(this.getBackground());
        uvStatusTextField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        raiseHandcolorChooser = new JColorChooser(new Color(0, 0, 0));
        talkingColorChooser = new JColorChooser(new Color(0, 0, 0));
        this.configurationFile = configurationFile;
        this.role = role;
        myIpSetTextField = new JTextField();
        imt = initialMenuLayout;
        
        mainCameraBox = new JComboBox();
        mainCameraPixelFormatBox = new JComboBox();
        mainCameraFrameSizeBox = new JComboBox();
        mainCameraFPSBox = new JComboBox();
        presentationBox = new JComboBox();
        presentationPixelFormatBox = new JComboBox();
        presentationFrameSizeBox = new JComboBox();
        presentationFPSBox = new JComboBox();
        displayBox = new JComboBox();
        displaySettingBox = new JComboBox();
        audioInComboBox = new JComboBox();
        audioOutComboBox = new JComboBox();
        languageCombobox = new JComboBox();
        
        cameraSettingText = new JTextField();
        presentationSettingText = new JTextField();
        cameraSettingText.setEditable(false);
        presentationSettingText.setEditable(false);
        cameraSettingText.setBorder(BorderFactory.createEmptyBorder());
        presentationSettingText.setBorder(BorderFactory.createEmptyBorder());
        
        configuration = readJsonFile(configurationFile);
        
        if(configuration.has("audio producer")){
            try {
                audioInSettingConfiguration = configuration.getString("audio producer");
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(configuration.has("audio consumer")){
            try {
                audioOutSettingConfiguration = configuration.getString("audio consumer");
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        raiseHandColor = new Color(255, 0, 0);
        if(configuration.has("raise hand color")){
            JSONObject raiseHandColorJson;
            try {
                raiseHandColorJson = configuration.getJSONObject("raise hand color");
                if(raiseHandColorJson.has("red") && raiseHandColorJson.has("green") && raiseHandColorJson.has("blue")){
                int red = raiseHandColorJson.getInt("red");
                int green = raiseHandColorJson.getInt("green");
                int blue = raiseHandColorJson.getInt("blue");
                raiseHandColor = new Color(red, green, blue);
            }
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        talkingColor = new Color(0, 255, 255);
        if(configuration.has("talking color")){
            JSONObject raiseHandColorJson;
            try {
                raiseHandColorJson = configuration.getJSONObject("talking color");
                if(raiseHandColorJson.has("red") && raiseHandColorJson.has("green") && raiseHandColorJson.has("blue")){
                    int red = raiseHandColorJson.getInt("red");
                    int green = raiseHandColorJson.getInt("green");
                    int blue = raiseHandColorJson.getInt("blue");
                    talkingColor = new Color(red, green, blue);
                }
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        if(configuration.has("talking resizing")){
            try {
                setResizeAmount = new JTextField(String.valueOf((int)configuration.getDouble("talking resizing")));
            } catch (JSONException ex) {
                setResizeAmount = new JTextField();
            }
        }else{
            setResizeAmount = new JTextField();
        }
        if(configuration.has("presentation")){
            try {
                presentationUsed = configuration.getBoolean("presentation");
            } catch (JSONException ex) {
                presentationUsed = false;
            }
        }else{
            presentationUsed = false;
        }
        if(configuration.has("student only")){
            try {
                studentOnly = configuration.getBoolean("student only");
            } catch (JSONException ex) {
                studentOnly = false;
            }
        }else{
            studentOnly = false;
        }
        setResizeAmount.setBorder(BorderFactory.createEmptyBorder());
        uvProcess = null;
        mainPanel = new JPanel();
        setLayout(new GridBagLayout());
        visualizationPanel = new JPanel();
        videoAudioPanel = new JPanel();
        videoAudioPanel.setLayout(new GridBagLayout());
        miscsPanel = new JPanel();
        mainTabPanel = new JTabbedPane();
        mainTabPanel.addTab(languageBundle.getString("VISUALIZATION"), visualizationPanel);
        mainTabPanel.addTab(languageBundle.getString("AUDIO_VIDEO"), videoAudioPanel);
        mainTabPanel.addTab(languageBundle.getString("MISC"), miscsPanel);
        
        addWindowListener(new WindowAdapter() {//action on close button (x)
            @Override
            public void windowClosing(WindowEvent e) {
                if(uvProcess != null){
                    uvProcess.destroy();
                }
                dispose();
            }
        });
        
        JButton saveButton = new JButton(languageBundle.getString("SAVE"));
        saveButton.setFont(fontButtons);
        saveButton.addActionListener((ActionEvent event) -> {
            if(uvProcess != null){
                uvProcess.destroy();
            }
            saveSettingAction();
        });
        JButton discardButton = new JButton(languageBundle.getString("DISCARD"));
        discardButton.setFont(fontButtons);
        discardButton.addActionListener((ActionEvent event) -> {
            if(uvProcess != null){
                uvProcess.destroy();
            }
            dispose();
        });
        setVisualizationPanel();
        setVideoAudioPanel();
        setMiscsPanel();
        
        //setting fields
                
        //try if ultragrid is functional
        ultragridOK(uvPathString);
        //load posibylities
        try {
            videoDevices = loadVideoDevicesAndSettings(uvPathString);
            addTestcrdDevice(videoDevices);
            audioConsumers = read_audio_devices_in_or_out(uvPathString, false);
            audioProducers = read_audio_devices_in_or_out(uvPathString, true);
        } catch (IOException ex) {
            videoDevices = new ArrayList<>();
            audioConsumers = new ArrayList<>();
            audioProducers = new ArrayList<>();
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        setAudioJComboBox(audioInComboBox, audioInSettingConfiguration, audioProducers);
        setAudioJComboBox(audioOutComboBox, audioOutSettingConfiguration, audioConsumers);
        
        setAllJComboBoxesVideosetting(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
        setAllJComboBoxesVideosetting(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        
        setJComboBoxDisplay(displayBox, displaySettingBox);
        
        try {
            String videoSetting = configuration.getString("producer settings");
            SetVideoSettingFromConfig(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, videoDevices, videoSetting);
            if(presentationUsed){
                String presentationSetting = configuration.getString("presentation producer");
                SetVideoSettingFromConfig(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, videoDevices, presentationSetting);
            }
        } catch (JSONException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        GridBagConstraints mainPanelConstraints = new GridBagConstraints();
        mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanelConstraints.weightx = 0.5;
        mainPanelConstraints.gridx = 0;
        mainPanelConstraints.gridy = 0;
        mainPanelConstraints.gridheight = 1;
        mainPanelConstraints.gridwidth = 3;
        add(mainTabPanel, mainPanelConstraints);
        mainPanelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        mainPanelConstraints.ipady = 0;
        mainPanelConstraints.weightx = 0.5;
        mainPanelConstraints.gridx = 1;
        mainPanelConstraints.gridy = 1;
        mainPanelConstraints.gridheight = 1;
        mainPanelConstraints.gridwidth = 1;
        add(saveButton, mainPanelConstraints);
        mainPanelConstraints.weightx = 0.5;
        mainPanelConstraints.gridx = 2;
        mainPanelConstraints.gridy = 1;
        mainPanelConstraints.gridheight = 1;
        mainPanelConstraints.gridwidth = 1;
        add(discardButton, mainPanelConstraints);
        setVisible(true);
        setResizable(false);
        pack();

    }

    /**
     * set visualization panel
     */
    private void setVisualizationPanel(){
        JPanel raiseHandColorPanel = new JPanel();
        JPanel talkingColorPanel = new JPanel();
        JPanel resizingSizePanel = new JPanel();
        JPanel languagePanel = new JPanel();
        
        JTextField languageInfoTextField = new JTextField(languageBundle.getString("LANGUAGE"));
        languageInfoTextField.setEditable(false);
        languageInfoTextField.setBorder(BorderFactory.createEmptyBorder());
        languageCombobox.setEditable(false);
        languageCombobox.setLightWeightPopupEnabled(true);
        String setLanguage = "";
        try {
            setLanguage = configuration.getString("language");
        } catch (JSONException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        fillLanguageComboBox(languageCombobox, setLanguage);
        languagePanel.setBorder(new TitledBorder(languageBundle.getString("LANGUAGE")));     
               
        JTextField setResizeAmountTextInfo = new JTextField(languageBundle.getString("RESIZING_INFO_TEXT"));
        JTextField setResizePixelSign = new JTextField(languageBundle.getString("PIXELS")); 
        setResizeAmountTextInfo.setBorder(BorderFactory.createEmptyBorder());
        setResizePixelSign.setBorder(BorderFactory.createEmptyBorder());
        setResizeAmount.setEditable(true);
        setResizeAmountTextInfo.setEditable(false);
        setResizePixelSign.setEditable(false); 
        setResizeAmountTextInfo.setHorizontalAlignment(JTextField.RIGHT);
        setResizeAmount.setHorizontalAlignment(JTextField.CENTER);
        setResizePixelSign.setHorizontalAlignment(JTextField.LEFT);
        setResizeAmount.setColumns(3);
        resizingSizePanel.setBorder(new TitledBorder(languageBundle.getString("RESIZING")));
        
        
        raiseHandcolorChooser.setPreviewPanel(new CustomPreviewPanel(new Dimension(100, 100)));
        raiseHandcolorChooser.setLayout(new FlowLayout());
        //Remove the default chooser panels
        AbstractColorChooserPanel raiseHandColorPanelsToRemove[] = raiseHandcolorChooser.getChooserPanels();
        
        //VR: opraveno na advanced for-loop
        for (AbstractColorChooserPanel raiseHandColorPanelsToRemove1 : raiseHandColorPanelsToRemove) {
            raiseHandcolorChooser.removeChooserPanel(raiseHandColorPanelsToRemove1);
        }
        raiseHandcolorChooser.addChooserPanel(new RGBChooserPanel(languageBundle));
        raiseHandcolorChooser.setColor(raiseHandColor);
        raiseHandColorPanel.setBorder(new TitledBorder(languageBundle.getString("COLOR_RISE_HAND")));
        raiseHandColorPanel.add(raiseHandcolorChooser);
        
        
        talkingColorChooser.setPreviewPanel(new CustomPreviewPanel(new Dimension(100, 100)));
        talkingColorChooser.setLayout(new FlowLayout());
        //Remove the default chooser panels
        AbstractColorChooserPanel talkingColorPanelsToRemove[] = talkingColorChooser.getChooserPanels();
        //VR: opraveno na advanced for-loop
        for (AbstractColorChooserPanel talkingColorPanelsToRemove1 : talkingColorPanelsToRemove) {
            talkingColorChooser.removeChooserPanel(talkingColorPanelsToRemove1);
        }
        talkingColorChooser.addChooserPanel(new RGBChooserPanel(languageBundle));
        talkingColorChooser.setColor(talkingColor);
        talkingColorPanel.setBorder(new TitledBorder(languageBundle.getString("COLOR_TALKING")));
        talkingColorPanel.add(talkingColorChooser);
        
        if(languageBundle.containsKey("VIS_TOOL_TIP_LANGUAGE")){
            languageCombobox.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_LANGUAGE"));
        }
        if(languageBundle.containsKey("VIS_TOOL_TIP_RESIZING")){
            setResizeAmountTextInfo.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_RESIZING"));
            setResizeAmount.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_RESIZING"));
            setResizePixelSign.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_RESIZING"));
        }
        if(languageBundle.containsKey("VIS_TOOL_TIP_COLOR_RISE_HAND")){
            raiseHandColorPanel.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_COLOR_RISE_HAND"));
        }
        if(languageBundle.containsKey("VIS_TOOL_TIP_COLOR_TALKING")){
            talkingColorPanel.setToolTipText(languageBundle.getString("VIS_TOOL_TIP_COLOR_TALKING"));
        }
        
        languagePanel.setLayout(new GridBagLayout());
        GridBagConstraints languagePanelConstrains = new GridBagConstraints();
        languagePanelConstrains.insets = new Insets(5,5,5,5);
        languagePanelConstrains.weightx = 0.5;
        languagePanelConstrains.gridheight = 1;
        languagePanelConstrains.gridwidth = 1;
        languagePanelConstrains.gridx = 1;
        languagePanelConstrains.gridy = 0;
        languagePanelConstrains.anchor = GridBagConstraints.CENTER;
        languagePanel.add(languageCombobox, languagePanelConstrains);
        
        resizingSizePanel.setLayout(new GridBagLayout());
        GridBagConstraints resizingSizePanelConstrains = new GridBagConstraints();
        resizingSizePanelConstrains.insets = new Insets(5,5,5,5);
        //resizingSizePanelConstrains.weightx = 0.5;
        resizingSizePanelConstrains.gridheight = 1;
        resizingSizePanelConstrains.gridwidth = 1;
        resizingSizePanelConstrains.gridx = 0;
        resizingSizePanelConstrains.gridy = 0;
        resizingSizePanel.add(setResizeAmountTextInfo, resizingSizePanelConstrains);
        resizingSizePanelConstrains.gridx = 1;
        resizingSizePanelConstrains.gridy = 0;
        resizingSizePanel.add(setResizeAmount, resizingSizePanelConstrains);
        resizingSizePanelConstrains.gridx = 2;
        resizingSizePanelConstrains.gridy = 0;
        resizingSizePanel.add(setResizePixelSign, resizingSizePanelConstrains);
        
        visualizationPanel.setLayout(new GridBagLayout());
        GridBagConstraints visualizationPanelConstrains = new GridBagConstraints();
        visualizationPanelConstrains.fill = GridBagConstraints.HORIZONTAL;
        visualizationPanelConstrains.weightx = 0.5;
        visualizationPanelConstrains.gridheight = 1;
        visualizationPanelConstrains.gridwidth = 1;
        visualizationPanelConstrains.gridx = 0;
        visualizationPanelConstrains.gridy = 0;
        visualizationPanel.add(languagePanel, visualizationPanelConstrains);
        visualizationPanelConstrains.gridx = 0;
        visualizationPanelConstrains.gridy = 1;
        visualizationPanel.add(resizingSizePanel, visualizationPanelConstrains);
        visualizationPanelConstrains.gridx = 0;
        visualizationPanelConstrains.gridy = 2;
        visualizationPanel.add(raiseHandColorPanel, visualizationPanelConstrains);
        visualizationPanelConstrains.gridx = 0;
        visualizationPanelConstrains.gridy = 3;
        visualizationPanel.add(talkingColorPanel, visualizationPanelConstrains);
    }
    
    /**
     * set video and audio panel
     */
    private void setVideoAudioPanel(){
        JPanel mainCameraPanel = new JPanel();
        mainCameraPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("CAMERA")));
        JPanel presetationPanel = new JPanel();
        presetationPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("PRESENTATION")));
        JPanel displayPanel = new JPanel();
        displayPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("DISPLAY")));
        JPanel audioPanel = new JPanel();
        audioPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("AUDIO")));
        
        //setting boxes, buttons and lables
        //boxes
        //createt gloabay so they can be change globay
        
        //set not editable
        mainCameraBox.setEditable(false);
        mainCameraPixelFormatBox.setEditable(false);
        mainCameraFrameSizeBox.setEditable(false);
        mainCameraFPSBox.setEditable(false);
        presentationBox.setEditable(false);
        presentationPixelFormatBox.setEditable(false);
        presentationFrameSizeBox.setEditable(false);
        presentationFPSBox.setEditable(false);
        displayBox.setEditable(false);
        displaySettingBox.setEditable(false);
        audioInComboBox.setEditable(false);
        audioOutComboBox.setEditable(false);
       
        //set action
        mainCameraBox.addActionListener((ActionEvent event) -> {
            actionSetCameraDeviceBox(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
        });
        mainCameraPixelFormatBox.addActionListener((ActionEvent event) -> {
            actionSetCameraPixelFormatBox(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
        });
        mainCameraFrameSizeBox.addActionListener((ActionEvent event) -> {
            actionSetCameraFrameSizeBox(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
        });
        mainCameraFPSBox.addActionListener((ActionEvent event) -> {
            actionSetFPSBox(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
        });
        presentationBox.addActionListener((ActionEvent event) -> {
            actionSetCameraDeviceBox(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        });
        presentationPixelFormatBox.addActionListener((ActionEvent event) -> {
            actionSetCameraPixelFormatBox(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        });
        presentationFrameSizeBox.addActionListener((ActionEvent event) -> {
            actionSetCameraFrameSizeBox(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        });
        presentationFPSBox.addActionListener((ActionEvent event) -> {
            actionSetFPSBox(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        });

        //info fields
        JTextField displayDeviceText = new JTextField(languageBundle.getString("DISPLAY_SETTING"));
        JTextField displaySettingText = new JTextField(languageBundle.getString("DISPLAY_SETTING_ADVANCED"));
        JTextField cameraDeviceText = new JTextField(languageBundle.getString("CAMERA"));
        JTextField cameraPixelFormatText = new JTextField(languageBundle.getString("FORMAT"));
        JTextField cameraFrameSizeText = new JTextField(languageBundle.getString("SIZE"));
        JTextField cameraFPSText = new JTextField(languageBundle.getString("FPS"));
        JTextField presentationDeviceText = new JTextField(languageBundle.getString("DEVICE"));
        JTextField presentationPixelFormatText = new JTextField(languageBundle.getString("FORMAT"));
        JTextField presentationFrameSizeText = new JTextField(languageBundle.getString("SIZE"));
        JTextField presentationFPSText = new JTextField(languageBundle.getString("FPS"));
        JTextField audioInText = new JTextField(languageBundle.getString("AUDIO_IN"));
        JTextField audioOutText = new JTextField(languageBundle.getString("AUDIO_OUT"));
      	JTextField cameraSettingInfoText = new JTextField(languageBundle.getString("DEVICE_SETTING"));
        JTextField presentationSettingInfoText = new JTextField(languageBundle.getString("DEVICE_SETTING"));

        displayDeviceText.setEditable(false);
        displaySettingText.setEditable(false);
        cameraDeviceText.setEditable(false);
        cameraPixelFormatText.setEditable(false);
        cameraFrameSizeText.setEditable(false);
        cameraFPSText.setEditable(false);
        presentationDeviceText.setEditable(false);
        presentationPixelFormatText.setEditable(false);
        presentationFrameSizeText.setEditable(false);
        presentationFPSText.setEditable(false);
        audioInText.setEditable(false);
        audioOutText.setEditable(false);
        cameraSettingInfoText.setEditable(false);
        presentationSettingInfoText.setEditable(false);
        displayDeviceText.setBorder(BorderFactory.createEmptyBorder());
        displaySettingText.setBorder(BorderFactory.createEmptyBorder());
        cameraDeviceText.setBorder(BorderFactory.createEmptyBorder());
        cameraPixelFormatText.setBorder(BorderFactory.createEmptyBorder());
        cameraFrameSizeText.setBorder(BorderFactory.createEmptyBorder());
        cameraFPSText.setBorder(BorderFactory.createEmptyBorder());
        presentationDeviceText.setBorder(BorderFactory.createEmptyBorder());
        presentationPixelFormatText.setBorder(BorderFactory.createEmptyBorder());
        presentationFrameSizeText.setBorder(BorderFactory.createEmptyBorder());
        presentationFPSText.setBorder(BorderFactory.createEmptyBorder());
        audioInText.setBorder(BorderFactory.createEmptyBorder());
        audioOutText.setBorder(BorderFactory.createEmptyBorder());
        cameraSettingInfoText.setBorder(BorderFactory.createEmptyBorder());
        presentationSettingInfoText.setBorder(BorderFactory.createEmptyBorder());
        if(languageBundle.containsKey("AV_TOOL_TIP_DISPLAY_DEVICE")){
            displayDeviceText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_DISPLAY_DEVICE"));
            displayBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_DISPLAY_DEVICE"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_DISPLAY_SETTING")){
            displaySettingText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_DISPLAY_SETTING"));
            displaySettingBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_DISPLAY_SETTING"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_CAMERA_DEVICE")){
            cameraDeviceText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_DEVICE"));
            mainCameraBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_DEVICE"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_CAMERA_PIXEL_FORMAT")){
            cameraPixelFormatText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_PIXEL_FORMAT"));
            mainCameraPixelFormatBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_PIXEL_FORMAT"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_CAMERA_SIZE")){
            cameraFrameSizeText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_SIZE"));
            mainCameraFrameSizeBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_SIZE"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_CAMERA_FPS")){
            cameraFPSText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_FPS"));
            mainCameraFPSBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_FPS"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_PRESENTATION_DEVICE")){
            presentationDeviceText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_DEVICE"));
            presentationBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_DEVICE"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_PRESENTATION_PIXEL_FORMAT")){
            presentationPixelFormatText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_PIXEL_FORMAT"));
            presentationPixelFormatBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_PIXEL_FORMAT"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_PRESENTATION_SIZE")){
            presentationFrameSizeText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_SIZE"));
            presentationFrameSizeBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_SIZE"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_PRESENTATION_FPS")){
            presentationFPSText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_FPS"));
            presentationFPSBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_FPS"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_AUDIO_IN")){
            audioInText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_AUDIO_IN"));
            audioInComboBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_AUDIO_IN"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_AUDIO_OUT")){
            audioOutText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_AUDIO_OUT"));
            audioOutComboBox.setToolTipText(languageBundle.getString("AV_TOOL_TIP_AUDIO_OUT"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_CAMERA_DEVICE_SETTING")){
            cameraSettingText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_DEVICE_SETTING"));
            cameraSettingInfoText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_CAMERA_DEVICE_SETTING"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_PRESENTATION_DEVICE_SETTING")){
            presentationSettingText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_DEVICE_SETTING"));
            presentationSettingInfoText.setToolTipText(languageBundle.getString("AV_TOOL_TIP_PRESENTATION_DEVICE_SETTING"));
        }
        //buttons
        JButton testCameraButton = new JButton(languageBundle.getString("TEST_CAMERA"));
        JButton testPresentationButton = new JButton(languageBundle.getString("TEST_PRESENTATION"));
        testCameraButton.setFont(fontButtons);
        testPresentationButton.setFont(fontButtons);
        if(languageBundle.containsKey("AV_TOOL_TIP_TEST_CAMERA")){
            testCameraButton.setToolTipText(languageBundle.getString("AV_TOOL_TIP_TEST_CAMERA"));
        }
        if(languageBundle.containsKey("AV_TOOL_TIP_TEST_PRESENTATION")){
            testPresentationButton.setToolTipText(languageBundle.getString("AV_TOOL_TIP_TEST_PRESENTATION"));
        }
        testCameraButton.addActionListener((ActionEvent event) -> {
            try {
                String reciveSetting =  getDisplaySetting();
                String outputSetting = getVideoSettings(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, videoDevices);
                startUltragrid(uvPathString, reciveSetting, outputSetting);
            } catch (IOException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        testPresentationButton.addActionListener((ActionEvent event) -> {
            try {
                String reciveSetting = getDisplaySetting();
                String outputSetting = getVideoSettings(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, videoDevices);
                startUltragrid(uvPathString, reciveSetting, outputSetting);
            } catch (IOException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //check box
        JCheckBox presentationCheckBox = new JCheckBox(languageBundle.getString("PRESENTATION"));
        presentationCheckBox.addItemListener((ItemEvent e) -> {
            boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
            presetationPanel.setVisible(isSelected);
            testPresentationButton.setVisible(isSelected);
            presentationUsed = isSelected;
            this.pack();
        });
        if(!role.equals(languageBundle.getString("TEACHER"))){
            presentationCheckBox.setEnabled(false);
            presetationPanel.setVisible(false);
            testPresentationButton.setVisible(false);
            presentationUsed = false;
            this.pack();
        }
        
        //putting boxes to panel
        mainCameraPanel.setLayout(new GridBagLayout());
        GridBagConstraints mainCameraPanelConstrains = new GridBagConstraints();
        mainCameraPanelConstrains.insets = new Insets(5,5,5,5);
        mainCameraPanelConstrains.weightx = 0.5;
        mainCameraPanelConstrains.gridheight = 1;
        mainCameraPanelConstrains.gridwidth = 1;
        mainCameraPanelConstrains.gridx = 0;
        mainCameraPanelConstrains.gridy = 0;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_END;
        mainCameraPanel.add(cameraDeviceText, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 1;
        mainCameraPanelConstrains.gridy = 0;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_START;
        mainCameraPanel.add(mainCameraBox, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 0;
        mainCameraPanelConstrains.gridy = 1;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_END;
        mainCameraPanel.add(cameraPixelFormatText, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 1;
        mainCameraPanelConstrains.gridy = 1;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_START;
        mainCameraPanel.add(mainCameraPixelFormatBox, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 0;
        mainCameraPanelConstrains.gridy = 2;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_END;
        mainCameraPanel.add(cameraFrameSizeText, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 1;
        mainCameraPanelConstrains.gridy = 2;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_START;
        mainCameraPanel.add(mainCameraFrameSizeBox, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 0;
        mainCameraPanelConstrains.gridy = 3;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_END;
        mainCameraPanel.add(cameraFPSText, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 1;
        mainCameraPanelConstrains.gridy = 3;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_START;
        mainCameraPanel.add(mainCameraFPSBox, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 0;
        mainCameraPanelConstrains.gridy = 4;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_END;
        mainCameraPanel.add(cameraSettingInfoText, mainCameraPanelConstrains);
        mainCameraPanelConstrains.gridx = 1;
        mainCameraPanelConstrains.gridy = 4;
        mainCameraPanelConstrains.anchor = GridBagConstraints.LINE_START;
        mainCameraPanel.add(cameraSettingText, mainCameraPanelConstrains);
        
        presetationPanel.setLayout(new GridBagLayout());        
        GridBagConstraints presetationPanelConstrains = new GridBagConstraints();        
        presetationPanelConstrains.insets = new Insets(5,5,5,5);
        presetationPanelConstrains.weightx = 0.5;
        presetationPanelConstrains.gridx = 0;
        presetationPanelConstrains.gridy = 0;
        presetationPanelConstrains.gridheight = 1;
        presetationPanelConstrains.gridwidth = 1;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_END;
        presetationPanel.add(presentationDeviceText, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 1;
        presetationPanelConstrains.gridy = 0;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_START;
        presetationPanel.add(presentationBox, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 0;
        presetationPanelConstrains.gridy = 1;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_END;
        presetationPanel.add(presentationPixelFormatText, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 1;
        presetationPanelConstrains.gridy = 1;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_START;
        presetationPanel.add(presentationPixelFormatBox, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 0;
        presetationPanelConstrains.gridy = 2;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_END;
        presetationPanel.add(presentationFrameSizeText, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 1;
        presetationPanelConstrains.gridy = 2;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_START;
        presetationPanel.add(presentationFrameSizeBox, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 0;
        presetationPanelConstrains.gridy = 3;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_END;
        presetationPanel.add(presentationFPSText, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 1;
        presetationPanelConstrains.gridy = 3;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_START;
        presetationPanel.add(presentationFPSBox, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 0;
        presetationPanelConstrains.gridy = 4;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_END;
        presetationPanel.add(presentationSettingInfoText, presetationPanelConstrains);
        presetationPanelConstrains.gridx = 1;
        presetationPanelConstrains.gridy = 4;
        presetationPanelConstrains.anchor = GridBagConstraints.LINE_START;
        presetationPanel.add(presentationSettingText, presetationPanelConstrains);
        
        displayPanel.setLayout(new GridBagLayout());        
        GridBagConstraints displayPanelConstrains = new GridBagConstraints();
        displayPanelConstrains.insets = new Insets(5,5,5,5);
        displayPanelConstrains.weightx = 0.5;
        displayPanelConstrains.gridx = 0;
        displayPanelConstrains.gridy = 0;
        displayPanelConstrains.gridheight = 1;
        displayPanelConstrains.gridwidth = 1;
        displayPanelConstrains.anchor = GridBagConstraints.LINE_END;
        displayPanel.add(displayDeviceText, displayPanelConstrains);
        displayPanelConstrains.gridx = 1;
        displayPanelConstrains.gridy = 0;
        displayPanelConstrains.anchor = GridBagConstraints.LINE_START;
        displayPanel.add(displayBox, displayPanelConstrains);
        displayPanelConstrains.gridx = 0;
        displayPanelConstrains.gridy = 1;
        displayPanelConstrains.anchor = GridBagConstraints.LINE_END;
        displayPanel.add(displaySettingText, displayPanelConstrains);
        displayPanelConstrains.gridx = 1;
        displayPanelConstrains.gridy = 1;
        displayPanelConstrains.anchor = GridBagConstraints.LINE_START;
        displayPanel.add(displaySettingBox, displayPanelConstrains);
        
        audioPanel.setLayout(new GridBagLayout());
        GridBagConstraints AudioConstrains = new GridBagConstraints();
        AudioConstrains.insets = new Insets(5,5,5,5);
        AudioConstrains.anchor = GridBagConstraints.LINE_END;
        AudioConstrains.weightx = 0.5;
        AudioConstrains.gridheight = 1;
        AudioConstrains.gridwidth = 1;
        AudioConstrains.gridx = 0;
        AudioConstrains.gridy = 0;
        audioPanel.add(audioInText, AudioConstrains);
        AudioConstrains.anchor = GridBagConstraints.LINE_START;
        AudioConstrains.gridx = 1;
        AudioConstrains.gridy = 0;
        audioPanel.add(audioInComboBox, AudioConstrains);
        AudioConstrains.anchor = GridBagConstraints.LINE_END;
        AudioConstrains.gridx = 0;
        AudioConstrains.gridy = 1;
        audioPanel.add(audioOutText, AudioConstrains);
        AudioConstrains.anchor = GridBagConstraints.LINE_START;
        AudioConstrains.gridx = 1;
        AudioConstrains.gridy = 1;
        audioPanel.add(audioOutComboBox, AudioConstrains);
        
        videoAudioPanel.setLayout(new GridBagLayout());
        GridBagConstraints videoAudioConstrains = new GridBagConstraints();
        videoAudioConstrains.fill = GridBagConstraints.HORIZONTAL;
        videoAudioConstrains.insets = new Insets(5,5,5,5);
        videoAudioConstrains.weightx = 0.5;
        videoAudioConstrains.anchor = GridBagConstraints.CENTER;
        videoAudioConstrains.gridx = 0;
        videoAudioConstrains.gridy = 0;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 3;
        videoAudioPanel.add(audioPanel, videoAudioConstrains);
        videoAudioConstrains.gridx = 0;
        videoAudioConstrains.gridy = 1;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 3;
        videoAudioPanel.add(displayPanel, videoAudioConstrains);
        videoAudioConstrains.gridx = 0;
        videoAudioConstrains.gridy = 2;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 3;
        videoAudioPanel.add(mainCameraPanel, videoAudioConstrains);
        videoAudioConstrains.gridx = 0;
        videoAudioConstrains.gridy = 3;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 1;
        videoAudioPanel.add(presentationCheckBox, videoAudioConstrains);
        videoAudioConstrains.gridx = 2;
        videoAudioConstrains.gridy = 3;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 1;
        videoAudioConstrains.ipadx = 30;
        videoAudioPanel.add(testCameraButton, videoAudioConstrains);
        videoAudioConstrains.gridx = 0;
        videoAudioConstrains.gridy = 4;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 3;
        videoAudioConstrains.ipadx = 0;
        videoAudioPanel.add(presetationPanel, videoAudioConstrains);
        videoAudioConstrains.gridx = 2;
        videoAudioConstrains.gridy = 5;
        videoAudioConstrains.gridheight = 1;
        videoAudioConstrains.gridwidth = 1;
        videoAudioPanel.add(testPresentationButton, videoAudioConstrains);
        
        presetationPanel.setVisible(false);
        testPresentationButton.setVisible(false);
        presentationCheckBox.setSelected(presentationUsed);
    }
    
    /**
     * set miscs panel
     */
    private void setMiscsPanel(){
        
        JPanel myIpAddressPanel = new JPanel();
        myIpAddressPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("MY_IP")));
        JPanel serverIpSettingPanel = new JPanel();
        serverIpSettingPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("SERVER_IP_SETTING")));
        JPanel addressPanel = new JPanel();
        addressPanel.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("PATHS")));
        JPanel statusPanel = new JPanel();
        addressPanel.setBorder(BorderFactory.createEmptyBorder());
        
        myIpSetTextField.setEditable(true);
        myIpSetTextField.setColumns(10);
        myIpSetTextField.setBorder(BorderFactory.createEmptyBorder());
        JTextField myIpSetTextFieldInfoText = new JTextField(languageBundle.getString("MY_IP"));
        myIpSetTextFieldInfoText.setEditable(false);
        myIpSetTextFieldInfoText.setBorder(BorderFactory.createEmptyBorder());
        
        JTextField serverIpAddresChangeTextField = new JTextField();
        JTextField serverIpNameChange = new JTextField();
        JTextField serverIpPortChange = new JTextField();
        serverIpAddresChangeTextField.setColumns(10);
        serverIpNameChange.setColumns(13);
        serverIpPortChange.setColumns(13);
        serverIpAddresChangeTextField.setBorder(BorderFactory.createEmptyBorder());
        serverIpNameChange.setBorder(BorderFactory.createEmptyBorder());
        serverIpPortChange.setBorder(BorderFactory.createEmptyBorder());
        
        //text to explane fields
        JTextField serverIpAddresChangeTextFieldInfoText = new JTextField(languageBundle.getString("IP_ADDRESS_SERVER"));
        JTextField serverIpNameChangeInfoText = new JTextField(languageBundle.getString("SERVER_NAME"));
        JTextField serverIpPortChangeInfoText = new JTextField(languageBundle.getString("SERVER_PORT"));
        JTextField uvPathInfoText = new JTextField(languageBundle.getString("UV_PATH"));
        JTextField layoutPathInfoText = new JTextField(languageBundle.getString("LAYOUT_PATH"));
        JTextField uvStatusTextFieldInfoText = new JTextField(languageBundle.getString("ULTRAGRID"));
        JTextField displayStatusTextFieldInfoTExt = new JTextField(languageBundle.getString("DISPLAY_SOFTWARE"));
        JTextField audioStatusTextFieldInfoTExt = new JTextField(languageBundle.getString("AUDIO_SOFTWARE"));
        serverIpAddresChangeTextFieldInfoText.setEditable(false);
        serverIpNameChangeInfoText.setEditable(false);
        serverIpPortChangeInfoText.setEditable(false);
        uvPathInfoText.setEditable(false);
        layoutPathInfoText.setEditable(false);
        uvStatusTextFieldInfoText.setEditable(false);
        displayStatusTextFieldInfoTExt.setEditable(false);
        audioStatusTextFieldInfoTExt.setEditable(false);
        serverIpAddresChangeTextFieldInfoText.setHorizontalAlignment(JTextField.RIGHT);
        serverIpNameChangeInfoText.setHorizontalAlignment(JTextField.RIGHT);
        serverIpPortChangeInfoText.setHorizontalAlignment(JTextField.RIGHT);
        uvPathInfoText.setHorizontalAlignment(JTextField.RIGHT);
        layoutPathInfoText.setHorizontalAlignment(JTextField.RIGHT);
        uvStatusTextFieldInfoText.setHorizontalAlignment(JTextField.RIGHT);
        displayStatusTextFieldInfoTExt.setHorizontalAlignment(JTextField.RIGHT);
        audioStatusTextFieldInfoTExt.setHorizontalAlignment(JTextField.RIGHT);
       
        serverIpAddresChangeTextFieldInfoText.setBorder(BorderFactory.createEmptyBorder());
        serverIpNameChangeInfoText.setBorder(BorderFactory.createEmptyBorder());
        serverIpPortChangeInfoText.setBorder(BorderFactory.createEmptyBorder());
        uvPathInfoText.setBorder(BorderFactory.createEmptyBorder());
        layoutPathInfoText.setBorder(BorderFactory.createEmptyBorder());
        uvStatusTextFieldInfoText.setBorder(BorderFactory.createEmptyBorder());
        displayStatusTextFieldInfoTExt.setBorder(BorderFactory.createEmptyBorder());
        audioStatusTextFieldInfoTExt.setBorder(BorderFactory.createEmptyBorder());
        
        JComboBox serverIpSelect = new JComboBox();
        serverIpSelect.setEditable(false);
        serverIpSelect.addActionListener((ActionEvent event) -> {
            if(serverIpSelect.getItemCount() > 0){
                int selectedIndex = serverIpSelect.getSelectedIndex();
                serverIpAddresChangeTextField.setText(ipAddresses.get(selectedIndex).address);
                serverIpNameChange.setText(ipAddresses.get(selectedIndex).name);
                serverIpPortChange.setText(ipAddresses.get(selectedIndex).port);
            }
        });
        
        JButton reloadUltragridButton = new JButton(languageBundle.getString("RESCAN_ULTRAGRID"));
        JButton addNewServerButton = new JButton(languageBundle.getString("ADD"));
        JButton saveChangesInServerButton = new JButton(languageBundle.getString("USE"));
        JButton deleteCurrentServerButton = new JButton(languageBundle.getString("DELETE"));
        reloadUltragridButton.setFont(fontButtons);
        addNewServerButton.setFont(fontButtons);
        saveChangesInServerButton.setFont(fontButtons);
        deleteCurrentServerButton.setFont(fontButtons);
        reloadUltragridButton.addActionListener((ActionEvent event) -> {
            ultragridOK(uvPathString);
            try {
                videoDevices = loadVideoDevicesAndSettings(uvPathString);
                addTestcrdDevice(videoDevices);
                audioConsumers = read_audio_devices_in_or_out(uvPathString, false);
                audioProducers = read_audio_devices_in_or_out(uvPathString, true);
            } catch (IOException ex) {
                videoDevices = new ArrayList<>();
                audioConsumers =  new ArrayList<>();
                audioProducers = new ArrayList<>();
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            setAudioJComboBox(audioInComboBox, audioInSettingConfiguration, audioProducers);
            setAudioJComboBox(audioOutComboBox, audioOutSettingConfiguration, audioConsumers);
            setAllJComboBoxesVideosetting(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, cameraSettingText, videoDevices);
            setAllJComboBoxesVideosetting(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, presentationSettingText, videoDevices);
        });
        addNewServerButton.addActionListener((ActionEvent event) -> {
            IPServerSaved newServer = new IPServerSaved();
            newServer.address = "0.0.0.0";
            newServer.name = "new server";
            newServer.port = "80";
            ipAddresses.add(newServer);
            setServerIpsComboBox(ipAddresses, serverIpSelect);
            serverIpSelect.setSelectedIndex(ipAddresses.size() - 1);
        });
        saveChangesInServerButton.addActionListener((ActionEvent event) -> {
            if(serverIpSelect.getItemCount() > 0){
                int selectedIndex = serverIpSelect.getSelectedIndex();
                ipAddresses.get(selectedIndex).address = serverIpAddresChangeTextField.getText();
                ipAddresses.get(selectedIndex).name = serverIpNameChange.getText();
                ipAddresses.get(selectedIndex).port = serverIpPortChange.getText();
                setServerIpsComboBox(ipAddresses, serverIpSelect);
                serverIpSelect.setSelectedIndex(selectedIndex);
            }
        });
        deleteCurrentServerButton.addActionListener((ActionEvent event) -> {
            if(serverIpSelect.getItemCount() > 0){
                int selectedIndex = serverIpSelect.getSelectedIndex();
                ipAddresses.remove(selectedIndex);
                serverIpAddresChangeTextField.setText("");
                serverIpNameChange.setText("");
                serverIpPortChange.setText("");
                setServerIpsComboBox(ipAddresses, serverIpSelect);
            }
        });
        
        //path fields
        //creating and setting
        if(configuration.has("ultragrid path")){
            try {
                uvPathString = configuration.getString("ultragrid path");
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
        if(configuration.has("layout path")){
            try {
                layoutPathString = configuration.getString("layout path");
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JTextField uvSystemPath = new JTextField(uvPathString);
        JTextField layoutSystemPath = new JTextField(layoutPathString);
        uvSystemPath.setColumns(20);
        layoutSystemPath.setColumns(20);
        uvSystemPath.setBorder(BorderFactory.createEmptyBorder());
        layoutSystemPath.setBorder(BorderFactory.createEmptyBorder());
        uvSystemPath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                propagateText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                propagateText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                propagateText();
            }
            
            public void propagateText(){
                uvPathString = uvSystemPath.getText();
            }
        });
        
        layoutSystemPath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                propagateText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                propagateText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                propagateText();
            }
            
            public void propagateText(){
                layoutPathString = layoutSystemPath.getText();
            }
        });
        JFileChooser uvFileChooser = new JFileChooser();
        JFileChooser layoutFileChooser = new JFileChooser();
        layoutFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JButton setUvSystemPathButton = new JButton("...");
        JButton setLayoutSystemPathButton = new JButton("...");
        setUvSystemPathButton.setFont(fontButtons);
        setLayoutSystemPathButton.setFont(fontButtons);
        //setting action path choosing
        setUvSystemPathButton.addActionListener((ActionEvent event) -> {
            int returnVal = uvFileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                uvSystemPath.setText(uvFileChooser.getSelectedFile().getPath());
            }
        });
        
        setLayoutSystemPathButton.addActionListener((ActionEvent event) -> {
            int returnVal = layoutFileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                layoutSystemPath.setText(layoutFileChooser.getSelectedFile().getPath());
            }
        });
        
        
        if(languageBundle.containsKey("MISC_TOOL_TIP_MY_IP")){
            myIpSetTextField.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_MY_IP"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_IP_ADDRESS")){
            serverIpAddresChangeTextFieldInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_IP_ADDRESS"));
            serverIpAddresChangeTextField.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_IP_ADDRESS"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_NAME")){
            serverIpNameChangeInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_NAME"));
            serverIpNameChange.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_NAME"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_PORT")){
            serverIpPortChangeInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_PORT"));
            serverIpPortChange.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_PORT"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_ADD_NEW")){
            addNewServerButton.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_ADD_NEW"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_USE")){
            saveChangesInServerButton.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_USE"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_SERVER_DELETE")){
            deleteCurrentServerButton.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_SERVER_DELETE"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_UV_PATH")){
            uvPathInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_UV_PATH"));
            uvSystemPath.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_UV_PATH"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_LAYOUT_PATH")){
            layoutPathInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_LAYOUT_PATH"));
            layoutSystemPath.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_LAYOUT_PATH"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_RELOAD_UV")){
            reloadUltragridButton.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_RELOAD_UV"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_UV_STATUS_TEXT")){
            uvStatusTextField.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_UV_STATUS_TEXT"));
            uvStatusTextFieldInfoText.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_UV_STATUS_TEXT"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_DISPLAY_STATUS_TEXT")){
            displayStatusTextField.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_DISPLAY_STATUS_TEXT"));
            displayStatusTextFieldInfoTExt.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_DISPLAY_STATUS_TEXT"));
        }
        if(languageBundle.containsKey("MISC_TOOL_TIP_AUDIO_STATUS_TEXT")){
            audioStatusTextField.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_AUDIO_STATUS_TEXT"));
            audioStatusTextFieldInfoTExt.setToolTipText(languageBundle.getString("MISC_TOOL_TIP_AUDIO_STATUS_TEXT"));
        }
        
        myIpAddressPanel.setLayout(new GridBagLayout());
        GridBagConstraints myIpAddressConstraints = new GridBagConstraints();
        myIpAddressConstraints.insets = new Insets(5,5,5,5);
        myIpAddressConstraints.weightx = 0.5;
        myIpAddressConstraints.gridheight = 1;
        myIpAddressConstraints.gridwidth = 1;
        myIpAddressConstraints.gridx = 0;
        myIpAddressConstraints.gridy = 0;
        myIpAddressPanel.add(myIpSetTextField, myIpAddressConstraints);
        
        statusPanel.setLayout(new GridBagLayout());
        GridBagConstraints statusPanelConstraints = new GridBagConstraints();
        statusPanelConstraints.insets = new Insets(5, 5, 5, 5);
        statusPanelConstraints.weightx = 0.5;
        statusPanelConstraints.anchor = GridBagConstraints.LINE_END;
        statusPanelConstraints.gridheight = 1;
        statusPanelConstraints.gridwidth = 1;
        statusPanelConstraints.gridx = 0;
        statusPanelConstraints.gridy = 0;
        statusPanel.add(uvStatusTextFieldInfoText, statusPanelConstraints);
        statusPanelConstraints.anchor = GridBagConstraints.LINE_START;
        statusPanelConstraints.gridx = 1;
        statusPanelConstraints.gridy = 0;
        statusPanel.add(uvStatusTextField, statusPanelConstraints);
        statusPanelConstraints.anchor = GridBagConstraints.LINE_END;
        statusPanelConstraints.gridx = 0;
        statusPanelConstraints.gridy = 1;
        statusPanel.add(displayStatusTextFieldInfoTExt, statusPanelConstraints);
        statusPanelConstraints.anchor = GridBagConstraints.LINE_START;
        statusPanelConstraints.gridx = 1;
        statusPanelConstraints.gridy = 1;
        statusPanel.add(displayStatusTextField, statusPanelConstraints);
        statusPanelConstraints.anchor = GridBagConstraints.LINE_END;
        statusPanelConstraints.gridx = 0;
        statusPanelConstraints.gridy = 2;
        statusPanel.add(audioStatusTextFieldInfoTExt, statusPanelConstraints);
        statusPanelConstraints.anchor = GridBagConstraints.LINE_START;
        statusPanelConstraints.gridx = 1;
        statusPanelConstraints.gridy = 2;
        statusPanel.add(audioStatusTextField, statusPanelConstraints);
        
        serverIpSettingPanel.setLayout(new GridBagLayout());
        GridBagConstraints serverIpSettingPanelConstraints = new GridBagConstraints();
        serverIpSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        serverIpSettingPanelConstraints.insets = new Insets(5,5,5,5);
        serverIpSettingPanelConstraints.weightx = 0.5;
        serverIpSettingPanelConstraints.gridheight = 1;
        serverIpSettingPanelConstraints.gridwidth = 1;
        serverIpSettingPanelConstraints.gridx = 1;
        serverIpSettingPanelConstraints.gridy = 0;
        serverIpSettingPanel.add(serverIpAddresChangeTextFieldInfoText, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 2;
        serverIpSettingPanelConstraints.gridy = 0;
        serverIpSettingPanel.add(serverIpAddresChangeTextField, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 3;
        serverIpSettingPanelConstraints.gridy = 0;
        serverIpSettingPanel.add(addNewServerButton, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 0;
        serverIpSettingPanelConstraints.gridy = 1;
        serverIpSettingPanel.add(serverIpSelect, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 1;
        serverIpSettingPanelConstraints.gridy = 1;
        serverIpSettingPanel.add(serverIpNameChangeInfoText, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 2;
        serverIpSettingPanelConstraints.gridy = 1;
        serverIpSettingPanel.add(serverIpNameChange, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 3;
        serverIpSettingPanelConstraints.gridy = 1;
        serverIpSettingPanel.add(saveChangesInServerButton, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 1;
        serverIpSettingPanelConstraints.gridy = 2;
        serverIpSettingPanel.add(serverIpPortChangeInfoText, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 2;
        serverIpSettingPanelConstraints.gridy = 2;
        serverIpSettingPanel.add(serverIpPortChange, serverIpSettingPanelConstraints);
        serverIpSettingPanelConstraints.gridx = 3;
        serverIpSettingPanelConstraints.gridy = 2;
        serverIpSettingPanel.add(deleteCurrentServerButton, serverIpSettingPanelConstraints);
        
        addressPanel.setLayout(new GridBagLayout());
        GridBagConstraints addressPanelConstraints = new GridBagConstraints();
        addressPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        addressPanelConstraints.insets = new Insets(5,5,5,5);
        addressPanelConstraints.weightx = 0.5;
        addressPanelConstraints.gridheight = 1;
        addressPanelConstraints.gridwidth = 1;
        addressPanelConstraints.gridx = 0;
        addressPanelConstraints.gridy = 0;
        addressPanel.add(uvPathInfoText, addressPanelConstraints);
        addressPanelConstraints.gridx = 1;
        addressPanelConstraints.gridy = 0;
        addressPanel.add(uvSystemPath, addressPanelConstraints);
        addressPanelConstraints.gridx = 2;
        addressPanelConstraints.gridy = 0;
        addressPanel.add(setUvSystemPathButton, addressPanelConstraints);        
        addressPanelConstraints.gridx = 0;
        addressPanelConstraints.gridy = 2;
        addressPanel.add(layoutPathInfoText, addressPanelConstraints);
        addressPanelConstraints.gridx = 1;
        addressPanelConstraints.gridy = 2;
        addressPanel.add(layoutSystemPath, addressPanelConstraints);
        addressPanelConstraints.gridx = 2;
        addressPanelConstraints.gridy = 2;
        addressPanel.add(setLayoutSystemPathButton, addressPanelConstraints);
        
        miscsPanel.setLayout(new GridBagLayout());
        GridBagConstraints miscsPanelConstraints = new GridBagConstraints();
        miscsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        miscsPanelConstraints.gridheight = 1;
        miscsPanelConstraints.gridwidth = 3;
        miscsPanelConstraints.gridx = 0;
        miscsPanelConstraints.gridy = 0;
        miscsPanel.add(myIpAddressPanel, miscsPanelConstraints);
        miscsPanelConstraints.gridx = 0;
        miscsPanelConstraints.gridy = 1;
        miscsPanel.add(serverIpSettingPanel, miscsPanelConstraints);
        miscsPanelConstraints.gridx = 0;
        miscsPanelConstraints.gridy = 2;
        miscsPanel.add(addressPanel, miscsPanelConstraints);
        miscsPanelConstraints.fill = GridBagConstraints.NONE;
        miscsPanelConstraints.anchor = GridBagConstraints.CENTER;
        miscsPanelConstraints.weightx = 0.5;
        miscsPanelConstraints.gridx = 0;
        miscsPanelConstraints.gridy = 3;
        miscsPanelConstraints.gridheight = 1;
        miscsPanelConstraints.gridwidth = 2;
        miscsPanel.add(statusPanel, miscsPanelConstraints);
        miscsPanelConstraints.anchor = GridBagConstraints.LINE_END;
        miscsPanelConstraints.gridx = 2;
        miscsPanelConstraints.gridy = 3;
        miscsPanelConstraints.gridheight = 1;
        miscsPanelConstraints.gridwidth = 1;
        miscsPanel.add(reloadUltragridButton, miscsPanelConstraints);
        
        ipAddresses = loadIpAddreses();
        setServerIpsComboBox(ipAddresses, serverIpSelect);
        String myIpLoaded = "";
        try {
            myIpLoaded = configuration.getString("this ip");
        } catch (JSONException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        myIpSetTextField.setText(myIpLoaded);
    }
    
    /**
     * get list of video devices and setting
     * @param uvPath
     * @return
     * @throws IOException 
     */
    private List<VideoDevice> loadVideoDevicesAndSettings(String uvPath) throws IOException{
        if(!correctUv){
            return new ArrayList<>();
        }
        List<VideoDevice> ret = new ArrayList<>();
        if(correctUv){
            String osName = System.getProperty("os.name");
            String uvVideoSetting;
            if(osName.toUpperCase().contains("WINDOWS")){
                uvVideoSetting = "dshow";
                ret = getVideoDevicesAndSettingsWindows(uvPath, uvVideoSetting);
            }else if(osName.toUpperCase().contains("LINUX")){
                uvVideoSetting = "v4l2";
                ret = getVideoDevicesAndSettingsLinux(uvPath, uvVideoSetting);
            }else if(osName.toUpperCase().contains("MAC")){
                uvVideoSetting = "avfoundation";
                ret = getVideoDevicesAndSettingsMac(uvPath, uvVideoSetting);
            }else{      //probably should log incorrect os system
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, "no supported os");
                return null;
            }
        }
        return ret;
    }
    
    /**
     * get list of video devices and setting from Linux OS
     * @param uvAddress
     * @param uvVideoSetting
     * @return
     * @throws IOException 
     */
    List<VideoDevice> getVideoDevicesAndSettingsLinux(String uvAddress, String uvVideoSetting) throws IOException{
        Process uvProcess = new ProcessBuilder(uvAddress, "-t", uvVideoSetting + ":help").start();
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        
        boolean loadCamera = false;
        List<VideoDevice> videoInputs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(loadCamera){ //lines inside the device setting 
                if(line.length() == 0){     //end of divice setting
                    loadCamera = false;
                }else{
                    VideoDevice lastVideoDevice = videoInputs.get(videoInputs.size()-1);   //get last video device
                    if(line.contains("Pixel format")){  //line with new pixel format
                        Pattern pixelFormatPattern = Pattern.compile("Pixel format ((.*?)[(].*[)])");
                        Matcher pixelFormatMatcher = pixelFormatPattern.matcher(line);
                        VideoPixelFormat pf = new VideoPixelFormat();
                        if(pixelFormatMatcher.find()){
                            pf.name = pixelFormatMatcher.group(1);
                            String pixelFormatString = pixelFormatMatcher.group(2);
                            pf.pixelFormat = pixelFormatString.replaceAll("\\s+","");
                        }
                        
                        pf.name = line.substring(line.indexOf("Pixel format")+12, line.indexOf('.'));
                        lastVideoDevice.vpf.add(pf);
                    }else{                              //line with frame size
                        VideoPixelFormat lastPixelFormat = lastVideoDevice.vpf.get(lastVideoDevice.vpf.size()-1);
                        VideoFrameSize fs = new VideoFrameSize();
                        Pattern resolutionPattern = Pattern.compile("[\\d]+x[\\d]+");
                        Matcher resolutionMatcher = resolutionPattern.matcher(line);
                        if(resolutionMatcher.find()){
                            fs.widthXheight = resolutionMatcher.group();
                        }
                        Pattern fpsPattern = Pattern.compile("(([\\d]+)/([\\d]+))");
                        Matcher  fpsMatcher = fpsPattern.matcher(line);
                        fs.fps = new ArrayList<>();
                        while(fpsMatcher.find()){
                            VideoFPS vfps = new VideoFPS();
                            vfps.fps = fpsMatcher.group(3);
                            vfps.setting = "v4l2:dev=" + lastVideoDevice.device + ":fmt=" + lastPixelFormat.pixelFormat + ":size=" + fs.widthXheight
                                                    + ":tpf=" + fpsMatcher.group(1);
                            fs.fps.add(vfps);
                        }
                        lastPixelFormat.vfs.add(fs);
                    }
                }
            }else{  // out of device setting
                if(line.contains("Device")){
                    loadCamera = true;
                    VideoDevice vd = new VideoDevice();
                    Pattern devicePattern = Pattern.compile("Device ((/.*?)/(.*?)) ([(].*[)])");
                    Matcher deviceMatcher = devicePattern.matcher(line);
                    if(deviceMatcher.find()){
                        vd.name = deviceMatcher.group(3) + " " + deviceMatcher.group(4);
                        vd.device = deviceMatcher.group(1);
                    }
                    videoInputs.add(vd);
                }
            }
        }
        
        return videoInputs;
    }
    
    /**
     * get list of video devices and setting from Windows OS
     * @param uvAddress
     * @param uvVideoSetting
     * @return
     * @throws IOException 
     */
    List<VideoDevice> getVideoDevicesAndSettingsWindows(String uvAddress, String uvVideoSetting) throws IOException{
        
        Process uvProcess = new ProcessBuilder(uvAddress, "-t", uvVideoSetting + ":help").start();
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        
        List<VideoDevice> videoInputs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Pattern devicePattern = Pattern.compile("^(Device (\\d+):.)(.*)");
            Matcher deviceMatcher = devicePattern.matcher(line);
            if(deviceMatcher.find()){
                VideoDevice vd = new VideoDevice();
                vd.name = deviceMatcher.group(3);               
                vd.device = deviceMatcher.group(2);
                vd.vpf = new ArrayList<>();
                videoInputs.add(vd);
            }
            
            Pattern modePattern = Pattern.compile("Mode\\s+(\\d+):\\s(.*?)\\s*(\\d*x\\d*)\\s*@([0-9\\.]*)");
            Matcher modeMatcher = modePattern.matcher(line);
            while(modeMatcher.find()){
                VideoDevice vd = videoInputs.get(videoInputs.size() - 1);
                int pixelFormatPosition = 0;
                int widthXheightPosition = 0;
                boolean found_item = false;             //maybe not most elegant
                for(int i=0;i<vd.vpf.size();i++){
                    if(vd.vpf.get(i).pixelFormat.equals(modeMatcher.group(2))){
                        pixelFormatPosition = i;
                        found_item = true;
                    }
                }
                if(!found_item){
                    VideoPixelFormat vpf = new VideoPixelFormat();
                    vpf.pixelFormat = modeMatcher.group(2);
                    vpf.name = modeMatcher.group(2);
                    vpf.vfs = new ArrayList<>();
                    vd.vpf.add(vpf);
                    pixelFormatPosition = vd.vpf.size() - 1;
                }
                found_item = false;
                for(int i=0;i<vd.vpf.get(pixelFormatPosition).vfs.size();i++){
                    if(vd.vpf.get(pixelFormatPosition).vfs.get(i).widthXheight.equals(modeMatcher.group(3))){
                        widthXheightPosition = i;
                        found_item = true;
                    }
                }
                if(!found_item){
                    VideoFrameSize vfs = new VideoFrameSize();
                    vfs.widthXheight = modeMatcher.group(3);
                    vfs.fps = new ArrayList<>();
                    vd.vpf.get(pixelFormatPosition).vfs.add(vfs);
                    widthXheightPosition = vd.vpf.get(pixelFormatPosition).vfs.size() - 1;
                }
                VideoFPS vfps = new VideoFPS();
                vfps.fps = modeMatcher.group(4) + " mode:" + modeMatcher.group(1);
                vfps.setting = "dshow" + ":" + vd.device + ":" + modeMatcher.group(1);
                vd.vpf.get(pixelFormatPosition).vfs.get(widthXheightPosition).fps.add(vfps);
            }
        }
        return videoInputs;
    }
    
    /**
     * get list of video devices and setting from Mac OS
     * @param uvAddress
     * @param uvVideoSetting
     * @return
     * @throws IOException 
     */
    List<VideoDevice> getVideoDevicesAndSettingsMac(String uvAddress, String uvVideoSetting) throws IOException{
        Process uvProcess = new ProcessBuilder(uvAddress, "-t", uvVideoSetting + ":help").start();
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        
        List<VideoDevice> videoInputs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Pattern devicePattern = Pattern.compile("^[\\*]?(\\d*):\\s*(.*)");
            Matcher deviceMatcher = devicePattern.matcher(line);
            if(deviceMatcher.find()){
                VideoDevice vd = new VideoDevice();
                vd.name = deviceMatcher.group(2);
                vd.device = deviceMatcher.group(1);
                vd.vpf = new ArrayList<>();
                videoInputs.add(vd);
            }
            
            Pattern modePattern = Pattern.compile("(\\d+): (.*?) (\\d+x\\d+) \\(max frame rate (\\d*) FPS\\)");
            Matcher modeMatcher = modePattern.matcher(line);
            while(modeMatcher.find()){
                if(videoInputs.isEmpty()){    //something strange happend, stop process
                    return videoInputs;
                }
                VideoPixelFormat vpf = new VideoPixelFormat();
                VideoFrameSize vfs = new VideoFrameSize();;
                int max_fps = 0;
                String partialSetting;
                List<VideoFPS> listVfps = new ArrayList<>();
                VideoDevice vd = videoInputs.get(videoInputs.size() - 1);
                partialSetting = uvVideoSetting + ":device=" + vd.device + ":mode=" + modeMatcher.group(1) ;
                vpf.pixelFormat = modeMatcher.group(2);
                vpf.name = modeMatcher.group(2);
                vfs.widthXheight = modeMatcher.group(3);
                max_fps = Integer.parseInt(modeMatcher.group(4));
                for(int i = max_fps; i > 0; i--) {
                    VideoFPS vfps = new VideoFPS();
                    vfps.setting = partialSetting + ":framerate=" + String.valueOf(i);
                    vfps.fps = String.valueOf(i);
                    listVfps.add(vfps);
                }
                boolean found_item = false;             //maybe not most elegant
                for(int i=0;i<vd.vpf.size();i++){
                    if(vd.vpf.get(i).pixelFormat.equals(vpf.pixelFormat)){
                        vpf = vd.vpf.get(i);
                        found_item = true;
                    }
                }
                if(!found_item){
                    vpf.vfs = new ArrayList<>();
                    vd.vpf.add(vpf);
                }
                found_item = false;
                for(int i=0;i<vpf.vfs.size();i++){
                    if(vpf.vfs.get(i).widthXheight.equals(vfs.widthXheight)){
                        vfs = vpf.vfs.get(i);
                        found_item = true;
                    }
                }
                if(!found_item){
                    vfs.fps = new ArrayList<>();
                    vpf.vfs.add(vfs);
                }
                vfs.fps = listVfps;
            }
        }
                        
        
        return videoInputs;
    }
    
    /**
     * get video display setting from comboBoxes
     * @return 
     */
    String getDisplaySetting(){
        String reciveSetting = "";
        if(displayBox.getItemCount() > 0){
            reciveSetting = displayBox.getSelectedItem().toString();
            if(displaySettingBox.getItemCount() > 0){
                if(displaySettingBox.getSelectedItem().toString().equals("nodecorate")){
                    reciveSetting += ":" + displaySettingBox.getSelectedItem().toString();
                }
            }
        }
        return reciveSetting;
    }
    
    /**
     * get video setting from selected comboBoxes
     * @param devicesBox
     * @param formatBox
     * @param frameSizeBox
     * @param fpsBox
     * @param videoDevices
     * @return 
     */
    String getVideoSettings(JComboBox devicesBox, JComboBox formatBox, JComboBox frameSizeBox, JComboBox fpsBox,
                                            List<VideoDevice> videoDevices){        
        String ret = "";
        if((devicesBox.getItemCount() > 0) && (formatBox.getItemCount() > 0) && (frameSizeBox.getItemCount() > 0) && (fpsBox.getItemCount() > 0)){
            String camera = devicesBox.getSelectedItem().toString();
            String format = formatBox.getSelectedItem().toString();
            String resolution = frameSizeBox.getSelectedItem().toString();
            String fps = fpsBox.getSelectedItem().toString();
            int cameraInt = devicesBox.getSelectedIndex();
            int formatInt = formatBox.getSelectedIndex();
            int resolutionInt = frameSizeBox.getSelectedIndex();
            int fpsInt = fpsBox.getSelectedIndex();
            List<VideoPixelFormat> videoPixelFormats = null;
            List<VideoFrameSize> videoFrameSizes = null;
            List<VideoFPS> videoFPS = null;
            if(videoDevices.size() > cameraInt){
                if(videoDevices.get(cameraInt).name.compareTo(camera) == 0){
                   videoPixelFormats = videoDevices.get(cameraInt).vpf;
                }
            }
            if(videoPixelFormats.size() > formatInt){
                if(videoPixelFormats.get(formatInt).name.compareTo(format) == 0){
                   videoFrameSizes = videoPixelFormats.get(formatInt).vfs;
                }
            }
            if(videoPixelFormats != null){
                if(videoPixelFormats.size() > formatInt){
                    if(videoPixelFormats.get(formatInt).name.compareTo(format) == 0){
                       videoFrameSizes = videoPixelFormats.get(formatInt).vfs;
                    }
                }
            }
            if(videoFrameSizes != null){
                if(videoFrameSizes.size() > resolutionInt){
                    if(videoFrameSizes.get(resolutionInt).widthXheight.compareTo(resolution) == 0){
                       videoFPS = videoFrameSizes.get(resolutionInt).fps;
                    }
                }
            }
            if(videoFPS != null){
                if(videoFPS.size() > fpsInt){
                    if(videoFPS.get(fpsInt).fps.compareTo(fps) == 0){
                       ret = videoFPS.get(fpsInt).setting;
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * set devices comboBox from video setting
     * @param devicesBox
     * @param videoDevices 
     */
    private void setJComboBoxDevices(JComboBox devicesBox, List<VideoDevice> videoDevices){
        devicesBox.removeAllItems();
        if(videoDevices != null){
            for(int i=0;i<videoDevices.size();i++){
                devicesBox.addItem(videoDevices.get(i).name);
            }
        }
    }
    
    /**
     * set format comboBox from video setting
     * @param formatBox
     * @param deviceBox
     * @param videoDevices 
     */
    private void setJComboBoxFormat(JComboBox formatBox, JComboBox deviceBox, List<VideoDevice> videoDevices){
        formatBox.removeAllItems();
        String device;
        if((deviceBox == null) || (deviceBox.getItemCount() == 0)){
            return;
        }else{
            device = deviceBox.getSelectedItem().toString();
        }
        if(videoDevices != null){
            for(int i=0;i<videoDevices.size();i++){
                if(device.compareTo(videoDevices.get(i).name) == 0){
                    for(int j=0;j<videoDevices.get(i).vpf.size();j++){
                        formatBox.addItem(videoDevices.get(i).vpf.get(j).name);
                    }
                    return;
                }
            }
        }
    }
    
    /**
     * set frame size comboBox from video setting 
     * @param frameSizeBox
     * @param deviceBox
     * @param formatBox
     * @param videoDevices 
     */
    private void setJComboBoxFrameSize(JComboBox frameSizeBox, JComboBox deviceBox, JComboBox formatBox, List<VideoDevice> videoDevices){
        frameSizeBox.removeAllItems();
        String device;
        String format;
        if((deviceBox == null) || (deviceBox.getItemCount() == 0) || (formatBox == null) || (formatBox.getItemCount() == 0)){
            return;
        }else{
            device = deviceBox.getSelectedItem().toString();
            format = formatBox.getSelectedItem().toString();
        }
        if(videoDevices != null){
            for(int i=0;i<videoDevices.size();i++){
                if(device.compareTo(videoDevices.get(i).name) == 0){
                    for(int j=0;j<videoDevices.get(i).vpf.size();j++){
                        if(format.compareTo(videoDevices.get(i).vpf.get(j).name) == 0){
                            for(int k=0;k<videoDevices.get(i).vpf.get(j).vfs.size();k++){
                                frameSizeBox.addItem(videoDevices.get(i).vpf.get(j).vfs.get(k).widthXheight);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * set fps comboBox from video devices
     * @param fpsBox
     * @param deviceBox
     * @param formatBox
     * @param widthXheightBox
     * @param videoDevices 
     */
    private void setJComboBoxFPS(JComboBox fpsBox, JComboBox deviceBox, JComboBox formatBox, JComboBox widthXheightBox, List<VideoDevice> videoDevices){
        fpsBox.removeAllItems();
        String device;
        String format;
        String widthXheight;
        if((deviceBox == null) || (deviceBox.getItemCount() == 0) || (formatBox == null) || (formatBox.getItemCount() == 0) ||
               (widthXheightBox == null) || (widthXheightBox.getItemCount() == 0)){
            return;
        }else{
            device = deviceBox.getSelectedItem().toString();
            format = formatBox.getSelectedItem().toString();
            widthXheight = widthXheightBox.getSelectedItem().toString();
        }
        if(videoDevices != null){
            for(int i=0;i<videoDevices.size();i++){
                if(device.compareTo(videoDevices.get(i).name) == 0){
                    for(int j=0;j<videoDevices.get(i).vpf.size();j++){
                        if(format.compareTo(videoDevices.get(i).vpf.get(j).name) == 0){
                            for(int k=0;k<videoDevices.get(i).vpf.get(j).vfs.size();k++){
                                if(widthXheight.compareTo(videoDevices.get(i).vpf.get(j).vfs.get(k).widthXheight) == 0){
                                    for(int l=0;l<videoDevices.get(i).vpf.get(j).vfs.get(k).fps.size();l++){
                                        fpsBox.addItem(videoDevices.get(i).vpf.get(j).vfs.get(k).fps.get(l).fps);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * set all video comboBoxes from videoDevices
     * @param deviceBox
     * @param formatBox
     * @param widthXheightBox
     * @param fpsBox
     * @param settingVerification
     * @param videoDevices 
     */
    void setAllJComboBoxesVideosetting(JComboBox deviceBox, JComboBox formatBox, JComboBox widthXheightBox, JComboBox fpsBox, JTextField settingVerification, List<VideoDevice> videoDevices){
        setJComboBoxDevices(deviceBox, videoDevices);
        setJComboBoxFormat(formatBox, deviceBox, videoDevices);
        setJComboBoxFrameSize(widthXheightBox, deviceBox, formatBox, videoDevices);
        setJComboBoxFPS(fpsBox, deviceBox, formatBox, widthXheightBox, videoDevices);
        actionSetFPSBox(deviceBox, formatBox, widthXheightBox, fpsBox, settingVerification, videoDevices);
    }
    
    /**
     * set audio combo box
     */
    void setAudioJComboBoxAudioBoxes(JComboBox audioBox, String audioSetting, List<AudioDevice> audioDevices){
        for(int i=0;i<audioDevices.size();i++){
            if(audioDevices.get(i).setting.equals(audioSetting)){
                audioBox.setSelectedIndex(i);
            }
        }
    }
    
    /**
     * action to do when device comboBox is changed
     * @param devicesBox
     * @param formatBox
     * @param frameSizeBox
     * @param fpsBox
     * @param settingVerification
     * @param videoDevices 
     */
    private void actionSetCameraDeviceBox(JComboBox devicesBox, JComboBox formatBox, JComboBox frameSizeBox, JComboBox fpsBox, JTextField settingVerification,
                                            List<VideoDevice> videoDevices){
        
        setJComboBoxFormat(formatBox, devicesBox, videoDevices);
        setJComboBoxFrameSize(frameSizeBox, devicesBox, formatBox, videoDevices);
        setJComboBoxFPS(fpsBox, devicesBox, formatBox, frameSizeBox, videoDevices);
        actionSetFPSBox(devicesBox, formatBox, frameSizeBox, fpsBox, settingVerification, videoDevices);
    }
    
    /**
     * action to do when pixel format is changed
     * @param devicesBox
     * @param formatBox
     * @param frameSizeBox
     * @param fpsBox
     * @param settingVerification
     * @param videoDevices 
     */
    private void actionSetCameraPixelFormatBox(JComboBox devicesBox, JComboBox formatBox, JComboBox frameSizeBox, JComboBox fpsBox, JTextField settingVerification,
                                            List<VideoDevice> videoDevices){

        setJComboBoxFrameSize(frameSizeBox, devicesBox, formatBox, videoDevices);
        setJComboBoxFPS(fpsBox, devicesBox, formatBox, frameSizeBox, videoDevices);
        actionSetFPSBox(devicesBox, formatBox, frameSizeBox, fpsBox, settingVerification, videoDevices);
    }
    
    /**
     * action to do when frame size comboBox is changed
     * @param devicesBox
     * @param formatBox
     * @param frameSizeBox
     * @param fpsBox
     * @param settingVerification
     * @param videoDevices 
     */
    private void actionSetCameraFrameSizeBox(JComboBox devicesBox, JComboBox formatBox, JComboBox frameSizeBox, JComboBox fpsBox, JTextField settingVerification,
                                            List<VideoDevice> videoDevices){

        setJComboBoxFPS(fpsBox, devicesBox, formatBox, frameSizeBox, videoDevices);
        actionSetFPSBox(devicesBox, formatBox, frameSizeBox, fpsBox, settingVerification, videoDevices);
    }
    
    /**
     * action to do when fps comboBox is changed
     * @param deviceBox
     * @param formatBox
     * @param widthXheightBox
     * @param fpsBox
     * @param settingVerification
     * @param videoDevices 
     */
    private void actionSetFPSBox(JComboBox deviceBox, JComboBox formatBox, JComboBox widthXheightBox, JComboBox fpsBox, JTextField settingVerification,
                                            List<VideoDevice> videoDevices){
        String device;
        String format;
        String widthXheight;
        String fps;
        if((deviceBox == null) || (deviceBox.getItemCount() == 0) || (formatBox == null) || (formatBox.getItemCount() == 0) ||
               (widthXheightBox == null) || (widthXheightBox.getItemCount() == 0) || (fpsBox == null) || (fpsBox.getItemCount() == 0)) {
            return;
        }else{
            device = deviceBox.getSelectedItem().toString();
            format = formatBox.getSelectedItem().toString();
            widthXheight = widthXheightBox.getSelectedItem().toString();
            fps = fpsBox.getSelectedItem().toString();
        }
        if(videoDevices != null){
            for(int i=0;i<videoDevices.size();i++){
                if(device.compareTo(videoDevices.get(i).name) == 0){
                    for(int j=0;j<videoDevices.get(i).vpf.size();j++){
                        if(format.compareTo(videoDevices.get(i).vpf.get(j).name) == 0){
                            for(int k=0;k<videoDevices.get(i).vpf.get(j).vfs.size();k++){
                                if(widthXheight.compareTo(videoDevices.get(i).vpf.get(j).vfs.get(k).widthXheight) == 0){
                                    for(int l=0;l<videoDevices.get(i).vpf.get(j).vfs.get(k).fps.size();l++){
                                        if(videoDevices.get(i).vpf.get(j).vfs.get(k).fps.get(l).fps.equals(fps)){
                                            settingVerification.setText(videoDevices.get(i).vpf.get(j).vfs.get(k).fps.get(l).setting);
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * read possible audio devices
     * @param uvAddress
     * @param audio_in true if read audio in, false if read audio out
     * @return
     * @throws IOException 
     */
    private List<AudioDevice> read_audio_devices_in_or_out(String uvAddress, boolean audio_in) throws IOException{
        if(!correctUv){
            return new ArrayList<>();
        }

        List<AudioDevice> audioDevices = new ArrayList<>();

        //if port audio than add devices
        List<AudioDevice> audioDevicesPortaudio = read_audio_devices_in_or_out_portaudio(uvAddress, audio_in);
        //if coreaudio than add devices
        List<AudioDevice> audioDevicesCoreaudio = read_audio_devices_in_or_out_coreaudio(uvAddress, audio_in);
        //if somthing linux than add devices
        List<AudioDevice> audioDevicesALSA = read_audio_devices_in_or_out_alsa(uvAddress, audio_in);
        
        audioDevices.addAll(audioDevicesPortaudio);
        audioDevices.addAll(audioDevicesCoreaudio);
        audioDevices.addAll(audioDevicesALSA);
        return audioDevices;
    }
    
    /**
     * read possible portaudio audio devices
     * @param uvAddress address to ultragrid
     * @param audio_in boolean if we want audio in(true) or audio out(false)
     * @return
     * @throws IOException 
     */
    private List<AudioDevice> read_audio_devices_in_or_out_portaudio(String uvAddress, boolean audio_in) throws IOException{
        if(!correctUv){
            return new ArrayList<>();
        }
        if(!havePortaudio){
            return new ArrayList<>();
        }
        
        Process uvProcess;
        if(audio_in){
            uvProcess = new ProcessBuilder(uvAddress, "-s", "portaudio:help").start();
        }else{
            uvProcess = new ProcessBuilder(uvAddress, "-r", "portaudio:help").start();
        }
        
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        
        List<AudioDevice> audioDevices = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Pattern devicePattern = Pattern.compile("portaudio([:\\d]+) : (.+) \\(output channels: (\\d+); input channels: (\\d+)\\)");
            Matcher deviceMatcher = devicePattern.matcher(line);
            if(deviceMatcher.find()){
                if(audio_in){
                    if(!deviceMatcher.group(4).equals("0")){    //have some input chanels
                        AudioDevice new_device = new AudioDevice();
                        new_device.name = deviceMatcher.group(2);
                        new_device.setting = "portaudio" + deviceMatcher.group(1);
                        audioDevices.add(new_device);
                    }
                }else{
                    if(!deviceMatcher.group(3).equals("0")){    //have some input chanels
                        AudioDevice new_device = new AudioDevice();
                        new_device.name = deviceMatcher.group(2);
                        new_device.setting = "portaudio" + deviceMatcher.group(1);
                        audioDevices.add(new_device);
                    }
                }
            }
            
        }
        return audioDevices;
    }
    
    /**
     * read possible coreaudio audio devices
     * @param uvAddress address to ultragrid
     * @param audio_in boolean if we want audio in(true) or audio out(false)
     * @return
     * @throws IOException 
     */
    private List<AudioDevice> read_audio_devices_in_or_out_coreaudio(String uvAddress, boolean audio_in) throws IOException{
        if(!correctUv){
            return new ArrayList<>();
        }
        if(!haveCoreaudio){
            return new ArrayList<>();
        }
        
        Process uvProcess;
        if(audio_in){
            uvProcess = new ProcessBuilder(uvAddress, "-s", "coreaudio:help").start();
        }else{
            uvProcess = new ProcessBuilder(uvAddress, "-r", "coreaudio:help").start();
        }
         
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
       
        List<AudioDevice> audioDevices = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Pattern devicePattern = Pattern.compile("coreaudio([:\\d]*) : (.+)");
            Matcher deviceMatcher = devicePattern.matcher(line);
            if(deviceMatcher.find()){
                AudioDevice new_device = new AudioDevice();
                new_device.name = deviceMatcher.group(2);
                new_device.setting = "coreaudio" + deviceMatcher.group(1);
                audioDevices.add(new_device);
            }
        }
        return audioDevices;
    }
    
    /**
     * read possible alsa audio devices
     * @param uvAddress address to ultragrid
     * @param audio_in boolean if we want audio in(true) or audio out(false)
     * @return
     * @throws IOException 
     */
    private List<AudioDevice> read_audio_devices_in_or_out_alsa(String uvAddress, boolean audio_in) throws IOException{
        if(!correctUv){
            return new ArrayList<>();
        }
        if(!haveALSA){
            return new ArrayList<>();
        }
        Process uvProcess;
        if(audio_in){
            uvProcess = new ProcessBuilder(uvAddress, "-s", "ALSA:help").start();
        }else{
            uvProcess = new ProcessBuilder(uvAddress, "-r", "ALSA:help").start();
        }
         
        InputStream is = uvProcess.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
       
        List<AudioDevice> audioDevices = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Pattern devicePattern = Pattern.compile("alsa(:\\d*) : (.+)");
            Matcher deviceMatcher = devicePattern.matcher(line);
            if(deviceMatcher.find()){
                AudioDevice new_device = new AudioDevice();
                new_device.name = deviceMatcher.group(2);
                new_device.setting = "alsa" + deviceMatcher.group(1);
                audioDevices.add(new_device);
            }
            
        }
        AudioDevice pulseAudio = new AudioDevice();
        pulseAudio.name = "PulseAudio";
        pulseAudio.setting = "alsa:pusle";
        return audioDevices;
    }
    
    /**
     * check if uv address is correct and what component is present, result save 
     * in global variabile and information text message is set
     * @param uvAddress
     * @param verificationTextField 
     */
    void ultragridOK(String uvAddress){
        if(uvProcess != null){  //if another uv process is open close it
            uvProcess.destroyForcibly();
        }
        
        correctUv = true;   //will be changed if any problem is found
        //initiial tests if it can be ultragrid
        if(uvAddress.isEmpty()){
            uvStatusTextField.setForeground(Color.red);
            uvStatusTextField.setText(languageBundle.getString("EMPTY_PATH"));
            correctUv = false;
        }
        File uvFile = new File(uvAddress);
        if(!uvFile.exists()){
            uvStatusTextField.setForeground(Color.red);
            uvStatusTextField.setText(languageBundle.getString("INVALID_PATH")+ " " + uvFile.getName() + ".");
            correctUv = false;
        }
        if(uvFile.isDirectory()){
            uvStatusTextField.setForeground(Color.red);
            uvStatusTextField.setText(languageBundle.getString("FILE")+ " " + uvFile.getName() + " " + languageBundle.getString("IS_DIRECTORY"));
            correctUv = false;
        }
        if(!uvFile.canExecute()){
            uvStatusTextField.setForeground(Color.red);
            uvStatusTextField.setText(languageBundle.getString("FILE")+ " " + uvFile.getName() + " " + languageBundle.getString("CANNOT_BE_EXECUTED"));
            correctUv = false;
        }
        
        boolean correctUvOutput = false;
        boolean correctUvreturnValue = false;
        
        if(correctUv){
            try {
                // mac return uv help value 10, -v seems be ok
                uvProcess = new ProcessBuilder(uvAddress, "-v").start();

                InputStream is = uvProcess.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                boolean firsLine = true;
                int linesCheckMaxLimit = 100;
                int i = 0;

                while (((line = br.readLine()) != null) && (linesCheckMaxLimit > i)) {
                    i++;
                    Pattern glPattern = Pattern.compile("OpenGL \\.*+ (no|yes)");
                    Matcher glMatcher = glPattern.matcher(line);

                    Pattern sdlPattern = Pattern.compile("SDL \\.*+ (no|yes)");
                    Matcher sdlMatcher = sdlPattern.matcher(line);

                    Pattern portaudioPattern = Pattern.compile("Portaudio \\.*+ (no|yes)");
                    Matcher portaudioMatcher = portaudioPattern.matcher(line);

                    Pattern coreaudioPattern = Pattern.compile("CoreAudio \\.*+ (no|yes)");
                    Matcher coreaudioMatcher = coreaudioPattern.matcher(line);

                    Pattern alsaPattern = Pattern.compile("ALSA \\.*+ (no|yes)");
                    Matcher alsaMatcher = alsaPattern.matcher(line);

                    if(firsLine){
                        Pattern ultragridPattern = Pattern.compile("UltraGrid ");
                        Matcher ultragridMatcher = ultragridPattern.matcher(line);
                        correctUvOutput = ultragridMatcher.find();
                        firsLine = false;
                    }

                    if(glMatcher.find()){
                        if(glMatcher.group(1).equals("yes")){
                            haveGL = true;
                        }else{
                            haveGL = false;
                        }
                    }

                    if(sdlMatcher.find()){
                        if(sdlMatcher.group(1).equals("yes")){
                            haveSDL = true;
                        }else{
                            haveSDL = false;
                        }
                    }

                    if(portaudioMatcher.find()){
                        if(portaudioMatcher.group(1).equals("yes")){
                            havePortaudio = true;
                        }else{
                            havePortaudio = false;
                        }
                    }

                    if(coreaudioMatcher.find()){
                        if(coreaudioMatcher.group(1).equals("yes")){
                            haveCoreaudio = true;
                        }else{
                            haveCoreaudio = false;
                        }
                    }

                    if(alsaMatcher.find()){
                        if(alsaMatcher.group(1).equals("yes")){
                            haveALSA = true;
                        }else{
                            haveALSA = false;
                        }
                    }
                }
                correctUvreturnValue = (uvProcess.exitValue() == 0);
            } catch (IllegalThreadStateException | IOException ex){
                uvProcess.destroyForcibly();
            }
            uvProcess.destroyForcibly();
            //probably overkill destroing process, but I realy dont want to allow process to survive
            if(correctUvreturnValue && correctUvOutput){
                correctUv = true;
                uvStatusTextField.setForeground(Color.getHSBColor((float)0.39, (float)1, (float)0.8));
                uvStatusTextField.setText(languageBundle.getString("ALLRIGHT"));
            }else{
                correctUv = false;
                if(correctUvreturnValue){
                    uvStatusTextField.setForeground(Color.red);
                    uvStatusTextField.setText(languageBundle.getString("ERROR"));
                }else{
                    uvStatusTextField.setForeground(Color.red);
                    uvStatusTextField.setText("UltraGid " + languageBundle.getString("CANNOT_BE_EXECUTED"));
                }
            }
        }
        setAudioStatusTextField();
        setDisplayStatusTextField();
    }
    
    /**
     * set display text field from global information if is possible to use GL and SDL
     */
    void setDisplayStatusTextField(){
        Color greenColor = Color.getHSBColor((float)0.39, (float)1, (float)0.8);
        if(!correctUv){
            displayStatusTextField.setText(languageBundle.getString("NONE"));
            displayStatusTextField.setForeground(Color.RED);
        }else{
            boolean noDisplaySoftware = true;
            displayStatusTextField.setText("");
            if(haveGL){
                noDisplaySoftware = false;
                displayStatusTextField.setText("GL");
            }
            if(haveSDL){
                if(!noDisplaySoftware){
                    displayStatusTextField.setText(displayStatusTextField.getText() + ", ");
                }
                noDisplaySoftware = false;
                displayStatusTextField.setText(displayStatusTextField.getText() + "SDL");
            }
            
            if(noDisplaySoftware){
                displayStatusTextField.setText(languageBundle.getString("NONE"));
                displayStatusTextField.setForeground(Color.RED);
            }else{
                displayStatusTextField.setForeground(greenColor);
            }
        }
    }
    
    /**
     * set audio text field from global information if is possible to use portaudio, coreaudio and ALSA
     */
    void setAudioStatusTextField(){
        Color greenColor = Color.getHSBColor((float)0.39, (float)1, (float)0.8);
        if(!correctUv){
            audioStatusTextField.setText(languageBundle.getString("NONE"));
            audioStatusTextField.setForeground(Color.RED);
        }else{
            boolean noAudioSoftware = true;
            audioStatusTextField.setText("");
            if(havePortaudio){
                noAudioSoftware = false;
                audioStatusTextField.setText("portaudio");
            }
            if(haveCoreaudio){
                if(!noAudioSoftware){
                    audioStatusTextField.setText(audioStatusTextField.getText() + ", ");
                }
                noAudioSoftware = false;
                audioStatusTextField.setText(audioStatusTextField.getText() + "coreaudio");
            }
            if(haveALSA){
                if(!noAudioSoftware){
                    audioStatusTextField.setText(audioStatusTextField.getText() + ", ");
                }
                noAudioSoftware = false;
                audioStatusTextField.setText(audioStatusTextField.getText() + "ALSA");
            }
            
            if(noAudioSoftware){
                audioStatusTextField.setText(languageBundle.getString("NONE"));
                audioStatusTextField.setForeground(Color.RED);
            }else{
                audioStatusTextField.setForeground(greenColor);
            }
        }
    }
    
    /**
     * start ultragrid
     * @param uvAddress
     * @param uvReciveSetting
     * @param uvSendSetting
     * @throws IOException 
     */
    private void startUltragrid(String uvAddress, String uvReciveSetting, String uvSendSetting) throws IOException{
        if(uvProcess != null){
            uvProcess.destroyForcibly();
        }
        if(correctUv){
            ProcessBuilder pb = new ProcessBuilder(uvAddress, "-d", uvReciveSetting, "-t", uvSendSetting);
            Process tmpProcess = pb.start();
            uvProcess = tmpProcess;
                    
        }
    }

    /**
     * fill display combo boxes
     * @param displayBox
     * @param displaySettingBox 
     */
    private void setJComboBoxDisplay(JComboBox displayBox, JComboBox displaySettingBox) {
        displayBox.removeAllItems();
        displayBox.addItem("gl");
        displayBox.addItem("sdl");
        displaySettingBox.removeAllItems();
        displaySettingBox.addItem("none");
        displaySettingBox.addActionListener((ActionEvent event) -> {
            setCorrectDisplaySetting(displayBox, displaySettingBox);
        });
        displayBox.addActionListener((ActionEvent event) -> {
            boolean sdlSelected = displayBox.getSelectedItem().equals("sdl");
            displaySettingBox.removeAllItems();
            displaySettingBox.addItem("none");
            setCorrectDisplaySetting(displayBox, displaySettingBox);
            if(sdlSelected){
                displaySettingBox.addItem("nodecorate");
            }
        });
        if(configuration.has("consumer settings")){
            try {
                String displaySettingLine = configuration.getString("consumer settings");
                String[] partDisplaySettings = displaySettingLine.split(":");
                if(partDisplaySettings[0].equals("gl")){
                    displayBox.setSelectedItem("gl");
                }else{
                    displayBox.setSelectedItem("sdl");
                    if(partDisplaySettings.length > 1){
                        if(partDisplaySettings[1].equals("nodecorate")){
                            displaySettingBox.setSelectedItem("nodecorate");
                        }else{
                            displaySettingBox.setSelectedItem("none");
                        }
                    }
                }
            } catch (JSONException ex) {
                Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    /**
     * load current selected display setting to global variabile 
     * @param displayBox
     * @param displaySettingBox 
     */
    private void setCorrectDisplaySetting(JComboBox displayBox, JComboBox displaySettingBox) {
        if(displayBox == null){
            return;
        }
        if(displaySettingBox == null){
            return;
        }
        if(displayBox.getItemCount() == 0){
            return;
        }
        if(displaySettingBox.getItemCount() == 0){
            return;
        }
        boolean sdlSelected = displayBox.getSelectedItem().equals("sdl");
        boolean nodecorateSelected = displaySettingBox.getSelectedItem().equals("nodecorate");
        if(sdlSelected){
            if(nodecorateSelected){
                displaySetting = "sdl:nodecorate";
            }else{
                displaySetting = "sdl";
            }
        }else{
            displaySetting = "gl";
        }
    }
    
    /**
     * fill audio comboBox from audio devices
     * @param audioBox
     * @param audioDevicis 
     */
    private void setAudioJComboBox(JComboBox audioBox, String audioSetting, List<AudioDevice> audioDevicis){
        audioBox.removeAllItems();
        for(int i=0;i<audioDevicis.size();i++){
            audioBox.addItem(audioDevicis.get(i).name);
        }
        for(int i=0;i<audioDevicis.size();i++){
            if(audioDevicis.get(i).setting.equals(audioSetting)){
                audioBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * find setting string in video devices and set is as selected
     * @param devicesBox
     * @param formatBox
     * @param frameSizeBox
     * @param fpsBox
     * @param videoDevices
     * @param videoSetting 
     */
    private void SetVideoSettingFromConfig(JComboBox devicesBox, JComboBox formatBox, JComboBox frameSizeBox, JComboBox fpsBox,
                                            List<VideoDevice> videoDevices, String videoSetting){
        for(int i=0;i<videoDevices.size();i++){
            for(int j=0;j<videoDevices.get(i).vpf.size();j++){
                for(int k=0;k<videoDevices.get(i).vpf.get(j).vfs.size();k++){
                    for(int l=0;l<videoDevices.get(i).vpf.get(j).vfs.get(k).fps.size();l++){
                        if(videoDevices.get(i).vpf.get(j).vfs.get(k).fps.get(l).setting.equals(videoSetting)){
                            devicesBox.setSelectedIndex(i);
                            formatBox.setSelectedIndex(j);
                            frameSizeBox.setSelectedIndex(k);
                            fpsBox.setSelectedIndex(l);
                            return;
                        }
                    }
                }
            }
        }

    }
    
    /**
     * @param jsonFile file to be read from
     * @return JSONObject that was read from file
     */
    JSONObject readJsonFile(File jsonFile){
        try {
            String entireFileText = new Scanner(jsonFile).useDelimiter("\\A").next();
            return new JSONObject(entireFileText);
        } catch (JSONException | FileNotFoundException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * dispose of this window
     */
    public void discardAction(){
        dispose();
    }

    boolean sameDeviceCameraPresentation(JComboBox devicesBoxCamera, JComboBox devicesBoxPresentation){
        if((devicesBoxCamera.getItemCount() <= 0) && (devicesBoxPresentation.getItemCount() <= 0)){
            return false;
        }
        if(devicesBoxCamera.getSelectedItem().equals(devicesBoxPresentation.getSelectedItem())){    
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * save setting that was changed
     */
    private void saveSettingAction() {
        JSONObject newClientConfiguration = new JSONObject();
        JSONObject raiseHandColorJson = new JSONObject();
        JSONObject talkingColorJson = new JSONObject();
        JSONArray serverIps = new JSONArray();
        
        String producerSetting = getVideoSettings(mainCameraBox, mainCameraPixelFormatBox, mainCameraFrameSizeBox, mainCameraFPSBox, videoDevices);
        String presentationSetting = getVideoSettings(presentationBox, presentationPixelFormatBox, presentationFrameSizeBox, presentationFPSBox, videoDevices);
        
        String audioOutSetting  = getAudioSetting(audioOutComboBox, audioConsumers);
        String audioInSetting = getAudioSetting(audioInComboBox, audioProducers);
        
        int resizeValue;
        try{
            resizeValue = Integer.valueOf(setResizeAmount.getText());
        }catch(NumberFormatException e){
            resizeValue = 0;
        }
        
        try {
            for(int i=0;i<ipAddresses.size();i++){
                JSONObject newIp = new JSONObject();
                newIp.put("ip", ipAddresses.get(i).address);
                newIp.put("name", ipAddresses.get(i).name);
                newIp.put("port", ipAddresses.get(i).port);
                serverIps.put(newIp);
            }
            
            raiseHandColorJson.put("red", raiseHandcolorChooser.getColor().getRed());
            raiseHandColorJson.put("blue", raiseHandcolorChooser.getColor().getBlue());
            raiseHandColorJson.put("green", raiseHandcolorChooser.getColor().getGreen());
            
            talkingColorJson.put("red", talkingColorChooser.getColor().getRed());
            talkingColorJson.put("blue", talkingColorChooser.getColor().getBlue());
            talkingColorJson.put("green", talkingColorChooser.getColor().getGreen());
            
            newClientConfiguration.put("this ip", myIpSetTextField.getText());
            newClientConfiguration.put("ultragrid path", uvPathString);
            newClientConfiguration.put("layout path", layoutPathString);
            newClientConfiguration.put("producer settings", producerSetting);
            newClientConfiguration.put("consumer settings", displaySetting);
            newClientConfiguration.put("audio consumer", audioOutSetting);
            newClientConfiguration.put("audio producer", audioInSetting);
            if(presentationUsed && !sameDeviceCameraPresentation(mainCameraBox, presentationBox)){
                newClientConfiguration.put("presentation producer", presentationSetting);
                newClientConfiguration.put("presentation", true);
            }else{
                newClientConfiguration.put("presentation", false);
            }
            newClientConfiguration.put("language", getLanguage());
            newClientConfiguration.put("raise hand color", raiseHandColorJson);
            newClientConfiguration.put("talking color", talkingColorJson);
            newClientConfiguration.put("server ips", serverIps);
            newClientConfiguration.put("talking resizing", resizeValue);
            newClientConfiguration.put("student only", studentOnly);
        } catch (JSONException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            imt.openErrorWindow(languageBundle.getString("ERROR_CANNOT_CREATE_SETTING"));
            return; //stop saving when error ocure
        }
        
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(configurationFile);
            fileWriter.write(newClientConfiguration.toString());
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
            imt.openErrorWindow(languageBundle.getString("ERROR_CANNOT_SAVE_SETTINGS"));
            return; //stop saving when error ocure
        }
        
        imt.loadClientConfigurationFromFile();
        imt.initServerChooseWindow();
        imt.initSettingRoomWindow();
        imt.initErrorWindow();
        imt.initIpSettingWindow();
        
        if(sameDeviceCameraPresentation(mainCameraBox, presentationBox) && presentationUsed){
            imt.openErrorWindow(languageBundle.getString("ERROR_PRESENTATION_AND_CAMERA_SAME_DEVICE"));
            dispose();
            return;
        }
        
        if(imt.logedIn){
            imt.openServerChooseWindow();
        }else{
            imt.openRoleNameWindow();
        }
        dispose();
    }
    
    /**
     * load ip address
     * @return list of ip addresses loaded from configuration
     */
    private List<IPServerSaved> loadIpAddreses(){
        List<IPServerSaved> ret = new ArrayList<>();
        JSONArray ipAddreses;
        try {
            ipAddreses = configuration.getJSONArray("server ips");
            for(int i=0;i<ipAddreses.length();i++){
                IPServerSaved ipAddress = new IPServerSaved();
                JSONObject loadedIP = ipAddreses.getJSONObject(i);
                ipAddress.address = loadedIP.getString("ip");
                ipAddress.name = loadedIP.getString("name");
                ipAddress.port = loadedIP.getString("port");
                ret.add(ipAddress);
            }
        } catch (JSONException ex) {
            Logger.getLogger(OptionsMainMenuWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    /**
     * fill server ip addresses to comboBox
     * @param ipAddresses list of ip addresses
     * @param serverIpSelect comboBox to be filled in
     */
    private void setServerIpsComboBox(List<IPServerSaved> ipAddresses, JComboBox serverIpSelect) {
        serverIpSelect.removeAllItems();
        for(int i=0;i<ipAddresses.size();i++){
            serverIpSelect.addItem(ipAddresses.get(i).address);
        }
    }
    
    /**
     * get audio setting from comboBox
     * @param audioBox comboBox to get language from
     * @param audioDevicis audio devices to get setting from
     * @return 
     */
    private String getAudioSetting(JComboBox audioBox, List<AudioDevice> audioDevicis){
        if(audioBox == null || audioBox.getItemCount() == 0){
            return "";
        }
        int selectedIndex = audioBox.getSelectedIndex();
        return audioDevicis.get(selectedIndex).setting;
    }

    /**
     * fill language comboBox
     * @param languageCombobox combo box to be filled in
     * @param setLanguage language to be set
     */
    private void fillLanguageComboBox(JComboBox languageCombobox, String setLanguage) {
        languageCombobox.removeAllItems();
        languageCombobox.addItem("Slovenský");
        languageCombobox.addItem("Český");
        languageCombobox.addItem("English");
        if(!setLanguage.isEmpty()){
            languageCombobox.setSelectedItem(setLanguage);
        }
    }
    
    /**
     * return language name
     * @return return language
     */
    private String getLanguage(){
        if(languageCombobox.getItemCount() > 0){
            return languageCombobox.getSelectedItem().toString();
        }else{
            return "";
        }
    }
    
    /**
     * add to list of video devices testcard device
     * @param videoDevices 
     */
    private void addTestcrdDevice(List<VideoDevice> videoDevices){
        VideoDevice testcardDevice = new VideoDevice();
        testcardDevice.device = "testcard";
        testcardDevice.name = "testcard";
        testcardDevice.vpf = new ArrayList<>();
        
        VideoPixelFormat testcardPixelFormat = new VideoPixelFormat();
        testcardPixelFormat.name = "RGB";
        testcardPixelFormat.pixelFormat = "RGB";
        testcardPixelFormat.vfs = new ArrayList<>();
        testcardDevice.vpf.add(testcardPixelFormat);
        
        VideoFrameSize testcardFrameSize_255 = new VideoFrameSize();
        testcardFrameSize_255.widthXheight = "255x255";
        testcardFrameSize_255.fps = new ArrayList<>();
        testcardPixelFormat.vfs.add(testcardFrameSize_255);
        
        for(int i=10;i<=60;i++){
            VideoFPS testcardFPS_255 = new VideoFPS();
            testcardFPS_255.fps = Integer.toString(i);
            testcardFPS_255.setting = "testcard:255:255:" + Integer.toString(i) + ":RGB";
            testcardFrameSize_255.fps.add(testcardFPS_255);
        }
        
        VideoFrameSize testcardFrameSize_1024 = new VideoFrameSize();
        testcardFrameSize_1024.widthXheight = "1024x768";
        testcardFrameSize_1024.fps = new ArrayList<>();
        testcardPixelFormat.vfs.add(testcardFrameSize_1024);
        
        for(int i=10;i<=30;i++){
            VideoFPS testcardFPS_1024 = new VideoFPS();
            testcardFPS_1024.fps = Integer.toString(i);
            testcardFPS_1024.setting = "testcard:1024:768:" + Integer.toString(i) + ":RGB";
            testcardFrameSize_1024.fps.add(testcardFPS_1024);
        }
        
        videoDevices.add(testcardDevice);
    }
}

/**
 * class for ip address saved in configuration file and work with this data
 * @author Huvart
 */
class IPServerSaved{
    public String name;
    public String address;
    public String port;
}

/**
 * part of group of classes to save video devices
 * this one is for video device
 * @author Huvart
 */
class VideoDevice{
    public String name;
    public String device;
    public List<VideoPixelFormat> vpf = new ArrayList<>();
}

/**
 * part of group of classes to save video devices
 * this one is for video format
 * @author Huvart
 */
class VideoPixelFormat{
    public String name;
    public String pixelFormat;
    public List<VideoFrameSize> vfs = new ArrayList<>();
}

/**
 * part of group of classes to save video devices
 * this one is for video frame size
 * @author Huvart
 */
class VideoFrameSize{
    public String widthXheight;
    public List<VideoFPS> fps;
}

/**
 * part of group of classes to save video devices
 * this one is for fps
 * @author Huvart
 */
class VideoFPS{
    String fps;
    String setting;
}

/**
 * class to save audio devices
 * @author Huvart
 */
class AudioDevice{
    String name;
    String setting;
}