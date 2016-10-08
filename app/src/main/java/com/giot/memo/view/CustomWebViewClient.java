package com.giot.memo.view;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 加载网页
 * Created by reed on 16/8/30.
 */
public class CustomWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

}
