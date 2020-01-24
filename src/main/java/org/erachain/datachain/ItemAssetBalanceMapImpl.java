package org.erachain.datachain;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;
import org.erachain.controller.Controller;
import org.erachain.core.account.Account;
import org.erachain.database.SortableList;
import org.erachain.dbs.DBTab;
import org.erachain.dbs.DBTabImpl;
import org.erachain.dbs.IteratorCloseable;
import org.erachain.dbs.mapDB.ItemAssetBalanceSuitMapDB;
import org.erachain.dbs.mapDB.ItemAssetBalanceSuitMapDBFork;
import org.erachain.dbs.nativeMemMap.NativeMapTreeMapFork;
import org.erachain.dbs.rocksDB.ItemAssetBalanceSuitRocksDB;
import org.erachain.utils.ObserverMessage;
import org.mapdb.DB;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple5;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.erachain.database.IDB.DBS_MAP_DB;
import static org.erachain.database.IDB.DBS_ROCK_DB;

/**
 * Балансы для заданного адреса на данный актив. balances for all account in blockchain<br>
 * <b>Список балансов:</b> имущество, займы, хранение, производство, резерв<br>
 * Каждый баланс: Всего Пришло и Остаток<br><br>
 *
 * <b>Ключ:</b> account.address + asset key<br>
 *
 * <b>Значение:</b> Балансы. in_OWN, in_RENT, on_HOLD = in_USE (TOTAL on HAND)
 *
 */
// TODO SOFT HARD TRUE
@Slf4j
public class ItemAssetBalanceMapImpl extends DBTabImpl<byte[], Tuple5<
        Tuple2<BigDecimal, BigDecimal>, // in OWN - total INCOMED + BALANCE
        Tuple2<BigDecimal, BigDecimal>, // in DEBT
        Tuple2<BigDecimal, BigDecimal>, // in STOCK
        Tuple2<BigDecimal, BigDecimal>, // it DO
        Tuple2<BigDecimal, BigDecimal>  // on HOLD
        >> implements ItemAssetBalanceMap {

    static final boolean SIZE_ENABLE = false;

    public ItemAssetBalanceMapImpl(int dbsUsed, DCSet databaseSet, DB database) {
        super(dbsUsed, databaseSet, database);

        if (databaseSet.isWithObserver()) {
            this.observableData.put(DBTab.NOTIFY_RESET, ObserverMessage.RESET_BALANCE_TYPE);
            this.observableData.put(DBTab.NOTIFY_LIST, ObserverMessage.LIST_BALANCE_TYPE);
            this.observableData.put(DBTab.NOTIFY_ADD, ObserverMessage.ADD_BALANCE_TYPE);
            this.observableData.put(DBTab.NOTIFY_REMOVE, ObserverMessage.REMOVE_BALANCE_TYPE);
        }
    }

    public ItemAssetBalanceMapImpl(int dbsUsed, ItemAssetBalanceMap parent, DCSet databaseSet) {
        super(dbsUsed, parent, databaseSet);
    }

    // TODO вставить настройки выбора СУБД
    @Override
    public void openMap()
    {


        if (parent == null) {
            switch (dbsUsed) {
                case DBS_ROCK_DB:
                    map = new ItemAssetBalanceSuitRocksDB(databaseSet, database, this);
                    break;
                default:
                    map = new ItemAssetBalanceSuitMapDB(databaseSet, database, this);
            }
        } else {
            switch (dbsUsed) {
                case DBS_MAP_DB:
                    map = new ItemAssetBalanceSuitMapDBFork((ItemAssetBalanceMap) parent, databaseSet, this);
                    break;
                case DBS_ROCK_DB:
                    map = new ItemAssetBalanceSuitRocksDB(databaseSet, database, this);
                    break;
                default:
                    map = new NativeMapTreeMapFork(parent, databaseSet, Fun.BYTE_ARRAY_COMPARATOR, this);
            }
        }
    }

    @Override
    public Fun.Tuple5<
            Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>,
            Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>> getDefaultValue() {
        return new Fun.Tuple5<
                Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>,
                Fun.Tuple2<BigDecimal, BigDecimal>, Fun.Tuple2<BigDecimal, BigDecimal>>
                (new Fun.Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO),
                        new Fun.Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO),
                        new Fun.Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO),
                        new Fun.Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO),
                        new Fun.Tuple2<BigDecimal, BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    public boolean contains(byte[] address, long key) {
        if (key < 0)
            key = -key;

        return this.contains(Bytes.concat(address, Longs.toByteArray(key)));
    }

    public void put(byte[] address, long key, Tuple5<
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> value) {
        if (key < 0)
            key = -key;

        this.put(Bytes.concat(address, Longs.toByteArray(key)), value);
    }

    public Tuple5<
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> get(byte[] address, long key) {
        if (key < 0)
            key = -key;


        Tuple5<
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> value = this.get(
                        Bytes.concat(address, Longs.toByteArray(key)));

        return value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SortableList<byte[], Tuple5<
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getBalancesSortableList(long assetKey) {

        if (Controller.getInstance().onlyProtocolIndexing)
            return null;

        if (assetKey < 0)
            assetKey = -assetKey;

        Collection<byte[]> keys;
        if (map instanceof ItemAssetBalanceSuitRocksDB) {
            //FILTER ALL KEYS
            keys = new ArrayList<>();
            try (IteratorCloseable<byte[]> iterator = ((ItemAssetBalanceSuit) map).assetIterator(assetKey)) {
                while (iterator.hasNext()) {
                    keys.add(iterator.next());
                }
            } catch (IOException e) {
            }
        } else {
            keys = ((ItemAssetBalanceSuit)map).assetKeys(assetKey);
        }

        //RETURN
        return new SortableList<byte[], Tuple5<
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>(this, keys);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SortableList<byte[], Tuple5<
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
            Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>> getBalancesSortableList(Account account) {

        if (Controller.getInstance().onlyProtocolIndexing)
            return null;

        Collection<byte[]> keys;
        if (map instanceof ItemAssetBalanceSuitRocksDB) {
            //FILTER ALL KEYS
            keys = new ArrayList<>();
            try (IteratorCloseable<byte[]> iterator = ((ItemAssetBalanceSuit) map).accountIterator(account)) {
                while (iterator.hasNext()) {
                    keys.add(iterator.next());
                }
            } catch (IOException e) {
            }
        } else {
            keys = ((ItemAssetBalanceSuit)map).accountKeys(account);
        }

        //RETURN
        return new SortableList<byte[], Tuple5<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>(this, keys);
    }

    public IteratorCloseable<byte[]> getIteratorByAccount(Account account) {

        if (Controller.getInstance().onlyProtocolIndexing)
            return null;

        return ((ItemAssetBalanceSuit) map).accountIterator(account);

    }

    public IteratorCloseable<byte[]> getIteratorByAsset(long assetKey) {

        if (Controller.getInstance().onlyProtocolIndexing)
            return null;

        if (assetKey < 0)
            assetKey = -assetKey;

        return ((ItemAssetBalanceSuit) map).assetIterator(assetKey);

    }

}