package org.erachain.gui.telegrams;

import org.erachain.controller.Controller;
import org.erachain.core.crypto.Base58;
import org.erachain.core.transaction.RSend;
import org.erachain.core.transaction.Transaction;
import org.erachain.gui.PasswordPane;
import org.erachain.gui.library.MTable;
import org.erachain.lang.Lang;
import org.erachain.utils.TableMenuPopupUtil;
import org.mapdb.Fun.Tuple3;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.*;

import static org.erachain.gui.items.utils.GUIUtils.checkWalletUnlock;

/**
 * @author Саша
 */
public class RightTelegramPanel extends javax.swing.JPanel {

    /**
     * Creates new form rightTelegramPanel
     */
    JPopupMenu menu;

    public WalletTelegramsFilterTableModel walletTelegramsFilterTableModel;
    protected int row;


    public RightTelegramPanel() {

        walletTelegramsFilterTableModel = new WalletTelegramsFilterTableModel();
        jTableMessages = new MTable(walletTelegramsFilterTableModel);

        // mouse from favorine column
        jTableMessages.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                Point point = e.getPoint();
                java.util.Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        int row = jTableMessages.rowAtPoint(point);
                        jTableMessages.setRowSelectionInterval(row, row);

                        int crow = jTableMessages.convertRowIndexToModel(row);
                        Transaction transaction = walletTelegramsFilterTableModel.getItem(crow);

                        if (e.getClickCount() == 2) {
                            tableMouse2Click(crow, transaction);
                        }

                    }
                }, 10);
            }
        });


        jTableMessages.setAutoCreateRowSorter(false);

        // jTableMessages.setRowHeight(50);
        jTableMessages.setDefaultRenderer(Long.class, new RendererMessage());
        jTableMessages.setDefaultRenderer(Tuple3.class, new RendererMessage());

