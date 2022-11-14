package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.TeamMemberViewHolder;
import com.aikucun.akapp.api.entity.TeamMembers;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ak123 on 2018/1/17.
 */

public class TeamMemberAdapter extends RecyclerArrayAdapter<TeamMembers> {

    private Context _context;
    private boolean showAmount;
    public TeamMemberAdapter(Context context,boolean _showAmount) {
        super(context);
        this.showAmount = _showAmount;
        _context = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        TeamMemberViewHolder viewHolder = new TeamMemberViewHolder(parent, _context,showAmount);
        return viewHolder;
    }

}
