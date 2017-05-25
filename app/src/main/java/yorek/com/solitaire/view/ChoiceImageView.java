package yorek.com.solitaire.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Stack;

import yorek.com.solitaire.R;
import yorek.com.solitaire.Updatable;
import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.calc.GameController;

/**
 * 待选区的View
 * Created by yorek on 11/13/16.
 */
public class ChoiceImageView extends ImageView implements Updatable {

    public static int NUMBER_DISTRIBUTE = 1;

    /**
     * 待选区的牌叠
     */
    private Stack<Card> mChoiseStack;

    public ChoiceImageView(Context context) {
        super(context);
    }

    public ChoiceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChoiceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChoiceImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Stack<Card> getChoiseStack() {
        return mChoiseStack;
    }

    public void setChoiseStack(Stack<Card> choiseStack) {
        mChoiseStack = choiseStack;
        updateStatus();
    }

    /**
     * 将已选区域的牌重新放入待选区
     * @param viewGroup 已选区域
     */
    public void again(ChosenPokerPileViewGroup viewGroup) {
        while (!viewGroup.isEmpty()) {
            Card card = viewGroup.popCard();
            card.setVisible(false);
            mChoiseStack.push(card);
        }
    }

    /**
     * 进行一次发牌操作，如果剩余牌数大于等于3，则发3张牌，否则发完剩下的牌
     * @param viewGroup 已选区域
     */
    public void popCard(ChosenPokerPileViewGroup viewGroup) {
        int availableCnt = mChoiseStack.size() >= NUMBER_DISTRIBUTE ? NUMBER_DISTRIBUTE : mChoiseStack.size();
        for (int i = 0; i < availableCnt; i++) {
            Card card = mChoiseStack.pop();
            card.setVisible(true);
            viewGroup.addCard(card);
        }
    }

    /**
     * 该区域的点击事件，需要根据待选区是否有余牌进行发牌或者回收牌的操作
     * @param viewGroup 已选区域
     */
    public void onClicked(ChosenPokerPileViewGroup viewGroup) {
        if (mChoiseStack.isEmpty()) {
            again(viewGroup);
            viewGroup.clearIndex();
        } else {
            popCard(viewGroup);
        }
        updateStatus();
    }

    /**
     * 根据待选区栈顶的牌显示对应的图片资源
     */
    private void updateStatus() {
        if (!mChoiseStack.isEmpty())    // 不空，显示最顶上的卡片的图片资源（显示的是背面）
            setImageResource(mChoiseStack.peek().getImgRes(getContext()));
        else                            // 空，显示占位图片
            setImageResource(R.drawable.placeholder);
    }

    @Override
    public void updateTheme() {
        updateStatus();
    }

    @Override
    public boolean isWin() {
        return mChoiseStack.isEmpty();
    }

    public static void setNumberDistributeByLevel(int level) {
        if (level != GameController.LEVEL_HARD) {
            NUMBER_DISTRIBUTE = 1;
        } else {
            NUMBER_DISTRIBUTE = 3;
        }
    }
}
