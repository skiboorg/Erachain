package org.erachain.webserver;

import com.google.common.collect.Iterables;
import javafx.print.Collation;
import org.erachain.api.ApiErrorFactory;
import com.google.gson.Gson;
import org.erachain.controller.Controller;
import org.erachain.core.blockexplorer.BlockExplorer;
import org.erachain.core.crypto.Base58;
import org.erachain.core.item.assets.Order;
import org.erachain.core.item.assets.Trade;
import org.erachain.core.transaction.Transaction;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.ItemAssetMap;
import org.erachain.datachain.TransactionFinalMap;
import org.erachain.utils.StrJSonFine;
import org.mapdb.Fun;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

@Path("apitrade")
@Produces(MediaType.APPLICATION_JSON)
public class APITrade {

    @Context
    HttpServletRequest request;

    private DCSet dcSet = DCSet.getInstance();
    private Controller cntrl = Controller.getInstance();

    @GET
    public Response Default() {
        Map<String, String> help = new LinkedHashMap<>();

        help.put("apitrade/get?have={have}&want={want}&timestamp={timestamp}&limit={limit}",
                "Get data by trade. Have= Want=, "
                        + "limit is count record. The number of transactions is limited by input param.");
        help.put("apitrade/orders?have={have}&want={want}&limit={limit}",
                "Get Orders.");
        help.put("apitrade/trades?have={have}&want={want}&limit={limit}",
                "Get trades.");

        return Response.status(200).header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*").entity(StrJSonFine.convert(help)).build();
    }

