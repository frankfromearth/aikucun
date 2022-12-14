package com.aikucun.akapp.adapter.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.storage.YiFaAdOrderManager;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleLoufaBufa;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleLoufaTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASalePending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleRejected;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleSubmit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoBufa;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoPending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusCancel;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusFahuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusInit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusPending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusQuehuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusQuehuoDone;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuihuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuikuanDone;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusWeifahuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusYifahuo;

/**
 * Created by jarry on 2017/6/9.
 */

public class OrderProdViewHolder extends BaseViewHolder<CartProduct> {
    public TextView contentText;
    public TextView skuText;
    public TextView amountText;
    public TextView productButton;
    public TextView barcode_tv;
    public TextView cancelButton;
    public ImageView productImage;

    public TextView remarkText;
    public Button remarkBtn;

    public String adOrderId;
    public TextView extrainfo_text;
    public RelativeLayout extrainfo_layout;
    public TextView copy_btn;

    public OrderProdViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_order_product);

        contentText = $(R.id.contentTv);
        skuText = $(R.id.skuTv);
        amountText = $(R.id.amountTv);
        productButton = $(R.id.order_product_btn);
        cancelButton = $(R.id.order_product_action);
        productImage = $(R.id.productImage);
        remarkBtn = $(R.id.remarkBtn);
        remarkText = $(R.id.txt_remark);
        barcode_tv = $(R.id.barcode_tv);
        copy_btn = $(R.id.copy_btn);
        extrainfo_text = $(R.id.extrainfo_text);
        extrainfo_layout = $(R.id.extrainfo_layout);
    }

    @Override
    public void setData(CartProduct data) {
        contentText.setText(data.getDesc());
        skuText.setText(data.getSku().getChima() + "  x1");
        amountText.setText("????????????" + StringUtils.getPriceString(data.getJiesuanjia()));

        remarkText.setText("??? ??????" + data.getRemark());
        int statusValue = data.getShangpinzhuangtai();
        String status = statusTextValue(statusValue);

        barcode_tv.setText("?????? " + data.getSku().getBarcode());

        Glide.with(getContext()).load(data.getImageUrl()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(productImage);

        productButton.setTextColor(getContext().getResources().getColor(R.color.white));
        productButton.setBackground(getContext().getResources().getDrawable(R.drawable.btn_bg_buy_selector));
        cancelButton.setVisibility(View.GONE);

        if (status.length() > 0) {
            productButton.setVisibility(View.VISIBLE);
            productButton.setText(status);
        } else {
            productButton.setVisibility(View.GONE);
        }


        if (ProductStatusInit == statusValue ||
                ProductStatusWeifahuo == statusValue ||
                ProductStatusYifahuo == statusValue ||
                (ProductASaleSubmit <= statusValue && ProductASaleTuihuoPending >= statusValue)) {

            if (ProductStatusInit == statusValue || ProductStatusWeifahuo == statusValue) {
                cancelButton.setText(R.string.cancel);
                if (ProductStatusWeifahuo == statusValue) {
                    cancelButton.setVisibility(View.VISIBLE);
                }

            } else if (ProductASaleSubmit <= statusValue && ProductASaleTuihuoPending >= statusValue) {
                productButton.setBackground(getContext().getResources().getDrawable(R.drawable.btn_bg_buy_selector));
            }


        } else {
            productButton.setTextColor(getContext().getResources().getColor(R.color.color_accent));
            productButton.setBackground(null);
        }


        //????????????????????????
        {
            String uid = RSAUtils.md5String(adOrderId + data.getCartproductid());
            boolean isPeihuo = YiFaAdOrderManager.getInstance().checkYiFaHuoIsPeihuo(uid);
            int color = getContext().getResources().getColor(R.color.black);

            if (isPeihuo) {
                color = getContext().getResources().getColor(R.color.color_accent);
            }

            contentText.setTextColor(color);
            skuText.setTextColor(color);
            amountText.setTextColor(color);
            remarkText.setTextColor(color);

        }

        if (data.getIsvirtual() == 1) {
            //????????????
            extrainfo_layout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(data.getExtrainfo())) {
                extrainfo_text.setText(data.getExtrainfo());
            } else {
                extrainfo_layout.setVisibility(View.GONE);
            }
            productButton.setVisibility(View.GONE);
        } else {
            extrainfo_layout.setVisibility(View.GONE);
            productButton.setVisibility(View.VISIBLE);
        }


        /*
        if (statusValue == ProductStatusInit)
        {
            productButton.setVisibility(View.VISIBLE);
            productButton.setText("?????????");
            cancelButton.setVisibility(View.GONE);
        }
        else if (statusValue == ProductStatusWeifahuo)
        {
            productButton.setVisibility(View.VISIBLE);
            productButton.setText("?????????");
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("??? ???");

            //??????????????????????????????

        }
        else if (statusValue == ProductStatusYifahuo || statusValue == ProductStatusFahuo)
        {
            //?????????????????????
            productButton.setVisibility(View.VISIBLE);
//            productButton.setText("????????????");
            productButton.setText("????????????");

            cancelButton.setVisibility(View.GONE);
//            cancelButton.setText("??? ???");//??????

            String uid = RSAUtils.md5String(adOrderId + data.getCartproductid());
            boolean isPeihuo =  YiFaAdOrderManager.getInstance().checkYiFaHuoIsPeihuo(uid);
            int color = getContext().getResources().getColor(R.color.black);

            if (isPeihuo) {
                color = getContext().getResources().getColor(R.color.color_accent);
                productButton.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("???????????????");
            }

            contentText.setTextColor(color);
            skuText.setTextColor(color);
            amountText.setTextColor(color);
            remarkText.setTextColor(color);
            statusText.setTextColor(color);
        }
        else
        {
            productButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
*/
    }

    private String statusTextValue(int status) {
        String statusText = "";
        switch (status) {
            case ProductStatusInit: {
                statusText = "?????????";
            }
            break;
            case ProductStatusWeifahuo: {
                statusText = "?????????";
            }
            break;
            case ProductStatusYifahuo: {
                statusText = "????????????";
            }
            break;
            case ProductStatusFahuo: {
                statusText = "";
            }
            break;
            case ProductStatusCancel: {
                statusText = "?????????";
            }
            break;
            case ProductStatusQuehuo: {
                statusText = "???????????? ?????????";
            }
            break;
            case ProductStatusTuihuo: {
                statusText = "?????? ?????????";
            }
            break;
            case ProductStatusTuikuan: {
                statusText = "???????????? ?????????";
            }
            break;
            case ProductStatusPending: {
                statusText = "?????????";
            }
            break;
            case ProductStatusTuikuanDone:
                statusText = "???????????? ?????????";
                break;
            case ProductStatusQuehuoDone:
                statusText = "???????????? ?????????";
                break;

            case ProductASaleSubmit:
            case ProductASaleRejected:
            case ProductASalePending:
            case ProductASaleLoufaBufa:
            case ProductASaleLoufaTuikuan:
            case ProductASaleTuihuoBufa:
            case ProductASaleTuihuoTuikuan:
            case ProductASaleTuihuoPending: {
                statusText = "????????????";
            }
            break;

            default:
                break;
        }
        return statusText;
    }
}
