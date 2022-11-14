package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.album.ClipImageActivity;
import com.aikucun.akapp.activity.album.PhotoActivity;
import com.aikucun.akapp.activity.discover.CameraActivity;
import com.aikucun.akapp.activity.discover.ImageSize;
import com.aikucun.akapp.activity.realauth.InputAuthRealNameActivity;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.UserInfoCallback;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.ImagesUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.RoundImageView;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.qiyunxin.android.http.cache.HttpStreamCache;
import com.qyx.android.weight.choosemorepic.PhotoItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by ak123 on 2018/1/7.
 * 账户设置
 */

public class AccountSettingActivity extends BaseActivity {
    @BindView(R.id.switchBtn)
    SwitchCompat switchCompat;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.head_image)
    RoundImageView head_image;
    @BindView(R.id.user_nick_name)
    TextView user_nick_name;
    @BindView(R.id.user_phone_tv)
    TextView user_phone_tv;
    //代购编号
    @BindView(R.id.purchasing_num_tv)
    TextView purchasing_num_tv;
    @BindView(R.id.vip_level_tv)
    TextView vip_level_tv;
    @BindView(R.id.realname_auth_status_tv)
    TextView realname_auth_status_tv;
    private static final int PAIS = 101;
    private static final int PIC = 102;
    private final int CHOOSE_CROP_FROM_CAMERA_REQUEST_CODE = 103;
    private ArrayList<ImageSize> imgSizes = new ArrayList<>();
    private ArrayList<PhotoItem> select_gl_arr = new ArrayList<>();

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.account_setting);
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

        getUserInfo();
    }

    @Override
    @OnClick({R.id.avatar_layout, R.id.setting_layout, R.id.nickname_layout, R.id.address_manager_layout, R.id.realname_auth_layout, R.id.vip_layout, R.id.forward_set_layout, R.id.exit_app_btn})
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.avatar_layout:
                checkPermission();
                break;
            case R.id.setting_layout:
                ActivityUtils.startActivity(this, SettingsActivity.class);
                break;
            case R.id.nickname_layout:
                ActivityUtils.startActivity(this, UserEditActivity.class, 100);
                break;
            case R.id.address_manager_layout:
                if (AppContext.getInstance().getToken().length() != 0) {
                    ActivityUtils.startActivity(this, AddressListActivity.class);
                } else {
                    MToaster.showShort(this, R.string.cannot_manager_address, MToaster.IMG_INFO);
                }
                break;
            case R.id.realname_auth_layout:
                UserInfo userInfo = AppContext.getInstance().getUserInfo();
                if (userInfo.getIdentityflag() == 0) {
                    ActivityUtils.startActivity(this, InputAuthRealNameActivity.class);
                }
                break;
            case R.id.vip_layout:
