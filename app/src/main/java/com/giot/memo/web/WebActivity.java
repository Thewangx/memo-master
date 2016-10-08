package com.giot.memo.web;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.util.SysConstants;
import com.giot.memo.view.CustomWebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra(SysConstants.WEB_URL);
        webView.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
        webView.getSettings().setBuiltInZoomControls(true);//设置使支持缩放
        webView.getSettings().setLoadsImagesAutomatically(true);//设置允许自动加载图片
        webView.loadUrl(url);
        webView.setWebViewClient(new CustomWebViewClient());
    }

}
