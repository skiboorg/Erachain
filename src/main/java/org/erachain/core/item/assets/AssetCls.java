package org.erachain.core.item.assets;


import com.google.common.primitives.Bytes;
import org.erachain.controller.PairsController;
import org.erachain.core.BlockChain;
import org.erachain.core.account.Account;
import org.erachain.core.account.PublicKeyAccount;
import org.erachain.core.block.Block;
import org.erachain.core.exdata.exLink.ExLinkAddress;
import org.erachain.core.item.ItemCls;
import org.erachain.core.item.persons.PersonCls;
import org.erachain.core.transaction.Transaction;
import org.erachain.core.transaction.TransactionAmount;
import org.erachain.database.PairMap;
import org.erachain.datachain.DCSet;
import org.erachain.datachain.IssueItemMap;
import org.erachain.datachain.ItemMap;
import org.erachain.gui.library.Library;
import org.erachain.lang.Lang;
import org.erachain.utils.NumberAsString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mapdb.Fun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * flag[0] - profitFeeMin[int] + profitFeeMax[int]
 * flag[1] - profitTax[int] + loanInterest[int] //  use "/apiasset/image/1048664" "/apiasset/icon/1048664"
 */
public abstract class AssetCls extends ItemCls {

    public static final int TYPE_KEY = ItemCls.ASSET_TYPE;

    protected static final int ASSET_TYPE_LENGTH = 1;

    protected static final long APP_DATA_DEX_AWARDS_MASK = 1;

    //
    protected int assetType;
    protected ExLinkAddress[] dexAwards;

    // CORE KEY
    public static final long ERA_KEY = 1L;
    public static final String ERA_ABBREV = "FOIL"; // ERA (main rights units)
    public static final String ERA_NAME = "FOIL";
    public static final String ERA_DESCR = "";

    public static final long FEE_KEY = 2L;
    public static final String FEE_ABBREV = "MVolt"; // COMP (main rights units)
    public static final String FEE_NAME = "MVolt";
    public static final String FEE_DESCR = "";

    public static final long AS_KEY = 3L;
    public static final String AS_ABBREV = "AS";
    public static final String AS_NAME = "AS";
    public static final String AS_DESCR = "";

    public static final long LIA_KEY = 5L;
    public static final String LIA_ABBREV = "LIA"; // Live In Asset
    public static final String LIA_NAME = "LIA";
    public static final String LIA_DESCR = "";

    public static final long BAL_KEY = 7L; // see in chainPROTOCOL.json
    public static final String BAL_ABBREV = "BAL";
    public static final String BAL_NAME = "BAL";
    public static final String BAL_DESCR = "";

    public static final long BTC_KEY = 12L;
    public static final long USD_KEY = 1840L;
    public static final long EUR_KEY = 1978L;

    public static final int UNIQUE = 1;
    public static final int VENTURE = 2;
    public static final int NAME = 3;
    public static final int INITIAL_FAVORITES = 100;

    ///////////////////////////////////////////////////
    /**
     * GOODS
     * передача в собственность, взять на хранение
     * 0 : движимая вещь вовне - может быть доставлена и передана на хранение (товары)
     */
    public static final int AS_OUTSIDE_GOODS = 0; // movable

    /**
     * ASSETS
     * передача имущества не требует действий во вне - все исполняется тут же. Их можно дать в долг и заьрать самостоятельно
     * Требования не предъявляются.
     * 3 : цифровое имущество - не требует действий вовне и исполняется внутри платформы (токены, цифровые валюты, цифровые билеты, цифровые права и т.д.)
     */
    public static final int AS_INSIDE_ASSETS = 1;

    /**
     * IMMOVABLE
     * передача в сосбтвенность, дать в аренду (по графику времени), взять на охрану
     * 1 : недвижимая вещь вовне - может быть передана в аренду (недвижимость)
     */

    public static final int AS_OUTSIDE_IMMOVABLE = 2;

    /**
     * outside CURRENCY
     * +++ деньги вовне - можно истребовать вернуть и подтвердить получение денег
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_OUTSIDE_CURRENCY = 11;

    /**
     * outside SERVICE
     * +++ услуги во вне
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_OUTSIDE_SERVICE = 12; // UTILITY

    /**
     * outside SHARE
     * +++ акция предприятия вовне
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_OUTSIDE_SHARE = 13;

    /**
     * outside BILL - вексель
     * +++ вексель на оплату во вне
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_OUTSIDE_BILL = 14;

    /**
     * outside BILL - вексель
     * +++ вексель на оплату во вне
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_OUTSIDE_BILL_EX = 15;

    /**
     * my debt
     * +++ мой долг перед другим лицом - это обязательство
     * === полный аналог OUTSIDE_CLAIM по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_MY_DEBT = 26;

    /**
     * 🕐🕜🕑🕝🕒🕞🕓🕟🕔🕠🕕🕡🕖🕢🕗🕣🕘🕤🕙🕥🕚🕦🕛🕧
     * outside WORK TIME - рабочее время, которое можно купить и потребовать потратить и учесть как затрата
     */
    public static final int AS_OUTSIDE_WORK_TIME_MINUTES = 34;
    public static final int AS_OUTSIDE_WORK_TIME_HOURS = 35;

    /**
     * outside CLAIMS
     * +++ требования и обязательства вовне - можно истребовать право и подтвердить его исполнение (ссуда, займ, услуга, право, требование, деньги, билеты и т.д.)
     * <p>
     * учет обязательств прав и требований на услуги и действия во внешнем мире - в том числе займы, ссуды, кредиты, фьючерсы и т.д.
     * нельзя вернуть эмитенту - но можно потребовать исполнение прав и можно подтвердить исполнение (погасить требование)
     * это делается теми же трнзакциями что выдать и забрать долг у внутренних активов
     * И в момент погашения одновременно передается как имущество эмитенту
     */
    public static final int AS_OUTSIDE_OTHER_CLAIM = 49;

    ///////////////
    /**
     * inside CURRENCY
     * +++ деньги
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INSIDE_CURRENCY = 51;

    /**
     * inside CLAIMS
     * +++ требования и обязательства
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INSIDE_UTILITY = 52; // SERVICE

    /**
     * inside CLAIMS
     * +++ требования и обязательства
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INSIDE_SHARE = 53;

    /**
     * inside BONUS
     * +++ бонусы - для анонимов так же платежи возможны
     * === ASSET - без обмена на бирже и можно анонимам переводить
     */
    public static final int AS_INSIDE_BONUS = 54;

    /**
     * inside RIGHTS
     * +++ права и доступы
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     * можно вернуть право себе создателю и справо дается не в долг а как на харанение - и потом любой может забрать с хранения
     * 2 баланса - имущечтыо и хранение - при передаче? короче каждый может кто имеет право выдавать или назначать право
     * потом забирать назначение с баланса Хранить - получается как с движимым товарос
     */
    public static final int AS_INSIDE_ACCESS = 55;

    /**
     * inside VOTE
     * +++ права и доступы
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INSIDE_VOTE = 56;

    /**
     * bank guarantee - банковская гарантия
     * === полный аналог AS_INSIDE_ASSETS по действиям в протоколе - чисто для наименования другого - так как не требует действий 2-й стороны - скорее бухгалтерская единица?
     */

    public static final int AS_BANK_GUARANTEE = 60;
    /**
     * bank guarantee total - банковская гарантия общая сумма - так как не требует действий 2-й стороны - скорее бухгалтерская единица?
     * === полный аналог AS_INSIDE_ASSETS по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_BANK_GUARANTEE_TOTAL = 61;

    /**
     * NFT - Non Fungible Token. невзаимозаменяемый токен
     * === полный аналог AS_INSIDE_ASSETS по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_NON_FUNGIBLE = 65;
    public static final int AS_RELEASED_FUNGIBLE = 67;

    /**
     * INDEXES (FOREX etc.)
     * +++ требования и обязательства
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INDEX = 100;

    /**
     * inside CLAIMS
     * +++ требования и обязательства
     * === полный аналог ASSET по действиям в протоколе - чисто для наименования другого
     */
    public static final int AS_INSIDE_OTHER_CLAIM = 119;

    /**
     * ACCOUNTING
     * учетные единицы - нельзя на бирже торговать - они ничего не стоят, можно делать любые действия от своего имени
     * 4 : учетные единицы - не имеет стоимости и не может быть продано (бухгалтерский учет)
     */
    public static final int AS_ACCOUNTING = 123;

    /**
     * self-managed
     * === Не может управляться ни кем кроме обладателя актива
     * === доступны 4-ре баланса и у каждого работает Возврат - backward
     */
    public static final int AS_SELF_MANAGED_ACCOUNTING = 124;

    /**
     * accounting loan
     * +++ мой займ другому лицу - учетный, бухгалтерский учет
     * === подобно AS_SELF_MANAGED_ACCOUNTING - но долговой баланс - отражает требование к оплате
     */
    public static final int AS_SELF_ACCOUNTING_LOAN = 125;

    /**
     * mutual aid fund
     * +++ фонд взаимопомощи - учетный, бухгалтерский учет
     * === подобно AS_SELF_MANAGED_ACCOUNTING - по-идее тут без требований к оплате
     */
    public static final int AS_SELF_ACCOUNTING_MUTUAL_AID_FUND = 126;

    /**
     * cash fund
     * +++ денежный фонд - для учета взносов ТСЖ например - учетный, бухгалтерский учет
     * === подобно AS_SELF_MANAGED_ACCOUNTING - c требованиями к оплате и с автоматическим снятием требования (DEBT) при погашении
     */
    public static final int AS_SELF_ACCOUNTING_CASH_FUND = 127;

    /**
     * self-managed - direct OWN balances
     * === Не может управляться ни кем кроме обладателя актива
     * === доступны 4-ре баланса и у каждого работает Возврат - backward
     */
    public static final int AS_SELF_MANAGED_DIRECT_SEND = 128;
    /**
     * self-managed - direct OWN balances
     * === Не может управляться ни кем кроме обладателя актива
     * === доступны 4-ре баланса и у каждого работает Возврат - backward
     */
    public static final int AS_SELF_MANAGED_SHARE = 129;

    protected AssetCls(byte[] typeBytes, byte[] appData, PublicKeyAccount maker, String name, byte[] icon, byte[] image, String description, int assetType) {
        super(typeBytes, appData, maker, name, icon, image, description);
        this.assetType = assetType;
    }

    public AssetCls(int type, byte pars, byte[] appData, PublicKeyAccount maker, String name, byte[] icon, byte[] image, String description, int assetType) {
        this(new byte[TYPE_LENGTH], appData, maker, name, icon, image, description, assetType);
        this.typeBytes[0] = (byte) type;
        this.typeBytes[1] = pars;
    }

    protected AssetCls(byte[] typeBytes, byte[] appDataIn, PublicKeyAccount maker, String name, byte[] icon, byte[] image, String description, int assetType,
                       ExLinkAddress[] dexAwards) {
        this(typeBytes, appDataIn, maker, name, icon, image, description, assetType);
        this.dexAwards = dexAwards;
    }

    @Override
    protected int parseAppData() {
        int pos = super.parseAppData();
        if ((flags & APP_DATA_DEX_AWARDS_MASK) != 0) {
            int dexAwardsLen = appData[pos++];
            dexAwards = new ExLinkAddress[dexAwardsLen];
            for (int i = 0; i < dexAwardsLen; i++) {
                dexAwards[i] = new ExLinkAddress(appData, pos);
                pos += dexAwards[i].length();
            }
        }
        return pos;
    }

