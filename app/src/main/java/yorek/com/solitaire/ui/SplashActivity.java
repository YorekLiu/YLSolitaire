package yorek.com.solitaire.ui;

import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import yorek.com.solitaire.R;
import yorek.com.solitaire.calc.GameController;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final String AUTHORITY = "yorek.com.solitaire";
    /*
    private static final Uri LEVEL_HARD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/level_hard");
    private static final Uri LEVEL_NORMAL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/level_normal");
    private static final Uri LEVEL_EASY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/level_easy");
    */
    private static final int LEVEL_HARD_CONTENT_CODE = GameController.LEVEL_HARD;
    private static final int LEVEL_NORMAL_CONTENT_CODE = GameController.LEVEL_NORMAL;
    private static final int LEVEL_EASY_CONTENT_CODE = GameController.LEVEL_EASY;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "level_hard", LEVEL_HARD_CONTENT_CODE);
        sUriMatcher.addURI(AUTHORITY, "level_normal", LEVEL_NORMAL_CONTENT_CODE);
        sUriMatcher.addURI(AUTHORITY, "level_easy", LEVEL_EASY_CONTENT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) {
            int level = sUriMatcher.match(getIntent().getData());
            beginGameWithLevel(level);
        } else {
            setContentView(R.layout.activity_splash);
            Button beginGameButton = (Button) findViewById(R.id.btn_begin_game);
            beginGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioGroup levelRadioGroup = (RadioGroup) findViewById(R.id.level_selector);
                    int checkedRatioButtonId = levelRadioGroup.getCheckedRadioButtonId();
                    int[] buttonIds = {R.id.level_hard_rbtn, R.id.level_normal_rbtn, R.id.level_easy_rbtn};
                    String[] shortcutIds = {"level_hard", "level_normal", "level_easy"};
                    for (int i = 0; i < buttonIds.length; i++) {
                        if (buttonIds[i] == checkedRatioButtonId) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                                shortcutManager.reportShortcutUsed(shortcutIds[i]);
                            }
                            beginGameWithLevel(i);
                        }
                    }
                }
            });
        }
    }

    private void beginGameWithLevel(int level) {
        GameController.setGameLevel(level);
        beginGame();
    }

    private void beginGame() {
        startActivity(new Intent(this, GameActivity.class));
        overridePendingTransition(0, 0);
        this.finish();
    }
}
