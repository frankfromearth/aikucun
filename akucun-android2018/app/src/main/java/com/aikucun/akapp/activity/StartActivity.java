package com.aikucun.akapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.api.callback.AddressListCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.UserInfoCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.SystemApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.jnij.JNIAKuCun;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.FileUtils;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.jxccp.ui.JXConstants;
import com.lzy.okgo.OkGo;

import java.util.List;

import cn.sucang.widget.spinkit.SpinKitView;
import okhttp3.Call;

/**
 * 启动界面
 * Created by jarry on 16/10/12.
 */

public class StartActivity extends Activity {
    private static final int SPLASH_TIME_MIN = 3000; // 最短时间

    private long startTime, loadingTime;

    private SpinKitView spinKitView;
    private TextView progressText;
    private Button btn_bind;

    private int retryCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppInfo();

        // 防止第三方跳转时出现双实例
        Activity activity = AppManager.getActivity(MainActivity.class);
        if (activity != null && !activity.isFinishing()) {
            finish();
        }
        String packageName = getPackageName();
        //设置APP包名用于provider使用
        JXConstants.setProviderName(packageName);
        final View view = View.inflate(this, R.layout.activity_start, null);
        setContentView(view);

        // 渐变展示启动屏
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(300);
        view.startAnimation(alphaAnimation);

        //
        spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
        progressText = (TextView) findViewById(R.id.start_progress_text);
        btn_bind = (Button) findViewById(R.id.btn_bind);


        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, BindPhoneActivity.class));
            }
        });
        //
        String token = AppContext.get(AppConfig.PREF_KEY_USER_TOKEN, "");
        if (token.length() > 0) {
            progressText.setText(R.string.msg_text_autologin);
        }

        final View progressView = findViewById(R.id.start_progress);
        final AlphaAnimation spinAnimation = new AlphaAnimation(0.0f, 1.0f);
        spinAnimation.setDuration(500);
        spinAnimation.setStartOffset(500);
        spinAnimation.setFillAfter(true);

        spinAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doUploadDid();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        progressView.startAnimation(spinAnimation);
        //删除转发目录下的文件
        FileUtils.deleteForwardImgs();
        /*
        //
        int height = getResources().getDisplayMetrics().heightPixels;
        float top = getResources().getDimension(R.dimen.space_65);
        float toYDelta = height / 2 - top - 162 * 2;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -toYDelta);
        translateAnimation.setDuration(400);
        translateAnimation.setStartOffset(500);
        translateAnimation.setFillAfter(true);

        ImageView logoImage = (ImageView) findViewById(R.id.iv_logo);
        logoImage.startAnimation(translateAnimation);

        translateAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationStart(Animation animation)
            {
            }
        });
        */
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    private void doInitTask() {
        String token = AppContext.getInstance().getToken();
        if (token.length() > 0) {
            // 自动登录
            doAutoLogin();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoLoginFailed();
                }
            }, 1500);
        }
    }

    /*
    * 检测是否是官方签名的应用
     */
    private boolean doCheckIsOfficial() {

        //        生成加密图片
        //        JNISafeData.createSafeBitmap(this);

        String[] values = JNIAKuCun.getDataFromImage(this);
        if (values == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
            builder.setTitle("检测到该应用非官方应用，请从正规途径下载！").setMessage(null).setPositiveButton(R.string.sure,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return false;
        } else {
            HttpConfig.getInstance().setKeyAndSecret(values);
        }
        return true;

    }

    /*
    * 上传DID
    * */
    private void doUploadDid() {
        if (!doCheckIsOfficial()) {
            return;
        }

        String market = AppContext.getInstance().getChannel();
        SCLog.debug("market = " + market);
        SystemApiManager.requestReportInfo(this, HttpConfig.getDid(), market, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                doInitTask();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if (--retryCount > 0) {
                    doUploadDid();
                } else {
                    autoLoginFailed();
                }
            }
        });
    }


    /**
     * 自动登录，请求获取用户基本信息
     */
    private void doAutoLogin() {
        startTime = System.currentTimeMillis();
        UsersApiManager.userGetInfo(this, new UserInfoCallback() {
            @Override
            public void onApiSuccess(UserInfo userInfo, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(userInfo, call, jsonResponse);
                requestUserAddress();
            }

            @Override
            public void onCacheSuccess(UserInfo userInfo, Call call) {
                super.onCacheSuccess(userInfo, call);
                AppContext.getInstance().setUserInfo(userInfo);
                requestUserAddress();
                autoLoginSuccess();
            }

            @Override
            public void onApiFailed(String message, int code) {
                if (TDevice.hasInternet()) {
                    showError(message);
                }
            }
        });
    }

