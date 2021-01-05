package org.erachain.gui.items.mails;

import org.erachain.controller.Controller;
import org.erachain.core.crypto.Base58;
import org.erachain.core.transaction.RSend;
import org.erachain.gui.PasswordPane;
import org.erachain.gui.library.MAccoutnTextField;
import org.erachain.gui.library.MTextPane;
import org.erachain.gui.library.SignLibraryPanel;
import org.erachain.lang.Lang;
import org.erachain.utils.DateTimeFormat;
import org.erachain.utils.MenuPopupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Саша
 */
public class MailInfo extends javax.swing.JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailInfo.class);
    public SignLibraryPanel voush_Library_Panel;
    public JTabbedPane jTabbedPane1;
    RSend trans;
    boolean encrypted;
    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_Block;
    private javax.swing.JLabel jLabel_Data;
    private javax.swing.JLabel jLabel_Message;
    private javax.swing.JLabel jLabel_Reciever;
    private javax.swing.JLabel jLabel_Sender;
    private javax.swing.JLabel jLabel_Title;
    private javax.swing.JScrollPane jScrollPane1;
    //    private javax.swing.JTextArea jTextArea_Messge;
    private MTextPane jTextArea_Messge;
    private javax.swing.JTextField jTextField_Block;
    private javax.swing.JTextField jTextField_Data;
    private MAccoutnTextField jTextField_Reciever;
    private MAccoutnTextField jTextField_Sender;
    private javax.swing.JTextField jTextField_Title;
    /**
     * Creates new form MailInfo
     */
    public MailInfo(RSend trans) {

        this.trans = trans;
        if (this.trans.isEncrypted()) encrypted = true;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel_Block = new javax.swing.JLabel();
        jTextField_Block = new javax.swing.JTextField();
        jLabel_Data = new javax.swing.JLabel();
        jTextField_Data = new javax.swing.JTextField();
        jLabel_Sender = new javax.swing.JLabel();
        //    jTextField_Sender = new javax.swing.JTextField();
        jLabel_Message = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea_Messge = new MTextPane();
        jLabel_Reciever = new javax.swing.JLabel();
        //    jTextField_Reciever = new javax.swing.JTextField();
        jLabel_Title = new javax.swing.JLabel();
        jTextField_Title = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel_Block.setText(Lang.getInstance().translate("Block") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 4);
        add(jLabel_Block, gridBagConstraints);

        jTextField_Block.setEditable(false);
        MenuPopupUtil.installContextMenu(jTextField_Block);
        jTextField_Block.setText(trans.viewHeightSeq());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(jTextField_Block, gridBagConstraints);


        jLabel_Data.setText(Lang.getInstance().translate("Date") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 4);
        add(jLabel_Data, gridBagConstraints);

        jTextField_Data.setEditable(false);
        MenuPopupUtil.installContextMenu(jTextField_Data);
        jTextField_Data.setText(DateTimeFormat.timestamptoString(trans.getTimestamp()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 9);
        add(jTextField_Data, gridBagConstraints);

        jLabel_Sender.setText(Lang.getInstance().translate("Sender") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        add(jLabel_Sender, gridBagConstraints);

        jTextField_Sender = new MAccoutnTextField(trans.getCreator());
        jTextField_Sender.setEditable(false);
        //      jTextField_Sender.setText(trans.getCreator().getPersonAsString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 9);
        add(jTextField_Sender, gridBagConstraints);

        jLabel_Reciever.setText(Lang.getInstance().translate("Recipient") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        add(jLabel_Reciever, gridBagConstraints);

        jTextField_Reciever = new MAccoutnTextField(trans.getRecipient());
        jTextField_Reciever.setEditable(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 9);
        add(jTextField_Reciever, gridBagConstraints);

        jLabel_Title.setText(Lang.getInstance().translate("Title") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        add(jLabel_Title, gridBagConstraints);

        jTextField_Title.setEditable(false);
        MenuPopupUtil.installContextMenu(jTextField_Title);
        jTextField_Title.setText(trans.getTitle());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 9);
        add(jTextField_Title, gridBagConstraints);

        jLabel_Message.setText(Lang.getInstance().translate("Message") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        add(jLabel_Message, gridBagConstraints);


        //   jTextArea_Messge.setEditable(false);
        //	jTextArea_Messge.setContentType("text/html");

        //	MenuPopupUtil.installContextMenu(jTextArea_Messge);
        //      MenuPopupUtil.installContextMenu(jTextArea_Messge);
        //     jTextArea_Messge.setColumns(20);
        //     jTextArea_Messge.setRows(5);
        //     jTextArea_Messge.setLineWrap(true);
        //jTextArea_Messge.setText(descript_Mesage());
        jTextArea_Messge.setText(trans.viewData());
        jTextArea_Messge.setPreferredSize(new Dimension(300, 200));
        //      jTextArea_Messge.setMaximumSize(new Dimension(600,800));
        MenuPopupUtil.installContextMenu(jTextArea_Messge.text_pane);

    /*
        StyledDocument doc = (StyledDocument) jTextArea_Messge.getDocument();
        // Create a style object and then set the style attributes
        Style style = doc.addStyle("StyleName", null);
        StyleConstants.setFontSize(style, UIManager.getFont("TextField.font").getSize());
        try {
			doc.insertString(doc.getLength(), descript_Mesage(), style);
		} catch (BadLocationException e1) {
                    logger.error(e1.getMessage(), e1);
		}
     */

        //jTextArea_Messge.setText();
        //    scrollPaneDescription.setViewportView(jTextArea_Messge);


        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 9);
        add(jTextArea_Messge, gridBagConstraints);


        jButton1.setText(Lang.getInstance().translate("Decrypt"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 7, 9);
        if (trans.isEncrypted()) add(jButton1, gridBagConstraints);


        jButton1.addActionListener(new ActionListener()

        {
            public void actionPerformed(ActionEvent e) {
                encrypt();
            }
        });

        jTextArea_Messge.text_pane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {


                    encrypt();

                }
            }
        });


        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        // add(jScrollPane2, gridBagConstraints);


        jTabbedPane1 = new javax.swing.JTabbedPane();
        voush_Library_Panel = new SignLibraryPanel(trans);
        jTabbedPane1.add(voush_Library_Panel);
        add(jTabbedPane1, gridBagConstraints);


    }// </editor-fold>
    // End of variables declaration 

    void encrypt() {

        //	jTextArea_Messge.setContentType("text/html");
        //	if (trans.isText())  jTextArea_Messge.setContentType("text");
        if (!trans.isEncrypted())
            return;

        if (encrypted) {
            if (!Controller.getInstance().isWalletUnlocked()) {
                //ASK FOR PASSWORD
                String password = PasswordPane.showUnlockWalletDialog(this);
                if (!Controller.getInstance().unlockWallet(password)) {
                    //WRONG PASSWORD
                    JOptionPane.showMessageDialog(null, Lang.getInstance().translate("Invalid password"), Lang.getInstance().translate("Unlock Wallet"), JOptionPane.ERROR_MESSAGE);

                    //		encrypted =!encrypted;

                    return;
                }
            }

            byte[] decryptedData = Controller.getInstance().decrypt(trans.getCreator(),
                    trans.getRecipient(), trans.getData());

            if (decryptedData == null) {
                jTextArea_Messge.setText(Lang.getInstance().translate("Decrypt Error!"));
            } else {
                jTextArea_Messge.setText(trans.isText() ?
                        new String(decryptedData, StandardCharsets.UTF_8)
                        : Base58.encode(decryptedData)); //Converter.toHex(decryptedData));

                jButton1.setText(Lang.getInstance().translate("Encrypt message"));
                encrypted = !encrypted;
            }
        } else {
            jTextArea_Messge.setText(trans.viewData());
            jButton1.setText(Lang.getInstance().translate("Decrypt"));
            encrypted = !encrypted;
        }
        //encrypted.isSelected();

    }

    /*
    private String descript_Mesage() {
        String imgLock = "";

        byte[] data =
        if(this.encrypted)
        {
                //jTextArea_Messge.setContentType("text/html");
                imgLock = "<img src='file:images/messages/locked.png'>";
                //return "<html>"+imgLock+"&nbsp;&nbsp;"+Lang.getInstance().translate( "Encrypted")+"</>";
                return imgLock+"&nbsp;&nbsp;"+Lang.getInstance().translate( "Encrypted")+"</>";
        }



        if ( trans.isText() ) {
            //jTextArea_Messge.setContentType("text");
            //jTextArea_Messge.setContentType("text/html");
            return Processor.process(new String(trans.getData(), StandardCharsets.UTF_8));
        }
        //jTextArea_Messge.setContentType("text/html");

        return Converter.toHex(trans.viewData());
    }
    */
    public void delay_on_Close() {
        voush_Library_Panel.delay_on_close();
    }

}
