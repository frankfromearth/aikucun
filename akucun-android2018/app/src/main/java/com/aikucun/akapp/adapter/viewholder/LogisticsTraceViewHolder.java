package com.aikucun.akapp.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Trace;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;


/**
 * Created by micker on 2017/7/16.
 */

public class LogisticsTraceViewHolder extends BaseViewHolder<Trace> {

    TextView contentTextView, timeTextView;
    ImageView logistics_status_iv;
    View bottom_line_view, line_view;

    public LogisticsTraceViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_logistics_layout);
        contentTextView = $(R.id.logistics_address_tv);
        timeTextView = $(R.id.date_tv);
        bottom_line_view = $(R.id.bottom_line_view);
        line_view = $(R.id.line_view);
        logistics_status_iv = $(R.id.logistics_status_iv);
    }

    @Override
    public void setData(Trace data) {
        contentTextView.setText(data.getContent());
        timeTextView.setText(data.getTime());
        if (data.isEnd()) {
            line_view.setVisibility(View.GONE);
            bottom_line_view.setVisibility(View.GONE);
        } else {
            bottom_line_view.setVisibility(View.VISIBLE);
            line_view.setVisibility(View.VISIBLE);
        }

        if (data.isTop()) {
            logistics_status_iv.setImageResource(R.drawable.icon_logistics_status_red);
        } else {
            logistics_status_iv.setImageResource(R.drawable.icon_logistics_status_gray);
        }
    }
}
