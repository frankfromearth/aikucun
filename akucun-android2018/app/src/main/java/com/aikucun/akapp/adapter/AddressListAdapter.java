package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.AddressViewHolder;
import com.aikucun.akapp.api.entity.Address;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

/**
 *
 * Created by Micker on 2017/7/16.
 */
public class AddressListAdapter extends RecyclerArrayAdapter<Address>
{
    public static final int ADDRESS_EVENT_DELETE = 10;
    public static final int ADDRESS_EVENT_EDIT = 11;
    public static final int ADDRESS_EVENT_DEFAULT = 12;
    public static final int ADDRESS_EVENT_CHOOSE = 13;

    public boolean isChooose = false;

    private OnItemEventListener onItemEventListener;

    public AddressListAdapter(Context context)
    {
        super(context);
    }

    public void setAddressList(List<Address> addressList)
    {
        clear();
        addAll(addressList);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        AddressViewHolder viewHolder = new AddressViewHolder(parent);
        viewHolder.isChoose = this.isChooose;
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        final Address address = getItem(position);
        final AddressViewHolder viewHolder = (AddressViewHolder) holder;
        viewHolder.address_del_btn.setOnClickListener(new View.OnClickListener()
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
        viewHolder.address_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(ADDRESS_EVENT_DEFAULT, address, position);
                }
            }
        });
        viewHolder.address_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(ADDRESS_EVENT_EDIT, address, position);
                }
            }
        });

        if (this.isChooose) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemEventListener != null)
                    {
                        onItemEventListener.onEvent(ADDRESS_EVENT_CHOOSE, address, position);
                    }
                }
            });
        }
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener
    {
        public void onEvent(int event, Address address, int position);
    }
}
