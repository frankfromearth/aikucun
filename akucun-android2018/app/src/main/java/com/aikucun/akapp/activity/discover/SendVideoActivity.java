package com.aikucun.akapp.activity.discover;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.video.AlyVideoConstant;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.DiscoverApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AlyVideoUploadUtil;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.utils.VideoUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.aswife.ui.MaskImageView;
import com.qiyunxin.android.http.utils.Utils;
import com.qyx.android.weight.view.MyToast;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * Created by ak123 on 2017/11/17.
 * 发布视频
 */

public class SendVideoActivity extends BaseActivity {

    @BindView(R.id.title_et)
    EditText title_et;
    @BindView(R.id.choose_video_iv)
    ImageView choose_video_iv;
    @BindView(R.id.delete_iv)
    ImageView delete_iv;
    @BindView(R.id.duration)
    TextView durationTv;
    @BindView(R.id.image)
    MaskImageView image;
    @BindView(R.id.send_text_et)
    EditText send_text_et;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.textCount)
    TextView textCount;
    @BindView(R.id.video_layout)
    FrameLayout video_layout;
    private String mStrContent, mTitle;
    private String coverPath, videoPath;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.publish);
    }

    @Override
    public void initData() {
        initListener();
        checkPermission();
    }

    private void initListener() {
        delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrContent = send_text_et.getText().toString();
                mTitle = title_et.getText().toString();
//                publishDiscover("125381d6825c43918c121865d70dd5bc", "http://video.akucun.com/watermark/image_cover/A72F996B320045E6914D1F3EF91BF9F8-10000-2.png");
                if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(videoPath)) {
                    getUploadVideoToken();
                }
            }
        });
