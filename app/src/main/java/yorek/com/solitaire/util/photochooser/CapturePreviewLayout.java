package yorek.com.solitaire.util.photochooser;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import yorek.com.solitaire.R;
import yorek.com.solitaire.util.ImageResizer;

/**
 * 拍照后选择重拍或者确定的View
 * Created by yorek on 5/11/17.
 */
public class CapturePreviewLayout extends RelativeLayout {
    private static final String TAG = CapturePreviewLayout.class.getSimpleName();

    private RelativeLayout mRootView;
    private ImageView mCaptureView;
    private ImageButton mDeleteButton;
    private ImageButton mDoneButton;

    private OnDeleteOrDoneCallback mCallback;
    public void setOnDeleteOrDoneCallback(OnDeleteOrDoneCallback callback) {
        mCallback = callback;
    }
    public interface OnDeleteOrDoneCallback {
        void onDelete();
        void onDone();
    }

    public CapturePreviewLayout(Context context) {
        this(context, null);
    }

    public CapturePreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CapturePreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initializeView();
    }

    private void initializeView() {
        LayoutInflater.from(getContext()).inflate(R.layout.capture_preview_layout, this, true);

        mRootView = (RelativeLayout) findViewById(R.id.capture_preview_layout);
        mCaptureView = (ImageView) findViewById(R.id.capture_preview);
        mDeleteButton = (ImageButton) findViewById(R.id.capture_delete);
        mDoneButton = (ImageButton) findViewById(R.id.capture_done);

        mDeleteButton.setOnClickListener(mDeleteOrDoneListener);
        mDoneButton.setOnClickListener(mDeleteOrDoneListener);
    }

    private OnClickListener mDeleteOrDoneListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mDeleteButton) {
                if (mCallback != null) {
                    mCallback.onDelete();
                }
            } else if (v == mDoneButton) {
                if (mCallback != null) {
                    mCallback.onDone();
                }
            }
        }
    };

    public void show() {
        Log.i(TAG, "show");
        mRootView.setVisibility(VISIBLE);
    }

    public void hide() {
        Log.i(TAG, "hide");
        mRootView.setVisibility(GONE);
    }

    public void setCaptureView(String filePath) {
        Log.i(TAG, "setCaptureView filePath = " + filePath);
        mCaptureView.setImageBitmap(ImageResizer.resize(filePath, mCaptureView.getWidth(), mCaptureView.getHeight()));
        show();
    }
}
