package org.erachain.core.transaction;

import org.erachain.core.BlockChain;
import org.erachain.core.account.Account;
import org.erachain.core.block.Block;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.core.item.assets.AssetFactory;

import java.math.BigDecimal;
import java.util.Arrays;

// core.block.Block.isValid(DLSet) - check as false it
public class GenesisIssueAssetTransaction extends GenesisIssueItemRecord {

    private static final byte TYPE_ID = (byte) GENESIS_ISSUE_ASSET_TRANSACTION;
    private static final String NAME_ID = "GENESIS Issue Asset";
    private boolean involvedInWallet;

    public GenesisIssueAssetTransaction(AssetCls asset) {
        super(TYPE_ID, NAME_ID, asset);

    }

    //GETTERS/SETTERS

    //PARSE CONVERT
    public static Transaction Parse(byte[] data) throws Exception {
        //CHECK IF WE MATCH BLOCK LENGTH
        if (data.length < BASE_LENGTH) {
            throw new Exception("Data does not match block length");
        }

        // READ TYPE
        int position = SIMPLE_TYPE_LENGTH;

        //READ ASSET
        // read without reference
        AssetCls asset = AssetFactory.getInstance().parse(Transaction.FOR_NETWORK, Arrays.copyOfRange(data, position, data.length), false);
        //position += asset.getDataLength(false);

        return new GenesisIssueAssetTransaction(asset);
    }

    @Override
    public boolean isInvolved(Account account) {
        if (!this.involvedInWallet) {
            // only one record to wallet for all accounts
            this.involvedInWallet = true;
            return true;
        }

        return false;

    }

    public void processBody(Block block, int forDeal) {

        if (false && BlockChain.CLONE_MODE && dcSet.getItemAssetMap().size() > 1
        ) {
            // skip invalid Maker assets
            return;
        }

        super.processBody(block, forDeal);

        AssetCls asset = (AssetCls) item;
        long quantity = asset.getQuantity();
        if (quantity > 0L) {
            Account maker = item.getMaker();
            Long assetKey = item.getKey();
            // надо добавить баланс на счет
            maker.changeBalance(dcSet, false, false, assetKey,
                    new BigDecimal(quantity).setScale(0), false, false, false);

            // make HOLD balance
            maker.changeBalance(dcSet, false, true, assetKey,
                    new BigDecimal(-quantity).setScale(0), false, false, false);
        }
    }

}
