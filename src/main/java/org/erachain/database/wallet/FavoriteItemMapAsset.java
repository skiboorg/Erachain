package org.erachain.database.wallet;

import org.erachain.core.BlockChain;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.utils.ObserverMessage;
import org.mapdb.DB;
import org.mapdb.Fun;

public class FavoriteItemMapAsset extends FavoriteItemMap {

    // favorites init SET
    public FavoriteItemMapAsset(DWSet dWSet, DB database) {
        super(dWSet, database, ObserverMessage.LIST_ASSET_FAVORITES_TYPE, "asset", AssetCls.INITIAL_FAVORITES);

        for (Fun.Tuple3<Long, Long, byte[]> nova: BlockChain.NOVA_ASSETS.values()) {
            add(nova.a);
        }

    }

}
