package yorek.com.solitaire.calc;

import java.util.Stack;

import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.view.ChoiceImageView;

/**
 * 游戏控制器
 * Created by yorek on 11/7/16.
 */
public class GameController {
    /**
     * 游戏难度
     */
    private static int mGameLevel = 0;
    /**
     * 简单难度：每次发牌只发一张，非K牌可以放入空的操作区
     */
    public static final int LEVEL_EASY = 2;
    /**
     * 普通难度：每次发牌只发一张，只有K牌可以放入空的操作区
     */
    public static final int LEVEL_NORMAL = 1;
    /**
     * 困难难度：每次发牌发三张，只有K牌可以放入空的操作区
     */
    public static final int LEVEL_HARD = 0;

    /**
     * 游戏步数
     */
    private static int sStep = 0;

    /**
     * 游戏耗时
     */
    private static int sTime;

    /**
     * 扑克牌是否在移动
     */
    private static boolean sIsMoving = false;

    /**
     * 临时操作栈：保存拖拽时，pop出来的Card
     */
    private static Stack<Card> sOperatorStack = new Stack<>();

    /**
     * 从临时操作栈中pop出一个数据
     * @return pop出来的数据
     * @see Stack#pop()
     */
    public static Card pop() {
        return sOperatorStack.pop();
    }

    /**
     * push一个数据到临时操作栈中
     * @param card 待push的数据
     * @see Stack#push(Object)
     */
    public static void push(Card card) {
        sOperatorStack.push(card);
    }

    /**
     * 临时操作栈是否为空
     * @return true if the stack is empty, otherwise false
     * @see Stack#isEmpty()
     */
    public static boolean isEmpty() {
        return sOperatorStack.isEmpty();
    }

    /**
     * 返回临时操作栈
     */
    public static Stack<Card> getOperatorStack() {
        return sOperatorStack;
    }

    /**
     * 返回临时操作栈最上面的一个数据
     * @return 临时操作栈最上面的数据
     * @see Stack#peek()
     */
    public static Card getTopCard() {
        int size = sOperatorStack.size();
        return size == 0 ? null : sOperatorStack.peek();
    }

    public static int getGameLevel() {
        return mGameLevel;
    }

    public static void setGameLevel(int gameLevel) {
        mGameLevel = gameLevel;
        ChoiceImageView.setNumberDistributeByLevel(gameLevel);
    }

    public static void incrementStep() {
        sStep++;
    }

    public static int getStep() {
        return sStep;
    }

    public static void resetStep() {
        sStep = 0;
    }

    public static void resetTime() {
        sTime = 0;
    }

    public static void setStep(int step) {
        sStep = step;
    }

    public static boolean isMoving() {
        return sIsMoving;
    }

    public static void setIsMoving(boolean isMoving) {
        sIsMoving = isMoving;
    }

    public static int getTime() {
        return sTime;
    }

    public static void setTime(int time) {
        sTime = time;
    }

    public static void incrementTime() {
        sTime++;
    }
}
