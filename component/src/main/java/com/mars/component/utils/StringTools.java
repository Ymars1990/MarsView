package com.mars.component.utils;

import android.graphics.Paint;
import android.util.Log;

/**
 * String 工具类
 *
 * @author Mars
 */
public class StringTools {
    private final static String TAG = StringTools.class.getSimpleName();

    /**
     * 判断非空String
     *
     * @param string
     * @return
     */
    public static boolean strIsNotNull(String string) {
        return string != null && string.replaceAll(" ", "").length() != 0;
    }

    /**
     * 测量文字宽高
     */
    public static float[] mesureText(Paint paint, String str) {
        float[] wh = new float[2];
        wh[0] = paint.measureText(str);
        wh[1] = paint.descent() - paint.ascent();
        Log.e(TAG, "  wh[0]:" + wh[0]);
        Log.e(TAG, "  wh[1]:" + wh[1]);
        return wh;
    }
}
