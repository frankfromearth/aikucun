package com.aikucun.akapp.activity.reconciliation;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.widget.ColorFilterImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ak123 on 2018/1/23.
 */

public class ChooseLiveInfoViewHolder extends BaseViewHolder<LiveInfo> {

    TextView nameTv;
    ColorFilterImageView heardIv;
    ImageView is_selected_iv;

    public ChooseLiveInfoViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_screen_brand_layout);
        nameTv = $(R.id.brand_name_tv);
        heardIv = $(R.id.headImage);
        is_selected_iv = $(R.id.is_selected_iv);
    }


    @Override
    public void setData(LiveInfo liveInfo) {
        if (!TextUtils.isEmpty(liveInfo.getPinpaiurl())) {
            Glide.with(getContext()).load(liveInfo.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(heardIv);
            heardIv.setVisibility(View.VISIBLE);
        } else {
            heardIv.setVisibility(View.GONE);
        }
        nameTv.setText(liveInfo.getPinpaiming());
        if (liveInfo.isSelected()) is_selected_iv.setVisibility(View.VISIBLE);
        else is_selected_iv.setVisibility(View.GONE);
    }

}
