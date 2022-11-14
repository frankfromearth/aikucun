package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.RechargeViewHolder;
import com.aikucun.akapp.api.entity.RechargeItem;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by micker on 2017/8/20.
 */

public class RechargeAdapter extends RecyclerArrayAdapter<RechargeItem> {

    private RechargeItem selectRechargeItem;


    public RechargeAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RechargeViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);
        final RechargeItem rechargeItem = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(0, rechargeItem, null, position);
                }
            }
        });

        ((RechargeViewHolder ) holder).setItemChoose(getSelectRechargeItem() == rechargeItem);
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }

    private OnItemEventListener onItemEventListener;

    public interface OnItemEventListener
    {
        public void onEvent(int event, RechargeItem rechargeItem, Object object, int position);
    }

    public RechargeItem getSelectRechargeItem() {
        return selectRechargeItem;
    }

    public void setSelectRechargeItem(RechargeItem selectRechargeItem) {
        this.selectRechargeItem = selectRechargeItem;
    }
}
