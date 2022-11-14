package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.InvitRewardViewHolder;
import com.aikucun.akapp.api.entity.InvitReward;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ak123 on 2018/1/17.
 */

public class InvitationRewardAdapter extends RecyclerArrayAdapter<InvitReward> {

    private Context _context;

    public InvitationRewardAdapter(Context context) {
        super(context);
        _context = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        InvitRewardViewHolder viewHolder = new InvitRewardViewHolder(parent, _context);
        return viewHolder;
    }

}
