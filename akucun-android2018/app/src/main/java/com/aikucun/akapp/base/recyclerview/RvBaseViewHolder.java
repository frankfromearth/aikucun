package com.aikucun.akapp.base.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * RecyclerView Base ViewHolder
 * Created by jarry on 16/11/6.
 */
public class RvBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final SparseArray<View> mViews;

    private RvOnItemClickListener mListener;

    public RvBaseViewHolder(View view)
    {
        super(view);
        this.mViews = new SparseArray<>();

        itemView.setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View view)
    {
        if(mListener != null){
            mListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(RvOnItemClickListener mListener)
    {
        this.mListener = mListener;
    }
}
