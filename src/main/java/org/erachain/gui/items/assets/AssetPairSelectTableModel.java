package org.erachain.gui.items.assets;

// 30/03

import org.erachain.controller.Controller;
import org.erachain.core.blockexplorer.BlockExplorer;
import org.erachain.core.item.ItemCls;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemAssetMap;
import org.erachain.lang.Lang;
import org.apache.log4j.Logger;
import org.mapdb.Fun.Tuple6;
import org.erachain.utils.NumberAsString;
import org.erachain.utils.ObserverMessage;

import javax.swing.table.AbstractTableModel;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class AssetPairSelectTableModel extends AbstractTableModel implements Observer {
    public static final int COLUMN_KEY = 0;
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_ORDERS_COUNT = 2;
    public static final int COLUMN_ORDERS_VOLUME = 3;
    public static final int COLUMN_TRADES_COUNT = 4;
    public static final int COLUMN_TRADES_VOLUME = 5;

    private static final Logger LOGGER = Logger.getLogger(AssetPairSelectTableModel.class);

    public long key;
    public List<ItemCls> assets;
    Map<Long, Tuple6<Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> all;
    private String[] columnNames = {Lang.getInstance().translate("Key"), Lang.getInstance().translate("Name"),
            Lang.getInstance().translate("<html>Orders<br>count</html>"), Lang.getInstance().translate("Orders Volume"),
            Lang.getInstance().translate("<html>Trades<br>count</html>"),
            Lang.getInstance().translate("Trades Volume")};
    private Boolean[] column_AutuHeight = new Boolean[]{false, true, false, false, false, false};
    private String filter_Name;
    private ItemAssetMap db;

    public AssetPairSelectTableModel(long key)// , String action)
    {
        this.key = key;
        db = DCSet.getInstance().getItemAssetMap();
        // Controller.getInstance().addObserver(this);
        /*
         * Collection<ItemCls> assetsBuf =
         * Controller.getInstance().getAllItems(ItemCls.ASSET_TYPE); this.assets
         * = new ArrayList<ItemCls>();
         *
         * for (ItemCls item : assetsBuf) { AssetCls asset = (AssetCls) item;
         * if(asset.getKey() != this.key) { /* if (action =="Buy"){ if
         * (Controller.getInstance().isAddressIsMine(asset.getCreator().
         * getAddress())) assets.add(asset); }else
         */
        /*
         * assets.add(asset); } }
         */
        this.all = BlockExplorer.getInstance().calcForAsset(DCSet.getInstance().getOrderMap().getOrders(this.key),
                DCSet.getInstance().getTradeMap().getTrades(this.key));

    }

    // читаем колонки которые изменяем высоту
    public Boolean[] get_Column_AutoHeight() {

        return this.column_AutuHeight;
    }

    // устанавливаем колонки которым изменить высоту
    public void set_get_Column_AutoHeight(Boolean[] arg0) {
        this.column_AutuHeight = arg0;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Class<? extends Object> getColumnClass(int c) { // set column type

        Object o = getValueAt(0, c);
        return o == null ? Null.class : o.getClass();
    }

    public AssetCls getAsset(int row) {

        return (AssetCls) this.assets.get(row);
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public int getRowCount() {
        if (assets == null)
            return 0;
        return this.assets.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (this.assets == null || row > this.assets.size() - 1) {
            return null;
        }

        long key = this.assets.get(row).getKey();
        Tuple6<Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal> item = this.all.get(key);

        try {


            switch (column) {
                case COLUMN_KEY:

                    return key;

                case COLUMN_NAME:

                    return this.assets.get(row).viewName();

                case COLUMN_ORDERS_COUNT:


                    return item == null ? "" : item.a;

                case COLUMN_ORDERS_VOLUME:


                    return item == null ? "" : ("<html>" + (item.c == null ? "0" : NumberAsString.formatAsString(item.c)))
                            + " " + this.assets.get(row).getShort() + "&hArr;  "//"<br>"
                            + NumberAsString.formatAsString(item.d)
                            + " " + Controller.getInstance().getAsset(this.key).getShort()
                            + "</html>";

                case COLUMN_TRADES_COUNT:

                    if (item == null) return "";
                    if (item.b > 0)
                        return item.b;
                    else
                        return "";

                case COLUMN_TRADES_VOLUME:

                    if (item == null) return "";
                    if (item.b > 0)
                        return "<html>" + NumberAsString.formatAsString(item.e)
                                + " " + this.assets.get(row).getShort() + "&hArr; " //"<br>"
                                + NumberAsString.formatAsString(item.f)
                                + " " + Controller.getInstance().getAsset(this.key).getShort()
                                + "</html>";
                    else
                        return "";


            }

        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            this.syncUpdate(o, arg);
        } catch (Exception e) {
            // GUI ERROR
        }
    }

    public synchronized void syncUpdate(Observable o, Object arg) {
        ObserverMessage message = (ObserverMessage) arg;

        // CHECK IF LIST UPDATED
        if ((message.getType() == ObserverMessage.NETWORK_STATUS && (int) message.getValue() == Controller.STATUS_OK)
                || (Controller.getInstance().getStatus() == Controller.STATUS_OK
                && (message.getType() == ObserverMessage.ADD_BALANCE_TYPE
                || message.getType() == ObserverMessage.REMOVE_BALANCE_TYPE))) {
            // this.fireTableDataChanged();
        }
    }

    public void removeObservers() {
        // this.balances.removeObserver();
        Controller.getInstance().deleteObserver(this);
    }

    public void Find_item_from_key(String text) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        if (text.equals("") || text == null)
            return;
        if (!text.matches("[0-9]*"))
            return;
        Long key_filter = new Long(text);
        assets = new ArrayList<ItemCls>();
        AssetCls asset = Controller.getInstance().getAsset(key_filter);
        if (asset == null || asset.getKey() == this.key)
            return;
        assets.add(asset);
        this.fireTableDataChanged();
    }

    public void set_Filter_By_Name(String str) {
        filter_Name = str;
        assets = db.get_By_Name(filter_Name, false);
        this.fireTableDataChanged();

    }

    public void clear() {
        assets = new ArrayList<ItemCls>();
        this.fireTableDataChanged();

    }
}