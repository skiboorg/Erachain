package org.erachain.webserver;

import org.erachain.api.ApiErrorFactory;
import org.erachain.api.CoreResource;
import org.erachain.controller.Controller;
import org.erachain.core.BlockChain;
import org.erachain.core.account.Account;
import org.erachain.core.account.PublicKeyAccount;
import org.erachain.core.block.Block;
import org.erachain.core.crypto.Base32;
import org.erachain.core.crypto.Base58;
import org.erachain.core.crypto.Crypto;
import org.erachain.core.item.ItemCls;
import org.erachain.core.item.assets.AssetCls;
import org.erachain.core.item.assets.Order;
import org.erachain.core.item.persons.PersonCls;
import org.erachain.core.transaction.Transaction;
import org.erachain.core.transaction.TransactionFactory;
import org.erachain.datachain.*;
import org.erachain.gui.transaction.OnDealClick;
import org.erachain.lang.Lang;
import org.erachain.utils.APIUtils;
import org.erachain.utils.Pair;
import org.erachain.utils.StrJSonFine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;
import org.mapdb.Fun.Tuple4;
import org.mapdb.Fun.Tuple5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;




@SuppressWarnings({"unchecked", "rawtypes"})

@Path("api")
public class API {

    private static final Logger LOGGER = LoggerFactory            .getLogger(API.class);
    @Context
    private UriInfo uriInfo;
    private HttpServletRequest request;
    private DCSet dcSet = DCSet.getInstance();
    private Controller cntrl = Controller.getInstance();

    @GET
    public Response Default() {

        Map help = new LinkedHashMap();

        help.put("see /apiasset", "Help for assets API");
        help.put("see /apipoll", "Help for polls API");
        help.put("see /apiperson", "Help for persons API");
        help.put("see /apitemplate", "Help for templates API");
        help.put("see /apistatus", "Help for statuses API");

        help.put("see /apitelegrams", "Help for telegrams API");
        help.put("see /apiexchange", "Help for exchange API");
        help.put("see /api/tx", "Help for transactions API");
        //help.put("see /apirecords", "Help for transactions API");
        help.put("see /apidocuments", "Help for documents API");

        help.put("*** BALANCE SYSTEM ***", "");
        help.put("bal 1", "Balance Components - {Total_Income, Remaining_Balance]");
        help.put("bal 2", "Balances Array - [Balance in Own, Balance in Debt, Balance on Hold, Balance of Consumer]");

        help.put("*** CHAIN ***", "");
        help.put("GET Height", "height");
        help.put("GET First Block", "firstblock");
        help.put("GET Last Block", "lastblock");
        help.put("GET Last Block Head", "lastblockhead");

        help.put("*** BLOCK ***", "");
        help.put("GET Block", "block/{signature}");
        help.put("GET Block by Height", "blockbyheight/{height}");
        help.put("GET Child Block Signature", "childblocksignature/{signature}");
        help.put("GET Child Block", "childblock/{signature}");

        help.put("*** BLOCKS ***", "");
        help.put("GET Blocks from Height by Limit (end:1 if END is reached)", "blocksfromheight/{height}/{limit}");
        help.put("GET Blocks Signatures from Height by Limit (end:1 if END id reached)", "/blockssignaturesfromheight/{height}/{limit}");

        help.put("*** RECORD ***", "");
        help.put("GET Record Parse from RAW", "recordparse/{RAW}");
        help.put("POST Record Parse from RAW", "recordparse RAW");
        help.put("GET Record", "record/{signature}");
        help.put("GET Record by Height and Sequence", "recordbynumber/{height-sequence}");
        help.put("GET Record RAW", "recordraw/{signature}");
        help.put("GET Record RAW by Height and Sequence", "recordrawbynumber/{block-seqNo]");
        help.put("GET Record RAW by Height and Sequence 2", "recordrawbynumber/{block]/[seqNo]");

        help.put("*** ADDRESS ***", "");
        help.put("GET Address Validate", "addressvalidate/{address}");
        help.put("GET Address Last Reference", "addresslastreference/{address}");
        help.put("GET Address Unconfirmed Last Reference", "addressunconfirmedlastreference/{address}/{from}/{count}");
        help.put("GET Address Generating Balance", "addressgeneratingbalance/{address}");
        help.put("GET Address Asset Balance", "addressassetbalance/{address}/{assetid}");
        help.put("GET Address Assets", "addressassets/{address}");
        help.put("GET Address Public Key", "addresspublickey/{address}");
        help.put("GET Address Forging Info", "addressforge/{address}");

        help.put("*** ASSET ***", "");
        help.put("GET Asset Height", "assetheight");
        help.put("GET Asset", "asset/{key}");
        help.put("GET Asset Data", "assetdata/{key}");
        help.put("GET Asset Image", "assetimage/{key}");
        help.put("GET Asset Icon", "asseticon/{key}");

        help.put("*** ASSETS ***", "");
        help.put("GET Assets by Name Filter", "assetsfilter/{filter_name_string}");

        help.put("*** EXCHANGE ***", "");
        help.put("GET Exchange Orders", "exchangeorders/{have}/{want}");

        help.put("*** PERSON ***", "");
        help.put("GET Person Height", "personheight");
        help.put("GET Person Key by PubKey of Owner", "personkeybyownerpublickey/{publickey}");
        help.put("GET Person", "person/{key}");
        help.put("GET Person Data", "persondata/{key}");
        help.put("GET Person Key by Address", "personkeybyaddress/{address}");
        help.put("GET Person by Address", "personbyaddress/{address}");
        help.put("GET Person Key by Public Key", "personkeybypublickey/{publickey}");
        help.put("GET Person by Public Key", "personbypublickey/{publickey}");
        help.put("GET Person by Public Key Base32", "personbypublickeybase32/{publickeybase32}");
        help.put("GET Accounts From Person", "getaccountsfromperson/{key}");
        help.put("Get Person Image", "personimage/{key}");

        help.put("*** PERSONS ***", "");
        help.put("GET Persons by Name Filter", "personsfilter/{filter_name_string}");

        help.put("*** TOOLS ***", "");
        help.put("POST Verify Signature for JSON {'message': ..., 'signature': Base58, 'publickey': Base58)", "verifysignature");
        help.put("GET info by node", " GET api/info");
        help.put("GET benchmark info by node", " GET api/bench");

        help.put("GET Broadcast", "/broadcast/{raw(Base58)}?lang=en|ru - lang for localize error message");
        help.put("POST Broadcast", "/broadcast?lang=en|ru raw(Base58) - lang for localize error message");
        help.put("POST Broadcastjson", "/broadcastjson JSON - JSON: {raw:raw(Base58), lang:en|ru} - lang for localize error message");

        help.put("POST Broadcasttelegram", "/broadcasttelegram JSON {'raw': raw(Base58)}");
        help.put("GET Broadcasttelegram", "/broadcasttelegram/{raw(Base58)}");

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(StrJSonFine.convert(help))
                .build();
    }


