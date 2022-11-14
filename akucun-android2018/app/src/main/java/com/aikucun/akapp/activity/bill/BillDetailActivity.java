package com.aikucun.akapp.activity.bill;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Bill;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.StringUtils;

import butterknife.BindView;

/**
 * Created by ak123 on 2018/1/9.
 * 账单详情
 */

public class BillDetailActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.bill_title_tv)
    TextView bill_title_tv;//支付类型标题
    @BindView(R.id.bill_amount_tv)
    TextView bill_amount_tv;//金额
    @BindView(R.id.bill_pay_status_tv)
    TextView bill_pay_status_tv;//支付状态
    @BindView(R.id.serial_num_tv)
    TextView serial_num_tv;//流水号
    @BindView(R.id.trading_time_tv)
    TextView trading_time_tv;//支付时间
    @BindView(R.id.trading_detail_tv)
    TextView trading_detail_tv;//支付详情


    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText.setText(R.string.bill_detail);
    }

    @Override
    public void initData() {
        Bill bill = (Bill) getIntent().getSerializableExtra("bill");
        if (bill != null) {
            if (bill.getTradeType() == 0) {
                bill_title_tv.setText(R.string.order_pay);
                bill_amount_tv.setText("-" +  StringUtils.getNum2Decimal((float)bill.getAmount() / 100+""));
                bill_amount_tv.setTextColor(getResources().getColor(R.color.color_accent));
            } else if (bill.getTradeType() == 1) {
                bill_title_tv.setText(R.string.order_refund);
                bill_amount_tv.setText("+" +  StringUtils.getNum2Decimal((float)bill.getAmount() / 100+""));
                bill_amount_tv.setTextColor(getResources().getColor(R.color.result_points));
            }
            serial_num_tv.setText(bill.getTradeId());
            trading_time_tv.setText(DateUtils.timeStamp2Date(bill.getCreateTime()));
            if (!TextUtils.isEmpty(bill.getTradeInfo())) {
                if (bill.getTradeInfo().indexOf(",") > -1) {
                    String temp = bill.getTradeInfo().replaceAll(",", "\n");
                    trading_detail_tv.setText(temp);
                } else trading_detail_tv.setText(bill.getTradeInfo());
            }
            if (bill.getTradeStatus() == 1 || bill.getTradeStatus() == 0) {
                bill_pay_status_tv.setText(R.string.transaction_success);
            } else if (bill.getTradeStatus() == 2)
                bill_pay_status_tv.setText(R.string.transaction_filed);
            else if (bill.getTradeStatus() == 3)
                bill_pay_status_tv.setText(R.string.transaction_handling);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_detail_layout;
    }
}
