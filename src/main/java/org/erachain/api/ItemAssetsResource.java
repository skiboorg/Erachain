package org.erachain.api;

import lombok.extern.slf4j.Slf4j;
import org.erachain.controller.Controller;
import org.erachain.core.account.Account;
import org.erachain.core.blockexplorer.BlockExplorer;
import org.erachain.core.crypto.Base58;
import org.erachain.core.crypto.Crypto;
import org.erachain.core.transaction.Transaction;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemAssetBalanceMap;
import org.erachain.dbs.IteratorCloseable;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.mapdb.Fun;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;

@Path("assets")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ItemAssetsResource {
    /**
     * Get all asset type 1
     *
     * @return ArrayJSON of all asset. request key means key asset and name asset.
     * <h2>Example request</h2>
     * GET assets
     * <h2>Example response</h2>
     * {
     * "1": "ERA",
     * "2": "COMPU",
     * "3": "АЗЫ",
     * "4": "ВЕДЫ",
     * "5": "►РА",
     * "6": "►RUNEURO",
     * "7": "►ERG",
     * "8": "►LERG",
     * "9": "►A"
     * }
     */
    @GET
    public String getAssetsLite() {
        return JSONValue.toJSONString(BlockExplorer.getInstance().jsonQueryAssetsLite());
    }

    /**
     * Get lite information asset by key asset
     * @param key is number asset
     * @return JSON object. Single asset
     */
    @GET
    @Path("/{key}")
    public String getAssetLite(@PathParam("key") String key) {
        Long assetAsLong = null;

        // HAS ASSET NUMBERFORMAT
        try {
            assetAsLong = Long.valueOf(key);

        } catch (NumberFormatException e) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);

        }

        // DOES ASSETID EXIST
        if (!DCSet.getInstance().getItemAssetMap().contains(assetAsLong)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);

        }

        return Controller.getInstance().getAsset(assetAsLong).toJson().toJSONString();
    }

    @GET
    @Path("/{key}/full")
    public String getAsset(@PathParam("key") String key) {
        Long assetAsLong = null;

        // HAS ASSET NUMBERFORMAT
        try {
            assetAsLong = Long.valueOf(key);

        } catch (NumberFormatException e) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);

        }

        // DOES ASSETID EXIST
        if (!DCSet.getInstance().getItemAssetMap().contains(assetAsLong)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        return JSONValue.toJSONString(BlockExplorer.getInstance().jsonQueryItemAsset(assetAsLong));
    }

    /**
     * Sorted Array
     *
     * @param assetKey
     * @param offset
     * @param position
     * @param limit
     * @return
     */
    @GET
    @Path("balances/{key}")
    public static String getBalances(@PathParam("key") Long assetKey, @DefaultValue("0") @QueryParam("offset") Integer offset,
                                     @DefaultValue("1") @QueryParam("position") Integer position,
                                     @DefaultValue("50") @QueryParam("limit") Integer limit) {

        ItemAssetBalanceMap map = DCSet.getInstance().getAssetBalanceMap();
        byte[] key;
        Crypto crypto = Crypto.getInstance();
        Fun.Tuple2<BigDecimal, BigDecimal> balance;

        JSONArray out = new JSONArray();
        int counter = limit;
        try (IteratorCloseable<byte[]> iterator = map.getIteratorByAsset(assetKey)) {
            while (iterator.hasNext()) {
                key = iterator.next();
                if (offset > 0) {
                    offset--;
                    continue;
                }

                try {
                    balance = Account.getBalanceInPosition(map.get(key), position);

                    // пустые не берем
                    if (balance.a.signum() == 0 && balance.b.signum() == 0)
                        continue;

                    JSONArray bal = new JSONArray();
                    bal.add(crypto.getAddressFromShort(ItemAssetBalanceMap.getShortAccountFromKey(key)));
                    bal.add(balance.a.toPlainString());
                    bal.add(balance.b.toPlainString());
                    out.add(bal);

                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    logger.error("Wrong key raw: " + Base58.encode(key));
                }

                if (limit > 0 && --counter <= 0) {
                    break;
                }

            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return out.toJSONString();
    }

}