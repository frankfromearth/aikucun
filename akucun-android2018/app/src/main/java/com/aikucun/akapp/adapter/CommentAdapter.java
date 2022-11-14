package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.CommentViewHolder;
import com.aikucun.akapp.api.entity.Comment;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;



/**
 * Created by micker on 2017/8/13.
 */

public class CommentAdapter extends RecyclerArrayAdapter<Comment> {

    public static final int COMMENT_EVENT_REPLY = 1;    //回复
    public static final int COMMENT_EVENT_DELETE = 2;   //删除

    public CommentAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        CommentViewHolder holder =  new CommentViewHolder(parent);
        return holder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);
        final Comment comment = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(COMMENT_EVENT_REPLY, comment, null, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(COMMENT_EVENT_DELETE, comment, null, position);
                }
                return false;
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
        public void onEvent(int event, Comment comment, Object object, int position);
    }
}
