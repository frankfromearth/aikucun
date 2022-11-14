package com.aikucun.akapp.adapter.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.InvitReward;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;


/**
 * 奖励邀请
 */

public class InvitRewardViewHolder extends BaseViewHolder<InvitReward> {

    RoundImageView head_image;
    TextView user_name_text;
    TextView personal_contribution_amount_text;
    TextView team_contribution_amount_text;
    private Context context;

    public InvitRewardViewHolder(ViewGroup parent, Context _context) {
        super(parent, R.layout.item_invita_reward_layout);
        context = _context;
        user_name_text = $(R.id.user_name_text);
        head_image = $(R.id.head_image);
        personal_contribution_amount_text = $(R.id.personal_contribution_amount_text);
        team_contribution_amount_text = $(R.id.team_contribution_amount_text);
    }

    @Override
    public void setData(InvitReward data) {
        if (StringUtils.isEmpty(data.getAvator())) {
            head_image.setImageResource(R.drawable.icon_default_avatar);
        }
        else {
            Glide.with(context).load(data.getAvator()).diskCacheStrategy(DiskCacheStrategy.ALL).into(head_image);
        }
        user_name_text.setText(data.getNick());
        personal_contribution_amount_text.setText(data.getDaigoufzh());
        team_contribution_amount_text.setText(data.getJianjiedaigouf());
    }
}
