package org.erachain.gui.telegrams;

import org.erachain.core.account.Account;
import org.erachain.gui.models.AccountsComboBoxModel;
import org.erachain.lang.Lang;

import javax.swing.*;

/**
 * @author Саша
 */
public class LeftTelegram extends javax.swing.JPanel {

    private AccountsComboBoxModel accountsModel;
    private ButtonGroup group;
    public JButton jButtonGetTelegrams;
  
/**
    * Creates new form leftTekegram
    */
   public LeftTelegram() {
       initComponents();
       this.jLabelAccount.setText(Lang.T("Filter by My Account") + ":");
       this.accountsModel = new AccountsComboBoxModel();
       this.jComboAccount.setModel(accountsModel);
       this.jLabel_AddAccount.setText(Lang.T("Add Recipient"));
       this.jButtonAddAccount.setText(Lang.T("Add Recipient"));
       this.jTextField_AddAccount.setText("");
       this.jCxbAllmessages.setText(Lang.T("All Recipients"));
       this.jCxbRecipientmessages.setText(Lang.T("Filter by Recipients List"));
       this.jButtonGetTelegrams.setText(Lang.T("Get Telegrams from Network"));

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

       jPanelTop = new javax.swing.JPanel();
       jComboAccount = new javax.swing.JComboBox<>();
       jLabelAccount = new javax.swing.JLabel();
       jScrollPaneCenter = new javax.swing.JScrollPane();
       jPanelBottom = new javax.swing.JPanel();
       jLabel_AddAccount = new javax.swing.JLabel();
       jTextField_AddAccount = new javax.swing.JTextField();
       jButtonAddAccount = new javax.swing.JButton();
       jCxbAllmessages = new JRadioButton();
       jCxbRecipientmessages = new JRadioButton();    
       jButtonGetTelegrams = new JButton();

       java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
       layout.columnWidths = new int[] {0};
       layout.rowHeights = new int[] {0, 8, 0, 8, 0};
       setLayout(layout);

       jPanelTop.setLayout(new java.awt.GridBagLayout());

   //    jComboAccount.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 1;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.weightx = 0.3;
       jPanelTop.add(jComboAccount, gridBagConstraints);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.insets = new java.awt.Insets(4, 11, 0, 0);
       jPanelTop.add(jLabelAccount, gridBagConstraints);
       
       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 1;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.weightx = 0.3;
       gridBagConstraints.insets = new java.awt.Insets(4, 11, 0, 0);
       jPanelTop.add(new JLabel(Lang.T("Recipients")), gridBagConstraints);
       
       group = new ButtonGroup();
       group.add(jCxbAllmessages);
       group.add(jCxbRecipientmessages);
       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 1;
       gridBagConstraints.gridy = 2;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.weightx = 0.3;
       jPanelTop.add(jCxbAllmessages, gridBagConstraints);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 2;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      // gridBagConstraints.insets = new java.awt.Insets(4, 11, 0, 0);
       jPanelTop.add(jCxbRecipientmessages, gridBagConstraints);
       
       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 3;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      // gridBagConstraints.insets = new java.awt.Insets(4, 11, 0, 0);
       jPanelTop.add(jButtonGetTelegrams, gridBagConstraints);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
       gridBagConstraints.weightx = 0.3;
       gridBagConstraints.insets = new java.awt.Insets(8, 7, 0, 7);
       add(jPanelTop, gridBagConstraints);

      // jTableFavoriteAccounts.setModel(new javax.swing.table.DefaultTableModel(
      //     new Object [][] {
      //         {null, null}
      //     },
      //     new String [] {
      //         "Title 1", "Title 2", "Title 3", "Title 4"
      //     }
      // ));
      

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 2;
       gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
       gridBagConstraints.weightx = 0.3;
       gridBagConstraints.weighty = 0.4;
       gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 7);
       add(jScrollPaneCenter, gridBagConstraints);

       java.awt.GridBagLayout jPanel2Layout = new java.awt.GridBagLayout();
       jPanel2Layout.columnWidths = new int[] {0, 8, 0, 8, 0};
       jPanel2Layout.rowHeights = new int[] {0};
       jPanelBottom.setLayout(jPanel2Layout);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
   //    jPanelBottom.add(jLabel_AddAccount, gridBagConstraints);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 2;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.ipadx = 53;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       gridBagConstraints.weightx = 0.3;
       gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 4;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
       jPanelBottom.add(jButtonAddAccount, gridBagConstraints);

       gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 4;
       gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
       gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
       gridBagConstraints.weightx = 0.3;
       gridBagConstraints.insets = new java.awt.Insets(0, 7, 8, 7);
       add(jPanelBottom, gridBagConstraints);
   }// </editor-fold>                        


   // Variables declaration - do not modify     
   public JRadioButton jCxbAllmessages;
   public JRadioButton jCxbRecipientmessages;    
   public javax.swing.JButton jButtonAddAccount;
   public javax.swing.JComboBox<Account> jComboAccount;
   private javax.swing.JLabel jLabelAccount;
   private javax.swing.JLabel jLabel_AddAccount;
   private javax.swing.JPanel jPanelBottom;
   private javax.swing.JPanel jPanelTop;
   public javax.swing.JScrollPane jScrollPaneCenter;
   public javax.swing.JTextField jTextField_AddAccount;
   // End of variables declaration                   
}
