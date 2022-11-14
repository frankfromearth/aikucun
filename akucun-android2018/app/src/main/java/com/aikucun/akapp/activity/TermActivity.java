package com.aikucun.akapp.activity;

import com.aikucun.akapp.R;

/**
 * Created by micker on 2017/7/12.
 */

public class TermActivity extends WebViewActivity {


    @Override
    public void initData() {
        mTitleText.setText(R.string.terms_of_use);
        webView.loadUrl("file:///android_asset/terms.html");
    }

}
