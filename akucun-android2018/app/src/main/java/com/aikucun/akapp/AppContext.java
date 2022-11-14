package com.aikucun.akapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.base.BaseApplication;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.storage.YiFaAdOrderManager;
import com.aikucun.akapp.utils.MySqliteHelper;
import com.aikucun.akapp.utils.MyYFHSqliteHelper;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.common.logger.Logger;
import com.jxccp.im.chat.manager.JXImManager;
import com.pgyersdk.crash.PgyCrashManager;
import com.qiyunxin.android.http.QiYunXinHttpApplication;
import com.qyx.android.weight.QiYunxinWeightApplication;
import com.tencent.bugly.Bugly;
import com.umf.pay.sdk.UmfPay;

import java.io.File;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;


/**
 * 全局应用程序类：用于保存和调用全局应用配置
 * Created by jarry on 16/3/11.
 */
public class AppContext extends BaseApplication {

    private static AppContext instance;

    private String userId;

    private String token;
    private String subUserId;

    private UserInfo userInfo;

    private String channel;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        loadLibs();
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        Logger.setDebug(true);
//        configChannel();
        initUmfPayInfo();
        initJiaXinInfo();
        clearCache();

        //
        SCLog.init();

        //Bugly
        Bugly.init(getApplicationContext(), "37ff16505c", false);

        //
        PgyCrashManager.register(this);

        // HTTP 模块初始化
        HttpConfig.init(this);

        // 数据库和本地存储初始化
        MySqliteHelper mySqliteHelper = new MySqliteHelper(this);
        mySqliteHelper.getWritableDatabase();
        ProductManager.getInstance().init(mySqliteHelper);

        //发货单详情本地存储初始化
        MyYFHSqliteHelper myYFHSqliteHelper = new MyYFHSqliteHelper(this);
        myYFHSqliteHelper.getWritableDatabase();
        YiFaAdOrderManager.getInstance().init(myYFHSqliteHelper);
        //
        String userId = AppContext.get(AppConfig.PREF_KEY_USER_ID, "");
        String token = AppContext.get(AppConfig.PREF_KEY_USER_TOKEN, "");
        String subUserId = AppContext.get(AppConfig.PREF_KEY_SUBUSER_ID, "");
        setUserId(userId);
        setSubUserId(subUserId);
        setToken(token);


        // 解决 Android 7.0 FileUriExposedException 问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        QiYunXinHttpApplication.getInstance().initData(getPackageName(), this);
        QiYunxinWeightApplication.getInstance().initData(this, null, null);
        clearTips();
        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);
    }

    private AppContext configChannel() {
        //        String market = TextUtils.isEmpty(PackerNg.getChannel(this)) ? this.getResources()
        //                .getString(R.string.channel) : PackerNg.getChannel(this);
        //        SCLog.debug("market = "+ market);
        //        UsersApiManager.requestReportInfo(null, HttpConfig.getDid(),market,new
        // JsonDataCallback(){
        //            @Override
        //            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse
        // jsonResponse) {
        //                super.onApiSuccess(jsonObject, call, jsonResponse);
        //            }
        //
        //            @Override
        //            public void onApiFailed(String message, int code) {
        //                super.onApiFailed(message, code);
        //            }
        //        });
        return this;
    }

    private void clearCache() {
        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        File fileDir = new File(dir);
        if (fileDir.exists()) {
            fileDir.delete();
        }

    }

    private void clearGlideMemories() {

    }

    //清空提示，每次打开重新提醒
    private void clearTips() {
        set("didShowAddressTip", false);
        set("didShowOrderTip", false);
        set("didShowCartTip", false);
    }

    /**
     * 获得当前app运行的AppContext
     **/
    public static AppContext getInstance() {
        return instance;
    }

    /**
     * 保存用户登录成功的 ID 和 Token
     *
     * @param userId    用户ID
     * @param subuserId 子用户ID
     * @param token     用户Token
     */
    public static void saveUserToken(String userId, String subuserId, String token) {
        AppContext.getInstance().setUserId(userId);
        AppContext.getInstance().setToken(token);
        AppContext.getInstance().setSubUserId(subuserId);
        AppContext.set(AppConfig.PREF_KEY_USER_ID, userId);
        AppContext.set(AppConfig.PREF_KEY_SUBUSER_ID, subuserId);
        AppContext.set(AppConfig.PREF_KEY_USER_TOKEN, token);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubUserId() {
        return subUserId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSubUserId(String _subUserId) {
        this.subUserId = _subUserId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /*判断是否VIP用户*/
    public boolean isVip() {
        UserInfo userInfo = getUserInfo();
        // FIXME: 2018/1/4
        if (null != userInfo && userInfo.getViplevel() > 0) {

            return true;
        }
        return false;
    }

    /*获取打包渠道号*/
    public String getChannel() {
        if (this.channel == null) {
            //渠道号只获取第一次
            String localChannel = AppContext.get("local_channel", "");
            if (!TextUtils.isEmpty(localChannel)) {
                this.channel = localChannel;
            } else {
                this.channel = getChannelInfo();
                if (TextUtils.isEmpty(this.channel)) {
                    this.channel = "1000";
                }
                AppContext.set("local_channel", this.channel);
            }
        }
        return this.channel;
    }

    /*从meta-data中读取渠道号*/
    private String getChannelInfo() {
        String channel = null;
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            Object o = appInfo.metaData.get("aichannel");
            if (o instanceof String) {
                channel = (String) o;
            } else if (o instanceof Integer) {
                channel = String.format(("%04d"), (int) o);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SCLog.debug("channel = " + channel);
        return channel;
    }

    /*判断是否需要检测更新，目前只有蒲公英渠道检测更新*/
    public boolean shouldCheckUpgrade() {
        return this.channel == null ? true : (this.channel.equalsIgnoreCase("2002") || this.channel.equalsIgnoreCase("2004"));
    }

    private void loadLibs() {
        System.loadLibrary("openh264");
        System.loadLibrary("encoder");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initUmfPayInfo() {
        //初始化umfpay
        UmfPay.setDebug(true);
        UmfPay.init(this);
        UmfPay.setWeChatAppId(WXEntryActivity.WX_APP_ID);
    }

    private void initJiaXinInfo() {
        JXImManager.getInstance().init(getApplicationContext(), "oxa0z2flnzjudw#akc685#10002");
        JXImManager.getInstance().setDebugMode(true);
        JXImManager.Login.getInstance().setAutoLogin(false);
    }

    /**
     * 设置购物车消息数量
     * @param count
     */
    public void setCartMsgCount(int count) {
        AppContext.set("cart_new_msg_count_" + getSubUserId(), count);
    }

    /**
     * 获取购物车消息数量
     * @return
     */
    public int getCartMsgCount(){
        return AppContext.get("cart_new_msg_count_"+getSubUserId(),0);
    }

}
