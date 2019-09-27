package org.erachain.datachain;

import org.erachain.core.account.PublicKeyAccount;
import org.erachain.core.block.Block;
import org.erachain.dbs.DBTab;

public interface BlockMap extends DBTab<Integer, Block> {

    int HEIGHT_INDEX = 1; // for GUI

    Block last();

    byte[] getLastBlockSignature();

    void resetLastBlockSignature();

    boolean isProcessing();

    void setProcessing(boolean processing);

    Block getWithMind(int height);

    Block get(Integer height);

    boolean add(Block block);

    // TODO make CHAIN deletes - only for LAST block!
    Block remove(byte[] signature, byte[] reference, PublicKeyAccount creator);

    void notifyResetChain();

    void notifyProcessChain(Block block);

    void notifyOrphanChain(Block block);
}
