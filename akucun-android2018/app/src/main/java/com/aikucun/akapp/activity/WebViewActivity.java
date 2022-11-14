package com.aikucun.akapp.activity;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by micker on 2017/7/12.
 */

public class WebViewActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.btn_right)
    TextView btn_right;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);

        initWebView();
    }

    @Override
    public void initData() {
        String title = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_WEB_TITLE);
        if (title != null && title.length() > 0) {
            mTitleText.setText(title);
        }
        String url = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_WEB_URL);
        if (url != null && url.length() > 0) {
            webView.loadUrl(url);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("GBK");
        settings.setDefaultFontSize(16);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }

        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();
    }
}
