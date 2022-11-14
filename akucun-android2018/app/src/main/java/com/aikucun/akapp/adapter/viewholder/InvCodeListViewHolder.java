package com.aikucun.akapp.adapter.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.TeamMembers;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;


/**
 * 邀请码列表
 */

public class InvCodeListViewHolder extends BaseViewHolder<TeamMembers> {
    RoundImageView headerIv;
    TextView invit_code_text;
    private Context context;

    public InvCodeListViewHolder(ViewGroup parent, Context _context) {
        super(parent, R.layout.item_invcode_list);
        context = _context;
        invit_code_text = $(R.id.invit_code_text);
        headerIv = $(R.id.head_image);
    }

    @Override
    public void setData(TeamMembers data) {
        if (StringUtils.isEmpty(data.getAvatar())) {
            headerIv.setImageResource(R.drawable.icon_default_avatar);
        } else {
            Glide.with(context).load(data.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(headerIv);
        }
    }
}
