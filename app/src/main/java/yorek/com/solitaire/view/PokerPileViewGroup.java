package yorek.com.solitaire.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import yorek.com.solitaire.GlobalApplication;
import yorek.com.solitaire.ICardMovable;
import yorek.com.solitaire.R;
import yorek.com.solitaire.Updatable;
import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.bean.GameBoard;
import yorek.com.solitaire.calc.GameController;
import yorek.com.solitaire.ui.GameActivity;
import yorek.com.solitaire.util.DisplayUtils;

/**
 * 操作区的布局
 * Created by yorek on 11/2/16.
 */
public class PokerPileViewGroup extends ViewGroup implements View.OnTouchListener, ICardMovable,
        Updatable {

    private static final String TAG = "PokerPileViewGroup";
    /**
     * 一摞卡片中，每张卡片需要移开的距离
     */
    protected int cardDeltaPadding;
    /**
     * 水平布局时卡片离父布局的上下的间距
     */
    protected int paddingHorizontal;
    /**
     * 垂直布局时卡片离父布局的上下的间距
     */
    protected int paddingVertical;

    /**
     * 卡片布局方向
     */
    protected int orientation;
    /**
     * 竖直布局
     */
    protected static final int ORIENTATION_VERTICAL = 0;
    /**
     * 水平布局
     */
    protected static final int ORIENTATION_HORIZONTAL = 1;
    /**
     * 区域标记值
     */
    protected int mFlag;
    /**
     * touch时是否已经将Card放入临时牌叠中
     */
    protected boolean mIsPoped;

    protected Stack<Card> mCardStack;
    protected List<ImageView> mCardViews;
    /**
     * 记录该ViewGroup在的区域范围
     */
    protected Rect mRect;

    public PokerPileViewGroup(Context context) {
        this(context, null);
    }

    public PokerPileViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokerPileViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 解析XML中写好的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PokerPileViewGroup);
        orientation =  a.getInt(R.styleable.PokerPileViewGroup_orientation, ORIENTATION_HORIZONTAL);
        mFlag = a.getInt(R.styleable.PokerPileViewGroup_flag, 0);
        a.recycle();
    }

    /**
     * 加载完成后，计算两张牌之间的间隔以及牌叠之间的间隔
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cardDeltaPadding = orientation == ORIENTATION_HORIZONTAL ?
                (int) getResources().getDimension(R.dimen.card_delta_padding_horizontal)
                : (int) getResources().getDimension(R.dimen.card_delta_padding_vertical);
        paddingHorizontal = (int) getResources().getDimension(R.dimen.target_pile_padding);
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (orientation == ORIENTATION_HORIZONTAL) {
                child.layout(cardDeltaPadding * (count - i - 1), 0,
                        GlobalApplication.sCardWidth + cardDeltaPadding * (count - i - 1),
                        GlobalApplication.sCardHeight);
            } else {
                child.layout(0, cardDeltaPadding * i,
                        GlobalApplication.sCardWidth,
                        GlobalApplication.sCardHeight + cardDeltaPadding * i);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int totalHeight = 0;
        int childCnt = getChildCount();
        if (orientation == ORIENTATION_HORIZONTAL) {
            totalWidth = GlobalApplication.sCardWidth + cardDeltaPadding * (childCnt - 1);
            totalHeight = GlobalApplication.sCardHeight;
        } else {
            totalWidth = GlobalApplication.sDisplayScreenWidth / GameBoard.CARD_PILES_OPERATOR;
            totalHeight = (int) ((GlobalApplication.sDisplayScreenHeight - DisplayUtils.dp2px(getContext(), 20))
                    * (10 - GlobalApplication.OPERATOR_HEIGHT_WEIGHT)
                    / GlobalApplication.OPERATOR_HEIGHT_WEIGHT);
        }
        setMeasuredDimension(totalWidth, totalHeight);
    }

    /**
     * 根据传入的resId来初始化一个ImageView，并设置其padding
     */
    protected ImageView instanceImageView(int resId) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(resId);
        imageView.setPadding(0, paddingHorizontal, 0, paddingHorizontal);

        imageView.setOnTouchListener(this);

        return imageView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 屏蔽掉多指事件，一定要return false不然会继续执行下去
        if (GameController.isMoving() && event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }

        // 将被Touch的ImageView以及以上的View所对应的牌全部出栈
        // 并push进临时操作栈
        // 加入mIsPoped变量的原因：使该段出栈、入栈的代码在拖拽一个View时只执行一次
        if (!mIsPoped && !GameController.isMoving()) {
            ImageView touchedView = (ImageView) v;
            boolean isContinueAdd = true;
            for (int i = mCardViews.size() - 1; i >= 0; i--) {
                if (touchedView.equals(mCardViews.get(i))) {
                    isContinueAdd = false;
                }
                Card popCard = popCard();
                GameController.push(popCard);
                if (!isContinueAdd) {
                    mIsPoped = true;
                    break;
                }
            }
        }

        ((GameActivity) getContext()).onTouchEvent(this, event);
        return true;
    }

    /**
     * 布局发生改变时，保存其范围
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRect = new Rect();
        this.getGlobalVisibleRect(mRect);
    }

    /**
     * 点(x, y)是否在该ViewGroup的范围内
     * @param x x坐标
     * @param y y坐标
     * @return true if contain, otherwise false
     */
    public boolean isContain(int x, int y) {
        return mRect.contains(x, y);
    }

    /**
     * 是否已经将Card放入临时牌叠中是否正在拖拽中
     * @return true 已经放入, false 没有放入
     */
    public boolean isPoped() {
        return mIsPoped;
    }

    /**
     * 设置要放入的牌是否已经放入
     */
    public void setPoped(boolean poped) {
        mIsPoped = poped;
    }

    /**
     * 设置牌叠，并更新视图
     */
    public void setCards(Stack<Card> cards) {
        mCardStack = cards;
        mCardViews = new ArrayList<>(cards.size());
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            ImageView imageView = instanceImageView(card.getImgRes(getContext()));
            imageView.setEnabled(card.isVisible());
            addView(imageView);
            mCardViews.add(imageView);
        }
        updateState();
    }

    /**
     * 向操作区添加一张牌
     */
    @Override
    public void addCard(Card card) {
        // 将Visible设置为true，便于显示牌的正面而不是反面
        mCardStack.add(card);
        card.setVisible(true);
        ImageView imageView = instanceImageView(card.getImgRes(getContext()));

        // 将enable设置为true，使其可以响应触摸事件
        imageView.setEnabled(true);
        addView(imageView);
        mCardViews.add(imageView);

        //
        updateState();
    }

    /**
     * 将栈顶牌pop出，并删除对应的view
     * @return 栈顶牌
     */
    public Card popCard() {
        removeView(mCardViews.get(mCardViews.size() - 1));
        mCardViews.remove(mCardViews.size() - 1);
        return mCardStack.pop();
    }

    /**
     * 获得栈顶的牌
     * @return 如果栈为空 返回null, 否则返回栈顶的Card
     * @see Stack#peek()
     */
    public Card peekCard() {
        return mCardStack.isEmpty() ? null : mCardStack.peek();
    }

    /**
     * 判断栈是否为空
     * @see Stack#isEmpty()
     */
    public boolean isEmpty() {
        return mCardStack.isEmpty();
    }

    /**
     * 更新各个牌对应的View的图片资源以及是否可用
     */
    public void updateState() {
        if (mCardViews.size() > 0) {
            mCardStack.peek().setVisible(true);
            mCardViews.get(mCardViews.size() - 1).setImageResource(mCardStack.peek().getImgRes(getContext()));
            mCardViews.get(mCardViews.size() - 1).setEnabled(true);
        }
    }

    /**
     * 获取牌叠对应的所有View
     */
    public List<ImageView> getCardViews() {
        return mCardViews;
    }

    /**
     * 设置牌叠对应的所有View
     */
    public void setCardViews(List<ImageView> cardViews) {
        mCardViews = cardViews;
    }

    /**
     * 获取区域标记值
     */
    public int getFlag() {
        return mFlag;
    }

    /**
     * 设置区域标记值
     */
    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    @Override
    public void updateTheme() {
        removeAllViews();
        setCards(mCardStack);
    }

    @Override
    public boolean isWin() {
        boolean isWin = true;
        for (Card card : mCardStack) {
            if (!card.isVisible()) {
                isWin = false;
                break;
            }
        }
        return isWin;
    }
}