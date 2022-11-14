package com.aikucun.akapp.activity.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.qiyunxin.android.http.utils.FileUtils;
import com.qyx.android.weight.clip.ClipImageLayout;
import com.qyx.android.weight.utils.QyxWeightImageUtils;

import butterknife.BindView;

/**
 * 剪切
 */
public class ClipImageActivity extends BaseActivity {
    private static final String KEY = "picPath";
    FileUtils fileUtils = new FileUtils();

    @BindView(R.id.clipImageLayout)
    ClipImageLayout clipImageLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    protected int getLayoutId() {
        return R.layout.crop_image_layout;
    }


    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.shear_header_image);
    }

    @Override
    public void initData() {
        String path = getIntent().getStringExtra(KEY);
        /**
         * 获取图片的旋转度
         */
        int degreee = QyxWeightImageUtils.readBitmapDegree(path);
        Bitmap bitmap = QyxWeightImageUtils.getBitmapByPath(path);
        if (bitmap != null) {
            if (degreee == 0) {
                clipImageLayout.setImageBitmap(bitmap);
            } else {
                clipImageLayout.setImageBitmap(QyxWeightImageUtils.rotateBitmap(
                        degreee, bitmap));
            }
        } else {
            finish();
        }

        findViewById(R.id.btn_right).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = clipImageLayout.clip();
                Intent intent = new Intent();
                String path = fileUtils.saveCropBitmap(bitmap);
                intent.putExtra("pic_path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
