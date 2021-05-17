package org.erachain.database.wallet;

import org.erachain.core.BlockChain;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.utils.ObserverMessage;
import org.mapdb.DB;

public class FavoriteItemMapAsset extends FavoriteItemMap {

    // favorites init SET
    public FavoriteItemMapAsset(DWSet dWSet, DB database) {
        super(dWSet, database, ObserverMessage.LIST_ASSET_FAVORITES_TYPE, "asset", AssetCls.INITIAL_FAVORITES);

        if (BlockChain.TEST_MODE) {
            add(1077L);
            add(1078L);
            add(1079L);

        } else {
            add(3003L);
            add(AssetCls.USD_KEY);
        }
    }

}
