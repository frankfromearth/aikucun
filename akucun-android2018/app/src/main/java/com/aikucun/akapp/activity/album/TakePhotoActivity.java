package com.aikucun.akapp.activity.album;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.discover.ImageSize;
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
 * @author SL
 *         拍照图片预览
 */
public class TakePhotoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.photo_img)
    MaskImageView photo_img;
    private final int CHOOSE_TAKE_PHOTO_REQUEST_CODE = 110;
    private String currentPhotoPath;
    private FileUtils fileUtils = new FileUtils();
    private String big_pic_filename;
    private ArrayList<ImageSize> sizes;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_take_photo;
    }

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.yulan);
        mToolBar.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        sizes = (ArrayList<ImageSize>) bundle.getSerializable("size");
        initView();
        initListener();
        Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File f = fileUtils.createImageFile();
            currentPhotoPath = f.getAbsolutePath();
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(getImageByCamera,
                    CHOOSE_TAKE_PHOTO_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initListener() {
        findViewById(R.id.btn_right).setOnClickListener(
                new OnClickListener() {

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
                                    big_pic_filename + size.name, 80,
                                    size.width, size.height);
                        }
                    }
                    photo_img.SetUrl(big_pic_filename);
                    photo_img.setVisibility(View.VISIBLE);
                    mToolBar.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            finish();
        }
    }
}
