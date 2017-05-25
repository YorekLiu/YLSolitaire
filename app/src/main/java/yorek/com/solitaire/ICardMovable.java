package yorek.com.solitaire;

import yorek.com.solitaire.bean.Card;

/**
 * 操作区、目标区公共方法接口
 * Created by liuyangyao on 2016/11/13.
 */
public interface ICardMovable {
    /**
     * 添加一张卡片
     */
    void addCard(Card card);

    /**
     * 更新视图
     */
    void updateState();

    /**
     * 设置当前状态是否正处于拖动状态
     */
    void setPoped(boolean poped);
}
