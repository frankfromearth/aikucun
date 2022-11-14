package com.aikucun.akapp.activity.realauth;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.activity.album.PhotoActivity;
import com.aikucun.akapp.activity.discover.CameraActivity;
import com.aikucun.akapp.activity.discover.ImageSize;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.AuthApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.ImagesUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.ContentWithSpaceEditText;
import com.alibaba.fastjson.JSONObject;
import com.aswife.ui.MaskImageView;
import com.qyx.android.weight.choosemorepic.PhotoItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by ak123 on 2017/12/20.
 * 实名认证输入真实姓名
 */

public class InputAuthRealNameActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.real_name_et)
    EditText real_name_et;
    @BindView(R.id.idnum_et)
    ContentWithSpaceEditText idnum_et;
    @BindView(R.id.idno_back_iv)
    MaskImageView idno_back_iv;
    @BindView(R.id.idno_positive_iv)
    MaskImageView idno_positive_iv;
    @BindView(R.id.idno_back_layout)
    FrameLayout idno_back_layout;
    @BindView(R.id.idno_positive_layout)
    FrameLayout idno_positive_layout;
    @BindView(R.id.back_iv_layout)
    FrameLayout back_iv_layout;
    @BindView(R.id.positive_iv_layout)
    FrameLayout positive_iv_layout;
    @BindView(R.id.icon_delete_positive_iv)
    ImageView icon_delete_positive_iv;
    @BindView(R.id.icon_delete_back_iv)
    ImageView icon_delete_back_iv;

    private ArrayList<ImageSize> imgSizes = new ArrayList<ImageSize>();
    //1：正面2：反面
    private int selectedType = 0;
    private String idnoPositivePath, idnoBackPath;
    public static InputAuthRealNameActivity mInputAuthRealNameActivity = null;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.realname_auth);
        mInputAuthRealNameActivity = this;
    }

    @Override
    public void initData() {
        initLisenter();
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = real_name_et.getText().toString();
                final String idnum = idnum_et.getText().toString().replaceAll(" ","");
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(idnum)) {
                    if ((TextUtils.isEmpty(idnoPositivePath) && !TextUtils.isEmpty(idnoBackPath)) || (!TextUtils.isEmpty(idnoPositivePath) && TextUtils.isEmpty(idnoBackPath))) {
                        MToaster.showShort(InputAuthRealNameActivity.this, getString(R.string.idno_error), MToaster.IMG_INFO);
                    } else {
                        if (!verForm(idnum)) {
                            Toast.makeText(InputAuthRealNameActivity.this, getString(R.string.idnum_error), Toast.LENGTH_LONG).show();
                        } else {
                            checkPress(idnum, name);
                        }
                    }

                }
            }
        });
    }

    private void checkPress(final String idnum, final String name) {
        compressImg(idnum, name);
    }

    private void checkPermissions(){
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
     * 压缩图片
     *
     * @param idnum
     * @param name
     */
    private void compressImg(final String idnum, final String name) {
        if (!TextUtils.isEmpty(idnoPositivePath) && !TextUtils.isEmpty(idnoBackPath)) {
            ImagesUtils.compressImg(InputAuthRealNameActivity.this, idnoPositivePath, new ImagesUtils.ICompressListener() {
                @Override
                public void onResult(String path) {
                    if (!TextUtils.isEmpty(path)) {
                        idnoPositivePath = path;
                        ImagesUtils.compressImg(InputAuthRealNameActivity.this, idnoBackPath, new ImagesUtils.ICompressListener() {
                            @Override
                            public void onResult(String path) {
                                if (!TextUtils.isEmpty(path)) {
                                    idnoBackPath = path;
                                    checkUserInfoisUsed(idnum, name, ImagesUtils.fileToBase64(idnoPositivePath), ImagesUtils.fileToBase64(idnoBackPath));
                                }
                            }
                        });
                    }

                }
            });
        } else checkUserInfoisUsed(idnum, name, "", "");
    }

    private void initLisenter() {
        idno_positive_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 1;
                checkPermissions();
            }
        });
        idno_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 2;
                checkPermissions();
            }
        });
        icon_delete_positive_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idnoPositivePath = "";
                idno_positive_iv.SetUrl("");
                positive_iv_layout.setVisibility(View.GONE);
            }
        });
        icon_delete_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idnoBackPath = "";
                idno_back_iv.SetUrl("");
                back_iv_layout.setVisibility(View.GONE);
            }
        });
        idno_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                        (idno_back_iv.getMeasuredWidth(), idno_back_iv.getMeasuredHeight());
                List<String> photoUrls = new ArrayList<String>();
                photoUrls.add(idnoBackPath);
                ImagePagerActivity.startImagePagerActivity((InputAuthRealNameActivity.this), photoUrls,
                        0, imageSize);
            }
        });
        idno_positive_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                        (idno_positive_iv.getMeasuredWidth(), idno_positive_iv.getMeasuredHeight());
                List<String> photoUrls = new ArrayList<String>();
                photoUrls.add(idnoPositivePath);
                ImagePagerActivity.startImagePagerActivity((InputAuthRealNameActivity.this), photoUrls,
                        0, imageSize);
            }
        });
    }

    protected void showPicDialog() {
        BottomDialog.showBottomDialog(InputAuthRealNameActivity.this, R.string.take_phone, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
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
     * 拍照
     */
    private void takePhone() {
        imgSizes.clear();
        imgSizes.add(new ImageSize(960, 960, ""));
        Bundle bundle = new Bundle();
        bundle.putSerializable("size", imgSizes);
        ActivityUtils.startActivity(InputAuthRealNameActivity.this,
                CameraActivity.class, bundle, 101);
    }

    /**
     * 选择图片
     */
    private void chooseImgs() {
        Bundle bundle = new Bundle();
        bundle.putInt("selected_count", 0);
        bundle.putInt("max_select_count", 1);
        ActivityUtils.startActivity(InputAuthRealNameActivity.this,
                PhotoActivity.class, bundle, 100);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (data == null) {
                return;
            }

            switch (requestCode) {
                case 101:
                    try {
                        String strPicPath = data.getStringExtra("big_pic_filename");
                        if (selectedType == 1) {
                            idnoPositivePath = strPicPath;
                            idno_positive_iv.SetUrl(strPicPath);
                            positive_iv_layout.setVisibility(View.VISIBLE);
                        } else {
                            idnoBackPath = strPicPath;
                            idno_back_iv.SetUrl(strPicPath);
                            back_iv_layout.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 100:
                    try {
                        ArrayList<PhotoItem> select_gl_arr = (ArrayList<PhotoItem>) data
                                .getSerializableExtra("gl_arr");
                        if (select_gl_arr.size() > 0) {
                            for (int i = 0; i < select_gl_arr.size(); i++) {
                                String path = select_gl_arr.get(i).getPath();
                                if (selectedType == 1) {
                                    positive_iv_layout.setVisibility(View.VISIBLE);
                                    idnoPositivePath = path;
                                    idno_positive_iv.SetUrl(path);
                                } else {
                                    back_iv_layout.setVisibility(View.VISIBLE);
                                    idnoBackPath = path;
                                    idno_back_iv.SetUrl(path);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_auth_realname_layout;
    }


    /**
     * 检查身份证是否已被认证
     *
     * @param idcard
     * @param realName
     */
    private void checkUserInfoisUsed(final String idcard, final String realName, final String positivePath, final String backPath) {
        showProgress(getString(R.string.loading));
        AuthApiManager.checkUserInfoIsUsed(this, idcard, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.getString("userid"))){
                    Bundle bundle = new Bundle();
                    bundle.putString("real_name", realName);
                    bundle.putString("id_num", idcard);
                    AppContext.set("idno_positive_base64",positivePath);
                    AppContext.set("idno_back_base64",backPath);
                    ActivityUtils.startActivity(InputAuthRealNameActivity.this, InputAuthBankNumActivity.class, bundle);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message)) {
                    MToaster.showShort(InputAuthRealNameActivity.this, message, MToaster.IMG_INFO);
                }
            }
        });
    }


    private boolean verForm(String num) {
        String reg = "^\\d{15}$|^\\d{17}[0-9Xx]$";
        if (!num.matches(reg)) {
            return false;
        }
        return true;
    }
}
