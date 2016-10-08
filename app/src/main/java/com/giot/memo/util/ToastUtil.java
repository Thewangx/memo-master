package com.giot.memo.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.giot.memo.App;

/**
 * the custom Toast
 * Created by reed on 16/7/22.
 */
public class ToastUtil {

    public static void show(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void show(@StringRes int resId) {
        Toast.makeText(App.getContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
