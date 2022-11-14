package com.aikucun.akapp.adapter.viewholder;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.AdOrder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by jarry on 2017/6/10.
 */
public class AdOrderViewHolder extends BaseViewHolder<AdOrder> {
    public ImageView pinpaiImage;
    public TextView pinpaiNameText;
    public TextView orderNoText;
    public TextView orderAmountText;
    public TextView orderStatusText;
    public TextView appleyBitBtn;
    public TextView appleyScanBtn;

    public AdOrderViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_order_item);

        pinpaiImage = $(R.id.pinpai_icon);
        pinpaiNameText = $(R.id.pinpai_name_text);
        orderNoText = $(R.id.order_no_text);
        orderAmountText = $(R.id.order_amount_text);
        orderStatusText = $(R.id.order_status_text);
        appleyBitBtn = $(R.id.appley_bit_btn);
        appleyScanBtn = $(R.id.appley_scan_btn);
    }

    @Override
    public void setData(AdOrder data) {
        Glide.with(getContext()).load(data.getPinpaiURL()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(pinpaiImage);

        pinpaiNameText.setText(data.getPinpai());

        orderNoText.setText("发货单号：" + data.getOdorderstr());
        Log.e("订单状态：",data.getStatu()+"");
        Log.e("商品类型：",data.getIsvirtual()+"");
        if (data.getStatu() == 4) {
            orderAmountText.setText("共 " + data.getNum() + " 件  实发：" + data.getPnum() + " 件");

            if (data.getIsvirtual() == 0) {
                orderStatusText.setTextColor(Color.parseColor("#666666"));
                orderStatusText.setText(data.shortWuliuInfo());
                orderStatusText.setVisibility(View.VISIBLE);
            }else{
                orderStatusText.setVisibility(View.GONE);
            }

            //            orderStatusText.setText(data.getWuliugongsi() + "  单号：" + data
            // .getWuliuhao());
            appleyBitBtn.setVisibility(View.VISIBLE);
            appleyBitBtn.setText(R.string.statement);
            if (data.getIsvirtual() == 1) {
                appleyScanBtn.setVisibility(View.GONE);
            } else appleyScanBtn.setVisibility(View.VISIBLE);


            /*
            AdOrder order = YiFaAdOrderManager.getInstance().getByAdOrderId(data.getAdorderid());
            if (order!=null) {
                SCLog.debug("从本地数据库中获取已发货单详情");
            } else {
                appleyScanBtn.setVisibility(View.VISIBLE);
            }
            */
        } else {
            if (0 == data.getStatu()) {
                orderStatusText.setText("待发货");
                appleyBitBtn.setText("申请对帐单");
                appleyScanBtn.setVisibility(View.GONE);
            } else {
                orderStatusText.setText("拣货中");
                appleyBitBtn.setText("对帐单");
                if (data.getIsvirtual() == 1) {
                    appleyScanBtn.setVisibility(View.GONE);
                } else appleyScanBtn.setVisibility(View.VISIBLE);
            }
            orderAmountText.setText("待发 " + data.daifahuoNum() + " 件");
            orderStatusText.setTextColor(Color.RED);
            appleyBitBtn.setVisibility(View.VISIBLE);
            orderStatusText.setVisibility(View.VISIBLE);
        }

//        if (data.getIsvirtual() == 1) {
//            //虚拟商品
//            orderStatusText.setVisibility(View.GONE);
//        } else {
//            orderStatusText.setVisibility(View.VISIBLE);
//        }
    }
}
