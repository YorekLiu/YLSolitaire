package yorek.com.solitaire.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 游戏时局
 * 记录游戏的进行状态
 * Created by yorek on 10/31/16.
 */
public class GameBoard {

    private static final String TAG = "GameBoard";

    /**
     * 中间操作区域的牌叠数
     */
    public static final int CARD_PILES_OPERATOR = 7;

    /**
     * 左上角目标区域的牌叠数
     */
    public static final int CARD_PILES_TARGET = 4;

    /**
     * 中间操作区域的七叠牌列
     */
    private static List<Stack<Card>> sOperatorCardPiles;

    /**
     * 左上角四叠目标牌叠
     */
    private static List<Stack<Card>> sTargetCardPiles;

    /**
     * 右上角待选牌叠（待选区域）
     */
    private static Stack<Card> sChoiceCardPiles;

    /**
     * 右上角待选牌叠中出来的牌（已选区域）
     */
    private static Stack<Card> sChosenCardPiles;

    /**
     * 初始化该局游戏里面的数据
     */
    public static void initGameBoard(List<Card> cards) {
        // 发牌给操作区域的七个牌叠
        int cnt = 0;
        List<Stack<Card>> cardPiles = new ArrayList<>(GameBoard.CARD_PILES_OPERATOR);
        for (int i = 0; i < GameBoard.CARD_PILES_OPERATOR; i++) {
            Stack<Card> cardStack = new Stack<>();
            for (int j = 0; j < i; j++) {
                cardStack.push(cards.get(cnt++));
            }
            Card visibleCard = cards.get(cnt++);
            visibleCard.setVisible(true);
            cardStack.push(visibleCard);

            cardPiles.add(cardStack);
        }
        GameBoard.setOperatorCardPiles(cardPiles);

        // 将剩余的牌放至右上角待选牌叠
        Stack<Card> choiceCardPiles = new Stack<>();
        for (int i = cnt; i < Card.TOTAL_CARDS; i++) {
            choiceCardPiles.push(cards.get(i));
        }
        GameBoard.setChoiceCardPiles(choiceCardPiles);

        // 初始化目标区域以及已选区域的Stack
        List<Stack<Card>> targetPiles = new ArrayList<Stack<Card>>();
        for (int i = 0; i < CARD_PILES_TARGET; i++) {
            targetPiles.add(new Stack<Card>());
        }
        GameBoard.setTargetCardPiles(targetPiles);
        GameBoard.setChosenCardPiles(new Stack<Card>());
    }

    public static List<Stack<Card>> getOperatorCardPiles() {
        return sOperatorCardPiles;
    }

    public static void setOperatorCardPiles(List<Stack<Card>> operatorCardPiles) {
        sOperatorCardPiles = operatorCardPiles;
    }

    public static List<Stack<Card>> getTargetCardPiles() {
        return sTargetCardPiles;
    }

    public static void setTargetCardPiles(List<Stack<Card>> targetCardPiles) {
        sTargetCardPiles = targetCardPiles;
    }

    public static Stack<Card> getChoiceCardPiles() {
        return sChoiceCardPiles;
    }

    public static void setChoiceCardPiles(Stack<Card> choiceCardPiles) {
        sChoiceCardPiles = choiceCardPiles;
    }

    public static Stack<Card> getChosenCardPiles() {
        return sChosenCardPiles;
    }

    public static void setChosenCardPiles(Stack<Card> chosenCardPiles) {
        sChosenCardPiles = chosenCardPiles;
    }

    /**
     * 打印游戏当前的状态
     */
    public static void showGameBoard() {
        // 打印操作区域的扑克信息
        int cnt = 0;
        StringBuilder logBuilder;
        for (Stack<Card> cardStack: sOperatorCardPiles) {
            logBuilder = new StringBuilder("[ operator " + cnt++ + " ] [bottom] ");
            for (Card card: cardStack) {
                logBuilder.append(card.toString()).append(" ");
            }
            logBuilder.append(" [top] ");
            Log.d(TAG, logBuilder.toString());
        }

        // 打印目标区域的扑克信息
        cnt = 0;
        for (Stack<Card> cardStack: sTargetCardPiles) {
            logBuilder = new StringBuilder("[ target " + cnt++ + " ] ");
            for (Card card: cardStack) {
                logBuilder.append(card.toString()).append(" ");
            }
            Log.d(TAG, logBuilder.toString());
        }

        // 打印待选、已选区域的扑克信息
        logBuilder = new StringBuilder("[ choice ] ");
        for (Card card: sChoiceCardPiles) {
            logBuilder.append(card.toString()).append(" ");
        }
        Log.d(TAG, logBuilder.toString());

        logBuilder = new StringBuilder("[ chosen ] ");
        for (Card card: sChosenCardPiles) {
            logBuilder.append(card.toString()).append(" ");
        }
        Log.d(TAG, logBuilder.toString());
    }
}
