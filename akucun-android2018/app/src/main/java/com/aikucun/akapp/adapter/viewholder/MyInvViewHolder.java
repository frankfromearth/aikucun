package com.aikucun.akapp.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.MyInv;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by jarry on 17/6/30.
 */

public class MyInvViewHolder extends BaseViewHolder<MyInv>
{
    public TextView titleText;
    public TextView contentText;
    public TextView timeText;

    public MyInvViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_my_in_item);

        titleText = $(R.id.msg_title_text);
        contentText = $(R.id.msg_content_text);
        timeText = $(R.id.msg_time_text);

    }

    @Override
    public void setData(MyInv data)
    {
        titleText.setText(data.getNicheng() + "( + " + data.getYonghubianhao() + ")");
        timeText.setText(data.getCreatetime());

        contentText.setText(1 == data.getStatu()? "已开通" : "开通会员");
        contentText.setBackgroundColor(itemView.getResources().getColor(1 == data.getStatu()? R.color.lightgray:R.color.color_accent));
        contentText.setTextColor(1 == data.getStatu()? R.color.black:R.color.white);
    }
}
