package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.LogisticsTraceViewHolder;
import com.aikucun.akapp.api.entity.Trace;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ak123 on 2017/12/11.
 */

public class LogisticsTraceAdapter extends RecyclerArrayAdapter<Trace> {

    public LogisticsTraceAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        LogisticsTraceViewHolder viewHolder = new LogisticsTraceViewHolder(parent);
        return viewHolder;
    }
}
