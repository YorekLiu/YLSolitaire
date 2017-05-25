package yorek.com.solitaire.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yorek.com.solitaire.GlobalApplication;
import yorek.com.solitaire.ICardMovable;
import yorek.com.solitaire.PermissionManager;
import yorek.com.solitaire.R;
import yorek.com.solitaire.Updatable;
import yorek.com.solitaire.bean.Card;
import yorek.com.solitaire.bean.GameBoard;
import yorek.com.solitaire.calc.GameController;
import yorek.com.solitaire.calc.GameGenerator;
import yorek.com.solitaire.util.DisplayUtils;
import yorek.com.solitaire.view.ChoiceImageView;
import yorek.com.solitaire.view.ChosenPokerPileViewGroup;
import yorek.com.solitaire.view.ColorDialog;
import yorek.com.solitaire.view.PokerPileViewGroup;
import yorek.com.solitaire.view.TargetImageView;
import yorek.com.solitaire.view.WinnerDialog;

public class GameActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GameGenerator.OnGameGeneratorCallback,
        ColorDialog.OnColorSelectedListener {

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final int REQUEST_CODE_THEME = 0x1001;
    public static final int REQUEST_CODE_SETTING = 0x1001;

    private PermissionManager mPermissionManager;
    private boolean mIsAllPermissionGranted;

    /**
     * 目标区域的四个View
     */
    private TargetImageView[] targetsView;
    /**
     * 待选区的view
     */
    private ChoiceImageView choiceView;
    /**
     * 已选区域的ViewGroup
     */
    private ChosenPokerPileViewGroup chosenView;
    /**
     * 操作区域的七个ViewGroup
     */
    private PokerPileViewGroup[] operatorsView;

    /**
     * 目标区域在layout中的id
     */
    private int[] targetIds = {
            R.id.target1, R.id.target2, R.id.target3, R.id.target4 };
    /**
     * 操作区域在layout中的id
     */
    private int[] operatorIds = {
            R.id.operator1, R.id.operator2, R.id.operator3, R.id.operator4,
            R.id.operator5, R.id.operator6, R.id.operator7};
    /**
     * 所有的牌区，用来切换皮肤以及判断是否达到胜利条件
     */
    private List<Updatable> mUpdatables;

    private DrawerLayout drawer;
    private View mRootView;
    /**
     * 计步器
     */
    private TextView mStepView;
    /**
     * 计时器
     */
    private TextView mTimeView;
    /**
     * 游戏难度
     */
    private TextView mGameLevelView;
    /**
     * 背景颜色选择器
     */
    private ColorDialog mColorDialog;

    private Timer mTimer;
    private boolean mIsTimerInitialize;

    /**
     * 待选区域的点击事件
     */
    private View.OnClickListener mChoiceViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            choiceView.onClicked(chosenView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 初始化视图
        initView();

        // 设置发牌完成后的回调，并开始发牌
        GameGenerator.getInstance().setCallback(this);
        GameGenerator.getInstance().beginGenerator();

        // 打印牌局的信息
        GameBoard.showGameBoard();

        // 检查权限
        mPermissionManager = new PermissionManager(this);

        // 初始化游戏信息栏
        mGameLevelView.setText(String.format(getString(R.string.level),
                getResources().getStringArray(R.array.game_level)[GameController.getGameLevel()]));
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTimer == null && mIsTimerInitialize) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateTime();
                }
            }, 0, 1000);
        }

        if (mPermissionManager.requestAppLaunchPermissions()) {
            mIsAllPermissionGranted = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 初始化视图，设置LayoutParams和Listener
     */
    private void initView() {
        // 讲所有区域添加到数组，切换皮肤时可以一次切换完
        mUpdatables = new ArrayList<>(GameBoard.CARD_PILES_OPERATOR + GameBoard.CARD_PILES_TARGET + 2);

        choiceView = (ChoiceImageView) findViewById(R.id.choice);

        // 目标区域以及待选区的宽高为一扑克牌的宽高即可
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                GlobalApplication.sCardWidth, GlobalApplication.sCardHeight);
        targetsView = new TargetImageView[GameBoard.CARD_PILES_TARGET];
        for (int i = 0; i < GameBoard.CARD_PILES_TARGET; i++) {
            targetsView[i] = (TargetImageView) findViewById(targetIds[i]);
            targetsView[i].setLayoutParams(params);
            mUpdatables.add(targetsView[i]);
        }
        choiceView.setLayoutParams(params);
        mUpdatables.add(choiceView);

        // 操作区和已选区域的宽高由其自身包含的扑克数决定
        chosenView = (ChosenPokerPileViewGroup) findViewById(R.id.chosen);
        operatorsView = new PokerPileViewGroup[GameBoard.CARD_PILES_OPERATOR];
        for (int i = 0; i < GameBoard.CARD_PILES_OPERATOR; i++) {
            operatorsView[i] = (PokerPileViewGroup) findViewById(operatorIds[i]);
            mUpdatables.add(operatorsView[i]);
        }
        mUpdatables.add(chosenView);

        // 设置待选区的监听器，使其可以发牌以及重新发牌
        choiceView.setOnClickListener(mChoiceViewClickListener);

        mRootView = findViewById(R.id.activity_main);
        mStepView = (TextView) findViewById(R.id.step);
        mTimeView = (TextView) findViewById(R.id.time);
        mGameLevelView = (TextView) findViewById(R.id.game_level);

        // 长按背景，设置背景颜色
        mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mColorDialog == null) {
                    mColorDialog = new ColorDialog(GameActivity.this);
                    mColorDialog.setOnColorSelectedListener(GameActivity.this);
                    mColorDialog.setOnShowListener(mOnShowListener);
                    mColorDialog.setOnDismissListener(mOnDismissListener);
                    mColorDialog.setOwnerActivity(GameActivity.this);
                }
                mColorDialog.show();
                return false;
            }
        });
    }

    private DialogInterface.OnShowListener mOnShowListener = new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    };

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            startTimer();
        }
    };

    /**
     * 发牌完成后的回调，通知view开始加载数据
     */
    @Override
    public void onGenerateDone() {
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        for (int i = 0; i < GameBoard.CARD_PILES_OPERATOR; i++) {
            operatorsView[i].setCards(GameBoard.getOperatorCardPiles().get(i));
        }

        for (int i = 0; i < GameBoard.CARD_PILES_TARGET; i++) {
            targetsView[i].setTargetStack(GameBoard.getTargetCardPiles().get(i));
        }

        chosenView.setCards(GameBoard.getChosenCardPiles());
        choiceView.setChoiseStack(GameBoard.getChoiceCardPiles());
    }

    /**
     * 重新开始游戏<br />
     * 一个简单的办法：重启activity即可
     */
    public void restart(View v) {
        // TODO 需要优化，这种办法体验不好
        finish();
        startActivity(new Intent(this, GameActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        GameController.resetStep();
        GameController.resetTime();
        super.finish();
    }

    /// 扑克的拖拽事件 @ {
    /**
     * 拖拽时产生的ViewGroup
     */
    private PokerPileViewGroup operPileGroup;
    /**
     * 手指按下时获取的相对于屏幕的x, y值
     */
    int lastX, lastY;
    int firstTouchX, firstTouchY;
    public void onTouchEvent(ICardMovable cardMovable, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!GameController.isMoving()) {
                    GameController.setIsMoving(true);
                    Log.d("Yorek", "GameController.setIsMoving(true);");
                }

                // 禁止Drawer
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                // 保存x, y值
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                // 保存手指按下时的值，为了模拟点击事件
                firstTouchX = lastX;
                firstTouchY = lastY;

                // 将要移动牌保存至一个PokerPileViewGroup里面，方便处理拖拽效果
                if (operPileGroup == null) {
                    operPileGroup = new PokerPileViewGroup(this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            GlobalApplication.sCardWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                    operPileGroup.setCards(GameController.getOperatorStack());

                    // 获取left top的间距，不然产生的PokerPileViewGroup会出现在左上角
                    int left = lastX - GlobalApplication.sCardWidth / 2;
                    int top = (int) (lastY - GlobalApplication.sCardHeight / 2 - DisplayUtils.dp2px(this, 20));
                    params.setMargins(left, top, 0, 0);

                    // 将创建的PokerPileViewGroup添加至Activity
                    this.addContentView(operPileGroup, params);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // 计算并设置dx, dy，产生拖拽效果
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = operPileGroup.getLeft() + dx;
                int top = operPileGroup.getTop() + dy;
                int right = operPileGroup.getRight() + dx;
                int bottom = operPileGroup.getBottom() + dy;
                operPileGroup.layout(left, top, right, bottom);

                // 更新x, y
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 移除手指按下时产生的PokerPileViewGroup
                ((ViewGroup) operPileGroup.getParent()).removeView(operPileGroup);
                operPileGroup = null;

                // 判断是否为点击事件
                boolean isDealed = false;
                if (isSingleTapUp((int) event.getRawX(), (int) event.getRawY(), firstTouchX, firstTouchY)) {
                    Card gcTopCard = GameController.getTopCard();
                    if (gcTopCard != null) {
                        // 处理目标区
                        for (TargetImageView targetImageView : targetsView) {
                            if (targetImageView != cardMovable && (targetImageView.isEmpty() && gcTopCard.getCardValue() == Card.CARD_VALUE_A ||
                                    !targetImageView.isEmpty() && targetImageView.peekCard().isTargetContinue(gcTopCard) && GameController.getOperatorStack().size() == 1)) {
                                // 可以摆放
                                while (!GameController.isEmpty()) {
                                    targetImageView.addCard(GameController.pop());
                                }
                                isDealed = true;
                                break;
                            }
                        }

                        if (!isDealed) {
                            // 处理操作区
                            for (PokerPileViewGroup pokerPileViewGroup: operatorsView) {
                                if (pokerPileViewGroup != cardMovable && (pokerPileViewGroup.isEmpty() && gcTopCard.getCardValue() == Card.CARD_VALUE_K ||
                                        !pokerPileViewGroup.isEmpty() && pokerPileViewGroup.peekCard().isOperatorContinue(gcTopCard))) {
                                    // 可以摆放
                                    while (!GameController.isEmpty()) {
                                        pokerPileViewGroup.addCard(GameController.pop());
                                    }
                                    isDealed = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!isDealed) {
                        // 不可以摆放，则将临时操作区的牌放回原处
                        while (!GameController.isEmpty()) {
                            cardMovable.addCard(GameController.pop());
                        }
                    } else {
                        updateStep();
                        checkIsWin();
                    }

                    // 拖拽结束
                    cardMovable.setPoped(false);
                    cardMovable.updateState();
                    GameController.setIsMoving(false);

                    // 启用Drawer
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    return;
                }

                // 判断是否可以摆放
                if (!isAccept(event, cardMovable)) {
                    // 不可以摆放，则将临时操作区的牌放回原处
                    while (!GameController.isEmpty()) {
                        cardMovable.addCard(GameController.pop());
                    }

                    // 拖拽结束
                    cardMovable.setPoped(false);
                } else {
                    // 摆放完后，更新步数
                    updateStep();
                }

                GameController.setIsMoving(false);

                // 启用Drawer
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
        }
    }

    private boolean isSingleTapUp(int x, int y, int preX, int preY) {
        int touchSlop = ViewConfiguration.getTouchSlop();
        return Math.abs(x - firstTouchX) <= touchSlop && Math.abs(y - firstTouchY) <= touchSlop;
    }

    /**
     * 判断是否可以摆放，若可以，则进行摆放，否则返回false
     * @return true 可以摆放，并进行了摆放； false 不可以摆放
     */
    private boolean isAccept(MotionEvent event, ICardMovable cardMovable) {
        // 获取手指松开时的x, y
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        // 遍历各个区域， 判断手指松开时是否处于该区域
        PokerPileViewGroup group = findFingerLeaveInPokerPile(x, y);
        if (group != null) {
            Boolean isAddCards = doAddCards(cardMovable, group);
            if (isAddCards != null) return isAddCards;
        }

        // 遍历各个区域， 判断手指松开时是否处于该区域
        TargetImageView imageView = findFingerLeaveInTarget(x ,y);
        if (imageView != null) {
            Boolean isAddCards = doAddCards(cardMovable, imageView);
            if (isAddCards != null) return isAddCards;

        }
        return false;
    }

    @Nullable
    private Boolean doAddCards(ICardMovable cardMovable, TargetImageView imageView) {
        // 在该区域
        if (imageView.isEmpty()) {  // 目标区域没有牌
            if (GameController.getTopCard() != null &&
                    GameController.getTopCard().getCardValue() == Card.CARD_VALUE_A) {
                // 目标区域没有牌，且临时操作区的栈顶为“A”
                // 满足条件，进行摆放
                imageView.addCard(GameController.pop());

                // 拖拽结束，更新状态
                cardMovable.setPoped(false);
                cardMovable.updateState();

                // 检查是否可以胜利
                checkIsWin();
                return true;
            }
            return false;
        } else if (GameController.getTopCard() != null
                && imageView.peekCard().isTargetContinue(GameController.getTopCard())
                && GameController.getOperatorStack().size() == 1) {
            // GameController.getOperatorStack().size() == 1 : 修复了"带牌"入目标区域的Bug
            // 目标区域有牌，且牌符合连接规则
            // 满足条件，进行摆放
            imageView.addCard(GameController.pop());
            // 拖拽结束，更新状态
            cardMovable.setPoped(false);
            cardMovable.updateState();

            // 检查是否可以胜利
            checkIsWin();
            return true;
        }
        return null;
    }

    @Nullable
    private Boolean doAddCards(ICardMovable cardMovable, PokerPileViewGroup group) {
        // 在该区域
        if (group.peekCard() == null) {     // 目标区域没有牌
            if (GameController.getTopCard() == null) { // 临时操作区的栈顶元素为空
                return false;
            } else if (checkInLevel()) {
                // 目标区域没有牌，且临时操作区的栈顶为“K”
                // 满足条件，进行摆放
                while (!GameController.isEmpty()) {
                    group.addCard(GameController.pop());
                }
                // 拖拽结束，更新状态
                cardMovable.setPoped(false);
                cardMovable.updateState();

                // 检查是否可以胜利
                checkIsWin();
                return true;
            } else {  // 目标区域没有牌，且临时操作区的栈顶不为“K”
                return false;
            }
        } else if (GameController.getTopCard() != null
                && group.peekCard().isOperatorContinue(GameController.getTopCard())) {
            // 目标区域有牌，且牌符合连接规则
            // 满足条件，进行摆放
            while (!GameController.isEmpty()) {
                group.addCard(GameController.pop());
            }
            // 拖拽结束，更新状态
            cardMovable.setPoped(false);
            cardMovable.updateState();

            // 检查是否可以胜利
            checkIsWin();
            return true;
        }
        return null;
    }
    /// @}

    private PokerPileViewGroup findFingerLeaveInPokerPile(int x, int y) {
        for (PokerPileViewGroup group: operatorsView) {
            if (group.isContain(x, y)) {
                return group;
            }
        }
        return null;
    }

    private TargetImageView findFingerLeaveInTarget(int x, int y) {
        for (TargetImageView imageView : targetsView) {
            if (imageView.isContain(x, y)) {
                return imageView;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;
        int requestCode = 0;
        switch (id) {
            case R.id.nav_theme:
                intent = new Intent(this, ThemeActivity.class);
                requestCode = REQUEST_CODE_THEME;
                break;

//            ¶
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (intent != null) {
            startActivityForResult(intent, requestCode);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_THEME && resultCode == Activity.RESULT_OK) {
            for (Updatable updatable : mUpdatables) {
                updatable.updateTheme();
            }
            return;
        }

        if (mColorDialog != null) {
            mColorDialog.onActivityResult(requestCode, resultCode, data,
                    mRootView.getWidth(), mRootView.getHeight());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult(), requestCode = " + requestCode);
        if (mPermissionManager.getAppLaunchPermissionRequestCode()
                == requestCode) {
            if (mPermissionManager.isAppLaunchPermissionsResultReady(
                    permissions, grantResults)) {
                mIsAllPermissionGranted = true;
            } else {
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
            return;
        }
    }

    private void checkIsWin() {
        boolean isWin = true;
        for (Updatable updatable : mUpdatables) {
            isWin = isWin && updatable.isWin();
        }

        if (isWin) {
            // 达到胜利条件
            scheduleWinner();

            // 停止计时
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void scheduleWinner() {
        Log.i(TAG, "time = " + GameController.getTime() + " --------------");

        buildWinDialog();
    }

    private void buildWinDialog() {
        AlertDialog dialog = new WinnerDialog(this);
        dialog.setOwnerActivity(this);
        dialog.show();
    }

    private boolean checkInLevel() {
        if (GameController.getGameLevel() == GameController.LEVEL_EASY) {
            return true;
        } else if (GameController.getTopCard().getCardValue() == Card.CARD_VALUE_K) {
            return true;
        }
        return false;
    }

    private void updateStep() {
        GameController.incrementStep();
        mStepView.setText(String.format(getString(R.string.step), GameController.getStep()));
    }

    private void startTimer() {
        if (mTimer == null) {
            mIsTimerInitialize = true;
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateTime();
                }
            }, 0, 1000);
        }
    }

    private void updateTime() {
        GameController.incrementTime();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimeView.setText(String.format(getString(R.string.time), GameController.getTime()));
            }
        });
    }

    @Override
    public void onColorSelected(String hexColor) {
        try {
            mRootView.setBackgroundColor(Color.parseColor(hexColor));
        } catch (Exception exception) {
            Toast.makeText(this, "Error in parse Color", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackgroundRestore() {
        mRootView.setBackgroundResource(R.drawable.bg_1);

        if (mColorDialog != null) {
            mColorDialog.recycle();
        }
    }

    @Override
    public void onBitmapChased(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        });
    }
}
