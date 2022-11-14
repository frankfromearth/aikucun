package com.aikucun.akapp.activity.discover;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.album.PhotoActivity;
import com.aikucun.akapp.adapter.SendDynamicPicAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.DiscoverApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.qiyunxin.android.http.utils.Utils;
import com.qyx.android.weight.choosemorepic.PhotoItem;
import com.qyx.android.weight.view.MyGridView;
import com.qyx.android.weight.view.MyToast;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import rx.functions.Action1;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by ak123 on 2017/11/16.
 * 发送图文动态
 */

public class SendImgTextActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.pic_gd)
    MyGridView pic_gd;
    @BindView(R.id.send_text_et)
    EditText send_text_et;
    @BindView(R.id.title_et)
    EditText title_et;
    @BindView(R.id.textCount)
    TextView textCount;
    @BindView(R.id.choose_address_layout)
    LinearLayout choose_address_layout;
    private ArrayList<String> showPics = new ArrayList<String>();
    private ArrayList<PhotoItem> select_gl_arr = new ArrayList<PhotoItem>();
    private ArrayList<ImageSize> imgSizes = new ArrayList<ImageSize>();
    private ArrayList<String> compressedList = new ArrayList<String>();
    private SendDynamicPicAdapter dynamicPicAdapter;
    private static final int PAIS = 101;
    private static final int PIC = 102;
    //已选中图片数量
    private int mIntSelectPicCount;
    private String mStrContent, mTitle;

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
        setPicGd();
        initListener();
        checkPermission();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_imgtext;
    }

    private void initListener() {
        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrContent = send_text_et.getText().toString();
                mTitle = title_et.getText().toString();
                if (!TextUtils.isEmpty(mTitle) && showPics.size() > 1) {
                    Utils.hideSoftKeyboard(send_text_et);
//                    publishDiscover();
                    compressImgs();
                }

//                if (showPics.size() > 1 && !TextUtils.isEmpty(showPics.get(0))) {
//                    // loadingProgDialog.show();
//                    findViewById(R.id.btn_right).setEnabled(false);
//                    // TODO: 2017/11/16 上传图片
//                    // uploadPic(showPics);
//                } else {
//                    if (!TextUtils.isEmpty(mStrContent)) {
//                        // loadingProgDialog.show();
//                        findViewById(R.id.btn_right).setEnabled(false);
//                        // TODO: 2017/11/16 直接发送
//                        //sendDynamic();
//                    }
//                }

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
        choose_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityUtils.startActivity(SendImgTextActivity.this, ChooseAddressActivity.class,null,100);
            }
        });
    }

    private void setPicGd() {
        for (int i = 0, size = showPics.size(); i < size; i++) {
            if (showPics.get(i).equals("")) {
                showPics.remove(i);
                break;
            }
        }
        if (showPics.size() < 9) {
            showPics.add(showPics.size(), "");
        }
        if (dynamicPicAdapter == null) {
            dynamicPicAdapter = new SendDynamicPicAdapter(
                    SendImgTextActivity.this, showPics, showPicDialogListener,
                    new SendDynamicPicAdapter.IDeleteListener() {

                        @Override
                        public void onDelete(int index) {
                            delImg(showPics.get(index), index);
                        }
                    });
            pic_gd.setAdapter(dynamicPicAdapter);
        } else {
            dynamicPicAdapter.setData(showPics);
        }
    }


    View.OnClickListener showPicDialogListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showPicDialog();
        }
    };

    /**
     * 删除某个图片
     *
     * @param pos
     * @param position
     */
    public void delImg(final String pos, final int position) {
        MyDialogUtils.showV7NormalDialog(SendImgTextActivity.this, R.string.delete_selected_img_prompt, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                showPics.remove(pos);
                mIntSelectPicCount--;
                handler.sendEmptyMessage(0);
            }
        });
    }


    protected void showPicDialog() {
        BottomDialog.showBottomDialog(SendImgTextActivity.this, R.string.take_phone, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
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
        ActivityUtils.startActivity(SendImgTextActivity.this,
                CameraActivity.class, bundle, PAIS);
    }

    /**
     * 选择图片
     */
    private void chooseImgs() {
        Bundle bundle = new Bundle();
        bundle.putInt("selected_count", mIntSelectPicCount);
        bundle.putInt("max_select_count", 9);
        ActivityUtils.startActivity(SendImgTextActivity.this,
                PhotoActivity.class, bundle, PIC);
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    setPicGd();
                    break;
                case 1:
                    // TODO: 2017/11/16 发布
                    break;
            }

            return false;
        }
    });


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (data == null) {
                return;
            }

            File compressFile = null;
            Bitmap compressBitmap = null;
            switch (requestCode) {
                case PAIS:
                    try {
                        String strPicPath = data.getStringExtra("big_pic_filename");

                        showPics.add(strPicPath);
                        mIntSelectPicCount++;
                        handler.sendEmptyMessage(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PIC:
                    try {
                        select_gl_arr.clear();
                        select_gl_arr = (ArrayList<PhotoItem>) data
                                .getSerializableExtra("gl_arr");

                        if (select_gl_arr.size() > 0) {
                            mIntSelectPicCount += select_gl_arr.size();
                            for (int i = 0; i < select_gl_arr.size(); i++) {
                                String path = select_gl_arr.get(i).getPath();
                                showPics.add(path);

                            }
                            handler.sendEmptyMessage(0);
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
    public void onBackPressed() {
        String str = title_et.getText().toString();
        if ((showPics.size() > 1 && !TextUtils.isEmpty(showPics.get(0))) ||  !TextUtils.isEmpty(str)) {
            showDialog();
        } else {
            super.onBackPressed();
            AppManager.getAppManager().finishActivity(this);
        }
//        super.onBackPressed();
    }

    private void showDialog() {
        MyDialogUtils.showV7NormalDialog(SendImgTextActivity.this, R.string.quit_editing, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 发布动态
     */
    private void publishDiscover() {
        showProgress(getResources().getString(R.string.data_uploading));
        DiscoverApiManager.pubishDiscvoer(this, mTitle, mStrContent, "", "", "", "", compressedList, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                cancelProgress();
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_DISCOVER_LIST_REFRESH));
                finish();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                MyToast.makeText(SendImgTextActivity.this, message, MyToast.LENGTH_SHORT);
            }
        });

    }

    private void compressImgs() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0, size = showPics.size(); i < size; i++) {
            if (!TextUtils.isEmpty(showPics.get(i))) {
                list.add(showPics.get(i));
            }
        }
        Luban.with(this)
                .load(list)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(com.aikucun.akapp.utils.FileUtils.getImgCompressPath())                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        if (file != null) {
                            compressedList.add(file.getAbsolutePath());
                        }
                        if (compressedList.size() == showPics.size() - 1) {
                            publishDiscover();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();    //启动压缩
    }

    /**
     * 权限检测
     */
    private void checkPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean){
                    if (TDevice.isCameraAvailable()){
                        chooseImgs();
                    }else  showMessage(getResources().getString(R.string.camera_is_not_available));
                }else showApplicationSettingDetails(getResources().getString(R.string.video_permission));
            }
        });
    }
}
