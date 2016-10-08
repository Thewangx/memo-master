package com.giot.memo.util;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * 加载图片工具类
 * Created by reed on 16/7/25.
 */
public class ImageLoader {

    public static void with(Context context, String url, ImageView imageView) {
        load(Glide.with(context), url, imageView);
    }

    public static void with(Activity activity, String url, ImageView imageView) {
        load(Glide.with(activity), url, imageView);
    }

    public static void with(android.support.v4.app.Fragment fragment, String url, ImageView imageView) {
        load(Glide.with(fragment), url, imageView);
    }

    private static void load(RequestManager manager, String url, ImageView imageView) {
        manager.load(url).into(imageView);
    }


}
