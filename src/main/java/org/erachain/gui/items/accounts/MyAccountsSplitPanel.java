package org.erachain.gui.items.accounts;

import org.erachain.controller.Controller;
import org.erachain.core.account.Account;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.gui.SplitPanel;
import org.erachain.lang.Lang;
import org.erachain.settings.Settings;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;


public class MyAccountsSplitPanel extends SplitPanel  {

    /**
     * 
     */
    private static String iconFile = Settings.getInstance().getPatnIcons()+ "MyAccountsSplitPanel.png"; //MyAccountsSplitPanel.png";
    private static final long serialVersionUID = 1L;
    public AccountsPanel accountPanel;
    public AssetCls assetSelect;
    private Account selecArg;
    private AccountsRightPanel rightPanel;

    public MyAccountsSplitPanel() {
        super("MyAccountsSplitPanel");

        this.jScrollPanelLeftPanel.setVisible(false);
        this.searchToolBar_LeftPanel.setVisible(false);
        this.toolBarLeftPanel.setVisible(false);
        this.setName(Lang.getInstance().translate("My Accounts"));
        this.jToolBarRightPanel.setVisible(false);

        GridBagConstraints PanelGBC = new GridBagConstraints();
        PanelGBC.fill = GridBagConstraints.BOTH;
        PanelGBC.anchor = GridBagConstraints.NORTHWEST;
        PanelGBC.weightx = 1;
        PanelGBC.weighty = 1;
        PanelGBC.gridx = 0;
        PanelGBC.gridy = 0;
        accountPanel = new AccountsPanel();
        this.leftPanel.add(accountPanel, PanelGBC);
        // EVENTS on CURSOR
        accountPanel.table.getSelectionModel().addListSelectionListener(new Account_Tab_Listener());
        rightPanel = new AccountsRightPanel();

     //   this.repaint();

    }

    @Override
    public void onClose() {
        rightPanel.table_Model.deleteObservers();
        accountPanel.tableModel.deleteObservers();
        Controller.getInstance().deleteObserver(accountPanel.reload_Button);
        Controller.getInstance().deleteObserver(accountPanel.newAccount_Button);
    }

    class Account_Tab_Listener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent arg0) {

            AssetCls asset = (AssetCls) accountPanel.cbxFavorites.getSelectedItem();
            Account account = null;
            if (accountPanel.table.getSelectedRow() >= 0)
                account = accountPanel.tableModel.getItem(accountPanel.table.convertRowIndexToModel(accountPanel.table.getSelectedRow()));

            if (account != null && account.equals(selecArg) && asset != null && asset.equals(assetSelect)) return;
            selecArg = account;
            assetSelect = asset;
            rightPanel.table_Model.setAccount(account);
            rightPanel.table_Model.fireTableDataChanged();
            rightPanel.setAsset(asset);
            jScrollPaneJPanelRightPanel.setViewportView(rightPanel);
            
        }

    }

    public static  Image getIcon() {
        {
            try {
                return Toolkit.getDefaultToolkit().getImage(iconFile);
            } catch (Exception e) {
                return null;
            }
        }
    }


}
