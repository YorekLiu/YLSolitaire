package yorek.com.solitaire.util;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * 压缩Image至指定的大小
 * Created by yorek on 5/10/17.
 */
public class ImageResizer{

    private static final String TAG = "ImageResizer";

    public static Bitmap resize(ContentResolver cr, Uri url, int reqWidth, int reqHeight) {
        InputStream input = null;
        try {
            input = cr.openInputStream(url);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error in cr.openInputStream(url) : " + e.getMessage());
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeStream(input, null, options);

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error in input.close() : " + e.getMessage());
            return null;
        }

        options.inSampleSize = calcSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        try {
            input = cr.openInputStream(url);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error in cr.openInputStream(url) : " + e.getMessage());
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error in input.close() : " + e.getMessage());
            return null;
        }

        return bitmap;
    }

    public static Bitmap resize(String fileName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(fileName, options);

        options.inSampleSize = calcSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(fileName, options);
    }

    public static Bitmap resize(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calcSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    public static Bitmap resize(Resources resources, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(resources, resId, options);

        options.inSampleSize = calcSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, resId, options);
    }

    private static int calcSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while (((halfHeight / inSampleSize) >= reqHeight)
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.i(TAG, "inSampleSize = " + inSampleSize +", [outWidth, outHeight] = ["
            + height + ", " + width + "], [reqWidth, reqHeight] = [" + reqWidth + ", " + reqHeight + "]");
        return inSampleSize;
    }
}
