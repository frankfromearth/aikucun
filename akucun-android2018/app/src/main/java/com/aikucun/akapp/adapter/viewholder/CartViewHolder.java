package com.aikucun.akapp.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by jarry on 2017/6/9.
 */

public class CartViewHolder extends BaseViewHolder<CartProduct>
{
    public TextView contentText;
    public TextView skuText;
    public TextView amountText;
    public TextView remarkText;
    public TextView quehuoTv;
    public ImageView productImage;
    public ImageButton deleteBtn;
    public ImageView imageview;
    public Button remarkBtn,reBuyBtn;
    private CartProduct product;

    public CartViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_cart_item);

        contentText = $(R.id.contentTv);
        skuText = $(R.id.skuTv);
        amountText = $(R.id.amountTv);
        remarkText = $(R.id.txt_remark);
        productImage = $(R.id.productImage);
        deleteBtn = $(R.id.deleteBtn);
        remarkBtn = $(R.id.remarkBtn);
        imageview = $(R.id.image_choose);
        reBuyBtn = $(R.id.reBuyBtn);
        quehuoTv = $(R.id.quehuoTv);
    }

    @Override
    public void setData(CartProduct data)
    {
        this.product = data;
        contentText.setText(data.getDesc());
        skuText.setText(data.getChima() + "  x1");
        amountText.setText("结算价：" + StringUtils.getPriceString(data.getJiesuanjia()));

        if (StringUtils.isEmpty(data.getRemark())) {
            remarkText.setVisibility(View.GONE);
        } else {
            remarkText.setVisibility(View.VISIBLE);
            remarkText.setText("备 注：" + data.getRemark());
        }
        imageview.setImageResource(data.isSelected()?R.drawable.right:R.drawable.circle);
        Glide.with(getContext()).load(data.getImageUrl()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(productImage);

        if (data.isQueHuo()) {
            int color = getContext().getResources().getColor(R.color.color_accent);
            contentText.setTextColor(color);
            skuText.setTextColor(color);
            amountText.setTextColor(color);
            remarkText.setTextColor(color);
            quehuoTv.setVisibility(View.VISIBLE);
        } else {
            quehuoTv.setVisibility(View.GONE);
        }
    }

    public void setCartRecycle() {
        deleteBtn.setVisibility(View.GONE);
        imageview.setVisibility(View.GONE);
        remarkBtn.setVisibility(View.GONE);
        reBuyBtn.setVisibility(View.VISIBLE);

        if (!product.isQueHuo()) {
            if (2 == product.getBuystatus()) {
                reBuyBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                reBuyBtn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_bg_red));
                reBuyBtn.setEnabled(true);
                reBuyBtn.setText("重新购买");
            } else  {
                reBuyBtn.setTextColor(getContext().getResources().getColor(R.color.color_accent));
                reBuyBtn.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                reBuyBtn.setEnabled(false);
                reBuyBtn.setText("已重新购买");
            }
        } else {
            reBuyBtn.setTextColor(getContext().getResources().getColor(R.color.white));
            reBuyBtn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_bg_disabled));
            reBuyBtn.setEnabled(false);
            reBuyBtn.setText("重新购买");
        }
    }
}
