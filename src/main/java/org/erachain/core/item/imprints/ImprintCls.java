package org.erachain.core.item.imprints;


import org.erachain.core.BlockChain;
import org.erachain.core.account.PublicKeyAccount;
import org.erachain.core.crypto.Base58;
import org.erachain.core.crypto.Crypto;
import org.erachain.core.item.ItemCls;
import org.erachain.core.transaction.Transaction;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemMap;

import java.util.Arrays;

public abstract class ImprintCls extends ItemCls {

    public static final int TYPE_KEY = ItemCls.IMPRINT_TYPE;

    protected static final int IMPRINT = 1;
    protected static final int CUTTED_REFERENCE_LENGTH = 20;

    public ImprintCls(byte[] typeBytes, byte[] appData, PublicKeyAccount maker, String name, byte[] icon, byte[] image, String description) {
        super(typeBytes, appData, maker, name, icon, image, description);

    }

    public ImprintCls(int type, byte[] appData, PublicKeyAccount maker, String name, byte[] icon, byte[] image, String description) {
        this(new byte[TYPE_LENGTH], appData, maker, name, icon, image, description);
        this.typeBytes[0] = (byte) type;
    }

    //GETTERS/SETTERS

    @Override
    public int getItemType() {
        return TYPE_KEY;
    }

    public String getItemTypeName() {
        return "imprint";
    }

    public static byte[] hashName(String name_total) {
        byte[] digest = Crypto.getInstance().digest(name_total.getBytes());
        return Arrays.copyOfRange(digest, 0, CUTTED_REFERENCE_LENGTH);
    }

    public static String hashNameToBase58(String name_total) {
        return Base58.encode(hashName(name_total));
    }

    public byte[] hashName() {
        return hashName(name);
    }

    // DB
    public ItemMap getDBMap(DCSet db) {
        return db.getItemImprintMap();
    }

}
