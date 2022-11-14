/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.listeners.JXDemoMessageListener;
import com.jxccp.ui.utils.JXCommonUtils;
import com.jxccp.ui.view.adapter.JXChatAdapter;

import java.io.File;

/**
 * 图片预览界面
 */
public class JXPhotoActivity extends Activity
        implements JXDemoMessageListener, View.OnClickListener {

    private RelativeLayout rootLayout;

    private ImageView photoView;

    private String messageID;

    private String localUrl;

    private String direction;

    private ProgressDialog pd;

    private DisplayMetrics metrics;

    private int screenWidth;

    private int screenHeight;

    private boolean needRefresh = false;

    private AsyncTask<Void, Void, Void> downloadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jx_activity_photo);
        metrics = getResources().getDisplayMetrics();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        rootLayout = (RelativeLayout)findViewById(R.id.rl_root);
        rootLayout.setOnClickListener(this);

        if (savedInstanceState == null) {
            rootLayout.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            rootLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                            rootLayout.startAnimation(AnimationUtils
                                    .loadAnimation(JXPhotoActivity.this, R.anim.jx_image_display));
                            return true;
                        }
                    });
        }
        photoView = (ImageView)findViewById(R.id.iv_photo);

        String htmlurl = this.getIntent().getStringExtra("html");
        if (!TextUtils.isEmpty(htmlurl)) {
            if (!htmlurl.startsWith("http")) {
                htmlurl = "http://jiaxin.faqrobot.org" + htmlurl;
            }
            Drawable drawable = JXChatAdapter.drawabelCache.get(htmlurl);
            if (drawable != null) {
                photoView.setImageDrawable(drawable);
            } else {
                Glide.with(this).load(htmlurl).placeholder(R.drawable.jx_ic_photo_default).fitCenter()
                        .into(photoView);
            }
        } else {
            messageID = this.getIntent().getStringExtra("MessageID");
            localUrl = this.getIntent().getStringExtra("LocalUrl");
            direction = this.getIntent().getStringExtra("Direction");
            JXLog.d("message id = " + messageID + " , local url = " + localUrl + " , direction = "
                    + direction);
            JXUiHelper.getInstance().addMessagListener(this);
            if (localUrl != null && fileIsExists(localUrl)) {
                setImageLayoutParams();
                if (localUrl.endsWith(".gif")) {
                    Glide.with(this).load(new File(localUrl)).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().crossFade().into(photoView);
                }else {
                    Glide.with(this).load(new File(localUrl)).fitCenter().crossFade().into(photoView);
                }
            } else {
                downloadTask = new AsyncTask<Void, Void, Void>() {

                    protected void onPreExecute() {
                        pd = new ProgressDialog(JXPhotoActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setCanceledOnTouchOutside(false);
                        pd.setMessage(getString(R.string.jx_loading_image));
                        pd.show();
                    };

                    @Override
                    protected Void doInBackground(Void... arg0) {
                        downloadImage();
                        return null;
                    }
                }.execute();
            }
        }
    }

    @Override
    protected void onStop() {
        Glide.with(JXPhotoActivity.this).onStop();
        JXUiHelper.getInstance().removeMessageListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (pd != null) {
            pd.dismiss();
        }
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
        super.onDestroy();
    }

    private void setImageLayoutParams() {
        int[] sizes = JXCommonUtils.getImageSize(localUrl);
        final int width = sizes[0];
        final int height = sizes[1];
        JXLog.i("image size, width:" + width + ", height:" + height);
        double target = metrics.density * 150;
        int scalledW;
        int scalledH;
        if (width <= height) {
            if ((height <= screenHeight && height > target) || (height > screenHeight)) {
                scalledH = screenHeight;
                target = screenHeight;
            } else {
                scalledH = (int)target;
            }
            if (target != screenHeight) {
                scalledW = (int)target;
            } else {
                scalledW = (int)(width / ((double)height / target));
            }
        } else {
            if ((width <= screenWidth && width > target) || (width > screenWidth)) {
                scalledW = screenWidth;
                target = screenWidth;
            } else {
                scalledW = (int)target;
            }
            if (target != screenWidth) {
                scalledH = (int)target;
            } else {
                scalledH = (int)(height / ((double)width / target));
            }
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scalledW,
                scalledH);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        photoView.setLayoutParams(layoutParams);
    }

    private void downloadImage() {
        JXImManager.Message.getInstance().download(messageID);
    }

    @Override
    public void onClick(View v) {
        if (R.id.rl_root == v.getId()) {
            if (needRefresh) {
                setResult(JXChatView.IMAGE_PREVW_RESULT_CODE);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (needRefresh) {
            setResult(JXChatView.IMAGE_PREVW_RESULT_CODE);
        }
        finish();
        super.onBackPressed();
    }

    public boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(JXMessage message) {
        if (!messageID.equals(message.getMessageId())) {
            return;
        }
        needRefresh = true;
        JXLog.d("download success !");
        runOnUiThread(new Runnable() {
            public void run() {
                setImageLayoutParams();
                if (localUrl.endsWith(".gif")) {
                    Glide.with(JXPhotoActivity.this).load(new File(localUrl)).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter()
                            .crossFade().into(photoView);
                } else {
                    Glide.with(JXPhotoActivity.this).load(new File(localUrl)).fitCenter().crossFade()
                            .into(photoView);
                }
                if (pd != null) {
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onError(final JXMessage message, int code, String reason) {
        if (!messageID.equals(message.getMessageId())) {
            return;
        }
        JXLog.d("download image file error ,error cod =" + code + " , error reason = " + reason);
        runOnUiThread(new Runnable() {
            public void run() {
                if(message.getStatus() == JXMessage.Status.EXPIRED){
                    JXCommonUtils.showToast(getApplicationContext(),
                            getString(R.string.jx_file_expired));
                }
            }
        });
        if (pd != null) {
            pd.dismiss();
        }
    }

    @Override
    public void onTransfered(JXMessage message, long totalSize, long currentSize) {
        if (!messageID.equals(message.getMessageId())) {
            return;
        }
    }
}
