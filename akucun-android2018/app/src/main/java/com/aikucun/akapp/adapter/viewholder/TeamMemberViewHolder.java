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
 * 团队人员
 */

public class TeamMemberViewHolder extends BaseViewHolder<TeamMembers> {
    RoundImageView headerIv;
    TextView user_name_text;
    TextView forward_count_text;
    //个人贡献金额
    TextView personal_contribution_amount_text;
    private Context context;
    private boolean showAmount=true;

    public TeamMemberViewHolder(ViewGroup parent, Context _context,boolean _showAmount) {
        super(parent, R.layout.item_team_member);
        this.showAmount = _showAmount;
        context = _context;
        user_name_text = $(R.id.user_name_text);
        headerIv = $(R.id.head_image);
        forward_count_text = $(R.id.forward_count_text);
        personal_contribution_amount_text = $(R.id.personal_contribution_amount_text);
    }

    @Override
    public void setData(TeamMembers data) {
        if (StringUtils.isEmpty(data.getAvatar())) {
            headerIv.setImageResource(R.drawable.icon_default_avatar);
        }
        else {
            Glide.with(context).load(data.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(headerIv);
        }
        user_name_text.setText(data.getNick());
//        forward_count_text.setText(data.getForwardCount() + "");
        if (showAmount){
            String rewardTotal = StringUtils.getNum2Decimal((float) data.getMonthsTotal() / 100 + "");
            personal_contribution_amount_text.setText("¥ " + rewardTotal);
        }

    }
}
