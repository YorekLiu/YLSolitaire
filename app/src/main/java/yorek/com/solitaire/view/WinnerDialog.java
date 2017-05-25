package yorek.com.solitaire.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import yorek.com.solitaire.R;
import yorek.com.solitaire.calc.GameController;
import yorek.com.solitaire.ui.GameActivity;

/**
 *
 * Created by yorek on 12/28/16.
 */
public class WinnerDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private static final String TAG = "WinnerDialog";

    private static final int STEP_COEFFICIENT = 300;
    private static final int TOTAL_SCORE = 30000;
    public static final int ANIMATION_DURATION = 300;

    private TextView mScoreTextView;

    public WinnerDialog(@NonNull Context context) {
        super(context);
        initialize();
    }

    public WinnerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initialize();
    }

    public WinnerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize();
    }

    private void initialize() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_winner, null);

        TextView stepView = (TextView) contentView.findViewById(R.id.step);
        TextView timeView = (TextView) contentView.findViewById(R.id.time);
        TextView gameLevelView = (TextView) contentView.findViewById(R.id.game_level);
        mScoreTextView = (TextView) contentView.findViewById(R.id.score_view);

        stepView.setText(String.valueOf(GameController.getStep()));
        timeView.setText(String.valueOf(GameController.getTime()));
        gameLevelView.setText(getContext().getResources().getStringArray(R.array.game_level)[GameController.getGameLevel()]);

        setTitle(R.string.congratulation);
        setView(contentView);
        setCancelable(false);
        setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.you_win), this);
    }

    @Override
    public void show() {
        super.show();

        beginCalculator();
    }

    private void beginCalculator() {
        int totalScore = getScore();

        setScoreWithAnimation(mScoreTextView, totalScore, ANIMATION_DURATION);
    }

    private void setScoreWithAnimation(final TextView textView, final int score, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, score);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int nowScore = (int) (valueAnimator.getAnimatedFraction() * score);
                textView.setText(String.valueOf(nowScore));
            }
        });
        valueAnimator.start();
    }

    private int getScore() {
        return (TOTAL_SCORE - GameController.getStep() * STEP_COEFFICIENT) + (TOTAL_SCORE - GameController.getTime());
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == AlertDialog.BUTTON_POSITIVE) {
            if (getOwnerActivity() != null && getOwnerActivity() instanceof GameActivity) {
                ((GameActivity) getOwnerActivity()).restart(null);
            }
        }
    }
}
