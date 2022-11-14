package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.AfterSaleViewHolder;
import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;


/**
 * Created by micker on 2017/8/20.
 */

public class AfterSaleListAdapter extends RecyclerArrayAdapter<AfterSaleItem> {

    public AfterSaleListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new AfterSaleViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);
        final AfterSaleViewHolder viewHolder = (AfterSaleViewHolder) holder;
        final AfterSaleItem afterSaleItem = getItem(position);

        viewHolder.rl_detail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(0, afterSaleItem, position);
                }
            }
        });
    }

    private OnItemEventListener onItemEventListener;

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }
    public interface OnItemEventListener
    {
        public void onEvent(int event, AfterSaleItem afterSaleItem, int position);
    }

}