    @GET
    @Path("ordersByTimestamp")
    // apitrade/get?have=1&want=2&timestamp=3&limit=4
    public Response getOrdersByTimestamp(@QueryParam("have") Long have, @QueryParam("want") Long want,
                              @QueryParam("timestamp") Long timestamp, @DefaultValue("20") @QueryParam("limit") Long limit) {

        ItemAssetMap map = this.dcSet.getItemAssetMap();
        // DOES ASSETID EXIST
        if (!map.contains(have)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }
        if (!map.contains(want)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        //Long haveKey = Long.parseLong(have);
        //Long wantKey = Long.parseLong(want);
        int limitInt = limit.intValue();
        if (limitInt > 50)
            limitInt = 50;


        List<Order> haveOrders = DCSet.getInstance().getOrderMap().getOrdersHave(have, limitInt);
        List<Order> wantOrders = DCSet.getInstance().getOrderMap().getOrdersWant(want, limitInt);

        Gson gs = new Gson();
        String result = gs.toJson(listRusult);

        return Response.status(200).header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(result).build();
    }

    /**
     * Get trades by timestamp. The number of transactions is limited by input
     * param.
     *
     * @param have      is account
     * @param want      is account two
     * @param timestamp value time
     * @param limit     count out record
     * @return record trades
     * @author Ruslan
     */

    @GET
    @Path("get")
    // apitrade/get?have=1&want=2&timestamp=3&limit=4
    public Response getTrades(@QueryParam("have") Long have, @QueryParam("want") Long want,
                                      @QueryParam("timestamp") Long timestamp, @DefaultValue("20") @QueryParam("limit") Long limit) {

        ItemAssetMap map = this.dcSet.getItemAssetMap();
        // DOES ASSETID EXIST
        if (!map.contains(have)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }
        if (!map.contains(want)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        //Long haveKey = Long.parseLong(have);
        //Long wantKey = Long.parseLong(want);
        int limitInt = limit.intValue();
        if (limitInt > 200)
            limitInt = 200;

        List<Trade> listRusult = cntrl.getTradeByTimestmp(have, want, timestamp, limitInt);

        Gson gs = new Gson();
        String result = gs.toJson(listRusult);

        return Response.status(200).header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(result).build();
    }

    /**
     * Get orders. The number of items in SEL and BUY is limited by LIMIT
     * param.
     *
     * @param have      is HaveKey
     * @param want      is WantKey
     * @param limit     count out record, default = 20
     * @return orders for SELL and BUY
     * @author Icreator
     */

    @GET
    @Path("orders")
    // apitrade/orders?have=1&want=2&limit=20
    public Response orders(@QueryParam("have") Long have, @QueryParam("want") Long want,
                           @DefaultValue("20") @QueryParam("limit") Long limit) {

        ItemAssetMap map = this.dcSet.getItemAssetMap();
        // DOES ASSETID EXIST
        if (!map.contains(have)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }
        if (!map.contains(want)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        Map output = new LinkedHashMap();

        List<Order> ordersHave = dcSet.getOrderMap().getOrdersForTradeWithFork(have, want, false);
        List<Order> ordersWant = dcSet.getOrderMap().getOrdersForTradeWithFork(want, have, true);

        Map sellsJSON = new LinkedHashMap();
        Map buysJSON = new LinkedHashMap();

        BigDecimal sumAmount = BigDecimal.ZERO;

        BigDecimal sumSellingAmount = BigDecimal.ZERO;

        BigDecimal vol;
        int counter = 0;
        // show SELLs in BACK order
        for (int i = ordersHave.size() - 1; i >= 0; i--) {

            Order order = ordersHave.get(i);
            Map sellJSON = new LinkedHashMap();

            sellJSON.put("price", order.getPrice());
            vol = order.getAmountHaveLeft();
            sellJSON.put("amount", vol);
            sumAmount = sumAmount.add(vol);

            sellJSON.put("sellingPrice", Order.calcPrice(order.getAmountWant(), order.getAmountHave()));

            BigDecimal sellingAmount = order.getAmountWantLeft();

            sellJSON.put("sellingAmount", sellingAmount);

            sumSellingAmount = sumSellingAmount.add(sellingAmount);

            sellsJSON.put(order.getId().toString(), sellJSON);

            if(counter++ > limit) break;

        }

        output.put("sells", sellsJSON);

        output.put("sellsSumAmount", sumAmount);
        output.put("sellsSumTotal", sumSellingAmount);

        sumAmount = BigDecimal.ZERO;

        BigDecimal sumBuyingAmount = BigDecimal.ZERO;

        counter = 0;
        for (int i = ordersWant.size() - 1; i >= 0; i--) {

            Order order = ordersWant.get(i);

            Map buyJSON = new LinkedHashMap();

            buyJSON.put("price", order.getPrice());
            vol = order.getAmountHaveLeft();
            buyJSON.put("amount", vol);

            sumAmount = sumAmount.add(vol);

            buyJSON.put("buyingPrice", Order.calcPrice(order.getAmountWant(), order.getAmountHave()));

            BigDecimal buyingAmount = order.getAmountWantLeft();

            buyJSON.put("buyingAmount", buyingAmount);

            sumBuyingAmount = sumBuyingAmount.add(buyingAmount);

            buysJSON.put(order.getId(), buyJSON);
            //buysJSON.put(Base58.encode(order.a.a, 64), buyJSON);

            if(counter++ > limit) break;

        }
        output.put("buys", buysJSON);

        output.put("buysSumAmount", sumBuyingAmount);
        output.put("buysSumTotal", sumAmount);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(StrJSonFine.convert(output))
                //.entity(output.toJSONString())
                .build();

    }

    /**
     * Get trades. The number of items in SEL and BUY is limited by LIMIT
     * param.
     *
     * @param have      is HaveKey
     * @param want      is WantKey
     * @param limit     count out record, default = 100
     * @return trades
     * @author Icreator
     */

    @GET
    @Path("trades")
    // apitrade/trades?have=1&want=2&limit=20
    public Response trades(@QueryParam("have") Long have, @QueryParam("want") Long want,
                           @DefaultValue("20") @QueryParam("limit") Long limit) {

        ItemAssetMap map = this.dcSet.getItemAssetMap();
        // DOES ASSETID EXIST
        if (!map.contains(have)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }
        if (!map.contains(want)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        Map output = new LinkedHashMap();

        //Map tradesJSON = new LinkedHashMap();
        List tradesJSON = new ArrayList();

        List<Trade> trades = dcSet.getTradeMap().getTrades(have,
                want, 0, 0);

        output.put("tradesCount", trades.size());

        BigDecimal tradeWantAmount = BigDecimal.ZERO;
        BigDecimal tradeHaveAmount = BigDecimal.ZERO;

        TransactionFinalMap finalMap = DCSet.getInstance().getTransactionFinalMap();
        Transaction createOrder;

        if (limit > 200)
            limit = 200l;

        int i = 0;
        for (Trade trade : trades) {
            if (i++ > limit) break;

            Map tradeJSON = new LinkedHashMap();

            Order orderInitiator = Order.getOrder(dcSet, trade.getInitiator());

            Order orderTarget = Order.getOrder(dcSet, trade.getTarget());

            tradeJSON.put("amountHave", trade.getAmountHave());
            tradeJSON.put("amountWant", trade.getAmountWant());

            tradeJSON.put("realPrice", trade.calcPrice());
            tradeJSON.put("realReversePrice", trade.calcPriceRevers());

            createOrder = finalMap.get(orderInitiator.getId());
            tradeJSON.put("initiatorTxSignature", Base58.encode(createOrder.getSignature()));
            tradeJSON.put("initiatorId", orderInitiator.getId());

            tradeJSON.put("initiatorCreator", orderInitiator.getCreator().getAddress());
            tradeJSON.put("initiatorAmount", orderInitiator.getAmountHave());
            if (orderInitiator.getHave() == have) {
                tradeJSON.put("type", "sell");
                tradeWantAmount = tradeWantAmount.add(trade.getAmountHave());
                tradeHaveAmount = tradeHaveAmount.add(trade.getAmountWant());

            } else {
                tradeJSON.put("type", "buy");
                tradeHaveAmount = tradeHaveAmount.add(trade.getAmountHave());
                tradeWantAmount = tradeWantAmount.add(trade.getAmountWant());
            }

            createOrder = finalMap.get(orderInitiator.getId());
            tradeJSON.put("targetTxSignature", Base58.encode(createOrder.getSignature()));
            tradeJSON.put("targetId", orderTarget.getId());
            tradeJSON.put("targetCreator", orderTarget.getCreator().getAddress());
            tradeJSON.put("targetAmount", orderTarget.getAmountHave());

            tradeJSON.put("timestamp", trade.getTimestamp());
            tradeJSON.put("dateTime", BlockExplorer.timestampToStr(trade.getTimestamp()));

            tradesJSON.add(tradeJSON);
        }
        output.put("trades", tradesJSON);

        output.put("tradeWantAmount", tradeWantAmount);
        output.put("tradeHaveAmount", tradeHaveAmount);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(StrJSonFine.convert(output))
                //.entity(output.toJSONString())
                .build();

    }

}