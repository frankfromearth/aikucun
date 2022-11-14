package com.aikucun.akapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.service.DownloadService;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.SCLog;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by micker on 2017/7/12.
 */

public class StateMentActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.content_tv)
    TextView content_tv;

    @BindView(R.id.progress)
    ProgressBar progressbar;

    String downloadURL;
    String fileName;

    private int isDownloadSucceed = 0;//0 loding, 1success, 2failed

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.statement);
    }

    @Override
    public void initData() {

        Intent intent = getIntent();

        downloadURL = intent.getStringExtra("url");
        fileName = RSAUtils.md5String(downloadURL)+".xls";

        downloadXLS();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_statement;
    }

    private void downloadXLS(){
        content_tv.setText("正在下载...");
        Intent intent=new Intent(this, DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD);
        intent.putExtra(DownloadService.DOWNLOAD_URL,downloadURL);
        intent.putExtra(DownloadService.FILE_NAME,fileName);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        startService(intent);
    }

    private File contentFile() {
        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        final File file = new File(dir, fileName);
        return file;
    }

    private boolean showIntentPreview() {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(contentFile());
            intent.setDataAndType (uri, "application/vnd.ms-excel");
            this.startActivity(intent);
            return true;
        }catch (Exception ex) {

            SCLog.error("启动预览失败!");
        }
        return false;
    }

    private void doShowIntentPreview() {
        boolean flag = showIntentPreview();

        if (!flag) {
            MToaster.showShort(this,"请先安装带有预览功能的APP，如QQ,微信!",MToaster.IMG_INFO);
        }
    }


    private void showIntentShare() {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(contentFile()));//此处一定要用Uri.fromFile(file),其中file为File类型，否则附件无法发送成功。
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("*/*");
        this.startActivity(shareIntent);
    }

    @Override
    @OnClick({R.id.setting_option_preview,R.id.setting_option_share,R.id.content_tv})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.setting_option_preview:{
                if (isDownloadSucceed == 1) {
                    doShowIntentPreview();
                }
            }
            break;
            case R.id.setting_option_share:{
                if (isDownloadSucceed == 1) {
                    showIntentShare();
                }
            }
            break;
            case R.id.content_tv: {

                if (2 == isDownloadSucceed) {
                    downloadXLS();
                } else if(0 == isDownloadSucceed) {
                    MToaster.showShort(StateMentActivity.this,"正在下载",MToaster.IMG_INFO);
                } else if(1 == isDownloadSucceed) {
                    showIntentShare();
                }
            }
            break;

        }
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                StateMentActivity.this.progressbar.setProgress(progress);
                if (progress == 100) {

                    isDownloadSucceed = 1;
                    content_tv.setText("下载成功.");
                    StateMentActivity.this.progressbar.setProgress(progress);
                    StateMentActivity.this.progressbar.setVisibility(View.GONE);
                    doShowIntentPreview();
                }
            }
            else if (resultCode == DownloadService.DOWNLOAD_ERROR) {
                content_tv.setText("下载失败.点击重试");
                isDownloadSucceed = 2;
                StateMentActivity.this.progressbar.setProgress(0);
                StateMentActivity.this.progressbar.setVisibility(View.GONE);

            }
        }
    }



}
