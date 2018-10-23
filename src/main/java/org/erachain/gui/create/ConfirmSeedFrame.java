package org.erachain.gui.create;
// 03/03

import org.erachain.controller.Controller;
import org.erachain.core.crypto.Base58;
import org.erachain.lang.Lang;
import org.erachain.settings.Settings;
import org.erachain.utils.SaveStrToFile;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class ConfirmSeedFrame extends JFrame {

    private static final Logger LOGGER = Logger
            .getLogger(ConfirmSeedFrame.class);
    private CreateWalletFrame parent;
    private JTextField seedTxt;
    private JTextField passwordTxt;
    private JTextField confirmPasswordTxt;
	private JTextField jTextFieldDataDir;

    public ConfirmSeedFrame(CreateWalletFrame parent) {
        super(Lang.getInstance().translate("Erachain.org") + " - " + Lang.getInstance().translate("Create Wallet"));

        //ICON
        List<Image> icons = new ArrayList<Image>();
        icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon16.png"));
        icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon32.png"));
        icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon64.png"));
        icons.add(Toolkit.getDefaultToolkit().getImage("images/icons/icon128.png"));
        this.setIconImages(icons);

        //PARENT
        this.parent = parent;

        //LAYOUT
        this.setLayout(new GridBagLayout());

        //PADDING
        ((JComponent) this.getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

        //LABEL GBC
        GridBagConstraints labelGBC = new GridBagConstraints();
        labelGBC.insets = new Insets(5, 5, 5, 5);
        labelGBC.fill = GridBagConstraints.HORIZONTAL;
        labelGBC.anchor = GridBagConstraints.NORTHWEST;
        labelGBC.weightx = 1;
        labelGBC.gridwidth = 2;
        labelGBC.gridx = 0;

        //BUTTON GBC
        GridBagConstraints buttonGBC = new GridBagConstraints();
        buttonGBC.insets = new Insets(5, 5, 0, 5);
        buttonGBC.fill = GridBagConstraints.NONE;
        buttonGBC.anchor = GridBagConstraints.NORTHWEST;
        buttonGBC.gridwidth = 1;
        buttonGBC.gridx = 0;

        
        
        //LABEL
        labelGBC.gridy = 0; //labelGBC.gridy+1;
        JLabel label1 = new JLabel(Lang.getInstance().translate("Please confirm your wallet seed") + ":");
        this.add(label1, labelGBC);


        //ADD TEXTBOX
        labelGBC.gridy = labelGBC.gridy+1;
        this.seedTxt = new JTextField();
        this.add(this.seedTxt, labelGBC);

        // MENU
        JPopupMenu menu = new JPopupMenu();
        JMenuItem pasteSeed = new JMenuItem(Lang.getInstance().translate("Paste"));
        pasteSeed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    String clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);
                    seedTxt.setText(clipboardContent);
                } catch (UnsupportedFlavorException | IOException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
        });
        menu.add(pasteSeed);
        seedTxt.setComponentPopupMenu(menu);


        //LABEL
        labelGBC.gridy = labelGBC.gridy+1;
        labelGBC.insets.top = 0;
        JLabel label2 = new JLabel(Lang.getInstance().translate("By confirming your wallet seed we know you have saved the seed."));
        this.add(label2, labelGBC);

        //LABEL
        labelGBC.gridy = labelGBC.gridy+1;
        labelGBC.insets.top = 10;
        JLabel label3 = new JLabel(Lang.getInstance().translate("Please enter your wallet password") + ":");
        this.add(label3, labelGBC);

        //ADD TEXTBOX
        labelGBC.gridy = labelGBC.gridy+1;
        labelGBC.insets.top = 5;
        this.passwordTxt = new JPasswordField();
        this.add(this.passwordTxt, labelGBC);

        //LABEL
        labelGBC.gridy = labelGBC.gridy+1;
        labelGBC.insets.top = 10;
        JLabel label4 = new JLabel(Lang.getInstance().translate("Please confirm your password") + ":");
        this.add(label4, labelGBC);

        //ADD TEXTBOX
        labelGBC.gridy = labelGBC.gridy+1;
        labelGBC.insets.top = 5;
        this.confirmPasswordTxt = new JPasswordField();
        this.add(this.confirmPasswordTxt, labelGBC);

       // path label
        labelGBC.gridy = labelGBC.gridy+1;
        JLabel labelPath = new JLabel(Lang.getInstance().translate("Set the Wallet directory or leave it as default") + ":");
        this.add(labelPath, labelGBC);
        JPanel pan = new JPanel();
        pan.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints panGBC = new GridBagConstraints();
        panGBC.insets = new Insets(5, 5, 5, 5);
        panGBC.fill = GridBagConstraints.HORIZONTAL;
        panGBC.anchor = GridBagConstraints.NORTHWEST;
        panGBC.weightx = 0.2;
        panGBC.gridx = 0;
        panGBC.gridy =0;
      //path text
      //  labelGBC.gridy = labelGBC.gridy+1;
        jTextFieldDataDir = new JTextField(Settings.getInstance().getWalletDir());
        jTextFieldDataDir.setEditable(false);
        pan.add(jTextFieldDataDir, panGBC);
       // this.add(jTextFieldDataDir, labelGBC);
     
        // button path  
        JButton btnBrowseWallet = new JButton(Lang.getInstance().translate("Browse..."));
        labelGBC.gridy = labelGBC.gridy+1;
        btnBrowseWallet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String path = jTextFieldDataDir.getText(); 
                File ff = new File(path);
                if (!ff.exists()) path = ".." + File.separator;
                fileopen.setCurrentDirectory(new File(path));
                int ret = fileopen.showDialog(null, Lang.getInstance().translate("Set wallet dir"));
                if (ret == JFileChooser.APPROVE_OPTION) {
                	jTextFieldDataDir.setText(fileopen.getSelectedFile().toString());
                	
                }
            }
        });
        panGBC = new java.awt.GridBagConstraints();
        panGBC.anchor = java.awt.GridBagConstraints.NORTHEAST;
        pan.add(btnBrowseWallet, panGBC);
        
        this.add(pan, labelGBC);
        
        
        //BUTTON confirm
        buttonGBC.gridy = labelGBC.gridy+1;
        JButton confirmButton = new JButton(Lang.getInstance().translate("Confirm"));
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onConfirmClick();
            }
        });
        //    confirmButton.setPreferredSize(new Dimension(110, 25));
        this.add(confirmButton, buttonGBC);

        //BUTTON BACK
        buttonGBC.gridx = 1;
        JButton backButton = new JButton(Lang.getInstance().translate("Back"));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBackClick();
            }
        });
        //     backButton.setPreferredSize(new Dimension(110, 25));
        this.add(backButton, buttonGBC);

        //CLOSE NICELY
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Controller.getInstance().stopAll(0);
                //    	System.exit(0);
            }
        });

        //CALCULATE HEIGHT WIDTH
        this.pack();
        //   	this.setSize(500, this.getHeight());

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void onConfirmClick() {

        //CHECK IF SEEDS MATCH
        byte[] seed = this.parent.getSeed();

        byte[] confirm;
        try {
            confirm = Base58.decode(this.seedTxt.getText());
        } catch (Exception e) {
            confirm = null;
        }

        if (!Arrays.equals(seed, confirm) || seed == null || seed.length != 32) {
            //INVALID SEED
            String message = Lang.getInstance().translate("Invalid or incorrect seed!");
            JOptionPane.showMessageDialog(new JFrame(), message, Lang.getInstance().translate("Invalid seed"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = this.passwordTxt.getText();
        if (password.length() == 0) {
            //PASSWORD CANNOT BE EMPTY
            String message = Lang.getInstance().translate("Password cannot be empty!");
            JOptionPane.showMessageDialog(new JFrame(), message, Lang.getInstance().translate("Invalid password"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(this.confirmPasswordTxt.getText())) {
            //PASSWORDS DO NOT MATCH
            String message = Lang.getInstance().translate("Password do not match!");
            JOptionPane.showMessageDialog(new JFrame(), message, Lang.getInstance().translate("Invalid password"), JOptionPane.ERROR_MESSAGE);
            return;
        }

       
    	
        //CALLBACK
        this.parent.onConfirm(password,jTextFieldDataDir.getText() );

        //CLOSE THIS WINDOW
        this.dispose();
    }

    private void onBackClick() {
        this.parent.setVisible(true);

        this.dispose();
    }
}