    @GET
    @Path("height")
    public static Response getHeight() {
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(String.valueOf(Controller.getInstance().getMyHeight()))
                .build();
    }

    @GET
    @Path("firstblock")
    public Response getFirstBlock() {

        JSONObject out = dcSet.getBlockMap().get(1).toJson();

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("lastblock")
    public Response lastBlock() {

        Block lastBlock = dcSet.getBlockMap().last();
        JSONObject out = lastBlock.toJson();

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("lastblockhead")
    public Response lastBlockHead() {

        Block.BlockHead lastBlock = dcSet.getBlocksHeadsMap().last();
        JSONObject out = lastBlock.toJson();

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("/childblocksignature/{signature}")
    public Response getChildBlockSignature(@PathParam("signature") String signature) {
        //DECODE SIGNATURE
        byte[] signatureBytes;
        JSONObject out = new JSONObject();

        int step = 1;
        try {
            signatureBytes = Base58.decode(signature);

            ++step;
            Integer heightWT = dcSet.getBlockSignsMap().get(signatureBytes);
            if (heightWT != null && heightWT > 0) {
                byte[] childSign = dcSet.getBlocksHeadsMap().get(heightWT + 1).signature;
                out.put("child", Base58.encode(childSign));
            } else {
                out.put("message", "signature not found");
            }
        } catch (Exception e) {
            out.put("error", step);
            if (step == 1)
                out.put("message", "signature error, use Base58 value");
            else if (step == 2)
                out.put("message", "child not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("/childblock/{signature}")
    public Response getChildBlock(@PathParam("signature") String signature) {
        //DECODE SIGNATURE
        byte[] signatureBytes;
        JSONObject out = new JSONObject();

        int step = 1;
        try {
            signatureBytes = Base58.decode(signature);

            ++step;
            Integer heightWT = dcSet.getBlockSignsMap().get(signatureBytes);
            if (heightWT != null && heightWT > 0) {
                out = dcSet.getBlockMap().getAndProcess(heightWT + 1).toJson();
            } else {
                out.put("message", "signature not found");
            }
        } catch (Exception e) {
            out.put("error", step);
            if (step == 1)
                out.put("message", "signature error, use Base58 value");
            else if (step == 2)
                out.put("message", "child not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("block/{signature}")
    public Response block(@PathParam("signature") String signature) {

        JSONObject out = new JSONObject();

        int step = 1;

        try {
            byte[] key = Base58.decode(signature);

            ++step;
            Block block = dcSet.getBlockSignsMap().getBlock(key);
            out.put("block", block.toJson());

            ++step;
            byte[] childSign = dcSet.getBlocksHeadsMap().get(block.getHeight() + 1).signature;
            if (childSign != null)
                out.put("next", Base58.encode(childSign));

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "signature error, use Base58 value");
            else if (step == 2)
                out.put("message", "block not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("blockbyheight/{height}")
    public Response blockByHeight(@PathParam("height") String heightStr) {

        JSONObject out = new JSONObject();
        int step = 1;

        try {
            int height = Integer.parseInt(heightStr);

            ++step;
            Block block = cntrl.getBlockByHeight(dcSet, height);
            out.put("block", block.toJson());

            ++step;
            byte[] childSign = dcSet.getBlocksHeadsMap().get(block.getHeight() + 1).signature;
            if (childSign != null)
                out.put("next", Base58.encode(childSign));

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height error, use integer value");
            else if (step == 2)
                out.put("message", "block not found");
            else
                out.put("message", e.getMessage());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("blockbyheight2/{height}")
    public Response blockByHeight2(@PathParam("height") String heightStr) {

        JSONObject out = new JSONObject();
        int step = 1;

        try {
            int height = Integer.parseInt(heightStr);

            ++step;
            Block block;
            LinkedList eee = null;
            //LinkedList eee = ((LinkedList) dcSet.getBlocksHeadMap().map);//.keySet());
            //LinkedList eee = ((LinkedList) dcSet.getBlocksHeadMap().map.keySet());
            //LinkedList eee = ((LinkedList) dcSet.getBlocksHeadMap().map.values());
            //LinkedList eee = ((LinkedList) dcSet.getBlocksHeadMap().map.entrySet());
            ListIterator listIterator = eee.listIterator(height);
            block = (Block) listIterator.next();

            //block = dcSet.getBlocksHeadMap().get(iterator.next());
            out.put("block", block.toJson());

            ++step;
            byte[] childSign = dcSet.getBlocksHeadsMap().get(block.getHeight() + 1).signature;
            if (childSign != null)
                out.put("next", Base58.encode(childSign));

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height error, use integer value " + e.getMessage());
            else if (step == 2)
                out.put("message", "block not found " + e.getMessage());
            else
                out.put("message", e.getMessage());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("/blocksfromheight/{height}/{limit}")
    public Response getBlocksFromHeight(@PathParam("height") int height,
                                        @PathParam("limit") int limit) {

        if (limit > 30)
            limit = 30;

        //Map out = new LinkedHashMap();
        JSONObject out = new JSONObject();
        int step = 1;

        try {

            JSONArray array = new JSONArray();
            BlockMap blockMap = dcSet.getBlockMap();
            int max = blockMap.size();
            for (int i = height; i < height + limit; i++) {
                if (i > max) {
                    out.put("end", 1);
                    break;
                }
                array.add(blockMap.getAndProcess(i));
            }
            out.put("blocks", array);

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height error, use integer value");
            else if (step == 2)
                out.put("message", "block not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toString())
                .build();
    }

    @GET
    @Path("/blockssignaturesfromheight/{height}/{limit}")
    public Response getBlocksSignsFromHeight(@PathParam("height") int height,
                                             @PathParam("limit") int limit) {

        if (limit > 100)
            limit = 100;

        JSONObject out = new JSONObject();
        int step = 1;

        try {

            JSONArray array = new JSONArray();
            BlocksHeadsMap blocksHeadsMap = dcSet.getBlocksHeadsMap();
            int max = dcSet.getBlockMap().size();
            for (int i = height; i < height + limit; i++) {
                if (i > max) {
                    out.put("end", 1);
                    break;
                }
                array.add(Base58.encode(blocksHeadsMap.get(i).signature));
            }
            out.put("signatures", array);

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height error, use integer value");
            else if (step == 2)
                out.put("message", "block not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }


    /*
     * ************** RECORDS **********
     */

    @POST
    @Path("recordparse")
    public Response recordParse(String raw) // throws JSONException
    {

        JSONObject out = new JSONObject();

        //CREATE TRANSACTION FROM RAW
        Transaction transaction = null;
        try {
            transaction = TransactionFactory.getInstance().parse(Base58.decode(raw), Transaction.FOR_NETWORK);
            try {
                out = transaction.toJson();
            } catch (Exception e) {
                out.put("error", -1);
                out.put("message", APIUtils.errorMess(-1, e.toString(), transaction));
                transaction.updateMapByError(-1, e.toString(), out);
            }
        } catch (Exception e) {
            out.put("error", -1);
            out.put("message", e.toString());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("recordparse/{raw}")
    public Response recordParseGET(@PathParam("raw") String raw) // throws JSONException
    {
        return recordParse(raw);
    }

    @GET
    @Path("record/{signature}")
    public Response record(@PathParam("signature") String signature) {

        JSONObject out = new JSONObject();

        int step = 1;

        try {
            byte[] key = Base58.decode(signature);

            ++step;
            Transaction record = cntrl.getTransaction(key, dcSet);
            out = record.toJson();

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "signature error, use Base58 value");
            else if (step == 2)
                out.put("message", "record not found");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("recordbynumber/{number}")
    public Response recodByNumber(@PathParam("number") String numberStr) {

        JSONObject out = new JSONObject();
        int step = 1;

        try {

            String[] strA = numberStr.split("\\-");
            int height = Integer.parseInt(strA[0]);
            int seq = Integer.parseInt(strA[1]);

            ++step;
            Transaction record = dcSet.getTransactionFinalMap().get(height, seq);
            out = record.toJson();

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height-sequence error, use integer-integer value");
            else if (step == 2)
                out.put("message", "record not found");
            else
                out.put("message", e.getMessage());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("recordraw/{signature}")
    public Response recordRaw(@PathParam("signature") String signature) {

        JSONObject out = new JSONObject();

        int step = 1;

        byte[] key;
        Transaction record = null;
        try {
            key = Base58.decode(signature);
            ++step;
            record = cntrl.getTransaction(key, dcSet);

            ++step;
            if (record == null) {
                out.put("error", step);
                out.put("message", "record not found");
            } else {
                ++step;
                try {
                    out = record.rawToJson();
                } catch (Exception e) {
                    out.put("error", step);
                    out.put("message", e.getMessage());
                }
            }

        } catch (Exception e) {
            out.put("error", step);
            if (step == 1)
                out.put("message", "signature error, use Base58 value");
            else
                out.put("message", e.getMessage());
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("recordrawbynumber/{number}")
    public Response recodRawByNumber(@PathParam("number") String numberStr) {

        JSONObject out = new JSONObject();
        int step = 1;

        try {

            String[] strA = numberStr.split("\\-");
            int height = Integer.parseInt(strA[0]);
            int seqNo = Integer.parseInt(strA[1]);

            ++step;
            Transaction record = dcSet.getTransactionFinalMap().get(height, seqNo);

            ++step;
            out = record.rawToJson();

        } catch (Exception e) {

            out.put("error", step);
            if (step == 1)
                out.put("message", "height-sequence error, use integer-integer value");
            else if (step == 2)
                out.put("message", "record not found");
            else
                out.put("message", e.getMessage());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("recordrawbynumber/{block}/{seqNo}")
    public Response recodRawBySeqNo(@PathParam("block") int block, @PathParam("seqNo") int seqNo) {

        JSONObject out = new JSONObject();

        try {

            Transaction record = dcSet.getTransactionFinalMap().get(block, seqNo);
            out = record.rawToJson();

        } catch (Exception e) {
            out.put("message", e.getMessage());
        }


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("broadcast/{raw}")
    // http://127.0.0.1:9047/api/broadcast/DPDnFCNvPk4m8GMi2ZprirSgQDwxuQw4sWoJA3fmkKDrYwddTPtt1ucFV4i45BHhNEn1W1pxy3zhRfpxKy6fDb5vmvQwwJ3M3E12jyWLBJtHRYPLnRJnK7M2x5MnPbvnePGX1ahqt7PpFwwGiivP1t272YZ9VKWWNUB3Jg6zyt51fCuyDCinLx4awQPQJNHViux9xoGS2c3ph32oi56PKpiyM
    public Response broadcastRaw(@PathParam("raw") String raw, @QueryParam("lang") String lang) {


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastFromRaw_1(raw, lang).toJSONString())
                .build();
    }

    @POST
    @Path("broadcastjson")
    public Response broadcastFromRawJsonPost(@Context HttpServletRequest request,
                                             MultivaluedMap<String, String> form) {

        String raw = form.getFirst("raw");
        String lang = form.getFirst("lang");

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastFromRaw_1(raw, lang).toJSONString())
                .build();

    }

    @POST
    @Path("broadcast")
    public Response broadcastFromRawPost(@Context HttpServletRequest request,
                                         @QueryParam("lang") String lang,
                                         String raw) {

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastFromRaw_1(raw, lang).toJSONString())
                .build();

    }

    // http://127.0.0.1:9047/api/broadcast?data=DPDnFCNvPk4m8GMi2ZprirSgQDwxuQw4sWoJA3fmkKDrYwddTPtt1ucFV4i45BHhNEn1W1pxy3zhRfpxKy6fDb5vmvQwwJ3M3E12jyWLBJtHRYPLnRJnK7M2x5MnPbvnePGX1ahqt7PpFwwGiivP1t272YZ9VKWWNUB3Jg6zyt51fCuyDCinLx4awQPQJNHViux9xoGS2c3ph32oi56PKpiyM
    public JSONObject broadcastFromRaw_1(String rawDataBase58, String lang) {
        int step = 1;
        JSONObject out = new JSONObject();
        try {
            //	JSONObject jsonObject = (JSONObject) JSONValue.parse(x);
            //	String rawDataBase58 = (String) jsonObject.get("raw");
            byte[] transactionBytes = Base58.decode(rawDataBase58);


            step++;
            Pair<Transaction, Integer> result = Controller.getInstance().lightCreateTransactionFromRaw(transactionBytes);
            if (result.getB() == Transaction.VALIDATE_OK) {
                out.put("status", "ok");
                return out;
            } else {
                JSONObject langObj = Lang.getInstance().getLangJson(lang);

                out.put("error", result.getB());
                out.put("message", langObj == null ? OnDealClick.resultMess(result.getB()) : Lang.T(OnDealClick.resultMess(result.getB()), langObj));
                out.put("lang", lang);
                if (result.getA() != null && result.getA().errorValue != null) {
                    out.put("value", langObj == null ? result.getA().errorValue : Lang.T(result.getA().errorValue, langObj));
                }
                return out;
            }

        } catch (Exception e) {
            LOGGER.warn(" on step: " + step + " - " + e.toString() + " - " + e.getMessage(), e);
            Transaction.updateMapByErrorSimple(-1, e.toString() + " on step: " + step, out);
            return out;
        }
    }

    public JSONObject broadcastTelegram_1(String rawDataBase58) {
        JSONObject out = new JSONObject();
        byte[] transactionBytes;
        Transaction transaction;

        try {
            transactionBytes = Base58.decode(rawDataBase58);
        } catch (Exception e) {
            Transaction.updateMapByErrorSimple(-1, e.toString() + " INVALID_RAW_DATA", out);
            return out;
        }

        try {
            transaction = TransactionFactory.getInstance().parse(transactionBytes, Transaction.FOR_NETWORK);
        } catch (Exception e) {
            Transaction.updateMapByErrorSimple(-1, e.toString() + " parse ERROR", out);
            return out;
        }

        // CHECK IF RECORD VALID
        if (!transaction.isSignatureValid(DCSet.getInstance())) {
            transaction.updateMapByError(-1, "INVALID_SIGNATURE", out);
            return out;
        }

        int result = Controller.getInstance().broadcastTelegram(transaction, true);
        if (result == 0) {
            out.put("status", "ok");
        } else {
            transaction.updateMapByError(result, out);
        }
        out.put("signature", Base58.encode(transaction.getSignature()));
        return out;
    }

    @GET
    //@Path("broadcasttelegram/{raw}")
    @Path("broadcasttelegram/{raw}")
    // http://127.0.0.1:9047/broadcasttelegram/DPDnFCNvPk4m8GMi2ZprirSgQDwxuQw4sWoJA3fmkKDrYwddTPtt1ucFV4i45BHhNEn1W1pxy3zhRfpxKy6fDb5vmvQwwJ3M3E12jyWLBJtHRYPLnRJnK7M2x5MnPbvnePGX1ahqt7PpFwwGiivP1t272YZ9VKWWNUB3Jg6zyt51fCuyDCinLx4awQPQJNHViux9xoGS2c3ph32oi56PKpiyM
    public Response broadcastTelegram(@PathParam("raw") String raw) {


        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastTelegram_1(raw).toJSONString())
                .build();
    }

    @POST
    @Path("broadcasttelegramjson")
    public Response broadcastTelegramPost(@Context HttpServletRequest request,
                                          MultivaluedMap<String, String> form) {

        String raw = form.getFirst("raw");

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastTelegram_1(raw).toJSONString())
                .build();

    }

    @POST
    @Path("broadcasttelegram")
    public Response broadcastTelegramPost(@Context HttpServletRequest request,
                                          String raw) {

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(broadcastTelegram_1(raw).toJSONString())
                .build();

    }

    /*
     * ********** ADDRESS **********
     */
    // TODO переименовать бы LastTimestamp - так более понятно
    @GET
    @Path("addresslastreference/{address}")
    public Response getAddressLastReference(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);
        }

        // GET ACCOUNT
        Account account = result.a;

        long[] lastTimestamp = account.getLastTimestamp();

        String out;
        if (lastTimestamp == null) {
            out = "-";
        } else {
            out = "" + lastTimestamp[0];
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out)
                .build();

    }

    @GET
    @Path("addressunconfirmedlastreference/{address}/{from}/{count}")
    public Response getUnconfirmedLastReferenceUnconfirmed(@PathParam("address") String address,
                                                           @PathParam("from") int from, @PathParam("count") int count) {

        // сейчас этот поиск делается по другому и он не нужен вообще для создания транзакций а следовательно закроем его
        /*
        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);
        }

        // GET ACCOUNT
        Account account = result.a;

        HashSet<byte[]> isSomeoneReference = new HashSet<byte[]>();

        Controller cntrl = Controller.getInstance();

        List<Transaction> transactions = Controller.getInstance().getUnconfirmedTransactions(from, count, true);

        DCSet db = DCSet.getInstance();
        Long lastTimestamp = account.getLastTimestamp();
        byte[] signature;
        if (!(lastTimestamp == null)) {
            signature = cntrl.getSignatureByAddrTime(db, address, lastTimestamp);
            transactions.add(cntrl.get(signature));
        }

        for (Transaction item : transactions) {
            if (item.getCreator().equals(account)) {
                for (Transaction item2 : transactions) {
                    if (item.getTimestamp() == item2.getTimestamp()
                            & item.getCreator().getAddress().equals(item2.getCreator().getAddress())) {
                        // if same address and parent timestamp
                        isSomeoneReference.add(item.getSignature());
                        break;
                    }
                }
            }
        }

        String out = "-";
        if (isSomeoneReference.isEmpty()) {
            return getAddressLastReference(address);
        }

        for (Transaction item : cntrl.getUnconfirmedTransactions(from, count, true)) {
            if (item.getCreator().equals(account)) {
                if (!isSomeoneReference.contains(item.getSignature())) {
                    //return Base58.encode(tx.getSignature());
                    out = "" + item.getTimestamp();
                    break;
                }
            }
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out)
                .build();
        */
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity("---nope")
                .build();

    }


    @GET
    @Path("addressvalidate/{address}")
    public Response validate(@PathParam("address") String address) {
        // CHECK IF VALID ADDRESS
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(String.valueOf(Crypto.getInstance().isValidAddress(address)))
                .build();
    }

    @GET
    @Path("addressgeneratingbalance/{address}")
    public Response getAddressGeneratingBalanceOfAddress(
            @PathParam("address") String address) {
        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Account account = result.a;

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity("" + BlockChain.calcWinValue(DCSet.getInstance(),
                        account, Controller.getInstance().getBlockChain().getHeight(DCSet.getInstance()),
                        account.getBalanceUSE(Transaction.RIGHTS_KEY, dcSet).intValue(), null))
                .build();
    }

    @GET
    @Path("addressassetbalance/{address}/{assetid}")
    public Response getAddressAssetBalance(@PathParam("address") String address,
                                           @PathParam("assetid") String assetid) {
        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Long assetAsLong;

        // HAS ASSET NUMBERFORMAT
        try {
            assetAsLong = Long.valueOf(assetid);
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


        Tuple5<Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>> balance
                = result.a.getBalance(assetAsLong);
        JSONArray array = new JSONArray();

        array.add(setJSONArray(balance.a));
        array.add(setJSONArray(balance.b));
        array.add(setJSONArray(balance.c));
        array.add(setJSONArray(balance.d));
        array.add(setJSONArray(balance.e));

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(array.toJSONString())
                .build();
    }

    @GET
    @Path("addressassets/{address}")
    public Response getAddressAssetBalance(@PathParam("address") String address) {
        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Account account = result.a;
        ItemAssetBalanceMap map = DCSet.getInstance().getAssetBalanceMap();
        List<Tuple2<byte[], Tuple5<
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>> assetsBalances
                = map.getBalancesList(account);

        JSONObject out = new JSONObject();

        for (Tuple2<byte[], Tuple5<
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>,
                Tuple2<BigDecimal, BigDecimal>, Tuple2<BigDecimal, BigDecimal>>>
                assetsBalance : assetsBalances) {
            JSONArray array = new JSONArray();
            long assetKey = ItemAssetBalanceMap.getAssetKeyFromKey(assetsBalance.a);

            if (BlockChain.ERA_COMPU_ALL_UP) {
                array.add(setJSONArray(account.balAaddDEVAmount(assetKey, assetsBalance.b.a)));
            } else {
                array.add(setJSONArray(assetsBalance.b.a));
            }
            array.add(setJSONArray(assetsBalance.b.b));
            array.add(setJSONArray(assetsBalance.b.c));
            array.add(setJSONArray(assetsBalance.b.d));
            array.add(setJSONArray(assetsBalance.b.e));
            out.put(assetKey, array);
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    private JSONArray setJSONArray(Tuple2 t) {
        JSONArray array = new JSONArray();
        array.add(t.a);
        array.add(t.b);
        return array;
    }

    @GET
    @Path("addresspublickey/{address}")
    public Response getPublicKey(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        if (PublicKeyAccount.isValidPublicKey(address)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        byte[] publicKey = Controller.getInstance().getPublicKeyByAddress(address);

        if (publicKey == null) {
            throw ApiErrorFactory.getInstance().createError(
                    Transaction.INVALID_PUBLIC_KEY);
        } else {
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(Base58.encode(publicKey))
                    .build();
        }
    }


    @GET
    @Path("addressforge/{address}")
    public Response getAddressForge(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        Account account = result.a;
        if (account == null) {
            throw ApiErrorFactory.getInstance().createError(
                    result.b);
        }

        JSONObject out = new JSONObject();
        BigDecimal forgingValue = account.getBalanceUSE(Transaction.RIGHTS_KEY, dcSet);
        int height = Controller.getInstance().getMyHeight() + 1;
        long previousTarget = Controller.getInstance().blockChain.getTarget(dcSet);
        // previous making blockHeight + previous ForgingH balance + this ForgingH balance
        Tuple3<Integer, Integer, Integer> lastForgingPoint = account.getLastForgingData(dcSet);
        if (lastForgingPoint == null) {
            out.put("lastForgingPoint", "null");
        } else {
            JSONObject lastForgingPointJSON = new JSONObject();
            lastForgingPointJSON.put("height", lastForgingPoint.a);
            lastForgingPointJSON.put("prevBalance", lastForgingPoint.b);
            lastForgingPointJSON.put("balance", lastForgingPoint.c);
            out.put("lastPoint", lastForgingPointJSON);
            Tuple3<Integer, Integer, Integer> forgingPoint = account.getForgingData(dcSet, lastForgingPoint.a);
            if (forgingPoint == null) {
                out.put("forgingPoint", "null");
            } else {
                JSONObject forgingPointJson = new JSONObject();
                forgingPointJson.put("prevHeight", forgingPoint.a);
                forgingPointJson.put("prevBalance", forgingPoint.b);
                forgingPointJson.put("balance", forgingPoint.c);
                out.put("forgingPoint", forgingPointJson);
            }
        }

        long winValue = BlockChain.calcWinValue(dcSet, account, height, forgingValue.intValue(), lastForgingPoint);
        int targetedWinValue = BlockChain.calcWinValueTargetedBase(dcSet, height, winValue, previousTarget);
        out.put("forgingValue", forgingValue.toPlainString());
        out.put("height", height);
        out.put("winValue", winValue);
        out.put("previousTarget", previousTarget);
        out.put("targetedWinValue", targetedWinValue);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    @GET
    @Path("addresspersonkey/{address}")
    public Response getPersonKey(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(address);

        if (personItem == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.CREATOR_NOT_PERSONALIZED);
        } else {
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("" + personItem.a)
                    .build();
        }
    }

    @GET
    @Path("getaccountsfromperson/{key}")
    public Response getAccountsFromPerson(@PathParam("key") String key) {
        JSONObject out = new JSONObject();
        ItemCls cls = DCSet.getInstance().getItemPersonMap().get(new Long(key));
        if (DCSet.getInstance().getItemPersonMap().get(new Long(key)) == null) {
            out.put("error", "Person not Found");
        } else {
            TreeMap<String, Stack<Tuple3<Integer, Integer, Integer>>> addresses = DCSet.getInstance().getPersonAddressMap().getItems(new Long(key));
            if (addresses.isEmpty()) {
                out.put("null", "null");
            } else {
                Set<String> ad = addresses.keySet();
                int i = 0;
                for (String a : ad) {
                    out.put(i, a);
                    i++;
                }
            }
        }
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(out.toJSONString())
                .build();
    }

    /*
     * ************* ASSET **************
     */
    @GET
    @Path("assetheight")
    public Response assetHeight() {

        long height = dcSet.getItemAssetMap().getLastKey();

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity("" + height)
                .build();

    }

    @GET
    @Path("asset/{key}")
    public Response asset(@PathParam("key") long key) {

        ItemAssetMap map = DCSet.getInstance().getItemAssetMap();

        AssetCls asset = (AssetCls) map.get(key);
        if (asset == null) {
            throw ApiErrorFactory.getInstance().createError(
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(asset.toJson().toJSONString())
                .build();

    }

    @GET
    @Path("assetdata/{key}")
    public Response assetData(@PathParam("key") long key) {

        ItemAssetMap map = DCSet.getInstance().getItemAssetMap();

        AssetCls asset = (AssetCls) map.get(key);
        if (asset == null) {
            throw ApiErrorFactory.getInstance().createError(
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(asset.toJsonData().toJSONString())
                .build();

    }

    /*
     * ************* ASSETS **************
     */

    @GET
    @Path("assetsfilter/{filter_name_string}")
    public Response assetsFilter(@PathParam("filter_name_string") String filter) {

        return APIItemAsset.find(filter);

    }

    /*
     * ************* EXCHANGE **************
     */
    @GET
    @Path("exchangeorders/{have}/{want}")
    public Response exchangeOrders(@PathParam("have") long have, @PathParam("want") long want) {

        ItemAssetMap map = DCSet.getInstance().getItemAssetMap();
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

        /* OLD
        SortableList<BigInteger, Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>>> ordersA = this.dcSet.getOrderMap().getOrdersSortableList(have, want, true);

        JSONArray arrayA = new JSONArray();

        if (!ordersA.isEmpty()) {
            for (Pair<BigInteger, Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                    Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>>> pair : ordersA) {
                Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                        Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>> order = pair.getB();
                JSONArray itemJson = new JSONArray();
                itemJson.add(order.b.b.subtract(order.b.c)); // getAmountHaveLeft());
                itemJson.add(Order.calcPrice(order.b.b, order.c.b));

                arrayA.add(itemJson);
            }
        }

        SortableList<BigInteger, Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>>> ordersB = this.dcSet.getOrderMap().getOrdersSortableList(want, have, true);

        JSONArray arrayB = new JSONArray();

        if (!ordersA.isEmpty()) {
            for (Pair<BigInteger, Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                    Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>>> pair : ordersB) {
                Tuple3<Tuple5<BigInteger, String, Long, Boolean, BigDecimal>,
                        Tuple3<Long, BigDecimal, BigDecimal>, Tuple2<Long, BigDecimal>> order = pair.getB();
                JSONArray itemJson = new JSONArray();
                itemJson.add(order.b.b.subtract(order.b.c)); // getAmountHaveLeft());
                itemJson.add(Order.calcPrice(order.b.b, order.c.b));

                arrayB.add(itemJson);
            }
        }
        */

        JSONArray arraySell = new JSONArray();
        List<Order> orders = this.dcSet.getOrderMap().getOrdersForTrade(have, want, false);
        for (Order order : orders) {
            JSONArray itemJson = new JSONArray();
            itemJson.add(order.getAmountHaveLeft());
            itemJson.add(order.calcLeftPrice());
            itemJson.add(order.getAmountWantLeft());

            arraySell.add(itemJson);

        }

        JSONArray arrayBuy = new JSONArray();
        orders = this.dcSet.getOrderMap().getOrdersForTrade(want, have, false);
        for (Order order : orders) {
            JSONArray itemJson = new JSONArray();
            itemJson.add(order.getAmountHaveLeft());
            itemJson.add(order.calcLeftPriceReverse()); // REVERSE
            itemJson.add(order.getAmountWantLeft());

            arrayBuy.add(itemJson);

        }

        JSONObject itemJSON = new JSONObject();

        // ADD DATA
        itemJSON.put("buy", arrayBuy);
        itemJSON.put("sell", arraySell);
        itemJSON.put("pair", have + ":" + want);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(itemJSON.toJSONString())
                .build();

    }


    /*
     * ************* PERSON **************
     */
    @GET
    @Path("personheight")
    public Response personHeight() {

        long height = dcSet.getItemPersonMap().getLastKey();

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity("" + height)
                .build();

    }

    @GET
    @Path("person/{key}")
    public Response person(@PathParam("key") long key) {

        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        PersonCls person = (PersonCls) map.get(key);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(person.toJson().toJSONString())
                .build();

    }

    @Path("assetimage/{key}")
    @GET
    @Produces({"image/png", "image/jpg"})
    public Response assetImage(@PathParam("key") long key) throws IOException {

        int weight = 0;
        if (key <= 0) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    "Error key");
        }

        ItemAssetMap map = DCSet.getInstance().getItemAssetMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        AssetCls asset = (AssetCls) map.get(key);

        if (asset.getImage() != null) {
            // image to byte[] hot scale (param2 =0)
            //	byte[] b = Images_Work.ImageToByte(new ImageIcon(person.getImage()).getImage(), 0);
            ///return Response.ok(new ByteArrayInputStream(asset.getImage())).build();
            return Response.status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(new ByteArrayInputStream(asset.getImage()))
                    .build();
        }
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity("")
                .build();

    }

    @Path("asseticon/{key}")
    @GET
    @Produces({"image/png", "image/jpg"})
    public Response assetIcon(@PathParam("key") long key) throws IOException {

        if (key <= 0) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    "Error key");
        }

        ItemAssetMap map = DCSet.getInstance().getItemAssetMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_ASSET_NOT_EXIST);
        }

        AssetCls asset = (AssetCls) map.get(key);

        if (asset.getIcon() != null) {
            // image to byte[] hot scale (param2 =0)
            //	byte[] b = Images_Work.ImageToByte(new ImageIcon(person.getImage()).getImage(), 0);
            //return Response.ok(new ByteArrayInputStream(asset.getIcon())).build();
            return Response.status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(new ByteArrayInputStream(asset.getIcon()))
                    .build();
        }
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity("")
                .build();
    }

    @Path("personimage/{key}")
    @GET
    @Produces({"image/png", "image/jpg"})
    public Response getFullImage(@PathParam("key") long key) throws IOException {

        int weight = 0;
        if (key <= 0) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    "Error key");
        }

        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        PersonCls person = (PersonCls) map.get(key);

        // image to byte[] hot scale (param2 =0)
        //	byte[] b = Images_Work.ImageToByte(new ImageIcon(person.getImage()).getImage(), 0);
        //return Response.ok(new ByteArrayInputStream(person.getImage())).build();
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(new ByteArrayInputStream(person.getImage()))
                .build();
    }


    @GET
    @Path("persondata/{key}")
    public Response personData(@PathParam("key") long key) {

        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES ASSETID EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        PersonCls person = (PersonCls) map.get(key);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(person.toJsonData().toJSONString())
                .build();

    }

    @GET
    @Path("personkeybyaddress/{address}")
    public Response getPersonKeyByAddres(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(address);

        if (personItem == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.CREATOR_NOT_PERSONALIZED);
        } else {
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("" + personItem.a)
                    .build();
        }
    }

    @GET
    @Path("personkeybypublickey/{publickey}")
    public Response getPersonKeyByPublicKey(@PathParam("publickey") String publicKey) {

        // CHECK IF VALID ADDRESS
        if (!PublicKeyAccount.isValidPublicKey(publicKey)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_PUBLIC_KEY);

        }

        PublicKeyAccount publicKeyAccount = new PublicKeyAccount(publicKey);

        Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(publicKeyAccount.getShortAddressBytes());

        if (personItem == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.CREATOR_NOT_PERSONALIZED);
        } else {
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("" + personItem.a)
                    .build();
        }
    }

    @GET
    @Path("personkeybyownerpublickey/{publickey}")
    public Response getPersonKeyByOwnerPublicKey(@PathParam("publickey") String publicKey) {

        // CHECK IF VALID ADDRESS
        if (!PublicKeyAccount.isValidPublicKey(publicKey)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_PUBLIC_KEY);

        }

        PublicKeyAccount publicKeyAccount = new PublicKeyAccount(publicKey);
        byte[] pkBytes = publicKeyAccount.getPublicKey();
        if (!DCSet.getInstance().getIssuePersonMap().contains(pkBytes)) {
            throw ApiErrorFactory.getInstance().createError(
                    Transaction.ITEM_PERSON_NOT_EXIST);
        } else {
            Long key = DCSet.getInstance().getIssuePersonMap().get(pkBytes);
            if (key == null || key == 0) {
                throw ApiErrorFactory.getInstance().createError(
                        Transaction.ITEM_PERSON_NOT_EXIST);
            }

            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("" + key)
                    .build();
        }
    }

    @GET
    @Path("personkeybyownerpublickey32/{publickey}")
    public Response getPersonKeyByOwnerPublicKey32(@PathParam("publickey") String publicKey32) {

        JSONObject answer = new JSONObject();
        try {
            byte[] publicKey = Base32.decode(publicKey32);
            return getPersonKeyByOwnerPublicKey(Base58.encode(publicKey));
        } catch (Exception e) {
            answer.put("Error", "Invalid Base32 Key");
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(answer.toJSONString())
                    .build();
        }
    }


    @GET
    @Path("personbyaddress/{address}")
    public Response personByAddress(@PathParam("address") String address) {

        // CHECK IF VALID ADDRESS
        Tuple2<Account, String> result = Account.tryMakeAccount(address);
        if (result.a == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_ADDRESS);

        }

        Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(address);

        if (personItem == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        long key = personItem.a;
        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        PersonCls person = (PersonCls) map.get(key);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(person.toJson().toJSONString())
                .build();

    }

    @GET
    @Path("personbypublickey/{publickey}")
    public Response personByPublicKey(@PathParam("publickey") String publicKey) {

        // CHECK IF VALID ADDRESS
        if (!PublicKeyAccount.isValidPublicKey(publicKey)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_PUBLIC_KEY);

        }

        PublicKeyAccount publicKeyAccount = new PublicKeyAccount(publicKey);

        Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(publicKeyAccount.getShortAddressBytes());
        //Tuple4<Long, Integer, Integer, Integer> personItem = DCSet.getInstance().getAddressPersonMap().getItem(publicKeyAccount.getAddress());

        if (personItem == null) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        long key = personItem.a;
        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES EXIST
        if (!map.contains(key)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ASSET_ID);
                    Transaction.ITEM_PERSON_NOT_EXIST);
        }

        PersonCls person = (PersonCls) map.get(key);

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(person.toJson().toJSONString())
                .build();
    }

    @GET
    @Path("personbyownerpublickey/{publickey}")
    public Response getPersonByOwnerPublicKey(@PathParam("publickey") String publicKey) {

        // CHECK IF VALID ADDRESS
        if (!PublicKeyAccount.isValidPublicKey(publicKey)) {
            throw ApiErrorFactory.getInstance().createError(
                    //ApiErrorFactory.ERROR_INVALID_ADDRESS);
                    Transaction.INVALID_PUBLIC_KEY);

        }

        PublicKeyAccount publicKeyAccount = new PublicKeyAccount(publicKey);
        byte[] pkBytes = publicKeyAccount.getPublicKey();
        if (!DCSet.getInstance().getIssuePersonMap().contains(pkBytes)) {
            throw ApiErrorFactory.getInstance().createError(
                    Transaction.ITEM_PERSON_NOT_EXIST);
        } else {
            Long key = DCSet.getInstance().getIssuePersonMap().get(pkBytes);
            if (key == null || key == 0) {
                throw ApiErrorFactory.getInstance().createError(
                        Transaction.ITEM_PERSON_NOT_EXIST);
            }

            PersonCls person = (PersonCls) DCSet.getInstance().getItemPersonMap().get(key);
            if (person == null) {
                throw ApiErrorFactory.getInstance().createError(
                        Transaction.ITEM_PERSON_NOT_EXIST);
            }

            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(person.toJson().toJSONString())
                    .build();
        }
    }

    @GET
    @Path("personbyownerpublickey32/{publickey}")
    public Response getPersonByOwnerPublicKey32(@PathParam("publickey") String publicKey32) {

        JSONObject answer = new JSONObject();
        try {
            byte[] publicKey = Base32.decode(publicKey32);
            return getPersonByOwnerPublicKey(Base58.encode(publicKey));
        } catch (Exception e) {
            answer.put("Error", "Invalid Base32 Key");
            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(answer.toJSONString())
                    .build();
        }
    }

    @GET
    @Path("personbypublickeybase32/{publickeybase}")
    @Deprecated
    public Response personsByBankKey(@PathParam("publickeybase") String publicKey32) {

        return getPersonByOwnerPublicKey32(publicKey32);

    }

    /*
     * ************* PERSONS **************
     */

    @GET
    @Path("personsfilter/{filter_name_string}")
    public Response personsFilter(@PathParam("filter_name_string") String filter) {

        if (filter == null || filter.length() < 3) {
            return Response.status(501)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("error - so small filter length")
                    .build();
        }

        ItemPersonMap map = DCSet.getInstance().getItemPersonMap();
        // DOES ASSETID EXIST
        List<ItemCls> list = map.getByFilterAsArray(filter, 0, 100);

        JSONArray array = new JSONArray();

        if (list != null) {
            for (ItemCls item : list) {
                array.add(item.toJson());
            }
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(array.toJSONString())
                .build();

    }


    /*
     * ************* TOOLS **************
     */

    /**
     * wiury2876rw7yer8923y63riyrf9287y6r87wyr9737yriwuyr3yr978ry48732y3rsiouyvbkshefiuweyriuwer
     * {"trtr": 293847}
     * @param x
     * @return
     */
    @POST
    @Path("verifysignature")
    public Response verifysignature(String x) {
        try {
            // READ JSON
            JSONObject jsonObject = (JSONObject) JSONValue.parse(x);
            String message = (String) jsonObject.get("message");
            String signature = (String) jsonObject.get("signature");
            String publicKey = (String) jsonObject.get("publickey");

            // DECODE SIGNATURE
            byte[] signatureBytes;
            try {
                signatureBytes = Base58.decode(signature);
            } catch (Exception e) {
                throw ApiErrorFactory.getInstance().createError(
                        Transaction.INVALID_SIGNATURE);

            }

            // DECODE PUBLICKEY
            byte[] publicKeyBytes;
            try {
                publicKeyBytes = Base58.decode(publicKey);
            } catch (Exception e) {
                throw ApiErrorFactory.getInstance().createError(
                        //ApiErrorFactory.ERROR_INVALID_PUBLIC_KEY);
                        Transaction.INVALID_PUBLIC_KEY);

            }

            return Response.status(200)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(String.valueOf(Crypto.getInstance().verify(publicKeyBytes,
                            signatureBytes, message.getBytes(StandardCharsets.UTF_8))))
                    .build();

        } catch (NullPointerException e) {
            // JSON EXCEPTION
            throw ApiErrorFactory.getInstance().createError(
                    ApiErrorFactory.ERROR_JSON);
        } catch (ClassCastException e) {
            // JSON EXCEPTION
            throw ApiErrorFactory.getInstance().createError(
                    ApiErrorFactory.ERROR_JSON);
        }
    }

    @GET
    @Path("info")
    public Response getInformation() throws NoSuchFieldException, IllegalAccessException {
        JSONObject jsonObject = CoreResource.infoJson();

        if (false) {
            Object f = Controller.class.getDeclaredField("version");
            ((Field) f).setAccessible(true);
            String version = ((Field) f).get(Controller.getInstance()).toString();
            ((Field) f).setAccessible(false);
            jsonObject.put("version2", version);
        }

        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(jsonObject.toJSONString())
                .build();
    }

    @GET
    @Path("bench")
    public Response getSpeedInfo() {
        return Response.status(200)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Access-Control-Allow-Origin", "*")
                .entity(Controller.getInstance().getBenchmarks().toJSONString())
                .build();
    }

}
