package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.MessageViewHolder;
import com.aikucun.akapp.api.entity.Message;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by jarry on 17/6/30.
 */

public class MessageAdapter extends RecyclerArrayAdapter<Message>
{
    public MessageAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new MessageViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);
    }

}
