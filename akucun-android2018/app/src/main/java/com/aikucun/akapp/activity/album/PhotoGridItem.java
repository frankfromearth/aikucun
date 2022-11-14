package com.aikucun.akapp.activity.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.TDevice;
import com.aswife.ui.MaskImageView;

public class PhotoGridItem extends RelativeLayout implements Checkable {
    private Context mContext;
    private boolean mCheck;
    public MaskImageView mImageView;
    public ImageView mSelect, conver_iv;
    public FrameLayout image_layout;

    public PhotoGridItem(Context context) {
        this(context, null, 0);
    }

    public PhotoGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(mContext).inflate(
                R.layout.photoalbum_gridview_item, this);
        mSelect = findViewById(R.id.photo_select);
        conver_iv = findViewById(R.id.conver_iv);
        image_layout = findViewById(R.id.image_layout);
        mImageView = findViewById(R.id.photo_img_view);

        float w = TDevice.getScreenWidth();
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.height = layoutParams.width = (int) w / 4;
        mImageView.setLayoutParams(layoutParams);
        //设置整个图片高宽
        ViewGroup.LayoutParams imageLayoutP = image_layout.getLayoutParams();
        imageLayoutP.height = imageLayoutP.width = (int) w / 4;
        image_layout.setLayoutParams(imageLayoutP);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheck = checked;
        if (checked) {
            mSelect.setImageResource(R.drawable.icon_img_selected);
            conver_iv.setVisibility(View.VISIBLE);
        } else {
            conver_iv.setVisibility(View.GONE);
            mSelect.setImageResource(R.drawable.icon_img_not_select);
        }
    }

    @Override
    public boolean isChecked() {
        return mCheck;
    }

    @Override
    public void toggle() {
        setChecked(!mCheck);
    }

    public void setImgResID(int id) {
        if (mImageView != null) {
            mImageView.setImageResource(id);

            mImageView.setBackgroundResource(id);
        }
    }

    public void SetBitmap(Bitmap bit) {
        if (mImageView != null) {
            mImageView.setImageBitmap(bit);
        }
    }

    public ImageView getImageView() {
        if (mImageView != null) {
            return mImageView;
        }
        return null;
    }

    public void setUrl(String url) {
        if (mImageView != null) {
            mImageView.setBackgroundResource(R.drawable.icon_default_image);
            mImageView.SetUrl(url);
        }
    }
}
