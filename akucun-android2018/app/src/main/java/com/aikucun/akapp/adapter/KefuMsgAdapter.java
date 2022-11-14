package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.KefuMsgViewHolder;
import com.aikucun.akapp.api.entity.KefuMsgItem;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by jarry on 17/6/30.
 */

public class KefuMsgAdapter extends RecyclerArrayAdapter<KefuMsgItem>
{
    public KefuMsgAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new KefuMsgViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        if (onItemEventListener != null) {
            onItemEventListener.onEvent(0,getItem(position),position);
        }
    }

    private OnItemEventListener onItemEventListener;

    public void setOnItemEventListener(OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener
    {
        public void onEvent(int event, KefuMsgItem kefuMsgItem, int position);
    }

}
