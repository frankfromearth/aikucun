package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.MyInvViewHolder;
import com.aikucun.akapp.api.entity.MyInv;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import static com.aikucun.akapp.adapter.AddressListAdapter.ADDRESS_EVENT_DELETE;

/**
 * Created by jarry on 17/6/30.
 */

public class MyInvAdapter extends RecyclerArrayAdapter<MyInv>
{
    public MyInvAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new MyInvViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        final MyInv address = getItem(position);
        final MyInvViewHolder viewHolder = (MyInvViewHolder) holder;
        viewHolder.contentText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(ADDRESS_EVENT_DELETE, address, position);
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
        public void onEvent(int event, MyInv address, int position);
    }

}
