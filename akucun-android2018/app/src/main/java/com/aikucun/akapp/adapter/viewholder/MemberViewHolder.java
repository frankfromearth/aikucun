package com.aikucun.akapp.adapter.viewholder;


import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.MemberInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by micker on 2017/8/13.
 */

public class MemberViewHolder extends BaseViewHolder<MemberInfo>  {

    public TextView memberName;
    public TextView memberDesc;
    public TextView memberPrice;

    private MemberInfo memberInfo;

    public MemberViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_member_item);

        memberName = $(R.id.member_name);
        memberDesc = $(R.id.member_time);
        memberPrice = $(R.id.member_price);
    }

    @Override
    public void setData(MemberInfo data)
    {
        this.memberInfo = data;
        memberName.setText(data.getMiaoshu());
        memberDesc.setText("有效期一年，购买立即生效");
        memberPrice.setText("¥ " + data.getJine());

    }
}
