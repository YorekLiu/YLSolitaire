package yorek.com.solitaire.calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.bean.GameBoard;

/**
 * 开局时用来分牌
 * 饿汉单例模式实现
 * Created by yorek on 10/31/16.
 */
public class GameGenerator {

    private static final String TAG = "GameGenerator";

    /**
     * 发牌完成时的回调接口类
     */
    public interface OnGameGeneratorCallback {
        /**
         * 发牌完成回调方法
         */
        void onGenerateDone();
    }
    /**
     * 发牌完成回调接口
     */
    private OnGameGeneratorCallback mCallback;
    public void setCallback(OnGameGeneratorCallback callback) {
        mCallback = callback;
    }

    /// 饿汉单例模式 @{
    private static GameGenerator sCardGenerator = new GameGenerator();
    public static GameGenerator getInstance() {
        return sCardGenerator;
    }
    private GameGenerator() {
        beginGenerator();
    }
    /// }

    /**
     * 开始生成牌的数据
     */
    public void beginGenerator() {
        // 1.生成52个表示各个扑克牌的整数
        int[] cardValues = getRandomIntegers();

        // 2. 将52个整数转化为52张牌
        List<Card> cards = parseIntToCard(cardValues);

        // 3. 初始化牌局
        GameBoard.initGameBoard(cards);

        // 4. 调用回调方法，通知Activity开始加载数据
        if (mCallback != null) {
            mCallback.onGenerateDone();
        } else {
           Log.d(TAG, "Please implement this callback in your activity or fragment.");
        }
    }

    /**
     * 随机生成52个不重复的整数，表示52张牌
     * @return 52个表示牌的整数
     */
    private int[] getRandomIntegers() {
        // 申请一个52长度的数组
        int[] random = new int[Card.TOTAL_CARDS];
        for (int i = 0; i < Card.TOTAL_CARDS; i++) {
            random[i] = i;
        }

        // 开始洗牌
        Random rd = new Random();
        int temp;
        int j;
        for (int i = 0; i < 52; i++)
        {
            j = rd.nextInt(52);
            temp = random[i];
            random[i] = random[j];
            random[j] = temp;
        }

        return random;
    }

    /**
     * 将值1~52的整数数组转换为扑克的花色以及数值
     * @param cardValues 值为1、52以及之间的数值数组
     * @return 有花色以及数值的扑克牌
     */
    private List<Card> parseIntToCard(int[] cardValues) {
        // 申请52位长度的链表
        List<Card> cards = new ArrayList<>(cardValues.length);

        int cardType;
        int cardValue;
        for (int value : cardValues) {
            // 得到花色以及数值
            cardType = value / Card.CARD_TOTAL_VALUES;
            cardValue = value % Card.CARD_TOTAL_VALUES;

            Card card = new Card();
            card.setCardType(cardType);
            card.setCardValue(cardValue);
            cards.add(card);
        }

        return cards;
    }
}
