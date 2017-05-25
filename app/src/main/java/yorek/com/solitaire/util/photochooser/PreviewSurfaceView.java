package yorek.com.solitaire.util.photochooser;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by yorek on 5/11/17.
 */
public class PreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = PreviewSurfaceView.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean mIsRear;

    public PreviewSurfaceView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public PreviewSurfaceView(Context context, Camera camera, boolean isRear) {
        this(context, camera);
        mIsRear = isRear;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.i(TAG, "[surfaceChanged]format = " + format + ", w = " + w + ", h = " + h);

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        initCamera(w, h);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void initCamera(int w, int h) {
        Camera.Parameters mParameters = mCamera.getParameters();
        Camera.Size bestSize = chooseBestPictureSize(mParameters, w, h);
        mParameters.setPictureSize(bestSize.width, bestSize.height);
        bestSize = chooseBestPreviewSize(mParameters, w, h);
        mParameters.setPreviewSize(bestSize.width, bestSize.height);
        mHolder.setFixedSize(w, h);
        mParameters.set("rotation", mIsRear ? 90 : 270);
        mCamera.setParameters(mParameters);
    }

    private Camera.Size chooseBestPictureSize(Camera.Parameters parameters, int w, int h) {
        Camera.Size bestSize = null;
        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (bestSize == null) {
                bestSize = size;
            }
            if (w * size.width == h * size.height) {
                bestSize = size;
            }
        }

        Log.i(TAG, "[chooseBestPictureSize] [width, height] = [" + bestSize.width + ", " + bestSize.height + "]");
        return bestSize;
    }

    private Camera.Size chooseBestPreviewSize(Camera.Parameters parameters, int w, int h) {
        Camera.Size bestSize = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (bestSize == null) {
                bestSize = size;
            }
            if (w * size.width == h * size.height) {
                bestSize = size;
            }
        }

        Log.i(TAG, "[chooseBestPreviewSize] [width, height] = [" + bestSize.width + ", " + bestSize.height + "]");
        return bestSize;
    }
}
