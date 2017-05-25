package yorek.com.solitaire.bean;

import android.content.Context;

import yorek.com.solitaire.GlobalApplication;

/**
 * 扑克牌实体
 * Created by yorek on 10/31/16.
 */
public class Card {
    /**
     * 红桃:H-Heart 桃心(象形),代表爱情.
     * 黑桃:S-Spade 橄榄叶(象形),代表和平.
     * 方块:D-Diamond 钻石(形同意合),代表财富.
     * 梅花:C-Club 三叶草(象形),代表幸运.
     */
    public static final int CARD_TYPE_H = 0;
    public static final int CARD_TYPE_S = 1;
    public static final int CARD_TYPE_D = 2;
    public static final int CARD_TYPE_C = 3;
    public static final int CARD_TOTAL_TYPES = 4;

    private static String[] CARD_TYPES = new String[CARD_TOTAL_TYPES];

    /**
     * 牌的数值
     */
    public static final int CARD_VALUE_A = 0;
    public static final int CARD_VALUE_2 = 1;
    public static final int CARD_VALUE_3 = 2;
    public static final int CARD_VALUE_4 = 3;
    public static final int CARD_VALUE_5 = 4;
    public static final int CARD_VALUE_6 = 5;
    public static final int CARD_VALUE_7 = 6;
    public static final int CARD_VALUE_8 = 7;
    public static final int CARD_VALUE_9 = 8;
    public static final int CARD_VALUE_10 = 9;
    public static final int CARD_VALUE_J = 10;
    public static final int CARD_VALUE_Q = 11;
    public static final int CARD_VALUE_K = 12;
    public static final int CARD_TOTAL_VALUES = 13;

    private static String[] CARD_VALUES = new String[CARD_TOTAL_VALUES];

    /**
     * 扑克牌反面的资源名前缀
     */
    public static final String sCardBackName = "cardback_";

    /**
     * 牌的总数 4 * 13 = 52
     */
    public static final int TOTAL_CARDS = 52;

    /**
     * 扑克花色
     */
    private int cardType;
    /**
     * 扑克值
     */
    private int cardValue;
    /**
     * 扑克花色、数值是否可见
     */
    private boolean visible;

    /**
     * 根据扑克牌资源的名称取值，方便加载资源
     */
    static {
        // 初始化扑克花色
        CARD_TYPES[CARD_TYPE_H] = "heart";
        CARD_TYPES[CARD_TYPE_S] = "spade";
        CARD_TYPES[CARD_TYPE_D] = "diamond";
        CARD_TYPES[CARD_TYPE_C] = "club";

        // 初始化扑克数值
        CARD_VALUES[CARD_VALUE_A] = "1";
        CARD_VALUES[CARD_VALUE_2] = "2";
        CARD_VALUES[CARD_VALUE_3] = "3";
        CARD_VALUES[CARD_VALUE_4] = "4";
        CARD_VALUES[CARD_VALUE_5] = "5";
        CARD_VALUES[CARD_VALUE_6] = "6";
        CARD_VALUES[CARD_VALUE_7] = "7";
        CARD_VALUES[CARD_VALUE_8] = "8";
        CARD_VALUES[CARD_VALUE_9] = "9";
        CARD_VALUES[CARD_VALUE_10] = "10";
        CARD_VALUES[CARD_VALUE_J] = "11";
        CARD_VALUES[CARD_VALUE_Q] = "12";
        CARD_VALUES[CARD_VALUE_K] = "13";
    }

    public Card() {
        visible = false;
    }

    public Card(int cardType, int cardValue, boolean visible) {
        this.cardType = cardType;
        this.cardValue = cardValue;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCardValue() {
        return cardValue;
    }

    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    /**
     * 根据扑克是否可见以及扑克的花色、数值返回对应的drawable资源
     * @return drawable资源
     */
    public int getImgRes(Context context) {
        String resString;
        if (isVisible()) {
            // 如果扑克可见
            resString = GlobalApplication.getCurrentSkinPrefix() + toString();
        } else {
            // 如果扑克不可见
            resString = Card.sCardBackName + (GlobalApplication.getCardSkin() / 2 + 1);
        }
        return context.getResources().getIdentifier(resString, "drawable", context.getPackageName());
    }

    /**
     * @return 返回 "花色" + "数值"
     */
    @Override
    public String toString() {
        return CARD_TYPES[cardType] + CARD_VALUES[cardValue];
    }

    /**
     * 操作区的顶端卡片与正在拖拽的卡片是否可以连接
     * @param card 正在拖拽的卡片
     * @return true 两张牌颜色不一样且正在拖拽的卡片数值比操作区顶端卡片数值大一<br /> false 不可连接
     */
    public boolean isOperatorContinue(Card card) {
        // 四种花色值中，颜色相同的两种花色相差一
        // 因为两个牌花色相加为1，说明颜色不一样
        boolean isTypeOk = (cardType + card.getCardType()) % 2 == 1;
        boolean isValueOk = (cardValue - 1) == card.getCardValue();

        return isTypeOk && isValueOk;
    }

    /**
     * 目标区的顶端卡片与正在拖拽的卡片是否可以连接
     * @param card 正在拖拽的卡片
     * @return true 两张牌花色一样且正在拖拽的卡片数值比操作区顶端卡片数值小一<br /> false 不可连接
     */
    public boolean isTargetContinue(Card card) {
        boolean isTypeOk = cardType == card.getCardType();
        boolean isValueOk = (cardValue + 1) == card.getCardValue();

        return isTypeOk && isValueOk;
    }
}