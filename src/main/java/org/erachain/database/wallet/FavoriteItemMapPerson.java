package org.erachain.database.wallet;

import org.erachain.core.BlockChain;
import org.mapdb.DB;
import org.erachain.utils.ObserverMessage;
import org.mapdb.Fun;

public class FavoriteItemMapPerson extends FavoriteItemMap {

    // favorites init SET
    public FavoriteItemMapPerson(DWSet dWSet, DB database) {
        super(dWSet, database, ObserverMessage.LIST_PERSON_FAVORITES_TYPE, "person", 0);

        for (Fun.Tuple3<Long, Long, byte[]> nova: BlockChain.NOVA_PERSONS.values()) {
            add(nova.a);
        }

    }
}
