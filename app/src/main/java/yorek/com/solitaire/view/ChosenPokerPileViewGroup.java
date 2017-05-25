package yorek.com.solitaire.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import yorek.com.solitaire.GlobalApplication;
import yorek.com.solitaire.bean.Card;

/**
 * 已选区域的布局
 * Created by yorek on 11/2/16.
 */
public class ChosenPokerPileViewGroup extends PokerPileViewGroup {

    private static final String TAG = "ChosenPokerPilesGroups";

    /**
     * 该区域最多显示出来的牌的数量
     */
    private static final int MAX_VISIBLE_CARD = 3;
    /**
     * 当前显示出来的栈顶的索引
     */
    private int mIndex = -1;

    public ChosenPokerPileViewGroup(Context context) {
        super(context);
    }

    public ChosenPokerPileViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChosenPokerPileViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int count = getVisibleCards();
        // 从栈顶元素开始依次进行layout，最多layout三个子View
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(mIndex - (count - i - 1));
            child.layout(cardDeltaPadding * (count - i - 1), 0,
                    GlobalApplication.sCardWidth + cardDeltaPadding * (count - i - 1),
                    GlobalApplication.sCardHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int totalHeight = 0;
        int childCnt = getVisibleCards();
        totalWidth = GlobalApplication.sCardWidth + cardDeltaPadding * (childCnt - 1);
        totalHeight = GlobalApplication.sCardHeight;

        setMeasuredDimension(totalWidth, totalHeight);
    }

    /**
     * 获取应该显示的卡片的数量
     * @return 应该显示的卡片的数量
     */
    private int getVisibleCards() {
        int childCount = getChildCount();
        if (childCount >= 0 && childCount <= MAX_VISIBLE_CARD) {
            return childCount;
        } else {
            return MAX_VISIBLE_CARD;
        }
    }

    /**
     * 重置索引
     */
    public void clearIndex() {
        mIndex = -1;
    }

    @Override
    public void addCard(Card card) {
        super.addCard(card);
        mIndex++;
    }

    @Override
    public Card popCard() {
        mIndex--;
        return super.popCard();
    }

    /**
     * 更新状态，使栈顶的卡片可以拖动，其余的不能拖动
     */
    @Override
    public void updateState() {
        for (int i = 0; i < mCardViews.size() - 1; i++) {
            mCardViews.get(i).setEnabled(false);
        }
        if (mCardViews.size() > 0) {
            mCardViews.get(mCardViews.size() - 1).setEnabled(true);
        }
    }

    @Override
    public boolean isWin() {
        return mCardStack.isEmpty();
    }
}