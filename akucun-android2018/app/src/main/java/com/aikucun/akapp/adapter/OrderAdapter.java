package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.AdOrderViewHolder;
import com.aikucun.akapp.adapter.viewholder.OrderViewHolder;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.OrderModel;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.security.InvalidParameterException;

/**
 * Created by jarry on 2017/6/11.
 */
public class OrderAdapter extends RecyclerArrayAdapter<Object>
{
    public static final int TYPE_INVALID = 0;
    public static final int TYPE_ORDER = 1;
    public static final int TYPE_ADORDER = 2;

    public static final int ORDER_EVENT_PAY = 1;
    public static final int ORDER_EVENT_SCAN = 2;

    private OnItemEventListener onItemEventListener;

    public OrderAdapter(Context context)
    {
        super(context);
    }

    @Override
    public int getViewType(int position)
    {
        if (getItem(position) instanceof OrderModel)
        {
            return TYPE_ORDER;
        }
        else if (getItem(position) instanceof AdOrder)
        {
            return TYPE_ADORDER;
        }
        return TYPE_INVALID;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_ORDER:
                return new OrderViewHolder(parent);
            case TYPE_ADORDER:
                return new AdOrderViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        int type = getViewType(position);
        if (type == TYPE_ORDER)
        {
            final OrderModel order = (OrderModel) getItem(position);
            final OrderViewHolder viewHolder = (OrderViewHolder) holder;

            viewHolder.payButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onItemEventListener != null)
                    {
                        onItemEventListener.onEvent(ORDER_EVENT_PAY, order, position);
                    }
                }
            });
        } else {
            final AdOrder adOrder = (AdOrder)getItem(position);
            final AdOrderViewHolder viewHolder = (AdOrderViewHolder) holder;

            viewHolder.appleyBitBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onItemEventListener != null)
                    {
                        onItemEventListener.onEvent(ORDER_EVENT_PAY, adOrder, position);
                    }
                }
            });

            viewHolder.appleyScanBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onItemEventListener != null)
                    {
                        onItemEventListener.onEvent(ORDER_EVENT_SCAN, adOrder, position);
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
        public void onEvent(int event, OrderModel order, int position);
        public void onEvent(int event, AdOrder order, int position);
    }
}
