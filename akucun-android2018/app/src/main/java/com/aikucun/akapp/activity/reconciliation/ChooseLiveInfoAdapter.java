package com.aikucun.akapp.activity.reconciliation;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.api.entity.LiveInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ak123 on 2018/1/23.
 */

public class ChooseLiveInfoAdapter extends RecyclerArrayAdapter<LiveInfo> {
    Context context;

    public ChooseLiveInfoAdapter(Context _context) {
        super(_context);
        context = _context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        ChooseLiveInfoViewHolder viewHolder = new ChooseLiveInfoViewHolder(parent);
        return viewHolder;
    }
}
