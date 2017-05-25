package yorek.com.solitaire.util.photochooser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

import yorek.com.solitaire.util.ImageResizer;

import static android.app.Activity.RESULT_OK;

/**
 * 可以从Gallery选取或者从Camera选取Photo
 * Created by yorek on 5/9/17.
 */
public class PhotoChooser {
    private static final String TAG = PhotoChooser.class.getSimpleName();

    public static final String AUTHORITY = "yorek.com.solitaire.fileprovider";

    private static final int CODE_CHOOSE_FROM_GALLERY = 0x1030;
    private static final int CODE_CHOOSE_FROM_CAMERA = 0x1031;
    private static final int CODE_CHOOSE_FROM_CAPTURE = 0x1032;
    private static final String TEMP_FILE_NAME = "tempImg.jpg";

    private Activity mContext;
    /**
     * 从Camera选取图片需要一个位置存放图片
     */
    private File mTmpImgFile;
    /**
     * 从Gallery选取或者从Camera取得的Bitmap
     */
    private Bitmap mBitmap;

    /**
     * 取得的Bitmap通过这个回调传出去
     */
    private OnPhotoChooserListener mListener;
    public interface OnPhotoChooserListener {
        void onSuccess(Bitmap bitmap);
    }
    public void setOnPhotoChooserListener(OnPhotoChooserListener listener) {
        mListener = listener;
    }

    /// Constructor begin @{
    private PhotoChooser(Activity context, int code) {
        mContext = context;
        if (code == CODE_CHOOSE_FROM_CAMERA) {
            mTmpImgFile = new File(context.getExternalCacheDir(), TEMP_FILE_NAME);
        }
    }

    public static PhotoChooser newGalleryChooser(Context context, OnPhotoChooserListener listener) {
        if (context != null && context instanceof Activity) {
            return newGalleryChooser((Activity) context, listener);
        } else {
            throw new IllegalArgumentException("This method need an activity.");
        }
    }

    public static PhotoChooser newGalleryChooser(Activity context, OnPhotoChooserListener listener) {
        if (context != null) {
            PhotoChooser photoChooser = new PhotoChooser(context, CODE_CHOOSE_FROM_GALLERY);
            photoChooser.chooseFromGalleryInternal();
            photoChooser.setOnPhotoChooserListener(listener);
            return photoChooser;
        } else {
            throw new IllegalArgumentException("Context is null.");
        }
    }

    public static PhotoChooser newCameraChooser(Context context, OnPhotoChooserListener listener) {
        if (context != null && context instanceof Activity) {
            return newCameraChooser((Activity) context, listener);
        } else {
            throw new IllegalArgumentException("This method need an activity.");
        }
    }

    public static PhotoChooser newCameraChooser(Activity context, OnPhotoChooserListener listener) {
        if (context != null) {
            PhotoChooser photoChooser = new PhotoChooser(context, CODE_CHOOSE_FROM_CAMERA);
            photoChooser.chooseFromCameraInternal();
            photoChooser.setOnPhotoChooserListener(listener);
            return photoChooser;
        } else {
            throw new IllegalArgumentException("Context is null.");
        }
    }

    public static PhotoChooser newCaptureChooser(Context context, OnPhotoChooserListener listener) {
        if (context != null && context instanceof Activity) {
            return newCaptureChooser((Activity) context, listener);
        } else {
            throw new IllegalArgumentException("This method need an activity.");
        }
    }

    public static PhotoChooser newCaptureChooser(Activity context, OnPhotoChooserListener listener) {
        if (context != null) {
            PhotoChooser photoChooser = new PhotoChooser(context, CODE_CHOOSE_FROM_CAPTURE);
            photoChooser.chooseFromCaptureInternal();
            photoChooser.setOnPhotoChooserListener(listener);
            return photoChooser;
        } else {
            throw new IllegalArgumentException("Context is null.");
        }
    }
    /// Constructor end @{

    public void onActivityResult(final int requestCode, int resultCode, final Intent data, final int reqWidth, final int reqHeight) {
        if (resultCode == RESULT_OK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (requestCode == CODE_CHOOSE_FROM_CAMERA) {
                        mBitmap = ImageResizer.resize(mTmpImgFile.getAbsolutePath(), reqWidth, reqHeight);
                    } else if (requestCode == CODE_CHOOSE_FROM_GALLERY){
                        Uri originalUri = data.getData();
                        try {
                            mBitmap = ImageResizer.resize(mContext.getContentResolver(), originalUri, reqWidth, reqHeight);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (requestCode == CODE_CHOOSE_FROM_CAPTURE) {
                        mBitmap = ImageResizer.resize(data.getStringExtra(CaptureActivity.CAPTURE_FILE_PATH), reqWidth, reqHeight);
                    }

                    if (mListener != null) {
                        mListener.onSuccess(mBitmap);
                    }
                }
            }).start();
        }
    }

    private void chooseFromGalleryInternal() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mContext.startActivityForResult(intent, CODE_CHOOSE_FROM_GALLERY);
    }

    private void chooseFromCameraInternal() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            imageUri = Uri.fromFile(mTmpImgFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            mContext.startActivityForResult(intent, CODE_CHOOSE_FROM_CAMERA);
        } else {
            imageUri = FileProvider.getUriForFile(mContext, AUTHORITY, mTmpImgFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            mContext.startActivityForResult(intent, CODE_CHOOSE_FROM_CAMERA);
        }
    }

    private void chooseFromCaptureInternal() {
        Intent intent = new Intent(mContext, CaptureActivity.class);
        mContext.startActivityForResult(intent, CODE_CHOOSE_FROM_CAPTURE);
    }

    public void recycle() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            Log.i(TAG, "[recycle] recycle mBitmap");
            mBitmap.recycle();
            mBitmap = null;
        }
        System.gc();
    }
}
