package com.aikucun.akapp.adapter.viewholder;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.RechargeItem;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by micker on 2017/8/13.
 */

public class RechargeViewHolder extends BaseViewHolder<RechargeItem>  {

    public TextView memberName;
    public TextView memberDesc;
    public TextView memberPrice;
    public ImageView imageView;
    public View member_option;

    private RechargeItem rechargeItem;

    public RechargeViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_recharge_item);

        memberName = $(R.id.member_name);
        memberDesc = $(R.id.member_time);
        memberPrice = $(R.id.member_price);
        imageView = $(R.id.imageView);
        member_option = $(R.id.member_option);
    }

    @Override
    public void setData(RechargeItem data)
    {
        this.rechargeItem = data;
        memberName.setText(data.getTitle());
        memberDesc.setText(data.getContent());
        memberPrice.setText("¥ " + data.getJine());
    }

    public void setItemChoose(boolean isChoose) {

        member_option.setBackground(getContext().getResources().getDrawable(isChoose?R.drawable.b_vip_choose_bg:R.drawable.b_vip_unchoose_bg));
        imageView.setVisibility(isChoose?View.VISIBLE:View.GONE);
    }
}
