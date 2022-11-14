package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.AccountViewHolder;
import com.aikucun.akapp.api.entity.AccountRecord;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by jarry on 2017/6/21.
 */

public class AccountAdapter extends RecyclerArrayAdapter<AccountRecord>
{
    public AccountAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new AccountViewHolder(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position)
    {
        super.OnBindViewHolder(holder, position);
    }
}
