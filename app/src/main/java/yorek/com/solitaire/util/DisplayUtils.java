package yorek.com.solitaire.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Pair;

/**
 * 获取屏幕大小尺寸的工具类
 * Created by yorek on 11/2/16.
 */
public class DisplayUtils {

    /**
     * 获取屏幕的分辨率大小
     * @return 屏幕宽、高的pair,可使用pair.first、pair.second来获取宽和高
     * @see Pair#first
     * @see Pair#second
     */
    public static Pair<Integer, Integer> getDisplayPixels(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return new Pair<>(width, height);
    }

    /**
     * 获取屏幕密度
     * @return 屏幕密度
     */
    public static float getDisplayDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.density;
    }

    /**
     * 像素转dp
     */
    public static float px2dp(Context context, int px) {
        return px / getDisplayDensity(context);
    }

    /**
     * dp转像素
     */
    public static float dp2px(Context context, int dp) {
        return dp * getDisplayDensity(context);
    }
}
