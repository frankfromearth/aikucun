package com.aikucun.akapp.activity.discover;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aswife.ui.MaskImageView;
import com.qiyunxin.android.http.common.ImageUtil;
import com.qiyunxin.android.http.utils.FileUtils;
import com.qiyunxin.android.http.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by ak123 on 2017/11/16.
 * 拍照页面
 */

public class CameraActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.how_get_permission_layout)
    View how_get_permission_layout;
    @BindView(R.id.photo_img)
    MaskImageView photo_img;
    @BindView(R.id.btn_right)
    Button btn_right;
    @BindView(R.id.rl_title)
    RelativeLayout rl_title;
    private String currentPhotoPath;
    private ArrayList<ImageSize> sizes;
    private Camera camera = null;
    private final int CHOOSE_TAKE_PHOTO_REQUEST_CODE = 110;
    private FileUtils fileUtils = new FileUtils();
    private String big_pic_filename;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.yulan);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        sizes = (ArrayList<ImageSize>) bundle.getSerializable("size");

        if (getCameraInstance() != null) {
            camera.release();
            Intent getImageByCamera = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File f = fileUtils.createImageFile();
                currentPhotoPath = f.getAbsolutePath();
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                startActivityForResult(getImageByCamera,
                        CHOOSE_TAKE_PHOTO_REQUEST_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initListener();
    }


    private Camera getCameraInstance() {
        try {
            camera = Camera.open();
        } catch (Exception e) {
            btn_right.setVisibility(View.GONE);
            mTitleText.setText(R.string.not_read_camera);
            rl_title.setVisibility(View.VISIBLE);
            photo_img.setVisibility(View.GONE);
            how_get_permission_layout.setVisibility(View.VISIBLE);
        }
        return camera;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_local_photo;
    }

    private void initListener() {
        findViewById(R.id.btn_right).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data = new Intent();
                        data.putExtra("big_pic_filename", big_pic_filename);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_TAKE_PHOTO_REQUEST_CODE) {
                try {
                    big_pic_filename = fileUtils.getAlbumDir() + "/"
                            + Utils.md5(currentPhotoPath) + ".jpg";
                    if (sizes != null) {
                        for (int i = 0; i < sizes.size(); i++) {
                            ImageSize size = sizes.get(i);
                            ImageUtil.CompressImageAndSave(currentPhotoPath,
                                    big_pic_filename + size.name, 100,
                                    size.width, size.height);
                        }
                    }
                    photo_img.SetUrl(big_pic_filename);
                    photo_img.setVisibility(View.VISIBLE);
                    mToolBar.setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_right).setVisibility(
                            View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            finish();
        }
    }

}
