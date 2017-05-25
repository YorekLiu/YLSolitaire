package yorek.com.solitaire.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import yorek.com.solitaire.R;
import yorek.com.solitaire.util.photochooser.PhotoChooser;

/**
 *
 * Created by yorek on 12/28/16.
 */
public class ColorDialog extends AlertDialog implements TextWatcher, View.OnClickListener {

    private String mHexColor;
    private EditText mColorEditView;
    private Button mAlbumButton;
    private Button mCameraButton;
    private Button mCaptureButton;
    private PhotoChooser mPhotoChooser;

    private DialogInterface.OnClickListener mOkResetClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                if (mListener != null) {
                    if (updateColor(mColorEditView.getText().toString())) {
                        mListener.onColorSelected(mHexColor);
                    }
                }
            } else if (which == AlertDialog.BUTTON_NEUTRAL) {
                if (mListener != null) {
                    mListener.onBackgroundRestore();
                }
            }
        }
    };

    private OnColorSelectedListener mListener;
    public interface OnColorSelectedListener {
        void onColorSelected(String hexColor);
        void onBackgroundRestore();
        void onBitmapChased(Bitmap bitmap);
    }

    public ColorDialog(@NonNull Context context) {
        super(context);
        initialize();
    }

    public ColorDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initialize();
    }

    public ColorDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize();
    }

    private void initialize() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color, null);
        mColorEditView = (EditText) contentView.findViewById(R.id.color_edit_text);
        mColorEditView.addTextChangedListener(this);

        mAlbumButton = (Button) contentView.findViewById(R.id.select_from_album);
        mAlbumButton.setOnClickListener(this);

        mCameraButton = (Button) contentView.findViewById(R.id.select_from_camera);
        mCameraButton.setOnClickListener(this);

        mCaptureButton = (Button) contentView.findViewById(R.id.select_from_capture);
        mCaptureButton.setOnClickListener(this);

        setTitle(R.string.color_selector);
        setView(contentView);
        setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), mOkResetClickListener);
        setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.restore), mOkResetClickListener);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateColor(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean updateColor(String hexColor) {
        if (hexColor != null && hexColor.length() == 6) {
            int color;
            try {
                mHexColor = hexColor.startsWith("#") ? hexColor : "#" + hexColor;
                color = Color.parseColor(mHexColor);
                mColorEditView.setBackgroundColor(color);
                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mAlbumButton) {
            mPhotoChooser = PhotoChooser.newGalleryChooser(getOwnerActivity(), mOnPhotoChooserListener);
        } else if (v == mCameraButton) {
            mPhotoChooser = PhotoChooser.newCameraChooser(getOwnerActivity(), mOnPhotoChooserListener);
        } else if (v == mCaptureButton) {
            mPhotoChooser = PhotoChooser.newCaptureChooser(getOwnerActivity(), mOnPhotoChooserListener);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, int reqWidth, int reqHeight) {
        mPhotoChooser.onActivityResult(requestCode, resultCode, data, reqWidth, reqHeight);
        dismiss();
    }

    private PhotoChooser.OnPhotoChooserListener mOnPhotoChooserListener = new PhotoChooser.OnPhotoChooserListener() {
        @Override
        public void onSuccess(Bitmap bitmap) {
            if (bitmap != null && mListener != null) {
                mListener.onBitmapChased(bitmap);
            }
        }
    };

    public void recycle() {
        if (mPhotoChooser != null) {
            mPhotoChooser.recycle();
        }
    }
}