    public static byte[] makeAppData(boolean iconAsURL, int iconType, boolean imageAsURL, int imageType,
                                     ExLinkAddress[] dexAwards) {
        byte[] appData = ItemCls.makeAppData(dexAwards == null ? 0 : APP_DATA_DEX_AWARDS_MASK,
                iconAsURL, iconType, imageAsURL, imageType);

        if (dexAwards == null)
            return appData;

        appData = Bytes.concat(appData, new byte[]{(byte) dexAwards.length});
        for (ExLinkAddress exAddress : dexAwards) {
            appData = Bytes.concat(appData, exAddress.toBytes());
        }

        return appData;
    }

    //GETTERS/SETTERS

    public static int[] assetTypes;

    public static int[] assetTypes() {

        if (assetTypes != null)
            return assetTypes;

        int[] array = new int[]{

                AS_OUTSIDE_GOODS,
                AS_OUTSIDE_IMMOVABLE,
                AS_OUTSIDE_CURRENCY,
                AS_OUTSIDE_SERVICE,
                AS_OUTSIDE_BILL,
                AS_OUTSIDE_WORK_TIME_HOURS,
                AS_OUTSIDE_WORK_TIME_MINUTES,
                AS_OUTSIDE_SHARE,

                AS_MY_DEBT,

                AS_OUTSIDE_OTHER_CLAIM,

                AS_INSIDE_ASSETS,
                AS_INSIDE_CURRENCY,
                AS_INSIDE_UTILITY,
                AS_INSIDE_SHARE,
                AS_INSIDE_BONUS,
                AS_INSIDE_ACCESS,
                AS_INSIDE_VOTE,
                AS_BANK_GUARANTEE,
                AS_BANK_GUARANTEE_TOTAL,
                AS_NON_FUNGIBLE,
                AS_INDEX,
                AS_INSIDE_OTHER_CLAIM,

                AS_ACCOUNTING,
                AS_SELF_MANAGED_ACCOUNTING,
                AS_SELF_ACCOUNTING_LOAN,
                AS_SELF_ACCOUNTING_MUTUAL_AID_FUND,
                AS_SELF_ACCOUNTING_CASH_FUND,
                AS_SELF_MANAGED_DIRECT_SEND,
                AS_SELF_MANAGED_SHARE
        };

        if (BlockChain.TEST_MODE) {
            // AS_SELF_ACCOUNTING_CASH_FUND,
        }

        Arrays.sort(array);

        return array;
    }

    @Override
    public int getItemType() {
        return TYPE_KEY;
    }

    @Override
    public long START_KEY() {
        if (Transaction.parseHeightDBRef(dbRef) > BlockChain.START_KEY_UP)
            return BlockChain.START_KEY_UP_ITEMS;

        return START_KEY_OLD;
    }

    @Override
    public long MIN_START_KEY() {
        if (Transaction.parseHeightDBRef(dbRef) > BlockChain.START_KEY_UP)
            return BlockChain.START_KEY_UP_ITEMS;

        return MIN_START_KEY_OLD;
    }

    @Override
    public String getItemTypeName() {
        return "asset";
    }

    // DB
    @Override
    public ItemMap getDBMap(DCSet db) {
        return db.getItemAssetMap();
    }

    @Override
    public IssueItemMap getDBIssueMap(DCSet db) {
        return db.getIssueAssetMap();
    }

    //public abstract long getQuantity();

    public abstract BigDecimal getReleased();
    public abstract BigDecimal getReleased(DCSet dc);

    public int getAssetType() {
        return this.assetType;
    }

    // https://unicode-table.com/ru/#23FC
    public static String charAssetType(long key, int assetType) {

        if (key < 10000) {
            return "";
        }

        switch (assetType) {
            case AS_OUTSIDE_GOODS:
                return "▲";
            case AS_OUTSIDE_IMMOVABLE:
                return "▼";
            case AS_ACCOUNTING:
                if (key == 555L || key == 666L || key == 777L)
                    return "♥";

                return "±";
            case AS_INDEX:
                return "⤴";
            case AS_INSIDE_VOTE:
                return "✋";
            case AS_OUTSIDE_BILL:
                return "⬖"; // ⬒
            case AS_OUTSIDE_SERVICE:
                return "⬔";
            case AS_INSIDE_BONUS:
                return "⮌";
            case AS_INSIDE_ACCESS:
                return "⛨";
            case AS_INSIDE_SHARE:
                return "◒";
            case AS_SELF_MANAGED_ACCOUNTING:
            case AS_SELF_ACCOUNTING_LOAN:
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
            case AS_SELF_ACCOUNTING_CASH_FUND:
            case AS_SELF_MANAGED_DIRECT_SEND:
            case AS_SELF_MANAGED_SHARE:
                return "±";
            case AS_MY_DEBT:
                return "◆";
            case AS_OUTSIDE_WORK_TIME_HOURS:
                // 🕐🕜🕑🕝🕒🕞🕓🕟🕔🕠🕕🕡🕖🕢🕗🕣🕘🕤🕙🕥🕚🕦🕛🕧
                return "◕";
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                return "◔";


        }

        if (assetType >= AS_OUTSIDE_CURRENCY
                && assetType <= AS_OUTSIDE_OTHER_CLAIM)
            return "◄";

        if (assetType == AS_INSIDE_ASSETS
                || assetType >= AS_INSIDE_CURRENCY
                && assetType <= AS_INSIDE_OTHER_CLAIM)
            return "►";

        // ● ⚫ ◆ █ ▇ ■ ◢ ◤ ◔ ◑ ◕ ⬛ ⬜ ⬤ ⛃
        return "⚫";

    }

    public String charAssetType() {
        return charAssetType(this.key, this.assetType);
    }

    @Override
    public String getName() {
		/*
		if (this.key == 1)
			return "ERA";
		 */

        return this.name;
    }

    @Override
    public int getMinNameLen() {
        return 1;
    }

    @Override
    public String viewName() {

        if (this.key < 100) {
            return this.name;
        } else if (key < getStartKey()) {
            return charAssetType() + this.name;
        }

        return charAssetType() + viewAssetTypeAbbrev() + ":" + this.name;

    }

    public PublicKeyAccount getMaker() {
        if (this.key > 10 && this.key < 100 && BlockChain.ASSET_OWNERS.containsKey(this.key)) {
            return BlockChain.ASSET_OWNERS.get(this.key);
        }

        return this.maker;
    }

    @Override
    public String getDescription() {
		/*
		if (this.key == 1)
			return "'Управляющая единица' (единица доли собственности) - подобна акции предприятия. Дает право собирать блоки тем чаще, чем больше Вы имеете их в обладании. Так же дает право удостоверять других персон и создавать новые статусы.";
		else if (this.key == 2)
			return "'Рабочая единица' (единица оплаты) - применяется для оплаты транзакций и как награда за сборку блоков.";
		else if (this.key == 3)
			return "'Доверяющая единица' (единица доверия) - применяется для оценки доверия и чести личности.";
		else if (this.key == 4)
			return "'Полезная единица' (единица пользы) - применяется для оценки пользы личности.";
		else if (this.key == 5)
			return "'Деловая единица' (единица деловитости) - применяется для оценки деловитости и активности личности.";
		 */

        return this.description;
    }

    @Override
    public String viewDescription() {
        switch ((int) this.key) {
            case 1:
                return "<p><img src=\"data:image/png;base64,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\" alt=\"\" width=\"275\" height=\"100\" /></p>\n" +
                        "<p><strong>FOIL</strong> is the official native digital accounting unit. In the Blockchain FOIL Network,&nbsp;&nbsp; Foil gives its holder additional rights to manage and govern the ecosystem.</p>\n" +
                        "<p>Native token FOIL has a set of features:</p>" +
                        "<p>&bull; Earn MVolt<br />&bull; Perform on chain creator transactions<br />&bull; FOIL governance and voting<br />&bull; Participate in FOIL launchpad IDOs<br />&bull; Participate in staking and liquidity mining<br />&bull; Total Supply 100 000 000</p>" +
                        "<p style=\"text-align: left;\"><em>Source: </em><a href=\"https://foil.network/\">FOIL Network</a></p>" +
                        "<p style=\"text-align: left;\">&nbsp;</p>" +
                        "<p style=\"text-align: left;\"><a href=\"https://scan.foil.network/\">FOIL DataVision</a> (block explorer)</p>" +
                        "<p style=\"text-align: left;\"><a href=\"https://oldscan.foil.network/index/blockexplorer.html?blocks\">FOIL old block explorer</a></p>" +
                        "<p style=\"text-align: left;\"><em>Source: <a href=\"https://foil.network/\">FOIL Network</a></em></p>" +
                        "<p>&nbsp;</p>";
            case 2:
                return "<p><img src=\"data:image/png;base64,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\" alt=\"\" width=\"275\" height=\"100\" /></p>" +
                        "<p><strong>MVolt</strong> is a stable token and the energy of the platform. FOIL Network charges MVolt for the operations, preventing the abuse of node resources. It is used to pay for transactions, and it is given as a reward for building blocks.</p>" +
                        "<p>Native token FOIL has a set of features:</p>" +
                        "<p>&bull; Genesis supply 1 000 000 = 1Tb of Data<br />&bull; Additional supply will be created through<br />public key verification</p>" +
                        "<p style=\"text-align: left;\"><em>Source: </em><a href=\"https://foil.network/\">FOIL Network</a></p>" +
                        "<p style=\"text-align: left;\">&nbsp;</p>" +
                        "<p style=\"text-align: left;\"><a href=\"https://scan.foil.network/\">FOIL DataVision</a> (block explorer)</p>" +
                        "<p style=\"text-align: left;\"><a href=\"https://oldscan.foil.network/index/blockexplorer.html?blocks\">FOIL old block explorer</a></p>" +
                        "<p style=\"text-align: left;\"><em>Source: <a href=\"https://foil.network/\">FOIL Network</a></em></p>" +
                        "<p>&nbsp;</p>";
            case 18: // DOGE
                try {
                    return new String(Files.readAllBytes(Paths.get("images/icons/assets/DOGE.txt"))) ;
                } catch (Exception e) {
                }
                return "";
            case 20: // LTC
                try {
                    return new String(Files.readAllBytes(Paths.get("images/icons/assets/LTC.txt"))) ;
                } catch (Exception e) {
                }
                return "";
            case 22: // LTC
                try {
                    return new String(Files.readAllBytes(Paths.get("images/icons/assets/DASH.txt"))) ;
                } catch (Exception e) {
                }
                return "";

        }

        return this.description;
    }

    @Override
    public String[] getTags() {
        return new String[]{":" + viewAssetTypeAbbrev().toLowerCase()};
    }

