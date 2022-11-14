package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.MemberViewHolder;
import com.aikucun.akapp.api.entity.MemberInfo;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import static com.aikucun.akapp.adapter.CommentAdapter.COMMENT_EVENT_REPLY;

/**
 * Created by micker on 2017/8/20.
 */

public class MemberAdapter extends RecyclerArrayAdapter<MemberInfo> {

    public MemberAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);
        final MemberInfo memberInfo = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(COMMENT_EVENT_REPLY, memberInfo, null, position);
                }
            }
        });

    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }

    private OnItemEventListener onItemEventListener;

    public interface OnItemEventListener
    {
        public void onEvent(int event, MemberInfo memberInfo, Object object, int position);
    }
}
