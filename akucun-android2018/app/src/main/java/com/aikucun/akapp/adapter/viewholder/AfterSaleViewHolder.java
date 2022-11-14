package com.aikucun.akapp.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by jarry on 2017/6/10.
 */
public class AfterSaleViewHolder extends BaseViewHolder<AfterSaleItem>
{
    public ImageView productImage;
    public TextView contentTv,skuTv,amountTv;

    public TextView server_no,server_progress,status_text;


    public RelativeLayout rl_detail;


    public AfterSaleViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_after_sale_list_item);

        productImage = $(R.id.productImage);
        contentTv = $(R.id.contentTv);
        skuTv = $(R.id.skuTv);
        amountTv = $(R.id.amountTv);

        rl_detail = $(R.id.rl_detail);
        server_no = $(R.id.server_no);
        server_progress = $(R.id.server_progress);
        status_text = $(R.id.status_text);
    }

    @Override
    public void setData(AfterSaleItem data)
    {
        Glide.with(getContext()).load(data.getImageUrl()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(productImage);

        contentTv.setText(data.getDesc());
        skuTv.setText(data.getSku().getChima() + "  x1");
        amountTv.setText("结算价：" + StringUtils.getPriceString(data.getJiesuanjia()));


        server_no.setText("服务单号：" + data.getServicehao());
        status_text.setText(data.serviceTypeText());
        server_progress.setText("售后进度：" +data.statusText());

    }
}
