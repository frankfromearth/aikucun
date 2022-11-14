package com.aikucun.akapp.activity.video;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;

import java.text.SimpleDateFormat;

/**
 * Created by ak123 on 2017/11/17.
 * 视频播放页面
 */

public class VideoPlayActivity extends Activity {

    private AliyunVodPlayerView mAliyunVodPlayerView = null;

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");

    private ScreenStatusController mScreenStatusController = null;

    private boolean isStrangePhone() {
        boolean strangePhone = Build.DEVICE.equalsIgnoreCase("mx5")
                || Build.DEVICE.equalsIgnoreCase("Redmi Note2")
                || Build.DEVICE.equalsIgnoreCase("Z00A_1")
                || Build.DEVICE.equalsIgnoreCase("hwH60-L02")
                || Build.DEVICE.equalsIgnoreCase("hermes")
                || (Build.DEVICE.equalsIgnoreCase("V4") && Build.MANUFACTURER.equalsIgnoreCase("Meitu"));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (isStrangePhone()) {
////            setTheme(R.style.ActTheme);
//        } else {
//            setTheme(R.style.NoActionTheme);
//        }
        //取消标题
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_skin_layout);
        mAliyunVodPlayerView = (AliyunVodPlayerView) findViewById(R.id.video_view);
        mAliyunVodPlayerView.setKeepScreenOn(true);//保持屏幕敞亮
        mAliyunVodPlayerView.setCirclePlay(true);//循环播放
//        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
//        mAliyunVodPlayerView.setPlayingCache(true, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
//        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Orange);
        mAliyunVodPlayerView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                mAliyunVodPlayerView.start();
            }
        });

        mAliyunVodPlayerView.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放正常完成时触发
            }
        });

        mAliyunVodPlayerView.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                //首帧显示时触发
            }
        });

        mAliyunVodPlayerView.setOnChangeQualityListener(new IAliyunVodPlayer.OnChangeQualityListener() {
            @Override
            public void onChangeQualitySuccess(String finalQuality) {
                //清晰度切换成功时触发
            }

            @Override
            public void onChangeQualityFail(int code, String msg) {
                //清晰度切换失败时触发
            }
        });

        mAliyunVodPlayerView.setOnStoppedListner(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                //使用stop接口时触发
            }
        });
        mAliyunVodPlayerView.enableNativeLog();
        setPlaySource();
        mScreenStatusController = new ScreenStatusController(this);
        mScreenStatusController.setScreenStatusListener(new ScreenStatusController.ScreenStatusListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {

            }
        });

        mScreenStatusController.startListen();
    }

    private void setPlaySource() {
        String vid = getIntent().getStringExtra("vid");
        String akId = getIntent().getStringExtra("akId");
        String akSecret = getIntent().getStringExtra("akSecret");
        String scuToken = getIntent().getStringExtra("scuToken");
        AliyunVidSts vidSts = new AliyunVidSts();
        vidSts.setVid(vid);
        vidSts.setAcId(akId);
        vidSts.setAkSceret(akSecret);
        vidSts.setSecurityToken(scuToken);
        mAliyunVodPlayerView.setVidSts(vidSts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("lfj1019", " orientation = " + getResources().getConfiguration().orientation);
        updatePlayerViewMode();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {                //转为竖屏了。
                //显示状态栏
//                if (!isStrangePhone()) {
//                    getSupportActionBar().show();
//                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                if (!isStrangePhone()) {
//                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
//                }

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                if (!isStrangePhone()) {
//                    aliVcVideoViewLayoutParams.topMargin = 0;
//                }

            }

        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        mScreenStatusController.stopListen();

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        VcPlayerLog.d("lfj1030", "onWindowFocusChanged = " + hasFocus);
        updatePlayerViewMode();
    }
}
