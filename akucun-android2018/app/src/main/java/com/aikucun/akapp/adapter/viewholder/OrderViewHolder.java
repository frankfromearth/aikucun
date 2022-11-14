package com.aikucun.akapp.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.OrderModel;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by jarry on 2017/6/10.
 */
public class OrderViewHolder extends BaseViewHolder<OrderModel> {
    public ImageView pinpaiImage;
    public TextView pinpaiNameText;
    public TextView orderNoText;
    public TextView orderAmountText;
    public TextView orderStatusText;
    public TextView appleyBitBtn;
    public TextView payButton;
    public CountdownView countdown;
    public RelativeLayout ll_countdown;

    public OrderViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_order_item);

        pinpaiImage = $(R.id.pinpai_icon);
        pinpaiNameText = $(R.id.pinpai_name_text);
        orderNoText = $(R.id.order_no_text);
        orderAmountText = $(R.id.order_amount_text);
        orderStatusText = $(R.id.order_status_text);
        appleyBitBtn = $(R.id.appley_bit_btn);
        payButton = $(R.id.pay_btn);
        countdown = $(R.id.countdown);
        ll_countdown = $(R.id.ll_countdown);

        appleyBitBtn.setText("申请对帐单");
    }

    @Override
    public void setData(OrderModel data) {
        Glide.with(getContext()).load(data.getPinpaiURL()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(pinpaiImage);

        pinpaiNameText.setText(data.getPinpai());

        orderNoText.setText("订单号：" + data.displayOrderId());
        orderAmountText.setText("共" + data.getShangpinjianshu() + "件  结算金额：" + StringUtils
                .getPriceString(data.getZongjine()));

        orderStatusText.setText(data.orderStatusText());
        Date date = new Date();
        long temp =  data.getOvertimeshuzi() * 1000 - date.getTime();
        if (data.getStatus() == 0 && temp > 0) {
//            orderStatusText.setTextColor(Color.RED);
            payButton.setVisibility(View.VISIBLE);
            ll_countdown.setVisibility(View.VISIBLE);
        } else {
//            orderStatusText.setTextColor(Color.parseColor("#666666"));
            payButton.setVisibility(View.INVISIBLE);
            ll_countdown.setVisibility(View.GONE);
        }
       if (temp > 0){
           countdown.start(temp);
           countdown.restart();
           countdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
               @Override
               public void onEnd(CountdownView cv) {
                   // TODO: 2018/2/7 倒计时结束
                   payButton.setVisibility(View.GONE);
                   ll_countdown.setVisibility(View.GONE);
                   orderStatusText.setText(R.string.unpay_cancel_order);
               }
           });
       }else{
           if (data.getStatus() == 0){
               orderStatusText.setText(R.string.unpay_cancel_order);
           }
       }

    }
}
