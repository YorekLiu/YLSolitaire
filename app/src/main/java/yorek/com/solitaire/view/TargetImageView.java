package yorek.com.solitaire.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Stack;

import yorek.com.solitaire.ICardMovable;
import yorek.com.solitaire.R;
import yorek.com.solitaire.Updatable;
import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.calc.GameController;
import yorek.com.solitaire.ui.GameActivity;

/**
 * 目标区域ViewGroup
 * Created by yorek on 11/13/16.
 */
public class TargetImageView extends ImageView implements ICardMovable, Updatable {

    private Stack<Card> mTargetStack;

    /**
     * touch时是否已经将Card放入临时牌叠中
     */
    private boolean mIsPoped;

    /**
     * 记录该View在的区域范围
     */
    protected Rect mRect;

    public TargetImageView(Context context) {
        super(context);
    }

    public TargetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TargetImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TargetImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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

    public Stack<Card> getTargetStack() {
        return mTargetStack;
    }

    public void setTargetStack(Stack<Card> targetStack) {
        mTargetStack = targetStack;
        updateState();
    }

    /**
     * 更新该View要显示的资源
     */
    @Override
    public void updateState() {
        if (!mTargetStack.isEmpty())
            setImageResource(mTargetStack.peek().getImgRes(getContext()));
        else
            setImageResource(R.drawable.placeholder);
    }

    /**
     * 判断栈是否为空
     * @see Stack#isEmpty()
     */
    public boolean isEmpty() {
        return mTargetStack.isEmpty();
    }

    /**
     * 向目标区添加一张牌
     */
    public void addCard(Card card) {
        mTargetStack.add(card);
        updateState();
    }

    /**
     * 将栈顶牌pop出，并删除对应的view
     * @return 栈顶牌
     */
    public Card popCard() {
        if (mTargetStack.size() == 0)
            return null;
        Card card = mTargetStack.pop();
        updateState();
        return card;
    }

    /**
     * 获得栈顶的牌
     * @return 返回栈顶的Card
     * @see Stack#peek()
     */
    public Card peekCard() {
        return mTargetStack.peek();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 屏蔽掉多指事件，一定要return false不然会继续执行下去
        if (GameController.isMoving() && event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }

        if (!mIsPoped) {
            Card popCard = popCard();
            if (popCard == null)
                return true;
            GameController.push(popCard);
            mIsPoped = true;
        }

        ((GameActivity) getContext()).onTouchEvent(this, event);
        return true;
    }

    /**
     * 设置要放入的牌是否已经放入 < br/>
     * 该方法不是不需，只是为了实现接口而写的
     */
    @Override
    public void setPoped(boolean poped) {
        mIsPoped = poped;
    }

    @Override
    public void updateTheme() {
        updateState();
    }

    @Override
    public boolean isWin() {
        return true;
    }
}