// sorter
        TableRowSorter<WalletTelegramsFilterTableModel> t = new TableRowSorter<WalletTelegramsFilterTableModel>(walletTelegramsFilterTableModel);
        t.setSortable(0, false); //Указываем, что сортировать будем в первой колонке
        //   t.setSortable(1, false); // а в других нет

        if (false) {
            // comparator
            t.setComparator(0, new Comparator<Tuple3<String, String, Transaction>>() {
                @Override
                public int compare(Tuple3<String, String, Transaction> o1, Tuple3<String, String, Transaction> o2) {
                    // TODO Auto-generated method stub
                    return o2.c.getTimestamp().compareTo(o1.c.getTimestamp());
                }
            });
        }

        // sort list  - AUTO sort
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        t.setSortKeys(sortKeys);
        // sort table
        jTableMessages.setRowSorter(t);
        // end sortet

        initComponents();

        initMenu();

        TableMenuPopupUtil.installContextMenu(jTableMessages, menu);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelTop = new javax.swing.JPanel();
        jLabelLeft = new javax.swing.JLabel();
        jLabelCenter = new javax.swing.JLabel();
        jLabelRaght = new javax.swing.JLabel();
        jScrollPaneCenter = new javax.swing.JScrollPane();
        jcheckIsEnscript = new JCheckBox();
        jPanelBottom = new javax.swing.JPanel();
        jScrollPaneText = new javax.swing.JScrollPane();
        jTextPaneText = new javax.swing.JTextPane();
        jButtonSendTelegram = new javax.swing.JButton();

        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[]{0};
        layout.rowHeights = new int[]{0, 8, 0, 8, 0};
        setLayout(layout);

        java.awt.GridBagLayout jPanelTopLayout = new java.awt.GridBagLayout();
        jPanelTopLayout.columnWidths = new int[]{0, 6, 0, 6, 0};
        jPanelTopLayout.rowHeights = new int[]{0};
        jPanelTop.setLayout(jPanelTopLayout);

        jLabelLeft.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelLeft.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 0.3;
        jPanelTop.add(jLabelLeft, gridBagConstraints);

        jLabelCenter.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanelTop.add(jLabelCenter, gridBagConstraints);

        jLabelRaght.setText("jLabel3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.3;
        jPanelTop.add(jLabelRaght, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(9, 10, 0, 11);
        add(jPanelTop, gridBagConstraints);

        jScrollPaneCenter.setViewportView(jTableMessages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 11);
        add(jScrollPaneCenter, gridBagConstraints);

        java.awt.GridBagLayout jPanelBottomLayout = new java.awt.GridBagLayout();
        jPanelBottomLayout.columnWidths = new int[]{0, 6, 0};
        jPanelBottomLayout.rowHeights = new int[]{0};
        jPanelBottom.setLayout(jPanelBottomLayout);

        jScrollPaneText.setViewportView(jTextPaneText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        jPanelBottom.add(jScrollPaneText, gridBagConstraints);

        jButtonSendTelegram.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        jPanelBottom.add(jButtonSendTelegram, gridBagConstraints);


        jcheckIsEnscript.setSelected(true);
        jcheckIsEnscript.setText(Lang.getInstance().translate("Encrypt message"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;

        jPanelBottom.add(jcheckIsEnscript, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 11);
        add(jPanelBottom, gridBagConstraints);
    }// </editor-fold>


    private void initMenu() {
        // menu

        menu = new JPopupMenu();

        menu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
                // TODO Auto-generated method stub
                int row1 = jTableMessages.getSelectedRow();
                if (row1 < 0) {
                    return;
                }
                row = jTableMessages.convertRowIndexToModel(row1);
            }
        });

        JMenuItem copyText = new JMenuItem(Lang.getInstance().translate("Copy Text"));
        copyText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                RSend rSend = (RSend) walletTelegramsFilterTableModel.getItem(row);
                byte[] dataMess;
                String message;

                if (rSend.isEncrypted()) {

                    if (checkWalletUnlock(null)) {
                        return;
                    }
                    dataMess = Controller.getInstance().decrypt(rSend.getCreator(), rSend.getRecipient(), rSend.getData());

                } else {
                    dataMess = rSend.getData();
                }

                if (dataMess != null) {
                    if (rSend.isText()) {
                        try {
                            message = new String(dataMess, "UTF-8");
                        } catch (UnsupportedEncodingException e1) {
                            message = "error UTF-8";
                            JOptionPane.showMessageDialog(new JFrame(),
                                    Lang.getInstance().translate(message),
                                    Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        message = Base58.encode(dataMess);
                    }

                } else {
                    message = "decode error";
                    JOptionPane.showMessageDialog(new JFrame(),
                            Lang.getInstance().translate(message),
                            Lang.getInstance().translate("Error"), JOptionPane.ERROR_MESSAGE);

                }

                StringSelection stringSelection = new StringSelection(message);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                JOptionPane.showMessageDialog(new JFrame(),
                        Lang.getInstance().translate("Text has been copy to buffer") + "!",
                        Lang.getInstance().translate("Success"), JOptionPane.INFORMATION_MESSAGE);

            }
        });
        menu.add(copyText);

        menu.addSeparator();

        JMenuItem deleteTelegram = new JMenuItem(Lang.getInstance().translate("Delete Telegram"));
        deleteTelegram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Tuple3<Long, Long, Transaction> tt = (Tuple3<Long, Long, Transaction>) walletTelegramsFilterTableModel.getValueAt(row, 0);
                Controller.getInstance().getWallet().database.getTelegramsMap().delete(tt.c.viewSignature());
                //     System.out.println(row);
            }
        });
        menu.add(deleteTelegram);


    }

    protected void tableMouse2Click(int row, Transaction transaction) {

        RSend rSend = (RSend) transaction;
        if (rSend.isEncrypted()) {

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

            byte[] dataMess = Controller.getInstance().decrypt(rSend.getCreator(), rSend.getRecipient(), rSend.getData());

            String message;

            if (dataMess != null) {
                if (rSend.isText()) {
                    try {
                        message = new String(dataMess, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        message = "error UTF-8";
                    }
                } else {
                    message = Base58.encode(dataMess);
                }
            } else {
                message = "decode error";
            }

            walletTelegramsFilterTableModel.setValueAt(message, row, walletTelegramsFilterTableModel.COLUMN_MESSAGE);
            //walletTelegramsFilterTableModel.fireTableCellUpdated(row, walletTelegramsFilterTableModel.COLUMN_MESSAGE);
            walletTelegramsFilterTableModel.fireTableDataChanged();
        }

    }

    // Variables declaration - do not modify
    public javax.swing.JButton jButtonSendTelegram;
    public javax.swing.JLabel jLabelCenter;
    public javax.swing.JLabel jLabelLeft;
    public javax.swing.JLabel jLabelRaght;
    public javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JScrollPane jScrollPaneCenter;
    private javax.swing.JScrollPane jScrollPaneText;
    public MTable jTableMessages;
    public javax.swing.JTextPane jTextPaneText;
    public JCheckBox jcheckIsEnscript;
    // End of variables declaration
}
