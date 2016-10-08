package com.giot.memo.util;

import android.util.Log;

import com.giot.memo.BuildConfig;

/**
 * the custom Log
 * Created by reed on 16/7/22.
 */
public class LogUtil {

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, formatMsg(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, formatMsg(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, formatMsg(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, formatMsg(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, formatMsg(msg));
        }
    }

    private static String formatMsg(String msg) {
        String mediaMsg = HORIZONTAL_DOUBLE_LINE + "  " + msg;
        return TOP_BORDER + "\n" + mediaMsg + "\n" + BOTTOM_BORDER;
    }

}