//    private void syncGoods() {
//        // 开始同步数据
//        progressText.setText(R.string.msg_text_initdata);
//        ProductManager.getInstance().removeOverLives();
//        syncProducts(ProductManager.getInstance().productUpdate);
//    }

    /**
     * 自动登录成功，跳转首页
     */
    private void autoLoginSuccess() {
        //
        loadingTime = System.currentTimeMillis() - startTime;
        if (loadingTime < SPLASH_TIME_MIN) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(StartActivity.this, GuideOneActivity.class);
                    pullActivity(intent);
                }
            }, (SPLASH_TIME_MIN - loadingTime));
        } else {
            Intent intent = new Intent(StartActivity.this, GuideOneActivity.class);
            pullActivity(intent);
        }
    }

    /**
     * 自动登录失败，包括首次登录，跳转登录界面
     */
    private void autoLoginFailed() {
        // 登录错误
        Intent intent = new Intent(StartActivity.this, GuideOneActivity.class);
        intent.putExtra("isError",true);
        pullActivity(intent);
    }

    private void pullActivity(Intent intent) {
        spinKitView.stop();

        //
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        //
        finish();
    }

    /**
     * 同步商品数据
     *
     * @param sync 上一次同步时间
     */
//    private void syncProducts(long sync) {
//        LiveApiManager.syncProducts(this, sync, new SyncProductsCallback() {
//            @Override
//            public void onApiSuccess(SynProductsResp synProductsResp, Call call, ApiResponse
//                    jsonResponse) {
//                super.onApiSuccess(synProductsResp, call, jsonResponse);
//
//                // 未同步完继续同步
////                if (synProductsResp.hasmore) {
////                    syncProducts(synProductsResp.lastupdate);
////                } else {
////                    syncComments(ProductManager.getInstance().commentUpdate);
////                }
//            }
//
//            @Override
//            public void onApiFailed(String message, int code) {
//                showError(message);
//            }
//        });
//    }

    /**
     * 同步商品评论
     *
     * @param sync 上一次同步时间
     */
//    private void syncComments(long sync) {
//        CommentsApiManager.syncComments(this, sync, new SyncCommentsCallback() {
//            @Override
//            public void onApiSuccess(SynCommentsResp synCommentsResp, Call call, ApiResponse
//                    jsonResponse) {
//                super.onApiSuccess(synCommentsResp, call, jsonResponse);
//
//                SCLog.debug("--> synCommentsResp Success ");
//
//                // 未同步完继续同步
//                if (synCommentsResp.hasmore) {
//                    syncComments(synCommentsResp.lastupdate);
//                } else {
//                    autoLoginSuccess();
//                }
//            }
//        });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (11 == requestCode) {
//            syncGoods();
//
//            /*
//            if (10 == resultCode) {
//                syncGoods();
//            }
//            if (110 == resultCode) {
//                spinKitView.stop();
//                spinKitView.setVisibility(View.INVISIBLE);
//                btn_bind.setVisibility(View.VISIBLE);
//                progressText.setText("手机号绑定成功才能进入");
//                MToaster.showShort(this,"手机号绑定成功才能进入",MToaster.IMG_INFO);
//            }
//            */
//        }
//    }

    /**
     * 显示错误提示框
     *
     * @param message 错误提示内容
     */
    private void showError(String message) {
        spinKitView.stop();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage(message).setPositiveButton(R.string.sure, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoLoginFailed();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void requestUserAddress() {

        UsersApiManager.userAddressList(this, new AddressListCallback() {
            @Override
            public void onApiSuccess(List<Address> addresses, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(addresses, call, jsonResponse);
                AddressUtils.setAddresses(addresses);
                autoLoginSuccess();
            }

            @Override
            public void onCacheSuccess(List<Address> addresses, Call call) {
                super.onCacheSuccess(addresses, call);
                AddressUtils.setAddresses(addresses);
            }
        });
    }

    private void getAppInfo() {
//        String channel = WalleChannelReader.getChannel(this.getApplicationContext());
//        int envType = BuildConfig.ENV_TYPE;
//        String packageName = getPackageName();
//        //设置APP包名用于provider使用
//        JXConstants.setProviderName(packageName);
//        switch (envType) {
//            case EnvType.DEVELOP:
//                Log.e("开发环境：", "-------------->");
//                break;
//            case EnvType.CHECK:
//                Log.e("测试环境：", "-------------->");
//                break;
//            case EnvType.PRODUCT:
//                Log.e("生产环境：", "-------------->");
//                break;
//        }
//        Log.e("包名：", packageName);
//        Log.e("渠道名称：",channel);
    }
}
