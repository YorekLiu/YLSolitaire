package yorek.com.solitaire.util.photochooser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import yorek.com.solitaire.R;

public class CaptureActivity extends AppCompatActivity {
    private static final String TAG = CaptureActivity.class.getSimpleName();

    public static final String CAPTURE_FILE_PATH = "capture_file_path";

    private Camera mCamera;
    private PreviewSurfaceView mPreviewSurfaceView;
    private CapturePreviewLayout mCapturePreviewLayout;
    private FrameLayout previewLayout;
    private File mPictureFile;

    private boolean mIsRear = true;

    private View.OnClickListener mSwitchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mIsRear = !mIsRear;
            releaseCamera();
            initCamera();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        if (!checkCameraHardware(this)) {
            Log.d(TAG, "There is no camera hardware in your mobile phone.");
            Toast.makeText(getApplicationContext(), R.string.msg_no_camera_detected, Toast.LENGTH_LONG).show();
            finish();
        }

        previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        mCapturePreviewLayout = (CapturePreviewLayout) findViewById(R.id.capture_preview_layout);
        mCapturePreviewLayout.setOnDeleteOrDoneCallback(mCallback);

        // Add a listener to the Capture button
        ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
        ImageButton switchCameraButton = (ImageButton) findViewById(R.id.button_switch_camera);
        switchCameraButton.setOnClickListener(mSwitchCameraListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void initCamera() {
        // Create an instance of Camera
        if (mIsRear) {
            mCamera = getCameraInstance();
        } else {
            mCamera = openFrontFacingCameraGingerbread();
        }

        // Create our Preview view and set it as the content of our activity.
        mPreviewSurfaceView = new PreviewSurfaceView(this, mCamera, mIsRear);
        previewLayout.addView(mPreviewSurfaceView);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        previewLayout.removeView(mPreviewSurfaceView);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera is not available (in use or does not exist) : " + e.getMessage());
            Toast.makeText(getApplicationContext(), R.string.msg_camera_not_available, Toast.LENGTH_LONG).show();
            finish();
        }
        return camera; // returns null if camera is unavailable
    }

    private Camera openFrontFacingCameraGingerbread() {
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), R.string.msg_camera_not_available, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        return cam;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mPictureFile = getOutputMediaFile();
            if (mPictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(mPictureFile);
                fos.write(data);
                fos.close();

                mCapturePreviewLayout.setCaptureView(mPictureFile.getAbsolutePath());
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

        }
    };

    private File getOutputMediaFile(){
        //get the mobile Pictures directory
        File picDir = getExternalCacheDir();

        //get the current time
        String timeStamp = new SimpleDateFormat(getString(R.string.picture_format),
                Locale.ENGLISH).format(new Date());

        return new File(picDir.getPath() + File.separator + "Capture_"+ timeStamp + ".jpg");
    }

    private CapturePreviewLayout.OnDeleteOrDoneCallback mCallback = new CapturePreviewLayout.OnDeleteOrDoneCallback() {
        @Override
        public void onDelete() {
            mCapturePreviewLayout.hide();
        }

        @Override
        public void onDone() {
            setResult(CaptureActivity.RESULT_OK,
                    new Intent().putExtra(CAPTURE_FILE_PATH, mPictureFile.getAbsolutePath()));
            finish();
        }
    };
}
