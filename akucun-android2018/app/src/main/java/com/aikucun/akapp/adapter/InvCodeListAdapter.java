package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.InvCodeListViewHolder;
import com.aikucun.akapp.api.entity.TeamMembers;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ak123 on 2018/1/17.
 */

public class InvCodeListAdapter extends RecyclerArrayAdapter<TeamMembers> {

    private Context _context;
    public InvCodeListAdapter(Context context, boolean _showAmount) {
        super(context);
        _context = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        InvCodeListViewHolder viewHolder = new InvCodeListViewHolder(parent, _context);
        return viewHolder;
    }

}