//                userInfo = AppContext.getInstance().getUserInfo();

                    ActivityUtils.startActivity(this, MemberActivity.class,104);
                break;
            case R.id.forward_set_layout:
                ActivityUtils.startActivity(this, ProductForwardSettingActivity.class);
                break;
            case R.id.exit_app_btn:
                exitDialog();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_setting_layout;
    }

    private void getUserInfo() {
        showProgress("");
        UsersApiManager.userGetInfo(this, new UserInfoCallback() {
            @Override
            public void onApiSuccess(UserInfo userInfo, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(userInfo, call, jsonResponse);
                AppContext.getInstance().setUserInfo(userInfo);
                cancelProgress();
                initUserInfo();
            }

            @Override
            public void onCacheSuccess(UserInfo userInfo, Call call) {
                super.onCacheSuccess(userInfo, call);
                AppContext.getInstance().setUserInfo(userInfo);
                cancelProgress();
                initUserInfo();
            }

            @Override
            public void onApiFailed(String message, int code) {
                cancelProgress();
                if (TDevice.hasInternet()) {
                    MToaster.showShort(AccountSettingActivity.this, message, MToaster.IMG_INFO);
                }
                initUserInfo();
            }
        });
    }

    private void initUserInfo() {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();

        head_image.SetUrl(userInfo.getAvator(), true);
//        Glide.with(this).load(userInfo.getAvator()).error(R.drawable.icon_default_avatar).diskCacheStrategy(DiskCacheStrategy
//                .NONE).into(head_image);
        user_nick_name.setText(userInfo.getName());
        if (!TextUtils.isEmpty(userInfo.getShoujihao())) {
            if (userInfo.getShoujihao().length() == 11) {
                String tempPhoneNum = userInfo.getShoujihao().substring(0, 3) + "****" + userInfo.getShoujihao().substring(userInfo.getShoujihao().length() - 4, userInfo.getShoujihao().length());
                user_phone_tv.setText(tempPhoneNum);
            } else user_phone_tv.setText(userInfo.getShoujihao());
        }
        if (userInfo.getIdentityflag() == 1) {
            realname_auth_status_tv.setText(R.string.authed);
        } else realname_auth_status_tv.setText(R.string.unauth);
        purchasing_num_tv.setText(userInfo.getYonghubianhao());
        vip_level_tv.setText("VIP" + userInfo.getViplevel());
    }

    private void exitDialog() {
        MyDialogUtils.showV7NormalDialog(this, R.string.eixt_app_info, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                PushManager.getInstance().unBindAlias(getApplicationContext(), AppContext.getInstance()
                        .getUserId(), true);
                UsersApiManager.userLogout(AccountSettingActivity.this, AppContext.getInstance().getUserId(), AppContext
                        .getInstance().getToken(), null);
                logout();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            initUserInfo();
        }else if (requestCode == 104 && resultCode == RESULT_OK){
            getUserInfo();
        }else if (requestCode == PAIS) {
            if (data == null) {
                return;
            }
            try {
                String strPicPath = data.getStringExtra("big_pic_filename");
                Bundle bundle = new Bundle();
                bundle.putString("picPath", strPicPath);
                ActivityUtils.startActivity(AccountSettingActivity.this, ClipImageActivity.class, bundle, CHOOSE_CROP_FROM_CAMERA_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PIC) {
            try {
                if (data == null) {
                    return;
                }
                select_gl_arr.clear();
                select_gl_arr = (ArrayList<PhotoItem>) data
                        .getSerializableExtra("gl_arr");

                if (select_gl_arr.size() > 0) {
                    for (int i = 0; i < select_gl_arr.size(); i++) {
                        String path = select_gl_arr.get(i).getPath();
                        Bundle bundle = new Bundle();
                        bundle.putString("picPath", path);
                        ActivityUtils.startActivity(AccountSettingActivity.this, ClipImageActivity.class, bundle, CHOOSE_CROP_FROM_CAMERA_REQUEST_CODE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHOOSE_CROP_FROM_CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            String pic_path = data.getStringExtra("pic_path");
            if (!TextUtils.isEmpty(pic_path)) {
                updateUserAvatar(pic_path);
            }
        }
    }


    protected void showPicDialog() {

        BottomDialog.showBottomDialog(AccountSettingActivity.this, R.string.take_phone, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                takePhone();
            }
        }, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                chooseImgs();
            }
        });

    }

    /**
     * 权限检测
     */
    private void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    if (TDevice.isCameraAvailable()) {
                        showPicDialog();
                    } else showMessage(getResources().getString(R.string.camera_is_not_available));
                } else
                    showApplicationSettingDetails(getResources().getString(R.string.video_permission));
            }
        });
    }

    /**
     * 拍照
     */
    private void takePhone() {
        imgSizes.clear();
        imgSizes.add(new ImageSize(960, 960, ""));
        Bundle bundle = new Bundle();
        bundle.putSerializable("size", imgSizes);
        ActivityUtils.startActivity(AccountSettingActivity.this,
                CameraActivity.class, bundle, PAIS);
    }

    /**
     * 选择图片
     */
    private void chooseImgs() {
        Bundle bundle = new Bundle();
        bundle.putInt("selected_count", 0);
        bundle.putInt("max_select_count", 1);
        ActivityUtils.startActivity(AccountSettingActivity.this,
                PhotoActivity.class, bundle, PIC);
    }

    private void updateUserAvatar(String path) {
        showProgress(getString(R.string.loading));
        String base64image = ImagesUtils.fileToBase64(path);
        UsersApiManager.userUpdateInfo(this, "", base64image, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                if (jsonObject != null) {
                    String avatar = jsonObject.getString("avatar");
                    if (!TextUtils.isEmpty(avatar)) {
                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        userInfo.setAvator(avatar);
                        HttpStreamCache.getInstance().ClearCacheBitmap(userInfo.getAvator());
                        head_image.SetUrl(avatar);
                    }
                }

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(AccountSettingActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equals(AppConfig.MESSAGE_EVENT_REFRESH_USER_INFO)) {
            getUserInfo();
        }
    }
}
