package com.aikucun.akapp.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonCallback;
import com.aikucun.akapp.api.manager.SystemApiManager;
import com.aikucun.akapp.interf.BaseViewInterface;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.SystemBarUtil;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.sucang.widget.spinkit.SKProgressDialog;
import okhttp3.Call;

/**
 * Base Activity
 * Created by jarry on 16/3/11.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseViewInterface, View
        .OnClickListener, BGASwipeBackHelper.Delegate {
    protected LayoutInflater mInflater;

    protected long mBackPressedTime;

    protected TextView mTitleText;
    protected BGASwipeBackHelper mSwipeBackHelper;

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        //        if (AppContext.getNightModeSwitch()) {
        //            setTheme(R.style.AppBaseTheme_Night);
        //        } else {
        //            setTheme(R.style.AppBaseTheme_Light);
        //        }

        AppManager.getAppManager().addActivity(this);

        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明背景
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mInflater = getLayoutInflater();
        //        mActionBar = getSupportActionBar();
        //        if (hasActionBar()) {
        //            initActionBar(mActionBar);
        //        }

        // 通过注解绑定控件
        ButterKnife.bind(this);

        init(savedInstanceState);
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    public void setTitleText(String title) {
        if (mTitleText != null) {
            mTitleText.setText(title);
            mTitleText.requestFocus();
        }
    }

    protected void onBeforeSetContentLayout() {
//        setTranslucentStatus(true);
        setSystemBarTintColor(R.color.color_primarydark);
    }

    abstract protected int getLayoutId();

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected void init(Bundle savedInstanceState) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TDevice.hasInternet()) {
            showMessage("网络连接不可用！请检查设置");
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    protected void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    protected void setSystemBarTintColor(int tintColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil mTintManager = new SystemBarUtil(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setTintColor(tintColor);
        }
    }

    /**
     * 退出应用，返回键二次确认
     */
    public void exitApp() {
        long curTime = SystemClock.uptimeMillis();
        if ((curTime - mBackPressedTime) < (3 * 1000)) {
            //
            AppManager.getAppManager().AppExit();
            finish();

        } else {
            mBackPressedTime = curTime;

            MToaster.showShort(this, "再次点击退出应用", MToaster.IMG_INFO);
//            final Snackbar snackbar = Snackbar.make(getRootView(), "再次点击退出应用", Snackbar
//                    .LENGTH_LONG);
//            snackbar.setAction(R.string.cancel, new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    mBackPressedTime = 0;
//                    snackbar.dismiss();
//                }
//            });
//            snackbar.show();
        }
    }

    /**
     * Api 接口失败处理
     */
    public void handleApiFailed(String message, int code) {
        cancelProgress();
        if (code == 40005) {
            MyDialogUtils.showV7NormalDialog(this, "提示", message, new MyDialogUtils.IDialogListenter() {
                @Override
                public void onClick() {
                    logout();
                }
            });
            return;
        }

        if (code == 40006) {
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_GOODS));
            return;
        }
        if (code == 40011) {// 低版本升级
            MyDialogUtils.showV7NormalDialog(this, R.string.update_apk_title, message, new MyDialogUtils.IDialogListenter() {
                @Override
                public void onClick() {
                    checkVersionUpdate(false);
//                    PgyUpdateManager.register(BaseActivity.this, AppConfig.PROVIDER_FILE_PATH);
                }
            });
//            checkVersionUpdate();
//            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_UPGRADE));
            return;
        }
        if (40043 == code) {
            /*
            //非会员不支持采购，请先购买会员资料
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("您还不是会员").setMessage(message).setNegativeButton(R.string.cancel,null).setPositiveButton("去开通", new
                    DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            Intent intent = new Intent(BaseActivity.this, MemberActivity.class);
                            startActivity(intent);

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();*/
            MyDialogUtils.showNotVipDialog(this);
            return;
        }
        showMessage(message);
    }

    /**
     * 注销登录
     */
    public void logout() {
        // 注销, 清空 User ID & Token
        AppContext.set(AppConfig.PREF_KEY_USER_ID, null);
        AppContext.set(AppConfig.PREF_KEY_USER_TOKEN, null);
        // 清空
        AppContext.set(AppConfig.PREF_KEY_SKU_UPDATE, null);
        AppContext.set(AppConfig.PREF_KEY_COMMENT_UPDATE, null);

        AppContext.set(AppConfig.LOGIN_DATE_TIME, null);

        AppContext.getInstance().setToken("");
        AppContext.getInstance().setUserId("");
        AddressUtils.setSelectedAddress(null);
        AddressUtils.setDefaultAddress(null);
        AddressUtils.setAddresses(null);
        // 跳转登录界面
        Intent intent = new Intent(this, WXEntryActivity.class);
        startActivity(intent);
    }

    /**
     * 获取当前根视图
     *
     * @return View
     */
    public View getRootView() {
        return null == getWindow() ? null : getWindow().getDecorView();
    }

    /**
     * Snackbar 显示提示信息
     *
     * @param message 提示内容
     */
    public void showMessage(String message) {
//        final Snackbar snackbar = Snackbar.make(getRootView(), message, Snackbar.LENGTH_LONG);
//        snackbar.setAction("关闭", new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                snackbar.dismiss();
//            }
//        });
//        snackbar.show();
        MToaster.showShort(this, message, MToaster.IMG_INFO);
    }

    public void showMessage(int messageResId) {
        MToaster.showShort(this, messageResId, MToaster.IMG_INFO);
    }

    /**
     * Snackbar 显示提示信息，可自定义操作按钮
     *
     * @param message  提示内容
     * @param text     操作按钮文字
     * @param listener 操作按钮事件
     */
    public void showMessage(String message, CharSequence text, final View.OnClickListener listener) {
        View rootView = getRootView();
        if (null != rootView) {
            final Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
            snackbar.setAction(text, listener);
            snackbar.show();
        }
    }

    /**
     * 进度等待框
     */
    private SKProgressDialog mProgressDialog;

    /**
     * 显示等待框
     *
     * @param title 等待框提示文字
     */
    public void showProgress(String title) {
        mProgressDialog = SKProgressDialog.createDialog(this).setTitleMessage(title);
        mProgressDialog.show();
    }

    /**
     * 更新等待框提示内容
     *
     * @param text 等待框提示文字
     */
    public void updateProgress(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.setTitleMessage(text);
        }
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void updateUploadProgress(String progress) {
        if (mProgressDialog != null) mProgressDialog.setProgress(progress);
    }

    /**
     * 关闭等待框显示
     */
    public void cancelProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showApplicationSettingDetails(String serviceName) {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.runing_permissions) + (StringUtils.isEmpty(serviceName) ? "" : ("\n" + serviceName)))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    }
                }).show();

    }

    public void checkVersionUpdate(final boolean isShowDialog) {
        SystemApiManager.checkUpdate(this, new JsonCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                cancelProgress();
                if (jsonObject != null) {
                    JSONObject dataJson = jsonObject.getJSONObject("data");
                    if (dataJson != null) {
                        boolean isUpdate = dataJson.getBooleanValue("isUpdate");
                        if (isUpdate) {
                            JSONObject filesObj = dataJson.getJSONObject("files");
                            if (filesObj != null) {
                                String downloadUrl = filesObj.getString("downloadUrl");
                                String hash = filesObj.getString("hash");
                                String md5 = filesObj.getString("md5");
                                String describe = filesObj.getString("describe");
                                if (!TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(hash) && !TextUtils.isEmpty(md5)) {
                                    //显示下载框
                                    showUpdateDialog(describe, md5, hash, downloadUrl);
                                }
                            }
                        } else {
                            if (isShowDialog)
                                MyDialogUtils.showV7NormalDialog(BaseActivity.this, R.string.new_version, null);
                        }
                    }

                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
            }
        });

    }

    /**
     * 显示更新提示框
     *
     * @param content
     * @param md5
     * @param hash
     * @param downloadUrl
     */
    private void showUpdateDialog(String content, final String md5, final String hash, final String downloadUrl) {
        MyDialogUtils.showV7NormalDialog(this, R.string.update_apk_title, content, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                MyDialogUtils.showDownloadDialog(BaseActivity.this, md5, hash, downloadUrl);
            }
        });
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

}
