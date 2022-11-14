package com.aikucun.akapp.adapter.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Bill;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;


/**
 * 账单
 */

public class BillViewHolder extends BaseViewHolder<Bill> {

    TextView titletextview;
    TextView amounttextview;
    TextView datetextview;
    private Context context;

    public BillViewHolder(ViewGroup parent, Context _context) {
        super(parent, R.layout.layout_bill_item);
        context = _context;
        titletextview = $(R.id.title_tv);
        amounttextview = $(R.id.amount_text);
        datetextview = $(R.id.date_tv);
    }

    @Override
    public void setData(Bill data) {
        if (data.getTradeType() == 0) {
            titletextview.setText(R.string.order_pay);
            amounttextview.setText("-" + StringUtils.getNum2Decimal((float)data.getAmount() / 100+""));
            amounttextview.setTextColor(context.getResources().getColor(R.color.color_accent));
        } else if (data.getTradeType() == 1) {
            titletextview.setText(R.string.order_refund);
            amounttextview.setText("+" + StringUtils.getNum2Decimal((float)data.getAmount() / 100+""));
            amounttextview.setTextColor(context.getResources().getColor(R.color.result_points));
        }
        datetextview.setText(DateUtils.timeStamp2Date(data.getCreateTime()));

    }
}
