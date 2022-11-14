package com.aikucun.akapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.bumptech.glide.Glide;
import com.igexin.sdk.PushManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jarry on 2017/6/12.
 */
public class SettingsActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.switchBtn)
    SwitchCompat switchCompat;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.setting);
    }

    @Override
    public void initData() {
        switchCompat.setChecked(AppContext.get(AppConfig.UDK_REMARK_DATA, true));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppContext.set(AppConfig.UDK_REMARK_DATA, isChecked);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    @OnClick({R.id.setting_option_sync, R.id.setting_option_cache, R
            .id.setting_option_update, R.id.setting_option_about, R
            .id.setting_option_term, R.id.forward_set_layout,R.id.exit_app_btn})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_option_sync: {
                showProgress(getString(R.string.sync_goods));
                ProductManager.clearAllData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgress();
                        MToaster.showShort(SettingsActivity.this, R.string.sync_goods_success, MToaster.IMG_INFO);
                    }
                },500);

//                LiveManager.getInstance().setOverPinpaiIds("");
//                syncProducts(0);
            }
            break;
            case R.id.setting_option_cache: {
                clearCacheDiskSelf();
            }
            break;

            case R.id.setting_option_update: {
                handleCheckUpdate();
            }
            break;
            case R.id.setting_option_term: {
//                startActivity(new Intent(this, TermActivity.class));
                String url = HttpConfig.APP_TERMS_URL + System.currentTimeMillis();
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_TITLE, "爱库存APP使用(服务)条款");
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_URL, url);
                startActivity(intent);
            }
            break;
            case R.id.setting_option_about: {
                startActivity(new Intent(this, AboutActivity.class));
            }
            break;
            case R.id.forward_set_layout:
                ActivityUtils.startActivity(this, ProductForwardSettingActivity.class);
                break;
            case R.id.exit_app_btn:
                exitDialog();
                break;
        }
    }



    private void exitDialog() {
        MyDialogUtils.showV7NormalDialog(this, R.string.eixt_app_info, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                PushManager.getInstance().unBindAlias(getApplicationContext(), AppContext.getInstance()
                        .getUserId(), true);
                UsersApiManager.userLogout(SettingsActivity.this, AppContext.getInstance().getUserId(), AppContext
                        .getInstance().getToken(), null);
                logout();
            }
        });
    }

    private void handleCheckUpdate() {
        showProgress("");
        checkVersionUpdate(true);
    }

//    protected void onSyncProductsHasNoMoreData() {
//        cancelProgress();
//        showMessage("同步商品数据完成");
//    }

    public boolean clearCacheDiskSelf() {
        showProgress("缓存清理中...");
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(AppContext.getInstance()).clearDiskCache();

                        cancelProgress();
                        showMessage("已清理完成");
                    }
                }).start();
            } else {
                Glide.get(AppContext.getInstance()).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
