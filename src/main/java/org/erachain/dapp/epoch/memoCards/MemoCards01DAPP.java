package org.erachain.dapp.epoch.memoCards;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.erachain.core.BlockChain;
import org.erachain.core.account.Account;
import org.erachain.core.account.PublicKeyAccount;
import org.erachain.core.block.Block;
import org.erachain.core.exdata.exLink.ExLinkAddress;
import org.erachain.core.item.ItemCls;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.core.item.assets.AssetVenture;
import org.erachain.core.transaction.RSend;
import org.erachain.core.transaction.Transaction;
import org.erachain.dapp.DAPPFactory;
import org.erachain.dapp.EpochDAPPjson;
import org.erachain.dapp.epoch.shibaverse.server.Farm_01;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemAssetBalanceMap;
import org.erachain.datachain.SmartContractValues;
import org.erachain.dbs.IteratorCloseable;
import org.erachain.webserver.WebResource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class MemoCards01DAPP extends EpochDAPPjson {

    int WAIT_RAND = 3;

    static public final int ID = 10021;
    static public final String NAME = "Memo Cards 01";

    final public static HashSet<PublicKeyAccount> accounts = new HashSet<>();

    // APPBQyonEPbk2ZazbUuHZ2ffN1QJYaK1ow
    final public static PublicKeyAccount MAKER = PublicKeyAccount.makeForDApp(crypto.digest(Longs.toByteArray(ID)));

    static {
        accounts.add(MAKER);
    }

    /**
     * admin account
     */
    final static public Account adminAddress = new Account("7NhZBb8Ce1H2S2MkPerrMnKLZNf9ryNYtP");

    final static public String COMMAND_WITHDRAW = "withdraw";
    final static public long BUSTER_1_KEY = BlockChain.DEMO_MODE ? 1050917L : 9999L;

    /**
     * make random from future
     */
    final static public String COMMAND_RANDOM = "random";

    public static final int RARE_COMMON = 0;
    public static final int RARE_UNCOMMON = 1;
    public static final int RARE_RARE = 2;
    public static final int RARE_EPIC = 3;

    public MemoCards01DAPP(String data, String status) {
        super(ID, MAKER, data, status);
    }

    public String getName() {
        return NAME;
    }

    public static MemoCards01DAPP make(RSend txSend, String dataStr) {
        // dataStr = null
        if (dataStr == null || dataStr.isEmpty())
            return null;

        return new MemoCards01DAPP(dataStr, "");

    }

    /// PARSE / TOBYTES

    public static MemoCards01DAPP Parse(byte[] bytes, int pos, int forDeal) {

        // skip ID
        pos += 4;

        String data;
        String status;
        if (forDeal == Transaction.FOR_DB_RECORD) {
            byte[] dataSizeBytes = Arrays.copyOfRange(bytes, pos, pos + 4);
            int dataSize = Ints.fromByteArray(dataSizeBytes);
            pos += 4;
            byte[] dataBytes = Arrays.copyOfRange(bytes, pos, pos + dataSize);
            pos += dataSize;
            data = new String(dataBytes, StandardCharsets.UTF_8);

            byte[] statusSizeBytes = Arrays.copyOfRange(bytes, pos, pos + 4);
            int statusLen = Ints.fromByteArray(statusSizeBytes);
            pos += 4;
            byte[] statusBytes = Arrays.copyOfRange(bytes, pos, pos + statusLen);
            pos += statusLen;
            status = new String(statusBytes, StandardCharsets.UTF_8);

        } else {
            data = "";
            status = "";
        }

        return new MemoCards01DAPP(data, status);
    }

    ///////// COMMANDS

    /**
     * @param block
     * @param transaction
     * @param nonce
     * @return
     */
    public static byte[] getRandHash(Block block, Transaction transaction, int nonce) {

        byte[] hash = new byte[32];
        System.arraycopy(block.getSignature(), 0, hash, 0, 14);
        System.arraycopy(Ints.toByteArray(nonce), 0, hash, 14, 4);
        System.arraycopy(transaction.getSignature(), 0, hash, 18, 14);

        return crypto.digest(hash);

    }

    private int openBuster_1_getSetCount(int setID, int rareLevel) {
        switch (setID) {
            case 1:
                switch (rareLevel) {
                    case RARE_COMMON:
                        return 10;
                    case RARE_UNCOMMON:
                        return 3;
                    case RARE_RARE:
                        return 1;
                    case RARE_EPIC:
                        return 0;
                }
        }
        return 256;
    }

    /**
     * @param dcSet
     * @param block
     * @param commandTX
     * @param setID     ID of set
     * @param rareLevel level of card rarity
     * @param charValue characterictic value
     */
    private Object[] makeAsset(DCSet dcSet, Block block, RSend commandTX, int setID, int rareLevel, int charValue) {
        int setCount = openBuster_1_getSetCount(setID, rareLevel);
        charValue = setCount * (2 * Short.MAX_VALUE - 1) / charValue;

        String name = "ca" + setID + "." + rareLevel + "." + charValue;
        Tuple2 keyID = new Tuple2(ID, name);

        Long assetKey;
        SmartContractValues valuesMap = dcSet.getSmartContractValues();

        Object[] issuedAsset = new Object[2];

        // seek if already exist
        if (valuesMap.contains(keyID)) {
            assetKey = (Long) valuesMap.get(keyID);
            issuedAsset[1] = false;

        } else {
            // make new COMET

            // for orphan action
            issuedAsset[1] = true;

            JSONObject json = new JSONObject();
            json.put("value", charValue);
            json.put("rare", rareLevel);
            json.put("set", setID);
            json.put("type", "card");
            String description = json.toJSONString();

            boolean iconAsURL = false;
            int iconType = 0;
            boolean imageAsURL = false;
            int imageType = 0;
            Long startDate = null;
            Long stopDate = null;
            String tags = "mtga, :nft, prolog set";
            ExLinkAddress[] dexAwards = null;
            boolean isUnTransferable = false;
            boolean isAnonimDenied = false;

            AssetVenture randomAsset = new AssetVenture(AssetCls.makeAppData(
                    iconAsURL, iconType, imageAsURL, imageType, startDate, stopDate, tags, dexAwards, isUnTransferable, isAnonimDenied),
                    stock, name, null, null,
                    description, AssetCls.AS_INSIDE_ASSETS, 0, 0);
            randomAsset.setReference(commandTX.getSignature(), commandTX.getDBRef());

            //INSERT INTO BLOCKCHAIN DATABASE
            assetKey = dcSet.getItemAssetMap().incrementPut(randomAsset);
            //INSERT INTO CONTRACT DATABASE
            dcSet.getSmartContractValues().put(keyID, assetKey);

        }

        // TRANSFER ASSET
        transfer(dcSet, block, commandTX, stock, commandTX.getCreator(), BigDecimal.ONE, assetKey, false, null, "buster_1");
        issuedAsset[0] = assetKey;

        return issuedAsset;

    }

    /**
     * make pack by RARE
     *
     * @return
     */
    private void openBuster_1_getPack(DCSet dcSet, Block block, RSend commandTX, int nonce, List actions) {

        // GET RANDOM
        byte[] randomArray = getRandHash(block, commandTX, nonce);
        int index = 2;
        if (randomArray[0] < 2) {
            // make COMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));

            // make UNCOMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_UNCOMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));

        } else if (randomArray[0] < 4) {
            // make COMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));

            // make UNCOMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_UNCOMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_UNCOMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
        } else {
            // make COMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_COMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));

            // make UNCOMMON cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_UNCOMMON, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));

            // make RARE cards
            actions.add(makeAsset(dcSet, block, commandTX, 1, RARE_RARE, Ints.fromBytes((byte) 0, (byte) 0, randomArray[index++], randomArray[index++])));
        }

    }

    /**
     * @param dcSet
     * @param commandTX
     * @param asOrphan
     */
    private boolean openBuster_1(DCSet dcSet, Block block, RSend commandTX, boolean asOrphan) {
        // открываем бустер

        if (asOrphan) {

            SmartContractValues valuesMap = dcSet.getSmartContractValues();
            Object[] actions = removeState(dcSet, commandTX.getDBRef());

            int index = actions.length;
            Object[] act;
            Long assetKey;
            ItemCls asset;

            while (--index > 0) {
                act = (Object[]) actions[index];

                assetKey = (Long) act[0];
                transfer(dcSet, null, commandTX, stock, commandTX.getCreator(), BigDecimal.ONE, assetKey, true, null, null);

                if ((boolean) act[1]) {
                    // DELETE FROM BLOCKCHAIN DATABASE
                    asset = dcSet.getItemAssetMap().decrementRemove(assetKey);

                    // DELETE FROM CONTRACT DATABASE
                    valuesMap.delete(new Tuple2(ID, asset.getName()));

                }
            }

            status = "wait";

            return true;
        }

        if (!commandTX.hasAmount() || !commandTX.hasPacket() && commandTX.getAmount().signum() <= 0) {
            fail("Wrong amount. Need > 0");
            return false;
        } else if (commandTX.isBackward()) {
            fail("Wrong direction - backward");
            return false;
        } else if (commandTX.balancePosition() != Account.BALANCE_POS_OWN) {
            fail("Wrong balance position. Need OWN[1]");
            return false;
        }

        SmartContractValues valuesMap = dcSet.getSmartContractValues();
        PublicKeyAccount creator = commandTX.getCreator();
        int count = commandTX.getAmount().intValue();

        // need select direction by asOrphan, else decrementDelete will not work!
        int nonce = count;

        List actions = new ArrayList();
        do {

            nonce--;

            openBuster_1_getPack(dcSet, block, commandTX, nonce, actions);

        } while (--count > 0);

        putState(dcSet, commandTX.getDBRef(), actions.toArray());

        status = "done";

        return true;

    }

    private boolean random(DCSet dcSet, Block block, RSend commandTX, boolean asOrphan) {
        if (commandTX.getAssetKey() == BUSTER_1_KEY)
            return openBuster_1(dcSet, block, commandTX, asOrphan);
        return true;
    }



    @Override
    public boolean process(DCSet dcSet, Block block, Transaction commandTX) {

        if (commandTX instanceof RSend) {
            RSend rsend = (RSend) commandTX;

            if (COMMAND_RANDOM.equals(command)) {
                if (rsend.isBackward() || rsend.balancePosition() != Account.BALANCE_POS_OWN) {
                    fail("wrong action: " + rsend.viewActionType());
                    return false;
                } else if (block == null) {
                    fail("wait block");
                    return false;
                }

                /// WAIT RANDOM FROM FUTURE
                dcSet.getTimeTXWaitMap().put(commandTX.getDBRef(), block.heightBlock + WAIT_RAND);
                status = "wait";
                return false;

            }
        }

        fail("unknown command");
        return false;

    }

    @Override
    public boolean processByTime(DCSet dcSet, Block block, Transaction transaction) {

        if (COMMAND_RANDOM.equals(command)) {
            return random(dcSet, block, (RSend) transaction, false);
        }

        fail("unknow command");
        return false;

    }

    @Override
    public void orphan(DCSet dcSet, Transaction commandTX) {

        if (status.startsWith("fail")) {
            // not processed
            return;
        }

        if (status.startsWith("wait")) {
            /// WAIT RANDOM FROM FUTURE
            dcSet.getTimeTXWaitMap().remove(commandTX.getDBRef());

            /// COMMANDS
        } else if (COMMAND_BUY.equals(command)) {
            shopBuy(dcSet, null, (RSend) commandTX, true);

            /// ADMIN COMMANDS
        } else if ("init".equals(command)) {
            init(dcSet, null, (RSend) commandTX, true);
        } else if (COMMAND_WITHDRAW.startsWith(command)) {
            adminWithdraw(dcSet, null, (RSend) commandTX, true);
        } else if (COMMAND_SET_PRICE.equals(command)) {
            shopSetPrices(dcSet, null, (RSend) commandTX, true);
        }

    }

    @Override
    public void orphanByTime(DCSet dcSet, Block block, Transaction transaction) {
        if (COMMAND_RANDOM.equals(command)) {
            random(dcSet, block, (RSend) transaction, true);
        }

    }

    private static String[][][] imgsStr;

    {
        imgsStr = new String[][][]{
                new String[][]{
                        new String[]{"1050868", WebResource.TYPE_IMAGE.toString()},
                        new String[]{"1050867", WebResource.TYPE_IMAGE.toString()},
                },
                new String[][]{
                        new String[]{"1050864", WebResource.TYPE_IMAGE.toString()},
                        new String[]{"1050862", WebResource.TYPE_IMAGE.toString()},
                        null,
                        new String[]{"1050863", WebResource.TYPE_IMAGE.toString()},
                },
                new String[][]{
                        new String[]{"1050860", WebResource.TYPE_IMAGE.toString()},
                },
                new String[][]{
                        null,
                        new String[]{"1050866", WebResource.TYPE_IMAGE.toString()},
                },
                new String[][]{
                        new String[]{"1050857", WebResource.TYPE_IMAGE.toString()},
                        new String[]{"1050859", WebResource.TYPE_IMAGE.toString()},
                        new String[]{"1050858", WebResource.TYPE_IMAGE.toString()},
                },
                new String[][]{
                        new String[]{"1050856", WebResource.TYPE_IMAGE.toString()},
                        new String[]{"1050855", WebResource.TYPE_IMAGE.toString()},
                        null,
                        new String[]{"1050854", WebResource.TYPE_IMAGE.toString()},
                },
                null,
                new String[][]{
                        null,
                        null,
                        new String[]{"1050852", WebResource.TYPE_IMAGE.toString()},
                        null,
                        new String[]{"1050851", WebResource.TYPE_IMAGE.toString()},
                },
        };
    }

    static int confirms = 10;
    static int deploy_period = 3;

    public static String getImageURL(AssetCls asset) {

        JSONArray arrayJson = new JSONArray();
        JSONObject item;


        int height = Transaction.parseHeightDBRef(asset.getDBref());

        if (contr.getMyHeight() < height + deploy_period + confirms) {
            item = new JSONObject();
            item.put("url", "/apiasset/image/1050869");
            item.put("type", WebResource.TYPE_IMAGE.toString());
            arrayJson.add(item);
            return arrayJson.toJSONString();
        }

        Block.BlockHead blockHead = DCSet.getInstance().getBlocksHeadsMap().get(height + deploy_period);

        byte[] hash = blockHead.signature;
        byte[] hash2 = Ints.toByteArray((int) asset.getKey());
        System.arraycopy(hash2, 0, hash, 0, hash2.length);

        hash = crypto.digest(hash);
        int slot = 0;
        int slotRare;
        int slotRareLvl;

        String[][] slotArray;
        do {
            slotRare = Ints.fromBytes((byte) 0, (byte) 0, hash[slot << 1], hash[(slot << 1) + 1]);
            if ((slotRare >> 11) == 0) {
                slotRareLvl = 5;
            } else if ((slotRare >> 12) == 0) {
                slotRareLvl = 4;
            } else if ((slotRare >> 13) == 0) {
                slotRareLvl = 3;
            } else if ((slotRare >> 14) == 0) {
                slotRareLvl = 2;
            } else if ((slotRare >> 15) == 0) {
                slotRareLvl = 1;
            } else {
                slotRareLvl = 0;
            }

            slotArray = imgsStr[slot];
            if (slotArray == null)
                continue;

            if (slotArray.length <= slotRareLvl) {
                slotRareLvl = slotArray.length - 1;
            }

            String[] itemArray;
            do {
                itemArray = slotArray[slotRareLvl];
            } while (itemArray == null && slotRareLvl-- > 0);

            if (itemArray == null)
                continue;

            item = new JSONObject();
            item.put("url", "/apiasset/image/" + itemArray[0]);
            item.put("type", itemArray[1]);
            arrayJson.add(item);

        } while (slot++ < 7);

        item = new JSONObject();
        item.put("url", "/apiasset/image/1050853");
        item.put("type", WebResource.TYPE_IMAGE.toString());
        arrayJson.add(item);
        item = new JSONObject();
        item.put("url", "/apiasset/image/1050865");
        item.put("type", WebResource.TYPE_IMAGE.toString());
        arrayJson.add(item);

        return arrayJson.toJSONString();

    }

    static DecimalFormat format2 = new DecimalFormat("#.##");

    public static String viewDescription(AssetCls asset, String description) {
        int released = asset.getReleased(DCSet.getInstance()).intValue();
        double rary = Math.sqrt(1.0d / released);
        return "<html>RARY: <b>" + format2.format(rary) + "</b><br>" + description + "</html>";
    }

    public static void setDAPPFactory(HashMap<Account, Integer> stocks) {
        for (Account account : accounts) {
            stocks.put(account, ID);
        }
    }

}
