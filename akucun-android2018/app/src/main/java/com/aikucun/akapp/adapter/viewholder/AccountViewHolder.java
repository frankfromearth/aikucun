package com.aikucun.akapp.adapter.viewholder;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.AccountRecord;
import com.aikucun.akapp.utils.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import static com.aikucun.akapp.api.entity.AccountRecord.AccountRecordIN;
import static com.aikucun.akapp.api.entity.AccountRecord.AccountRecordOUT;

/**
 * Created by jarry on 2017/6/22.
 */

public class AccountViewHolder extends BaseViewHolder<AccountRecord>
{
    public TextView recordTitleText;
    public TextView recordDespText;
    public TextView recordTimeText;
    public TextView recordValueText;


    public AccountViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_account_record);

        recordTitleText = $(R.id.account_record_title);
        recordDespText = $(R.id.account_record_desp);
        recordTimeText = $(R.id.account_record_time);
        recordValueText = $(R.id.account_record_jine);
    }

    @Override
    public void setData(AccountRecord data)
    {
        if (data.getType() == AccountRecordIN)
        {
            recordValueText.setTextColor(Color.RED);
            recordValueText.setText("+ " + StringUtils.getPriceString(data.getJine()));
        }
        else if (data.getType() == AccountRecordOUT)
        {
            recordValueText.setTextColor(Color.GREEN);
            recordValueText.setText("- " + StringUtils.getPriceString(data.getJine()));
        }

        recordTitleText.setText(data.getTitle());
        recordDespText.setText(data.getMiaoshu());
        recordTimeText.setText(data.getTime());
    }
}