    @Override
    public byte[] getIcon() {
        switch ((int) key) {
            case 1:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/FOIL.png"));
                } catch (Exception e) {
                }
                return icon;
            case 2:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/MVolt.png"));
                } catch (Exception e) {
                }
                return icon;
            case (int) BTC_KEY:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/BTC.gif"));
                } catch (Exception e) {
                }
                return icon;
            case 14:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/ETH.png"));
                } catch (Exception e) {
                }
                return icon;
            case 16:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/BNB.png"));
                } catch (Exception e) {
                }
                return icon;
            case 18:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/DOGE.png"));
                } catch (Exception e) {
                }
                return icon;
            case 20:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/LTC.png"));
                } catch (Exception e) {
                }
                return icon;
            case 22:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/DASH.png"));
                } catch (Exception e) {
                }
                return icon;
            case 24:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/ZEN.png"));
                } catch (Exception e) {
                }
                return icon;
            case 1643:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/RUB.png"));
                } catch (Exception e) {
                }
                return icon;
            case 1156:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/CNY.png"));
                } catch (Exception e) {
                }
                return icon;
            case (int) EUR_KEY:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/EUR.png"));
                } catch (Exception e) {
                }
                return icon;
            case (int) USD_KEY:
                try {
                    icon = Files.readAllBytes(Paths.get("images/icons/assets/USD.png"));
                } catch (Exception e) {
                }
                return icon;
        }
        return icon;
    }

    @Override
    public byte[] getImage() {
        if (key < 1000 && image.length > 0)
            return new byte[0];

        return image;
    }

    public abstract long getQuantity();

    public abstract int getScale();

    public static ExLinkAddress[] getDefaultDEXAwards(int type, Account owner) {
        if (type == AS_NON_FUNGIBLE) {
            return new ExLinkAddress[]{new ExLinkAddress(owner, 10000, "Author royalty")};
        }
        return null;
    }

    public ExLinkAddress[] getDEXAwards() {
        if ((flags & APP_DATA_DEX_AWARDS_MASK) == 0) {
            return getDefaultDEXAwards(assetType, maker);
        }
        return dexAwards;
    }

    @Override
    public HashMap getNovaItems() {
        return BlockChain.NOVA_ASSETS;
    }

    public boolean hasDEXAwards() {
        return (flags & APP_DATA_DEX_AWARDS_MASK) != 0;
    }

    public boolean isMovable() {
        if (false && this.key < BlockChain.AMOUNT_SCALE_FROM) {
            return (this.typeBytes[1] & (byte) 1) > 0;
        }
        return this.assetType == AS_OUTSIDE_GOODS;
    }

    public boolean isImMovable() {
        if (false && this.key < BlockChain.AMOUNT_SCALE_FROM) {
            return (this.typeBytes[1] & (byte) 1) <= 0;
        }
        return this.assetType == AS_OUTSIDE_IMMOVABLE;
    }

    public boolean isInsideType() {
        return this.assetType == AS_INSIDE_ASSETS
                || this.assetType >= AS_INSIDE_CURRENCY
                && this.assetType <= AS_INSIDE_OTHER_CLAIM;
    }

    public boolean isInsideCurrency() {
        return this.assetType == AS_INSIDE_CURRENCY;
    }

    public boolean isInsideUtility() {
        return this.assetType == AS_INSIDE_UTILITY;
    }

    public boolean isInsideShare() {
        return this.assetType == AS_INSIDE_SHARE;
    }

    public boolean isInsideBonus() {
        return this.assetType == AS_INSIDE_BONUS;
    }

    public boolean isInsideAccess() {
        return this.assetType == AS_INSIDE_ACCESS;
    }

    public boolean isInsideVote() {
        return this.assetType == AS_INSIDE_VOTE;
    }

    public boolean isIndex() {
        return this.assetType == AS_INDEX;
    }

    public boolean isInsideOtherClaim() {
        return this.assetType == AS_INSIDE_OTHER_CLAIM;
    }

    /**
     * Их нельзя вернуть из долга самостоятельно
     *
     * @return
     */
    public boolean isNotReDebted() {
        return isOutsideType();
    }

    public boolean isOutsideType() {
        return isOutsideType(this.assetType);
    }

    public static boolean isOutsideType(int assetType) {
        return // ?? this.assetType == AS_OUTSIDE_GOODS ||
                assetType >= AS_OUTSIDE_CURRENCY
                        && assetType <= AS_OUTSIDE_OTHER_CLAIM;
    }

    public boolean isOutsideCurrency() {
        return this.assetType == AS_OUTSIDE_CURRENCY;
    }

    public boolean isOutsideService() {
        return this.assetType == AS_OUTSIDE_SERVICE;
    }

    public boolean isOutsideShare() {
        return this.assetType == AS_OUTSIDE_SHARE;
    }

    public boolean isOutsideBill() {
        return this.assetType == AS_OUTSIDE_BILL;
    }

    public boolean isOutsideBillEx() {
        return this.assetType == AS_OUTSIDE_BILL_EX;
    }

    public boolean isOutsideOtherClaim() {
        return this.assetType == AS_OUTSIDE_OTHER_CLAIM;
    }

    public static boolean isUnHoldable(long key, int assetType) {
        if (key < getStartKey(ItemCls.ASSET_TYPE, START_KEY_OLD, MIN_START_KEY_OLD)
                || assetType == AS_INSIDE_ASSETS
                || assetType > AS_OUTSIDE_OTHER_CLAIM
                && assetType <= AS_INSIDE_OTHER_CLAIM
        ) {
            return true;
        }
        return false;
    }

    public boolean isUnHoldable() {
        return isUnHoldable(key, assetType);
    }

    public static boolean isUnSpendable(long key, int assetType) {
        return key < 100
                || assetType == AssetCls.AS_INDEX
                || assetType == AssetCls.AS_INSIDE_ACCESS
                || assetType == AssetCls.AS_INSIDE_BONUS;
    }

    public boolean isUnSpendable() {
        return isUnSpendable(key, assetType);
    }

    public static boolean isUnTransferable(long key, int assetType, boolean senderIsAssetMaker) {
        return assetType == AssetCls.AS_NON_FUNGIBLE && !senderIsAssetMaker;
    }

    public boolean isUnTransferable(boolean senderIsAssetMaker) {
        return isUnTransferable(key, assetType, senderIsAssetMaker);
    }

    public boolean validPair(long pairAssetKey) {
        if (assetType == AssetCls.AS_NON_FUNGIBLE) {
            if (pairAssetKey != AssetCls.ERA_KEY
                && pairAssetKey != AssetCls.FEE_KEY
                && pairAssetKey != AssetCls.BTC_KEY
                && pairAssetKey != 18
            ) {
                return false;
            }
        }

        return true;
    }

    public static boolean isUnDebtable(long key, int assetType) {
        return assetType == AssetCls.AS_INDEX
                || assetType == AssetCls.AS_INSIDE_BONUS;
    }

    public boolean isUnDebtable() {
        return isUnDebtable(key, assetType);
    }

    public static boolean isTypeUnique(int assetType, long quantity) {
        if (quantity == 1L
                || assetType == AS_OUTSIDE_BILL
                || assetType == AS_OUTSIDE_BILL_EX
                || assetType == AS_BANK_GUARANTEE
                || assetType == AS_NON_FUNGIBLE
        ) {
            return true;
        }
        return false;
    }

    public abstract boolean isUnique();

    public abstract boolean isUnlimited(Account address, boolean notAccounting);

    /**
     * Управлять может только сам обладатель
     *
     * @return
     */
    public boolean isSelfManaged() {
        return assetType >= AS_SELF_MANAGED_ACCOUNTING;
    }

    /**
     * Активы у которых есть только 4-ре баланса и каждый из них имеет возможность забрать - backward
     *
     * @return
     */
    public boolean isDirectBalances() {
        return assetType >= AS_SELF_MANAGED_ACCOUNTING;
    }

    public static boolean isAccounting(int assetType) {
        return assetType >= AS_ACCOUNTING;
    }

    public boolean isAccounting() {
        return isAccounting(assetType);
    }

    public boolean isSendPersonProtected() {
        return (key <= AssetCls.ERA_KEY || key > getStartKey()) // GATE Assets
                && assetType != AssetCls.AS_NON_FUNGIBLE
                && !isAccounting()
                && assetType != AssetCls.AS_INSIDE_BONUS
                && assetType != AssetCls.AS_INSIDE_VOTE;
    }

    /**
     * Actions on OWN balance will update DEBT balance too
     *
     * @return
     */
    public boolean isChangeDebtBySendActions() {
        return this.assetType == AS_SELF_ACCOUNTING_CASH_FUND;
    }

    public static boolean isChangeDebtBySpendActions(int assetType) {
        return isOutsideType(assetType);
    }

    public boolean isChangeDebtBySpendActions() {
        return isChangeDebtBySpendActions(this.assetType);
    }

    /**
     * Если обратный Послать то в меню местами меняем
     *
     * @return
     */
    public static boolean isReverseSend(int assetType) {
        return assetType == AS_SELF_MANAGED_ACCOUNTING
                || assetType == AS_SELF_ACCOUNTING_MUTUAL_AID_FUND
                || assetType == AS_SELF_ACCOUNTING_CASH_FUND;
    }

    public boolean isReverseSend() {
        return isReverseSend(this.assetType);
    }


    /**
     * в обычном сотоянии тут отрицательные балансы или нет?
     *
     * @param balPos
     * @return
     */
    public static boolean isReverseBalancePos(int assetType, int balPos) {

        switch (balPos) {
            case Account.BALANCE_POS_OWN:
                return isReverseSend(assetType);
            case Account.BALANCE_POS_SPEND:
                return true;
        }
        return false;
    }

    /**
     * в обычном сотоянии тут отрицательные балансы или нет?
     *
     * @param balPos
     * @return
     */
    public boolean isReverseBalancePos(int balPos) {
        return isReverseBalancePos(this.assetType, balPos);
    }

    public BigDecimal defaultAmountAssetType() {
        switch (assetType) {
            case AS_BANK_GUARANTEE:
            case AS_NON_FUNGIBLE:
                return BigDecimal.ONE;
        }
        return isUnique() ? BigDecimal.ONE : null;
    }

    public PublicKeyAccount defaultRecipient(int actionType, boolean backward) {

        if (isOutsideType()) {
            if (actionType == Account.BALANCE_POS_SPEND
                    || actionType == Account.BALANCE_POS_DEBT) {
                return getMaker();
            }
        }

        return null;
    }

    public static String viewAssetTypeCls(int assetType) {
        switch (assetType) {
            case AS_OUTSIDE_GOODS:
                return "Movable";
            case AS_OUTSIDE_IMMOVABLE:
                return "Immovable";

            case AS_OUTSIDE_CURRENCY:
                return "Outside Currency";
            case AS_OUTSIDE_WORK_TIME_HOURS:
                return "Work Time [hours]";
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                return "Work Time [minutes]";
            case AS_OUTSIDE_SERVICE:
                return "Outside Service";
            case AS_OUTSIDE_SHARE:
                return "Outside Share";
            case AS_OUTSIDE_BILL:
                return "AS_OUTSIDE_BILL_N";
            case AS_OUTSIDE_BILL_EX:
                return "Bill of exchange";
            case AS_MY_DEBT:
                return "AS_MY_DEBT_N";
            case AS_OUTSIDE_OTHER_CLAIM:
                return "Outside Other Claim";

            case AS_INSIDE_ASSETS:
                return "Digital Asset";
            case AS_INSIDE_CURRENCY:
                return "Digital Currency";
            case AS_INSIDE_UTILITY:
                return "Digital Utility";
            case AS_INSIDE_SHARE:
                return "Digital Share";
            case AS_INSIDE_BONUS:
                return "Digital Bonus";
            case AS_INSIDE_ACCESS:
                return "Digital Access";
            case AS_INSIDE_VOTE:
                return "Digital Vote";
            case AS_BANK_GUARANTEE:
                return "Bank Guarantee";
            case AS_BANK_GUARANTEE_TOTAL:
                return "Accounting Bank Guarantee";
            case AS_NON_FUNGIBLE:
                return "Non Fungible Token";
            case AS_INDEX:
                return "Index";
            case AS_INSIDE_OTHER_CLAIM:
                return "Other Digital Claim";

            case AS_ACCOUNTING:
                return "Accounting";
            case AS_SELF_MANAGED_ACCOUNTING:
                return "Self Managed";
            case AS_SELF_ACCOUNTING_LOAN:
                return "AS_SELF_ACCOUNTING_LOAN_N";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                return "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_N";
            case AS_SELF_ACCOUNTING_CASH_FUND:
                return "AS_SELF_ACCOUNTING_CASH_FUND_N";
            case AS_SELF_MANAGED_DIRECT_SEND:
                return "AS_SELF_MANAGED_DIRECT_SEND_N";
            case AS_SELF_MANAGED_SHARE:
                return "AS_SELF_MANAGED_SHARE_N";

        }
        return null;
    }

    public String viewAssetType() {
        return viewAssetTypeCls(this.assetType);
    }

    public static String viewAssetTypeFullCls(int assetType) {
        switch (assetType) {
            case AS_OUTSIDE_GOODS:
                return "Movable Goods";
            case AS_OUTSIDE_IMMOVABLE:
                return "Immovable Goods, Real Estate";
            case AS_OUTSIDE_CURRENCY:
                return "Outside Currency";
            case AS_OUTSIDE_WORK_TIME_HOURS:
                return "Work Time [hours]";
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                return "Work Time [minutes]";
            case AS_OUTSIDE_SERVICE:
                return "Outside Service";
            case AS_OUTSIDE_SHARE:
                return "Outside Share Rights";
            case AS_OUTSIDE_BILL:
                return "AS_OUTSIDE_BILL_NF";
            case AS_OUTSIDE_BILL_EX:
                return "Bill of Exchange";
            case AS_MY_DEBT:
                return "AS_MY_DEBT_NF";
            case AS_OUTSIDE_OTHER_CLAIM:
                return "Other Outside Right of Claim";

            case AS_INSIDE_ASSETS:
                return "Digital Asset";
            case AS_INSIDE_CURRENCY:
                return "Digital Currency";
            case AS_INSIDE_UTILITY:
                return "Digital Utility";
            case AS_INSIDE_SHARE:
                return "Digital Share";
            case AS_INSIDE_BONUS:
                return "Bonuses, Loyalty Points";
            case AS_INSIDE_ACCESS:
                return "Digital Access Rights";
            case AS_INSIDE_VOTE:
                return "Digital Vote";
            case AS_BANK_GUARANTEE:
                return "Bank Guarantee";
            case AS_BANK_GUARANTEE_TOTAL:
                return "Accounting Bank Guarantee";
            case AS_NON_FUNGIBLE:
                return "Non Fungible Token";
            case AS_INDEX:
                return "Digital Index";
            case AS_INSIDE_OTHER_CLAIM:
                return "Other Digital Right of Claim";

            case AS_ACCOUNTING:
                return "Accounting";
            case AS_SELF_MANAGED_ACCOUNTING:
                return "Self Managed for Accounting";
            case AS_SELF_ACCOUNTING_LOAN:
                return "AS_SELF_ACCOUNTING_LOAN_NF";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                return "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_NF";
            case AS_SELF_ACCOUNTING_CASH_FUND:
                return "AS_SELF_ACCOUNTING_CASH_FUND_NF";
            case AS_SELF_MANAGED_DIRECT_SEND:
                return "AS_SELF_MANAGED_DIRECT_SEND_NF";
            case AS_SELF_MANAGED_SHARE:
                return "AS_SELF_MANAGED_SHARE_NF";

        }
        return null;
    }

    public static String viewAssetTypeFullClsAndChars(int assetType) {
        return charAssetType(Long.MAX_VALUE, assetType) + viewAssetTypeAbbrev(assetType) + ":" + viewAssetTypeFullCls(assetType);
    }

    public String viewAssetTypeFullClsAndChars() {
        return charAssetType(Long.MAX_VALUE, assetType) + viewAssetTypeAbbrev(assetType) + ":" + viewAssetTypeFullCls(assetType);
    }

    public static String viewAssetTypeAbbrev(int asset_type) {
        switch (asset_type) {
            case AS_OUTSIDE_GOODS:
                return "OGd";
            case AS_OUTSIDE_IMMOVABLE:
                return "UIm";
            case AS_OUTSIDE_CURRENCY:
                return "OCr";
            case AS_OUTSIDE_WORK_TIME_HOURS:
                return "WH";
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                return "WM";
            case AS_OUTSIDE_SERVICE:
                return "OSv";
            case AS_OUTSIDE_SHARE:
                return "OSh";
            case AS_OUTSIDE_BILL:
                return "PNo"; // Promissory Note";
            case AS_OUTSIDE_BILL_EX:
                return "BEx"; //Bill of Exchange";
            case AS_MY_DEBT:
                return "Dbt"; // Debt to Loaner
            case AS_OUTSIDE_OTHER_CLAIM:
                return "OCl";

            case AS_INSIDE_ASSETS:
                return "Ast";
            case AS_INSIDE_CURRENCY:
                return "Cur";
            case AS_INSIDE_UTILITY:
                return "Utl";
            case AS_INSIDE_SHARE:
                return "Shr";
            case AS_INSIDE_BONUS:
                return "Bon";
            case AS_INSIDE_ACCESS:
                return "Rit";
            case AS_INSIDE_VOTE:
                return "Vte";
            case AS_BANK_GUARANTEE:
                return "BGu";
            case AS_BANK_GUARANTEE_TOTAL:
                return "BGuT";
            case AS_NON_FUNGIBLE:
                return "NFT";
            case AS_INDEX:
                return "Idx";
            case AS_INSIDE_OTHER_CLAIM:
                return "CLM";

            case AS_ACCOUNTING:
                return "Acc";
            case AS_SELF_MANAGED_ACCOUNTING:
                return "SAcc";
            case AS_SELF_ACCOUNTING_LOAN:
                return "AccL";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                return "AccAF";
            case AS_SELF_ACCOUNTING_CASH_FUND:
                return "AccCF";
            case AS_SELF_MANAGED_DIRECT_SEND:
                return "AccDS";
            case AS_SELF_MANAGED_SHARE:
                return "AccSh";
        }
        return "?";
    }

    public String viewAssetTypeAbbrev() {
        return viewAssetTypeAbbrev(assetType);
    }

    public String viewAssetTypeFull() {
        return viewAssetTypeFullCls(this.assetType);
    }

    public static String viewAssetTypeDescriptionCls(int assetType) {
        switch (assetType) {
            case AS_OUTSIDE_GOODS:
                return "Movable things and goods. These goods can be taken for storage by the storekeeper or for confirmation of delivery. In this case you can see the balances on the accounts of storekeepers and delivery agents";
            case AS_OUTSIDE_IMMOVABLE:
                return "Real estate and other goods and things not subject to delivery. Such things can be taken and given for rent and handed over to the guard";
            case AS_OUTSIDE_CURRENCY:
                return "AS_OUTSIDE_CURRENCY_D";
            case AS_OUTSIDE_WORK_TIME_HOURS:
                return "AS_OUTSIDE_WORK_TIME_HOURS_D";
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                return "AS_OUTSIDE_WORK_TIME_MINUTES_D";
            case AS_OUTSIDE_SERVICE:
                return "An external service that needs to be provided outside. To notify your wish to provide services you must make demands and then confirm the fulfillment";
            case AS_OUTSIDE_SHARE:
                return "External shares which have to be transferred to an external depository. The depositary can be notified by presenting the claim and then confirm the shares transfer";
            case AS_OUTSIDE_BILL:
                return "AS_OUTSIDE_BILL_D";
            case AS_OUTSIDE_BILL_EX:
                return "A digital bill of exchange can be called for redemption by external money. You can take it into your hands";
            case AS_MY_DEBT:
                return "AS_MY_DEBT_D";
            case AS_OUTSIDE_OTHER_CLAIM:
                return "Other external rights, requirements and obligations. Any obligation (as well as other external assets), which can be claimed by the record \"summon\" and discharged by the record \"confirmation of fulfillment\" of this obligation. You can take it into your hands";
            case AS_INSIDE_ASSETS:
                return "Internal (digital) asset. It does not require any external additional actions when transferring between accounts inside Erachain";
            case AS_INSIDE_CURRENCY:
                return "Digital money";
            case AS_INSIDE_UTILITY:
                return "Digital service or a cost is something that can be used inside Erachain nvironment, for example as a payment for external services";
            case AS_INSIDE_SHARE:
                return "Digital share. The share of ownership of an external or internal enterpris, the possession of which establishes the right to own the corresponding share of the enterprise without the need to take any external actions";
            case AS_INSIDE_BONUS:
                return "Digital loyalty points, bonuses, awards, discount points (bonus). It has no generally accepted value and can not be exchanged for other types of assets inside the Erachain environment. The exchange for other bonuses and rewards are allowed";
            case AS_INSIDE_ACCESS:
                return "Digital rights of access and control, membership, pass";
            case AS_INSIDE_VOTE:
                return "A digital voice for voting";
            case AS_BANK_GUARANTEE:
                return "A digital bank guarantee";
            case AS_BANK_GUARANTEE_TOTAL:
                return "A digital accounting bank guarantee";
            case AS_NON_FUNGIBLE:
                return "AS_NON_FUNGIBLE_D";
            case AS_INDEX:
                return "Index on foreign and domestic assets, for example currencies on FOREX";
            case AS_INSIDE_OTHER_CLAIM:
                return "Other digital rights, requirements and obligations. These assets (as well as other digital assets) can be given in debt and seized by the lender";
            case AS_ACCOUNTING:
                return "AS_ACCOUNTING_D";
            case AS_SELF_MANAGED_ACCOUNTING:
                return "AS_SELF_MANAGED_ACCOUNTING_D";
            case AS_SELF_ACCOUNTING_LOAN:
                return "AS_SELF_ACCOUNTING_LOAN_D";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                return "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_D";
            case AS_SELF_ACCOUNTING_CASH_FUND:
                return "AS_SELF_ACCOUNTING_CASH_FUND_D";
            case AS_SELF_MANAGED_DIRECT_SEND:
                return "AS_SELF_MANAGED_DIRECT_SEND_D";
            case AS_SELF_MANAGED_SHARE:
                return "AS_SELF_MANAGED_SHARE_D";

        }
        return "";
    }

    public static String viewAssetTypeDescriptionDEX(int assetType, long key) {
        if (key < 100)
            return "AS_CURRENCY_100_DEX";

        switch (assetType) {
            case AS_NON_FUNGIBLE:
                return "AS_NON_FUNGIBLE_DEX";
        }
        return null;
    }

    public static String viewAssetTypeAction(long assetKey, int assetType, boolean backward, int actionType, boolean isCreatorMaker) {
        switch (assetType) {
            case AS_OUTSIDE_IMMOVABLE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "Transfer to the ownership ";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "Confiscate from rent" : "Transfer to rent";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "Return from rent";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "The employment security/received from security" : null;
                    default:
                        return null;
                }
            case AS_OUTSIDE_CURRENCY:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null // для формирования списка действия надо выдать НУЛЬ
                                : isCreatorMaker ? "AS_OUTSIDE_CURRENCY_Issue" : "AS_OUTSIDE_CURRENCY_1";
                    case Account.BALANCE_POS_DEBT:
                        return isCreatorMaker ? null
                                : backward ? "AS_OUTSIDE_CURRENCY_2B" // Отозвать требование об исполнении денежного требования
                                : "AS_OUTSIDE_CURRENCY_2"; // Потребовать исполнения денежного требования
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null
                                : isCreatorMaker ? null
                                : "AS_OUTSIDE_CURRENCY_4"; // Подтвердить исполнение денежного требования
                    default:
                        return null;
                }
            case AS_OUTSIDE_WORK_TIME_HOURS:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "AS_OUTSIDE_WORK_TIME_HOURS_1"; // Передать в собственность рабочие часы
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_OUTSIDE_WORK_TIME_HOURS_2B" // Отозвать требование траты рабочих часов
                                : "AS_OUTSIDE_WORK_TIME_HOURS_2"; // Потребовать потратить рабочие часы
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_OUTSIDE_WORK_TIME_HOURS_4"; // Подтвердить затраты рабочих часов
                    default:
                        return null;
                }
            case AS_OUTSIDE_WORK_TIME_MINUTES:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "AS_OUTSIDE_WORK_TIME_MINUTES_1"; // Передать в собственность рабочие минуты
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_OUTSIDE_WORK_TIME_MINUTES_2B" // Отозвать требование траты рабочих минут
                                : "AS_OUTSIDE_WORK_TIME_MINUTES_2"; // Потребовать потратить рабочие минуты
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_OUTSIDE_WORK_TIME_MINUTES_4"; // Подтвердить затраты рабочих минут
                    default:
                        return null;
                }
            case AS_OUTSIDE_SERVICE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "Transfer Service Requirement";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "To reduce the provision of services" // Отозвать требование в предоставлении услуг
                                : "To require the provision of services";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "Confirm the provision of services";
                    default:
                        return null;
                }
            case AS_OUTSIDE_SHARE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "To transfer shares in the property";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "To reduce the transfer of shares"
                                : "To require the transfer of shares";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "Return debt";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "Confirm receipt of shares";
                    default:
                        return null;
                }
            case AS_OUTSIDE_BILL:
            case AS_OUTSIDE_BILL_EX:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "AS_OUTSIDE_BILL_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_OUTSIDE_BILL_2B"
                                : "AS_OUTSIDE_BILL_2";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_OUTSIDE_BILL_4";
                    default:
                        return null;
                }
            case AS_MY_DEBT:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null
                                : isCreatorMaker ? "AS_MY_DEBT_Issue" : "AS_MY_DEBT_1";
                    case Account.BALANCE_POS_DEBT:
                        return isCreatorMaker ? null // эмитент долга не может делать требования
                                : backward ? "AS_MY_DEBT_2B"
                                : "AS_MY_DEBT_2";
                    case Account.BALANCE_POS_SPEND:
                        return isCreatorMaker ? null // эмитент долга не может делать погашения
                                : backward ? null : "AS_MY_DEBT_4";
                    default:
                        return null;
                }
            case AS_OUTSIDE_OTHER_CLAIM:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_OUTSIDE_OTHER_CLAIM_Issue"
                                : "AS_OUTSIDE_OTHER_CLAIM_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_OUTSIDE_OTHER_CLAIM_2B"
                                : "AS_OUTSIDE_OTHER_CLAIM_2";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_OUTSIDE_OTHER_CLAIM_4";
                    default:
                        return null;
                }
            case AS_INSIDE_CURRENCY:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_CURRENCY_Issue"
                                : "AS_INSIDE_CURRENCY_1";
                    default:
                        return null;
                }
            case AS_INSIDE_UTILITY:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_UTILITY_Issue"
                                : "AS_INSIDE_UTILITY_1";
                    default:
                        return null;
                }
            case AS_INSIDE_SHARE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_SHARE_Issue"
                                : "AS_INSIDE_SHARE_1";
                    default:
                        return null;
                }
            case AS_INSIDE_BONUS:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_BONUS_Issue"
                                : "AS_INSIDE_BONUS_1";
                    default:
                        return null;
                }
            case AS_INSIDE_ACCESS:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_ACCESS_Issue" : "AS_INSIDE_ACCESS_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_INSIDE_ACCESS_2B"
                                : "AS_INSIDE_ACCESS_2";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "AS_INSIDE_ACCESS_2R";
                    case Account.BALANCE_POS_SPEND:
                        return "AS_INSIDE_ACCESS_4";
                    default:
                        return null;
                }
            case AS_INSIDE_VOTE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_VOTE_Issue" : "AS_INSIDE_VOTE_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_INSIDE_VOTE_2B"
                                : "AS_INSIDE_VOTE_2";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "AS_INSIDE_VOTE_2R";
                    case Account.BALANCE_POS_SPEND:
                        return "AS_INSIDE_VOTE_4";
                    default:
                        return null;
                }
            case AS_BANK_GUARANTEE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_BANK_GUARANTEE_Issue" : "AS_BANK_GUARANTEE_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_BANK_GUARANTEE_2B" : "AS_BANK_GUARANTEE_2";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "AS_BANK_GUARANTEE_2R";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "AS_BANK_GUARANTEE_3" : null;
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_BANK_GUARANTEE_4";
                    default:
                        return null;
                }
            case AS_BANK_GUARANTEE_TOTAL:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_BANK_GUARANTEE_TOTAL_Issue" : "AS_BANK_GUARANTEE_TOTAL_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_BANK_GUARANTEE_TOTAL_2B" : "AS_BANK_GUARANTEE_TOTAL_2";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return backward ? null : "AS_BANK_GUARANTEE_TOTAL_2R";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_BANK_GUARANTEE_TOTAL_4";
                    default:
                        return null;
                }
            case AS_NON_FUNGIBLE: {
                if (actionType == Account.BALANCE_POS_OWN) {
                    return backward ? null : isCreatorMaker ? "AS_NON_FUNGIBLE_Issue" : null;
                }
                return null;
            }
            case AS_INDEX:
                break;
            case AS_INSIDE_OTHER_CLAIM:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "AS_INSIDE_OTHER_CLAIM_Issue" : "AS_INSIDE_OTHER_CLAIM_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_INSIDE_OTHER_CLAIM_2B"
                                : "AS_INSIDE_OTHER_CLAIM_2";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? null : "AS_INSIDE_OTHER_CLAIM_4";
                    default:
                        return null;
                }
            case AS_ACCOUNTING:
                break;
            case AS_SELF_MANAGED_ACCOUNTING:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_MANAGED_ACCOUNTING_1B" : "AS_SELF_MANAGED_ACCOUNTING_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_SELF_MANAGED_ACCOUNTING_2B" : "AS_SELF_MANAGED_ACCOUNTING_2";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "AS_SELF_MANAGED_ACCOUNTING_3B" : "AS_SELF_MANAGED_ACCOUNTING_3";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? "AS_SELF_MANAGED_ACCOUNTING_4B" : "AS_SELF_MANAGED_ACCOUNTING_4";
                    default:
                        return null;
                }
            case AS_SELF_ACCOUNTING_LOAN:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_ACCOUNTING_LOAN_1B" : "AS_SELF_ACCOUNTING_LOAN_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_SELF_ACCOUNTING_LOAN_2B" : "AS_SELF_ACCOUNTING_LOAN_2";
                    case Account.BALANCE_POS_HOLD:
                        // SPEND нельзя брать так как он Баланс Мой изменит у меня
                        return backward ? "AS_SELF_ACCOUNTING_LOAN_3B" : "AS_SELF_ACCOUNTING_LOAN_3";
                    default:
                        return null;
                }
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_1B" : "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_1";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_4B" : "AS_SELF_ACCOUNTING_MUTUAL_AID_FUND_4";
                    default:
                        return null;
                }
            case AS_SELF_ACCOUNTING_CASH_FUND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_ACCOUNTING_CASH_FUND_1B" : "AS_SELF_ACCOUNTING_CASH_FUND_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_SELF_ACCOUNTING_CASH_FUND_2B" : "AS_SELF_ACCOUNTING_CASH_FUND_2";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? "AS_SELF_ACCOUNTING_CASH_FUND_4B" : "AS_SELF_ACCOUNTING_CASH_FUND_4";
                    default:
                        return null;
                }
            case AS_SELF_MANAGED_DIRECT_SEND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_MANAGED_DIRECT_SEND_1B" : "AS_SELF_MANAGED_DIRECT_SEND_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_SELF_MANAGED_DIRECT_SEND_2B" : "AS_SELF_MANAGED_DIRECT_SEND_2";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "AS_SELF_MANAGED_DIRECT_SEND_3B" : "AS_SELF_MANAGED_DIRECT_SEND_3";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? "AS_SELF_MANAGED_DIRECT_SEND_4B" : "AS_SELF_MANAGED_DIRECT_SEND_4";
                    default:
                        return null;
                }
            case AS_SELF_MANAGED_SHARE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? "AS_SELF_MANAGED_SHARE_1B" : "AS_SELF_MANAGED_SHARE_1";
                    case Account.BALANCE_POS_DEBT:
                        return backward ? "AS_SELF_MANAGED_SHARE_2B" : "AS_SELF_MANAGED_SHARE_2";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "AS_SELF_MANAGED_SHARE_3B" : "AS_SELF_MANAGED_SHARE_3";
                    case Account.BALANCE_POS_SPEND:
                        return backward ? "AS_SELF_MANAGED_SHARE_4B" : "AS_SELF_MANAGED_SHARE_4";
                    default:
                        return null;
                }

        }

        switch (actionType) {
            case Account.BALANCE_POS_OWN:
                return backward ? null : "Transfer to the ownership";
            case Account.BALANCE_POS_DEBT:
                return backward ? "To confiscate a debt"
                        : "Transfer to debt";
            case TransactionAmount.ACTION_REPAY_DEBT:
                return backward ? null : "Return debt";
            case Account.BALANCE_POS_HOLD:
                return isUnHoldable(assetKey, assetType) ? null
                        : backward ? "Confirm acceptance \"in hand\"" : null;
            case Account.BALANCE_POS_SPEND:
                return backward ? null : "Spend";
            case Account.BALANCE_POS_PLEDGE:
                return backward ? null //"Re-pledge"
                        : null; //"Pledge";
            case TransactionAmount.ACTION_RESERVED_6:
                // for CALCULATED TX
                return null; // backward ? "Reserved 6-" : "Reserved 6+";
        }

        return null;
    }

    public String viewAssetTypeAction(boolean backward, int actionType, boolean isCreatorMaker) {
        return viewAssetTypeAction(key, assetType, backward, actionType, isCreatorMaker);
    }

    public static String viewAssetTypeAdditionAction(long assetKey, int assetType, boolean backward, int actionType, boolean isCreatorMaker) {
        switch (assetType) {
            case AS_SELF_ACCOUNTING_CASH_FUND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return "AS_SELF_SEND_ADDITIONAL_ACT_DEBT";
                }
        }

        if (actionType == Account.BALANCE_POS_SPEND && isChangeDebtBySpendActions(assetType)) {
            return "AdditionAction_on_isChangeDebtBySpendActions";
        }

        return null;
    }

    /**
     * isMirrorDebtBySend - same
     *
     * @param backward
     * @param actionType
     * @param isCreatorMaker
     * @return
     */
    public String viewAssetTypeAdditionAction(boolean backward, int actionType, boolean isCreatorMaker) {
        return viewAssetTypeAdditionAction(key, assetType, backward, actionType, isCreatorMaker);
    }

    /**
     * Balance Position + Backward + Action Name
     *
     * @param assetKey
     * @param assetType
     * @param isCreatorMaker
     * @param useAddedActions
     * @return
     */
    public static List<Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String>> viewAssetTypeActionsList(long assetKey,
                                                                                                  int assetType, Boolean isCreatorMaker, boolean useAddedActions) {

        List<Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String>> list = new ArrayList<>();

        String actionStr;
        String addActionStr;
        Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String> item;
        for (int balPos : TransactionAmount.ACTIONS_LIST) {

            boolean backward = !AssetCls.isReverseSend(assetType) || balPos != Account.BALANCE_POS_OWN;

            actionStr = viewAssetTypeAction(assetKey, assetType, !backward, balPos,
                    isCreatorMaker != null ? isCreatorMaker : true);
            if (actionStr != null) {
                item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, !backward), actionStr);
                if (!list.contains(item)) {
                    list.add(item);
                    if (useAddedActions) {
                        addActionStr = viewAssetTypeAdditionAction(assetKey, assetType, !backward, balPos,
                                isCreatorMaker != null ? isCreatorMaker : true);
                        if (addActionStr != null) {
                            item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, !backward), addActionStr);
                            list.add(item);
                        }
                    }
                }
            }

            if (isCreatorMaker == null) {
                actionStr = viewAssetTypeAction(assetKey, assetType, !backward, balPos,
                        false);
                if (actionStr != null) {
                    item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, !backward), actionStr);
                    if (!list.contains(item)) {
                        list.add(item);
                        if (useAddedActions) {
                            addActionStr = viewAssetTypeAdditionAction(assetKey, assetType, !backward, balPos,
                                    false);
                            if (addActionStr != null) {
                                item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, !backward), addActionStr);
                                list.add(item);
                            }
                        }
                    }
                }
            }

            actionStr = viewAssetTypeAction(assetKey, assetType, backward, balPos,
                    isCreatorMaker != null ? isCreatorMaker : true);
            if (actionStr != null) {
                item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, backward), actionStr);
                if (!list.contains(item)) {
                    list.add(item);
                    if (useAddedActions) {
                        addActionStr = viewAssetTypeAdditionAction(assetKey, assetType, backward, balPos,
                                isCreatorMaker != null ? isCreatorMaker : true);
                        if (addActionStr != null) {
                            item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, backward), addActionStr);
                            list.add(item);
                        }
                    }
                }
            }

            if (isCreatorMaker == null) {
                actionStr = viewAssetTypeAction(assetKey, assetType, backward, balPos,
                        false);
                if (actionStr != null) {
                    item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, backward), actionStr);
                    if (!list.contains(item)) {
                        list.add(item);
                        if (useAddedActions) {
                            addActionStr = viewAssetTypeAdditionAction(assetKey, assetType, backward, balPos,
                                    false);
                            if (addActionStr != null) {
                                item = new Fun.Tuple2<>(new Fun.Tuple2<>(balPos, backward), addActionStr);
                                list.add(item);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    public List<Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String>> viewAssetTypeActionsList(Boolean isCreatorMaker, boolean useAddedActions) {
        return viewAssetTypeActionsList(key, assetType, isCreatorMaker, useAddedActions);
    }

    public String viewAssetTypeActionTitle(boolean backward, int actionType, boolean isCreatorMaker) {
        return viewAssetTypeAction(backward, actionType, isCreatorMaker);
    }

    public static String viewAssetTypeCreator(int assetType, boolean backward, int actionType, boolean isCreatorMaker) {
        switch (assetType) {
            case AS_MY_DEBT:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return isCreatorMaker ? "Debtor" : "Lender";
                    case Account.BALANCE_POS_DEBT:
                    case Account.BALANCE_POS_SPEND:
                        return isCreatorMaker ? null // эмитент долга не может делать требования
                                : "Debtor";
                    default:
                        return null;
                }
            case AS_BANK_GUARANTEE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                    case Account.BALANCE_POS_DEBT:
                    case Account.BALANCE_POS_SPEND:
                        return "Guarantee";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return "Beneficiary";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "Beneficiary" : null;
                }
            case AS_BANK_GUARANTEE_TOTAL:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                    case Account.BALANCE_POS_DEBT:
                        return "Guarantee";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return "Beneficiary";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "Beneficiary" : null;
                    case Account.BALANCE_POS_SPEND:
                        return "Spender";
                }
            case AS_NON_FUNGIBLE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : isCreatorMaker ? "Author" : null;
                }
            case AS_SELF_MANAGED_ACCOUNTING:
            case AS_SELF_MANAGED_DIRECT_SEND:
            case AS_SELF_MANAGED_SHARE:
                return "Accountant";
            case AS_SELF_ACCOUNTING_LOAN:
                return "Lender";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
            case AS_SELF_ACCOUNTING_CASH_FUND:
                return "Cashier";
            case AS_OUTSIDE_IMMOVABLE:
            case AS_OUTSIDE_CURRENCY:
            case AS_OUTSIDE_SERVICE:
            case AS_OUTSIDE_SHARE:
            case AS_OUTSIDE_BILL:
            case AS_OUTSIDE_BILL_EX:
            case AS_OUTSIDE_OTHER_CLAIM:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return isCreatorMaker ? "Issuer" : "Sender";
                    case Account.BALANCE_POS_DEBT:
                    case Account.BALANCE_POS_SPEND:
                        return isCreatorMaker ? null : "Issuer";
                    default:
                        return null;
                }
            case AS_INSIDE_ASSETS:
            case AS_INSIDE_CURRENCY:
            case AS_INSIDE_UTILITY:
            case AS_INSIDE_SHARE:
            case AS_INSIDE_BONUS:
            case AS_INSIDE_ACCESS:
            case AS_INSIDE_VOTE:
            case AS_INDEX:
            case AS_INSIDE_OTHER_CLAIM:
            case AS_ACCOUNTING:
        }

        switch (actionType) {
            case Account.BALANCE_POS_OWN:
                return backward ? null : "Sender";
            case Account.BALANCE_POS_DEBT:
                return "Creditor";
            case TransactionAmount.ACTION_REPAY_DEBT:
                return "Debtor";
            case Account.BALANCE_POS_HOLD:
                return backward ? "Taker" : null;
            case Account.BALANCE_POS_SPEND:
                return backward ? null : "Spender";
        }

        return null;
    }

    public String viewAssetTypeCreator(boolean backward, int actionType, boolean isCreatorMaker) {
        return viewAssetTypeCreator(assetType, backward, actionType, isCreatorMaker);
    }

    public static String viewAssetTypeTarget(int assetType, boolean backward, int actionType, boolean isRecipientMaker) {
        switch (assetType) {
            case AS_MY_DEBT:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return isRecipientMaker ? null : "Lender"; // Тут может быть начальная эмиссия к Кредитору и переуступка - тоже кредитору по сути
                    case Account.BALANCE_POS_DEBT:
                    case Account.BALANCE_POS_SPEND:
                        return isRecipientMaker ?
                                "Debtor"
                                : null; // реципиент только эмитент долга;
                    default:
                        return null;
                }
            case AS_BANK_GUARANTEE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return "Recipient";
                    case Account.BALANCE_POS_DEBT:
                        return "Beneficiary";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return "Guarantee";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "Guarantee" : null;
                    case Account.BALANCE_POS_SPEND:
                        return "Spender";
                }
            case AS_BANK_GUARANTEE_TOTAL:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return "Recipient";
                    case Account.BALANCE_POS_DEBT:
                        return "Principal";
                    case TransactionAmount.ACTION_REPAY_DEBT:
                        return "Guarantee";
                    case Account.BALANCE_POS_HOLD:
                        return backward ? "Guarantee" : null;
                    case Account.BALANCE_POS_SPEND:
                        return "Spender";
                }
            case AS_NON_FUNGIBLE:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return backward ? null : "Recipient";
                }
            case AS_SELF_MANAGED_ACCOUNTING:
            case AS_SELF_MANAGED_DIRECT_SEND:
            case AS_SELF_MANAGED_SHARE:
                return "Ledger";
            case AS_SELF_ACCOUNTING_LOAN:
                return "Debtor";
            case AS_SELF_ACCOUNTING_MUTUAL_AID_FUND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return "Benefactor";
                    case Account.BALANCE_POS_SPEND:
                        return "Recipient";
                }
            case AS_SELF_ACCOUNTING_CASH_FUND:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                    case Account.BALANCE_POS_DEBT:
                        return "Participant";
                    case Account.BALANCE_POS_SPEND:
                        return "Recipient";
                }
            case AS_OUTSIDE_IMMOVABLE:
            case AS_OUTSIDE_CURRENCY:
            case AS_OUTSIDE_SERVICE:
            case AS_OUTSIDE_SHARE:
            case AS_OUTSIDE_BILL:
            case AS_OUTSIDE_BILL_EX:
            case AS_OUTSIDE_OTHER_CLAIM:
                switch (actionType) {
                    case Account.BALANCE_POS_OWN:
                        return isRecipientMaker ? "Issuer" : "Recipient";
                    case Account.BALANCE_POS_DEBT:
                    case Account.BALANCE_POS_SPEND:
                        return isRecipientMaker ? "Issuer" : null;
                    default:
                        return null;
                }
            case AS_INSIDE_ASSETS:
            case AS_INSIDE_CURRENCY:
            case AS_INSIDE_UTILITY:
            case AS_INSIDE_SHARE:
            case AS_INSIDE_BONUS:
            case AS_INSIDE_ACCESS:
            case AS_INSIDE_VOTE:
            case AS_INDEX:
            case AS_INSIDE_OTHER_CLAIM:
            case AS_ACCOUNTING:
        }

        switch (actionType) {
            case Account.BALANCE_POS_OWN:
                return "Recipient";
            case Account.BALANCE_POS_DEBT:
                return "Debtor";
            case TransactionAmount.ACTION_REPAY_DEBT:
                return "Creditor";
            case Account.BALANCE_POS_HOLD:
                return "Supplier";
            case Account.BALANCE_POS_SPEND:
                return "Spender";
        }

        return null;
    }

    public String viewAssetTypeTarget(boolean backward, int actionType, boolean isRecipientMaker) {
        return viewAssetTypeTarget(assetType, backward, actionType, isRecipientMaker);

    }

    public String viewAssetTypeActionOK(boolean backward, int actionType, boolean isCreatorMaker) {
        switch (assetType) {
            case AS_OUTSIDE_IMMOVABLE:
            case AS_OUTSIDE_CURRENCY:
            case AS_OUTSIDE_SERVICE:
            case AS_OUTSIDE_SHARE:
            case AS_OUTSIDE_BILL:
            case AS_OUTSIDE_BILL_EX:
            case AS_OUTSIDE_OTHER_CLAIM:
            case AS_INSIDE_ASSETS:
            case AS_INSIDE_CURRENCY:
            case AS_INSIDE_UTILITY:
            case AS_INSIDE_SHARE:
            case AS_INSIDE_BONUS:
            case AS_INSIDE_ACCESS:
            case AS_INSIDE_VOTE:
            case AS_BANK_GUARANTEE:
            case AS_BANK_GUARANTEE_TOTAL:
            case AS_NON_FUNGIBLE:
            case AS_INDEX:
            case AS_INSIDE_OTHER_CLAIM:
            case AS_ACCOUNTING:
        }

        return viewAssetTypeAction(backward, actionType, isCreatorMaker) + " # to";

    }

    public int getOperations(DCSet dcSet) {
        return dcSet.getOrderMap().getCountOrders(key);
    }

    //OTHER
    public static JSONObject AssetTypeJson(int assetType, JSONObject langObj) {

        JSONObject assetTypeJson = new JSONObject();
        assetTypeJson.put("id", assetType);
        assetTypeJson.put("name", Lang.T(AssetCls.viewAssetTypeCls(assetType), langObj));
        assetTypeJson.put("nameFull", Lang.T(AssetCls.viewAssetTypeFullCls(assetType), langObj));

        long startKey = ItemCls.getStartKey(
                AssetCls.ASSET_TYPE, START_KEY_OLD, MIN_START_KEY_OLD);
        List<Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String>> actions = AssetCls.viewAssetTypeActionsList(startKey,
                assetType, null, true);
        StringJoiner joiner = new StringJoiner(", ");
        JSONArray actionsArray = new JSONArray();
        for (Fun.Tuple2<Fun.Tuple2<Integer, Boolean>, String> actionItem : actions) {
            int action = actionItem.a.a;
            boolean backward = actionItem.a.b;

            joiner.add(Lang.T(actionItem.b, langObj));
            JSONObject actionJson = new JSONObject();
            actionJson.put("position", action);
            actionJson.put("backward", backward);
            actionJson.put("name", Lang.T(actionItem.b, langObj));

            String name;
            //// CREATOR
            name = viewAssetTypeCreator(assetType, backward, action, false);
            if (name != null) actionJson.put("creator", Lang.T(name, langObj));

            name = viewAssetTypeCreator(assetType, backward, action, true);
            if (name != null) actionJson.put("creator_owner", Lang.T(name, langObj));

            //////// TARGET
            name = viewAssetTypeTarget(assetType, backward, action, false);
            if (name != null) actionJson.put("target", Lang.T(name, langObj));

            name = viewAssetTypeTarget(assetType, backward, action, true);
            if (name != null) actionJson.put("target_owner", Lang.T(name, langObj));

            actionsArray.add(actionJson);
        }

        assetTypeJson.put("actions", actionsArray);

        String description = Lang.T(AssetCls.viewAssetTypeDescriptionCls(assetType), langObj) + ".<br>";
        if (AssetCls.isReverseSend(assetType)) {
            description += Lang.T("Actions for OWN balance is reversed", langObj) + ".<br>";
        }
        description += "<b>" + Lang.T("Acceptable actions", langObj) + ":</b><br>" + joiner.toString();

        String dexDesc = AssetCls.viewAssetTypeDescriptionDEX(assetType, startKey);
        if (dexDesc != null) {
            description += "<br><b>" + Lang.T("DEX rules and taxes", langObj) + ":</b><br>" + Lang.T(dexDesc, langObj);
        }

        assetTypeJson.put("description", description);

        return assetTypeJson;
    }

    public static JSONObject assetTypesJson;

    public static JSONObject AssetTypesActionsJson() {

        if (assetTypesJson != null)
            return assetTypesJson;

        assetTypesJson = new JSONObject();
        for (String iso : Lang.getInstance().getLangListAvailable().keySet()) {
            JSONObject langObj = Lang.getInstance().getLangJson(iso);
            JSONObject langJson = new JSONObject();
            for (int type : assetTypes()) {
                langJson.put(type, AssetTypeJson(type, langObj));
            }
            assetTypesJson.put(iso, langJson);
        }
        return assetTypesJson;
    }

    public static JSONObject typeJson(int type) {

        String assetTypeName;

        assetTypeName = viewAssetTypeCls(type);
        if (assetTypeName == null)
            return null;

        JSONObject typeJson = new JSONObject();

        JSONObject langObj = Lang.getInstance().getLangJson("en");

        long startKey = getStartKey(ItemCls.ASSET_TYPE, START_KEY_OLD, MIN_START_KEY_OLD);
        typeJson.put("key", type);
        typeJson.put("char", charAssetType(startKey, type));
        typeJson.put("abbrev", viewAssetTypeAbbrev(type));
        typeJson.put("name", Lang.T(assetTypeName, langObj));
        typeJson.put("name_full", Lang.T(viewAssetTypeFullCls(type), langObj));
        typeJson.put("desc", Lang.T(viewAssetTypeDescriptionCls(type), langObj));
        String dexDesc = AssetCls.viewAssetTypeDescriptionDEX(type, startKey);
        if (dexDesc != null) {
            typeJson.put("desc_DEX", Lang.T(dexDesc, langObj));
        }

        return typeJson;
    }

    public static JSONArray typesJson() {

        JSONArray types = new JSONArray();

        for (int i = 0; i < 256; i++) {
            JSONObject json = typeJson(i);
            if (json == null)
                continue;

            types.add(json);
        }
        return types;
    }

    public String viewProperties(JSONObject langObj) {

        StringJoiner joiner = new StringJoiner(", ");

        if (isImMovable())
            joiner.add(Lang.T("ImMovable", langObj));
        if (isUnlimited(maker, false))
            joiner.add(Lang.T("Unlimited", langObj));
        if (isAccounting())
            joiner.add(Lang.T("Accounting", langObj));
        if (isUnique())
            joiner.add(Lang.T("Unique", langObj));
        if (isUnHoldable())
            joiner.add(Lang.T("Not holdable", langObj));
        if (isOutsideType())
            joiner.add(Lang.T("Outside Claim", langObj));
        if (isSelfManaged())
            joiner.add(Lang.T("Self Managed", langObj));
        if (isChangeDebtBySendActions())
            joiner.add(Lang.T("isChangeDebtBySendActions", langObj));
        if (isChangeDebtBySpendActions())
            joiner.add(Lang.T("isChangeDebtBySpendActions", langObj));
        if (isDirectBalances())
            joiner.add(Lang.T("isDirectBalances", langObj));
        if (isNotReDebted())
            joiner.add(Lang.T("isNotReDebted", langObj));
        if (isOutsideOtherClaim())
            joiner.add(Lang.T("isOutsideOtherClaim", langObj));
        if (isReverseSend())
            joiner.add(Lang.T("isReverseSend", langObj));

        return joiner.toString();
    }

    public int isValid() {
        if (hasDEXAwards()) {

            if (isAccounting()) {
                errorValue = "Award is denied for Accounting Asset";
                return Transaction.INVALID_AWARD;
            }

            // нельзя делать ссылку на иконку у Персон
            int total = 0;
            for (int i = 0; i < dexAwards.length; ++i) {
                ExLinkAddress exAddress = dexAwards[i];
                if (exAddress.getValue1() <= 0) {
                    errorValue = "Award[" + i + "] percent is so small (<=0%)";
                    return Transaction.INVALID_AWARD;
                } else if (exAddress.getValue1() > 25000) {
                    errorValue = "Award[" + i + "] percent is so big (>25%)";
                    return Transaction.INVALID_AWARD;
                }

                total += exAddress.getValue1();
                if (total > 25000) {
                    errorValue = "Total Award percent is so big (>25%)";
                    return Transaction.INVALID_AWARD;
                }
            }
        }

        return super.isValid();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject toJson() {

        JSONObject assetJSON = super.toJson();

        JSONObject landObj = Lang.getInstance().getLangJson("en");

        // ADD DATA
        assetJSON.put("assetTypeKey", this.assetType);
        assetJSON.put("assetTypeName", Lang.T(viewAssetType(), landObj));
        assetJSON.put("assetTypeDesc", Lang.T(viewAssetTypeDescriptionCls(assetType), landObj));

        String dexDesc = AssetCls.viewAssetTypeDescriptionDEX(assetType, START_KEY());
        if (dexDesc != null) {
            assetJSON.put("type_desc_DEX", Lang.T(dexDesc, landObj));
        }

        assetJSON.put("released", this.getReleased());
        assetJSON.put("type_key", this.assetType);
        assetJSON.put("type_char", charAssetType());
        assetJSON.put("type_abbrev", viewAssetTypeAbbrev());
        assetJSON.put("type_name", Lang.T(viewAssetType(), landObj));
        assetJSON.put("type_name_full", Lang.T(viewAssetTypeFull(), landObj));
        assetJSON.put("type_desc", Lang.T(viewAssetTypeDescriptionCls(assetType), landObj));

        assetJSON.put("scale", this.getScale());
        assetJSON.put("quantity", this.getQuantity());

        assetJSON.put("isImMovable", this.isImMovable());
        assetJSON.put("isUnlimited", this.isUnlimited(maker, false));
        assetJSON.put("isAccounting", this.isAccounting());
        assetJSON.put("isUnique", this.isUnique());
        assetJSON.put("isUnHoldable", this.isUnHoldable());
        assetJSON.put("isOutsideType", this.isOutsideType());
        assetJSON.put("isSelfManaged", this.isSelfManaged());
        assetJSON.put("isChangeDebtBySendActions", this.isChangeDebtBySendActions());
        assetJSON.put("isChangeDebtBySpendActions", this.isChangeDebtBySpendActions());
        assetJSON.put("isDirectBalances", this.isDirectBalances());
        assetJSON.put("isNotReDebted", this.isNotReDebted());
        assetJSON.put("isOutsideOtherClaim", this.isOutsideOtherClaim());
        assetJSON.put("isReverseSend", this.isReverseSend());

        JSONObject revPos = new JSONObject();
        for (int pos = Account.BALANCE_POS_OWN; pos <= Account.BALANCE_POS_6; pos++) {
            revPos.put("" + pos, isReverseBalancePos(pos));
        }
        assetJSON.put("reversedBalPos", revPos);

        ExLinkAddress[] listDEXAwards = getDEXAwards();
        if (listDEXAwards != null) {
            JSONArray array = new JSONArray();
            for (ExLinkAddress award : listDEXAwards) {
                array.add(award.toJson());
            }
            assetJSON.put("DEXAwards", array);
        }

        return assetJSON;
    }

    public JSONObject jsonForExplorerPage(JSONObject langObj, Object[] args) {

        JSONObject assetJSON = super.jsonForExplorerPage(langObj, args);
        assetJSON.put("assetTypeNameFull", charAssetType() + viewAssetTypeAbbrev() + ":" + Lang.T(viewAssetTypeFull(), langObj));

        assetJSON.put("quantity", this.getQuantity());

        BigDecimal released = getReleased();
        assetJSON.put("released", released);

        if (args != null) {
            // параметры для показа Объемов торгов
            AssetCls quoteAsset = (AssetCls) args[0];
            TradePair tradePair = PairsController.reCalcAndUpdate(this, quoteAsset, (PairMap) args[1], 10);

            BigDecimal price = tradePair.getLastPrice();
            if (price.signum() == 0) {
                price = tradePair.getLower_askPrice();
                if (price.signum() == 0) {
                    price = tradePair.getHighest_bidPrice();
                }
            }
            BigDecimal marketCap = released.multiply(price);
            assetJSON.put("marketCap", marketCap);
            assetJSON.put("price", price);

            assetJSON.put("changePrice", tradePair.getFirstPrice().signum() > 0 ?
                    price.subtract(tradePair.getFirstPrice())
                            .movePointRight(2).divide(tradePair.getFirstPrice(), 3, RoundingMode.DOWN)
                    : 0.0);

        }

        return assetJSON;
    }

    public JSONObject jsonForExplorerInfo(DCSet dcSet, JSONObject langObj, boolean forPrint) {

        JSONObject itemJson = super.jsonForExplorerInfo(dcSet, langObj, forPrint);
        itemJson.put("Label_Asset", Lang.T("Asset", langObj));
        itemJson.put("Label_Scale", Lang.T("Accuracy", langObj));
        itemJson.put("Label_AssetType", Lang.T("Type # вид", langObj));
        itemJson.put("Label_AssetType_Desc", Lang.T("Type Description", langObj));
        itemJson.put("Label_Quantity", Lang.T("Quantity", langObj));
        itemJson.put("Label_Released", Lang.T("Released", langObj));

        itemJson.put("Label_ImMovable", Lang.T("ImMovable", langObj));
        itemJson.put("Label_Unlimited", Lang.T("Unlimited", langObj));
        itemJson.put("Label_Accounting", Lang.T("Accounting", langObj));
        itemJson.put("Label_Unique", Lang.T("Unique", langObj));
        itemJson.put("Label_UnHoldable", Lang.T("Un holdable", langObj));
        itemJson.put("Label_OutsideType", Lang.T("Outside Type", langObj));
        itemJson.put("Label_SelfManaged", Lang.T("Self Managed", langObj));
        itemJson.put("Label_ChangeDebtBySendActions", Lang.T("isChangeDebtBySendActions", langObj));
        itemJson.put("Label_ChangeDebtBySpendActions", Lang.T("isChangeDebtBySpendActions", langObj));
        itemJson.put("Label_DirectBalances", Lang.T("isDirectBalances", langObj));
        itemJson.put("Label_isNotReDebted", Lang.T("isNotReDebted", langObj));
        itemJson.put("Label_isOutsideOtherClaim", Lang.T("isOutsideOtherClaim", langObj));
        itemJson.put("Label_isReverseSend", Lang.T("isReverseSend", langObj));
        itemJson.put("Label_Properties", Lang.T("Properties", langObj));
        itemJson.put("Label_DEX_Awards", Lang.T("DEX Awards", langObj));

        itemJson.put("assetTypeNameFull", charAssetType() + viewAssetTypeAbbrev() + ":" + Lang.T(viewAssetTypeFull(), langObj));
        itemJson.put("released", getReleased());

        if (!forPrint) {
            itemJson.put("Label_Holders", Lang.T("Holders", langObj));
            itemJson.put("Label_Available_pairs", Lang.T("Available pairs", langObj));
            itemJson.put("Label_Pair", Lang.T("Pair", langObj));
            itemJson.put("Label_Orders_Count", Lang.T("Orders Count", langObj));
            itemJson.put("Label_Open_Orders_Volume", Lang.T("Open Orders Volume", langObj));
            itemJson.put("Label_Trades_Count", Lang.T("Trades Count", langObj));
            itemJson.put("Label_Trades_Volume", Lang.T("Trades Volume", langObj));

            itemJson.put("orders", getOperations(DCSet.getInstance()));
        }

        itemJson.put("quantity", NumberAsString.formatAsString(getQuantity()));
        itemJson.put("released", NumberAsString.formatAsString(getReleased(dcSet)));

        itemJson.put("scale", getScale());

        itemJson.put("assetType", Lang.T(viewAssetType(), langObj));
        itemJson.put("assetTypeChar", charAssetType() + viewAssetTypeAbbrev());

        itemJson.put("assetTypeFull", Lang.T(viewAssetTypeFull(), langObj));
        StringJoiner joiner = new StringJoiner(", ");
        for (Fun.Tuple2<?, String> item : viewAssetTypeActionsList(null, true)) {
            joiner.add(Lang.T(item.b, langObj));
        }

        String desc = Lang.T(viewAssetTypeDescriptionCls(getAssetType()), langObj)
                + ".<br><b>" + Lang.T("Acceptable actions", langObj) + "</b>: " + joiner.toString();
        String dexDesc = AssetCls.viewAssetTypeDescriptionDEX(assetType, START_KEY());
        if (dexDesc != null) {
            desc += "<br><b>" + Lang.T("DEX rules and taxes", langObj) + ":</b><br>" + Lang.T(dexDesc, langObj);
        }

        itemJson.put("assetTypeDesc", desc);


        itemJson.put("properties", viewProperties(langObj));

        return itemJson;
    }

    public String makeHTMLHeadView() {

        String text = super.makeHTMLHeadView();
        text += Lang.T("Asset Class") + ":&nbsp;"
                + Lang.T(getItemSubType() + "") + "<br>"
                + Lang.T("Asset Type") + ":&nbsp;"
                + "<b>" + charAssetType() + viewAssetTypeAbbrev() + "</b>:" + Lang.T(viewAssetTypeFull() + "") + "<br>"
                + Lang.T("Quantity") + ":&nbsp;" + getQuantity() + ", "
                + Lang.T("Scale") + ":&nbsp;" + getScale() + "<br>"
                + Lang.T("Description") + ":<br>";
        if (getKey() > 0 && getKey() < START_KEY()) {
            text += Library.to_HTML(Lang.T(viewDescription())) + "<br>";
        } else {
            text += Library.to_HTML(viewDescription()) + "<br>";
        }

        return text;

    }

    public int getDataLength(boolean includeReference) {
        return super.getDataLength(includeReference) + ASSET_TYPE_LENGTH;
    }

    public static void processTrade(DCSet dcSet, Block block, Account receiver,
                                    boolean isInitiator, AssetCls assetHave, AssetCls assetWant,
                                    boolean asOrphan, BigDecimal tradeAmountForWant, long timestamp, Long orderID) {
        //TRANSFER FUNDS
        BigDecimal tradeAmount = tradeAmountForWant.setScale(assetWant.getScale());
        BigDecimal assetRoyaltyTotal = BigDecimal.ZERO;
        BigDecimal inviterRoyalty;
        BigDecimal forgerFee;
        int scale = assetWant.getScale();
        Long assetWantKey = assetWant.getKey();

        PublicKeyAccount haveAssetMaker = assetHave.getMaker();
        PublicKeyAccount inviter = null;


        //////// ACCOUNTING assets is Denied for Awards //////

        ExLinkAddress[] dexAwards = assetHave.getDEXAwards();
        if (dexAwards != null) {
            for (ExLinkAddress dexAward : dexAwards) {
                if (receiver.equals(dexAward.getAccount())) {
                    // to mySelf not pay
                    continue;
                }

                BigDecimal assetRoyalty = tradeAmount.multiply(new BigDecimal(dexAward.getValue1()))
                        .movePointLeft(5) // in ExLinkAddress is x1000 and x100 as percent
                        .setScale(scale, RoundingMode.DOWN);
                if (assetRoyalty.signum() > 0) {
                    assetRoyaltyTotal = assetRoyaltyTotal.add(assetRoyalty);
                    dexAward.getAccount().changeBalance(dcSet, asOrphan, false, assetWantKey,
                            assetRoyalty, false, false, false);
                    if (!asOrphan && block != null)
                        block.addCalculated(dexAward.getAccount(), assetWantKey, assetRoyalty,
                                "NFT Royalty by Order @" + Transaction.viewDBRef(orderID), orderID);
                }
            }
        }

        if (assetHave.getAssetType() == AS_NON_FUNGIBLE) {

            // всегда 1% форжеру
            forgerFee = tradeAmount.movePointLeft(2).setScale(scale, RoundingMode.DOWN);

            Fun.Tuple4<Long, Integer, Integer, Integer> issuerPersonDuration = haveAssetMaker.getPersonDuration(dcSet);
            if (issuerPersonDuration != null) {
                inviter = PersonCls.getIssuer(dcSet, issuerPersonDuration.a);
            }

            if (inviter == null) {
                inviterRoyalty = BigDecimal.ZERO;
            } else {
                inviterRoyalty = forgerFee;
            }

        } else if (assetWant.getKey() < assetWant.getStartKey()
                && !isInitiator) {
            // это системные активы - берем комиссию за них
            forgerFee = tradeAmount.movePointLeft(3).setScale(scale, RoundingMode.DOWN);

            // за рефералку тут тоже
            Fun.Tuple4<Long, Integer, Integer, Integer> issuerPersonDuration = receiver.getPersonDuration(dcSet);
            if (issuerPersonDuration != null) {
                inviter = PersonCls.getIssuer(dcSet, issuerPersonDuration.a);
                if (inviter == null)
                    inviterRoyalty = BigDecimal.ZERO;
                else
                    inviterRoyalty = forgerFee;
            } else {
                inviter = null;
                inviterRoyalty = BigDecimal.ZERO;
            }

        } else {

            if (assetRoyaltyTotal.signum() > 0) {

                forgerFee = assetRoyaltyTotal.movePointLeft(2).setScale(scale, RoundingMode.DOWN);

                Fun.Tuple4<Long, Integer, Integer, Integer> issuerPersonDuration = haveAssetMaker.getPersonDuration(dcSet);
                if (issuerPersonDuration != null) {
                    inviter = PersonCls.getIssuer(dcSet, issuerPersonDuration.a);
                }

                if (inviter == null) {
                    inviterRoyalty = BigDecimal.ZERO;
                } else {
                    inviterRoyalty = forgerFee;
                }

            } else {
                inviterRoyalty = BigDecimal.ZERO;
                inviter = null;
                forgerFee = BigDecimal.ZERO;
            }
        }

        if (assetRoyaltyTotal.signum() > 0) {
            tradeAmount = tradeAmount.subtract(assetRoyaltyTotal);
        }

        if (inviterRoyalty.signum() > 0) {
            tradeAmount = tradeAmount.subtract(inviterRoyalty);

            long inviterRoyaltyLong = inviterRoyalty.setScale(assetWant.getScale()).unscaledValue().longValue();
            Transaction.process_gifts(dcSet, BlockChain.FEE_INVITED_DEEP, inviterRoyaltyLong, inviter, asOrphan,
                    assetWant, block,
                    "NFT Royalty referral bonus " + "@" + Transaction.viewDBRef(orderID),
                    orderID, timestamp);
        }

        if (forgerFee.signum() > 0) {
            tradeAmount = tradeAmount.subtract(forgerFee);

            if (block != null) {
                block.addAssetFee(assetWant, forgerFee, null);
            }
        }

        receiver.changeBalance(dcSet, asOrphan, false, assetWantKey,
                tradeAmount, false, false, false);
        if (!asOrphan && block != null)
            block.addCalculated(receiver, assetWantKey, tradeAmount,
                    "Trade Order @" + Transaction.viewDBRef(orderID), orderID);

    }

}