//        send_text_et.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//                                      int arg3) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1,
//                                          int arg2, int arg3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int number = 400 - s.length();
//                if (number >= 0) {
//                    textCount.setText("" + number);
//                }
//            }
//        });
        choose_video_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseVideoDailog();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(send_text_et);
                JCVideoPlayerStandard.startFullscreen(SendVideoActivity.this, JCVideoPlayerStandard.class, videoPath, "");
            }

        });
    }

    private void showDeleteDialog() {
        MyDialogUtils.showV7NormalDialog(SendVideoActivity.this, R.string.delete_selected_video_prompt, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                videoPath = "";
                video_layout.setVisibility(View.GONE);
                coverPath = "";
                choose_video_iv.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_video;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK && data != null) {
            int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE, 0);
            if (type == AliyunVideoRecorder.RESULT_TYPE_RECORD) {
                videoPath = data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
                Log.e("视频地址：", videoPath);
                try {
                    Log.e("视频大小：", VideoUtils.getFileSize(videoPath));
                    //封面地址
                    coverPath = VideoUtils.getVideoCover(videoPath);
                    if (!TextUtils.isEmpty(coverPath))
                        image.SetUrl(coverPath);
                    String duration = VideoUtils.getVideoDuration(videoPath);
                    durationTv.setText(duration);

                    video_layout.setVisibility(View.VISIBLE);
                    choose_video_iv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == AliyunVideoRecorder.RESULT_TYPE_CROP) {
                videoPath = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                long time = data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0);
                coverPath = VideoUtils.getVideoCover(videoPath);
                if (!TextUtils.isEmpty(coverPath))
                    image.SetUrl(coverPath);
                String duration = VideoUtils.getTime((int) time / 1000);
                durationTv.setText(duration);
                //加载到ImageView控件上
                video_layout.setVisibility(View.VISIBLE);
                choose_video_iv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 发布动态
     *
     * @param videoId
     * @param converUrl
     */
    private void publishDiscover(String videoId, String converUrl) {
        updateProgress(getResources().getString(R.string.data_uploading));
//        showProgress(getResources().getString(R.string.data_uploading));
        DiscoverApiManager.pubishDiscoverByVideo(this, mTitle, mStrContent, "", "", "", videoId, converUrl, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                if (!TextUtils.isEmpty(coverPath)) {
                    File file = new File(coverPath);
                    if (file != null && file.exists()) file.delete();
                }
                if (!TextUtils.isEmpty(videoPath)) {
                    File videoFile = new File(videoPath);
                    if (videoFile != null && videoFile.exists()) videoFile.delete();
                }
                cancelProgress();
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_DISCOVER_LIST_REFRESH));
                finish();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                MyToast.makeText(SendVideoActivity.this, message, MyToast.LENGTH_SHORT);
            }
        });

    }

    /**
     * 发送视频
     */
    private void sendVideo() {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        getWindowManager().getDefaultDisplay()
//                .getMetrics(dm);
//        int h = 960;
//        if (dm.heightPixels < 960) {
//            h = dm.heightPixels;
//        }
//        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
////                .doH264Compress(true)
//                .smallVideoWidth(480)
////                .smallVideoWidth(dm.widthPixels)
//                .smallVideoHeight(360)
//                .recordTimeMax(10 * 1000)
//                .maxFrameRate(20)
//                .minFrameRate(8)
//                .captureThumbnailsTime(1)
//                .recordTimeMin((int) (1.5 * 1000))
//                .build();
//        Intent intent = new Intent(SendVideoActivity.this, MediaRecorderActivity.class);
//        intent.putExtra("media_recorder_config_key", config);
//        startActivityForResult(intent, 102);
    }

    /**
     * 获取上传视频token
     */
    private void getUploadVideoToken() {
        AlyVideoUploadUtil.getVideoToken(SendVideoActivity.this, new AlyVideoUploadUtil.IGetVideoTokenListener() {
            @Override
            public void onResult(String akId, String akSecret, String token, String exTime) {
                if (!TextUtils.isEmpty(akId) && !TextUtils.isEmpty(akSecret) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(exTime)) {
                    uploadVideoToAly();
                }
            }
        });
    }

    /**
     * 上传视频到阿里云
     */
    private void uploadVideoToAly() {
        String akId = AppContext.get(AlyVideoConstant.ALY_AKID, "");
        String akSecret = AppContext.get(AlyVideoConstant.ALY_AKSECRET, "");
        String token = AppContext.get(AlyVideoConstant.ALY_SECTOKEN, "");
        String exTime = AppContext.get(AlyVideoConstant.ALY_EXPIRATION, "");
        if (TextUtils.isEmpty(akId) || TextUtils.isEmpty(akSecret) || TextUtils.isEmpty(token) || TextUtils.isEmpty(exTime)) {
            return;
        }

        showProgress(getResources().getString(R.string.uploading_video));
        AlyVideoUploadUtil alyVideoUploadUtil = new AlyVideoUploadUtil(this);
        alyVideoUploadUtil.uploadVideo(videoPath, akId, akSecret, token, exTime, coverPath, new AlyVideoUploadUtil.IVideoUploadListener() {
            @Override
            public void onSuccessResult(String videoId, String imageUrl) {
                if (!TextUtils.isEmpty(videoId) && !TextUtils.isEmpty(imageUrl)) {
                    // TODO: 2017/12/1 删除本地已转码的封面和视频
                    Message msg = myHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = "";
                    myHandler.sendMessage(msg);
                    //发布视频
                    Message publishMsg = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("videoId", videoId);
                    bundle.putString("imageUrl", imageUrl);
                    publishMsg.setData(bundle);
                    publishMsg.what = 2;
                    myHandler.sendMessage(publishMsg);
                } else {
                    cancelProgress();
                    MyToast.makeText(SendVideoActivity.this, getResources().getString(R.string.upload_video_error), MyToast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                long progress = uploadedSize * 100 / totalSize;
                Message msg = myHandler.obtainMessage();
                msg.what = 1;
                msg.obj = progress + "%";
                myHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        } else {
            String content = send_text_et.getText().toString();
            String title = title_et.getText().toString();
            if (!TextUtils.isEmpty(videoPath) || !TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
                showDialog();
            } else {
                super.onBackPressed();
                AppManager.getAppManager().finishActivity(this);
            }

        }

    }

    private void showChooseVideoDailog() {
        BottomDialog.showBottomDialog(this, R.string.shot_video, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                //拍照
                VideoUtils.startRecordVideo(SendVideoActivity.this, 103);
            }
        }, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                //从相册选择
                VideoUtils.chooseVideo(SendVideoActivity.this, 103);
            }
        });
    }

    private void showDialog() {
        MyDialogUtils.showV7NormalDialog(SendVideoActivity.this, R.string.quit_editing, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    private Handler myHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                updateUploadProgress(msg.obj + "");
            } else if (msg.what == 2) {
                Bundle bundle = msg.getData();
                String videoId = bundle.getString("videoId");
                String imageUrl = bundle.getString("imageUrl");
                publishDiscover(videoId, imageUrl);
            }
            return false;
        }
    });

    /**
     * 权限检测
     */
    private void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    if (TDevice.isCameraAvailable()) {
                        VideoUtils.startRecordVideo(SendVideoActivity.this, 103);
                    } else showMessage(getResources().getString(R.string.camera_is_not_available));
                } else showApplicationSettingDetails(getResources().getString(R.string.album));
            }
        });
    }

}
