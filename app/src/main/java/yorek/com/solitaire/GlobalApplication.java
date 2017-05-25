package yorek.com.solitaire;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import yorek.com.solitaire.bean.GameBoard;
import yorek.com.solitaire.util.DisplayUtils;

/**
 * 工程Application
 * Created by yorek on 11/2/16.
 */
public class GlobalApplication extends Application {
    public static final String TAG = "GlobalApplication";

    public static final boolean DEBUG = BuildConfig.DEBUG;

    // 屏幕的宽高以及density
    public static int sDisplayScreenWidth;
    public static int sDisplayScreenHeight;
    public static float sDisplayDensity;

    // 扑克卡片的宽高
    public static int sCardWidth;
    public static int sCardHeight;

    /**
     * 目标区域高度的权重
     */
    public static final int OPERATOR_HEIGHT_WEIGHT = 2;

    /**
     * 扑克牌当前的样式值
     */
    private static int CARD_SKIN = 3;
    /**
     * 可用的扑克牌样式数
     */
    public static final int CARD_TOTAL_SKIN = 8;
    /**
     * 扑克样式的名称前缀
     */
    private static String[] sCardSkinNames = new String[CARD_TOTAL_SKIN];
    static {
        sCardSkinNames[0] = "n_1_";
        sCardSkinNames[1] = "n_2_";
        sCardSkinNames[2] = "n_3_";
        sCardSkinNames[3] = "n_4_";
        sCardSkinNames[4] = "s_1_";
        sCardSkinNames[5] = "s_2_";
        sCardSkinNames[6] = "s_3_";
        sCardSkinNames[7] = "s_4_";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 设置异常捕获器
        if (!DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }

        // 获取屏幕分辨率大小以及屏幕像素密度
        Pair<Integer, Integer> pair = DisplayUtils.getDisplayPixels(this);
        sDisplayScreenWidth = pair.first;
        sDisplayScreenHeight = pair.second;
        sDisplayDensity = DisplayUtils.getDisplayDensity(this);

        // 获取计算后的扑克牌尺寸
        sCardWidth = getCardWidth(this);
        sCardHeight = getCardHeight(this);

        if (DEBUG) {
            Log.d(TAG, "sDisplayScreenWidth = " + sDisplayScreenWidth + ", sDisplayScreenHeight ="
                    + sDisplayScreenHeight + ", sDisplayDensity = " + sDisplayDensity);
            Log.d(TAG, "sCardWidth = " + sCardWidth + ", sCardHeight = " + sCardHeight);
        }
    }

    /**
     * 返回扑克的皮肤值[0, 7]
     */
    public static int getCardSkin() {
        return CARD_SKIN;
    }

    /**
     * 设置扑克牌的样式
     * @param cardSkin [0, CARD_TOTAL_SKIN - 1]之间的整数
     */
    public static void setCardSkin(int cardSkin) {
        if (cardSkin < 0 || cardSkin >= CARD_TOTAL_SKIN)
            throw new IllegalArgumentException("cardSkin is invalid.");
        CARD_SKIN = cardSkin;
    }

    /**
     * 返回皮肤所对应的牌的前缀
     * @param cardSkin 皮肤值
     * @return 皮肤对应的牌的前缀， e.g. 0 --> n_1_
     */
    public static String getSkinPrefix(int cardSkin) {
        return sCardSkinNames[cardSkin];
    }

    /**
     * 返回当前皮肤的前缀
     */
    public static String getCurrentSkinPrefix() {
        return sCardSkinNames[CARD_SKIN];
    }

    /**
     * 获取卡片的宽度
     */
    private static int getCardWidth(Context context) {
        // 屏幕宽度减去空白后七等分
        return (int) (sDisplayScreenWidth - (GameBoard.CARD_PILES_OPERATOR + 1)
                * context.getResources().getDimension(R.dimen.target_pile_padding)) / GameBoard.CARD_PILES_OPERATOR;
    }

    /**
     * 获取卡片的高度
     */
    private static int getCardHeight(Context context) {
        return getCardWidth(context) * 3 / 2;
    }
}
