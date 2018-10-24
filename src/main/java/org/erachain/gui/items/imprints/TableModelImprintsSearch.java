package org.erachain.gui.items.imprints;

import org.erachain.core.item.ItemCls;
import org.erachain.core.item.imprints.ImprintCls;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemImprintMap;
import org.erachain.gui.items.TableModelItems;
import org.erachain.lang.Lang;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableModelImprintsSearch extends TableModelItems {

    public static final int COLUMN_KEY = 0;
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_BORN = 2;
    public static final int COLUMN_PUBLISHER = 3;
    public static final int COLUMN_FAVORITE = 4;
    static Logger LOGGER = LoggerFactory.getLogger(TableModelImprintsSearch.class.getName());
    private String[] columnNames = Lang.getInstance()
            .translate(new String[]{"Key", "Name", "Birthday", "Publisher", "Favorite"});// ,
    // "Favorite"});
    private Boolean[] column_AutuHeight = new Boolean[]{false, true, true, false};
    private ItemImprintMap db;
    private List<ItemCls> list;
    private String filter_Name = "";
    private long key_filter = 0;

    public TableModelImprintsSearch() {
        super.COLUMN_FAVORITE = COLUMN_FAVORITE;
        db = DCSet.getInstance().getItemImprintMap();
    }

    public void set_Filter_By_Name(String str) {
        filter_Name = str;
        list = db.get_By_Name(filter_Name, false);
        this.fireTableDataChanged();

    }

    public void clear() {
        list = new ArrayList<ItemCls>();
        this.fireTableDataChanged();

    }

    public Class<? extends Object> getColumnClass(int c) { // set column type
        Object o = getValueAt(0, c);
        return o == null ? Null.class : o.getClass();
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
    public ItemCls getItem(int row) {
        return this.list.get(row);
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int index) {
        return this.columnNames[index];
    }

    @Override
    public int getRowCount() {
        if (this.list == null)
            return 0;
        ;
        return this.list.size();

    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null || row > this.list.size() - 1) {
            return null;
        }

        ImprintCls person = (ImprintCls) list.get(row);

        switch (column) {
            case COLUMN_KEY:

                return person.getKey();

            case COLUMN_NAME:

                return person.getName();

            case COLUMN_PUBLISHER:

                return person.getOwner().getPersonAsString();

            case COLUMN_FAVORITE:

                return person.isFavorite();

            //	case COLUMN_BORN:

            // DateFormat f = new DateFormat("DD-MM-YYYY");
            // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
            // return dateFormat.format( new Date(person.getBirthday()));
            //		return person.getBirthdayStr();

        }

        return null;
    }


    public void Find_item_from_key(String text) {
        // TODO Auto-generated method stub
        if (text.equals("") || text == null) return;
        if (!text.matches("[0-9]*")) return;
        key_filter = new Long(text);
        list = new ArrayList<ItemCls>();
        ImprintCls pers = (ImprintCls) db.get(key_filter);
        if (pers == null) return;
        list.add(pers);
        try {
            this.fireTableDataChanged();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("fireTableDataChanged ?");
        }


    }
}
