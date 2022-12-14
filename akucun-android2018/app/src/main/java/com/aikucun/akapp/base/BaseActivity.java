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
     * ??????????????????????????? super.onCreate(savedInstanceState) ?????????????????????
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // ???????????? Application ??? onCreate ??????????????? BGASwipeBackHelper.init ???????????????????????????
        // ?????????????????????????????????????????????????????????????????????

        // ????????????????????????????????????????????? true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // ?????????????????????????????????????????????????????????????????? true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // ?????????????????????????????????????????????????????? true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // ?????????????????? id??????????????? R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // ???????????????????????????????????????????????????????????? true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // ?????????????????????????????????????????????????????????????????????????????? true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // ??????????????????????????????????????????????????????????????? 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // ???????????????????????????????????????????????????????????? false
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
            //????????????
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mInflater = getLayoutInflater();
        //        mActionBar = getSupportActionBar();
        //        if (hasActionBar()) {
        //            initActionBar(mActionBar);
        //        }

        // ????????????????????????
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
            showMessage("???????????????????????????????????????");
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // ???????????????????????????????????????????????????
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
     * ????????????????????????????????????
     */
    public void exitApp() {
        long curTime = SystemClock.uptimeMillis();
        if ((curTime - mBackPressedTime) < (3 * 1000)) {
            //
            AppManager.getAppManager().AppExit();
            finish();

        } else {
            mBackPressedTime = curTime;

            MToaster.showShort(this, "????????????????????????", MToaster.IMG_INFO);
//            final Snackbar snackbar = Snackbar.make(getRootView(), "????????????????????????", Snackbar
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
     * Api ??????????????????
     */
    public void handleApiFailed(String message, int code) {
        cancelProgress();
        if (code == 40005) {
            MyDialogUtils.showV7NormalDialog(this, "??????", message, new MyDialogUtils.IDialogListenter() {
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
        if (code == 40011) {// ???????????????
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
            //???????????????????????????????????????????????????
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("??????????????????").setMessage(message).setNegativeButton(R.string.cancel,null).setPositiveButton("?????????", new
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
     * ????????????
     */
    public void logout() {
        // ??????, ?????? User ID & Token
        AppContext.set(AppConfig.PREF_KEY_USER_ID, null);
        AppContext.set(AppConfig.PREF_KEY_USER_TOKEN, null);
        // ??????
        AppContext.set(AppConfig.PREF_KEY_SKU_UPDATE, null);
        AppContext.set(AppConfig.PREF_KEY_COMMENT_UPDATE, null);

        AppContext.set(AppConfig.LOGIN_DATE_TIME, null);

        AppContext.getInstance().setToken("");
        AppContext.getInstance().setUserId("");
        AddressUtils.setSelectedAddress(null);
        AddressUtils.setDefaultAddress(null);
        AddressUtils.setAddresses(null);
        // ??????????????????
        Intent intent = new Intent(this, WXEntryActivity.class);
        startActivity(intent);
    }

    /**
     * ?????????????????????
     *
     * @return View
     */
    public View getRootView() {
        return null == getWindow() ? null : getWindow().getDecorView();
    }

    /**
     * Snackbar ??????????????????
     *
     * @param message ????????????
     */
    public void showMessage(String message) {
//        final Snackbar snackbar = Snackbar.make(getRootView(), message, Snackbar.LENGTH_LONG);
//        snackbar.setAction("??????", new View.OnClickListener()
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
     * Snackbar ?????????????????????????????????????????????
     *
     * @param message  ????????????
     * @param text     ??????????????????
     * @param listener ??????????????????
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
     * ???????????????
     */
    private SKProgressDialog mProgressDialog;

    /**
     * ???????????????
     *
     * @param title ?????????????????????
     */
    public void showProgress(String title) {
        mProgressDialog = SKProgressDialog.createDialog(this).setTitleMessage(title);
        mProgressDialog.show();
    }

    /**
     * ???????????????????????????
     *
     * @param text ?????????????????????
     */
    public void updateProgress(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.setTitleMessage(text);
        }
    }

    /**
     * ????????????
     *
     * @param progress
     */
    public void updateUploadProgress(String progress) {
        if (mProgressDialog != null) mProgressDialog.setProgress(progress);
    }

    /**
     * ?????????????????????
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
                                    //???????????????
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
     * ?????????????????????
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
     * ????????????????????????????????????????????????????????? true ?????????????????????????????????????????????????????????????????????????????????????????? false ??????
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * ??????????????????
     *
     * @param slideOffset ??? 0 ??? 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * ??????????????????????????????????????? Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

}
