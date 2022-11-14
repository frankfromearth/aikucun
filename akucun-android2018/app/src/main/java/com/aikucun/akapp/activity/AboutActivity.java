package com.aikucun.akapp.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.SCLog;

import butterknife.BindView;

/**
 * Created by micker on 2017/7/12.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.versionTv)
    TextView versionTv;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.about_us);
    }

    @Override
    public void initData() {

        versionTv.setText(getAppVersionName(getApplicationContext()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    public  String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            SCLog.debug("VersionInfo Exception " +  e);
        }
        return "V" + versionName + " build " + versionCode;
    }
}
