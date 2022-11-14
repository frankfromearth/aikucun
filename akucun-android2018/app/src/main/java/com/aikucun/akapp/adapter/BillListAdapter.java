package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.BillViewHolder;
import com.aikucun.akapp.api.entity.Bill;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * 账单列表
 */
public class BillListAdapter extends RecyclerArrayAdapter<Bill> {

    private Context _context;

    public BillListAdapter(Context context) {
        super(context);
        _context = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        BillViewHolder viewHolder = new BillViewHolder(parent, _context);
        return viewHolder;
    }

}
